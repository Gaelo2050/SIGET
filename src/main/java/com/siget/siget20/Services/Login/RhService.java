package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaRhDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

public interface RhService {

    public String ValidacionDeRRh(Long docenteId, String correoInstitucional, String rfc, String curp, String estatus, String vigenciaDocente, BigDecimal porcentajeAsistencia, boolean tieneSancionActiva, boolean periodoEscolarActivo);
    public List<TablaRhDto> InformacionConfirmacion();

}
