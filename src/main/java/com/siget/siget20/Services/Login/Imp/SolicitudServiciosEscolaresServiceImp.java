package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.SolicitudServiciosEscolaresDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.SolicitudServiciosEscolaresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("solicitudServiciosEscolaresServiceImp")
public class SolicitudServiciosEscolaresServiceImp implements SolicitudServiciosEscolaresService {

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Override
    public List<SolicitudServiciosEscolaresDto> SolicitudServiciosEscolares() {

        List<SolicitudServiciosEscolaresDto> solicitudDtos = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        List<Usuario> usuarios = loginRepository.obtenerUsuarioCompleto();

        for (Solicitud solicitud : solicitudes) {

            if (!"Servicios Escolares".equalsIgnoreCase(solicitud.getAreaDestino())) {
                continue;
            }

            for (Usuario usuario : usuarios) {
                if (solicitud.getDocenteId().equals(usuario.getId())) {
                    solicitudDtos.add(
                            new SolicitudServiciosEscolaresDto(
                                    solicitud.getSolicitudId(),
                                    usuario.getNombrecompleto(),
                                    solicitud.getDocumentoNombre(),
                                    solicitud.getConvocatoria(),
                                    solicitud.getDocenteId()
                            )
                    );
                }
            }
        }

        return solicitudDtos;
    }

    @Override
    public void aprobarSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado("APROBADO");
        solicitudRepository.save(solicitud);
    }

    @Override
    public void rechazarSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado("RECHAZADO");
        solicitudRepository.save(solicitud);
    }
}
