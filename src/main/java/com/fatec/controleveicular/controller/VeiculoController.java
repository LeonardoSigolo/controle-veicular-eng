package com.fatec.controleveicular.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.controleveicular.model.Veiculo;
import com.fatec.controleveicular.service.VeiculoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Cadastro e consulta dos veículos do usuário")
public class VeiculoController {

    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar um novo veículo",
               description = "Insere um veículo. O id é gerado automaticamente e a placa deve ser única.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos (campos obrigatórios, placa ou ano inválidos)"),
        @ApiResponse(responseCode = "409", description = "Já existe um veículo com a mesma placa")
    })
    @PostMapping
    public ResponseEntity<Veiculo> inserir(@Valid @RequestBody Veiculo veiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inserir(veiculo));
    }

    @Operation(summary = "Listar todos os veículos",
               description = "Retorna a lista de todos os veículos cadastrados (lista vazia se não houver nenhum).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar veículo por id",
               description = "Retorna os dados de um único veículo a partir do seu id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(
            @Parameter(description = "Id do veículo", example = "1") @PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar veículo por id",
               description = "Atualiza todas as informações de um veículo existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
        @ApiResponse(responseCode = "409", description = "Já existe outro veículo com a mesma placa")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(
            @Parameter(description = "Id do veículo", example = "1") @PathVariable long id,
            @Valid @RequestBody Veiculo veiculo) {
        return ResponseEntity.ok(service.atualizar(id, veiculo));
    }

    @Operation(summary = "Deletar veículo por id",
               description = "Remove o veículo e também todas as manutenções e documentos associados a ele.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Veículo removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do veículo", example = "1") @PathVariable long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
