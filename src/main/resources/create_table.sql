-- Script do enunciado da AT, com uma correção:
-- o original tinha uma vírgula depois da coluna "andamento", o que gera erro de sintaxe no MySQL.

CREATE DATABASE IF NOT EXISTS TarefasDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE TarefasDB;

CREATE TABLE IF NOT EXISTS Tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_tarefa VARCHAR(255) NOT NULL,
    data_vencimento DATE NOT NULL,
    prioridade ENUM('normal', 'alta', 'baixa') NOT NULL,
    andamento ENUM('pendente', 'em progresso', 'pronto') NOT NULL
);

-- Inserindo alguns dados de exemplo
INSERT INTO Tarefas (nome_tarefa, data_vencimento, prioridade, andamento) VALUES
('Finalizar relatório', '2026-09-01', 'alta', 'pendente'),
('Revisar código', '2026-08-30', 'normal', 'em progresso'),
('Enviar e-mail ao cliente', '2026-08-28', 'baixa', 'pronto');
