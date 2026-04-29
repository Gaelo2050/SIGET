package com.siget.siget20.Model.DTO;

public class TablaDocenteDto {
    private Long docenteId;
    private String nombre;
    private String area;
    private String documento;

    public TablaDocenteDto() {}

    public TablaDocenteDto(Long docenteId, String nombre, String area, String documento) {
        this.docenteId = docenteId;
        this.nombre = nombre;
        this.area = area;
        this.documento = documento;
    }


    public Long getDocenteId() {
        return docenteId;
    }
    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
