package com.opipo.jaderegent.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ImportNpcUseCase {

    private final NPCRepository npcRepository;
    private final VentajaRepository ventajaRepository;
    private final ObjectMapper objectMapper;

    public ImportNpcUseCase(NPCRepository npcRepository, VentajaRepository ventajaRepository,
            ObjectMapper objectMapper) {
        this.npcRepository = npcRepository;
        this.ventajaRepository = ventajaRepository;
        this.objectMapper = objectMapper;
    }

    public void importNpc(InputStream jsonInputStream, String fileName) throws IOException {
        String npcId = fileName.replace(".json", "");
        JsonNode root = objectMapper.readTree(jsonInputStream);

        String nombre = root.get("nombre").asText();
        String descripcionLarga = root.has("descripcion_larga") ? root.get("descripcion_larga").asText() : "";
        Integer nivelMaximo = root.get("nivel_maximo").asInt();
        String imagenUrl = root.has("imagen_url") ? root.get("imagen_url").asText() : "";

        // Save NPC
        NPC npc = NPC.builder()
                .npcId(npcId)
                .nombre(nombre)
                .descripcionLarga(descripcionLarga)
                .nivelMaximo(nivelMaximo)
                .imagenUrl(imagenUrl)
                .fechaImportacion(LocalDateTime.now())
                .build();

        npcRepository.save(npc);

        // Process Advantages (Ventajas)
        List<Ventaja> ventajas = new ArrayList<>();
        if (root.has("ventajas")) {
            JsonNode ventajasNode = root.get("ventajas");
            if (ventajasNode.isArray()) {
                for (JsonNode vNode : ventajasNode) {
                    Ventaja ventaja = parseVentaja(vNode, npc);
                    ventajas.add(ventaja);
                }
            }
        }

        // Remove existing advantages for this NPC before saving new ones (full replace)
        List<Ventaja> existingVentajas = ventajaRepository.findByNpcNpcId(npcId);
        ventajaRepository.deleteAll(existingVentajas);

        ventajaRepository.saveAll(ventajas);
    }

    private Ventaja parseVentaja(JsonNode node, NPC npc) {
        String nombre = node.get("nombre").asText();
        String ventajaId = node.has("ventaja_id") ? node.get("ventaja_id").asText()
                : generateVentajaId(npc.getNpcId(), nombre);
        String descripcionLarga = node.has("descripcion_larga") ? node.get("descripcion_larga").asText() : "";
        Integer minNivel = node.get("min_nivel_relacion").asInt();

        List<String> prerequisitos = new ArrayList<>();
        if (node.has("prerequisitos")) {
            JsonNode preNode = node.get("prerequisitos");
            if (preNode.isArray()) {
                for (JsonNode p : preNode) {
                    prerequisitos.add(p.asText());
                }
            }
        }

        String prerequisitosOperator = node.has("prerequisitos_operator")
                ? node.get("prerequisitos_operator").asText()
                : "AND";

        return Ventaja.builder()
                .ventajaId(ventajaId)
                .npc(npc)
                .nombre(nombre)
                .descripcionLarga(descripcionLarga)
                .minNivelRelacion(minNivel)
                .prerequisitos(prerequisitos)
                .prerequisitosOperator(prerequisitosOperator)
                .build();
    }

    private String generateVentajaId(String npcId, String nombreVentaja) {
        String normalized = Normalizer.normalize(nombreVentaja, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        normalized = normalized.toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_]", "");
        return npcId + "_" + normalized;
    }
}
