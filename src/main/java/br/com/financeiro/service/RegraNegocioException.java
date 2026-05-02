package br.com.financeiro.service;

/** Exceção lançada quando uma regra de negócio é violada. */
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
