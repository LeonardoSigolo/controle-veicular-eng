package com.fatec.controleveicular.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.controleveicular.model.Veiculo;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    // O Spring cria a consulta automaticamente a partir do nome do método
    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, long id);
}
