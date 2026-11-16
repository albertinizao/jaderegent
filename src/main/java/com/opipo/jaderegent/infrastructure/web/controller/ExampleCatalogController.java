package com.opipo.jaderegent.infrastructure.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleCatalogController {
    private final Path root;

    public ExampleCatalogController(@Value("${app.catalogs.legacy-root:data/catalogos}") String root) {
        this.root = Path.of(root).toAbsolutePath().normalize();
    }

    @GetMapping("/data/catalogos/ameiko.json")
    public ResponseEntity<Resource> example() {
        try {
            Path base = root.toRealPath();
            Path expected = base.resolve("ameiko.json");
            if (!expected.toRealPath().equals(expected) || !Files.isRegularFile(expected)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .header("X-Content-Type-Options", "nosniff").body(new FileSystemResource(expected));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
