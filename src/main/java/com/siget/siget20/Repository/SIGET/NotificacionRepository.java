package com.siget.siget20.Repository.SIGET;

import com.siget.siget20.Repository.SIGET.Entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("notificacionRepository")
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByAreaDestinoAndLeida(String areaDestino, boolean leida);
}