package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * score anima
 */
public class ScoreAnimation extends JLabel {
    private int score;
    private float alpha = 1.0f;
    private float scale = 0.5f;
    private Timer animationTimer;
    
    public ScoreAnimation(int score) {
        this.score = score;
        setText("+" + score + " pts");
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        setForeground(new Color(255, 220, 0));
        setHorizontalAlignment(JLabel.CENTER);
        setOpaque(false);
        
        // 
        setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // light
                g2d.setColor(new Color(255, 200, 0, (int)(alpha * 200)));
                for (int i = 0; i < 3; i++) {
                    g2d.drawString(getText(), 3 + i, 3 + i);
                }
                
                // text
                g2d.setColor(new Color(255, 220, 0, (int)(alpha * 255)));
                g2d.drawString(getText(), 2, 2);
                
                g2d.dispose();
            }
        });
        
        startAnimation();
    }
    
    private void startAnimation() {
        int delay = 16; 
        animationTimer = new javax.swing.Timer(delay, e -> {
            scale += 0.05f;
            if (scale > 1.2f) {
                scale = 1.2f;
                
            }
            
           
            if (scale >= 1.2f) {
                alpha -= 0.02f;
            }
            
            if (alpha <= 0) {
                alpha = 0;
                animationTimer.stop();
                // Capture parent once to avoid race where getParent() becomes null after removal
                java.awt.Container parent = getParent();
                if (parent != null) {
                    SwingUtilities.invokeLater(() -> {
                        // Use captured parent reference
                        parent.remove(this);
                        parent.repaint();
                    });
                }
            } else {
                repaint();
            }
        });
        animationTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // opacity
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
        
        // scale and position
        AffineTransform originalTransform = g2d.getTransform();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2d.translate(centerX, centerY);
        g2d.scale(scale, scale);
        g2d.translate(-centerX, -centerY);
        
        // light shadow
        g2d.setColor(new Color(255, 200, 0, 150));
        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    g2d.drawString(text, textX + i, textY + j);
                }
            }
        }
        
                // text
        g2d.setColor(getForeground());
        g2d.drawString(text, textX, textY);
        
        g2d.setTransform(originalTransform);
        g2d.dispose();
    }
}