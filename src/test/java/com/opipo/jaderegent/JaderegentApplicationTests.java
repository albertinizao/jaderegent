package com.opipo.jaderegent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Set;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class JaderegentApplicationTests {

    @Container
    static final MariaDBContainer<?> mariaDb = new MariaDBContainer<>("mariadb:11.4")
            .withDatabaseName("gestion_relaciones")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void mariaDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDb::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDb::getUsername);
        registry.add("spring.datasource.password", mariaDb::getPassword);
        registry.add("spring.datasource.driver-class-name", mariaDb::getDriverClassName);
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Test
    void contextLoads() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void flywayCreatesEntitySchema() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Set<String> tables = Set.copyOf(jdbc.queryForList(
                "select table_name from information_schema.tables where table_schema = database()",
                String.class));

        assertThat(tables).contains("pj", "npc", "ventaja", "ventaja_prerequisitos", "relacion",
                "interaccion", "seleccion_ventaja", "snapshot_selecciones", "snapshot_seleccion_item", "log_accion");
    }

    @Test
    void schemaMetadataMatchesEntityMappings() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertColumn(jdbc, "pj", "pj_id", "char", 36);
        assertColumn(jdbc, "relacion", "relacion_id", "char", 36);
        assertColumn(jdbc, "interaccion", "tipo", "enum", null);
        assertColumn(jdbc, "relacion", "pendiente_eleccion", "bit", null);
        assertColumn(jdbc, "relacion", "ultima_actualizacion_ts", "datetime", null);
        assertColumn(jdbc, "npc", "descripcion_larga", "longtext", null);

        assertThat(primaryKey(jdbc, "pj")).contains("pj_id");
        assertThat(primaryKey(jdbc, "relacion")).contains("relacion_id");
        assertThat(foreignKeyCount(jdbc, "relacion", "pj_id", "pj")).isEqualTo(1);
        assertThat(foreignKeyCount(jdbc, "relacion", "npc_id", "npc")).isEqualTo(1);
        assertThat(foreignKeyCount(jdbc, "ventaja", "npc_id", "npc")).isEqualTo(1);
        assertThat(foreignKeyCount(jdbc, "interaccion", "relacion_id", "relacion")).isEqualTo(1);
        assertThat(uniqueConstraintCount(jdbc, "pj")).isGreaterThanOrEqualTo(1);
        assertThat(uniqueConstraintCount(jdbc, "npc")).isGreaterThanOrEqualTo(1);
        assertThat(indexExists(jdbc, "relacion", "idx_relacion_pj")).isTrue();
        assertThat(indexExists(jdbc, "relacion", "idx_relacion_npc")).isTrue();
    }

    @Test
    void secondFlywayMigrationIsIdempotent() {
        int appliedBefore = flyway.info().applied().length;
        assertThatCode(flyway::validate).doesNotThrowAnyException();
        flyway.migrate();
        assertThatCode(flyway::validate).doesNotThrowAnyException();
        assertThat(flyway.info().applied()).hasSize(appliedBefore);
    }

    private static void assertColumn(JdbcTemplate jdbc, String table, String column, String expectedType,
            Integer expectedLength) {
        var metadata = jdbc.queryForMap(
                "select data_type, character_maximum_length from information_schema.columns "
                        + "where table_schema = database() and table_name = ? and column_name = ?",
                table, column);
        assertThat(metadata.get("data_type").toString().toLowerCase()).isEqualTo(expectedType);
        if (expectedLength != null) {
            assertThat(((Number) metadata.get("character_maximum_length")).intValue()).isEqualTo(expectedLength);
        }
    }

    private static Set<String> primaryKey(JdbcTemplate jdbc, String table) {
        return Set.copyOf(jdbc.queryForList(
                "select column_name from information_schema.key_column_usage "
                        + "where table_schema = database() and table_name = ? and constraint_name = 'PRIMARY'",
                String.class, table));
    }

    private static int foreignKeyCount(JdbcTemplate jdbc, String table, String column, String referencedTable) {
        return jdbc.queryForObject(
                "select count(*) from information_schema.key_column_usage where table_schema = database() "
                        + "and table_name = ? and column_name = ? and referenced_table_name = ?",
                Integer.class, table, column, referencedTable);
    }

    private static int uniqueConstraintCount(JdbcTemplate jdbc, String table) {
        return jdbc.queryForObject(
                "select count(*) from information_schema.table_constraints where table_schema = database() "
                        + "and table_name = ? and constraint_type = 'UNIQUE'",
                Integer.class, table);
    }

    private static boolean indexExists(JdbcTemplate jdbc, String table, String indexName) {
        return jdbc.queryForObject(
                "select count(*) from information_schema.statistics where table_schema = database() "
                        + "and table_name = ? and index_name = ?",
                Integer.class, table, indexName) > 0;
    }
}
