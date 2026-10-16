package br.unitins.tp2.fincontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Usuario extends DefaultEntity {

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_fiscal", length = 10, nullable = false)
    private PerfilFiscal perfilFiscal;

    @Enumerated(EnumType.STRING)
    @Column(name = "atividade_mei", length = 10)
    private AtividadeMei atividadeMei;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PerfilFiscal getPerfilFiscal() {
        return perfilFiscal;
    }

    public void setPerfilFiscal(PerfilFiscal perfilFiscal) {
        this.perfilFiscal = perfilFiscal;
    }

    public AtividadeMei getAtividadeMei() {
        return atividadeMei;
    }

    public void setAtividadeMei(AtividadeMei atividadeMei) {
        this.atividadeMei = atividadeMei;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}