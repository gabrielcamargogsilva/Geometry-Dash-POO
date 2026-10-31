package geometrydash.model;

/**
 * Representa os possíveis estados do fluxo do jogo.
 * 
 * Conceito de Java - Enum (Enumeração):
 * Um enum em Java é um tipo de dado especial que restringe uma variável
 * a um conjunto fixo de constantes pré-definidas.
 * 
 * Vantagens em POO:
 * 1. Type Safety (Segurança de Tipos): Impede valores inválidos (diferente de usar int ou String).
 * 2. Legibilidade: O código expressa com clareza o estado atual do jogo.
 */
public enum GameState {
    MENU,
    JOGANDO,
    PAUSADO,
    DERROTA,
    VITORIA
}

