package br.com.financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movimentacao {

    public enum Tipo {
        CREDITO, DEBITO, TRANSFERENCIA_ENTRADA, TRANSFERENCIA_SAIDA;

        public String getLabel() {
            switch (this) {
                case CREDITO:               return "Crédito";
                case DEBITO:                return "Débito";
                case TRANSFERENCIA_ENTRADA: return "Transferência Recebida";
                case TRANSFERENCIA_SAIDA:   return "Transferência Enviada";
                default:                    return this.name();
            }
        }
    }

    private Long          id;
    private Long          contaId;
    private Tipo          tipo;
    private BigDecimal    valor;
    private String        descricao;
    private Long          contaOrigemId;
    private Long          contaDestinoId;
    private LocalDateTime realizadaEm;

    // Campos extras para exibição (não persistidos diretamente)
    private String numeroContaOrigem;
    private String numeroContaDestino;

    public Movimentacao() {}

    // ── Getters & Setters ──────────────────────────────────────

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public Long getContaId()                    { return contaId; }
    public void setContaId(Long contaId)        { this.contaId = contaId; }

    public Tipo getTipo()                       { return tipo; }
    public void setTipo(Tipo tipo)              { this.tipo = tipo; }

    public BigDecimal getValor()                { return valor; }
    public void setValor(BigDecimal valor)      { this.valor = valor; }

    public String getDescricao()                { return descricao; }
    public void setDescricao(String descricao)  { this.descricao = descricao; }

    public Long getContaOrigemId()              { return contaOrigemId; }
    public void setContaOrigemId(Long id)       { this.contaOrigemId = id; }

    public Long getContaDestinoId()             { return contaDestinoId; }
    public void setContaDestinoId(Long id)      { this.contaDestinoId = id; }

    public LocalDateTime getRealizadaEm()            { return realizadaEm; }
    public void setRealizadaEm(LocalDateTime r)      { this.realizadaEm = r; }

    public String getNumeroContaOrigem()             { return numeroContaOrigem; }
    public void setNumeroContaOrigem(String n)       { this.numeroContaOrigem = n; }

    public String getNumeroContaDestino()            { return numeroContaDestino; }
    public void setNumeroContaDestino(String n)      { this.numeroContaDestino = n; }
}
