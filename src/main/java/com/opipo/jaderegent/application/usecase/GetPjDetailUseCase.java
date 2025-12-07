package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.PjDTO;
import com.opipo.jaderegent.infrastructure.web.dto.PjDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetPjDetailUseCase {
        private final PJRepository pjRepository;
        private final RelacionRepository relacionRepository;

        public GetPjDetailUseCase(PJRepository pjRepository, RelacionRepository relacionRepository) {
                this.pjRepository = pjRepository;
                this.relacionRepository = relacionRepository;
        }

        public PjDetailDTO getPjDetail(UUID pjId) {
                PJ pj = pjRepository.findById(pjId)
                                .orElseThrow(() -> new RuntimeException("PJ no encontrado: " + pjId));

                List<Relacion> relaciones = relacionRepository.findByPjPjId(pjId);

                // Convert PJ to DTO
                PjDTO pjDTO = PjDTO.builder()
                                .pjId(pj.getPjId())
                                .nombreDisplay(pj.getNombreDisplay())
                                .notaOpcional(pj.getNotaOpcional())
                                .imagenUrl(pj.getImagenUrl())
                                .build();

                // Convert Relaciones to DTOs
                List<RelacionDTO> relacionDTOs = relaciones.stream()
                                .map(rel -> RelacionDTO.builder()
                                                .relacionId(rel.getRelacionId())
                                                .npcId(rel.getNpc().getNpcId())
                                                .npcNombre(rel.getNpc().getNombre())
                                                .npcImagenUrl(rel.getNpc().getImagenUrl())
                                                .nivelActual(rel.getNivelActual())
                                                .nivelMaximo(rel.getNpc().getNivelMaximo())
                                                .pendienteEleccion(rel.getPendienteEleccion())
                                                .consistente(rel.getConsistente())
                                                .contadorInteracciones(rel.getContadorInteracciones())
                                                .ventajasObtenidasIds(rel.getVentajasObtenidas().stream()
                                                                .map(v -> v.getVentajaId())
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
                                .sorted((a, b) -> a.getNpcNombre().compareTo(b.getNpcNombre())) // Sort by NPC name
                                .toList();

                return PjDetailDTO.builder()
                                .pj(pjDTO)
                                .relaciones(relacionDTOs)
                                .build();
        }
}
