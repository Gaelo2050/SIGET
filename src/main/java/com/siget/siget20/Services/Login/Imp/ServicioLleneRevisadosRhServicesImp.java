package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.RevisadoRhDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosRhServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("servicioQueLleneRevisadosServices")
public class ServicioLleneRevisadosRhServicesImp implements ServicioLleneRevisadosRhServices {

    // IDs en la tabla DOCUMENTOS
    private static final Long ID_DOC_CONSTANCIA              = 1L;   // constancia de adscripcion
    private static final Long ID_DOC_TALON_PAGO              = 9L;   // talon de pago
    private static final Long ID_DOC_OFICIO_SABATICO         = 10L;  // oficio sabatico
    private static final Long ID_DOC_CONSTANCIA_LABORAL      = 12L;  // constancia laboral
    private static final Long ID_DOC_LIBERACION_ACTIVIDADES  = 13L;  // liberacion de actividades
    private static final Long ID_DOC_EXCLUSIVIDAD_LABORAL    = 16L;  // exclusividad laboral

    // Nombres que vienen en SOLICITUD.documento_nombre (en minúsculas)
    private static final String DOC_NOMBRE_CONSTANCIA              = "constancia de adscripcion";
    private static final String DOC_NOMBRE_TALON                   = "talon de pago";
    private static final String DOC_NOMBRE_OFICIO_SABATICO         = "oficio sabatico";
    private static final String DOC_NOMBRE_CONSTANCIA_LABORAL      = "constancia laboral";
    private static final String DOC_NOMBRE_LIBERACION_ACTIVIDADES  = "liberacion de actividades";
    private static final String DOC_NOMBRE_EXCLUSIVIDAD_LABORAL    = "exclusividad laboral";

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Override
    public List<RevisadoRhDto> obtenerRevisadosRh() {

        List<RevisadoRhDto> resultado = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        List<Usuario> usuarios      = loginRepository.obtenerUsuarioCompleto();
        List<DocumentoEmitido> documentos = documentoEmitidoRepository.findAll();

        for (Solicitud s : solicitudes) {

            // Solo solicitudes que vayan al área RH
            if (!"Rh".equalsIgnoreCase(s.getAreaDestino())) {
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
            if (docNombre.equals(DOC_NOMBRE_CONSTANCIA)) {
                docIdBuscado = ID_DOC_CONSTANCIA;
            } else if (docNombre.equals(DOC_NOMBRE_TALON)) {
                docIdBuscado = ID_DOC_TALON_PAGO;
            } else if (docNombre.equals(DOC_NOMBRE_OFICIO_SABATICO)) {
                docIdBuscado = ID_DOC_OFICIO_SABATICO;
            } else if (docNombre.equals(DOC_NOMBRE_CONSTANCIA_LABORAL)) {
                docIdBuscado = ID_DOC_CONSTANCIA_LABORAL;
            } else if (docNombre.equals(DOC_NOMBRE_LIBERACION_ACTIVIDADES)) {
                docIdBuscado = ID_DOC_LIBERACION_ACTIVIDADES;
            } else if (docNombre.equals(DOC_NOMBRE_EXCLUSIVIDAD_LABORAL)) {
                docIdBuscado = ID_DOC_EXCLUSIVIDAD_LABORAL;
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

            // Llenar DTO de salida
            RevisadoRhDto dto = new RevisadoRhDto();
            dto.setSolicitudId(s.getSolicitudId());
            dto.setDocenteId(s.getDocenteId());
            dto.setNombreDocente(usuario.getNombrecompleto());
            dto.setDocumentoNombre(s.getDocumentoNombre());
            dto.setConvocatoria(s.getConvocatoria());
            dto.setEstadoSolicitud(s.getEstado());      // APROBADO / RECHAZADO

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
