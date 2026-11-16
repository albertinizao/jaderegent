package com.opipo.jaderegent.infrastructure.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LegacyImageController {
    private final Path root;

    public LegacyImageController(@Value("${app.images.legacy-root:data/images}") String root) {
        this.root = Path.of(root).toAbsolutePath().normalize();
    }

    @GetMapping("/data/images/{category}/{name:.+}")
    public ResponseEntity<Resource> image(@PathVariable String category, @PathVariable String name) {
        if (!Set.of("npc", "pj").contains(category)) return ResponseEntity.notFound().build();
        String lower = name.toLowerCase(Locale.ROOT);
        MediaType type = lower.endsWith(".png") ? MediaType.IMAGE_PNG
                : lower.endsWith(".jpg") || lower.endsWith(".jpeg") ? MediaType.IMAGE_JPEG
                : lower.endsWith(".webp") ? MediaType.parseMediaType("image/webp") : null;
        if (type == null || name.contains("/") || name.contains("\\")) return ResponseEntity.notFound().build();
        try {
            Path base = root.toRealPath();
            Path file = base.resolve(category).resolve(name).toRealPath();
            if (!file.startsWith(base.resolve(category)) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().contentType(type).header("X-Content-Type-Options", "nosniff")
                    .body(new FileSystemResource(file));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
