package ui;

import model.*;
import data.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * main
 */
public class GameWindow extends JFrame {
    private static final int TOTAL_LEVELS = 10;  // total 10 levels
    private static final int ROUNDS_PER_LEVEL = 10;  // each level 10 rounds
    private static final int BASE_HAND_SIZE = 5;
    private static final int BASE_DRAW_COUNT = 5;
    
    // each level target score (increasing difficulty)
    private static final int[] LEVEL_TARGETS = {
                50,    // level 1
        100,   // level 2
        150,   // level 3
        200,   // level 4
        250,   // level 5
        300,   // level 6
        400,   // level 7
        500,   // level 8
        650,   // level 9
        800    // level 10
    };
    
    // game data
    private static final int MAX_MANIPULATION_PER_ROUND = 2;
    
    private Hand playerHand;
    private List<Joker> jokers;
    private ScoreCalculator scoreCalculator;
    private int totalScore;
    private int currentLevel;  // current level (1-10)
    private int levelScore;    // current level score
    private int currentRound;  // current round (1-10)
    private List<Card> deck;
    private Set<Card> selectedCards;
    private Random random;
    
    // UI components
    private JPanel mainPanel;
    private JPanel handPanel;
    private JPanel jokerPanel;
    private JPanel infoPanel;
    private JLabel scoreLabel;
    private JLabel roundLabel;
    private JLabel statusLabel;
    private JButton playButton;
    private JButton skipLevelButton;
    private JButton continueButton;
    private JButton discardButton;
    private JTextArea messageArea;
    private JLayeredPane gameLayeredPane;  // for displaying animations
    private int jokersAddedThisLevel;
    private int requestDeleteQuota;
    private JLabel quotaLabel;
    
    public GameWindow() {
        selectedCards = new HashSet<>();
        initializeGame();
        setupUI();
        startLevel();
    }
    
    /**
     * initialize game
     */
    private void initializeGame() {
        random = new Random();
        
        // create deck
        deck = GameLogicManager.createDeck();
        GameLogicManager.shuffleDeck(deck);
        
        // random get 3 jokers
        jokers = JokerDeck.getRandomJokers(3);
        
        // create hand
        int handSize = BASE_HAND_SIZE;
        
                // create score calculator (will apply joker effects)
        scoreCalculator = new ScoreCalculator(jokers);
        handSize = scoreCalculator.getModifiedHandSize(handSize);
        
        playerHand = new Hand(handSize);
        
        // initialize score and level
        totalScore = 0;
        currentLevel = 1;
        levelScore = 0;
        currentRound = 1;
        requestDeleteQuota = 0;
    }
    
    /**
     * UI
     */
    private void setupUI() {
        setTitle("🃏 Joker Card Game (Balatro Style) 🃏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // 
        gameLayeredPane = new JLayeredPane();
        gameLayeredPane.setPreferredSize(new Dimension(1200, 800));
        

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(25, 30, 45));
        
        // top info panel
        infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        //  
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(20, 25, 40));
        
        // 
        jokerPanel = createJokerPanel();
        centerPanel.add(jokerPanel, BorderLayout.WEST);
        
        // 
        handPanel = createHandPanel();
        centerPanel.add(handPanel, BorderLayout.CENTER);
        
        // 
        JPanel controlPanel = createControlPanel();
        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 
        mainPanel.setBounds(0, 0, 1200, 800);
        gameLayeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        gameLayeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 
                mainPanel.setBounds(0, 0, gameLayeredPane.getWidth(), gameLayeredPane.getHeight());
                mainPanel.revalidate();
            }
        });
        
        add(gameLayeredPane);
    }
    
    /**
     * 
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(35, 40, 55));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 85, 120), 2, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        // 
        JPanel infoPanel = new JPanel(new BorderLayout(15, 0));
        infoPanel.setBackground(new Color(35, 40, 55));
        
        // 
        JPanel infoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        infoContainer.setBackground(new Color(35, 40, 55));
        
        JButton shopButton = new JButton("🛒 Shop");
        shopButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        shopButton.setPreferredSize(new Dimension(110, 32));
        shopButton.setBackground(new Color(90, 180, 140));
        shopButton.setForeground(Color.WHITE);
        shopButton.setOpaque(true);
        shopButton.setBorderPainted(false);
        shopButton.setFocusPainted(false);
        shopButton.addActionListener(e -> openShopDialog());
        infoContainer.add(shopButton);
        
        // 
        roundLabel = new JLabel();
        roundLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        roundLabel.setForeground(new Color(240, 250, 255));
        roundLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoContainer.add(roundLabel);
        
        // 
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        scoreLabel.setForeground(new Color(255, 220, 120));
        scoreLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoContainer.add(scoreLabel);
        
        // 
        statusLabel = new JLabel();
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        statusLabel.setForeground(new Color(170, 220, 255));
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoContainer.add(statusLabel);
        
        quotaLabel = new JLabel();
        quotaLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        quotaLabel.setForeground(new Color(200, 210, 255));
        quotaLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoContainer.add(quotaLabel);
        
        infoPanel.add(infoContainer, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(new Color(30, 35, 50));
        
        // 
        JButton rulesButton = new JButton("📋 Hand Types");
        rulesButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        rulesButton.setPreferredSize(new Dimension(110, 32));
        rulesButton.setBackground(new Color(100, 150, 200));
        rulesButton.setForeground(Color.WHITE);
        rulesButton.setOpaque(true);
        rulesButton.setBorderPainted(false);
        rulesButton.setFocusPainted(false);
        rulesButton.addActionListener(e -> showHandTypeRules());
        buttonPanel.add(rulesButton);
        
        
        JButton levelButton = new JButton("🎯 Level Goals");
        levelButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        levelButton.setPreferredSize(new Dimension(110, 32));
        levelButton.setBackground(new Color(150, 100, 200));
        levelButton.setForeground(Color.WHITE);
        levelButton.setOpaque(true);
        levelButton.setBorderPainted(false);
        levelButton.setFocusPainted(false);
        levelButton.addActionListener(e -> showLevelTargets());
        buttonPanel.add(levelButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        updateInfoPanel();
        
        return panel;
    }
    
    /**
     * 
     */
    private JPanel createJokerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(35, 40, 55));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 170, 60), 2, true),
                new EmptyBorder(5, 5, 5, 5)
            ),
            "🃏 Your Jokers",
            0, 0,
            new Font(Font.SANS_SERIF, Font.BOLD, 16),
            new Color(255, 230, 160)
        ));
        panel.setPreferredSize(new Dimension(200, 0));
        
        return panel;
    }
    
    /**
     * 
     */
    private void updateJokerPanel() {
        jokerPanel.removeAll();
        
        for (Joker joker : jokers) {
            JPanel jokerCard = new JPanel();
            jokerCard.setLayout(new BoxLayout(jokerCard, BoxLayout.Y_AXIS));
            jokerCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 170, 60), 2, true),
                new EmptyBorder(8, 8, 8, 8)
            ));
            jokerCard.setBackground(new Color(55, 45, 35));
            jokerCard.setMaximumSize(new Dimension(180, Integer.MAX_VALUE));
            jokerCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel("<html><center>" + joker.getName() + "</center></html>");
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            nameLabel.setForeground(new Color(255, 220, 150));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel descLabel = new JLabel("<html><center><font size='2'>" + joker.getDescription() + "</font></center></html>");
            descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            descLabel.setForeground(Color.WHITE);
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            jokerCard.add(Box.createVerticalStrut(5));
            jokerCard.add(nameLabel);
            jokerCard.add(Box.createVerticalStrut(5));
            jokerCard.add(descLabel);
            jokerCard.add(Box.createVerticalStrut(5));
            
            jokerPanel.add(jokerCard);
            jokerPanel.add(Box.createVerticalStrut(10));
        }
        
        jokerPanel.revalidate();
        jokerPanel.repaint();
    }
    
    /**
     * 
     */
    private JPanel createHandPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 30, 45));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(120, 170, 255), 2, true),
                new EmptyBorder(5, 5, 5, 5)
            ),
            "🎴 Current Hand",
            0, 0,
            new Font(Font.SANS_SERIF, Font.BOLD, 16),
            new Color(180, 220, 255)
        ));
        
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        cardsPanel.setBackground(new Color(25, 30, 45));
        panel.add(cardsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 
     */
    private void updateHandPanel() {
        JPanel cardsPanel = (JPanel) ((BorderLayout) handPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        cardsPanel.removeAll();
        
        List<Card> cards = playerHand.getCards();
        for (Card card : cards) {
            CardPanel cardPanel = new CardPanel(card);
            cardPanel.setSelected(selectedCards.contains(card));
            
            cardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedCards.contains(card)) {
                        selectedCards.remove(card);
                        messageArea.setText("Deselected " + card + ". Continue selecting other cards, then click 「Play」.");
                    } else {
                        selectedCards.add(card);
                        String selectedStr = selectedCards.toString();
                        messageArea.setText("Selected:" + selectedStr + "\nTip: Continue clicking other cards to add, then click 「Play」 button.");
                    }
                    updateHandPanel();
                    updateStatusMessage();
                }
            });
            
            // 
            cardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    cardPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            
            cardsPanel.add(cardPanel);
        }
        
        JLabel handSizeLabel = new JLabel(
            "<html><center><font size='4'>" + cards.size() + " / " + playerHand.getMaxSize() + "</font></center></html>",
            JLabel.CENTER
        );
        handSizeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        handSizeLabel.setForeground(Color.WHITE);
        handSizeLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
        handPanel.revalidate();
        handPanel.repaint();
    }
    
    /**
     * 
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(25, 30, 45));
        
        // 
        playButton = new AnimatedButton(
            "🎴 Play",
            new Color(80, 150, 255),
            new Color(120, 180, 255),
            new Color(60, 120, 200)
        );
        playButton.addActionListener(e -> playSelectedCards());
        panel.add(playButton);
        
        // 
        discardButton = new AnimatedButton(
            "🗑️ Discard",
            new Color(200, 150, 100),
            new Color(230, 170, 120),
            new Color(170, 120, 80)
        );
        discardButton.addActionListener(e -> discardSelectedCards());
        panel.add(discardButton);
        
        // 
        continueButton = new AnimatedButton(
            "▶️ Continue",
            new Color(100, 200, 100),
            new Color(130, 230, 130),
            new Color(80, 170, 80)
        );
        continueButton.setVisible(false);
        continueButton.addActionListener(e -> {
            selectedCards.clear();
            continueButton.setVisible(false);
            playButton.setEnabled(true);
            discardButton.setEnabled(true);
            updateHandPanel();
            statusLabel.setText("Select cards to play");
        });
        panel.add(continueButton);
        
        // 
        skipLevelButton = new AnimatedButton(
            "🏆 Skip Level",
            new Color(200, 150, 50),
            new Color(230, 180, 70),
            new Color(170, 120, 40)
        );
        skipLevelButton.setVisible(false);
        skipLevelButton.setToolTipText("After achieving current level goal, can advance to next level early");
        skipLevelButton.addActionListener(e -> promptSkipLevel());
        panel.add(skipLevelButton);
        
        // 
        messageArea = new JTextArea(3, 50);
        messageArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        messageArea.setBackground(new Color(35, 40, 55));
        messageArea.setForeground(new Color(220, 230, 255));
        messageArea.setEditable(false);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 85, 120), 2, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(600, 90));
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(35, 40, 55));
        panel.add(scrollPane);
        
        return panel;
    }
    

    
    /**
     * 
     */
    private void updateInfoPanel() {
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        roundLabel.setText("📊 Level " + currentLevel + " | Round " + currentRound + " / " + ROUNDS_PER_LEVEL);
        scoreLabel.setText("💎 Total: " + totalScore + " | Level: " + levelScore + " / " + currentTarget);
        
        // 
        double progress = Math.min(100.0, (levelScore * 100.0 / currentTarget));
        
        if (levelScore >= currentTarget) {
            if (currentLevel < TOTAL_LEVELS) {
                statusLabel.setText("🎉 Level goal achieved! You can skip level or continue playing");
            } else {
                statusLabel.setText("🎉 Level goal achieved! Final level");
            }
            statusLabel.setForeground(new Color(255, 220, 120));
        } else {
            int remaining = currentTarget - levelScore;
            int remainingRounds = ROUNDS_PER_LEVEL - currentRound + 1;
            statusLabel.setText("🎯 Need " + remaining + " pts | Remaining " + remainingRounds + " rounds | Progress: " + 
                String.format("%.1f", progress) + "%");
            statusLabel.setForeground(new Color(170, 220, 255));
        }
        
        if (quotaLabel != null) {
            quotaLabel.setText("🎁 Quota: " + requestDeleteQuota + " | Hand: " + playerHand.size() + " / " + playerHand.getMaxSize());
        }
    }
    
   
  
    private void startLevel() {
        levelScore = 0;  // Reset level score
        currentRound = 1;  // Start from round 1
        jokersAddedThisLevel = 0;
        
        //  
        scoreCalculator = new ScoreCalculator(jokers);
        
        // 
        int handSize = scoreCalculator.getModifiedHandSize(BASE_HAND_SIZE);
        playerHand.setMaxSize(handSize);
        playerHand.clear();
        
        if (skipLevelButton != null) {
            skipLevelButton.setVisible(false);
        }
        
        updateJokerPanel();
        
        startRound();
    }
    
    /**
     * start
     */
    private void startRound() {
        scoreCalculator.resetRound();
        selectedCards.clear();
        
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        
        // draw
        int currentHandSize = playerHand.size();
        int handMaxSize = playerHand.getMaxSize();
        
        // 
        if (currentHandSize < BASE_HAND_SIZE) {
            // Only draw up to bring the hand toward the BASE_HAND_SIZE (5), not to the max.
            int needToDraw = BASE_HAND_SIZE - currentHandSize;
            int drawCount = scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT);
            int actualDraw = Math.min(needToDraw, drawCount);

            if (actualDraw > 0) {
                GameLogicManager.drawCards(deck, playerHand, actualDraw);
                messageArea.setText("🎴 Level " + currentLevel + " | Round " + currentRound + " begins!\n" +
                    "Level Target: " + currentTarget + " pts | Current Level Score: " + levelScore + " pts\n" +
                    "Hand: " + currentHandSize + " → " + playerHand.size() + " / " + handMaxSize + 
                    "\nDrew " + actualDraw + " cards\n\n" +
                    "📌 Instructions:\n1. Click cards to select\n2. 「Play」- Play selected cards to score\n3. 「Discard」- Discard selected cards (each discarded card refills immediately; unlimited times per round)\n4. After finishing a level, spend Request/Delete quota to modify current hand before the next level\n5. After achieving goal, 「Skip Level」 button will unlock to advance early" +
                    "\nQuota available: " + requestDeleteQuota);
            }
        } else {
            // If player already has >= BASE_HAND_SIZE, do not auto-draw this round (per rule)
            messageArea.setText("🎴 Level " + currentLevel + " | Round " + currentRound + " begins!\n" +
                "Level Target: " + currentTarget + " pts | Current Level Score: " + levelScore + " pts\n" +
                "Current Hand: " + currentHandSize + " / " + handMaxSize + " (5+ cards, no auto-draw)\n\n" +
                "📌 Instructions:\n1. Click cards to select\n2. 「Play」- Play selected cards to score\n3. 「Discard」- Discard selected cards (each discarded card refills immediately; unlimited times per round)\n4. After finishing a level, spend Request/Delete quota to modify current hand before the next level\n5. After achieving goal, 「Skip Level」 button will unlock to advance early" +
                "\nQuota available: " + requestDeleteQuota);
        }
        
        updateJokerPanel();
        updateHandPanel();
        updateInfoPanel();
        
        playButton.setEnabled(true);
        discardButton.setEnabled(true);
        
        statusLabel.setText("Choose the card to play.");
    }
    

    
    /**
     * 
     */
    private void updateStatusMessage() {
        if (selectedCards.isEmpty()) {
            statusLabel.setText("Select cards to play");
        } else {
            statusLabel.setText("Selected " + selectedCards.size() + " cards - Click 「Play」 button");
        }
    }
    
    /**
     *  
     */
    private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            messageArea.setText("❌ Please select cards to play first!\n\n📌 Instructions:\n1. Click cards to select (selected cards will have yellow border)\n2. Select at least a pair (two cards of the same rank)\n3. Then click 「Play」 button");
            return;
        }
        
        List<Card> cardsToPlay = new ArrayList<>(selectedCards);
        
        //  
        if (!scoreCalculator.canPlayHand(cardsToPlay)) {
            HandEvaluator.HandType type = HandEvaluator.evaluateHand(cardsToPlay);
            
            //  
            String analysis = GameLogicManager.analyzeHand(cardsToPlay);
            
            messageArea.setText("❌ These cards cannot be played!\n\nCurrent Hand Type: " + type.getName() + "\n" + 
                analysis + "\n\n💡 Strategy Tips:\n" +
                "1. If current hand cannot form a valid hand type, click 「Discard」 to get new cards\n" +
                "2. Or wait for the next round");
            return;
        }
        
        //  
        int score = scoreCalculator.calculateScore(cardsToPlay);
        HandEvaluator.HandType handType = HandEvaluator.evaluateHand(cardsToPlay);
        
        //  
        if (score == 0) {
            messageArea.setText("❌ These cards cannot score!\n\n💡 Tip: At least a pair is needed to score.\nFor example: Select two cards of the same rank (e.g., two Aces, two Kings, etc.)");
            return;
        }
        
        //  
        showScoreAnimation(score);
        
        // 
        String message = String.format(
            "✅ Played: %s\n🎴 Hand Type: %s\n💎 Score: %d pts\n📊 Total: %d",
            cardsToPlay,
            handType.getName(),
            score,
            totalScore + score
        );
        
        // 
        playerHand.removeCards(cardsToPlay);
        selectedCards.clear();
        
        // 
        int cardsPlayed = cardsToPlay.size();
        int currentHandSize = playerHand.size();
        
        // After playing, only auto-refill when the player's hand size is below the BASE hand size (5).
        // This respects the rule: if a player used Request and temporarily has >5 cards, playing
        // cards that still leaves them >=5 should NOT trigger additional draws. Only when hand
        // size falls below BASE_HAND_SIZE should the system draw up to bring it back toward BASE_HAND_SIZE.
        if (currentHandSize < BASE_HAND_SIZE) {
            // How many cards to draw to reach the base hand size
            int needToDraw = BASE_HAND_SIZE - currentHandSize;
            // But never draw more than number of cards played in this action (common refill rule)
            needToDraw = Math.min(needToDraw, cardsPlayed);
            // Also respect any joker-modified per-round draw limit
            int drawCountLimit = scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT);
            needToDraw = Math.min(needToDraw, drawCountLimit);

            if (needToDraw > 0) {
                GameLogicManager.drawCards(deck, playerHand, needToDraw);
                message += "\nRefilled with " + needToDraw + " card(s)";
            }
        }
        
        
        totalScore += score;
        levelScore += score;
        updateInfoPanel();
        updateHandPanel();
        showSkipLevelButtonIfEligible();
        
        // Check if level 10 is completed immediately when target is reached
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        if (currentLevel == TOTAL_LEVELS && levelScore >= currentTarget) {
            // Level 10 completed, evaluate immediately
            messageArea.setText(message + "\n\n🎉 Level 10 target achieved! Evaluating level...");
            javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
                evaluateLevel();
            });
            timer.setRepeats(false);
            timer.start();
            return;
        }
        
        messageArea.setText(message + "\n\n✅ Play completed, entering next round.");
        
        
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            endRound();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * 
     */
    private void discardSelectedCards() {
        if (selectedCards.isEmpty()) {
            messageArea.setText("❌ Please select cards to discard first!\n\n📌 Tip: Click cards to select cards to discard, then click 「Discard」 button.");
            return;
        }
        
        List<Card> cardsToDiscard = new ArrayList<>(selectedCards);
        
        // 
        String discardInfo = "Discard the selected cards?\n\n";
        discardInfo += "Cards to remove: " + cardsToDiscard + "\n";
        
        int result = JOptionPane.showConfirmDialog(
            this,
            discardInfo,
            "Discard Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        // 
        playerHand.removeCards(cardsToDiscard);
        selectedCards.clear();
        for (Card card : cardsToDiscard) {
            deck.add(card);  // 
        }
        GameLogicManager.shuffleDeck(deck);  // redraw
        
        int discardedCount = cardsToDiscard.size();
        int handSizeBeforeDraw = playerHand.size();
        int refilled = 0;
        // Only refill from deck if player's current hand is below BASE_HAND_SIZE
        if (handSizeBeforeDraw < BASE_HAND_SIZE) {
            int need = Math.min(BASE_HAND_SIZE - handSizeBeforeDraw, discardedCount);
            need = Math.min(need, scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT));
            if (need > 0) {
                GameLogicManager.drawCards(deck, playerHand, need);
                refilled = playerHand.size() - handSizeBeforeDraw;
            }
        }
        
        messageArea.setText("✅ Discarded " + discardedCount + " cards and refilled " + refilled + " card(s).\n" +
            "Current Hand: " + playerHand.size() + " / " + playerHand.getMaxSize() +
            "\n(Discarded cards are returned to the deck, so the deck size stays the same)");
        
        updateHandPanel();
        updateStatusMessage();
    }
    
    /**
     * 
     */
    private boolean canFormValidHand(List<Card> cards) {
        if (cards == null || cards.size() < 2) {
            return false;
        }
        
        // 
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            rankCount.put(card.getValue(), rankCount.getOrDefault(card.getValue(), 0) + 1);
        }
        
        // 
        boolean hasPair = rankCount.values().stream().anyMatch(count -> count >= 2);
        
        // If Flower Child joker is active, clubs can act as wild cards
        boolean hasFlowerChild = jokers.stream()
            .anyMatch(j -> j.getName().contains("Flower Child"));
        
        if (hasFlowerChild) {
            boolean hasClubs = cards.stream().anyMatch(Card::isClubs);
            if (hasClubs) {
                // 
                return true;
            }
        }
        
        return hasPair || HandEvaluator.isValidHand(cards);
    }
    

    
    /**
     * 
     */
    private void showScoreAnimation(int score) {
        ScoreAnimation animation = new ScoreAnimation(score);
        animation.setBounds(500, 250, 200, 100);
        gameLayeredPane.add(animation, JLayeredPane.POPUP_LAYER);
        gameLayeredPane.revalidate();
        gameLayeredPane.repaint();
    }
    
    /**
     * 
     */
    private void endRound() {
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        
                    // 
        if (currentRound < ROUNDS_PER_LEVEL) {
            // 
            currentRound++;
            updateInfoPanel();  // immediately update UI to show new round number
            messageArea.setText("🎴 Entering Round " + currentRound + "...\n" +
                "Level Score: " + levelScore + " / " + currentTarget + "\n" +
                "Remaining Rounds: " + (ROUNDS_PER_LEVEL - currentRound + 1));
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                startRound();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // 
            evaluateLevel();
        }
    }
    
    /**
     * 
     */
    private void evaluateLevel() {
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        
        // 
        if (levelScore >= currentTarget) {
            String dialogTitle;
            String dialogMessage;
            if (currentLevel >= TOTAL_LEVELS) {
                dialogTitle = "Game Complete";
                dialogMessage = "🎉 Congratulations! Completed all levels!\n\nTotal Score: " + totalScore;
            } else {
                int nextLevel = currentLevel + 1;
                int nextTarget = LEVEL_TARGETS[nextLevel - 1];
                dialogTitle = "Level Complete";
                dialogMessage = "🎉 Level " + currentLevel + " Complete!\n\n" +
                    "Current Level Score: " + levelScore + " / " + currentTarget + "\n" +
                    "Next Level Target: " + nextTarget + " pts";
            }
            completeLevel(dialogTitle, dialogMessage);
                } else {
            if (skipLevelButton != null) {
                skipLevelButton.setVisible(false);
            }
            JOptionPane.showMessageDialog(
                this,
                "❌ Unfortunately, Level " + currentLevel + " target not achieved (need " + currentTarget + " pts).\n" +
                "Actual Score: " + levelScore + " pts\nGame Over.",
                "Challenge Failed",
                JOptionPane.ERROR_MESSAGE
            );
                    endGame();
                }
            }
    
    private void completeLevel(String dialogTitle, String dialogMessage) {
        if (skipLevelButton != null) {
            skipLevelButton.setVisible(false);
        }
        requestDeleteQuota++;
        updateInfoPanel();
        
        JOptionPane.showMessageDialog(
            this,
            dialogMessage,
            dialogTitle,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (currentLevel >= TOTAL_LEVELS) {
            endGame();
            return;
        }
        
        currentLevel++;
        startLevel();
        openRewardDialog();
    }
    
    private void showSkipLevelButtonIfEligible() {
        if (skipLevelButton != null && currentLevel < TOTAL_LEVELS && levelScore >= GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS)) {
            skipLevelButton.setVisible(true);
        }
    }
    
    private void promptSkipLevel() {
        if (currentLevel >= TOTAL_LEVELS) {
            JOptionPane.showMessageDialog(
                this,
                "This is the final level. Skipping is unavailable.",
                "Cannot Skip",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int currentTarget = GameLogicManager.getCurrentLevelTarget(currentLevel, LEVEL_TARGETS);
        int nextLevel = currentLevel + 1;
        int nextTarget = LEVEL_TARGETS[nextLevel - 1];
        int choice = JOptionPane.showConfirmDialog(
            this,
            "🎯 Level " + currentLevel + " target achieved (" + levelScore + " / " + currentTarget + " pts).\n" +
            "Skip to Level " + nextLevel + " early?\nNext Level Target: " + nextTarget + " pts.",
            "Skip Level",
                JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            jumpToNextLevel("🏆 Level " + currentLevel + " skipped early!");
        }
    }
    
    private void jumpToNextLevel(String promptMessage) {
        if (currentLevel >= TOTAL_LEVELS) {
            endGame();
            return;
        }
        int nextLevel = currentLevel + 1;
        int nextTarget = LEVEL_TARGETS[nextLevel - 1];
        messageArea.setText(promptMessage + "\nPreparing Level " + nextLevel + "...");
        String dialogMessage = promptMessage + "\n\nNext Level Target: " + nextTarget + " pts";
        completeLevel("Level Skipped", dialogMessage);
    }
    
    private void openRewardDialog() {
        if (requestDeleteQuota <= 0 || currentLevel > TOTAL_LEVELS) {
            return;
        }
        
        JDialog dialog = new JDialog(this, "Request / Delete Actions", true);
        dialog.setSize(460, 320);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        content.setBackground(new Color(35, 40, 55));
        
        JLabel header = new JLabel("<html><center>Each quota lets you immediately:<br/>" +
            "🂡 Request → Copy one of your current cards<br/>" +
            "🗑️ Delete → Remove a current card and draw a replacement</center></html>",
            JLabel.CENTER);
        header.setForeground(new Color(220, 230, 255));
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        content.add(header, BorderLayout.NORTH);
        
        JLabel quotaInfo = new JLabel(buildRewardStatusText(), JLabel.CENTER);
        quotaInfo.setForeground(new Color(255, 220, 140));
        quotaInfo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        quotaInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        content.add(quotaInfo, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttons.setBackground(new Color(35, 40, 55));
        
        JButton requestButton = new JButton("Use Request");
        requestButton.setBackground(new Color(90, 200, 170));
        requestButton.setForeground(Color.WHITE);
        requestButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        requestButton.setOpaque(true);
        requestButton.setBorderPainted(false);
        requestButton.setFocusPainted(false);
        requestButton.addActionListener(e -> {
            if (requestDeleteQuota <= 0) {
                JOptionPane.showMessageDialog(dialog, "No quota remaining.", "Request", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (performRequestReward()) {
                requestDeleteQuota--;
                updateInfoPanel();
                quotaInfo.setText(buildRewardStatusText());
            }
        });
        buttons.add(requestButton);
        
        JButton deleteButton = new JButton("Use Delete");
        deleteButton.setBackground(new Color(200, 120, 120));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            if (requestDeleteQuota <= 0) {
                JOptionPane.showMessageDialog(dialog, "No quota remaining.", "Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (performDeleteReward()) {
                requestDeleteQuota--;
                updateInfoPanel();
                quotaInfo.setText(buildRewardStatusText());
            }
        });
        buttons.add(deleteButton);
        
        JButton keepButton = new JButton("Save for Later");
        keepButton.setBackground(new Color(120, 130, 170));
        keepButton.setForeground(Color.WHITE);
        keepButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        keepButton.setOpaque(true);
        keepButton.setBorderPainted(false);
        keepButton.setFocusPainted(false);
        keepButton.addActionListener(e -> dialog.dispose());
        buttons.add(keepButton);
        
        content.add(buttons, BorderLayout.SOUTH);
        
        dialog.add(content);
        dialog.setVisible(true);
    }
    
    private String buildRewardStatusText() {
        return "Remaining quota: " + requestDeleteQuota + " | Hand: " + playerHand.size() + " / " + playerHand.getMaxSize();
    }
    
    private boolean performRequestReward() {
        List<Card> cards = playerHand.getCards();
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Current hand is empty, cannot copy a card.",
                "Request Card",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        
        Card selected = showCardSelectionDialog(
            cards,
            "Request Card",
            "Select one card to copy. A duplicate will be added to your current hand immediately."
        );
        
        if (selected == null) {
            return false;
        }
        
        Card copy = new Card(selected.getSuit(), selected.getRank());
        playerHand.forceAddCard(copy);
        if (playerHand.size() > playerHand.getMaxSize()) {
            playerHand.setMaxSize(playerHand.size());
        }
        
        messageArea.setText("🂡 Request: Copied " + selected + " into your hand.");
        updateHandPanel();
        return true;
    }
    
    private boolean performDeleteReward() {
        List<Card> cards = playerHand.getCards();
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Current hand is empty, cannot delete a card.",
                "Delete Card",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        
        Card selected = showCardSelectionDialog(
            cards,
            "Delete Card",
            "Select one card to delete. It will be removed now and immediately replaced with the top card of the deck."
        );
        
        if (selected == null) {
            return false;
        }
        
        playerHand.removeCards(Collections.singletonList(selected));
        selectedCards.remove(selected);
        // Only draw replacement if hand size is now below BASE_HAND_SIZE
        if (playerHand.size() < BASE_HAND_SIZE) {
            int drawLimit = scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT);
            int toDraw = Math.min(1, drawLimit);
            if (toDraw > 0) {
                GameLogicManager.drawCards(deck, playerHand, toDraw);
                messageArea.setText("🗑️ Delete: Removed " + selected + " and drew a replacement.");
            } else {
                messageArea.setText("🗑️ Delete: Removed " + selected + " (no replacement due to draw limit)");
            }
        } else {
            messageArea.setText("🗑️ Delete: Removed " + selected + " (no replacement because hand has 5+ cards)");
        }
        
        updateHandPanel();
        return true;
    }
    
    /**
     * 
     */
    private void openShopDialog() {
        JDialog shopDialog = new JDialog(this, "🛒 Card Shop", true);
        shopDialog.setSize(520, 500);
        shopDialog.setLocationRelativeTo(this);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Get Joker", createJokerShopPanel(shopDialog));
        tabbedPane.addTab("Remove Joker", createRemoveJokerPanel());
        
        shopDialog.add(tabbedPane);
        shopDialog.setVisible(true);
    }
    
    private JPanel createJokerShopPanel(JDialog parent) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(35, 40, 55));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel desc = new JLabel("Draw up to 3 Joker cards you don't own, choose one to add.", JLabel.CENTER);
        desc.setForeground(new Color(220, 230, 255));
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        panel.add(desc, BorderLayout.NORTH);
        
        List<Joker> available = new ArrayList<>(JokerDeck.getAllJokers());
        available.removeIf(joker -> jokers.stream()
            .anyMatch(existing -> existing.getName().equals(joker.getName())));
        
        if (available.isEmpty()) {
            JLabel label = new JLabel("All Jokers unlocked!", JLabel.CENTER);
            label.setForeground(new Color(255, 220, 140));
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            panel.add(label, BorderLayout.CENTER);
            return panel;
        }
        
        Collections.shuffle(available, random);
        List<Joker> offers = available.subList(0, Math.min(3, available.size()));
        
        JPanel offerPanel = new JPanel();
        offerPanel.setBackground(new Color(35, 40, 55));
        offerPanel.setLayout(new BoxLayout(offerPanel, BoxLayout.Y_AXIS));
        
        for (Joker joker : offers) {
            JPanel card = new JPanel(new BorderLayout(10, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 170, 60), 2, true),
                new EmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(new Color(55, 45, 35));
            
            JLabel name = new JLabel(joker.getName());
            name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            name.setForeground(new Color(255, 220, 150));
            
            JLabel detail = new JLabel("<html><body style='width:260px'>" + joker.getDescription() + "</body></html>");
            detail.setForeground(Color.WHITE);
            detail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            
            JButton buyButton = new JButton("Get");
            buyButton.setBackground(new Color(90, 180, 140));
            buyButton.setForeground(Color.WHITE);
            buyButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            buyButton.setOpaque(true);
            buyButton.setBorderPainted(false);
            buyButton.addActionListener(e -> {
                addJokerFromShop(joker);
                parent.dispose();
            });
            
            card.add(name, BorderLayout.NORTH);
            card.add(detail, BorderLayout.CENTER);
            card.add(buyButton, BorderLayout.EAST);
            
            offerPanel.add(card);
            offerPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(offerPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createRemoveJokerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(35, 40, 55));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel desc = new JLabel("Select a Joker you currently own and remove it.", JLabel.CENTER);
        desc.setForeground(new Color(220, 230, 255));
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        panel.add(desc, BorderLayout.NORTH);
        
        if (jokers.isEmpty()) {
            JLabel empty = new JLabel("No Jokers available to remove.", JLabel.CENTER);
            empty.setForeground(new Color(255, 200, 140));
            empty.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            panel.add(empty, BorderLayout.CENTER);
            return panel;
        }
        
        JPanel listPanel = new JPanel();
        listPanel.setBackground(new Color(35, 40, 55));
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        for (Joker joker : jokers) {
            JPanel cardPanel = new JPanel(new BorderLayout(10, 5));
            cardPanel.setBackground(new Color(45, 35, 35));
            
            JLabel name = new JLabel(joker.getName());
            name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            name.setForeground(new Color(255, 200, 140));
            
            JLabel detail = new JLabel("<html><body style='width:260px'>" + joker.getDescription() + "</body></html>");
            detail.setForeground(Color.WHITE);
            detail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            
            JButton removeButton = new JButton("Remove");
            removeButton.setBackground(new Color(200, 120, 120));
            removeButton.setForeground(Color.WHITE);
            removeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            removeButton.setOpaque(true);
            removeButton.setBorderPainted(false);
            removeButton.addActionListener(e -> {
                removeJoker(joker);
            });
            
            cardPanel.add(name, BorderLayout.NORTH);
            cardPanel.add(detail, BorderLayout.CENTER);
            cardPanel.add(removeButton, BorderLayout.EAST);
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 120, 120), 1, true),
                new EmptyBorder(10, 10, 10, 10)
            ));
            
            listPanel.add(cardPanel);
            listPanel.add(Box.createVerticalStrut(8));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void addJokerFromShop(Joker newJoker) {
        if (jokersAddedThisLevel >= 1) {
            JOptionPane.showMessageDialog(
                this,
                "Already purchased a Joker this level, cannot buy more this level.",
                "Purchase Limit Reached",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        jokersAddedThisLevel++;
        jokers.add(newJoker);
        scoreCalculator.updateJokers(jokers);
        refreshHandConfiguration();
        updateJokerPanel();
        updateInfoPanel();
        messageArea.setText("🛒 Shop: Obtained new Joker - " + newJoker.getName());
        JOptionPane.showMessageDialog(
            this,
            "Obtained Joker: " + newJoker.getName(),
            "Purchase Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void removeJoker(Joker joker) {
        if (!jokers.remove(joker)) {
            JOptionPane.showMessageDialog(
                this,
                "Cannot remove Joker: " + joker.getName(),
                "Remove Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        scoreCalculator.updateJokers(jokers);
        refreshHandConfiguration();
        updateJokerPanel();
        updateInfoPanel();
        showSkipLevelButtonIfEligible();
        messageArea.setText("🛒 Shop: Removed Joker - " + joker.getName());
        
        JOptionPane.showMessageDialog(
            this,
            "Removed Joker: " + joker.getName(),
            "Remove Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void refreshHandConfiguration() {
        if (playerHand == null || scoreCalculator == null) {
            return;
        }
        int desiredMax = scoreCalculator.getModifiedHandSize(BASE_HAND_SIZE);
        int previousMax = playerHand.getMaxSize();
        int newMax = Math.max(desiredMax, playerHand.size());
        playerHand.setMaxSize(newMax);
        if (newMax > previousMax) {
            // Only auto-draw if player's current hand is below BASE_HAND_SIZE
            if (playerHand.size() < BASE_HAND_SIZE) {
                int needToDraw = Math.min(newMax - playerHand.size(), scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT));
                // But don't draw beyond bringing hand to BASE_HAND_SIZE
                needToDraw = Math.min(needToDraw, BASE_HAND_SIZE - playerHand.size());
                if (needToDraw > 0) {
                    GameLogicManager.drawCards(deck, playerHand, needToDraw);
                }
            }
        }
        updateHandPanel();
    }
    
    /**
     * 
     */
    private void showLevelTargets() {
        DialogManager.showLevelTargetsDialog(this, currentLevel, currentRound, totalScore, LEVEL_TARGETS, ROUNDS_PER_LEVEL);
    }
    
    /**
     * 
     */
    private void endGame() {
        boolean allLevelsCompleted = currentLevel > TOTAL_LEVELS || 
            (currentLevel == TOTAL_LEVELS && levelScore >= LEVEL_TARGETS[TOTAL_LEVELS - 1]);
        
        String message = "Game Over!\n\n";
        message += "Levels Completed: " + (currentLevel - (allLevelsCompleted ? 0 : 1)) + " / " + TOTAL_LEVELS + "\n";
        message += "Final Total Score: " + totalScore + " pts\n\n";
        
        if (allLevelsCompleted) {
            message += "🎉 Congratulations! Completed all levels!";
        } else {
            message += "Keep trying, good luck!";
        }
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        int restart = JOptionPane.showConfirmDialog(
            this,
            "Restart the game?",
            "Play Again",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (restart == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
        } else {
            System.exit(0);
        }
    }
    
    /**
     * 
     */
    private void showHandTypeRules() {
        DialogManager.showHandTypeRulesDialog(this);
    }
    

    

    
    private Card showCardSelectionDialog(List<Card> cards, String title, String message) {
        return DialogManager.showCardSelectionDialog(this, cards, title, message);
    }
}
