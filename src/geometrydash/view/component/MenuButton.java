package geometrydash.view.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Representa um botão interativo com efeito hover para menus e telas (Camada View).
 * 
 * Conceitos de POO:
 * - Encapsulamento: Dimensões geométricas, texto e estado de interação (hover)
 *   ficam contidos dentro do próprio botão, facilitando reutilização em qualquer tela.
 * - Coesão: O botão é responsável por testar se um ponto do mouse o atinge (hit test)
 *   e por sua própria renderização visual com Graphics2D.
 */
public class MenuButton {
    private final int x;
    private final int y;
    private final int largura;
    private final int altura;
    private final String texto;

    private boolean hovered;

    private Color corFundo = new Color(32, 42, 68);
    private Color corHover = new Color(48, 72, 118);
    private Color corBorda = new Color(91, 244, 224);
    private Color corTexto = Color.WHITE;

    public MenuButton(int x, int y, int largura, int altura, String texto) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.texto = texto;
        this.hovered = false;
    }

    /**
     * Verifica se as coordenadas do cursor do mouse estão dentro dos limites do botão.
     */
    public boolean contem(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + largura && mouseY >= y && mouseY <= y + altura;
    }

    /**
     * Renderiza o botão com cantos arredondados, borda destacada e texto centralizado.
     */
    public void desenhar(Graphics2D g2d) {
        // Fundo do botão (muda de cor ao passar o mouse)
        g2d.setColor(hovered ? corHover : corFundo);
        g2d.fillRoundRect(x, y, largura, altura, 12, 12);

        // Borda luminosa
        g2d.setColor(hovered ? Color.WHITE : corBorda);
        g2d.drawRoundRect(x, y, largura, altura, 12, 12);

        // Texto centralizado
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2d.setColor(corTexto);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (largura - fm.stringWidth(texto)) / 2;
        int textY = y + (altura - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(texto, textX, textY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setCores(Color corFundo, Color corHover, Color corBorda) {
        this.corFundo = corFundo;
        this.corHover = corHover;
        this.corBorda = corBorda;
    }
}
