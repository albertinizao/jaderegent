# Plan de Pruebas (v1.0)
### Sistema de Relaciones — Campaña *El Regente de Jade*
**Versión:** 1.0  
**Autor:** Alberto Cebrián  
**Fecha:** Octubre de 2025  

---

## 1. Introduction
The purpose of this document is to define the testing strategy and validation plan for the *Relationship System — Jade Regent Campaign*.  
It ensures that the implemented functionalities comply with the specifications defined in the SRS, SAD, and Use Flow documents.  
The main goal is to verify functional behavior and basic performance under expected conditions.

---

## 2. Test Strategy

### 2.1 Objectives
- Verify that all main user flows (Import NPC, Register Interaction, etc.) work as expected.
- Validate data consistency after relationship updates or rollbacks.
- Ensure the UI correctly reflects backend operations.
- Confirm that the system remains stable under concurrent access by all 7 users.

### 2.2 Scope
- **Included:** Functional, regression, integration, acceptance, and basic performance tests.
- **Excluded:** Advanced security, scalability, and external integration testing.

### 2.3 Test Types

| Test Type | Objective | Tool/Method | Criteria for success |
|------------|------------|--------------|----------------------|
| **Unit** | Validate individual methods and domain rules. | JUnit 5 + Spring Boot Test | ≥ 70% coverage, no errors. |
| **Integration** | Verify repository/service/API cooperation. | Spring Boot Test | All flows complete successfully. |
| **Functional/UI** | Validate user interactions and responses. | Manual (browser, mobile) | All steps match expected results. |
| **Regression** | Ensure previous features remain stable. | Automated reruns | No regressions detected. |
| **Acceptance** | Confirm campaign requirements are met. | Manual (Master role) | All critical cases pass. |
| **Performance** | Confirm acceptable load and response time. | Manual + observation | ≤ 2s average response. |

---

## 3. Test Environment

### 3.1 Configuration
| Component | Description |
|------------|--------------|
| **Master device** | Windows PC running backend and frontend locally. |
| **Player device** | Android mobile device (same LAN). |
| **Network** | Local Wi-Fi, no external access. |
| **Browser** | Chrome / Firefox (latest stable). |
| **Backend** | Java 23, Spring Boot 3.x, H2 DB (file mode). |

### 3.2 Minimum requirements
- CPU: Dual-Core 2.0 GHz or higher  
- RAM: 2 GB minimum  
- Disk: 200 MB  
- JDK: 23+  
- Display: 1280×720 or higher  

### 3.3 Initial dataset
To execute tests consistently, prepare an initial dataset including:
- 2 Player Characters (PCs)
- 2 Non-Player Characters (NPCs)
- Relationship entries between each PC and NPC
- Advantage trees imported for each NPC

---

## 4. Test Roles
| Role | Description | Typical tests |
|------|--------------|----------------|
| **Master** | Administrator and GM, responsible for imports, relationships, and snapshots. | Import NPC, Register Interaction, Increase Level, Restore Snapshot |
| **Player** | Interacts via frontend to select and review advantages. | Select Advantage, Review Relationships |

All tests are executed by the Master, but role-based behavior is simulated for functional verification.

---

## 5. Test Cases

| ID | Test case | Role | Preconditions | Steps | Expected result | Status |
|----|-------------|------|----------------|--------|------------------|---------|
| **TC-01** | Import NPC (valid JSON) | Master | Valid JSON file exists. | Open NPC panel → Import file → Confirm import. | NPC created with advantages visible in list. | Pending |
| **TC-02** | Import NPC (invalid JSON) | Master | Corrupt JSON file. | Attempt import. | Error message displayed; no NPC created. | Pending |
| **TC-03** | Register interaction | Master | Relationship exists. | Open Relationships panel → Click 👍 or 👎. | Counter increases/decreases; message shown. | Pending |
| **TC-04** | Increase relationship level | Master | Relationship eligible for upgrade. | Open detail → Click “Increase level.” | Level increased; pending advantage flag set. | Pending |
| **TC-05** | Select advantage (valid) | Player | Relationship pending selection. | Open NPC → Choose valid advantage. | Advantage saved; pending flag cleared. | Pending |
| **TC-06** | Select advantage (invalid) | Player | Relationship pending selection. | Choose unavailable advantage. | Warning: missing prerequisites; no changes saved. | Pending |
| **TC-07** | Review relationships | Player | Relationships exist. | Open overview page. | All NPCs and levels shown with correct data. | Pending |
| **TC-08** | Restore snapshot | Master | At least one snapshot exists. | Open Snapshots → Select → Confirm restore. | Old selections restored; confirmation message. | Pending |

---

## 6. Acceptance Criteria
- All **functional cases (TC-01 to TC-08)** must pass without blocking issues.
- Minimum **70% unit test coverage** in core modules.
- No data corruption across CRUD operations.
- No critical regression found after modifications.
- UI responsiveness < 2 seconds per operation under normal conditions.

---

## 7. Issue Management
- Detected issues will be recorded in a simple `issues.md` file or GitHub Project board.  
- Severity levels:
  - **Critical:** prevents core functionality.  
  - **Major:** affects one key feature but has a workaround.  
  - **Minor:** cosmetic or low-priority.  
- Issues are re-tested after fixes to confirm resolution.

---

## 8. Conclusions
This Test Plan defines the procedures and criteria required to validate the Relationship System.  
Its primary goal is to ensure functional stability, data integrity, and a smooth experience for both Master and Players within the local environment.

**Version 1.0 — Complete Test Plan Document.**  
Covers strategy, roles, environment, and cases derived from the six main user flows.

