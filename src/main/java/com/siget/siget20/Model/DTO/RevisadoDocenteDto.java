package com.siget.siget20.Model.DTO;

public class RevisadoDocenteDto {

    private Long solicitudId;
    private String areaDestino;
    private String documentoNombre;
    private String estado;             // APROBADO / RECHAZADO
    private boolean puedeDescargar;    // true si ya hay PDF
    private Long documentoEmitidoId;   // id para armar el link de descarga

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getAreaDestino() {
        return areaDestino;
    }

    public void setAreaDestino(String areaDestino) {
        this.areaDestino = areaDestino;
    }

    public String getDocumentoNombre() {
        return documentoNombre;
    }

    public void setDocumentoNombre(String documentoNombre) {
        this.documentoNombre = documentoNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isPuedeDescargar() {
        return puedeDescargar;
    }

    public void setPuedeDescargar(boolean puedeDescargar) {
        this.puedeDescargar = puedeDescargar;
    }

    public Long getDocumentoEmitidoId() {
        return documentoEmitidoId;
    }

    public void setDocumentoEmitidoId(Long documentoEmitidoId) {
        this.documentoEmitidoId = documentoEmitidoId;
    }
}
