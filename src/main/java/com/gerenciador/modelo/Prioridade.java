package com.gerenciador.modelo;

/**
 * Prioridades aceitas pela coluna ENUM('normal', 'alta', 'baixa') da tabela Tarefas.
 * O valor gravado no banco fica em "valor"; o texto exibido na tela fica em "rotulo".
 */
public enum Prioridade {

    NORMAL("normal", "Normal"),
    ALTA("alta", "Alta"),
    BAIXA("baixa", "Baixa");

    private final String valor;
    private final String rotulo;

    Prioridade(String valor, String rotulo) {
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
     * @return Prioridade correspondente
     * @throws IllegalArgumentException se o valor não existir
     */
    public static Prioridade deValor(String valor) {
        for (Prioridade p : values()) {
            if (p.valor.equals(valor)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Prioridade inválida: " + valor);
    }
}
