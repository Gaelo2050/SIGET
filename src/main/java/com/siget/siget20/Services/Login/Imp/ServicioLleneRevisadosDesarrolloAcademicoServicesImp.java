package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.RevisadoRhDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosDesarrolloAcademicoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("servicioQueLleneRevisadosDesarrolloAcademicoServices")
public class ServicioLleneRevisadosDesarrolloAcademicoServicesImp
        implements ServicioLleneRevisadosDesarrolloAcademicoServices {


    // IDs en la tabla DOCUMENTOS (ajusta si en tu BD son otros)
    private static final Long ID_DOC_ACTIVIDADES_TUTORIAS = 5L;
    private static final Long ID_DOC_CVU                  = 6L;

    // Nombres que vienen en SOLICITUD.documento_nombre (en minúsculas)
    private static final String DOC_NOMBRE_ACTIVIDADES_TUTORIAS = "actividades-tutorias";
    private static final String DOC_NOMBRE_CVU                  = "cvu";

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Override
    public List<RevisadoRhDto> obtenerRevisadosDesarrollo() {

        List<RevisadoRhDto> resultado = new ArrayList<>();

        // Cargamos todo lo necesario una sola vez
        List<Solicitud> solicitudes       = solicitudRepository.findAll();
        List<Usuario> usuarios            = loginRepository.obtenerUsuarioCompleto();
        List<DocumentoEmitido> documentos = documentoEmitidoRepository.findAll();

        for (Solicitud s : solicitudes) {

            // Solo solicitudes que vayan al área DESARROLLO ACADÉMICO
            if (!"DesarrolloAcademico".equalsIgnoreCase(s.getAreaDestino())) {
                continue;
            }

            // Solo las que ya tienen resultado
            String estado = s.getEstado();
            if (!"APROBADO".equalsIgnoreCase(estado) &&
                    !"RECHAZADO".equalsIgnoreCase(estado)) {
                continue;
            }

            // Buscar docente
            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(s.getDocenteId()))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                continue;
            }

            // Normalizar nombre de documento de la solicitud
            String docNombre = s.getDocumentoNombre() != null
                    ? s.getDocumentoNombre().trim().toLowerCase()
                    : "";

            // Decidir qué documento_id corresponde a esta solicitud
            Long docIdBuscado = null;
            if (docNombre.equals(DOC_NOMBRE_ACTIVIDADES_TUTORIAS)) {
                docIdBuscado = ID_DOC_ACTIVIDADES_TUTORIAS;
            } else if (docNombre.equals(DOC_NOMBRE_CVU)) {
                docIdBuscado = ID_DOC_CVU;
            }

            // Buscar el DocumentoEmitido adecuado (puede no existir aún)
            DocumentoEmitido docMatch = null;

            if (docIdBuscado != null) {
                Long finalDocIdBuscado = docIdBuscado;

                docMatch = documentos.stream()
                        .filter(d ->
                                d.getDocumentoId() != null &&
                                        d.getDocumentoId().equals(finalDocIdBuscado) &&
                                        d.getDocenteId()   != null &&
                                        d.getDocenteId().equals(s.getDocenteId())
                        )
                        .findFirst()
                        .orElse(null);
            }

            // Llenar DTO de salida (el mismo DTO que usas en RH)
            RevisadoRhDto dto = new RevisadoRhDto();
            dto.setSolicitudId(s.getSolicitudId());
            dto.setDocenteId(s.getDocenteId());
            dto.setNombreDocente(usuario.getNombrecompleto());
            dto.setDocumentoNombre(s.getDocumentoNombre());
            dto.setConvocatoria(s.getConvocatoria());
            dto.setEstadoSolicitud(s.getEstado());   // APROBADO / RECHAZADO

            if (docMatch != null) {
                dto.setDocumentoEmitidoId(docMatch.getDocumentoEmitidoId());
                dto.setEstadoDocumento(docMatch.getEstado()); // GENERADO / FIRMADO
                boolean tieneHtml = docMatch.getContenidoHtml() != null &&
                        !docMatch.getContenidoHtml().isBlank();
                dto.setTienePdf(tieneHtml);
            } else {
                dto.setDocumentoEmitidoId(null);
                dto.setEstadoDocumento(null);
                dto.setTienePdf(false);
            }

            resultado.add(dto);
        }

        return resultado;
    }
}
