package com.opipo.jaderegent.infrastructure.web.controller;

import com.opipo.jaderegent.application.usecase.BackupUseCase;
import com.opipo.jaderegent.infrastructure.web.dto.FullBackupDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupUseCase backupUseCase;

    public BackupController(BackupUseCase backupUseCase) {
        this.backupUseCase = backupUseCase;
    }

    @GetMapping
    public ResponseEntity<FullBackupDTO> downloadBackup() {
        return ResponseEntity.ok(backupUseCase.exportBackup());
    }

    @PostMapping("/restore")
    public ResponseEntity<Void> restoreBackup(@RequestBody FullBackupDTO backupData) {
        backupUseCase.importBackup(backupData);
        return ResponseEntity.ok().build();
    }
}
