package com.siget.siget20.Repository.SIGET;

import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoEmitidoRepository extends JpaRepository<DocumentoEmitido, Long> {

    List<DocumentoEmitido> findBySolicitudId(Long solicitudId);

    Optional<DocumentoEmitido> findByDocenteIdAndDocumentoId(Long docenteId, Long documentoId);

    List<DocumentoEmitido> findByDocenteId(Long docenteId);
}
