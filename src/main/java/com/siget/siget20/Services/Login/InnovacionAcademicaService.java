package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.InnovacionAcademicaDto;

public interface InnovacionAcademicaService {


    InnovacionAcademicaDto obtenerInnovacionPorDocente(Long docenteId);
}
