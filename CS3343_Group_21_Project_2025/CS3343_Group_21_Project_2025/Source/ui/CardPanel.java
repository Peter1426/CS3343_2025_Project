package ui;

import model.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/**
 *   display component
 */
public class CardPanel extends JPanel {
    private Card card;
    private boolean selected;
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;
    
    public CardPanel(Card card) {
        this.card = card;
        this.selected = false;
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public Card getCard() {
        return card;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // 
        if (!selected) {
            g2d.setColor(new Color(0, 0, 0, 60));
            RoundRectangle2D shadow = new RoundRectangle2D.Double(
                4, 4, width - 2, height - 2, 12, 12
            );
            g2d.fill(shadow);
        }
        
        // background col
        Color bgColor = selected ? new Color(255, 255, 180) : Color.WHITE;
        Color borderColor = selected ? new Color(255, 180, 0) : new Color(100, 100, 100);
        int borderWidth = selected ? 5 : 2;
        
        //  rectangle
        RoundRectangle2D cardShape = new RoundRectangle2D.Double(
            borderWidth / 2.0,
            borderWidth / 2.0,
            width - borderWidth,
            height - borderWidth,
            12, 12
        );
        
        // backgroud
        GradientPaint gradient = new GradientPaint(
            0, 0, bgColor,
            0, height, selected ? new Color(255, 240, 150) : new Color(245, 245, 245)
        );
        g2d.setPaint(gradient);
        g2d.fill(cardShape);
        
        // border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(cardShape);
        
        // red
        boolean isRed = card.isRed();
        Color textColor = isRed ? new Color(220, 20, 20) : new Color(20, 20, 20);
        
        // mark
        String rank = card.getRank().getDisplay();
        Font rankFont = new Font(Font.SANS_SERIF, Font.BOLD, 22);
        if (!rankFont.canDisplay(rank.charAt(0))) {
            rankFont = new Font(Font.MONOSPACED, Font.BOLD, 22);
        }
        g2d.setFont(rankFont);
        g2d.setColor(textColor);
        
        FontMetrics fm = g2d.getFontMetrics(rankFont);
        int rankWidth = fm.stringWidth(rank);
        int rankAscent = fm.getAscent();
        g2d.drawString(rank, (width - rankWidth) / 2, rankAscent + 8);
        
        // suit
        String suit = card.getSuit().getSymbol();
        Font suitFont = new Font(Font.SERIF, Font.BOLD, 32);
        // 
        String[] fontNames = {"Arial Unicode MS", "Lucida Sans Unicode", "DejaVu Sans", Font.SERIF};
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.BOLD, 32);
            if (testFont.canDisplay(suit.charAt(0))) {
                suitFont = testFont;
                break;
            }
        }
        g2d.setFont(suitFont);
        g2d.setColor(textColor);
        
        fm = g2d.getFontMetrics(suitFont);
        int suitWidth = fm.stringWidth(suit);
        int suitAscent = fm.getAscent();
        g2d.drawString(suit, (width - suitWidth) / 2, height / 2 + suitAscent / 2);
        
        // 
        Font smallFont = new Font(Font.SANS_SERIF, Font.BOLD, 11);
        if (!smallFont.canDisplay(rank.charAt(0))) {
            smallFont = new Font(Font.MONOSPACED, Font.BOLD, 11);
        }
        g2d.setFont(smallFont);
        fm = g2d.getFontMetrics(smallFont);
        int smallAscent = fm.getAscent();
        
        // 
        g2d.drawString(rank, 6, smallAscent + 2);
        
        // 
        Font smallSuitFont = new Font(Font.SERIF, Font.BOLD, 14);
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.BOLD, 14);
            if (testFont.canDisplay(suit.charAt(0))) {
                smallSuitFont = testFont;
                break;
            }
        }
        g2d.setFont(smallSuitFont);
        fm = g2d.getFontMetrics(smallSuitFont);
        g2d.drawString(suit, 6, smallAscent + 16);
        
        // 
        g2d.translate(width, height);
        g2d.rotate(Math.PI);
        g2d.setFont(smallFont);
        g2d.drawString(rank, 6, smallAscent + 2);
        g2d.setFont(smallSuitFont);
        g2d.drawString(suit, 6, smallAscent + 16);
        
        // 
        if (selected) {
            // reset transform
            g2d.setTransform(new AffineTransform());
            // draw selected background
            g2d.setColor(new Color(255, 200, 0, 150));
            g2d.fill(cardShape);
            // draw selected border
            g2d.setColor(new Color(255, 150, 0));
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(cardShape);
        }
        
            // add tooltip
        if (getToolTipText() == null) {
            setToolTipText("Click to select/deselect");
        }
        
        g2d.dispose();
    }
}

