package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.InnovacionAcademicaDto;
import com.siget.siget20.Services.Login.InnovacionAcademicaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("innovacionAcademicaService")
public class InnovacionAcademicaServiceImp implements InnovacionAcademicaService {

    @Override
    public InnovacionAcademicaDto obtenerInnovacionPorDocente(Long docenteId) {


        InnovacionAcademicaDto dto = new InnovacionAcademicaDto();
        dto.setAsignatura("Ingeniería de software");
        dto.setEstrategia("Aprendizaje basado en proyectos");
        dto.setProgramaEducativo("Ingeniería en Sistemas Computacionales");

        return dto;
    }
}
