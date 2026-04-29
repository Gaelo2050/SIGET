package com.siget.siget20.Repository.SIGET.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_emitido")
public class DocumentoEmitido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documento_emitido_id")
    private Long documentoEmitidoId;

    @Column(name = "solicitud_id", nullable = true)
    private Long solicitudId;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(name = "documento_id", nullable = false)
    private Long documentoId;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "firmado_area", nullable = false)
    private boolean firmadoArea;

    @Column(name = "usuario_firma_id")
    private Long usuarioFirmaId;

    @Column(name = "fecha_firma_area")
    private LocalDateTime fechaFirmaArea;

    @Column(name = "firma_base64")
    private String firmaBase64;

    @Column(name = "folio")
    private String folio;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;


    @Lob
    @Column(name = "contenido_html")
    private String contenidoHtml;



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

    public Long getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Long documentoId) {
        this.documentoId = documentoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isFirmadoArea() {
        return firmadoArea;
    }

    public void setFirmadoArea(boolean firmadoArea) {
        this.firmadoArea = firmadoArea;
    }

    public Long getUsuarioFirmaId() {
        return usuarioFirmaId;
    }

    public void setUsuarioFirmaId(Long usuarioFirmaId) {
        this.usuarioFirmaId = usuarioFirmaId;
    }

    public LocalDateTime getFechaFirmaArea() {
        return fechaFirmaArea;
    }

    public void setFechaFirmaArea(LocalDateTime fechaFirmaArea) {
        this.fechaFirmaArea = fechaFirmaArea;
    }

    public String getFirmaBase64() {
        return firmaBase64;
    }

    public void setFirmaBase64(String firmaBase64) {
        this.firmaBase64 = firmaBase64;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getContenidoHtml() {
        return contenidoHtml;
    }

    public void setContenidoHtml(String contenidoHtml) {
        this.contenidoHtml = contenidoHtml;
    }
}
