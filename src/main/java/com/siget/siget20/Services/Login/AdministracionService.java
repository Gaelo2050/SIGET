package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaAdministracionDto;

import java.util.List;

public interface AdministracionService {
    public String ValidacionDeAdministracion(Long DocenteId, String CorreoInstitucional, String Rfc, String Curp, String Estatus, String vigenciaDocente);
    public List<TablaAdministracionDto> InformacionConfirmacion();
}
