package com.siget.siget20.Repository.SIGET;

import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("solicitudRepository")
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByAreaDestinoAndEstado(String areaDestino, String estado);
    List<Solicitud> findByDocenteId(Long docenteId);
}
