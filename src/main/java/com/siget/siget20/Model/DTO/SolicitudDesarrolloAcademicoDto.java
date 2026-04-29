package com.siget.siget20.Model.DTO;

public class SolicitudDesarrolloAcademicoDto {
    private Long id;
    private String NameDocente;
    private String DocumentoNombre;
    private String convocatoria;

    public SolicitudDesarrolloAcademicoDto() {}

    public SolicitudDesarrolloAcademicoDto(Long id, String nameDocente, String documentoNombre, String convocatoria) {
        this.NameDocente = nameDocente;
        this.DocumentoNombre = documentoNombre;
        this.convocatoria = convocatoria;
        this.id = id;
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
}
