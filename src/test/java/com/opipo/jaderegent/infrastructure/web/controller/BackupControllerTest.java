package com.opipo.jaderegent.infrastructure.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.application.usecase.BackupUseCase;
import com.opipo.jaderegent.infrastructure.web.dto.FullBackupDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BackupControllerTest {

    @Mock private BackupUseCase backupUseCase;

    private BackupController controller;

    @BeforeEach
    void setUp() {
        controller = new BackupController(backupUseCase);
    }

    @Test
    void downloadBackup_shouldReturnExportedData() {
        FullBackupDTO backup = new FullBackupDTO(List.of(), List.of());
        when(backupUseCase.exportBackup()).thenReturn(backup);

        ResponseEntity<FullBackupDTO> response = controller.downloadBackup();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(backup, response.getBody());
    }

    @Test
    void restoreBackup_shouldCallImportAndReturnOk() {
        FullBackupDTO backup = new FullBackupDTO(List.of(), List.of());

        ResponseEntity<Void> response = controller.restoreBackup(backup);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(backupUseCase).importBackup(backup);
    }
}
