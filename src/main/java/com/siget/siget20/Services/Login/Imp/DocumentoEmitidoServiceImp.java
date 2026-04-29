package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Services.Login.DocumentoEmitidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("DocumentoEmitidoService")
public class DocumentoEmitidoServiceImp implements DocumentoEmitidoService {

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Override
    public DocumentoEmitido firmarConstanciaAdscripcion(DocumentoEmitido doc,
                                                        String base64Firma,
                                                        Usuario usuarioArea) {

        // Aquí SOLO ponemos la lógica de firma
        doc.setFirmaBase64(base64Firma);
        doc.setFirmadoArea(true);
        doc.setUsuarioFirmaId(usuarioArea.getId());
        doc.setFechaFirmaArea(LocalDateTime.now());
        doc.setEstado("FIRMADO");

        return documentoEmitidoRepository.save(doc);
    }
}
