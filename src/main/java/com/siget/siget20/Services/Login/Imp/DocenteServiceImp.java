package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.TablaDocenteDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoRepository;
import com.siget.siget20.Repository.SIGET.Entity.Documento;
import com.siget.siget20.Repository.SIGET.Entity.Notificacion;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.NotificacionRepository;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class DocenteServiceImp implements DocenteService {

    @Autowired
    @Qualifier("documentoRepository")
    private DocumentoRepository documentoRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login login;

    @Autowired
    NotificacionRepository notificacionRepository;

    @Autowired
    SolicitudRepository solicitudRepository;

    @Override
    public List<TablaDocenteDto> obtenerUsuarioTabla() {

        List<Documento> documentos = documentoRepository.findAll();
        List<Usuario> usuarios = login.obtenerUsuarioCompleto();

        List<TablaDocenteDto> resultado = new ArrayList<>();

        for (Documento doc : documentos) {

            for (Usuario us : usuarios) {

                // Saltar docentes
                if ("Docente".equalsIgnoreCase(us.getTipo())) {
                    continue;
                }


                if (doc.getUsuarioId().equals(us.getId())) {

                    resultado.add(new TablaDocenteDto(
                            us.getId(),
                            us.getNombrecompleto(),
                            doc.getDocumento(),
                            us.getTipo()
                    ));

                    break;
                }
            }
        }

        return resultado;
    }

    @Override
    public List<Notificacion> crearSolicitudYNotificacion(Long docenteId,
                                                          String documentoNombre,
                                                          String areaDestino,
                                                          String convocatoria) {

        // 1) Crear y guardar la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setDocenteId(docenteId);
        solicitud.setAreaDestino(areaDestino);
        solicitud.setDocumentoNombre(documentoNombre);
        solicitud.setConvocatoria(convocatoria);
        solicitud.setEstado("PENDIENTE");
        solicitud.setCreadoEn(LocalDateTime.now());

        // Guarda en la tabla solicitudes y recupera el ID generado
        solicitud = solicitudRepository.save(solicitud);

        // 2) Crear la notificación asociada
        Notificacion notificacion = new Notificacion();
        notificacion.setAreaDestino(areaDestino);
        notificacion.setSolicitudId(solicitud.getSolicitudId());
        notificacion.setMensaje("Nueva solicitud de " + documentoNombre + " del docente ID " + docenteId);
        notificacion.setLeida(false);
        notificacion.setCreadaEn(LocalDateTime.now());

        // Guarda en la tabla notificaciones
        notificacion = notificacionRepository.save(notificacion);

        // 3) Regresar la lista (por si luego quieres más de una notificación)
        return List.of(notificacion);
    }

}

