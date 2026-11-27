package com.fatec.controleveicular.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fatec.controleveicular.exception.ApiException;
import com.fatec.controleveicular.model.Manutencao;
import com.fatec.controleveicular.repository.ManutencaoRepository;
import com.fatec.controleveicular.repository.VeiculoRepository;

@Service
public class ManutencaoService {

    private static final Sort MAIS_RECENTES_PRIMEIRO = Sort.by(Sort.Direction.DESC, "data");

    private final ManutencaoRepository repository;
    private final VeiculoRepository veiculoRepository;

    public ManutencaoService(ManutencaoRepository repository, VeiculoRepository veiculoRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
    }

    public Manutencao inserir(Manutencao manutencao) {
        validarVeiculo(manutencao.getVeiculoId());
        manutencao.setId(0); // garante que será um INSERT
        return repository.save(manutencao);
    }

    // Se veiculoId for informado, lista somente as manutenções daquele veículo
    public List<Manutencao> listar(Long veiculoId) {
        if (veiculoId == null) {
            return repository.findAll(MAIS_RECENTES_PRIMEIRO);
        }
        return repository.findByVeiculoId(veiculoId, MAIS_RECENTES_PRIMEIRO);
    }

    public Manutencao buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Manutenção com id " + id + " não encontrada"));
    }

    public Manutencao atualizar(long id, Manutencao dados) {
        Manutencao existente = buscarPorId(id);
        validarVeiculo(dados.getVeiculoId());
        existente.setVeiculoId(dados.getVeiculoId());
        existente.setTipo(dados.getTipo());
        existente.setDescricao(dados.getDescricao());
        existente.setData(dados.getData());
        existente.setQuilometragem(dados.getQuilometragem());
        existente.setCusto(dados.getCusto());
        existente.setOficina(dados.getOficina());
        existente.setProximaRevisao(dados.getProximaRevisao());
        return repository.save(existente);
    }

    public void deletar(long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private void validarVeiculo(Long veiculoId) {
        if (!veiculoRepository.existsById(veiculoId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Veículo com id " + veiculoId + " não existe");
        }
    }
}
