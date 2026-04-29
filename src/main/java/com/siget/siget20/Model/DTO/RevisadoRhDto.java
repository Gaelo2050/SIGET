package com.siget.siget20.Model.DTO;

public class RevisadoRhDto {

    private Long documentoEmitidoId;   // puede ser null si no hay PDF
    private Long solicitudId;
    private Long docenteId;

    private String nombreDocente;
    private String documentoNombre;
    private String convocatoria;

    private String estadoSolicitud;    // APROBADO / RECHAZADO, etc.
    private String estadoDocumento;    // GENERADO / FIRMADO / null

    private boolean tienePdf;          // true si hay contenido_html

    public Long getDocumentoEmitidoId() {
        return documentoEmitidoId;
    }

    public void setDocumentoEmitidoId(Long documentoEmitidoId) {
        this.documentoEmitidoId = documentoEmitidoId;
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public String getDocumentoNombre() {
        return documentoNombre;
    }

    public void setDocumentoNombre(String documentoNombre) {
        this.documentoNombre = documentoNombre;
    }

    public String getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public boolean isTienePdf() {
        return tienePdf;
    }

    public void setTienePdf(boolean tienePdf) {
        this.tienePdf = tienePdf;
    }
}
