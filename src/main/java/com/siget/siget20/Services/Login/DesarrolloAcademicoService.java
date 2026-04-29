package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaDesarrolloAcademicoDto;

import java.util.List;

public interface DesarrolloAcademicoService {
    public String ValidacionDeDesarrolloAcademico(Long DocenteId, String CorreoInstitucional, String Rfc, String Curp, String Estatus, String vigenciaDocente);
    public List<TablaDesarrolloAcademicoDto> InformacionConfirmacion();
}
