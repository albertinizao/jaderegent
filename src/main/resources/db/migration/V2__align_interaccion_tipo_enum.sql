-- Hibernate's MariaDB dialect maps @Enumerated(EnumType.STRING) to a native ENUM.
-- Keep the existing nullable column and values while aligning the persisted type.
ALTER TABLE interaccion
    MODIFY COLUMN tipo ENUM('POSITIVA', 'NEGATIVA') NULL;
