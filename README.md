# Geometry Dash POO

Projeto acadêmico de um jogo desktop 2D de progressão automática inspirado na mecânica de Geometry Dash, construído com foco em **Arquitetura de Software (MVC)** e conceitos de **Programação Orientada a Objetos (POO)** em Java.

Desenvolvido para o curso de **Back End — SENAI 2026 (2º Semestre)**.

---

## 🛠️ Tecnologias e Decisões Técnicas

* **Linguagem:** Java 17 (Eclipse Temurin / OpenJDK).
* **Interface Gráfica:** Java Swing (`JFrame`, `JPanel`).
* **Renderização Visual:** `Graphics2D` nativo (formas geométricas, gradientes, transformações matriciais e anti-aliasing).
* **Arquitetura:** Model-View-Controller (MVC) com separação estrita de camadas.
* **Fases:** Arquivos JSON externos com validação de dados (`resources/phases/`), comprimento total e obstáculos espaçados para permitir saltos individuais mesmo na velocidade máxima.
* **Progressão:** Indicador de 0% a 100%; velocidade do cenário aumenta de 180 para 300 px/s e é limitada ao atingir o máximo.
* **Colisões:** Blocos usam hitbox retangular; espinhos colidem pela área triangular desenhada, incluindo o contato com suas bordas.
* **Áudio:** Música e efeitos locais em WAV, reproduzidos com Java Sound (`Clip`), sem dependências externas.
* **Controle de Versão:** Git Flow estrito (`branch de funcionalidade` -> `develop` -> `main`).
* **CI / Automação:** GitHub Actions para detecção de segredos (Gitleaks) e esteira de testes automatizados headless (`.github/workflows/`).

---

## 🏛️ Arquitetura MVC

* **Model (`geometrydash.model`):**
  * Representa o estado da partida, tempo, cinemática de salto, gravidade, cálculo de colisão e obstáculos.
  * **Regra de ouro:** Nenhuma dependência de bibliotecas de interface gráfica (AWT/Swing). 100% testável via testes unitários em memória.
* **View (`geometrydash.view`):**
  * Apresentação visual através de `GameWindow` e `GamePanel`.
  * Desenha cenário, obstáculos, personagem e telas de interface (Menu Principal, Controles, Créditos, Pausa, Derrota e Vitória).
  * Componentes reutilizáveis como `MenuButton` com efeito hover.
* **Controller (`geometrydash.controller`):**
  * Orquestra o ciclo contínuo com `GameLoop` (60 FPS com delta time).
  * Escuta eventos de teclado e mouse com `InputController`.
  * Garante que cliques em botões de menu não disparem saltos acidentais no jogo.

---

## 🎮 Controles

| Entrada | Ação |
| :--- | :--- |
| **Espaço** | Pular (durante a partida) / Iniciar ou reiniciar partida |
| **Clique do Mouse** | Pular (durante a partida) / Interagir com botões dos menus |
| **Esc** | Abrir / Fechar menu de pausa ou voltar ao menu principal |
| **M** | Retornar ao menu principal (a partir da pausa ou telas de resultado) |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java Development Kit (JDK) 17 ou superior instalado.

### 1. Compilação
No terminal (PowerShell no Windows):
```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src/*.java, test/*.java | ForEach-Object { $_.FullName })
```

### 2. Executar o Jogo
```powershell
java -cp bin geometrydash.Main
```

### 3. Executar Todos os Testes Automatizados
```powershell
java -cp bin geometrydash.model.GameModelTest
java -cp bin geometrydash.controller.GameLoopTest
java -cp bin geometrydash.controller.InputControllerTest
java -cp bin geometrydash.view.GamePanelTest
java -cp bin geometrydash.view.component.MenuButtonTest
java -cp bin geometrydash.audio.AudioAssetsTest
```
*(Todos os testes executam de forma headless, sem necessidade de abrir janelas visuais).*

O arquivo `resources/phases/fase-1.json` define a duração espacial da fase pelo campo `comprimento` e posiciona os obstáculos ao longo desse percurso. A velocidade começa em 180 px/s, aumenta 12 px/s a cada segundo de jogo e para de crescer em 300 px/s. O progresso é calculado pela distância percorrida em relação ao comprimento configurado; chegar ao fim conclui a fase.

Os arquivos de áudio originais ficam em `resources/audio/`. A música toca em loop durante a partida, pausa/retoma com o jogo e reinicia ao começar outra tentativa. Há efeitos para pulo, colisão, vitória e clique em botões. Se um arquivo ou dispositivo de áudio não estiver disponível, o erro é informado no console e o jogo continua sem aquele áudio.

---

## 📋 Status de Desenvolvimento

- [x] **Etapa 0:** Inspeção inicial e estruturação.
- [x] **Etapa 1:** Ponto de entrada, janela fixa Swing e `.gitignore`.
- [x] **Etapa 2:** Fundação MVC com `Graphics2D` e testes do Model.
- [x] **Etapa 3:** Game Loop contínuo com Delta Time estabilizado.
- [x] **Etapa 4:** Desenho do fundo, chão e personagem geométrico.
- [x] **Etapa 5:** Movimento automático contínuo do cenário.
- [x] **Etapa 6:** Física do salto com gravidade e rotação visual acumulada.
- [x] **Etapa 7:** Obstáculos (blocos e espinhos) e detecção de colisões.
- [x] **Etapa 8:** Carregador de fases externo via JSON com validação semântica.
- [x] **Etapa 9:** Telas de menu, controles, créditos, dica inicial, pausa, derrota e vitória com botões interativos.
- [x] **Etapa 10:** Progresso percentual, barra de progresso e dificuldade progressiva com conclusão da fase.
- [x] **Etapa 11:** Efeitos sonoros e música da fase.
- [ ] **Etapa 12:** Personalização do personagem e persistência local.
- [ ] **Etapa 13:** Testes de regressão e documentação final.
- [ ] **Etapa 14:** Empacotamento executável (JAR).
