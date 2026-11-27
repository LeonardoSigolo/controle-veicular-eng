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

import com.fatec.controleveicular.model.Documento;
import com.fatec.controleveicular.service.DocumentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/documentos")
@Tag(name = "Documentos", description = "Documentação dos veículos (CRLV, IPVA, seguro, licenciamento...)")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar um novo documento",
               description = "Insere um documento vinculado a um veículo já cadastrado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Documento cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou veículo inexistente")
    })
    @PostMapping
    public ResponseEntity<Documento> inserir(@Valid @RequestBody Documento documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inserir(documento));
    }

    @Operation(summary = "Listar documentos",
               description = "Retorna todos os documentos ordenados por data de vencimento. "
                       + "Use o parâmetro veiculoId para filtrar pelos documentos de um único veículo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Documento>> listar(
            @Parameter(description = "Filtra pelos documentos deste veículo (opcional)", example = "1")
            @RequestParam(required = false) Long veiculoId) {
        return ResponseEntity.ok(service.listar(veiculoId));
    }

    @Operation(summary = "Buscar documento por id",
               description = "Retorna os dados de um único documento a partir do seu id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento encontrado"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Documento> buscarPorId(
            @Parameter(description = "Id do documento", example = "1") @PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar documento por id",
               description = "Atualiza todas as informações de um documento existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou veículo inexistente"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Documento> atualizar(
            @Parameter(description = "Id do documento", example = "1") @PathVariable long id,
            @Valid @RequestBody Documento documento) {
        return ResponseEntity.ok(service.atualizar(id, documento));
    }

    @Operation(summary = "Deletar documento por id",
               description = "Remove definitivamente um documento.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Documento removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do documento", example = "1") @PathVariable long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
