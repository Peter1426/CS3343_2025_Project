package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * button with hover animation
 */
public class AnimatedButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color currentColor;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        updateColor();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = color;
    }
    
    public AnimatedButton(String text, Color normalColor, Color hoverColor, Color pressedColor) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
        this.currentColor = normalColor;
        
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        setPreferredSize(new Dimension(130, 45));
        setForeground(Color.WHITE);
        setOpaque(true);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        
        // add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                updateColor();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                updateColor();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                updateColor();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                updateColor();
            }
        });
    }
    
    private void updateColor() {
        if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        } else {
            currentColor = normalColor;
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, currentColor,
            0, getHeight(), 
            new Color(
                Math.max(0, currentColor.getRed() - 30),
                Math.max(0, currentColor.getGreen() - 30),
                Math.max(0, currentColor.getBlue() - 30)
            )
        );
        g2d.setPaint(gradient);
        
        
        int arc = 8;
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        
        // draw highlight   
        if (isHovered && !isPressed) {
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, arc, arc);
        }
        
        // draw border
        g2d.setColor(new Color(
            Math.max(0, currentColor.getRed() - 40),
            Math.max(0, currentColor.getGreen() - 40),
            Math.max(0, currentColor.getBlue() - 40)
        ));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
        
        // draw text
        g2d.setColor(getForeground());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(getText(), textX, textY);
        
        g2d.dispose();
    }
}

