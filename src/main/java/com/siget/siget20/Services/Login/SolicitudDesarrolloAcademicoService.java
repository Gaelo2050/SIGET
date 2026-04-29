package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.SolicitudDesarrolloAcademicoDto;
import com.siget.siget20.Model.DTO.SolicitudRhDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudDesarrolloAcademicoService {
    public List<SolicitudRhDto> SolicitudDesarrollo();

    void aprobarSolicitudDesarrollo(Long solicitudId);
    void rechazarSolicitudDesarrollo(Long solicitudId);
}
