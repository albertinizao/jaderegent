# Jade Regent Firestore Data Model Design

**Status: Draft — pending review**  
**Scope:** analysis and design only; no persistence implementation, migration execution, infrastructure creation, or production access.  
**Evidence basis:** static inspection of the current code and migrations, comparison with `dexm-calculator`, and official Google Cloud documentation. Runtime data volumes and actual data quality have not been inspected.

> **Migration gate:** A MariaDB backup and an isolated restore, including relationships and image binaries, must be completed and verified before any real data migration. The existing JSON export is not a substitute. See [backup and restore](backup-and-restore.md).

## 1. Executive summary

**Proposal:** Store domain entities and unbounded histories as separate Firestore documents, retain existing identifiers, and store image binaries in private object storage. Implement referential integrity, uniqueness, transactions, and deletion behavior explicitly rather than assuming JPA behavior carries over. The conservative baseline choices in section 9 are design decisions for review, not a claim of formal approval or evidence that implementation or migration has been authorized.

**Verified findings:**

1. Firestore's 1 MiB document limit affects both images and potentially `LONGTEXT` fields.
2. Existing JPA transactions and cascades span multiple entities; a multi-batch Firestore workflow would not automatically provide equivalent observable atomicity.
3. MariaDB guarantees unique PJ and NPC names under `utf8mb4_unicode_ci`, but does not declare a unique PJ–NPC pair constraint.
4. The reference Firestore adapter supports only a subset of JPA behavior. Its migration writer and general repository appear, by static inspection, to expect different document shapes.
5. The backup and isolated MariaDB restore remain unproven and block migration, not design.

**Assumptions and limitations:** This design targets Firestore Native mode, a Spring Boot backend and an IAP-protected application as requested. Database edition, region, actual data sizes, growth, operational costs, identity and authorization details, and production topology require confirmation. It does not establish that IAP alone supplies all application-level authorization.

## 2. Verified current data model

Sources: [initial schema](../src/main/resources/db/migration/V1__initial_schema.sql) and [image migration](../src/main/resources/db/migration/V3__add_entity_images.sql). PKs, referenced FKs and names described as mandatory are required; other SQL columns can be nullable. Preserve null semantics rather than silently applying defaults.

| Entity | Identifier | Fields and relationships | Constraints / observations |
| --- | --- | --- | --- |
| PJ | UUID | `nombreDisplay`, `notaOpcional`, `imagenUrl`, `fechaCreacion`, `imagenData`, `imagenMime` | Name required and unique. |
| NPC | String | `nombre`, `descripcionLarga`, `nivelMaximo`, `imagenUrl`, `fechaImportacion`, image bytes and MIME | Name required and unique; owns advantages. |
| Ventaja | String | `npcId`, `nombre`, `descripcionLarga`, `minNivelRelacion`, `prerequisitosOperator`; prerequisites in a separate table | Required FK to NPC and required name. Prerequisite rows have no guaranteed identifier or position. |
| Relacion | UUID | `pjId`, `npcId`, `nivelActual`, `pendienteEleccion`, `consistente`, `contadorInteracciones`, `ultimaActualizacionTs` | Required FKs to PJ and NPC; no SQL uniqueness constraint on the pair. |
| Interaccion | UUID | `relacionId`, `tipo`, `valor`, `nota`, `ts`, `usuario` | Required relationship FK; `tipo` can be null or `POSITIVA`/`NEGATIVA`. |
| SeleccionVentaja | UUID | `relacionId`, `ventajaId`, `nivelAdquisicion` | Required relationship and advantage FKs; no declared uniqueness by relationship and level. |
| SnapshotSelecciones | UUID | `relacionId`, `version`, `ts`, `usuario` | Required relationship FK; version uniqueness is not declared. |
| SnapshotSeleccionItem | UUID | `snapshotId`, `ventajaId`, `nivelRelacion` | Required snapshot and advantage FKs. |
| LogAccion | UUID | `ts`, `usuario`, `accion`, `detalle` | No declared FK. |

### 2.1 Integrity and semantic constraints

- Name uniqueness uses MariaDB's `utf8mb4_unicode_ci`; lowercasing alone is not a proven equivalent for accents, case, or Unicode comparisons.
- SQL `DATETIME(6)` represents local date-time without a time zone. No UTC instant can be inferred reliably without establishing the historical zone and daylight-saving interpretation.
- JPA cascades and explicit application deletions exist, but the SQL schema does not declare a general `ON DELETE CASCADE` policy.
- SQL `LONGTEXT` and image blobs may exceed Firestore's limit. Actual source sizes, duplicates, null distribution, and inconsistencies are **unknown**, not findings about production data.

## 3. Verified queries and proposed Firestore resolution

Repository methods are listed in [domain repositories](../src/main/java/com/opipo/jaderegent/domain/repository/); a declared method is not necessarily used by application code. Proposed indexes are candidates, not a tested index configuration. The following assumes the Firestore Native core query interface and standard single-field indexing unless exemptions are deliberately configured.

| Current query / use | Proposed resolution | Anticipated index |
| --- | --- | --- |
| List all PJs, NPCs, relationships; matrix loads and joins them by ID ([matrix use case](../src/main/java/com/opipo/jaderegent/application/usecase/GetRelationsMatrixUseCase.java)) | Read each collection and join in the backend initially. | No composite index for full reads; measure volume and cost. |
| PJ or NPC by ID | Direct document read. | None. |
| PJ by name or existence | Direct lookup via transactional name reservation. | Direct document ID lookup. |
| NPC by name or existence | Same reservation approach if preserving the repository contract; declared but no Java consumers found. | Direct document ID lookup. |
| Relationship by PJ and NPC | Query both fields; the pair guard coordinates creates but does not select a relationship among duplicates. Fail closed on ambiguous matches. | Candidate composite `(pjId ASC, npcId ASC)` for the query; not needed for direct pair lookup. |
| Relationships by PJ | Filter `pjId`; load referenced NPCs and selections; preserve Java-side ordering. | Single-field `pjId`. |
| Relationships by NPC | Filter `npcId`; load referenced PJs and selections. | Single-field `npcId`. |
| Advantages by NPC | Filter `npcId`. | Single-field `npcId`. |
| Selections by relationship | Read its selections subcollection. | None for all documents. |
| Selections by relationship and acquisition level | Filter within the relationship's subcollection; declared, no consumer found. | Single-field `nivelAdquisicion`. |
| Interactions by relationship, `ts DESC` | Query its interactions subcollection ordered by `ts DESC`. | Single-field `ts DESC`. |
| Snapshots by relationship, `version DESC` | Query its snapshots subcollection ordered by `version DESC`. | Single-field `version DESC`. |
| Items by snapshot | Read its items subcollection. | None for all documents. |
| Logs by user, `ts DESC` | Filter root `logs` by user and order by timestamp; declared, no consumer found. | Candidate composite `(usuario ASC, ts DESC)`. |
| Backup/export | Read all required collections and documents; separately design a consistent backup mechanism. | Depends on exact query implementations. |

The [PJ detail](../src/main/java/com/opipo/jaderegent/application/usecase/GetPjDetailUseCase.java) and [printable advantages](../src/main/java/com/opipo/jaderegent/application/usecase/GetPjPrintableAdvantagesUseCase.java) use Java ordering. Changing to Firestore ordering can change results, so preserve and test the existing comparator, Unicode behavior, and ties. Firestore `orderBy` excludes documents missing the ordered field; explicit `null` is distinct from a missing field. [Official ordering documentation](https://docs.cloud.google.com/firestore/native/docs/query-data/order-limit-data).

**Ordering parity gate for `ts` and `version`:** SQL permits nullable values in these fields, and neither the repository's single-field sort nor the schema establishes a deterministic order for equal values. Before enabling those histories, integration tests against an isolated MariaDB/JPA instance must determine source ordering for nulls, timestamp precision and any observed tie behavior; do not infer a secondary sort from UUID or document ID. Migration must write each sort field explicitly, including `null`, and validate that no field is missing; a missing field is an integrity failure to quarantine or repair only through a reviewed, traceable procedure. Verify Firestore order and null placement against the observed SQL/JPA contract; if they differ, combine separate bounded null and non-null reads in the backend and arrange them according to the verified source ordering. Ensure complete pagination of each group, with deterministic cursors for traversal that do not become an advertised business tie order. Equal-valued entries have no established relative ordering: compare tie groups as multisets, not sequences, unless a separately reviewed behavior change defines a secondary key. Test all-null, mixed null/non-null, missing field, equal timestamps or versions, precision boundaries, page boundaries and concurrent modifications. If equivalence or complete traversal cannot be demonstrated, fail the parity gate rather than omit records or invent an ordering contract.

## 4. Proposed Firestore model

### 4.1 Collection layout

```text
pjs/{pjId}
npcs/{npcId}
ventajas/{ventajaId}
relaciones/{relacionId}
  interacciones/{interaccionId}
  selecciones/{seleccionId}
  snapshots/{snapshotId}
    items/{itemId}
logs/{logId}
uniquePjNames/{comparisonKey}
uniqueNpcNames/{comparisonKey}
relacionPairs/{pairKey}  # transactional create guard, not evidence of historical uniqueness
```

**Proposal:** retain source UUIDs and string IDs as document IDs where valid. Preflight string IDs against Firestore's document-ID restrictions. For invalid IDs, use a deterministic encoded ID, preserve the original as `legacyId`, and consistently translate all references. [Firestore quotas](https://docs.cloud.google.com/firestore/quotas).

### 4.2 Fields and types

The table is a proposed persistence contract. Validate exact Java/SQL types and nullability before implementation. Reference IDs are strings, not automatic foreign keys. `schemaVersion` is proposed metadata; it is not an existing SQL column.

| Document | Proposed fields |
| --- | --- |
| `pjs/{id}` | `nombreDisplay: string`, `notaOpcional: string|null`, `fechaCreacion: string|null`, `imagenUrl: string|null`, `imagenMime: string|null`, `imagen: map|null`, `schemaVersion: integer`. |
| `npcs/{id}` | `nombre: string`, `descripcionLarga: string|null`, `nivelMaximo: integer|null`, `fechaImportacion: string|null`, `imagenUrl: string|null`, `imagenMime: string|null`, `imagen: map|null`, `schemaVersion: integer`. |
| `ventajas/{id}` | `npcId: string`, `nombre: string`, `descripcionLarga: string|null`, `minNivelRelacion: integer|null`, `prerequisitosOperator: string|null`, `prerequisitos: array<string|null>` (lossless source representation; records containing null are quarantined until isolated semantics are demonstrated), `schemaVersion: integer`. |
| `relaciones/{id}` | `pjId: string`, `npcId: string`, `nivelActual: integer|null`, `pendienteEleccion: boolean|null`, `consistente: boolean|null`, `contadorInteracciones: integer|null`, `ultimaActualizacionTs: string|null`, `schemaVersion: integer`. |
| `…/interacciones/{id}` | `tipo: string|null`, `valor: integer|null`, `nota: string|null`, `ts: string|null`, `usuario: string|null`. |
| `…/selecciones/{id}` | `ventajaId: string`, `nivelAdquisicion: integer|null`. |
| `…/snapshots/{id}` | `version: integer|null`, `ts: string|null`, `usuario: string|null`. |
| `…/snapshots/{id}/items/{id}` | `ventajaId: string`, `nivelRelacion: integer|null`. |
| `logs/{id}` | `ts: string|null`, `usuario: string|null`, `accion: string|null`, `detalle: string|null`. |

**Date assumption:** canonical fixed-width local date-time strings are a provisional lossless representation of SQL wall-clock values, not UTC timestamps. Test ordering, missing versus null, ties, precision, and the interpretation of historical values. A derived sortable field may be used only after defining its semantics.

**Large-text proposal:** keep text in the owning document only if the full serialized document fits with margin. Preflight UTF-8 and document sizes. Oversized text needs either private object storage plus metadata/checksum or documented chunking; both add complexity and reads. Do not truncate silently.

### 4.3 Rationale and tradeoffs

- Separate PJs, NPCs, advantages and relationships support current ID and filter queries without embedding potentially unbounded sets, at the cost of backend joins and additional reads.
- Relationship histories, selections and snapshots belong in subcollections to prevent unbounded parent growth, but require explicit descendant deletion and access controls.
- Embed prerequisites in an advantage because they are read with it; do not invent an order not guaranteed by the source table.
- Keep logs separate because their lifecycle is independent. IDs preserve explicit references, but Firestore does not enforce their existence.

Illustrative **synthetic** documents, not real records:

```json
{
  "pjId": "550e8400-e29b-41d4-a716-446655440001",
  "npcId": "npc-example",
  "nivelActual": 2,
  "pendienteEleccion": false,
  "consistente": true,
  "contadorInteracciones": 1,
  "ultimaActualizacionTs": "2026-01-15T18:30:00.000000",
  "schemaVersion": 1
}
```

```json
{
  "nombre": "Example NPC",
  "descripcionLarga": "Fictional example.",
  "nivelMaximo": 5,
  "fechaImportacion": null,
  "imagenUrl": "https://example.invalid/legacy-image.png",
  "imagenMime": "image/png",
  "imagen": {
    "objectKey": "npcs/npc-example/images/example-version",
    "sizeBytes": 245760,
    "sha256": "synthetic-placeholder"
  },
  "schemaVersion": 1
}
```

The checksum is intentionally a placeholder, not a real SHA-256 digest. The fictional example intentionally retains both an image reference and a legacy URL to illustrate coexistence; the stored image takes precedence as described in section 7.3.

### 4.4 Nullable prerequisite rows: source semantics and migration policy

**Verified schema:** [`ventaja_prerequisitos.prerequisitos`](../src/main/resources/db/migration/V1__initial_schema.sql#L35-L40) is nullable; its owning advantage FK is required. A missing row, a row containing `NULL`, an empty string and a duplicate row are distinct source states. This table has no declared row ID or position; preserve value multiplicities rather than inventing a source order. No source data was inspected, so whether any null rows exist is unknown.

**Verified application code:** [`Ventaja`](../src/main/java/com/opipo/jaderegent/domain/model/Ventaja.java#L31-L50) maps an eager `List<String>`; its constructor substitutes an empty list for a null *list*, while its setter accepts the supplied list. [`SelectVentajaUseCaseImpl`](../src/main/java/com/opipo/jaderegent/application/usecase/SelectVentajaUseCaseImpl.java#L59-L88) treats a null or empty list as unrestricted. If a null element reaches validation, assuming valid non-null obtained advantage IDs, it matches no ID: AND then fails, while OR can still succeed through another matching element. This is not proof that Hibernate loads a SQL null row as a null list element; loading/round-trip behavior is **unverified** and requires an isolated integration test. The NPC importer adds `JsonNode.asText()` values rather than explicitly preserving SQL null rows; it must not be used as evidence of SQL null handling.

**Migration preflight proposal:** only after the mandatory isolated MariaDB restore has been verified, count all prerequisite rows, count `WHERE prerequisitos IS NULL`, group null counts by owning advantage, and inventory empty strings and duplicates separately. Keep the owner ID and per-value multiplicity in a restricted reconciliation manifest, without disclosing private values in reports. Since there is no row key or guaranteed order, identify duplicate/null occurrences by aggregate counts, not fabricated source positions. Compare the raw SQL extraction with isolated JPA hydration before defining behavioral equivalence.

**Policy alternatives considered:**

1. **Preserve:** represent each source row, including each null, as an element in provisional `array<string|null>` (or equivalent explicitly typed row objects), preserving multiplicity. This is lossless but requires a consciously specified runtime policy for null elements and tests of Firestore serialization and AND/OR behavior; do not assume it matches JPA hydration.
2. **Reject/quarantine:** stop the affected migration or cutover when nulls are found, record counts and owners, and obtain a decision. This avoids silently changing semantics but blocks migration until resolved.
3. **Correct or remove with approval:** map each null to an approved valid prerequisite or explicitly remove it, recording original values, counts, rationale and a reversible audit manifest. This changes source information and can change access to advantages, especially AND versus OR; a backup alone does not authorize the correction.

**Selected baseline — draft, pending review:** losslessly preserve every raw row and its multiplicity in the restricted migration manifest, and quarantine affected records and block the real migration/cutover when a null occurs. Do not place quarantined records into an active runtime that might reinterpret them; never filter, default or replace nulls silently. An isolated SQL-to-JPA hydration and behavior test is an evidence gate before establishing runtime equivalence or handling any affected records. If equivalence cannot be established, keep the gate closed; a traceable remediation is a separate reviewed change. The nullable array describes a lossless representation, not a claim that JPA actually hydrates null elements.

**Required validation cases:** zero rows versus one null row; a valid ID plus null under AND and OR (including a matching and nonmatching ID); multiple nulls; repeated valid IDs; empty string versus null; extraction and write/read round-trip preserving each multiplicity; null list versus empty list; and exact hydration, selection and export comparison in an isolated database. Demonstrate that a rejected row blocks cutover and that any approved correction is traceable and reversible. Do not assert a source ordering.

## 5. Uniqueness, transactions, and deletion

### 5.1 Uniqueness proposal

Create name reservations and entity documents together in a transaction. Renames reserve the new name and release the previous reservation transactionally. The reservation key must implement a comparison policy demonstrated equivalent to MariaDB's actual collation, including relevant Unicode edge cases. Fail closed before enabling name writes or migration if equivalence cannot be demonstrated; no alternate comparison is silently adopted. A hash can avoid invalid or oversized IDs, but store and compare the canonical value to detect hash collisions.

The application checks for an existing PJ–NPC pair, but SQL does not guarantee uniqueness. Preserve every source relationship and its ID; quarantine duplicate pairs and block real cutover until explicitly resolved without silent discard. A pair guard serializes *new* create operations: in one transaction read the guard and parents, verify the pair is absent, then create both guard and relationship; use a shared contention/version document in all creation paths, including create-PJ/import-NPC, to close query-phantom races. Do not treat a single guard as authority to choose one historical relationship from duplicates. Before enabling writes, populate guards from a clean audited dataset; conditional create and idempotency keys prevent retry duplicates. This enforces the application’s intended create check under races and is a concurrency hardening, not a claim of an existing SQL uniqueness constraint.

**Pair-guard lifecycle and recovery:** the guard for a pair serializes every create, delete, replacement, import and restoration touching that pair. Creation reads both parent documents and the guard, confirms no conflicting relationship, then atomically creates both relationship and guard; all competing operations must participate in the same guard protocol. Individual relationship deletion reads the relationship, guard and dependent-state checks, and atomically deletes the relationship and releases the guard only when the existing deletion conditions and referential checks pass. If deletion is rejected, the transaction fails or exceeds limits, both relationship and guard remain unchanged. PJ/NPC deletion must atomically remove each affected relationship and its guard together with all required domain cleanup; if the complete operation cannot fit, reject it with no partial release. A relationship may be recreated only after the successful committed release; a concurrent delete/recreate must serialize so neither a duplicate nor an orphaned guard can result. A post-commit image-object cleanup is never the authority for guard release.

Import/restore must not blindly overwrite guards. While affected writes are frozen, validate pair multiplicity and parents, build or reconcile guards and relationships deterministically with conditional/idempotent writes, and compare a complete guard-to-relationship manifest before enabling writes. A pair with multiple historical relationships is quarantined and blocks cutover; a missing, stale or inconsistent guard also blocks activation until reconciliation has been verified. Restoring snapshots or rolling back uses the same procedure, preserving relationship IDs; retrying must not release a guard for a different relationship or overwrite newer state. Synthetic tests must cover accepted/rejected delete, parent deletion, transaction size failure, deletion racing recreation, duplicate creates, import/restore retries, stale/missing guard and interrupted restoration.

### 5.2 Transactional operations

| Operation | Current behavior and design implications |
| --- | --- |
| Create PJ | Checks name and creates relationships to all existing NPCs in a transaction ([use case](../src/main/java/com/opipo/jaderegent/application/usecase/CreatePjUseCase.java)). Coordinate concurrent NPC creation/import so no pair is missed. If it cannot fit a proven bounded atomic transaction, reject without partial writes and report the limit; do not silently expose a partial result. Coordinate creation and import with the same contention/version document so phantom concurrent NPC additions cannot be missed. |
| Create relationship | Checks both parents and the pair. Validate parents and create the pair guard and relationship atomically; fail closed if duplicates or ambiguous legacy state exist. |
| Register interaction | Persist the interaction and counter update together, with retry-safe transaction logic. |
| Increase/decrease level | Coordinate relationship state and affected selections. The current decrease path removes selections for the level without automatically recomputing `pendienteEleccion`. |
| Select advantage | Validate pending state, NPC ownership, minimum level and AND/OR prerequisites; serialize relationship and relevant selection changes. Acquisition level depends on selection count. See [selection use case](../src/main/java/com/opipo/jaderegent/application/usecase/SelectVentajaUseCaseImpl.java). |
| Import NPC | Replaces advantages; existing selection and snapshot FKs may block deletion. Check references and preserve or explicitly revise rejection behavior. |
| Import backup | Can replace selections and, on PJ name collision, delete the old PJ and relationships. Requires validation, idempotence, failure recovery and an explicit visibility policy. |

Firestore transactions are serializable, but contention can cause retries and eventual failure; reads must precede writes. Avoid external side effects inside retried transaction callbacks. Test actual transaction size and contention. [Official transaction contention documentation](https://docs.cloud.google.com/firestore/native/docs/transaction-data-contention).

### 5.3 Deletion semantics

**Verified:** PJ and NPC deletion explicitly cleans related interactions, selections, snapshots and items; NPC deletion also removes advantages. These use cases are transactional: [DeletePjUseCase](../src/main/java/com/opipo/jaderegent/application/usecase/DeletePjUseCase.java), [DeleteNpcUseCase](../src/main/java/com/opipo/jaderegent/application/usecase/DeleteNpcUseCase.java). Individual relationship deletion checks level and net counter are zero, but a zero net counter does **not** prove history is empty; interaction/snapshot FKs can still block deletion. Do not silently turn this endpoint into cascading deletion. [DeleteRelacionUseCase](../src/main/java/com/opipo/jaderegent/application/usecase/DeleteRelacionUseCase.java).

Deleting a Firestore parent document does not automatically delete subcollection documents. [Official deletion documentation](https://docs.cloud.google.com/firestore/native/docs/manage-data/delete-data).

**Alternatives:**

1. A bounded transaction, when all documents and request sizes fit and tests prove the behavior, most closely preserves atomicity.
2. A tombstone plus resumable batched cleanup scales better but changes observable atomicity. Every read must hide tombstoned aggregates; new references must be blocked; progress, retries and orphan reconciliation must be durable. This is not selected and requires a separate approved design change.

**Selected baseline:** use option 1 with all dependent existence checks and writes in a proven bounded transaction, shared contention guards for competing creates/deletes, and deterministic retry behavior. If size, contention, or reference checks prevent atomic completion, fail without partial effects; do not fall back to asynchronous cleanup. This is a deliberate operational limitation versus SQL and must be surfaced to callers and tested. Object garbage collection after a committed logical image replacement is separate, idempotent and cannot remove a currently referenced object; parent document deletion and domain references remain atomic or are rejected.

## 6. Reference application: transferable patterns and limits

`dexm-calculator` uses many root collections and multiple shapes: general documents with `id`, complete serialized JSON and copied scalar fields; other aggregates as character-keyed maps; and some fields embedded in characters. See its [FirestoreConfiguration.java](../../dexm-calculator/backend/src/main/java/com/dexm/personajes/adapter/out/persistence/firestore/FirestoreConfiguration.java) and [FirestoreRepositoryFactory.java](../../dexm-calculator/backend/src/main/java/com/dexm/personajes/adapter/out/persistence/firestore/FirestoreRepositoryFactory.java).

**Reusable patterns:** persistence adapters separated from the domain; application-default credentials and emulator support; query-driven index definitions; deterministic IDs; explicit Firestore transactions for specific invariants.

**Verified limitations / do not assume:** the proxy supports a subset of repositories; some bulk methods iterate individual writes; `flush` is a no-op; and the method parser removes `ForUpdate`, which is not an equivalent database lock. The presence of a transaction in one operation does not make all JPA `@Transactional` methods equivalent. Duplicating JSON and scalar fields also consumes document space.

**Static-inspection finding:** the reference [MariaDbToFirestoreMigration.java](../../dexm-calculator/backend/migration/src/main/java/com/dexm/personajes/migration/MariaDbToFirestoreMigration.java) writes rows under `payload`, while the general repository expects `json`, `id` and scalar fields; certain aggregates have other formats. Do not assume migration output is readable by current adapters without a specific transformation and integration tests. Reference links above point to the sibling workspace and are environment-specific.

## 7. Images

### 7.1 Verified current behavior and applicable limits

- Jade permits files up to **5 × 1024 × 1024 bytes**, declaring JPEG, PNG and WebP. The request multipart limit is also 5 MB, so multipart overhead may reduce the effective file maximum. See [ImageStorageService.java](../src/main/java/com/opipo/jaderegent/application/service/ImageStorageService.java) and [application.properties](../src/main/resources/application.properties).
- PJ and NPC image bytes and MIME are in MariaDB; legacy URLs and files also exist. The application prefers binary images and serves them through an application endpoint.
- The [legacy image importer](../src/main/java/com/opipo/jaderegent/application/service/LegacyImageImportService.java) matches normalized names and can produce ambiguous matches or overwrite images. It is not a safe migration mapping without inventory and collision resolution.
- Current validation does not establish strict MIME/content correspondence in every case or complete WebP structural validation. Actual data has not been inspected.
- Firestore limits a document to **1 MiB (1,048,576 bytes)** and an individual field value to **1 MiB minus 89 bytes**. Index limits also apply. An accepted Jade image can exceed the document limit without counting other fields. [Official quotas](https://docs.cloud.google.com/firestore/quotas).
- The reference frontend compresses JPEG data URLs to at most **150 × 1024 characters**, also accepting external URLs. That does not preserve every Jade image losslessly. See [reference image.ts](../../dexm-calculator/frontend/src/services/image.ts).

### 7.2 Alternatives and selected baseline

| Option | Advantages | Risks and tradeoffs |
| --- | --- | --- |
| Private Cloud Storage, delivered by Spring behind IAP — **selected draft baseline** | Preserve originals; retain application endpoint and authorization; Firestore contains metadata only. | Additional component, object storage/operation/transfer costs, backend image traffic, independent backup and reconciliation. |
| Private Cloud Storage with short-lived signed URLs | Direct downloads reduce backend traffic. | Anyone possessing the URL can use it until expiry; control leakage, caching and lifetime. [Signed URLs](https://docs.cloud.google.com/storage/docs/access-control/signed-urls). |
| Reduced image inside Firestore | Fewer components for very small images. | Does not necessarily preserve original or current size limits; Base64 and duplicated fields consume space; document limit still applies. |
| Local container filesystem | Simple for local development. | Cloud Run's writable filesystem is in memory and does not persist after instance termination. [Container contract](https://docs.cloud.google.com/run/docs/container-contract). |

**Proposed controls:** immutable or versioned private objects, least-privilege service identity, application authorization, and Firestore metadata containing object key, byte count and SHA-256, plus the independently stored raw `imagenMime` field as specified in section 7.3. Do not persist signed URLs as stable image identifiers. Evaluate retention, independent copies and restoration. Public access prevention does not disable signed URLs. [Public access prevention](https://docs.cloud.google.com/storage/docs/public-access-prevention); [object versioning](https://docs.cloud.google.com/storage/docs/object-versioning).

Firestore and Cloud Storage do not share a single atomic transaction. Proposed workflow: upload an immutable object, validate byte count/hash, conditionally update the document reference, then clean up old objects only later using reconciliation and retryable deletion. Backups/restores must coordinate document and object versions. Costs depend on region, storage, operations, transfer and retention; no estimate is possible without measurements. [Cloud Storage pricing](https://cloud.google.com/storage/pricing).

### 7.3 Legacy URL and stored-image lifecycle

**Verified source behavior:** [`PJ.getImagenUrl()`](../src/main/java/com/opipo/jaderegent/domain/model/PJ.java#L71-L76) and [`NPC.getImagenUrl()`](../src/main/java/com/opipo/jaderegent/domain/model/NPC.java#L87-L92) return the respective `/api/pj/{id}/image` or `/api/npc/{id}/image` when `imagenData != null`; otherwise they return the stored `imagenUrl` unchanged. This includes the unusual case of a non-null, zero-length blob: the getter still selects the endpoint, but [`ImageController`](../src/main/java/com/opipo/jaderegent/infrastructure/web/controller/ImageController.java#L15-L21) returns 404 for null or empty bytes. The image GET endpoint does not redirect to a URL. Some DTOs call these getters; [`PjController`](../src/main/java/com/opipo/jaderegent/infrastructure/web/controller/PjController.java#L58) also checks binary presence explicitly.

[`ImageStorageService`](../src/main/java/com/opipo/jaderegent/application/service/ImageStorageService.java#L20-L25) validates PUT, replaces bytes and MIME, and clears the stored URL; its image DELETE clears only bytes and MIME, leaving the stored URL untouched. Independently, [`UpdatePjUseCaseImpl`](../src/main/java/com/opipo/jaderegent/application/usecase/UpdatePjUseCaseImpl.java#L18-L32) and [`UpdateNpcUseCaseImpl`](../src/main/java/com/opipo/jaderegent/application/usecase/UpdateNpcUseCaseImpl.java#L17-L31) set a non-null URL without clearing existing binary data; passing null leaves the URL unchanged. Therefore coexistence is representable even though PUT clears URL, and deleting the binary can reveal a previously hidden URL. Legacy import likewise replaces bytes/MIME and clears URL. The actual prevalence of these states is unknown.

**Selected compatibility baseline — draft, pending review:** each PJ/NPC stores `imagenUrl: string|null` as the exact legacy fallback (which may be an external URL, a relative legacy path, or empty string) independently of `imagen: map|null`, which represents stored-binary metadata and a backend-resolvable object reference. Do not derive the raw legacy value through `getImagenUrl()` when bytes are present: it returns the endpoint instead. Extract the actual SQL `imagen_url` column and bytes/MIME separately, and compare both at migration. Preserve null versus empty string and validate relative legacy routes and authorization separately; retaining a legacy URL does not guarantee its target remains available.

For valid nonempty stored images, the existing API-facing image URL remains the entity endpoint; without stored image, expose `imagenUrl` unchanged (including null or empty). A direct GET of an absent image returns 404, not the fallback URL. To preserve historical states, represent an existing zero-length blob separately from absence and reproduce the getter/GET distinction; any correction is a separately reviewed change. If the referenced object is missing or corrupt, fail visibly and reconcile; do not silently expose the URL as a fallback, which would differ from current binary-first priority and hide corruption.

**Verified independent MIME state:** [V3](../src/main/resources/db/migration/V3__add_entity_images.sql#L1-L2) permits both `imagen_data` and `imagen_mime` to be null independently. A non-null MIME without a blob and a blob with null or malformed MIME are therefore schema-representable; their actual existence is unknown. For a nonempty blob, [ImageController](../src/main/java/com/opipo/jaderegent/infrastructure/web/controller/ImageController.java#L21) uses `application/octet-stream` when MIME is null or `MediaType.parseMediaType` throws, and otherwise sends the parsed value. A parseable MIME is not necessarily an allowed upload type or a match for the actual bytes; do not describe all unapproved or mismatched MIME strings as parse failures. The controller returns 404 for absent or empty bytes before parsing MIME.

**Proposed lossless mapping and inventory:** persist the exact SQL `imagen_mime` value independently as `imagenMime: string|null` on each PJ/NPC, whether `imagen` is null, zero-length, or points to a nonempty object; treat it as the source MIME for compatible HTTP response behavior. Preserve null versus empty versus malformed string exactly, without inferring or normalizing its value during migration; do not duplicate it as an authoritative object metadata value. Storage-specific MIME metadata, if used, is derivative and cannot replace the raw field. A reference to an empty object means non-null empty bytes; `imagen: null` means SQL-null blob. Preserve source URL independently in either case. Validate raw field read/write and HTTP formatting on an isolated test system.

After the mandatory verified MariaDB backup and isolated restore, inventory by entity type the counts of blob null, zero-length and nonempty crossed with MIME null, empty, syntactically invalid and parseable, and whether a URL is present. Capture only restricted identifiers or aggregated counts in the migration manifest, retaining byte hash and exact raw MIME privately where needed for reconciliation. Do not assume any of these combinations exist. Any correction of an anomalous MIME or replacement with detected content type is a separate decision requiring approval, traceable before/after values, and comparison against API behavior.

**Proposed write compatibility:** successful image PUT writes the replacement binary and normalized `imagenMime` and clears `imagenUrl`; failed validation must leave both unchanged. A regular entity update with a non-null URL sets that URL without removing an existing stored image; a null update leaves it unchanged. Image DELETE clears both the stored image metadata/reference and `imagenMime`, leaving `imagenUrl` unchanged, so a coexistence fallback becomes visible. Actual old object deletion is deferred/reconciled separately; a failure deleting an unreferenced object must not restore the logical image. Replacing or clearing both sources, or always preserving URLs on image PUT, would change current behavior and requires an explicit decision, tests and API/UI consequences. A null URL update does not currently clear the URL; introducing such a capability also needs approval.

**Required synthetic validation cases for both PJ and NPC:**

| Initial state | Operation | Expected compatibility result |
| --- | --- | --- |
| URL only | Read entity; GET image endpoint; image DELETE | Entity returns exact URL; direct GET 404; DELETE leaves URL. |
| Binary only | Read entity; GET; DELETE | Entity returns application endpoint; GET returns original bytes/MIME; DELETE leaves no URL. |
| URL and binary | Read; then DELETE binary | Endpoint wins initially; after DELETE the unchanged URL becomes visible. |
| URL and binary | Successful PUT replacement | New bytes/MIME take precedence and URL is cleared; after DELETE no legacy URL reappears. |
| Binary only | Set non-null URL via entity update; then DELETE | Binary still wins until DELETE; afterwards the new URL is visible. |
| Any state | Failed PUT; null URL update | Failed PUT preserves both sources; null URL update does not clear URL. |
| Non-null zero-byte blob with URL | Read entity; direct GET | Getter selects endpoint; GET 404; URL remains stored and becomes visible after DELETE. |
| Null blob, non-null MIME and URL | Migrate; read; direct GET; DELETE | Preserve exact MIME independently; URL wins entity read, GET 404; DELETE clears MIME and retains URL. |
| Nonempty blob, null MIME | Migrate; GET | Preserve null MIME and bytes; GET returns bytes with `application/octet-stream`. |
| Nonempty blob, malformed MIME | Migrate; GET | Preserve exact raw MIME and bytes; if parser throws, GET returns `application/octet-stream`. |
| Nonempty blob, parseable unexpected MIME | Migrate; GET | Preserve exact raw MIME; GET uses parsed type even if bytes do not match or current upload policy disallows it. |
| Empty blob, any MIME | Read entity; GET; DELETE | Endpoint selected, GET 404; preserve MIME through migration and clear MIME on DELETE. |
| Referenced object missing/corrupt | Read/GET | Visible failure and reconciliation, not silent legacy fallback; test failure and retry behavior. |

Also test MIME/size/hash verification, relative legacy paths, empty URL, authorization, coexistence after migration, and retries/concurrent replace-delete with conditional updates. No current data content is assumed.

## 8. Future migration and rollback

### 8.1 Source-to-destination map

| MariaDB source | Proposed destination |
| --- | --- |
| PJ | `pjs/{pjId}` plus name reservation; preserve raw `imagen_url` separately from image object and metadata, including coexistence. |
| NPC | `npcs/{npcId}` plus name reservation; preserve raw `imagen_url` separately from image object and metadata, including coexistence. |
| Advantage and prerequisite rows | `ventajas/{ventajaId}` with `npcId` and embedded prerequisite values, preserving null rows and multiplicities in the restricted inventory; affected records block active migration and cutover under section 4.4. |
| Relationship | `relaciones/{relacionId}`; pair guard only after a clean duplicate audit; quarantine duplicate pairs, do not discard them or arbitrarily select one. |
| Interaction | `relaciones/{relacionId}/interacciones/{interaccionId}`. |
| Selection | `relaciones/{relacionId}/selecciones/{seleccionId}`. |
| Selection snapshot | `relaciones/{relacionId}/snapshots/{snapshotId}`. |
| Snapshot item | `…/snapshots/{snapshotId}/items/{itemId}`. |
| Action log | `logs/{logId}`. |
| Legacy image URLs and files | Separate inventory; preserve raw URL, original bytes, provenance and precedence; anomalous cases are quarantined without silent repair. Do not assign by name without review. |

### 8.2 Migration sequence and equivalence checks

1. **Mandatory gate:** complete and verify isolated MariaDB backup and restore, including relationships, binaries, MIME and legacy image files. Existing JSON omits image bytes and historical data; see [backup and restore](backup-and-restore.md).
2. In the isolated restored copy, inventory aggregate counts, sizes, invalid IDs, nulls, dates, orphan references and image sources without exposing private data.
3. Detect name collisions under source collation; duplicate PJ–NPC pairs, selection levels and snapshot versions; dangling advantages; and counter/history discrepancies. Do not silently repair them.
4. Establish a consistent source cut and write policy. Require a read-only window for final extraction, verification and cutover; do not use live-write migration or dual writes under this baseline.
5. Migrate name reservations and PJ/NPC parents; advantages/prerequisites; relationships and pair guards after a clean duplicate audit; selections, interactions, snapshots and items; then logs. Copy image objects and verify references; objects uploaded before document creation remain provisional until confirmed.
6. Use deterministic IDs, execution manifests, idempotent writes and explicit conflict handling. A retry must not inadvertently overwrite later changes.
7. Compare entity and relationship counts, IDs, fields and nulls, prerequisite value multisets including null multiplicity, per-parent totals, referential integrity, query results and ordering; compare each image's raw legacy URL, coexistence, blob null versus empty, checksum, byte count and independently nullable raw MIME, including MIME without a blob. Exercise use cases with synthetic data and aggregate results from the isolated restore.
8. Reject cutover for missing objects, unresolved orphans, unexplained discrepancies or failed functional comparisons.

### 8.3 Rollback

- **Before cutover:** stop the process, discard or isolate the test destination, and retain MariaDB as the source of truth.
- **After cutover but before new Firestore writes:** point back to MariaDB and verify its integrity.
- **After Firestore accepts new writes:** simply returning to MariaDB would lose those writes. Freeze writes and use a tested reverse migration/reconciliation, otherwise remain stopped; no data loss is accepted under this baseline. Dual writes are not proposed without conflict and partial-failure handling.

Future backups must cover both Firestore and objects. A normal Firestore export is not necessarily a point-in-time snapshot at its start and does not include index definitions; choose and rehearse a consistent backup/restore procedure. [Official export/import documentation](https://docs.cloud.google.com/firestore/native/docs/manage-data/export-import).

## 9. Selected draft baseline, alternatives, and remaining gates

The following choices resolve the design alternatives conservatively for the next review; they are **draft design decisions, not a claim of formal approval**. They do not authorize implementation, production access, resource creation, or data migration. Actual data distribution, JPA null hydration, full MariaDB collation equivalence, capacity, regional selection, authorization granularity and historical time zone remain unverified. Failing any evidence gate blocks the affected operation or real cutover; it is not permission to discard or change data.

| Design area | Selected conservative baseline | Alternative and tradeoff | Status / gate |
| --- | --- | --- | --- |
| Images and access | Private Cloud Storage, served through Spring behind IAP with application authorization and least privilege; retain original bytes, URL and independent raw MIME. | Signed URLs reduce backend traffic but are bearer credentials; embedding is size-limited. | Selected draft; validate access, cost, private storage recovery and concurrency. |
| Image lifecycle | Preserve current binary-first priority, PUT clearing URL, normal URL update and binary-only DELETE, including empty blob and MIME edge cases. | Clearing both or retaining URL on PUT changes API/UI behavior. | Selected draft; synthetic compatibility tests required. |
| Nullable prerequisites | Preserve raw rows and multiplicities; quarantine affected records and block real migration/cutover if nulls occur; do not silently correct. | Runtime nullable elements can preserve information but JPA behavior is unverified; removal can alter AND/OR. | Selected fail-closed policy; isolated SQL/JPA round-trip and behavior evidence required before enabling affected records. |
| Name comparison | Require demonstrated MariaDB collation equivalence before name writes or migration; transactional reservations and collision verification. | A simpler new normalization risks different uniqueness results. | Selected fail-closed policy; equivalence remains an evidence gate, not proven. |
| Relationship pairs | Preserve all source IDs; quarantine duplicate pairs and block cutover; serialize new creates with a shared guard and atomic parent checks. | Enforcing a unique pair retroactively can discard valid source state; unguarded creates race. | Selected draft; duplicate frequency unknown, explicit resolution needed if detected. |
| Large operations and deletion | Bounded all-or-nothing transactions and contention guards; fail without partial effects if limits are exceeded; preserve individual relation FK-style rejection. | Tombstones and batch cleanup scale but change observable atomicity; cascade could destroy history. | Selected draft; verify limits and concurrency; any changed behavior requires a separate review. |
| Historical dates | Preserve original local values losslessly; never infer UTC. | Converting requires a verified zone and daylight-saving policy. | Selected draft; historical interpretation remains unknown. |
| Source anomalies | Preserve originals and a restricted reconciliation manifest; quarantine unresolved inconsistencies and block cutover. | Automatic repair is faster but risks silent loss or changed behavior. | Selected fail-closed policy; specific remediation requires review and audit. |
| Cutover and rollback | Freeze writes for consistent final extraction and cutover; after Firestore writes, freeze and use tested reverse reconciliation before return. No data-loss window is accepted. | Live migration or a simple return without reverse sync adds complexity or loses new writes. | Selected draft; isolated end-to-end rehearsal required. |
| Infrastructure and recovery | Use private access and independent recoverable backups; determine exact capacity, location, retention and budgets from measurements and requirements. | Premature fixed values are unsupported by current evidence. | Still an operational evidence gate before configuration or real migration. |

**Hard prerequisite for all real migration:** independently verify a MariaDB backup and isolated restore including relationships, image bytes, MIME and legacy image files. The existing JSON export is insufficient. Quarantined records, unresolved discrepancies, absent reverse-recovery evidence, or failed source/target comparisons also block cutover. Neither this document nor its selected baseline implies that any gate has already passed.

## 10. Proposed next implementation and testing sequence

1. Review the selected draft baseline and data contracts; establish measured operational limits and confirm the remaining evidence gates before implementing affected components.
2. Complete isolated MariaDB restoration and execute data-quality and size inventories there.
3. Design domain-specific repository and transaction interfaces; do not depend on a generic JPA proxy to supply unimplemented semantics.
4. In a later authorized phase, implement Firestore adapters and image storage only in test environments using synthetic data first.
5. Test queries and indexes, equivalence, Unicode/collation, dates and nulls, concurrent writes, duplicates, operation size, failures, retries, deletions and image recovery.
6. Rehearse migration and rollback on the restored copy and record acceptance evidence before considering real-environment access.
