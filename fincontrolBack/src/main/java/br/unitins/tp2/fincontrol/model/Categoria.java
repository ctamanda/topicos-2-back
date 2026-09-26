package br.unitins.tp2.fincontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Categoria extends DefaultEntity {

    @Column(length = 60, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private TipoCategoria tipo;

    @Column(length = 20)
    private String cor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
}
