package com.fatec.controleveicular.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.controleveicular.model.Manutencao;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    List<Manutencao> findByVeiculoId(Long veiculoId, Sort sort);

    void deleteByVeiculoId(Long veiculoId);
}
