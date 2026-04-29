package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.RevisadoDocenteDto;

import java.util.List;

public interface DocenteRevisadosService {

    List<RevisadoDocenteDto> obtenerRevisadosDocente(Long docenteId);
}
