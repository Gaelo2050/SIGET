package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.RevisadoRhDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosDSyCServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("servicioQueLleneRevisadosDSyCServices")
public class ServicioLleneRevisadosDSyCServicesImp implements ServicioLleneRevisadosDSyCServices {

    // ================== CONFIGURACIÓN ==================


    private static final Long ID_DOC_RECURSO_EDUCATIVO = 8L;

    private static final String DOC_NOMBRE_RECURSO_EDUCATIVO = "recurso educativo";

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    // ================== LÓGICA PRINCIPAL ==================

    @Override
    public List<RevisadoRhDto> obtenerRevisadosDSyC() {

        List<RevisadoRhDto> resultado = new ArrayList<>();

        // Cargar una sola vez datos de BD
        List<Solicitud> solicitudes       = solicitudRepository.findAll();
        List<Usuario> usuarios            = loginRepository.obtenerUsuarioCompleto();
        List<DocumentoEmitido> documentos = documentoEmitidoRepository.findAll();

        for (Solicitud s : solicitudes) {

            // 1) Solo solicitudes dirigidas al área DSyC
            if (!"DSyC".equalsIgnoreCase(s.getAreaDestino())) {
                continue;
            }

            // 2) Solo las que ya tienen resultado
            String estado = s.getEstado();
            if (!"APROBADO".equalsIgnoreCase(estado) &&
                    !"RECHAZADO".equalsIgnoreCase(estado)) {
                continue;
            }

            // 3) Buscar datos del docente
            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(s.getDocenteId()))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                continue;
            }

            // 4) Normalizar nombre de documento
            String docNombre = s.getDocumentoNombre() != null
                    ? s.getDocumentoNombre().trim().toLowerCase()
                    : "";

            // 5) Determinar qué documento_id le corresponde
            Long docIdBuscado = null;

            if (docNombre.equals(DOC_NOMBRE_RECURSO_EDUCATIVO)) {
                docIdBuscado = ID_DOC_RECURSO_EDUCATIVO;
            }

            // 6) Buscar el DocumentoEmitido (si existe)
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

            // 7) Llenar el DTO de salida (mismo que RH / Desarrollo)
            RevisadoRhDto dto = new RevisadoRhDto();
            dto.setSolicitudId(s.getSolicitudId());
            dto.setDocenteId(s.getDocenteId());
            dto.setNombreDocente(usuario.getNombrecompleto());
            dto.setDocumentoNombre(s.getDocumentoNombre());
            dto.setConvocatoria(s.getConvocatoria());
            dto.setEstadoSolicitud(s.getEstado()); // APROBADO / RECHAZADO

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
