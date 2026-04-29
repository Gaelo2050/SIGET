package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaDSyCDto;

import java.util.List;

public interface DSyCService {
    public String ValidacionDeDSyC(Long DocenteId, String CorreoInstitucional, String Rfc, String Curp, String Estatus, String vigenciaDocente);
    public List<TablaDSyCDto> InformacionConfirmacion();
}
