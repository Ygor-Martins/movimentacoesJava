package br.com.financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Conta {

    public enum Tipo { CORRENTE, POUPANCA, INVESTIMENTO }

    private Long          id;
    private String        numero;
    private String        titular;
    private Tipo          tipo;
    private BigDecimal    saldo;
    private boolean       ativa;
    private LocalDateTime criadaEm;

    public Conta() {}

    // ── Getters & Setters ──────────────────────────────────────

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getNumero()              { return numero; }
    public void setNumero(String numero)   { this.numero = numero; }

    public String getTitular()             { return titular; }
    public void setTitular(String t)       { this.titular = t; }

    public Tipo getTipo()                  { return tipo; }
    public void setTipo(Tipo tipo)         { this.tipo = tipo; }

    public BigDecimal getSaldo()           { return saldo; }
    public void setSaldo(BigDecimal s)     { this.saldo = s; }

    public boolean isAtiva()               { return ativa; }
    public void setAtiva(boolean ativa)    { this.ativa = ativa; }

    public LocalDateTime getCriadaEm()           { return criadaEm; }
    public void setCriadaEm(LocalDateTime c)     { this.criadaEm = c; }
}
