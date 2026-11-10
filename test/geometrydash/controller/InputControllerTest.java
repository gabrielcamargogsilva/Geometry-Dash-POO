package geometrydash.controller;

import geometrydash.audio.AudioService;
import geometrydash.model.entity.BlockObstacle;
import geometrydash.model.entity.GameState;
import geometrydash.model.entity.PersonagemCor;
import geometrydash.model.entity.PersonagemExpressao;
import geometrydash.model.entity.Phase;
import geometrydash.model.service.GameModel;
import geometrydash.persistence.ProfileStore;
import geometrydash.view.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifica os comandos de iniciar a partida, pular por teclado ou mouse,
 * e a interação com botões de menu sem disparar pulos acidentais.
 */
public class InputControllerTest {

    public static void main(String[] args) throws Exception {
        GameModel model = new GameModel();
        GamePanel view = new GamePanel(model);
        AudioFake audio = new AudioFake();
        Path diretorioPerfil = Files.createTempDirectory("geometrydash-input-test");
        Path caminhoPerfil = diretorioPerfil.resolve("perfil.properties");
        ProfileStore profileStore = new ProfileStore(caminhoPerfil);
        InputController controller = new InputController(model, view, audio, profileStore);

        // 1. Clique fora de botões no menu não deve iniciar nem pular
        pressionarMouseEm(controller, view, 10, 10);
        afirmar(model.getEstadoAtual() == GameState.MENU && model.isPersonagemNoChao(),
                "Clique fora de botões no menu não deve iniciar nem pular.");

        // 2. Clique no botão 'Jogar' deve iniciar a partida sem pular
        int btnJogarX = view.getBtnJogar().getX() + 10;
        int btnJogarY = view.getBtnJogar().getY() + 10;
        pressionarMouseEm(controller, view, btnJogarX, btnJogarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique no botão Jogar deve iniciar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique no botão Jogar NÃO pode disparar o salto do personagem.");
        afirmar(audio.cliques == 1, "Clicar em um botão deve reproduzir o efeito de clique.");

        // 3. Clique durante a partida deve pular
        pressionarMouseEm(controller, view, 10, 10);
        afirmar(!model.isPersonagemNoChao(),
                "Clique durante a partida deve iniciar o salto.");
        afirmar(audio.pulos == 1, "Um pulo aceito por clique deve reproduzir o efeito de pulo.");

        // 4. Espaço durante o salto deve ser ignorado (sem pulo duplo)
        double velocidadeDuranteSalto = model.getVelocidadeVerticalPersonagem();
        pressionarEspaco(controller, view);
        afirmar(model.getVelocidadeVerticalPersonagem() == velocidadeDuranteSalto,
                "Espaço durante o salto deve ser ignorado, sem pulo duplo.");
        afirmar(audio.pulos == 1, "Um pulo ignorado no ar não deve reproduzir efeito sonoro.");

        // 5. Aterrissagem e salto com espaço
        model.atualizar(0.7);
        afirmar(model.isPersonagemNoChao(), "O personagem deve aterrissar para testar um novo comando.");
        pressionarEspaco(controller, view);
        afirmar(!model.isPersonagemNoChao(),
                "Espaço durante a partida deve iniciar o salto quando o personagem está no chão.");
        afirmar(audio.pulos == 2, "Um pulo aceito pelo teclado deve reproduzir o efeito de pulo.");

        // 6. Teste de botões nas telas de Controles
        model.voltarAoMenu();
        int btnControlesX = view.getBtnControles().getX() + 10;
        int btnControlesY = view.getBtnControles().getY() + 10;
        pressionarMouseEm(controller, view, btnControlesX, btnControlesY);
        afirmar(model.getEstadoAtual() == GameState.CONTROLES,
                "Clique no botão Controles deve abrir a tela de Controles.");

        int btnVoltarX = view.getBtnVoltarControles().getX() + 10;
        int btnVoltarY = view.getBtnVoltarControles().getY() + 10;
        pressionarMouseEm(controller, view, btnVoltarX, btnVoltarY);
        afirmar(model.getEstadoAtual() == GameState.MENU,
                "Clique no botão Voltar deve retornar ao menu principal.");

        // 7. Personalização e salvamento das opções
        int btnPersonalizarX = view.getBtnPersonalizar().getX() + 10;
        int btnPersonalizarY = view.getBtnPersonalizar().getY() + 10;
        pressionarMouseEm(controller, view, btnPersonalizarX, btnPersonalizarY);
        afirmar(model.getEstadoAtual() == GameState.PERSONALIZACAO,
                "O botão Personalizar deve abrir a tela de personalização.");

        pressionarMouseEm(controller, view,
                view.getBtnPersonalizacaoCor().getX() + 10, view.getBtnPersonalizacaoCor().getY() + 10);
        pressionarMouseEm(controller, view,
                view.getBtnPersonalizacaoExpressao().getX() + 10,
                view.getBtnPersonalizacaoExpressao().getY() + 10);
        afirmar(model.getPersonagemCor() == PersonagemCor.LARANJA
                        && model.getPersonagemExpressao() == PersonagemExpressao.FELIZ,
                "Os botões devem atualizar a cor e a expressão do personagem.");
        afirmar(profileStore.carregar().cor() == PersonagemCor.LARANJA
                        && profileStore.carregar().expressao() == PersonagemExpressao.FELIZ,
                "As opções escolhidas devem ser salvas no perfil local.");

        pressionarMouseEm(controller, view,
                view.getBtnVoltarPersonalizacao().getX() + 10,
                view.getBtnVoltarPersonalizacao().getY() + 10);
        afirmar(model.getEstadoAtual() == GameState.MENU,
                "O botão Voltar da personalização deve retornar ao menu.");

        // 8. Teste de botões na tela de Pausa
        model.iniciarPartida();
        model.alternarPausa();
        afirmar(model.getEstadoAtual() == GameState.PAUSADO, "Deve estar pausado.");

        int btnContinuarX = view.getBtnPausaContinuar().getX() + 10;
        int btnContinuarY = view.getBtnPausaContinuar().getY() + 10;
        pressionarMouseEm(controller, view, btnContinuarX, btnContinuarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique em Continuar deve retomar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique em Continuar na pausa NÃO pode disparar o salto.");

        // 9. Teste de botões na tela de Derrota
        model.voltarAoMenu();
        model.carregarFase(new Phase("Derrota", 800, 600, 500,
                List.of(new BlockObstacle(120,
                        GameModel.POSICAO_CHAO_Y - GameModel.TAMANHO_PERSONAGEM,
                        GameModel.TAMANHO_PERSONAGEM, GameModel.TAMANHO_PERSONAGEM))));
        model.iniciarPartida();
        model.atualizar(0.016);
        afirmar(model.getEstadoAtual() == GameState.DERROTA,
                "O botão Tentar novamente deve ser testado após uma colisão real.");
        int btnTentarX = view.getBtnDerrotaTentar().getX() + 10;
        int btnTentarY = view.getBtnDerrotaTentar().getY() + 10;
        pressionarMouseEm(controller, view, btnTentarX, btnTentarY);
        afirmar(model.getEstadoAtual() == GameState.JOGANDO,
                "Clique em Tentar Novamente na tela de Derrota deve reiniciar a partida.");
        afirmar(model.isPersonagemNoChao(),
                "Clique em Tentar Novamente NÃO pode disparar o salto.");

        System.out.println(">>> [SUCESSO] Os comandos de iniciar e pular foram encaminhados corretamente.");
        Files.deleteIfExists(caminhoPerfil);
        Files.deleteIfExists(diretorioPerfil);
    }

    private static void pressionarMouseEm(InputController controller, GamePanel view, int x, int y) {
        MouseEvent evento = new MouseEvent(
                view, MouseEvent.MOUSE_PRESSED, 0L, 0, x, y, 1, false);
        controller.getMouseAdapter().mousePressed(evento);
    }

    private static void pressionarEspaco(InputController controller, GamePanel view) {
        KeyEvent evento = new KeyEvent(
                view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SPACE, ' ');
        controller.keyPressed(evento);
    }

    private static void afirmar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new AssertionError("Falha no teste: " + mensagemErro);
        }
    }

    private static class AudioFake implements AudioService {
        private int pulos;
        private int cliques;

        @Override public void iniciarMusica() { }
        @Override public void pausarMusica() { }
        @Override public void retomarMusica() { }
        @Override public void pararMusica() { }
        @Override public void tocarPulo() { pulos++; }
        @Override public void tocarColisao() { }
        @Override public void tocarVitoria() { }
        @Override public void tocarClique() { cliques++; }
        @Override public void close() { }
    }
}
