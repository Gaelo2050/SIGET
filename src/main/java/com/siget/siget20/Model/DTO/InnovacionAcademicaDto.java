package com.siget.siget20.Model.DTO;

public class InnovacionAcademicaDto {

    private String asignatura;
    private String estrategia;
    private String programaEducativo;

    public InnovacionAcademicaDto() {
    }

    public InnovacionAcademicaDto(String asignatura,
                                  String estrategia,
                                  String programaEducativo) {
        this.asignatura = asignatura;
        this.estrategia = estrategia;
        this.programaEducativo = programaEducativo;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(String estrategia) {
        this.estrategia = estrategia;
    }

    public String getProgramaEducativo() {
        return programaEducativo;
    }

    public void setProgramaEducativo(String programaEducativo) {
        this.programaEducativo = programaEducativo;
    }
}
