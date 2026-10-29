package com.gerenciador.modelo;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entidade que representa uma linha da tabela Tarefas do banco TarefasDB
 */
@Entity
@Table(name = "Tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_tarefa", nullable = false, length = 255)
    private String nome;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Convert(converter = PrioridadeConverter.class)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Convert(converter = AndamentoConverter.class)
    @Column(nullable = false)
    private Andamento andamento;

    public Tarefa() {
    }

    public Tarefa(String nome, LocalDate dataVencimento, Prioridade prioridade, Andamento andamento) {
        this.nome = nome;
        this.dataVencimento = dataVencimento;
        this.prioridade = prioridade;
        this.andamento = andamento;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Andamento getAndamento() {
        return andamento;
    }

    public void setAndamento(Andamento andamento) {
        this.andamento = andamento;
    }

    @Override
    public String toString() {
        return String.format("Tarefa(id=%d, nome=\"%s\", dataVencimento=%s, prioridade=%s, andamento=%s)",
                id,
                nome,
                dataVencimento,
                prioridade == null ? null : prioridade.getValor(),
                andamento == null ? null : andamento.getValor()
        );
    }
}
