# CONTEXTO DO PROJETO — GEOMETRY DASH POO

**Repositório:** https://github.com/gabrielcamargogsilva/Geometry-Dash-POO.git  
**Linguagem:** Java | **Interface:** Java Swing | **Desenho:** Graphics2D | **Arquitetura:** MVC  
**Atualizado em:** 30/10/2026

Este documento deve ser lido pelo agente da IDE antes de alterar o projeto. Ele registra os requisitos, decisões e regras de colaboração. Não presume que o repositório já tenha determinada implementação: inspecione os arquivos reais.

## 1. Missão do agente

Ajudar a construir um jogo 2D desktop inspirado em Geometry Dash, com identidade visual própria. O projeto é acadêmico: o estudante precisa compreender e conseguir explicar arquitetura, orientação a objetos, decisões técnicas e funcionamento do jogo.

### Regras de colaboração
- Ensine enquanto desenvolve. Explique o objetivo e o conceito antes de cada etapa.
- Trabalhe em pequenas etapas; uma funcionalidade ou unidade técnica por vez.
- Não gere o jogo inteiro de uma vez nem crie muitas classes sem necessidade.
- Explique responsabilidades, relações entre objetos, fluxo de dados e consequências das escolhas.
- Explique termos novos (MVC, encapsulamento, herança, polimorfismo, composição, delta time, hitbox, ciclo de jogo etc.).
- Inspecione o projeto antes de editar. Preserve código útil e não substitua tudo indiscriminadamente.
- Não invente classes, dependências, versões ou funcionalidades existentes; confira os arquivos reais.
- Prefira Java padrão e Swing. Justifique qualquer dependência externa.
- Teste cada etapa e explique o resultado esperado.
- Recomende commits Git pequenos após unidades funcionais/testes concluídos.
- Quando uma etapa tiver mudanças separáveis, organize vários commits granulares e coerentes (por exemplo, lógica/testes, View/testes e documentação).
- Não trate decisões ainda abertas como definitivas.
- Não repita perguntas já respondidas neste documento.
- Se um detalhe não bloqueia o trabalho, escolha um padrão simples e reversível, explique-o e registre-o.
- Não faça operações destrutivas de Git nem sobrescreva alterações do estudante sem autorização.
- Faça a construção em branches separadas, com commits claros e frequentes. Evite merges complexos.

O estudante prefere explicações progressivas, claras e diretas, adequadas para anotar e estudar. O objetivo é aprender a construir e compreender o código, não apenas copiar soluções.

## 2. Visão geral do jogo

Jogo 2D de progressão automática:
- Personagem quadrado personalizável, próximo ao lado esquerdo da tela.
- Personagem permanece aproximadamente fixo na horizontal; cenário e obstáculos movem-se da direita para a esquerda.
- Jogador pula para evitar obstáculos.
- Primeira versão com uma fase fixa, início e fim; permitir evolução para múltiplas fases.
- Progresso de 0% a 100%.
- Dificuldade aumenta progressivamente.
- Colisão causa derrota; atingir 100% causa vitória.
- Identidade visual geométrica e paleta próprias. Não copiar assets ou identidade proprietária de Geometry Dash.

## 3. Tecnologias e restrições

Escolhidas:
- Java desktop.
- Swing para janela e interface.
- `Graphics2D` para desenhar diretamente a área de jogo.
- Execução inicial pelo IDE ou terminal, com Java instalado.
- Janela de tamanho fixo, não redimensionável.
- Fase definida em JSON externo.
- Imagens e áudio locais.

Restrições:
- Sem frameworks front-end ou engine de jogo nesta primeira versão.
- Sem serviços externos para áudio ou execução.
- Evitar dependências desnecessárias.
- Empacotamento JAR/instalador poderá ser avaliado futuramente.

## 4. Arquitetura escolhida: MVC

O padrão principal é **Model–View–Controller**. Não é apenas criar três pastas: cada camada deve ter responsabilidades e dependências claras.

### Model
Estado e regras do domínio:
- Estado e transições da partida.
- Personagem, movimento e física do salto.
- Obstáculos, hitboxes e colisões.
- Fase, percurso, progresso e dificuldade.
- Tentativas, recorde, vitória e derrota.

**Regra:** Model não depende de Swing, `JPanel`, `Graphics` ou `Graphics2D`. Deve ser possível testar a lógica sem abrir uma janela.

### View
Apresentação:
- Janela Swing e área de desenho.
- Renderização de personagem, cenário e obstáculos com `Graphics2D`.
- Menus, controles, pausa, derrota e vitória.
- Barra/percentual de progresso e mensagens.

A View representa o estado do Model; não deve decidir regras do jogo nem executar física durante a pintura.

### Controller
Entrada e coordenação:
- Teclado: Espaço e Esc.
- Mouse: pulo e botões.
- Interpretar entrada de acordo com a tela/estado atual.
- Solicitar ações ao Model: pular, pausar, reiniciar, retornar ao menu.
- Impedir que clique em botão de menu seja interpretado também como pulo.

### Fluxo conceitual
1. Jogador envia uma entrada.
2. Controller interpreta conforme tela/estado.
3. Controller solicita ação ao Model.
4. Model aplica regras e atualiza o estado.
5. View apresenta o estado atualizado.

Componentes auxiliares para ciclo de atualização, carregamento JSON, persistência e áudio podem existir quando necessários. Não force tudo dentro das três camadas. Evite acoplamento desnecessário.

## 5. Estrutura de diretórios proposta

É uma direção inicial, não uma estrutura fechada. Inspecione o repositório e adapte:

```text
Geometry-Dash-POO/
├── src/
│   └── [pacote-base]/
│       ├── Main.java
│       ├── model/
│       │   ├── game/
│       │   ├── character/
│       │   ├── obstacle/
│       │   └── phase/
│       ├── view/
│       │   ├── window/
│       │   ├── screen/
│       │   ├── component/
│       │   └── rendering/
│       ├── controller/
│       │   ├── input/
│       │   └── game/
│       └── service/ (se necessário)
├── resources/
│   ├── images/
│   ├── audio/
│   └── phases/
├── tests/ (ou estrutura de testes adotada)
└── README.md
```

Use pacotes Java em minúsculas. Não crie todos os diretórios/classes de uma vez. Verifique como resources são encontrados ao executar pelo IDE e terminal.

## 6. Requisitos funcionais

### Menu e início
Menu principal:
- Jogar
- Personalizar
- Controles
- Créditos
- Sair

Menus controlados pelo mouse. Ao clicar em Jogar, a fase começa imediatamente, sem contagem regressiva. Personagem e cenário começam a se mover e a música inicia do começo. Uma dica curta (“Espaço ou clique para pular”) aparece por alguns segundos e desaparece. Instruções completas ficam na tela Controles.

### Controles
- Espaço: pular.
- Clique durante a partida: pular.
- Esc: abrir/fechar pausa.
- Clique em botão de menu/resultado não pode disparar pulo.
- O significado da entrada depende da tela/estado atual.

### Personagem e salto
- Forma: quadrado.
- Personalização inicial: cor e expressão facial de opções predefinidas.
- Aparência persiste entre execuções.
- Hitbox retangular ligeiramente menor que o desenho, independente da aparência.
- Salto com altura e duração fixas.
- Pulo apenas no chão; comando no ar ignorado.
- Sem pulo duplo, pulo variável ou buffer de pulo.
- Física por velocidade vertical inicial e gravidade: sobe desacelerando, chega ao ápice e desce acelerando.
- Quadrado tomba visualmente uma vez para a direita durante o salto, atingindo 90 graus, e permanece tombado ao aterrissar. Um novo pulo começa na orientação normal.
- Rotação não altera hitbox.
- Aterrissagem imediata, sem animação extra.
- Sem partículas ou rastros no salto.

### Cenário e fase
- Personagem aproximadamente fixo na horizontal.
- Cenário/obstáculos deslocam-se para a esquerda.
- Fundo e paleta consistentes durante a fase.
- Janela fixa, sem redimensionamento.
- Uma fase inicial com começo/fim; obstáculos nas mesmas posições em toda tentativa.
- Fase configurada em JSON externo.
- Tipos iniciais de obstáculos: espinhos triangulares e blocos.

### Colisões
- Hitboxes retangulares menores que os desenhos, para colisões mais tolerantes.
- Qualquer colisão com hitbox definida causa derrota.
- Método final de detecção ainda não decidido:
  1. Verificar sobreposição das hitboxes em cada atualização.
  2. Considerar o deslocamento entre atualizações (detecção contínua) para reduzir o risco de atravessar obstáculos.
- Comece simples e testável; explique limitações e avalie colisão contínua com base nos testes e na velocidade máxima. Não assuma que a opção 2 já foi escolhida.

### Progresso e dificuldade
- Progresso = distância percorrida / comprimento total da fase.
- Mostrar percentual 0–100% e barra horizontal no topo central.
- Velocidade aumenta gradualmente até um limite máximo e depois permanece constante.
- Complexidade das sequências de obstáculos aumenta ao longo do percurso.
- Sem seleção de dificuldade: todos começam na mesma fase e condições.

### Pausa
- Esc abre/fecha pausa.
- Pausa congela movimento, progresso, tempo decorrido e música.
- Opções: Continuar, Reiniciar fase, Menu principal.
- Reiniciar encerra tentativa atual e inicia outra.
- Pausar não conta como tentativa.

### Derrota
- Colisão para a música imediatamente, antes da animação.
- Exibir animação curta de derrota.
- Durante a animação, jogo não avança nem registra novas colisões.
- Depois, tela de derrota com percentual alcançado, recorde, “Tentar novamente” e “Menu principal”.

### Vitória
- Ao atingir 100%, interromper movimentação.
- Música para.
- Exibir animação de celebração.
- Tela de vitória apresenta tempo da tentativa, recorde, total de tentativas até a vitória, “Jogar novamente” e “Menu principal”.

### Tentativas e recorde
- Cada início de fase cria tentativa.
- Colisão encerra tentativa.
- Vitória encerra com sucesso.
- Reinício manual encerra tentativa atual e inicia outra.
- Pausa não conta como tentativa.
- Recorde é o maior percentual alcançado em qualquer tentativa; resultado inferior não substitui recorde.
- Recorde e personalização persistem entre execuções.

## 7. Áudio

- Música durante a fase.
- Efeitos para pulo, colisão, vitória e clique em menus.
- Arquivos locais.
- Música para imediatamente na colisão, antes da animação de derrota.
- Música para na vitória.
- Ao reiniciar, música recomeça do início.
- Pausa interrompe/pausa a música; continuar retoma.
- Áudio não deve ser controlado diretamente por classes de personagem/obstáculo. Isolar a responsabilidade.

## 8. JSON e persistência

### Fase
Arquivo JSON externo define dados da fase, incluindo tipos e posições dos obstáculos. Java carrega e interpreta os dados. Ajustar posições não deve exigir alterar lógica de desenho/colisão.

### Validação
JSON inválido impede início da fase e apresenta erro claro. Validar:
- Sintaxe.
- Campos obrigatórios.
- Tipos de dados.
- Posições válidas.
- Tipos de obstáculos conhecidos.
- Coerência dos valores e limites do percurso.

Distinguir erro sintático de erro semântico/conteúdo. Não deixar a aplicação em estado inconsistente.

### Persistência
Salvar localmente:
- Cor do personagem.
- Expressão facial.
- Recorde pessoal.

Formato ainda não decidido. Escolher solução simples e explicar antes de adicionar dependências.

## 9. Ciclo de jogo e tempo

- Movimento baseado no tempo real decorrido entre atualizações (`delta time`), não diretamente em FPS.
- Exemplo: velocidade 300 px/s; em 0,01 s desloca 3 px; em 0,02 s desloca 6 px.
- Limitar delta time máximo por atualização. Após travamento, descartar excesso para evitar deslocamentos bruscos.
- Explicar que limitar delta time não garante sozinho colisões perfeitas.
- Separar atualização e renderização:
  - atualização: posições, velocidades, estados, progresso, colisões;
  - renderização: desenhar o estado atual com `Graphics2D`.
- A View não executa física durante a pintura; pintar não pode avançar a simulação.
- Pausa congela também o tempo lógico e tempo da tentativa.

## 10. Recursos visuais e arquivos

Organizar recursos separados do código, em pastas por tipo:
- `images/`
- `audio/`
- `phases/`

Elementos geométricos podem ser desenhados com `Graphics2D`. Formatos definitivos e assets ainda não foram escolhidos.

## 11. Decisões técnicas e status

### Decisões já definidas e implementadas:
- **JDK:** OpenJDK 17 (Temurin 17.0.18+8).
- **Compilação e execução:** Padrão VS Code Java / `javac` com saída em `bin/`.
- **Pacote-base:** `geometrydash` (com subpacote inicial `geometrydash.view`).
- **Resolução da janela:** 800x600 pixels, não redimensionável (`setResizable(false)`).
- **Abordagem de Janela:** Composição com `JFrame` encapsulado na classe `GameWindow`.
- **Versionamento:** `.gitignore` configurado para ignorar `bin/` e `*.class`.

### Decisões em aberto (a definir conforme necessidade):
- Dimensões finais do personagem e do chão (baseline visual definido na etapa 4: personagem 40x40 em x=120, chão em y=480; validar com a física).
- Valores finais de velocidade e aceleração do percurso (a etapa 5 usa 180 px/s como velocidade inicial fixa e reversível do cenário).
- Valores finais da física do salto (baseline da etapa 6: gravidade 1800 px/s² e velocidade inicial -600 px/s; validar/calibrar com obstáculos).
- Duração da dica.
- Esquema JSON e biblioteca/estratégia de leitura.
- Formato de persistência.
- Formatos de imagem/áudio.
- Estratégia final de colisão.
- Detalhes visuais das telas.
- Organização final de classes e serviços.

## 12. Sequência recomendada de implementação

Ajuste após inspecionar o repositório; não duplique o que já existir.

- [x] **Etapa 0:** Inspecionar repositório, README, JDK, IDE, build, arquivos e código existente. *(Concluído)*
- [x] **Etapa 1:** Ponto de entrada (`Main.java`), janela fixa Swing (`GameWindow.java`) e `.gitignore`. *(Concluído)*
- [x] **Etapa 2:** Estabelecer fundação MVC com área de desenho (`Graphics2D` via `GamePanel`), estado mínimo do Model e Controller. *(Concluído)*
- [x] **Etapa 3:** Implementar ciclo de atualização, delta time limitado e separação atualização/renderização. *(Concluído)*
- [x] **Etapa 4:** Desenhar fundo, chão e personagem. *(Concluída)*
- [x] **Etapa 5:** Implementar movimento automático do cenário. *(Concluída)*
- [x] **Etapa 6:** Implementar salto com gravidade, aterrissagem e rotação visual. *(Concluída)*
- [ ] **Etapa 7:** Adicionar obstáculos e colisões, com testes.
- [ ] **Etapa 8:** Carregar fase via JSON e validar dados.
- [ ] **Etapa 9:** Implementar estados/telas: menu, dica, pausa, derrota, vitória e transições.
- [ ] **Etapa 10:** Implementar progresso e dificuldade progressiva.
- [ ] **Etapa 11:** Adicionar áudio.
- [ ] **Etapa 12:** Adicionar personalização e persistência.
- [ ] **Etapa 13:** Testar regressões, documentar e preparar apresentação.
- [ ] **Etapa 14:** Avaliar empacotamento JAR posteriormente.

## 13. Testes

Sempre que possível, testar lógica sem abrir a janela:
- Física do salto.
- Pulo somente no chão.
- Cálculo de progresso.
- Evolução/limite de velocidade.
- Colisões.
- Recorde.
- Tentativas.
- Validação JSON.
- Transições de estado.

Testes manuais:
- Jogar começa imediatamente.
- Clique em botão não provoca pulo.
- Esc pausa/retoma.
- Pausa congela tempo, movimento e música.
- Colisão interrompe jogo e bloqueia novas colisões durante animação.
- Vitória ocorre em 100%.
- Reiniciar cria nova tentativa.
- Personalização/recorde persistem após reabrir.

Evitar testes frágeis com sleeps longos. Quando possível, fornecer delta time explicitamente às funções para permitir testes determinísticos.

## 14. Git

### Estratégia de Branches (Fluxo Git Obrigatório: branch -> develop -> main)
- **Fluxo de integração estrito:** `branch de funcionalidade` -> `develop` -> `main`.
- **Branch base de trabalho:** `develop` (todas as branches de etapas partem dela).
- **Branch estável / produção:** `main` (recebe merges apenas vindos da `develop`).
- Cada etapa deve possuir uma branch específica criada a partir de `develop` com nome descritivo da funcionalidade desenvolvida (ex.: `feat/mvc-painel-graphics2d-e-estados`, `feat/game-loop-e-delta-time`), sem incluir o termo "etapa X" no nome da branch.
- Os commits e testes da funcionalidade são realizados em sua respectiva branch antes de qualquer integração.

Preferir commits pequenos e frequentes:
- `chore: configura estrutura inicial MVC`
- `feat: adiciona janela principal Swing`
- `feat: desenha personagem inicial`
- `feat: implementa movimento com delta time`
- `test: adiciona testes da física do salto`

Verificar alterações e testes antes de sugerir commit. Não executar operações destrutivas nem sobrescrever alterações do estudante sem autorização.

## 15. Formato de trabalho por etapa

Para cada etapa:
1. **Objetivo:** o que será construído.
2. **Conceito:** explicação didática.
3. **Responsabilidades:** quais classes/camadas participam e por quê.
4. **Implementação:** mudanças pequenas, explicadas por arquivo.
5. **Execução/teste:** comandos e resultado esperado.
6. **Análise:** o que foi demonstrado e o que falta.
7. **Git:** sugestão de commit quando a etapa estiver concluída.

Ao propor uma classe, explicar sua responsabilidade, camada MVC, dependências e quem a utiliza. Evitar abstrações prematuras, interfaces/fábricas/hierarquias criadas apenas por aparência. Usar POO para resolver problemas reais e tornar o domínio compreensível.

## 16. Primeira tarefa do agente na IDE

**Não comece a escrever código imediatamente.**

1. Leia este documento.
2. Inspecione todos os diretórios e arquivos do repositório aberto.
3. Leia README e configurações.
4. Identifique JDK e forma de execução.
5. Resuma fielmente o estado atual do repositório.
6. Compare a estrutura atual com a direção MVC.
7. Proponha a menor próxima alteração necessária para começar a fundação MVC.
8. Aguarde confirmação do estudante antes de alterações estruturais amplas.

O estudante já escolheu Java, Swing, `Graphics2D`, execução local e MVC. Não pergunte novamente essas decisões.

**Prioridade central:** entregar um jogo funcional, organizado e testável, garantindo que o estudante entenda e consiga defender tecnicamente cada parte.

## 17. Registro de Progresso das Etapas

### Etapa 0 (30/10/2026) — Inspeção Inicial
- Inspeção de ambiente concluída: OpenJDK 17.0.18, VS Code Java Project, sem dependências externas.
- Análise de estado e alinhamento das diretrizes de arquitetura MVC realizado.

### Etapa 1 (30/10/2026) — Ponto de Entrada e Janela Principal Swing
- **Ações:**
  - Configurado pacote base `geometrydash`.
  - Criado `geometrydash.Main` na Event Dispatch Thread (`SwingUtilities.invokeLater`).
  - Criado `geometrydash.view.GameWindow` encapsulando `JFrame` (800x600, centralizado, não redimensionável).
  - Criado `.gitignore` para ignorar diretório de build `bin/`.
  - Removido arquivo temporário `src/App.java`.
- **Conceitos de POO e Java abordados:**
  - Organização por pacotes (`package`).
  - Modificadores de acesso e métodos estáticos (`public static void main`).
  - Encapsulamento e Composição vs. Herança (GameWindow contém um JFrame).
  - Concorrência de interface gráfica com Swing EDT.

### Etapa 2 (30/10/2026) — Fundação MVC e Área de Desenho Graphics2D
- **Branch:** `feat/mvc-painel-graphics2d-e-estados` (integrada via merge em `develop`)
- **Ações:**
  - **CI:** Adicionado workflow de detecção de segredos com Gitleaks em `.github/workflows/gitleaks.yml`.
  - **Model:** Criado `GameState` (enum com `MENU`, `JOGANDO`, `PAUSADO`, `DERROTA`, `VITORIA`) e `GameModel` contendo a regra de transição de estado pura (zero dependências de Swing/AWT).
  - **View:** Criado `GamePanel` herdando de `JPanel` com área interna de 800x600 e renderização especializada via `Graphics2D` com anti-aliasing. Atualizado `GameWindow` com `pack()` para preservar a área útil exata.
  - **Controller:** Criado `InputController` estendendo `KeyAdapter` (captura de Espaço, Esc e M) e `GameController` orquestrando Model, View e eventos.
  - **Testes Unitários:** Criado `GameModelTest` em `test/` validando o Model 100% sem interface gráfica.
- **Conceitos de POO e Java abordados:**
  - Tipos enumerados fortemente tipados (`enum`).
  - Herança e Polimorfismo com Sobrescrita de métodos (`@Override` em `paintComponent`).
  - Padrão Adapter (`KeyAdapter` do Java AWT para tratar eventos sem declarar métodos desnecessários).
  - Desacoplamento arquitetural (Model isolado de View; View apenas reflete o Model; Controller intermedia).

### Etapa 3 (30/10/2026) — Ciclo de Jogo (Game Loop) e Delta Time
- **Branch:** `feat/game-loop-e-delta-time`
- **Ações:**
  - **Controller:** Criado `GameLoop` implementando `Runnable` com taxa alvo de 60 FPS, medição de delta time com `System.nanoTime()` e descarte de atrasos bruscos via `limitarDeltaTime` (teto de 0.05s).
  - **Model:** Adicionado `tempoDecorrido` e método `atualizar(double deltaTime)` no `GameModel`. O tempo só avança durante o estado `JOGANDO` e congela durante `PAUSADO` ou `MENU`.
  - **View:** `GamePanel` atualizado para exibir em tempo real a contagem de tempo da tentativa durante a partida e o tempo congelado na tela de pausa.
  - **Testes Unitários:** Adicionadas suítes completas de teste para avanço temporal determinístico no `GameModelTest` e validação do limitador de delta time no `GameLoopTest`.
- **Conceitos de POO e Java abordados:**
  - Concorrência básica em Java com `Runnable` e `Thread`.
  - Visibilidade de memória entre threads com a palavra-chave `volatile`.
  - Separação entre simulação lógica (`model.atualizar`) e renderização gráfica (`SwingUtilities.invokeLater(view::repaint)`).
  - Métodos puros e testabilidade determinística com Delta Time injetado.

### Etapa 4 (30/10/2026) — Fundo, Chão e Personagem
- **Branch:** `feat/fundo-chao-personagem`
- **Ações:**
  - **View:** `GamePanel` agora desenha fundo em gradiente, linhas geométricas discretas, chão quadriculado e uma faixa luminosa que marca o piso.
  - **View:** Desenhado um personagem quadrado estático de 40x40 pixels, com cor própria, contorno e expressão geométrica. Ele fica na posição horizontal x=120, apoiado no chão em y=480.
  - **Testes:** Criado `GamePanelTest`, que renderiza o painel em um `BufferedImage` e verifica as cores do fundo, do chão e do personagem sem abrir uma janela.
- **Conceitos de Java e POO abordados:**
  - `Graphics2D` e `GradientPaint` para composição visual.
  - Separação entre apresentação e simulação: a View desenha o personagem sem alterar posição, física ou tempo no Model.
  - Testes headless de interface Swing usando uma imagem em memória.
- **Limite desta etapa:** O cenário e o personagem permanecem estáticos; o movimento automático será tratado na etapa 5 e o salto na etapa 6.

### Etapa 5 (30/10/2026) — Movimento Automático do Cenário
- **Branch:** `feat/movimento-automatico-cenario`
- **Ações:**
  - **Model:** `GameModel.atualizar(deltaTime)` agora avança o deslocamento horizontal do cenário a 180 px/s somente durante `JOGANDO`.
  - **Model:** O deslocamento é mantido dentro de um ciclo de 40 pixels, correspondente à repetição da grade do chão, e reinicia ao iniciar uma nova partida ou voltar ao menu.
  - **View:** As linhas verticais da grade do chão são desenhadas a partir do deslocamento atual do Model, criando movimento contínuo para a esquerda sem mover o personagem.
  - **Testes:** `GameModelTest` cobre velocidade, pausa, reinício e limite do ciclo; `GamePanelTest` verifica que a grade se desloca entre quadros.
- **Decisão reversível:** A velocidade visual inicial é 180 px/s; poderá ser ajustada quando movimento, obstáculos e dificuldade forem calibrados em conjunto.
- **Conceitos de Java e POO abordados:**
  - Integração de velocidade por tempo: distância = velocidade × delta time.
  - Model calcula o estado do movimento; View apenas o representa durante a pintura.
  - Deslocamento modular mantém a repetição visual da grade sem crescimento numérico contínuo.
- **Limite desta etapa:** Ainda não há obstáculos; serão adicionados com posições e colisões em etapas posteriores.

### Etapa 6 (30/10/2026) — Salto, Gravidade e Rotação Visual
- **Branch:** `feat/salto-gravidade-rotacao`
- **Ações:**
  - **Model:** Implementada a física vertical com gravidade de 1800 px/s² e velocidade inicial de -600 px/s. O salto atinge cerca de 100 pixels de altura e dura aproximadamente 0,67 segundo.
  - **Model:** O pulo só é aceito durante `JOGANDO` e quando o personagem está no chão. A posição é integrada pelo delta time; ao cruzar o piso, a posição é ajustada para o chão e a velocidade vertical zera.
  - **Model:** A pausa congela posição e rotação no ar; ao aterrissar, a orientação fica tombada 90 graus para a direita até um novo pulo. Iniciar nova partida ou voltar ao menu restaura a posição, velocidade e orientação iniciais.
  - **Controller:** Espaço inicia a partida no menu e pula durante o jogo. Clique também pula durante o jogo; cliques no menu e comandos de pulo no ar são ignorados.
  - **View:** O personagem acompanha a posição vertical do Model e tomba uma única vez, até 90 graus para a direita, durante o salto. A transformação afeta apenas o desenho, não as regras ou a posição horizontal.
  - **Concorrência:** Métodos de acesso e atualização do Model são sincronizados para serializar atualizações do GameLoop e comandos recebidos pela interface Swing.
  - **Testes:** `GameModelTest` cobre início do salto, gravidade, ápice, pausa, aterrissagem, rotação e bloqueio de pulo duplo; `InputControllerTest` verifica teclado e mouse; `GamePanelTest` verifica elevação e rotação renderizadas.
- **Conceitos de Java e POO abordados:**
  - Cinemática vertical por aceleração e delta time.
  - Separação entre estado físico do Model e transformação visual da View.
  - Sincronização de métodos para coordenar entrada na EDT e atualização do GameLoop.
- **Limite desta etapa:** A hitbox ainda não foi criada; a rotação do desenho não influencia colisões. Obstáculos e colisões serão tratados nas etapas seguintes.
- **Ajuste de comportamento:** Após validação visual, a animação foi alterada de uma volta completa para um único tombamento de 90 graus para a direita. O personagem permanece tombado ao aterrissar e volta à orientação normal ao iniciar o próximo pulo.
