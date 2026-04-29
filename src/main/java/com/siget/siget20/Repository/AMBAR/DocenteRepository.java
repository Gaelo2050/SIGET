package com.siget.siget20.Repository.AMBAR;


import com.siget.siget20.Repository.AMBAR.Entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
}