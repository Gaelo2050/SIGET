package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;

public interface DocumentoEmitidoService {

    DocumentoEmitido firmarConstanciaAdscripcion(DocumentoEmitido doc, String base64Firma, Usuario usuarioArea);
}
