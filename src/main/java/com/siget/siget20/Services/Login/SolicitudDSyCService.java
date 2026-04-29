package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.SolicitudRhDto;

import java.util.List;

public interface SolicitudDSyCService {

    // Lista que llenará la tabla de "Evaluar" de DSyC
    List<SolicitudRhDto> SolicitudDSyC();

    // Cambia el estado de la solicitud a APROBADO
    void aprobarSolicitudDSyC(Long solicitudId);

    // Cambia el estado de la solicitud a RECHAZADO
    void rechazarSolicitudDSyC(Long solicitudId);
}
