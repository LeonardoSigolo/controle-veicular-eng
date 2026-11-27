package com.fatec.controleveicular.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "manutencao")
@Schema(description = "Manutenção realizada em um veículo")
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador gerado automaticamente", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotNull(message = "O veículo é obrigatório")
    @Schema(description = "Id do veículo ao qual a manutenção pertence", example = "1")
    private Long veiculoId;

    @NotBlank(message = "O tipo é obrigatório")
    @Schema(example = "Troca de óleo")
    private String tipo;

    @Schema(example = "Óleo 5W30 sintético e filtro de óleo")
    private String descricao;

    @NotNull(message = "A data é obrigatória")
    @Schema(description = "Data da manutenção (AAAA-MM-DD)", example = "2026-10-15")
    private LocalDate data;

    @NotNull(message = "A quilometragem é obrigatória")
    @PositiveOrZero(message = "A quilometragem não pode ser negativa")
    @Schema(example = "55000")
    private Integer quilometragem;

    @NotNull(message = "O custo é obrigatório")
    @PositiveOrZero(message = "O custo não pode ser negativo")
    @Schema(description = "Custo total em reais", example = "320.90")
    private BigDecimal custo;

    @Schema(example = "Auto Center Silva")
    private String oficina;

    @Schema(description = "Data prevista para a próxima revisão (opcional, AAAA-MM-DD)", example = "2027-04-15")
    private LocalDate proximaRevisao;

    // Construtor padrão (exigido pelo JPA)
    public Manutencao() {
    }

    // Construtor com parâmetros
    public Manutencao(long id, Long veiculoId, String tipo, String descricao, LocalDate data,
                      Integer quilometragem, BigDecimal custo, String oficina, LocalDate proximaRevisao) {
        this.id = id;
        this.veiculoId = veiculoId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
        this.quilometragem = quilometragem;
        this.custo = custo;
        this.oficina = oficina;
        this.proximaRevisao = proximaRevisao;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }

    public String getOficina() { return oficina; }
    public void setOficina(String oficina) { this.oficina = oficina; }

    public LocalDate getProximaRevisao() { return proximaRevisao; }
    public void setProximaRevisao(LocalDate proximaRevisao) { this.proximaRevisao = proximaRevisao; }
}
