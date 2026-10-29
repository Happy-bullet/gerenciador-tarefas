package com.gerenciador.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Traduz o enum Andamento para o texto do ENUM do MySQL e vice-versa.
 * Necessário porque "em progresso" tem espaço e não pode ser nome de constante Java.
 */
@Converter
public class AndamentoConverter implements AttributeConverter<Andamento, String> {

    @Override
    public String convertToDatabaseColumn(Andamento andamento) {
        return andamento == null ? null : andamento.getValor();
    }

    @Override
    public Andamento convertToEntityAttribute(String valor) {
        return valor == null ? null : Andamento.deValor(valor);
    }
}
