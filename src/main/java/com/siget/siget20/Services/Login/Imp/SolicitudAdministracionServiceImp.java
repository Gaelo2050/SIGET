package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.SolicitudAdministracionDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.SolicitudAdministracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("solicitudAdministracionServiceImp")
public class SolicitudAdministracionServiceImp implements SolicitudAdministracionService {

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Override
    public List<SolicitudAdministracionDto> SolicitudAdministracion() {

        List<SolicitudAdministracionDto> solicitudDtos = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        List<Usuario> usuarios = loginRepository.obtenerUsuarioCompleto();

        for (Solicitud solicitud : solicitudes) {

            if (!"Administracion".equalsIgnoreCase(solicitud.getAreaDestino())) {
                continue;
            }

            for (Usuario usuario : usuarios) {
                if (solicitud.getDocenteId().equals(usuario.getId())) {
                    solicitudDtos.add(
                            new SolicitudAdministracionDto(
                                    usuario.getId(),
                                    usuario.getNombrecompleto(),
                                    solicitud.getDocumentoNombre(),
                                    solicitud.getConvocatoria()
                            )
                    );
                }
            }
        }

        return solicitudDtos;
    }
}
