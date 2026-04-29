package com.siget.siget20.Model.DTO;

public class ConstanciaLaboralDocenteView {

    private String nombreCompleto;
    private String rfc;
    private String puesto;
    private String departamento;
    private String fechaIngreso;
    private String estatus;

    public ConstanciaLaboralDocenteView() {}

    public ConstanciaLaboralDocenteView(String nombreCompleto,
                                        String rfc,
                                        String puesto,
                                        String departamento,
                                        String fechaIngreso,
                                        String estatus) {
        this.nombreCompleto = nombreCompleto;
        this.rfc = rfc;
        this.puesto = puesto;
        this.departamento = departamento;
        this.fechaIngreso = fechaIngreso;
        this.estatus = estatus;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
