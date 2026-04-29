package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.RevisadoServiciosEscolaresDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosServiciosEscolaresServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("servicioQueLleneRevisadosServiciosEscolaresServices")
public class ServicioLleneRevisadosServiciosEscolaresServicesImp implements ServicioLleneRevisadosServiciosEscolaresServices {

    // SOLO trabajamos con documento_id = 1 (constancia de adscripción)
    private static final Long ID_DOC_CONSTANCIA = 2L;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Override
    public List<RevisadoServiciosEscolaresDto> obtenerRevisadosServiciosEscolares() {

        List<RevisadoServiciosEscolaresDto> resultado = new ArrayList<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        List<Usuario> usuarios = loginRepository.obtenerUsuarioCompleto();
        List<DocumentoEmitido> documentos = documentoEmitidoRepository.findAll();

        for (Solicitud s : solicitudes) {

            // Solo solicitudes que vayan al área Servicios Escolares
            if (!"Servicios Escolares".equalsIgnoreCase(s.getAreaDestino())) {
                continue;
            }

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

            // Buscar si hay documento_emitido para este docente, documento_id=1
            DocumentoEmitido docMatch = documentos.stream()
                    .filter(d ->
                            d.getDocumentoId() != null &&
                                    ID_DOC_CONSTANCIA.equals(d.getDocumentoId()) &&
                                    d.getDocenteId() != null &&
                                    d.getDocenteId().equals(s.getDocenteId())
                    )
                    .findFirst()
                    .orElse(null);

            RevisadoServiciosEscolaresDto dto = new RevisadoServiciosEscolaresDto();
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
                dto.setTienePdf(false);
            }

            resultado.add(dto);
        }

        return resultado;
    }
}
