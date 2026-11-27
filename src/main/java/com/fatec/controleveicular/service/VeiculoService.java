package com.fatec.controleveicular.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.controleveicular.exception.ApiException;
import com.fatec.controleveicular.model.Veiculo;
import com.fatec.controleveicular.repository.DocumentoRepository;
import com.fatec.controleveicular.repository.ManutencaoRepository;
import com.fatec.controleveicular.repository.VeiculoRepository;

@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final ManutencaoRepository manutencaoRepository;
    private final DocumentoRepository documentoRepository;

    // Injeção de dependência pelo construtor: o Spring entrega os repositories prontos
    public VeiculoService(VeiculoRepository repository,
                          ManutencaoRepository manutencaoRepository,
                          DocumentoRepository documentoRepository) {
        this.repository = repository;
        this.manutencaoRepository = manutencaoRepository;
        this.documentoRepository = documentoRepository;
    }

    public Veiculo inserir(Veiculo veiculo) {
        veiculo.setId(0); // garante que será um INSERT
        veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
        if (repository.existsByPlaca(veiculo.getPlaca())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Já existe um veículo cadastrado com a placa " + veiculo.getPlaca());
        }
        return repository.save(veiculo);
    }

    public List<Veiculo> listarTodos() {
        return repository.findAll();
    }

    public Veiculo buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Veículo com id " + id + " não encontrado"));
    }

    public Veiculo atualizar(long id, Veiculo dados) {
        Veiculo existente = buscarPorId(id);
        String placa = dados.getPlaca().toUpperCase();
        if (repository.existsByPlacaAndIdNot(placa, id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Já existe outro veículo cadastrado com a placa " + placa);
        }
        existente.setMarca(dados.getMarca());
        existente.setModelo(dados.getModelo());
        existente.setAno(dados.getAno());
        existente.setPlaca(placa);
        existente.setCor(dados.getCor());
        existente.setQuilometragem(dados.getQuilometragem());
        existente.setCombustivel(dados.getCombustivel());
        return repository.save(existente);
    }

    // Ao excluir um veículo, apaga também suas manutenções e documentos
    @Transactional
    public void deletar(long id) {
        buscarPorId(id);
        manutencaoRepository.deleteByVeiculoId(id);
        documentoRepository.deleteByVeiculoId(id);
        repository.deleteById(id);
    }
}
