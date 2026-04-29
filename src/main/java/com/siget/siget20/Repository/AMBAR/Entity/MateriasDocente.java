package com.siget.siget20.Repository.AMBAR.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MATERIAS_DOCENTE")
public class MateriasDocente {

    @Id
    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(name = "periodo", nullable = false, length = 30)
    private String periodo;

    @Column(name = "nivel", nullable = false, length = 30)
    private String nivel;

    @Column(name = "clave", nullable = false, length = 30)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "alumnos", nullable = false)
    private Integer alumnos;

    // Getters y setters

    public Long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId (Long materiaId) {
        this.materiaId = materiaId;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAlumnos () {
        return alumnos;
    }

    public void setAlumnos (Integer alumnos) {
        this.alumnos = alumnos;
    }
}