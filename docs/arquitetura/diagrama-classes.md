# Diagrama de classes — Geometry Dash POO

```mermaid
classDiagram
    namespace App {
        class Main
    }

    namespace MVC_View {
        class GameWindow {
            -frame: JFrame
            +exibir()
            +getFrame(): JFrame
        }

        class GamePanel {
            -model: GameModel
            +getBotoesAtivos(): List~MenuButton~
            #paintComponent(g: Graphics)
        }

        class MenuButton {
            -x: int
            -y: int
            -largura: int
            -altura: int
            -texto: String
            -hovered: boolean
            +contem(mouseX: int, mouseY: int): boolean
            +desenhar(g2d: Graphics2D)
        }
    }

    namespace MVC_Controller {
        class GameController {
            -model: GameModel
            -view: GamePanel
            -audio: AudioService
            -profileService: PlayerProfileService
            -inputController: InputController
            -gameLoop: GameLoop
            +encerrar()
        }

        class InputController {
            -model: GameModel
            -view: GamePanel
            -audio: AudioService
            -profileService: PlayerProfileService
            -mouseAdapter: MouseAdapter
            +keyPressed(e: KeyEvent)
            +getMouseAdapter(): MouseAdapter
        }

        class GameLoop {
            -model: GameModel
            -view: GamePanel
            -audio: AudioService
            -profileService: PlayerProfileService
            -rodando: boolean
            +iniciar()
            +parar()
            +run()
        }
    }

    namespace Audio {
        class AudioService {
            <<interface>>
            +iniciarMusica()
            +pausarMusica()
            +retomarMusica()
            +pararMusica()
            +tocarPulo()
            +tocarColisao()
            +tocarVitoria()
            +tocarClique()
            +close()
        }

        class AudioManager {
            -musica: Clip
            -somPulo: Clip
            -somColisao: Clip
            -somVitoria: Clip
            -somClique: Clip
        }
    }

    namespace Persistence {
        class ProfileStore {
            -repository: PlayerProfileRepository
            +carregar(): PlayerProfile
            +salvar(perfil: PlayerProfile)
        }
    }

    namespace Model_Entity {
        class GameState {
            <<enumeration>>
            MENU
            CONTROLES
            CREDITOS
            PERSONALIZACAO
            JOGANDO
            PAUSADO
            DERROTA
            VITORIA
        }

        class Obstacle {
            <<abstract>>
            -tipo: Tipo
            -x: double
            -y: double
            -largura: double
            -altura: double
            +mover(deslocamentoX: double)
            +colideComRetangulo(retanguloX: double, retanguloY: double, retanguloLargura: double, retanguloAltura: double): boolean*
            +copiar(): Obstacle*
            +getTipo(): Tipo
            +getX(): double
            +getY(): double
            +getLargura(): double
            +getAltura(): double
        }

        class BlockObstacle {
            +colideComRetangulo(): boolean
            +copiar(): BlockObstacle
        }

        class SpikeObstacle {
            -projecoesSeparadas(): boolean
            +colideComRetangulo(): boolean
            +copiar(): SpikeObstacle
        }

        class Phase {
            -nome: String
            -largura: int
            -altura: int
            -comprimento: int
            -obstaculos: List~Obstacle~
            +getNome(): String
            +getLargura(): int
            +getAltura(): int
            +getComprimento(): int
            +getObstaculos(): List~Obstacle~
        }

        class PlayerProfile {
            <<record>>
            +cor: PersonagemCor
            +expressao: PersonagemExpressao
            +recordePercentual: double
            +padrao(): PlayerProfile$
        }

        class PersonagemCor {
            <<enumeration>>
            TURQUESA
            LARANJA
            ROXO
            VERDE
            +getNomeExibicao(): String
            +getRgb(): int
            +proxima(): PersonagemCor
        }

        class PersonagemExpressao {
            <<enumeration>>
            CLASSICA
            FELIZ
            SERIA
            +getNomeExibicao(): String
            +proxima(): PersonagemExpressao
        }
    }

    namespace Model_Repository {
        class PlayerProfileRepository {
            <<interface>>
            +carregar(): PlayerProfile
            +salvar(perfil: PlayerProfile)
        }

        class FilePlayerProfileRepository {
            -caminho: Path
            +carregar(): PlayerProfile
            +salvar(perfil: PlayerProfile)
        }
    }

    namespace Model_Service {
        class GameModel {
            -estadoAtual: GameState
            -posicaoVerticalPersonagem: double
            -velocidadeVerticalPersonagem: double
            -rotacaoPersonagem: double
            -obstaculos: List~Obstacle~
            -faseAtual: Phase
            -distanciaPercorrida: double
            -personagemCor: PersonagemCor
            -personagemExpressao: PersonagemExpressao
            -recordePercentual: double
            +atualizar(deltaTime: double)
            +iniciarPartida()
            +reiniciarPartida()
            +alternarPausa()
            +abrirControles()
            +abrirCreditos()
            +abrirPersonalizacao()
            +voltarAoMenu()
            +pular(): boolean
            +carregarFase(fase: Phase)
            +aplicarPerfil(perfil: PlayerProfile)
            +getPerfil(): PlayerProfile
            +avancarCorPersonagem()
            +avancarExpressaoPersonagem()
            +getEstadoAtual(): GameState
            +getObstaculos(): List~Obstacle~
            +getProgressoPercentual(): double
        }

        class GameService {
            -model: GameModel
        }

        class PlayerProfileService {
            -repository: PlayerProfileRepository
            +carregarPerfil(): PlayerProfile
            +salvarPerfil(perfil: PlayerProfile)
        }

        class PhaseService
        class PhaseLoader {
            +carregarRecurso(caminhoRecurso: String): Phase$
            +carregarArquivo(caminhoArquivo: String): Phase$
            +carregarJson(json: String): Phase$
        }
    }

    %% ---- Ponto de Entrada (Main) ----
    Main --> GameModel
    Main --> PhaseService
    Main --> PlayerProfileService
    Main --> GameController
    Main --> GamePanel
    Main --> GameWindow

    %% ---- Herança e Implementação de Interface ----
    Obstacle <|-- BlockObstacle
    Obstacle <|-- SpikeObstacle
    AudioService <|.. AudioManager : implements
    PlayerProfileRepository <|.. FilePlayerProfileRepository : implements
    PlayerProfileRepository <|.. ProfileStore : implements
    GameLoop ..|> Runnable : implements
    InputController --|> KeyAdapter : extends
    GamePanel --|> JPanel : extends

    %% ---- View: composição e dependências ----
    GameWindow --> GamePanel : agrega
    GamePanel --> GameModel : consulta
    GamePanel *-- MenuButton : compõe

    %% ---- Controller: dependências reais ----
    GameController --> GameModel
    GameController --> GamePanel
    GameController --> AudioService
    GameController --> PlayerProfileService
    GameController *-- InputController : cria
    GameController *-- GameLoop : cria

    InputController --> GameModel
    InputController --> GamePanel
    InputController --> AudioService
    InputController --> PlayerProfileService

    GameLoop --> GameModel
    GameLoop --> GamePanel
    GameLoop --> AudioService
    GameLoop --> PlayerProfileService

    %% ---- Persistence e Repositório ----
    ProfileStore o-- PlayerProfileRepository : delega
    FilePlayerProfileRepository --> PlayerProfile
    PlayerProfileService --> PlayerProfileRepository
    PlayerProfileService --> PlayerProfile

    %% ---- Model Entity: associações ----
    Phase o-- Obstacle : contém lista
    PlayerProfile --> PersonagemCor
    PlayerProfile --> PersonagemExpressao

    %% ---- Model Service: associações ----
    GameModel --> Phase
    GameModel --> Obstacle
    GameModel --> GameState
    GameModel --> PlayerProfile
    GameModel --> PersonagemCor
    GameModel --> PersonagemExpressao
    GameService --> GameModel
    PhaseService --> PhaseLoader
    PhaseLoader --> Phase
    PhaseLoader --> Obstacle
    PhaseLoader --> BlockObstacle : cria
    PhaseLoader --> SpikeObstacle : cria
```
