package com.siget.siget20.Model.DTO;

public class SolicitudServiciosEscolaresDto {

    private Long id;
    private String NameDocente;
    private String DocumentoNombre;
    private String convocatoria;
    private Long docenteId;


    private boolean tienePdf;
    private Long documentoEmitidoId;
    private String estadoSolicitud;

    public SolicitudServiciosEscolaresDto() {}

    public SolicitudServiciosEscolaresDto(Long id,
                                          String nameDocente,
                                          String documentoNombre,
                                          String convocatoria,
                                          Long docenteId) {
        this.id = id;
        this.NameDocente = nameDocente;
        this.DocumentoNombre = documentoNombre;
        this.convocatoria = convocatoria;
        this.docenteId = docenteId;
    }



    public String getNameDocente() {
        return NameDocente;
    }
    public void setNameDocente(String nameDocente) {
        NameDocente = nameDocente;
    }

    public String getDocumentoNombre() {
        return DocumentoNombre;
    }
    public void setDocumentoNombre(String documentoNombre) {
        DocumentoNombre = documentoNombre;
    }

    public String getConvocatoria() {
        return convocatoria;
    }
    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocenteId() {
        return docenteId;
    }
    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    // -------- ALIAS que pide el fragmento --------

    // el fragmento usa rev.nombreDocente
    public String getNombreDocente() {
        return NameDocente;
    }
    public void setNombreDocente(String nombreDocente) {
        this.NameDocente = nombreDocente;
    }

    // el fragmento usa rev.tienePdf
    public boolean isTienePdf() {
        return tienePdf;
    }
    public void setTienePdf(boolean tienePdf) {
        this.tienePdf = tienePdf;
    }

    // el fragmento usa rev.documentoEmitidoId
    public Long getDocumentoEmitidoId() {
        return documentoEmitidoId;
    }
    public void setDocumentoEmitidoId(Long documentoEmitidoId) {
        this.documentoEmitidoId = documentoEmitidoId;
    }

    // el fragmento usa rev.estadoSolicitud
    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }
    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    // alias para lo que pide el fragmento tabla-evaluar
    public Long getSolicitudId() {
        return id;
    }

    public void setSolicitudId(Long solicitudId) {
        this.id = solicitudId;
    }
}

