package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.DTO.SolicitudAdministracionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudAdministracionService {
    public List<SolicitudAdministracionDto> SolicitudAdministracion();
}
