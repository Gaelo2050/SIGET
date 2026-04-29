package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.SolicitudRhDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.SolicitudRhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("solicitudRhServiceImp")
public class SolicitudRhServiceImp implements SolicitudRhService {

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Override
    public List<SolicitudRhDto> SolicitudRh() {

        List<SolicitudRhDto> solicitudRhDtos = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        //todos los usuarios
        List<Usuario> usuarios = loginRepository.obtenerUsuarioCompleto();

        for (Solicitud solicitud : solicitudes) {


            if (!"Rh".equalsIgnoreCase(solicitud.getAreaDestino())) {
                continue;
            }

            for (Usuario usuario : usuarios) {
                if (solicitud.getDocenteId().equals(usuario.getId())) {
                    solicitudRhDtos.add(
                            new SolicitudRhDto(
                                    solicitud.getSolicitudId(),
                                    solicitud.getDocenteId(),
                                    usuario.getId(),
                                    usuario.getNombrecompleto(),
                                    solicitud.getDocumentoNombre(),
                                    solicitud.getConvocatoria()
                            )
                    );
                }
            }
        }

        return solicitudRhDtos;
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
