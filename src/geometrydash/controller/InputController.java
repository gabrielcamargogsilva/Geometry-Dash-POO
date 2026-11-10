package geometrydash.controller;

import geometrydash.audio.AudioService;
import geometrydash.model.entity.GameState;
import geometrydash.model.service.GameModel;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;
import geometrydash.view.component.MenuButton;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Captura e interpreta entradas de teclado e mouse do jogador (Camada Controller).
 * 
 * Regra Fundamental:
 * - O clique em botões de menu ou de telas de resultado NUNCA dispara o pulo do personagem.
 * - O significado de cada entrada varia estritamente conforme o GameState ativo.
 */
public class InputController extends KeyAdapter {
    private final GameModel model;
    private final GamePanel view;
    private final AudioService audio;
    private final ProfileStore profileStore;
    private final MouseAdapter mouseAdapter;

    public InputController(GameModel model, GamePanel view, AudioService audio, ProfileStore profileStore) {
        this.model = model;
        this.view = view;
        this.audio = audio;
        this.profileStore = profileStore;
        this.mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tratarCliqueMouse(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                tratarMovimentoMouse(e.getX(), e.getY());
            }
        };
    }

    private void tratarCliqueMouse(int mouseX, int mouseY) {
        GameState estadoAtual = model.getEstadoAtual();

        // 1. Durante a partida, clique no cenário aciona o salto do personagem
        if (estadoAtual == GameState.JOGANDO) {
            if (model.pular()) {
                audio.tocarPulo();
            }
            view.repaint();
            return;
        }

        // 2. Nas demais telas, verifica se o clique acertou algum botão interativo
        boolean cliqueEmBotao = false;
        switch (estadoAtual) {
            case MENU:
                if (view.getBtnJogar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.iniciarPartida();
                } else if (view.getBtnControles().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.abrirControles();
                } else if (view.getBtnPersonalizar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.abrirPersonalizacao();
                } else if (view.getBtnCreditos().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.abrirCreditos();
                } else if (view.getBtnSair().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    System.exit(0);
                }
                break;

            case CONTROLES:
                if (view.getBtnVoltarControles().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            case CREDITOS:
                if (view.getBtnVoltarCreditos().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            case PERSONALIZACAO:
                if (view.getBtnPersonalizacaoCor().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.avancarCorPersonagem();
                    salvarPerfil();
                } else if (view.getBtnPersonalizacaoExpressao().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.avancarExpressaoPersonagem();
                    salvarPerfil();
                } else if (view.getBtnVoltarPersonalizacao().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            case PAUSADO:
                if (view.getBtnPausaContinuar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.alternarPausa();
                } else if (view.getBtnPausaReiniciar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.reiniciarPartida();
                } else if (view.getBtnPausaMenu().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            case DERROTA:
                if (view.getBtnDerrotaTentar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.reiniciarPartida();
                } else if (view.getBtnDerrotaMenu().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            case VITORIA:
                if (view.getBtnVitoriaJogar().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.reiniciarPartida();
                } else if (view.getBtnVitoriaMenu().contem(mouseX, mouseY)) {
                    cliqueEmBotao = true;
                    model.voltarAoMenu();
                }
                break;

            default:
                break;
        }

        if (cliqueEmBotao) {
            audio.tocarClique();
        }
        view.repaint();
    }

    private void salvarPerfil() {
        try {
            profileStore.salvar(model.getPerfil());
        } catch (IOException e) {
            System.err.println("Não foi possível salvar a personalização: " + e.getMessage());
        }
    }

    private void tratarMovimentoMouse(int mouseX, int mouseY) {
        boolean houveMudanca = false;

        for (MenuButton botao : view.getBotoesAtivos()) {
            boolean estavaComHover = botao.isHovered();
            boolean agoraComHover = botao.contem(mouseX, mouseY);

            if (estavaComHover != agoraComHover) {
                botao.setHovered(agoraComHover);
                houveMudanca = true;
            }
        }

        if (houveMudanca) {
            view.repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigoTecla = e.getKeyCode();
        GameState estadoAtual = model.getEstadoAtual();

        switch (codigoTecla) {
            case KeyEvent.VK_SPACE:
                if (estadoAtual == GameState.MENU || estadoAtual == GameState.DERROTA 
                        || estadoAtual == GameState.VITORIA) {
                    model.iniciarPartida();
                    view.repaint();
                } else if (estadoAtual == GameState.JOGANDO) {
                    if (model.pular()) {
                        audio.tocarPulo();
                    }
                    view.repaint();
                }
                break;

            case KeyEvent.VK_ESCAPE:
                if (estadoAtual == GameState.JOGANDO || estadoAtual == GameState.PAUSADO) {
                    model.alternarPausa();
                    view.repaint();
                } else if (estadoAtual == GameState.CONTROLES || estadoAtual == GameState.CREDITOS) {
                    model.voltarAoMenu();
                    view.repaint();
                } else if (estadoAtual == GameState.PERSONALIZACAO) {
                    model.voltarAoMenu();
                    view.repaint();
                }
                break;

            case KeyEvent.VK_M:
                if (estadoAtual == GameState.PAUSADO || estadoAtual == GameState.DERROTA 
                        || estadoAtual == GameState.VITORIA) {
                    model.voltarAoMenu();
                    view.repaint();
                }
                break;

            default:
                break;
        }
    }

    public MouseAdapter getMouseAdapter() {
        return mouseAdapter;
    }
}
