package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Game Start Screen
 */
public class StartScreen extends JFrame {
    
    public StartScreen() {
        setTitle("🃏 Joker Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 25, 40));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Title area
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 40, 0));
        
        // Main title
        JLabel titleLabel = new JLabel("🃏 Joker Card Game", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 220, 150));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Balatro Style", JLabel.CENTER);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Center decoration area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Game description
        JTextArea description = new JTextArea();
        description.setText(
            "🎴 Change scoring rules by combining Joker cards\n" +
            "🎯 Achieve target score within limited rounds\n" +
            "🛒 Get new Jokers or adjust deck in the shop\n" +
            "🏆 Challenge 10 levels and become a Joker master!"
        );
        description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        description.setForeground(new Color(220, 230, 255));
        description.setBackground(new Color(20, 25, 40));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(20, 20, 20, 20));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        centerPanel.add(description, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button area
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        
        // Start game button
        JButton startButton = new JButton("🎮 Start Game");
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.setBackground(new Color(100, 180, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setBorder(new LineBorder(new Color(120, 200, 255), 3, true));
        
        // Add hover effect
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(120, 200, 255));
                startButton.setBorder(new LineBorder(new Color(140, 220, 255), 3, true));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(100, 180, 255));
                startButton.setBorder(new LineBorder(new Color(120, 200, 255), 3, true));
            }
        });
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close start screen and open game window
                dispose();
                SwingUtilities.invokeLater(() -> {
                    GameWindow window = new GameWindow();
                    window.setVisible(true);
                });
            }
        });
        
        buttonPanel.add(startButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            StartScreen startScreen = new StartScreen();
            startScreen.setVisible(true);
        });
    }
}

