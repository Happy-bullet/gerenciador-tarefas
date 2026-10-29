package com.gerenciador.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Traduz o enum Prioridade para o texto do ENUM do MySQL e vice-versa.
 * Sem ele, o Hibernate gravaria "NORMAL" (nome da constante), que o banco rejeita.
 */
@Converter
public class PrioridadeConverter implements AttributeConverter<Prioridade, String> {

    @Override
    public String convertToDatabaseColumn(Prioridade prioridade) {
        return prioridade == null ? null : prioridade.getValor();
    }

    @Override
    public Prioridade convertToEntityAttribute(String valor) {
        return valor == null ? null : Prioridade.deValor(valor);
    }
}
