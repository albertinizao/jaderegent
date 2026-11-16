package com.opipo.jaderegent.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DataExposureTest {
    @Autowired MockMvc mvc;
    private static final Path DATA;
    static {
        try {
            DATA = Files.createTempDirectory("jade-http-test-");
            Files.createDirectories(DATA.resolve("images/npc"));
            Files.createDirectories(DATA.resolve("catalogos"));
            Files.write(DATA.resolve("images/npc/ameiko.jpg"), new byte[] {1, 2, 3});
            Files.writeString(DATA.resolve("catalogos/ameiko.json"), "{\"nombre\":\"Ameiko\"}");
            Files.writeString(DATA.resolve("catalogos/private.json"), "test secret");
            Files.writeString(DATA.resolve("images/npc/private.trace.db"), "test secret");
            Files.writeString(DATA.resolve("database.mv.db"), "test secret");
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }

    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.images.legacy-root", () -> DATA.resolve("images").toString());
        registry.add("app.catalogs.legacy-root", () -> DATA.resolve("catalogos").toString());
    }

    @Test void servesLegacyImage() throws Exception {
        mvc.perform(get("/data/images/npc/ameiko.jpg"))
                .andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(new byte[] {1, 2, 3}));
    }

    @Test void servesOnlyThePublicExampleCatalog() throws Exception {
        mvc.perform(get("/data/catalogos/ameiko.json"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("{\"nombre\":\"Ameiko\"}"));
        mvc.perform(get("/data/catalogos/private.json")).andExpect(status().isNotFound());
    }

    @Test void rejectsEncodedTraversal() throws Exception {
        mvc.perform(get(URI.create("/data/images/npc/%2e%2e%2f%2e%2e%2fdatabase.mv.db")))
                .andExpect(status().is4xxClientError());
    }

    @Test void rejectsSymbolicLinksOutsideImages() throws Exception {
        Path link = DATA.resolve("images/npc/outside.jpg");
        try {
            Files.createSymbolicLink(link, DATA.resolve("database.mv.db"));
        } catch (UnsupportedOperationException | java.io.IOException | SecurityException e) {
            Assumptions.assumeTrue(false, "Creating symlinks is unavailable in this environment");
        }
        mvc.perform(get("/data/images/npc/outside.jpg")).andExpect(status().isNotFound());
    }

    @Test void deniesDatabaseAndTrace() throws Exception {
        mvc.perform(get("/data/database.mv.db")).andExpect(status().isNotFound());
        mvc.perform(get("/data/database.trace.db")).andExpect(status().isNotFound());
        mvc.perform(get("/data/images/npc/private.trace.db")).andExpect(status().isNotFound());
        mvc.perform(get("/data/images/other/ameiko.jpg")).andExpect(status().isNotFound());
    }
}
