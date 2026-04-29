package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.SolicitudServiciosEscolaresDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudServiciosEscolaresService {
    public List<SolicitudServiciosEscolaresDto> SolicitudServiciosEscolares();

    void aprobarSolicitud(Long solicitudId);
    void rechazarSolicitud(Long solicitudId);
}
