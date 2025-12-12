package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.PjPrintableAdvantagesDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPjPrintableAdvantagesUseCase {

    private final PJRepository pjRepository;
    private final RelacionRepository relacionRepository;

    public GetPjPrintableAdvantagesUseCase(PJRepository pjRepository, RelacionRepository relacionRepository) {
        this.pjRepository = pjRepository;
        this.relacionRepository = relacionRepository;
    }

    @Transactional(readOnly = true)
    public PjPrintableAdvantagesDTO execute(UUID pjId) {
        PJ pj = pjRepository.findById(pjId)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));

        List<Relacion> relaciones = relacionRepository.findByPjPjId(pjId);

        List<PjPrintableAdvantagesDTO.NpcVentajasDTO> npcsDTO = new ArrayList<>();

        for (Relacion relacion : relaciones) {
            List<SeleccionVentaja> selecciones = relacion.getSelecciones();
            if (selecciones != null && !selecciones.isEmpty()) {

                List<PjPrintableAdvantagesDTO.VentajaDetailDTO> ventajasDTO = selecciones.stream()
                        .map(seleccion -> seleccion.getVentaja())
                        .map(ventaja -> new PjPrintableAdvantagesDTO.VentajaDetailDTO(
                                ventaja.getNombre(),
                                ventaja.getDescripcionLarga()))
                        .sorted(Comparator.comparing(PjPrintableAdvantagesDTO.VentajaDetailDTO::getNombre))
                        .collect(Collectors.toList());

                if (!ventajasDTO.isEmpty()) {
                    npcsDTO.add(new PjPrintableAdvantagesDTO.NpcVentajasDTO(
                            relacion.getNpc().getNombre(),
                            relacion.getNpc().getImagenUrl(),
                            ventajasDTO));
                }
            }
        }

        // Ordenar NPCs alfabéticamente
        npcsDTO.sort(Comparator.comparing(PjPrintableAdvantagesDTO.NpcVentajasDTO::getNpcNombre));

        return new PjPrintableAdvantagesDTO(
                pj.getNombreDisplay(),
                pj.getImagenUrl(),
                npcsDTO);
    }
}
