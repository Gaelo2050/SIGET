package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaServiciosEscolaresDto;

import java.util.List;

public interface ServiciosEscolaresService {
    public String ValidacionDeServiciosEscolares(Long DocenteId, String CorreoInstitucional, String Rfc, String Curp, String Estatus, String vigenciaDocente);
    public List<TablaServiciosEscolaresDto> InformacionConfirmacion();
}
