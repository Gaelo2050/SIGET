package com.siget.siget20.Model.DTO;

import java.math.BigDecimal;

public class TablaDesarrolloAcademicoDto {

    private Long docente_id;
    private String nombre;
    private String rfc;
    private String curp;
    private String correo;
    private String categoria;
    private String vigenciaDocente;
    private String estatusNombramiento;
    private BigDecimal porcentaje;
    private String exclusividad;
    private String sancion;
    private String observaciones;
    private String Validacion;

    public TablaDesarrolloAcademicoDto() {
    }

    public TablaDesarrolloAcademicoDto(Long docente_id,
                                       String nombre,
                                       String rfc,
                                       String curp,
                                       String correo,
                                       String categoria,
                                       String vigenciaDocente,
                                       String estatusNombramiento,
                                       BigDecimal porcentaje,
                                       String exclusividad,
                                       String sancion,
                                       String observaciones,
                                       String Validacion) {
        this.docente_id = docente_id;
        this.nombre = nombre;
        this.rfc = rfc;
        this.curp = curp;
        this.correo = correo;
        this.categoria = categoria;
        this.vigenciaDocente = vigenciaDocente;
        this.estatusNombramiento = estatusNombramiento;
        this.porcentaje = porcentaje;
        this.exclusividad = exclusividad;
        this.sancion = sancion;
        this.observaciones = observaciones;
        this.Validacion = Validacion;
    }

    public Long getDocente_id() {
        return docente_id;
    }

    public void setDocente_id(Long docente_id) {
        this.docente_id = docente_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getVigenciaDocente() {
        return vigenciaDocente;
    }

    public void setVigenciaDocente(String vigenciaDocente) {
        this.vigenciaDocente = vigenciaDocente;
    }

    public String getEstatusNombramiento() {
        return estatusNombramiento;
    }

    public void setEstatusNombramiento(String estatusNombramiento) {
        this.estatusNombramiento = estatusNombramiento;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getExclusividad() {
        return exclusividad;
    }

    public void setExclusividad(String exclusividad) {
        this.exclusividad = exclusividad;
    }

    public String getSancion() {
        return sancion;
    }

    public void setSancion(String sancion) {
        this.sancion = sancion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getValidacion() {
        return Validacion;
    }

    public void setValidacion(String validacion) {
        Validacion = validacion;
    }

    public String getFechaIng() {
        return vigenciaDocente;
    }
}
