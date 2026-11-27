package com.fatec.controleveicular.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fatec.controleveicular.exception.ApiException;
import com.fatec.controleveicular.model.Documento;
import com.fatec.controleveicular.repository.DocumentoRepository;
import com.fatec.controleveicular.repository.VeiculoRepository;

@Service
public class DocumentoService {

    private static final Sort POR_VENCIMENTO = Sort.by(Sort.Direction.ASC, "dataVencimento");

    private final DocumentoRepository repository;
    private final VeiculoRepository veiculoRepository;

    public DocumentoService(DocumentoRepository repository, VeiculoRepository veiculoRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
    }

    public Documento inserir(Documento documento) {
        validarVeiculo(documento.getVeiculoId());
        documento.setId(0); // garante que será um INSERT
        return repository.save(documento);
    }

    // Se veiculoId for informado, lista somente os documentos daquele veículo
    public List<Documento> listar(Long veiculoId) {
        if (veiculoId == null) {
            return repository.findAll(POR_VENCIMENTO);
        }
        return repository.findByVeiculoId(veiculoId, POR_VENCIMENTO);
    }

    public Documento buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Documento com id " + id + " não encontrado"));
    }

    public Documento atualizar(long id, Documento dados) {
        Documento existente = buscarPorId(id);
        validarVeiculo(dados.getVeiculoId());
        existente.setVeiculoId(dados.getVeiculoId());
        existente.setTipo(dados.getTipo());
        existente.setNumero(dados.getNumero());
        existente.setDataEmissao(dados.getDataEmissao());
        existente.setDataVencimento(dados.getDataVencimento());
        existente.setObservacao(dados.getObservacao());
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
