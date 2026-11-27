package com.fatec.controleveicular.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "veiculo")
@Schema(description = "Veículo cadastrado pelo usuário")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador gerado automaticamente", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotBlank(message = "A marca é obrigatória")
    @Schema(example = "Volkswagen")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")
    @Schema(example = "Gol 1.0")
    private String modelo;

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 1900, message = "Ano inválido")
    @Max(value = 2100, message = "Ano inválido")
    @Schema(example = "2018")
    private Integer ano;

    @NotBlank(message = "A placa é obrigatória")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$",
             message = "Placa inválida (use ABC1234 ou ABC1D23)")
    @Schema(example = "ABC1D23")
    private String placa;

    @NotBlank(message = "A cor é obrigatória")
    @Schema(example = "Prata")
    private String cor;

    @NotNull(message = "A quilometragem é obrigatória")
    @PositiveOrZero(message = "A quilometragem não pode ser negativa")
    @Schema(example = "56000")
    private Integer quilometragem;

    @NotBlank(message = "O combustível é obrigatório")
    @Schema(example = "Flex")
    private String combustivel;

    // Construtor padrão (exigido pelo JPA)
    public Veiculo() {
    }

    // Construtor com parâmetros
    public Veiculo(long id, String marca, String modelo, Integer ano, String placa,
                   String cor, Integer quilometragem, String combustivel) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.combustivel = combustivel;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public String getCombustivel() { return combustivel; }
    public void setCombustivel(String combustivel) { this.combustivel = combustivel; }
}
