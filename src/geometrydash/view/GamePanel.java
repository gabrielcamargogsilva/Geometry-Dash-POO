package geometrydash.view;

import geometrydash.model.entity.GameState;
import geometrydash.model.entity.Obstacle;
import geometrydash.model.entity.PersonagemExpressao;
import geometrydash.model.service.GameModel;
import geometrydash.view.component.MenuButton;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Área de desenho e apresentação do jogo (Camada View).
 * 
 * Conceitos de POO e Swing:
 * 1. Herança: 'extends JPanel' especializa um painel genérico do Swing
 *    para se tornar o nosso quadro de pintura exclusivo do jogo.
 * 2. Polimorfismo e Sobrescrita (@Override): Substituímos o comportamento de
 *    'paintComponent' padrão do Swing pela nossa própria lógica de renderização com Graphics2D.
 * 3. Composição: O GamePanel agrega objetos MenuButton para construir telas interativas.
 * 4. Associação com o Model: O GamePanel consulta o GameModel para saber
 *    o que desenhar, mas NUNCA altera o estado do Model durante a pintura.
 */
public class GamePanel extends JPanel {
    public static final int LARGURA = 800;
    public static final int ALTURA = 600;

    private static final int POSICAO_CHAO_Y = (int) GameModel.POSICAO_CHAO_Y;
    private static final int POSICAO_PERSONAGEM_X = 120;
    private static final int TAMANHO_PERSONAGEM = (int) GameModel.TAMANHO_PERSONAGEM;
    private static final Color COR_FUNDO_SUPERIOR = new Color(17, 24, 43);
    private static final Color COR_FUNDO_INFERIOR = new Color(38, 34, 65);
    private static final Color COR_CHAO = new Color(29, 38, 57);

    private final GameModel model;

    // Botões das Telas do Menu
    private final MenuButton btnJogar;
    private final MenuButton btnPersonalizar;
    private final MenuButton btnControles;
    private final MenuButton btnCreditos;
    private final MenuButton btnSair;

    // Botão das Telas Secundárias
    private final MenuButton btnVoltarControles;
    private final MenuButton btnVoltarCreditos;
    private final MenuButton btnPersonalizacaoCor;
    private final MenuButton btnPersonalizacaoExpressao;
    private final MenuButton btnVoltarPersonalizacao;

    // Botões da Tela de Pausa
    private final MenuButton btnPausaContinuar;
    private final MenuButton btnPausaReiniciar;
    private final MenuButton btnPausaMenu;

    // Botões da Tela de Derrota
    private final MenuButton btnDerrotaTentar;
    private final MenuButton btnDerrotaMenu;

    // Botões da Tela de Vitória
    private final MenuButton btnVitoriaJogar;
    private final MenuButton btnVitoriaMenu;

    public GamePanel(GameModel model) {
        this.model = model;

        // Inicialização dos botões
        this.btnJogar = new MenuButton(300, 205, 200, 42, "Jogar");
        this.btnPersonalizar = new MenuButton(300, 255, 200, 42, "Personalizar");
        this.btnControles = new MenuButton(300, 305, 200, 42, "Controles");
        this.btnCreditos = new MenuButton(300, 355, 200, 42, "Créditos");
        this.btnSair = new MenuButton(300, 405, 200, 42, "Sair");

        this.btnVoltarControles = new MenuButton(300, 440, 200, 42, "Voltar ao Menu");
        this.btnVoltarCreditos = new MenuButton(300, 440, 200, 42, "Voltar ao Menu");
        this.btnPersonalizacaoCor = new MenuButton(300, 350, 200, 42, "Próxima Cor");
        this.btnPersonalizacaoExpressao = new MenuButton(300, 405, 200, 42, "Próxima Expressão");
        this.btnVoltarPersonalizacao = new MenuButton(300, 460, 200, 42, "Voltar ao Menu");

        this.btnPausaContinuar = new MenuButton(300, 240, 200, 42, "Continuar");
        this.btnPausaReiniciar = new MenuButton(300, 295, 200, 42, "Reiniciar Fase");
        this.btnPausaMenu = new MenuButton(300, 350, 200, 42, "Menu Principal");

        this.btnDerrotaTentar = new MenuButton(300, 290, 200, 42, "Tentar Novamente");
        this.btnDerrotaMenu = new MenuButton(300, 345, 200, 42, "Menu Principal");

        this.btnVitoriaJogar = new MenuButton(300, 290, 200, 42, "Jogar Novamente");
        this.btnVitoriaMenu = new MenuButton(300, 345, 200, 42, "Menu Principal");

        configurarPainel();
    }

    private void configurarPainel() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(24, 24, 32));
        setFocusable(true);
    }

    /**
     * Retorna a lista de botões ativos correspondentes à tela exibida no momento.
     */
    public List<MenuButton> getBotoesAtivos() {
        GameState estado = model.getEstadoAtual();
        List<MenuButton> ativos = new ArrayList<>();

        switch (estado) {
            case MENU:
                ativos.add(btnJogar);
                ativos.add(btnPersonalizar);
                ativos.add(btnControles);
                ativos.add(btnCreditos);
                ativos.add(btnSair);
                break;
            case CONTROLES:
                ativos.add(btnVoltarControles);
                break;
            case CREDITOS:
                ativos.add(btnVoltarCreditos);
                break;
            case PERSONALIZACAO:
                ativos.add(btnPersonalizacaoCor);
                ativos.add(btnPersonalizacaoExpressao);
                ativos.add(btnVoltarPersonalizacao);
                break;
            case PAUSADO:
                ativos.add(btnPausaContinuar);
                ativos.add(btnPausaReiniciar);
                ativos.add(btnPausaMenu);
                break;
            case DERROTA:
                ativos.add(btnDerrotaTentar);
                ativos.add(btnDerrotaMenu);
                break;
            case VITORIA:
                ativos.add(btnVitoriaJogar);
                ativos.add(btnVitoriaMenu);
                break;
            default:
                break;
        }

        return Collections.unmodifiableList(ativos);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharCenarioBase(g2d);
        desenharObstaculos(g2d);
        desenharPersonagem(g2d);
        desenharInterfacePorEstado(g2d);
    }

    private void desenharObstaculos(Graphics2D g2d) {
        for (Obstacle obstaculo : model.getObstaculos()) {
            int x = (int) Math.round(obstaculo.getX());
            int y = (int) Math.round(obstaculo.getY());
            int largura = (int) Math.round(obstaculo.getLargura());
            int altura = (int) Math.round(obstaculo.getAltura());

            if (obstaculo.getTipo() == Obstacle.Tipo.BLOCO) {
                g2d.setColor(new Color(163, 88, 72));
                g2d.fillRect(x, y, largura, altura);
                g2d.setColor(new Color(230, 160, 95));
                g2d.drawRect(x, y, largura - 1, altura - 1);
            } else {
                int[] pontosX = {x, x + largura / 2, x + largura};
                int[] pontosY = {y + altura, y, y + altura};
                g2d.setColor(new Color(220, 86, 74));
                g2d.fillPolygon(pontosX, pontosY, 3);
                g2d.setColor(new Color(255, 190, 170));
                g2d.drawLine(x, y + altura, x + largura / 2, y);
                g2d.drawLine(x + largura / 2, y, x + largura, y + altura);
            }
        }
    }

    private void desenharCenarioBase(Graphics2D g2d) {
        g2d.setPaint(new GradientPaint(
                0, 0, COR_FUNDO_SUPERIOR,
                0, POSICAO_CHAO_Y, COR_FUNDO_INFERIOR));
        g2d.fillRect(0, 0, LARGURA, POSICAO_CHAO_Y);

        g2d.setColor(new Color(105, 117, 160, 35));
        for (int y = 40; y < POSICAO_CHAO_Y; y += 40) {
            g2d.drawLine(0, y, LARGURA, y);
        }

        g2d.setColor(COR_CHAO);
        g2d.fillRect(0, POSICAO_CHAO_Y, LARGURA, ALTURA - POSICAO_CHAO_Y);

        g2d.setColor(new Color(75, 93, 125, 90));
        double deslocamentoCenario = model.getDeslocamentoCenario();
        for (double x = -deslocamentoCenario; x < LARGURA; x += GameModel.TAMANHO_CICLO_CENARIO) {
            int posicaoX = (int) Math.round(x);
            g2d.drawLine(posicaoX, POSICAO_CHAO_Y, posicaoX, ALTURA);
        }
        for (int y = POSICAO_CHAO_Y + 40; y < ALTURA; y += 40) {
            g2d.drawLine(0, y, LARGURA, y);
        }

        g2d.setColor(new Color(91, 244, 224));
        g2d.fillRect(0, POSICAO_CHAO_Y - 4, LARGURA, 4);
    }

    private void desenharPersonagem(Graphics2D g2d) {
        int posicaoY = (int) Math.round(model.getPosicaoVerticalPersonagem());
        g2d.setColor(new Color(0, 0, 0, 55));
        g2d.fillOval(POSICAO_PERSONAGEM_X - 4, POSICAO_CHAO_Y - 2,
                TAMANHO_PERSONAGEM + 8, 8);

        Graphics2D graphicsPersonagem = (Graphics2D) g2d.create();
        graphicsPersonagem.rotate(model.getRotacaoPersonagem(),
                POSICAO_PERSONAGEM_X + TAMANHO_PERSONAGEM / 2.0,
                posicaoY + TAMANHO_PERSONAGEM / 2.0);

        graphicsPersonagem.setColor(new Color(model.getPersonagemCor().getRgb()));
        graphicsPersonagem.fillRect(POSICAO_PERSONAGEM_X, posicaoY, TAMANHO_PERSONAGEM, TAMANHO_PERSONAGEM);

        graphicsPersonagem.setColor(new Color(220, 255, 250));
        graphicsPersonagem.drawRect(POSICAO_PERSONAGEM_X, posicaoY,
                TAMANHO_PERSONAGEM - 1, TAMANHO_PERSONAGEM - 1);

        desenharExpressao(graphicsPersonagem, POSICAO_PERSONAGEM_X, posicaoY,
                TAMANHO_PERSONAGEM, model.getPersonagemExpressao());
        graphicsPersonagem.dispose();
    }

    private void desenharExpressao(
            Graphics2D g2d, int x, int y, int tamanho, PersonagemExpressao expressao) {
        g2d.setColor(new Color(19, 38, 55));
        int olhoLargura = Math.max(4, tamanho / 8);
        int olhoAltura = Math.max(5, tamanho / 6);
        int olhoEsquerdoX = x + tamanho * 9 / 40;
        int olhoDireitoX = x + tamanho * 26 / 40;
        int olhoY = y + tamanho * 11 / 40;

        if (expressao == PersonagemExpressao.FELIZ) {
            g2d.fillOval(olhoEsquerdoX, olhoY, olhoLargura, olhoAltura);
            g2d.fillOval(olhoDireitoX, olhoY, olhoLargura, olhoAltura);
            g2d.drawArc(x + tamanho * 14 / 40, y + tamanho * 22 / 40,
                    tamanho * 12 / 40, tamanho * 10 / 40, 180, 180);
        } else if (expressao == PersonagemExpressao.SERIA) {
            g2d.fillRect(olhoEsquerdoX, olhoY + 2, olhoLargura + 1, Math.max(3, olhoAltura / 2));
            g2d.fillRect(olhoDireitoX, olhoY + 2, olhoLargura + 1, Math.max(3, olhoAltura / 2));
            g2d.drawLine(x + tamanho * 14 / 40, y + tamanho * 31 / 40,
                    x + tamanho * 27 / 40, y + tamanho * 28 / 40);
        } else {
            g2d.fillRect(olhoEsquerdoX, olhoY, olhoLargura, olhoAltura);
            g2d.fillRect(olhoDireitoX, olhoY, olhoLargura, olhoAltura);
            g2d.fillRect(x + tamanho * 14 / 40, y + tamanho * 27 / 40,
                    tamanho * 12 / 40, Math.max(3, tamanho / 10));
        }
    }

    private void desenharInterfacePorEstado(Graphics2D g2d) {
        GameState estado = model.getEstadoAtual();

        switch (estado) {
            case MENU:
                desenharMenuPrincipal(g2d);
                break;

            case CONTROLES:
                desenharTelaControles(g2d);
                break;

            case CREDITOS:
                desenharTelaCreditos(g2d);
                break;

            case PERSONALIZACAO:
                desenharTelaPersonalizacao(g2d);
                break;

            case JOGANDO:
                desenharInterfaceJogando(g2d);
                break;

            case PAUSADO:
                desenharTelaPausa(g2d);
                break;

            case DERROTA:
                desenharTelaDerrota(g2d);
                break;

            case VITORIA:
                desenharTelaVitoria(g2d);
                break;

            default:
                break;
        }
    }

    private void desenharMenuPrincipal(Graphics2D g2d) {
        // Título estilizado
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2d.setColor(new Color(0, 220, 255));
        desenharTextoCentralizado(g2d, "GEOMETRY DASH POO", 150);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2d.setColor(new Color(180, 200, 225));
        desenharTextoCentralizado(g2d, "Versão Acadêmica — SENAI POO", 185);

        // Renderiza os botões
        btnJogar.desenhar(g2d);
        btnPersonalizar.desenhar(g2d);
        btnControles.desenhar(g2d);
        btnCreditos.desenhar(g2d);
        btnSair.desenhar(g2d);
    }

    private void desenharTelaControles(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(10, 15, 28, 220));

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2d.setColor(new Color(0, 220, 255));
        desenharTextoCentralizado(g2d, "CONTROLES E INSTRUÇÕES", 120);

        // Quadro de instruções
        g2d.setColor(new Color(25, 35, 55, 180));
        g2d.fillRoundRect(180, 160, 440, 240, 16, 16);
        g2d.setColor(new Color(91, 244, 224, 120));
        g2d.drawRoundRect(180, 160, 440, 240, 16, 16);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("• ESPAÇO ou CLIQUE DO MOUSE:", 210, 205);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2d.setColor(new Color(190, 230, 255));
        g2d.drawString("Pula para desviar dos obstáculos", 230, 230);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("• ESC:", 210, 275);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2d.setColor(new Color(190, 230, 255));
        g2d.drawString("Pausa e retoma a partida", 230, 300);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("• OBJETIVO:", 210, 345);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2d.setColor(new Color(190, 230, 255));
        g2d.drawString("Sobreviva a todos os obstáculos até o fim da fase!", 230, 370);

        btnVoltarControles.desenhar(g2d);
    }

    private void desenharTelaCreditos(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(10, 15, 28, 220));

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2d.setColor(new Color(0, 220, 255));
        desenharTextoCentralizado(g2d, "CRÉDITOS DO PROJETO", 120);

        // Quadro de créditos
        g2d.setColor(new Color(25, 35, 55, 180));
        g2d.fillRoundRect(180, 160, 440, 240, 16, 16);
        g2d.setColor(new Color(91, 244, 224, 120));
        g2d.drawRoundRect(180, 160, 440, 240, 16, 16);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        desenharTextoCentralizado(g2d, "Geometry Dash POO", 205);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2d.setColor(new Color(190, 220, 245));
        desenharTextoCentralizado(g2d, "Curso: Back End / SENAI 2026 (2º Semestre)", 245);
        desenharTextoCentralizado(g2d, "Linguagem: Java 17 | Interface: Swing & Graphics2D", 280);
        desenharTextoCentralizado(g2d, "Padrão Arquitetural: Model-View-Controller (MVC)", 315);
        desenharTextoCentralizado(g2d, "Identidade Geométrica 2D Autoral", 350);
        desenharTextoCentralizado(g2d, "Desenvolvedores: Gabriel Camargo Gonçalves Silva e Guilherme Leite", 385);

        btnVoltarCreditos.desenhar(g2d);
    }

    private void desenharTelaPersonalizacao(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(10, 15, 28, 220));

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g2d.setColor(new Color(0, 220, 255));
        desenharTextoCentralizado(g2d, "PERSONALIZAR PERSONAGEM", 105);

        int tamanho = 100;
        int x = (LARGURA - tamanho) / 2;
        int y = 145;
        g2d.setColor(new Color(model.getPersonagemCor().getRgb()));
        g2d.fillRoundRect(x, y, tamanho, tamanho, 12, 12);
        g2d.setColor(new Color(220, 255, 250));
        g2d.drawRoundRect(x, y, tamanho - 1, tamanho - 1, 12, 12);
        desenharExpressao(g2d, x, y, tamanho, model.getPersonagemExpressao());

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g2d.setColor(Color.WHITE);
        desenharTextoCentralizado(g2d, "Cor: " + model.getPersonagemCor().getNomeExibicao(), 280);
        desenharTextoCentralizado(g2d,
                "Expressão: " + model.getPersonagemExpressao().getNomeExibicao(), 315);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2d.setColor(new Color(190, 220, 245));
        desenharTextoCentralizado(g2d,
                String.format("Seu recorde: %.0f%%", model.getRecordePercentual()), 338);

        btnPersonalizacaoCor.desenhar(g2d);
        btnPersonalizacaoExpressao.desenhar(g2d);
        btnVoltarPersonalizacao.desenhar(g2d);
    }

    private void desenharInterfaceJogando(Graphics2D g2d) {
        // Indicador de tempo no canto superior esquerdo
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2d.setColor(new Color(220, 240, 255));
        String textoTempo = String.format("Tempo: %.2f s", model.getTempoDecorrido());
        g2d.drawString(textoTempo, 20, 30);
        desenharIndicadorProgresso(g2d);

        // Dica inicial curta (desaparece após 3 segundos)
        if (model.isExibindoDica()) {
            desenharDicaInicial(g2d);
        }
    }

    private void desenharIndicadorProgresso(Graphics2D g2d) {
        int x = 280;
        int y = 18;
        int largura = 240;
        int altura = 14;
        int larguraPreenchida = (int) Math.round((largura - 2) * model.getProgressoPercentual() / 100.0);

        g2d.setColor(new Color(20, 30, 50, 210));
        g2d.fillRoundRect(x, y, largura, altura, 10, 10);
        if (larguraPreenchida > 0) {
            g2d.setColor(new Color(91, 244, 224));
            g2d.fillRoundRect(x + 1, y + 1, larguraPreenchida, altura - 2, 8, 8);
        }
        g2d.setColor(new Color(180, 220, 235));
        g2d.drawRoundRect(x, y, largura, altura, 10, 10);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(new Color(220, 240, 255));
        g2d.drawString(String.format("%.0f%%", model.getProgressoPercentual()), 535, 30);
    }

    private void desenharDicaInicial(Graphics2D g2d) {
        g2d.setColor(new Color(20, 30, 50, 190));
        g2d.fillRoundRect(220, 50, 360, 38, 12, 12);
        g2d.setColor(new Color(91, 244, 224, 180));
        g2d.drawRoundRect(220, 50, 360, 38, 12, 12);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        desenharTextoCentralizado(g2d, "Dica: Pressione ESPAÇO ou CLIQUE para pular!", 74);
    }

    private void desenharTelaPausa(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(0, 0, 0, 180));
        desenharIndicadorProgresso(g2d);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g2d.setColor(new Color(255, 200, 0));
        desenharTextoCentralizado(g2d, "JOGO PAUSADO", 160);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2d.setColor(Color.WHITE);
        desenharTextoCentralizado(g2d, String.format("Tempo congelado: %.2f s", model.getTempoDecorrido()), 195);

        btnPausaContinuar.desenhar(g2d);
        btnPausaReiniciar.desenhar(g2d);
        btnPausaMenu.desenhar(g2d);
    }

    private void desenharTelaDerrota(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(40, 10, 15, 210));

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2d.setColor(new Color(255, 90, 80));
        desenharTextoCentralizado(g2d, "COLISÃO! FIM DE JOGO", 180);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(new Color(240, 200, 200));
        desenharTextoCentralizado(g2d, String.format("Tempo alcançado nesta tentativa: %.2f s", model.getTempoDecorrido()), 225);
        desenharTextoCentralizado(g2d, String.format("Progresso: %.0f%%", model.getProgressoPercentual()), 255);
        desenharTextoCentralizado(g2d, String.format("Recorde pessoal: %.0f%%", model.getRecordePercentual()), 280);

        btnDerrotaTentar.desenhar(g2d);
        btnDerrotaMenu.desenhar(g2d);
    }

    private void desenharTelaVitoria(Graphics2D g2d) {
        desenharOverlayEscuro(g2d, new Color(10, 35, 25, 210));

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2d.setColor(new Color(80, 255, 160));
        desenharTextoCentralizado(g2d, "VITÓRIA! FASE CONCLUÍDA!", 180);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(new Color(200, 245, 220));
        desenharTextoCentralizado(g2d, String.format("Tempo total da tentativa: %.2f s", model.getTempoDecorrido()), 225);
        desenharTextoCentralizado(g2d, String.format("Progresso: %.0f%%", model.getProgressoPercentual()), 255);
        desenharTextoCentralizado(g2d, String.format("Recorde pessoal: %.0f%%", model.getRecordePercentual()), 280);

        btnVitoriaJogar.desenhar(g2d);
        btnVitoriaMenu.desenhar(g2d);
    }

    private void desenharOverlayEscuro(Graphics2D g2d, Color cor) {
        g2d.setColor(cor);
        g2d.fillRect(0, 0, LARGURA, ALTURA);
    }

    private void desenharTextoCentralizado(Graphics2D g2d, String texto, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (LARGURA - fm.stringWidth(texto)) / 2;
        g2d.drawString(texto, x, y);
    }

    // Getters dos botões para o Controller
    public MenuButton getBtnJogar() { return btnJogar; }
    public MenuButton getBtnPersonalizar() { return btnPersonalizar; }
    public MenuButton getBtnControles() { return btnControles; }
    public MenuButton getBtnCreditos() { return btnCreditos; }
    public MenuButton getBtnSair() { return btnSair; }
    public MenuButton getBtnVoltarControles() { return btnVoltarControles; }
    public MenuButton getBtnVoltarCreditos() { return btnVoltarCreditos; }
    public MenuButton getBtnPersonalizacaoCor() { return btnPersonalizacaoCor; }
    public MenuButton getBtnPersonalizacaoExpressao() { return btnPersonalizacaoExpressao; }
    public MenuButton getBtnVoltarPersonalizacao() { return btnVoltarPersonalizacao; }
    public MenuButton getBtnPausaContinuar() { return btnPausaContinuar; }
    public MenuButton getBtnPausaReiniciar() { return btnPausaReiniciar; }
    public MenuButton getBtnPausaMenu() { return btnPausaMenu; }
    public MenuButton getBtnDerrotaTentar() { return btnDerrotaTentar; }
    public MenuButton getBtnDerrotaMenu() { return btnDerrotaMenu; }
    public MenuButton getBtnVitoriaJogar() { return btnVitoriaJogar; }
    public MenuButton getBtnVitoriaMenu() { return btnVitoriaMenu; }
}
