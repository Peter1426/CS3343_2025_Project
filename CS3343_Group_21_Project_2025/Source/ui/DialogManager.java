package ui;

import model.*;
import data.JokerDeck;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Dialog Manager - Creates and manages various game dialogs
 * Extracted from GameWindow to reduce UI code complexity
 */
public class DialogManager {
    
    // Common styling constants
    private static final Color BACKGROUND_COLOR = new Color(35, 40, 55);
    private static final Color DIALOG_BORDER_COLOR = new Color(120, 130, 170);
    private static final Color TEXT_COLOR = new Color(220, 230, 255);
    private static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    /**
     * Show card selection dialog
     */
    public static Card showCardSelectionDialog(JFrame parent, List<Card> cards, String title, String message) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }

        final Card[] selected = new Card[1];

        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        content.setBackground(new Color(30, 35, 50));

        JLabel header = new JLabel(message, JLabel.CENTER);
        header.setForeground(TEXT_COLOR);
        header.setFont(HEADER_FONT);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        content.add(header, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(0, Math.min(cards.size(), 4), 10, 10));
        cardsPanel.setBackground(new Color(20, 25, 40));
        ButtonGroup group = new ButtonGroup();

        JButton okButton = createStyledButton("OK", new Color(100, 150, 200));
        okButton.setEnabled(false);

        JLabel selectionLabel = new JLabel("No card selected", JLabel.CENTER);
        selectionLabel.setForeground(new Color(180, 200, 255));
        selectionLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 13));
        selectionLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        for (Card card : cards) {
            String color = card.isRed() ? "#ff6b6b" : "#d6e4ff";
            String label = "<html><center><span style='font-size:18px;color:" + color + ";'>" +
                card.toString() + "</span><br/><span style='font-size:11px;color:#9fb3ff;'>" +
                card.getSuit().name() + "</span></center></html>";

            JToggleButton cardButton = new JToggleButton(label);
            cardButton.setFocusPainted(false);
            cardButton.setBackground(new Color(45, 55, 80));
            cardButton.setForeground(Color.WHITE);
            cardButton.setBorder(new LineBorder(new Color(90, 110, 160), 2, true));
            cardButton.setPreferredSize(new Dimension(100, 80));
            cardButton.addActionListener(e -> {
                selected[0] = card;
                okButton.setEnabled(true);
                selectionLabel.setText("Selected: " + card);
            });
            group.add(cardButton);
            cardsPanel.add(cardButton);
        }

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardsPanel, BorderLayout.CENTER);
        centerWrapper.add(selectionLabel, BorderLayout.SOUTH);
        content.add(centerWrapper, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        actions.setBackground(BACKGROUND_COLOR);

        JButton cancelButton = createStyledButton("Cancel", new Color(150, 100, 100));
        cancelButton.addActionListener(e -> {
            selected[0] = null;
            dialog.dispose();
        });

        okButton.addActionListener(e -> dialog.dispose());
        actions.add(cancelButton);
        actions.add(okButton);

        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);

        return selected[0];
    }

    /**
     * Show reward dialog for request/delete actions
     */
    public static void showRewardDialog(JFrame parent, String statusText, 
                                       Runnable onRequestAction, Runnable onDeleteAction) {
        JDialog dialog = new JDialog(parent, "Request / Delete Actions", true);
        dialog.setSize(460, 320);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        content.setBackground(BACKGROUND_COLOR);

        JLabel header = new JLabel("<html><center>Each quota lets you immediately:<br/>" +
            "🂡 Request → Copy one of your current cards<br/>" +
            "🗑️ Delete → Remove a current card and draw a replacement</center></html>",
            JLabel.CENTER);
        header.setForeground(TEXT_COLOR);
        header.setFont(HEADER_FONT);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        content.add(header, BorderLayout.NORTH);

        JLabel quotaInfo = new JLabel(statusText, JLabel.CENTER);
        quotaInfo.setForeground(new Color(255, 220, 140));
        quotaInfo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        quotaInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        content.add(quotaInfo, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttons.setBackground(BACKGROUND_COLOR);

        if (onRequestAction != null) {
            JButton requestButton = createStyledButton("Use Request", new Color(90, 200, 170));
            requestButton.addActionListener(e -> {
                onRequestAction.run();
                dialog.dispose();
            });
            buttons.add(requestButton);
        }

        if (onDeleteAction != null) {
            JButton deleteButton = createStyledButton("Use Delete", new Color(200, 120, 120));
            deleteButton.addActionListener(e -> {
                onDeleteAction.run();
                dialog.dispose();
            });
            buttons.add(deleteButton);
        }

        JButton keepButton = createStyledButton("Save for Later", DIALOG_BORDER_COLOR);
        keepButton.addActionListener(e -> dialog.dispose());
        buttons.add(keepButton);

        content.add(buttons, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }

    /**
     * Show shop dialog with tabs for getting and removing jokers
     */
    public static void showShopDialog(JFrame parent, List<Joker> ownedJokers, 
                                    ActionListener onJokerPurchase, ActionListener onJokerRemove) {
        JDialog shopDialog = new JDialog(parent, "🛒 Card Shop", true);
        shopDialog.setSize(520, 500);
        shopDialog.setLocationRelativeTo(parent);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Get Joker", createJokerShopPanel(shopDialog, ownedJokers, onJokerPurchase));
        tabbedPane.addTab("Remove Joker", createRemoveJokerPanel(ownedJokers, onJokerRemove));

        shopDialog.add(tabbedPane);
        shopDialog.setVisible(true);
    }

    /**
     * Show level targets information dialog
     */
    public static void showLevelTargetsDialog(JFrame parent, int currentLevel, int currentRound, 
                                            int totalScore, int[] levelTargets, int roundsPerLevel) {
        JDialog levelDialog = new JDialog(parent, "Level Goals", true);
        levelDialog.setSize(500, 500);
        levelDialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 85, 120), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(new Color(35, 40, 55));

        // Title
        JLabel titleLabel = new JLabel("🎯 Level Goals (Total " + levelTargets.length + " Levels)", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 220, 150));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(35, 40, 55));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = new JPanel(new GridLayout(0, 4, 5, 5));
        tablePanel.setBackground(new Color(35, 40, 55));

        // Headers
        String[] headers = {"Level", "Target Score", "Rounds", "Status"};
        for (String header : headers) {
            JLabel headerLabel = createStyledLabel(header, HEADER_FONT, new Color(255, 255, 200), new Color(60, 70, 90));
            tablePanel.add(headerLabel);
        }

        // Level data
        for (int i = 1; i <= levelTargets.length; i++) {
            int target = levelTargets[i - 1];
            
            Color bgColor;
            String status;
            if (i == currentLevel) {
                bgColor = new Color(80, 60, 100);
                status = "🎯 In Progress";
            } else if (i < currentLevel) {
                bgColor = new Color(50, 80, 60);
                status = "✅ Completed";
            } else {
                bgColor = new Color(50, 55, 70);
                status = "🔒 Locked";
            }

            tablePanel.add(createStyledLabel("Level " + i, new Font(Font.SANS_SERIF, Font.BOLD, 12), Color.WHITE, bgColor));
            tablePanel.add(createStyledLabel(target + " pts", new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(255, 200, 100), bgColor));
            tablePanel.add(createStyledLabel(roundsPerLevel + " rounds", NORMAL_FONT, new Color(200, 220, 255), bgColor));
            tablePanel.add(createStyledLabel(status, new Font(Font.SANS_SERIF, Font.PLAIN, 11), Color.WHITE, bgColor));
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(new LineBorder(new Color(60, 70, 90), 2));
        scrollPane.setBackground(new Color(35, 40, 55));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom info and close button
        JLabel hintLabel = new JLabel(
            "<html><center>💡 Tip: Each level has " + roundsPerLevel + " rounds<br/>" +
            "Need to achieve target score to advance to next level<br/>" +
            "Current Progress: Level " + currentLevel + " | Round " + currentRound + "<br/>" +
            "Total Score: " + totalScore + " pts</center></html>",
            JLabel.CENTER
        );
        hintLabel.setFont(NORMAL_FONT);
        hintLabel.setForeground(new Color(200, 220, 255));
        hintLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(35, 40, 55));
        JButton closeButton = createStyledButton("Close", new Color(150, 100, 200));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> levelDialog.dispose());
        buttonPanel.add(closeButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(35, 40, 55));
        bottomPanel.add(hintLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        levelDialog.add(mainPanel);
        levelDialog.setVisible(true);
    }

    /**
     * Show hand type rules dialog
     */
    public static void showHandTypeRulesDialog(JFrame parent) {
        JDialog rulesDialog = new JDialog(parent, "Hand Type Scores", true);
        rulesDialog.setSize(650, 550);
        rulesDialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 85, 120), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(new Color(35, 40, 55));

        // Title
        JLabel titleLabel = new JLabel("📊 Hand Type Score Table (Texas Hold'em Rules)", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 220, 150));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(35, 40, 55));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table using GridLayout like level goals
        JPanel tablePanel = new JPanel(new GridLayout(0, 4, 5, 5));
        tablePanel.setBackground(new Color(35, 40, 55));

        // Headers
        String[] headers = {"Rank", "Hand Type", "Base Score", "Description"};
        for (String header : headers) {
            JLabel headerLabel = createStyledLabel(header, HEADER_FONT, new Color(255, 255, 200), new Color(60, 70, 90));
            tablePanel.add(headerLabel);
        }

        // Hand types data
        String[][] handTypesData = {
            {"1", "Royal Flush", "800", "A-K-Q-J-10 in the same suit"},
            {"2", "Straight Flush", "400", "Five consecutive cards of the same suit"},
            {"3", "Four of a Kind", "200", "Four cards with the same rank"},
            {"4", "Full House", "100", "Three of a kind plus a pair"},
            {"5", "Flush", "50", "Five cards of the same suit"},
            {"6", "Straight", "40", "Five consecutive ranks"},
            {"7", "Three of a Kind", "20", "Three cards with the same rank"},
            {"8", "Two Pair", "15", "Two different pairs"},
            {"9", "Pair", "10", "Two cards with the same rank"},
            {"10", "High Card", "0", "High card or mixed ranks"}
        };

        // Add data rows with consistent styling like level goals
        for (String[] rowData : handTypesData) {
            // Rank column
            Color bgColor = new Color(50, 55, 70);  // Default background
            tablePanel.add(createStyledLabel(rowData[0], new Font(Font.SANS_SERIF, Font.BOLD, 12), Color.WHITE, bgColor));
            
            // Hand Type column  
            tablePanel.add(createStyledLabel(rowData[1], new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(255, 220, 150), bgColor));
            
            // Base Score column
            tablePanel.add(createStyledLabel(rowData[2] + " pts", new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(255, 200, 100), bgColor));
            
            // Description column
            tablePanel.add(createStyledLabel(rowData[3], NORMAL_FONT, new Color(200, 220, 255), bgColor));
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(new LineBorder(new Color(60, 70, 90), 2));
        scrollPane.setBackground(new Color(35, 40, 55));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with tip and close button (like level goals)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(35, 40, 55));

        JLabel hintLabel = new JLabel(
            "<html><center>💡 Tip: Actual Score = Base Score × Joker Multiplier<br/>" +
            "Example: Pair (10 pts) × Double Vision (×2) = 20 pts</center></html>",
            JLabel.CENTER
        );
        hintLabel.setFont(NORMAL_FONT);
        hintLabel.setForeground(new Color(200, 220, 255));
        hintLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(35, 40, 55));
        JButton closeButton = createStyledButton("Close", new Color(100, 150, 200));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> rulesDialog.dispose());
        buttonPanel.add(closeButton);
        
        bottomPanel.add(hintLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        rulesDialog.add(mainPanel);
        rulesDialog.setVisible(true);
    }

    // Helper methods
    private static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private static JLabel createStyledLabel(String text, Font font, Color foreground, Color background) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(font);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);
        label.setBorder(new LineBorder(new Color(80, 90, 110), 1));
        return label;
    }

    private static JPanel createJokerShopPanel(JDialog parent, List<Joker> ownedJokers, ActionListener onJokerPurchase) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel desc = new JLabel("Draw up to 3 Joker cards you don't own, choose one to add.", JLabel.CENTER);
        desc.setForeground(TEXT_COLOR);
        desc.setFont(NORMAL_FONT);
        panel.add(desc, BorderLayout.NORTH);

        List<Joker> available = new ArrayList<>(JokerDeck.getAllJokers());
        available.removeIf(joker -> ownedJokers.stream()
            .anyMatch(existing -> existing.getName().equals(joker.getName())));

        if (available.isEmpty()) {
            JLabel label = new JLabel("All Jokers unlocked!", JLabel.CENTER);
            label.setForeground(new Color(255, 220, 140));
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            panel.add(label, BorderLayout.CENTER);
            return panel;
        }

        Collections.shuffle(available, new Random());
        List<Joker> offers = available.subList(0, Math.min(3, available.size()));

        JPanel offerPanel = new JPanel();
        offerPanel.setBackground(BACKGROUND_COLOR);
        offerPanel.setLayout(new BoxLayout(offerPanel, BoxLayout.Y_AXIS));

        for (Joker joker : offers) {
            JPanel card = new JPanel(new BorderLayout(10, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 170, 60), 2, true),
                new EmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(new Color(55, 45, 35));

            JLabel name = new JLabel(joker.getName());
            name.setFont(HEADER_FONT);
            name.setForeground(new Color(255, 220, 150));

            JLabel detail = new JLabel("<html><body style='width:260px'>" + joker.getDescription() + "</body></html>");
            detail.setForeground(Color.WHITE);
            detail.setFont(NORMAL_FONT);

            JButton buyButton = createStyledButton("Get", new Color(90, 180, 140));
            buyButton.setActionCommand(joker.getName());
            buyButton.addActionListener(onJokerPurchase);
            buyButton.addActionListener(e -> parent.dispose());

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

    private static JPanel createRemoveJokerPanel(List<Joker> ownedJokers, ActionListener onJokerRemove) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel desc = new JLabel("Select a Joker you currently own and remove it.", JLabel.CENTER);
        desc.setForeground(TEXT_COLOR);
        desc.setFont(NORMAL_FONT);
        panel.add(desc, BorderLayout.NORTH);

        if (ownedJokers.isEmpty()) {
            JLabel empty = new JLabel("No Jokers available to remove.", JLabel.CENTER);
            empty.setForeground(new Color(255, 200, 140));
            empty.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            panel.add(empty, BorderLayout.CENTER);
            return panel;
        }

        JPanel listPanel = new JPanel();
        listPanel.setBackground(BACKGROUND_COLOR);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (Joker joker : ownedJokers) {
            JPanel cardPanel = new JPanel(new BorderLayout(10, 5));
            cardPanel.setBackground(new Color(45, 35, 35));

            JLabel name = new JLabel(joker.getName());
            name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            name.setForeground(new Color(255, 200, 140));

            JLabel detail = new JLabel("<html><body style='width:260px'>" + joker.getDescription() + "</body></html>");
            detail.setForeground(Color.WHITE);
            detail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

            JButton removeButton = createStyledButton("Remove", new Color(200, 120, 120));
            removeButton.setActionCommand(joker.getName());
            removeButton.addActionListener(onJokerRemove);

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
}