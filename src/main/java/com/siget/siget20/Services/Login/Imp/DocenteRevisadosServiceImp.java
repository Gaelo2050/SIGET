package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.RevisadoDocenteDto;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.DocumentoRepository;
import com.siget.siget20.Repository.SIGET.Entity.Documento;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.DocenteRevisadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("docenterevisadosService")
public class DocenteRevisadosServiceImp implements DocenteRevisadosService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Override
    public List<RevisadoDocenteDto> obtenerRevisadosDocente(Long docenteId) {

        // 1) Todas las solicitudes del docente
        List<Solicitud> solicitudes = solicitudRepository.findByDocenteId(docenteId);

        // 2) Todos los documentos emitidos para ese docente
        List<DocumentoEmitido> documentosDocente =
                documentoEmitidoRepository.findByDocenteId(docenteId);

        // 3) Catálogo documentos: nombre -> id (constancia, CVU, etc.)
        Map<String, Long> documentoIdPorNombre = documentoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Documento::getDocumento,   // texto "constancia de adscripcion"
                        Documento::getId
                ));

        List<RevisadoDocenteDto> resultado = new ArrayList<>();

        for (Solicitud s : solicitudes) {

            String estado = s.getEstado();
            // Solo mostramos APROBADO o RECHAZADO
            if (!"APROBADO".equalsIgnoreCase(estado) &&
                    !"RECHAZADO".equalsIgnoreCase(estado)) {
                continue;
            }

            RevisadoDocenteDto dto = new RevisadoDocenteDto();
            dto.setSolicitudId(s.getSolicitudId());
            dto.setAreaDestino(s.getAreaDestino());
            dto.setDocumentoNombre(s.getDocumentoNombre());
            dto.setEstado(estado);

            // buscamos si hay documento_emitido que corresponda
            Long tipoId = documentoIdPorNombre.get(s.getDocumentoNombre());

            DocumentoEmitido docMatch = null;
            if (tipoId != null) {
                docMatch = documentosDocente.stream()
                        .filter(d -> d.getDocumentoId() != null
                                && d.getDocumentoId().equals(tipoId))
                        .findFirst()
                        .orElse(null);
            }

            if (docMatch != null &&
                    docMatch.getContenidoHtml() != null &&
                    !docMatch.getContenidoHtml().isBlank()) {

                dto.setPuedeDescargar(true);
                dto.setDocumentoEmitidoId(docMatch.getDocumentoEmitidoId());
            } else {
                dto.setPuedeDescargar(false);
                dto.setDocumentoEmitidoId(null);
            }

            resultado.add(dto);
        }

        return resultado;
    }
}
