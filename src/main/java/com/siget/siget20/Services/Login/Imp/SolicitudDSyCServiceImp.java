package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.SolicitudRhDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.SolicitudDSyCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("solicitudDSyCServiceImp")
public class SolicitudDSyCServiceImp implements SolicitudDSyCService {

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Override
    public List<SolicitudRhDto> SolicitudDSyC() {

        List<SolicitudRhDto> solicitudRhDtos = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        List<Usuario> usuarios = loginRepository.obtenerUsuarioCompleto();

        for (Solicitud solicitud : solicitudes) {

            // aquí filtramos por el área DSyC
            if (!"DSyC".equalsIgnoreCase(solicitud.getAreaDestino())) {
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
    public void aprobarSolicitudDSyC(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado("APROBADO");
        solicitudRepository.save(solicitud);
    }

    @Override
    public void rechazarSolicitudDSyC(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado("RECHAZADO");
        solicitudRepository.save(solicitud);
    }
}
