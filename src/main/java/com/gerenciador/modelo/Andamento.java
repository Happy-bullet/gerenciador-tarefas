package com.gerenciador.modelo;

/**
 * Situações aceitas pela coluna ENUM('pendente', 'em progresso', 'pronto') da tabela Tarefas.
 * O valor gravado no banco fica em "valor"; o texto exibido na tela fica em "rotulo".
 */
public enum Andamento {

    PENDENTE("pendente", "Pendente"),
    EM_PROGRESSO("em progresso", "Em Progresso"),
    PRONTO("pronto", "Pronto");

    private final String valor;
    private final String rotulo;

    Andamento(String valor, String rotulo) {
        this.valor = valor;
        this.rotulo = rotulo;
    }

    public String getValor() {
        return valor;
    }

    public String getRotulo() {
        return rotulo;
    }

    /**
     * Converte o valor do banco (ou do formulário) para o enum
     * @param valor Texto exatamente como está no ENUM do banco
     * @return Andamento correspondente
     * @throws IllegalArgumentException se o valor não existir
     */
    public static Andamento deValor(String valor) {
        for (Andamento a : values()) {
            if (a.valor.equals(valor)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Andamento inválido: " + valor);
    }
}
