package com.opipo.jaderegent.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.opipo.jaderegent.domain.model.NPC;
import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.model.Ventaja;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.infrastructure.web.dto.PjPrintableAdvantagesDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPjPrintableAdvantagesUseCaseTest {

    @Mock private PJRepository pjRepository;
    @Mock private RelacionRepository relacionRepository;

    @InjectMocks
    private GetPjPrintableAdvantagesUseCase useCase;

    @Test
    void execute_shouldReturnOnlyNpcsWithSelectedAdvantagesOrdered() {
        UUID pjId = UUID.randomUUID();
        PJ pj = PJ.builder().pjId(pjId).nombreDisplay("Renji").imagenUrl("pj-img").build();

        NPC npcA = NPC.builder().npcId("a").nombre("Ameiko").imagenUrl("a-img").build();
        NPC npcK = NPC.builder().npcId("k").nombre("Koya").imagenUrl("k-img").build();

        Ventaja z = Ventaja.builder().ventajaId("z").nombre("Zeta").descripcionLarga("dz").build();
        Ventaja a = Ventaja.builder().ventajaId("a").nombre("Alfa").descripcionLarga("da").build();

        Relacion withAdvantages = Relacion.builder()
                .npc(npcK)
                .selecciones(new ArrayList<>(List.of(
                        SeleccionVentaja.builder().ventaja(z).build(),
                        SeleccionVentaja.builder().ventaja(a).build())))
                .build();

        Relacion withoutAdvantages = Relacion.builder().npc(npcA).selecciones(new ArrayList<>()).build();

        when(pjRepository.findById(pjId)).thenReturn(Optional.of(pj));
        when(relacionRepository.findByPjPjId(pjId)).thenReturn(List.of(withoutAdvantages, withAdvantages));

        PjPrintableAdvantagesDTO result = useCase.execute(pjId);

        assertEquals("Renji", result.getPjNombre());
        assertEquals(1, result.getNpcs().size());
        assertEquals("Koya", result.getNpcs().getFirst().getNpcNombre());
        assertEquals("Alfa", result.getNpcs().getFirst().getVentajas().getFirst().getNombre());
    }

    @Test
    void execute_shouldFailWhenPjNotFound() {
        UUID pjId = UUID.randomUUID();
        when(pjRepository.findById(pjId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.execute(pjId));

        assertEquals("Personaje no encontrado", ex.getMessage());
    }
}
