package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.SolicitudRhDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudRhService {
    public List<SolicitudRhDto> SolicitudRh();

    void aprobarSolicitud(Long solicitudId);
    void rechazarSolicitud(Long solicitudId);
}
