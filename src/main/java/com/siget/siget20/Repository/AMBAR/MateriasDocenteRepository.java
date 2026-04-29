package com.siget.siget20.Repository.AMBAR;

import com.siget.siget20.Repository.AMBAR.Entity.MateriasDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MateriasDocenteRepository extends JpaRepository<MateriasDocente, Long> {
    List<MateriasDocente> findByDocente_DocenteId(Long docenteId);
}