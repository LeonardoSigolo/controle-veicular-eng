package com.fatec.controleveicular.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "documento")
@Schema(description = "Documento de um veículo (CRLV, IPVA, seguro, licenciamento etc.)")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador gerado automaticamente", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotNull(message = "O veículo é obrigatório")
    @Schema(description = "Id do veículo ao qual o documento pertence", example = "1")
    private Long veiculoId;

    @NotBlank(message = "O tipo é obrigatório")
    @Schema(example = "Seguro")
    private String tipo;

    @NotBlank(message = "O número é obrigatório")
    @Schema(description = "Número do documento ou da apólice", example = "AP-2026-889900")
    private String numero;

    @Schema(description = "Data de emissão (opcional, AAAA-MM-DD)", example = "2026-03-01")
    private LocalDate dataEmissao;

    @Schema(description = "Data de vencimento (opcional, AAAA-MM-DD)", example = "2027-03-01")
    private LocalDate dataVencimento;

    @Schema(example = "Seguro com cobertura total")
    private String observacao;

    // Construtor padrão (exigido pelo JPA)
    public Documento() {
    }

    // Construtor com parâmetros
    public Documento(long id, Long veiculoId, String tipo, String numero,
                     LocalDate dataEmissao, LocalDate dataVencimento, String observacao) {
        this.id = id;
        this.veiculoId = veiculoId;
        this.tipo = tipo;
        this.numero = numero;
        this.dataEmissao = dataEmissao;
        this.dataVencimento = dataVencimento;
        this.observacao = observacao;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
