package geometrydash.model.service;

import geometrydash.model.entity.BlockObstacle;
import geometrydash.model.entity.GameState;
import geometrydash.model.entity.Obstacle;
import geometrydash.model.entity.PersonagemCor;
import geometrydash.model.entity.PersonagemExpressao;
import geometrydash.model.entity.Phase;
import geometrydash.model.entity.PlayerProfile;
import geometrydash.model.entity.SpikeObstacle;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public static final double POSICAO_CHAO_Y = 480.0;
    public static final double TAMANHO_PERSONAGEM = 40.0;
    public static final double TAMANHO_CICLO_CENARIO = 40.0;
    public static final double DURACAO_DICA_INICIAL = 3.0;
    public static final double VELOCIDADE_INICIAL_CENARIO = 180.0;
    public static final double ACELERACAO_CENARIO = 12.0;
    public static final double VELOCIDADE_MAXIMA_CENARIO = 300.0;

    private static final double GRAVIDADE = 1800.0;
    private static final double VELOCIDADE_INICIAL_SALTO = -600.0;
    private static final double ROTACAO_POR_SALTO = Math.PI / 2.0;
    private static final double DURACAO_VOO = -2.0 * VELOCIDADE_INICIAL_SALTO / GRAVIDADE;
    private static final double VELOCIDADE_ROTACAO = ROTACAO_POR_SALTO / DURACAO_VOO;
    private static final double POSICAO_PERSONAGEM_X = 120.0;

    private GameState estadoAtual;
    private double tempoDecorrido;
    private double tempoDicaRestante;
    private double deslocamentoCenario;
    private double posicaoVerticalPersonagem;
    private double velocidadeVerticalPersonagem;
    private double rotacaoPersonagem;
    private double rotacaoInicialSalto;
    private double tempoNoAr;
    private boolean personagemNoChao;
    private final List<Obstacle> obstaculos;
    private Phase faseAtual;
    private double distanciaPercorrida;
    private PersonagemCor personagemCor;
    private PersonagemExpressao personagemExpressao;
    private double recordePercentual;

    public GameModel() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.deslocamentoCenario = 0.0;
        this.posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        this.velocidadeVerticalPersonagem = 0.0;
        this.rotacaoPersonagem = 0.0;
        this.rotacaoInicialSalto = 0.0;
        this.tempoNoAr = 0.0;
        this.personagemNoChao = true;
        this.obstaculos = new ArrayList<>();
        this.faseAtual = new Phase("Fase padrão", 800, 600, 1400, criarObstaculosPadrao());
        this.distanciaPercorrida = 0.0;
        this.personagemCor = PersonagemCor.TURQUESA;
        this.personagemExpressao = PersonagemExpressao.CLASSICA;
        this.recordePercentual = 0.0;
        inicializarObstaculos();
    }

    private static List<Obstacle> criarObstaculosPadrao() {
        List<Obstacle> obstaculosPadrao = new ArrayList<>();
        obstaculosPadrao.add(new BlockObstacle(520.0, POSICAO_CHAO_Y - 32.0, 32.0, 32.0));
        obstaculosPadrao.add(new SpikeObstacle(760.0, POSICAO_CHAO_Y - 24.0, 28.0, 24.0));
        obstaculosPadrao.add(new BlockObstacle(1020.0, POSICAO_CHAO_Y - 32.0, 32.0, 32.0));
        return obstaculosPadrao;
    }

    private void inicializarObstaculos() {
        obstaculos.clear();
        obstaculos.addAll(faseAtual.getObstaculos());
    }

    private double calcularVelocidade(double tempo) {
        return Math.min(VELOCIDADE_INICIAL_CENARIO + ACELERACAO_CENARIO * tempo,
                VELOCIDADE_MAXIMA_CENARIO);
    }

    public synchronized void atualizar(double deltaTime) {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        double velocidadeInicial = calcularVelocidade(tempoDecorrido);
        double velocidadeFinal = calcularVelocidade(tempoDecorrido + deltaTime);
        double deslocamento = (velocidadeInicial + velocidadeFinal) * 0.5 * deltaTime;
        double deslocamentoRestante = faseAtual.getComprimento() - distanciaPercorrida;
        double deslocamentoAplicado = Math.min(deslocamento, deslocamentoRestante);
        this.tempoDecorrido += deltaTime;

        if (this.tempoDicaRestante > 0.0) {
            this.tempoDicaRestante = Math.max(0.0, this.tempoDicaRestante - deltaTime);
        }

        distanciaPercorrida += deslocamentoAplicado;
        recordePercentual = Math.max(recordePercentual, getProgressoPercentual());
        deslocamentoCenario = (deslocamentoCenario + deslocamentoAplicado) % TAMANHO_CICLO_CENARIO;
        atualizarObstaculos(deslocamentoAplicado);

        if (!personagemNoChao) {
            atualizarSalto(deltaTime);
        }

        verificarColisao();
        if (estadoAtual == GameState.JOGANDO && distanciaPercorrida >= faseAtual.getComprimento()) {
            estadoAtual = GameState.VITORIA;
        }
    }

    private void atualizarObstaculos(double deslocamento) {
        for (Obstacle obstaculo : obstaculos) {
            obstaculo.mover(-deslocamento);
        }
    }

    private void verificarColisao() {
        if (this.estadoAtual != GameState.JOGANDO) {
            return;
        }

        double retanguloPersonagemX = POSICAO_PERSONAGEM_X;
        double retanguloPersonagemY = posicaoVerticalPersonagem;
        double retanguloPersonagemLargura = TAMANHO_PERSONAGEM;
        double retanguloPersonagemAltura = TAMANHO_PERSONAGEM;

        for (Obstacle obstaculo : obstaculos) {
            if (obstaculo.colideComRetangulo(retanguloPersonagemX, retanguloPersonagemY,
                    retanguloPersonagemLargura, retanguloPersonagemAltura)) {
                this.estadoAtual = GameState.DERROTA;
                break;
            }
        }
    }

    private void atualizarSalto(double deltaTime) {
        double novaPosicao = posicaoVerticalPersonagem
                + velocidadeVerticalPersonagem * deltaTime
                + 0.5 * GRAVIDADE * deltaTime * deltaTime;
        velocidadeVerticalPersonagem += GRAVIDADE * deltaTime;
        tempoNoAr += deltaTime;

        double posicaoNoChao = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        if (novaPosicao >= posicaoNoChao) {
            posicaoVerticalPersonagem = posicaoNoChao;
            velocidadeVerticalPersonagem = 0.0;
            rotacaoPersonagem = (rotacaoInicialSalto + ROTACAO_POR_SALTO) % (2.0 * Math.PI);
            tempoNoAr = 0.0;
            personagemNoChao = true;
            return;
        }

        posicaoVerticalPersonagem = novaPosicao;
        rotacaoPersonagem = rotacaoInicialSalto
                + Math.min(VELOCIDADE_ROTACAO * tempoNoAr, ROTACAO_POR_SALTO);
    }

    public synchronized void iniciarPartida() {
        if (estadoAtual == GameState.MENU || estadoAtual == GameState.PAUSADO
                || estadoAtual == GameState.DERROTA || estadoAtual == GameState.VITORIA) {
            this.estadoAtual = GameState.JOGANDO;
            this.tempoDecorrido = 0.0;
            this.tempoDicaRestante = DURACAO_DICA_INICIAL;
            this.distanciaPercorrida = 0.0;
            this.deslocamentoCenario = 0.0;
            inicializarObstaculos();
            reiniciarSalto();
        }
    }

    public synchronized void reiniciarPartida() {
        iniciarPartida();
    }

    public synchronized void alternarPausa() {
        if (this.estadoAtual == GameState.JOGANDO) {
            this.estadoAtual = GameState.PAUSADO;
        } else if (this.estadoAtual == GameState.PAUSADO) {
            this.estadoAtual = GameState.JOGANDO;
        }
    }

    public synchronized void abrirControles() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.CONTROLES;
        }
    }

    public synchronized void abrirCreditos() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.CREDITOS;
        }
    }

    public synchronized void abrirPersonalizacao() {
        if (this.estadoAtual == GameState.MENU) {
            this.estadoAtual = GameState.PERSONALIZACAO;
        }
    }

    public synchronized void avancarCorPersonagem() {
        if (estadoAtual == GameState.PERSONALIZACAO) {
            personagemCor = personagemCor.proxima();
        }
    }

    public synchronized void avancarExpressaoPersonagem() {
        if (estadoAtual == GameState.PERSONALIZACAO) {
            personagemExpressao = personagemExpressao.proxima();
        }
    }

    public synchronized void aplicarPerfil(PlayerProfile perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do jogador não pode ser nulo.");
        }
        personagemCor = perfil.cor();
        personagemExpressao = perfil.expressao();
        recordePercentual = perfil.recordePercentual();
    }

    public synchronized PlayerProfile getPerfil() {
        return new PlayerProfile(personagemCor, personagemExpressao, recordePercentual);
    }

    public synchronized PersonagemCor getPersonagemCor() {
        return personagemCor;
    }

    public synchronized PersonagemExpressao getPersonagemExpressao() {
        return personagemExpressao;
    }

    public synchronized double getRecordePercentual() {
        return recordePercentual;
    }

    public synchronized void voltarAoMenu() {
        this.estadoAtual = GameState.MENU;
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.distanciaPercorrida = 0.0;
        this.deslocamentoCenario = 0.0;
        inicializarObstaculos();
        reiniciarSalto();
    }

    private void reiniciarSalto() {
        posicaoVerticalPersonagem = POSICAO_CHAO_Y - TAMANHO_PERSONAGEM;
        velocidadeVerticalPersonagem = 0.0;
        rotacaoPersonagem = 0.0;
        rotacaoInicialSalto = 0.0;
        tempoNoAr = 0.0;
        personagemNoChao = true;
    }

    public synchronized void carregarFase(Phase fase) {
        if (fase == null) {
            throw new IllegalArgumentException("A fase carregada não pode ser nula.");
        }

        this.faseAtual = fase;
        this.distanciaPercorrida = 0.0;
        this.obstaculos.clear();
        this.obstaculos.addAll(fase.getObstaculos());
        this.tempoDecorrido = 0.0;
        this.tempoDicaRestante = 0.0;
        this.deslocamentoCenario = 0.0;
        reiniciarSalto();
    }

    public synchronized boolean pular() {
        if (estadoAtual != GameState.JOGANDO || !personagemNoChao) {
            return false;
        }

        personagemNoChao = false;
        velocidadeVerticalPersonagem = VELOCIDADE_INICIAL_SALTO;
        rotacaoInicialSalto = rotacaoPersonagem;
        tempoNoAr = 0.0;
        return true;
    }

    public synchronized GameState getEstadoAtual() {
        return estadoAtual;
    }

    public synchronized double getTempoDecorrido() {
        return tempoDecorrido;
    }

    public synchronized double getTempoDicaRestante() {
        return tempoDicaRestante;
    }

    public synchronized boolean isExibindoDica() {
        return estadoAtual == GameState.JOGANDO && tempoDicaRestante > 0.0;
    }

    public synchronized boolean isPersonagemNoChao() {
        return personagemNoChao;
    }

    public synchronized double getDeslocamentoCenario() {
        return deslocamentoCenario;
    }

    public synchronized double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public synchronized double getProgressoPercentual() {
        return Math.min(100.0, distanciaPercorrida / faseAtual.getComprimento() * 100.0);
    }

    public synchronized double getVelocidadeCenario() {
        return calcularVelocidade(tempoDecorrido);
    }

    public synchronized double getPosicaoVerticalPersonagem() {
        return posicaoVerticalPersonagem;
    }

    public synchronized double getRotacaoPersonagem() {
        return rotacaoPersonagem;
    }

    public synchronized double getVelocidadeVerticalPersonagem() {
        return velocidadeVerticalPersonagem;
    }

    public synchronized List<Obstacle> getObstaculos() {
        return List.copyOf(obstaculos);
    }

    public synchronized Phase getFaseAtual() {
        return faseAtual;
    }
}
