package com.opipo.jaderegent.application.usecase;

import com.opipo.jaderegent.domain.model.PJ;
import com.opipo.jaderegent.domain.model.Relacion;
import com.opipo.jaderegent.domain.model.SeleccionVentaja;
import com.opipo.jaderegent.domain.repository.NPCRepository;
import com.opipo.jaderegent.domain.repository.PJRepository;
import com.opipo.jaderegent.domain.repository.RelacionRepository;
import com.opipo.jaderegent.domain.repository.SeleccionVentajaRepository;
import com.opipo.jaderegent.domain.repository.VentajaRepository;
import com.opipo.jaderegent.infrastructure.web.dto.PjDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.RelacionDTO;
import com.opipo.jaderegent.infrastructure.web.dto.SeleccionVentajaDTO;
import com.opipo.jaderegent.infrastructure.web.dto.FullBackupDTO;
import com.opipo.jaderegent.infrastructure.web.dto.NpcDetailDTO;
import com.opipo.jaderegent.infrastructure.web.dto.VentajaDTO;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BackupUseCase {
    private final PJRepository pjRepository;
    private final GetPjDetailUseCase getPjDetailUseCase;
    private final GetNpcDetailUseCase getNpcDetailUseCase;
    private final RelacionRepository relacionRepository;
    private final SeleccionVentajaRepository seleccionVentajaRepository;
    private final NPCRepository npcRepository;
    private final VentajaRepository ventajaRepository;

    public BackupUseCase(PJRepository pjRepository, GetPjDetailUseCase getPjDetailUseCase,
            GetNpcDetailUseCase getNpcDetailUseCase,
            RelacionRepository relacionRepository, SeleccionVentajaRepository seleccionVentajaRepository,
            NPCRepository npcRepository, VentajaRepository ventajaRepository) {
        this.pjRepository = pjRepository;
        this.getPjDetailUseCase = getPjDetailUseCase;
        this.getNpcDetailUseCase = getNpcDetailUseCase;
        this.relacionRepository = relacionRepository;
        this.seleccionVentajaRepository = seleccionVentajaRepository;
        this.npcRepository = npcRepository;
        this.ventajaRepository = ventajaRepository;
    }

    public FullBackupDTO exportBackup() {
        List<PjDetailDTO> pjs = pjRepository.findAll().stream()
                .map(pj -> getPjDetailUseCase.getPjDetail(pj.getPjId()))
                .collect(Collectors.toList());

        List<NpcDetailDTO> npcs = npcRepository.findAll().stream()
                .map(npc -> getNpcDetailUseCase.getNpcDetail(npc.getNpcId()))
                .collect(Collectors.toList());

        return new FullBackupDTO(pjs, npcs);
    }

    @Transactional
    public void importBackup(FullBackupDTO backup) {
        // Import NPCs first (needed for relationships)
        if (backup.getNpcs() != null) {
            for (NpcDetailDTO npcDto : backup.getNpcs()) {
                com.opipo.jaderegent.domain.model.NPC npc = npcRepository.findById(npcDto.getNpc().getNpcId())
                        .orElse(new com.opipo.jaderegent.domain.model.NPC());

                npc.setNpcId(npcDto.getNpc().getNpcId());
                npc.setNombre(npcDto.getNpc().getNombre());
                npc.setDescripcionLarga(npcDto.getNpc().getDescripcionLarga());
                npc.setImagenUrl(npcDto.getNpc().getImagenUrl());
                npc.setNivelMaximo(npcDto.getNpc().getNivelMaximo());
                npcRepository.save(npc);

                // Import ventajas for this NPC
                if (npcDto.getVentajas() != null) {
                    for (VentajaDTO ventajaDto : npcDto.getVentajas()) {
                        com.opipo.jaderegent.domain.model.Ventaja existingVentaja = ventajaRepository
                                .findById(ventajaDto.getVentajaId())
                                .orElse(new com.opipo.jaderegent.domain.model.Ventaja());

                        existingVentaja.setVentajaId(ventajaDto.getVentajaId());
                        existingVentaja.setNpc(npc);
                        existingVentaja.setNombre(ventajaDto.getNombre());
                        existingVentaja.setDescripcionLarga(ventajaDto.getDescripcionLarga());
                        existingVentaja.setMinNivelRelacion(ventajaDto.getMinNivelRelacion());
                        existingVentaja.setPrerequisitos(ventajaDto.getPrerequisitos());
                        existingVentaja.setPrerequisitosOperator(ventajaDto.getPrerequisitosOperator());
                        ventajaRepository.save(existingVentaja);
                    }
                }
            }
        }

        // Import PJs and relationships
        if (backup.getPjs() != null) {
            for (PjDetailDTO dto : backup.getPjs()) {
                // Restore PJ
                Optional<PJ> existingById = pjRepository.findById(dto.getPj().getPjId());
                PJ pj;

                if (existingById.isPresent()) {
                    pj = existingById.get();
                } else {
                    // Check for name collision
                    Optional<PJ> existingByName = pjRepository.findByNombreDisplay(dto.getPj().getNombreDisplay());
                    if (existingByName.isPresent()) {
                        List<Relacion> rels = relacionRepository.findByPjPjId(existingByName.get().getPjId());
                        relacionRepository.deleteAll(rels);
                        pjRepository.delete(existingByName.get());
                        pjRepository.flush();
                    }
                    pj = new PJ();
                }

                pj.setPjId(dto.getPj().getPjId());
                pj.setNombreDisplay(dto.getPj().getNombreDisplay());
                pj.setNotaOpcional(dto.getPj().getNotaOpcional());
                pj.setImagenUrl(dto.getPj().getImagenUrl());
                pj = pjRepository.save(pj);

                // Restore Relationships
                if (dto.getRelaciones() != null) {
                    for (RelacionDTO relDto : dto.getRelaciones()) {
                        Relacion rel = null;

                        // Try by ID first if present
                        if (relDto.getRelacionId() != null) {
                            rel = relacionRepository.findById(relDto.getRelacionId()).orElse(null);
                        }

                        // Fallback to PJ+NPC
                        if (rel == null) {
                            Optional<Relacion> existing = relacionRepository.findByPjPjIdAndNpcNpcId(pj.getPjId(),
                                    relDto.getNpcId());
                            rel = existing.orElse(new Relacion());
                        }

                        if (relDto.getRelacionId() != null) {
                            rel.setRelacionId(relDto.getRelacionId());
                        }

                        rel.setPj(pj);
                        rel.setNpc(npcRepository.findById(relDto.getNpcId())
                                .orElseThrow(() -> new RuntimeException("NPC not found: " + relDto.getNpcId())));
                        rel.setNivelActual(relDto.getNivelActual());
                        rel.setPendienteEleccion(relDto.getPendienteEleccion());
                        rel.setConsistente(relDto.getConsistente());
                        rel.setContadorInteracciones(relDto.getContadorInteracciones());

                        rel = relacionRepository.save(rel);

                        // Restore Selections - delete existing ones first
                        seleccionVentajaRepository.deleteByRelacionRelacionId(rel.getRelacionId());

                        if (relDto.getSelecciones() != null) {
                            for (SeleccionVentajaDTO selDto : relDto.getSelecciones()) {
                                SeleccionVentaja sv = new SeleccionVentaja();
                                sv.setRelacion(rel);
                                sv.setVentaja(ventajaRepository.findById(selDto.getVentajaId()).orElseThrow(
                                        () -> new RuntimeException("Ventaja not found: " + selDto.getVentajaId())));
                                sv.setNivelAdquisicion(selDto.getNivelAdquisicion());
                                seleccionVentajaRepository.save(sv);
                            }
                        }
                    }
                }
            }
        }
    }
}
