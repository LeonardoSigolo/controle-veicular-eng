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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.controleveicular.model.Manutencao;
import com.fatec.controleveicular.service.ManutencaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/manutencoes")
@Tag(name = "Manutenções", description = "Histórico de manutenções dos veículos")
public class ManutencaoController {

    private final ManutencaoService service;

    public ManutencaoController(ManutencaoService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar uma nova manutenção",
               description = "Insere uma manutenção vinculada a um veículo já cadastrado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Manutenção registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou veículo inexistente")
    })
    @PostMapping
    public ResponseEntity<Manutencao> inserir(@Valid @RequestBody Manutencao manutencao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inserir(manutencao));
    }

    @Operation(summary = "Listar manutenções",
               description = "Retorna todas as manutenções, da mais recente para a mais antiga. "
                       + "Use o parâmetro veiculoId para filtrar pelas manutenções de um único veículo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Manutencao>> listar(
            @Parameter(description = "Filtra pelas manutenções deste veículo (opcional)", example = "1")
            @RequestParam(required = false) Long veiculoId) {
        return ResponseEntity.ok(service.listar(veiculoId));
    }

    @Operation(summary = "Buscar manutenção por id",
               description = "Retorna os dados de uma única manutenção a partir do seu id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Manutenção encontrada"),
        @ApiResponse(responseCode = "404", description = "Manutenção não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Manutencao> buscarPorId(
            @Parameter(description = "Id da manutenção", example = "1") @PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar manutenção por id",
               description = "Atualiza todas as informações de uma manutenção existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Manutenção atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou veículo inexistente"),
        @ApiResponse(responseCode = "404", description = "Manutenção não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Manutencao> atualizar(
            @Parameter(description = "Id da manutenção", example = "1") @PathVariable long id,
            @Valid @RequestBody Manutencao manutencao) {
        return ResponseEntity.ok(service.atualizar(id, manutencao));
    }

    @Operation(summary = "Deletar manutenção por id",
               description = "Remove definitivamente uma manutenção.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Manutenção removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Manutenção não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da manutenção", example = "1") @PathVariable long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
