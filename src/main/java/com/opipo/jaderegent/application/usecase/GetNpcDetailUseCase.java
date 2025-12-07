package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDTO;
import com.opipo.jaderegent.infrastructure.web.dto.VentajaDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetNpcDetailUseCase {

        private final NPCRepository npcRepository;
        private final VentajaRepository ventajaRepository;
        private final com.opipo.jaderegent.domain.repository.RelacionRepository relacionRepository;

        public GetNpcDetailUseCase(NPCRepository npcRepository, VentajaRepository ventajaRepository,
                        com.opipo.jaderegent.domain.repository.RelacionRepository relacionRepository) {
                this.npcRepository = npcRepository;
                this.ventajaRepository = ventajaRepository;
                this.relacionRepository = relacionRepository;
        }

        public NpcDetailDTO getNpcDetail(String npcId) {
                NPC npc = npcRepository.findById(npcId)
                                .orElseThrow(() -> new RuntimeException("NPC no encontrado: " + npcId));

                List<Ventaja> ventajas = ventajaRepository.findByNpcNpcId(npcId);

                // Convert NPC to DTO
                NpcDTO npcDTO = NpcDTO.builder()
                                .npcId(npc.getNpcId())
                                .nombre(npc.getNombre())
                                .descripcionLarga(npc.getDescripcionLarga())
                                .imagenUrl(npc.getImagenUrl())
                                .nivelMaximo(npc.getNivelMaximo())
                                .build();

                // Convert Ventajas to DTOs
                List<VentajaDTO> ventajaDTOs = ventajas.stream()
                                .map(v -> VentajaDTO.builder()
                                                .ventajaId(v.getVentajaId())
                                                .nombre(v.getNombre())
                                                .descripcionLarga(v.getDescripcionLarga())
                                                .minNivelRelacion(v.getMinNivelRelacion())
                                                .prerequisitos(v.getPrerequisitos())
                                                .prerequisitosOperator(v.getPrerequisitosOperator())
                                                .build())
                                .collect(Collectors.toList());

                List<com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO> relaciones = relacionRepository
                                .findByNpcNpcId(npcId).stream()
                                .map(rel -> com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO.builder()
                                                .relacionId(rel.getRelacionId())
                                                .pjId(rel.getPj().getPjId())
                                                .pjNombre(rel.getPj().getNombreDisplay())
                                                .pjImagenUrl(rel.getPj().getImagenUrl())
                                                // We map NPC info too even if redundant for this view, for consistency
                                                .npcId(rel.getNpc().getNpcId())
                                                .npcNombre(rel.getNpc().getNombre())
                                                .npcImagenUrl(rel.getNpc().getImagenUrl())
                                                .nivelActual(rel.getNivelActual())
                                                .nivelMaximo(rel.getNpc().getNivelMaximo())
                                                .pendienteEleccion(rel.getPendienteEleccion())
                                                .consistente(true) // Assumed
                                                .contadorInteracciones(rel.getContadorInteracciones())
                                                .ventajasObtenidasIds(
                                                                rel.getSelecciones().stream()
                                                                                .map(s -> s.getVentaja().getVentajaId())
                                                                                .toList())
                                                .selecciones(rel.getSelecciones().stream()
                                                                .map(s -> com.opipo.jaderegent.infrastructure.web.dto.SeleccionVentajaDTO
                                                                                .builder()
                                                                                .ventajaId(s.getVentaja()
                                                                                                .getVentajaId())
                                                                                .nombre(s.getVentaja().getNombre())
                                                                                .nivelAdquisicion(
                                                                                                s.getNivelAdquisicion())
                                                                                .nivelVentaja(s.getVentaja()
                                                                                                .getMinNivelRelacion())
                                                                                .descripcion(s.getVentaja()
                                                                                                .getDescripcionLarga())
                                                                                .build())
                                                                .toList())
                                                .build())
                                .toList();

                return new NpcDetailDTO(npcDTO, ventajaDTOs, relaciones);
        }
}
