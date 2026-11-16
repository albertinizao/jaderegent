# Backup and isolated restore

## Status and scope

This is an operational procedure, not evidence of a successful MariaDB restore.
On 2026-11-16 the configured local MariaDB endpoint was not listening and the
Docker engine was unavailable. The live database could not be backed up or
restored. Before migration, run the procedure below against the intended source
and verify the results. Do not connect to production without authorization.

The existing `/api/backup` JSON is **not a complete backup**: it only exports
PJ/NPC detail DTOs, does not carry `imagen_data` or `imagen_mime`, and does not
cover every table (including interaction and snapshot history). Its import is
destructive for some existing records; do not use it for this procedure.

An external-files-only snapshot was created at
`C:\Users\Alberto\JadeRegent-private-snapshot-5b53835ef374459abf9e4153e3a879b7\external-files-only.zip`.
It contains all 41 files in `data/`, including legacy H2 databases, trace,
catalog and images. It does **not** contain the current MariaDB database or
credentials. It was extracted to the adjacent `isolated-file-restore` directory;
all 41 files matched the originals by SHA-256 at snapshot time. That comparison
does not establish database consistency or restore MariaDB entities, relations,
binary images, or MIME types. The directory ACL is restricted to the current
Windows user and SYSTEM; secure/encrypt any additional copies. Historical H2
files are retained as-is and their consistency and relevance are not verified.

## Create a complete backup

1. Identify and obtain authorization for the actual MariaDB source. Stop
   application writes and scheduled jobs for the entire operation; this also
   keeps database image references and external files in sync. Record the server
   version and use a compatible MariaDB client. Confirm all required tables use
   transactional storage; `--single-transaction` cannot make nontransactional
   tables or concurrent DDL consistent. Record the source database name.
2. Create a **new**, private directory outside the repository, restrict its ACL
   to the operator and SYSTEM, and ensure no file with the intended backup name
   exists. Do not place backups in Git, Docker build contexts or public folders.
   Keep a separate restricted MariaDB option file outside the repository, with
   a `[client]` section containing the host, port, user and password. Do not
   place a password in a command, environment variable, log or screenshot. Limit
   the backup account to the necessary privileges.
3. In `cmd.exe`, using your own absolute paths and database name, run this once
   after checking `database.sql` does not exist. `--defaults-extra-file` must be
   the first option. It reads credentials from the restricted option file:

   ```cmd
   mariadb-dump --defaults-extra-file="C:\private\client.cnf" --single-transaction --quick --hex-blob --routines --events --triggers --skip-comments --no-create-db YOUR_DATABASE > "C:\private\new-backup\database.sql"
   ```

   Check the exit status (`echo %ERRORLEVEL%` must show `0`) and that the dump is
   nonempty. Redirection creates a file even on failure: a failed or partial file
   is NOT a backup. Schema, tables, rows, foreign keys, Flyway history, LONGBLOB
   bytes and MIME columns are included; accounts and server configuration are
   excluded. Preserve application version and migrations separately in Git.
   Review whether routines/events are present and whether the account can dump
   them; dumps may contain definers and other sensitive application data.
4. Copy the entire `data/` tree into the same private backup directory, without
   following links outside it. Include `images/`, `catalogos/` and legacy H2 files
   until their relevance is resolved. Raw H2 files are a reliable DB backup only
   when the H2 database is closed cleanly, or when created with H2's own online
   backup facility. A trace can contain secrets; protect it as sensitive data.
   Do not copy `.env*`, client option files or `config/application-local.properties`.
5. Generate SHA-256 hashes for the dump and each external file; preserve the
   manifest alongside the backup. Encrypt and store a separate protected copy.
   Verify file counts and hashes before and after copying. Restart writes only
   after both database and file copies have finished.

## Restore ONLY into a disposable, isolated destination

1. Start a **new**, uniquely named MariaDB container compatible with the source
   version, with `--network none`, no published ports and no bind mounts or
   pre-existing volumes; disable the event scheduler. An empty root password is
   acceptable solely inside this disconnected disposable container. Never point
   the following commands at an existing database. For example, in PowerShell:

   ```powershell
   $container = 'jade-restore-' + [guid]::NewGuid().ToString('N')
   docker run -d --name $container --network none -e MARIADB_ALLOW_EMPTY_ROOT_PASSWORD=1 mariadb:11.4 --event-scheduler=OFF
   # Wait for readiness; if this fails, stop and investigate.
   docker exec $container mariadb-admin -uroot ping
   docker exec $container mariadb -uroot -e 'CREATE DATABASE jade_restore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'
   ```

   Choose the image version matching the source rather than blindly assuming
   11.4. Wait until `ping` succeeds before creating the database. Verify the dump
   contains no `CREATE DATABASE`, `USE`, or unexpected destructive statements
   that could target another schema. Restore only inside this container:

   ```powershell
   $dump = 'C:\private\new-backup\database.sql'
   cmd /c "docker exec -i $container mariadb -uroot jade_restore < `"$dump`""
   if ($LASTEXITCODE -ne 0) { throw 'Restore failed' }
   ```

2. Extract/copy external files to a newly created, empty directory outside the
   source tree; never extract over `data/`. Verify every file against the saved
   manifest. Do not run the app against the source while testing the restore.
3. Compare the source and restored database using the same SQL checks and retain
   the output securely: row counts for **every** table; PJ/NPC/relationship and
   selection IDs and foreign keys; orphan checks; and for every non-null
   `pj.imagen_data` and `npc.imagen_data`, entity ID, `imagen_mime`, byte length
   and `SHA2(imagen_data, 256)`. Example checks:

   ```sql
   SELECT COUNT(*) FROM relacion r LEFT JOIN pj p ON p.pj_id=r.pj_id
     LEFT JOIN npc n ON n.npc_id=r.npc_id WHERE p.pj_id IS NULL OR n.npc_id IS NULL;
   SELECT pj_id, imagen_mime, OCTET_LENGTH(imagen_data), SHA2(imagen_data,256)
     FROM pj WHERE imagen_data IS NOT NULL ORDER BY pj_id;
   SELECT npc_id, imagen_mime, OCTET_LENGTH(imagen_data), SHA2(imagen_data,256)
     FROM npc WHERE imagen_data IS NOT NULL ORDER BY npc_id;
   ```

   Check representative entities and relationships through an isolated
   application, and compare response MIME and image hashes. Since the database
   container has `--network none`, an ordinary container or host application
   cannot reach it. One isolated option is to share that container's network
   namespace, with no published ports:

   - Build a local application image from the exact source version under test.
     Do not deploy it. Keep the database container running and isolated.
   - Open `docker exec -it $container mariadb -uroot` and, **inside that SQL
     session**, create a fresh temporary `jadecheck` user for `localhost` with a
     newly generated password and `SELECT` privileges only on `jade_restore`.
     Do not reuse source credentials. Do not put the password in a shell command
     or command history. Exit the SQL session after granting privileges.
   - Create a private, ACL-restricted temporary env file outside the repository
     containing `DB_URL=jdbc:mariadb://127.0.0.1:3306/jade_restore`,
     `DB_USERNAME=jadecheck`, the temporary `DB_PASSWORD`, and
     `SPRING_FLYWAY_ENABLED=false`. Check the restored Flyway history matches the
     app first; disabling Flyway prevents migration writes during this check.
   - In PowerShell, replace the example image and env-file paths, and optionally
     mount the restored `data/images` **read-only** at `/app/data/images`:

     ```powershell
     $app = 'jade-check-' + [guid]::NewGuid().ToString('N')
     docker run -d --name $app --network "container:$container" --env-file 'C:\private\restore.env' jade-local-check:latest
     # Use the same network namespace for an HTTP probe; publish no port.
     docker run --rm --network "container:$container" curlimages/curl:latest http://127.0.0.1:8080/api/npc
     ```

     Replace the probe path with an actual read-only endpoint and test selected
     `/api/npc/{id}/image` and `/api/pj/{id}/image` endpoints. If checking
     legacy images, mount the *isolated restored copy*, not the source folder,
     read-only; avoid mounting H2 files, traces or the backup directory. Review
     application startup and image version compatibility before relying on this
     unexecuted example. Remove the disposable app/container and temporary env
     file according to the operator's retention policy when checks are complete.
   If no stored images or relations
   exist, state this limitation explicitly; use synthetic fixtures for a separate
   mechanism test, not as proof of the real dataset. The restored database and
   extracted files remain sensitive; remove only these newly created disposable
   resources after verification and according to the operator's retention policy.

## HTTP and image build boundaries

Only `/data/images/npc/{file}` and `/data/images/pj/{file}` with JPEG, PNG or
WebP extensions are published as legacy images. The specific public example
`/data/catalogos/ameiko.json` is also available; no other catalog is exposed.
Do not put confidential files
under those public image paths. The Docker build context allowlists those image
extensions, the single example catalog and the built jar; it excludes H2 files, traces, other catalogs, backups,
configuration and credentials. Database-backed images remain served by the
existing `/api/.../image` endpoints. Docker build/runtime inspection still
requires an available Docker engine and must be done before deployment.
