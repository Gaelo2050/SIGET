package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.TablaDocenteDto;

import java.util.List;

public interface DocenteService {
    public List<TablaDocenteDto> obtenerUsuarioTabla();
    public List<?> crearSolicitudYNotificacion(Long docenteId, String documentoNombre, String areaDestino, String convocatoria);
}
