CREATE TABLE pj (
    pj_id CHAR(36) NOT NULL,
    nombre_display VARCHAR(255) NOT NULL,
    nota_opcional VARCHAR(255),
    imagen_url VARCHAR(255),
    fecha_creacion DATETIME(6),
    CONSTRAINT pk_pj PRIMARY KEY (pj_id),
    CONSTRAINT uk_pj_nombre_display UNIQUE (nombre_display)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE npc (
    npc_id VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion_larga LONGTEXT,
    nivel_maximo INT,
    imagen_url VARCHAR(255),
    fecha_importacion DATETIME(6),
    CONSTRAINT pk_npc PRIMARY KEY (npc_id),
    CONSTRAINT uk_npc_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE ventaja (
    ventaja_id VARCHAR(255) NOT NULL,
    npc_id VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion_larga LONGTEXT,
    min_nivel_relacion INT,
    prerequisitos_operator VARCHAR(255),
    CONSTRAINT pk_ventaja PRIMARY KEY (ventaja_id),
    CONSTRAINT fk_ventaja_npc FOREIGN KEY (npc_id) REFERENCES npc (npc_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_ventaja_npc ON ventaja (npc_id);

CREATE TABLE ventaja_prerequisitos (
    ventaja_ventaja_id VARCHAR(255) NOT NULL,
    prerequisitos VARCHAR(255),
    CONSTRAINT fk_ventaja_prerequisitos_ventaja FOREIGN KEY (ventaja_ventaja_id)
        REFERENCES ventaja (ventaja_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_ventaja_prerequisitos_ventaja ON ventaja_prerequisitos (ventaja_ventaja_id);

CREATE TABLE relacion (
    relacion_id CHAR(36) NOT NULL,
    pj_id CHAR(36) NOT NULL,
    npc_id VARCHAR(255) NOT NULL,
    nivel_actual INT,
    pendiente_eleccion BIT,
    consistente BIT,
    contador_interacciones INT,
    ultima_actualizacion_ts DATETIME(6),
    CONSTRAINT pk_relacion PRIMARY KEY (relacion_id),
    CONSTRAINT fk_relacion_pj FOREIGN KEY (pj_id) REFERENCES pj (pj_id),
    CONSTRAINT fk_relacion_npc FOREIGN KEY (npc_id) REFERENCES npc (npc_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_relacion_pj ON relacion (pj_id);
CREATE INDEX idx_relacion_npc ON relacion (npc_id);

CREATE TABLE interaccion (
    interaccion_id CHAR(36) NOT NULL,
    relacion_id CHAR(36) NOT NULL,
    tipo VARCHAR(255),
    valor INT,
    nota VARCHAR(255),
    ts DATETIME(6),
    usuario VARCHAR(255),
    CONSTRAINT pk_interaccion PRIMARY KEY (interaccion_id),
    CONSTRAINT fk_interaccion_relacion FOREIGN KEY (relacion_id) REFERENCES relacion (relacion_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_interaccion_relacion ON interaccion (relacion_id);

CREATE TABLE seleccion_ventaja (
    id CHAR(36) NOT NULL,
    relacion_id CHAR(36) NOT NULL,
    ventaja_id VARCHAR(255) NOT NULL,
    nivel_adquisicion INT,
    CONSTRAINT pk_seleccion_ventaja PRIMARY KEY (id),
    CONSTRAINT fk_seleccion_ventaja_relacion FOREIGN KEY (relacion_id) REFERENCES relacion (relacion_id),
    CONSTRAINT fk_seleccion_ventaja_ventaja FOREIGN KEY (ventaja_id) REFERENCES ventaja (ventaja_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_seleccion_ventaja_relacion ON seleccion_ventaja (relacion_id);
CREATE INDEX idx_seleccion_ventaja_ventaja ON seleccion_ventaja (ventaja_id);

CREATE TABLE snapshot_selecciones (
    snapshot_id CHAR(36) NOT NULL,
    relacion_id CHAR(36) NOT NULL,
    version INT,
    ts DATETIME(6),
    usuario VARCHAR(255),
    CONSTRAINT pk_snapshot_selecciones PRIMARY KEY (snapshot_id),
    CONSTRAINT fk_snapshot_selecciones_relacion FOREIGN KEY (relacion_id) REFERENCES relacion (relacion_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_snapshot_selecciones_relacion ON snapshot_selecciones (relacion_id);

CREATE TABLE snapshot_seleccion_item (
    id CHAR(36) NOT NULL,
    snapshot_id CHAR(36) NOT NULL,
    ventaja_id VARCHAR(255) NOT NULL,
    nivel_relacion INT,
    CONSTRAINT pk_snapshot_seleccion_item PRIMARY KEY (id),
    CONSTRAINT fk_snapshot_item_snapshot FOREIGN KEY (snapshot_id) REFERENCES snapshot_selecciones (snapshot_id),
    CONSTRAINT fk_snapshot_item_ventaja FOREIGN KEY (ventaja_id) REFERENCES ventaja (ventaja_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_snapshot_item_snapshot ON snapshot_seleccion_item (snapshot_id);
CREATE INDEX idx_snapshot_item_ventaja ON snapshot_seleccion_item (ventaja_id);

CREATE TABLE log_accion (
    log_id CHAR(36) NOT NULL,
    ts DATETIME(6),
    usuario VARCHAR(255),
    accion VARCHAR(255),
    detalle LONGTEXT,
    CONSTRAINT pk_log_accion PRIMARY KEY (log_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

