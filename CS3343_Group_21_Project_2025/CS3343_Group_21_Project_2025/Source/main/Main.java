package main;

import model.*;
import data.*;
import service.*;
import ui.*;
import javax.swing.*;
import java.util.*;

/**
 * Joker Card Game Main Program
 */
public class Main {
    private static final int TOTAL_ROUNDS = 5;
    private static final int BASE_HAND_SIZE = 5;
    private static final int BASE_DRAW_COUNT = 5;
    private static final int WIN_SCORE = 800;
    private static final int SPECIAL_ACTION_LIMIT = 2;
    
    private Hand playerHand;
    private List<Joker> jokers;
    private ScoreCalculator scoreCalculator;
    private int totalScore;
    private int currentRound;
    private List<Card> deck;
    private Scanner scanner;
    private Random random;
    private boolean jokerPurchasedThisRound;
    private int remainingSpecialActions;
    
    public static void main(String[] args) {
        // Check command line arguments, if --cli then start CLI version
        if (args.length > 0 && args[0].equals("--cli")) {
            Main game = new Main();
            game.start();
        } else {
            // Default to GUI version - show start screen first
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
    
    public void start() {
        scanner = new Scanner(System.in);
        random = new Random();
        
        System.out.println("========================================");
        System.out.println("    🃏 Joker Card Game (Balatro Style) 🃏");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize game
        initializeGame();
        
        // Display jokers
        displayJokers();
        
        // Main game loop
        while (currentRound <= TOTAL_ROUNDS) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Round " + currentRound + " / Total " + TOTAL_ROUNDS);
            System.out.println("Current Total Score: " + totalScore);
            System.out.println("Target Score: " + WIN_SCORE);
            System.out.println("=".repeat(50));
            
            playRound();
            
            if (totalScore >= WIN_SCORE) {
                System.out.println("\n🎉 Congratulations! You achieved the target score of " + WIN_SCORE + " points!");
                break;
            }
            
            currentRound++;
        }
        
        // Game over
        endGame();
        scanner.close();
    }
    
    /**
     * Initialize game
     */
    private void initializeGame() {
        // Create deck
        deck = createDeck();
        shuffleDeck();
        
        // Randomly get 3 joker cards
        jokers = JokerDeck.getRandomJokers(3);
        
        // Create hand
        int handSize = BASE_HAND_SIZE;
        
        // Create score calculator (will apply joker effects)
        scoreCalculator = new ScoreCalculator(jokers);
        handSize = scoreCalculator.getModifiedHandSize(handSize);
        
        playerHand = new Hand(handSize);
        
        // Initialize score and round
        totalScore = 0;
        currentRound = 1;
        jokerPurchasedThisRound = false;
        
        // Draw initial hand
        drawCards(BASE_DRAW_COUNT);
    }
    
    /**
     * Create standard 52-card deck
     */
    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }
    
    /**
     * Shuffle deck
     */
    private void shuffleDeck() {
        Collections.shuffle(deck);
    }
    
    /**
     * Draw specified number of cards
     */
    private void drawCards(int count) {
        int drawCount = scoreCalculator.getModifiedDrawCount(count);
        int drawn = 0;
        
        System.out.println("\nDrawing " + drawCount + " cards...");
        
        for (int i = 0; i < drawCount && !deck.isEmpty(); i++) {
            if (!playerHand.isFull()) {
                Card card = deck.remove(0);
                playerHand.addCard(card);
                drawn++;
            }
        }
        
        if (drawn < drawCount && deck.isEmpty()) {
            // Deck exhausted, reshuffle
            System.out.println("Deck exhausted, reshuffling!");
            deck = createDeck();
            shuffleDeck();
            
            // Continue drawing
            for (int i = drawn; i < drawCount && !deck.isEmpty(); i++) {
                if (!playerHand.isFull()) {
                    Card card = deck.remove(0);
                    playerHand.addCard(card);
                }
            }
        }
    }
    
    /**
     * Play one round
     */
    private void playRound() {
        jokerPurchasedThisRound = false;
        remainingSpecialActions = SPECIAL_ACTION_LIMIT;
        scoreCalculator.resetRound();
        
        // Draw cards at start of each round (fill to hand limit)
        if (playerHand.size() < playerHand.getMaxSize()) {
            int needToDraw = Math.min(
                playerHand.getMaxSize() - playerHand.size(),
                scoreCalculator.getModifiedDrawCount(BASE_DRAW_COUNT)
            );
            drawCards(needToDraw);
        }
        
        while (true) {
            // Display hand
            displayHand();
            
            System.out.println("\nPlease select an action:");
            System.out.println("1. Select cards to play");
            System.out.println("2. Skip this round");
            System.out.println("3. Enter shop");
            System.out.println("4. Request cards (duplicate selected cards, max 2)");
            System.out.println("5. Discard cards (permanently remove selected cards)");
            System.out.println("Remaining special action quota: " + remainingSpecialActions + " cards");
            System.out.print("Please enter option (1-5): ");
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("2")) {
                System.out.println("You skipped this round.");
                break;
            } else if (choice.equals("3")) {
                enterShop();
                continue;
            } else if (choice.equals("4")) {
                handleRequestCards();
                continue;
            } else if (choice.equals("5")) {
                handleDiscardCards();
                continue;
            } else if (choice.equals("1")) {
                if (playCards()) {
                    // Successfully played cards, continue
                    if (playerHand.isEmpty()) {
                        System.out.println("Hand exhausted, round ended.");
                        break;
                    }
                } else {
                    // Failed to play cards, retry
                    continue;
                }
            } else {
                System.out.println("Invalid option, please choose again.");
                continue;
            }
            
            // Ask if continue playing cards
            if (!playerHand.isEmpty()) {
                System.out.print("\nContinue playing cards? (y/n): ");
                String continueChoice = scanner.nextLine().trim().toLowerCase();
                if (!continueChoice.equals("y")) {
                    break;
                }
            }
        }
    }
    
    /**
     * Display hand
     */
    private void displayHand() {
        List<Card> cards = playerHand.getCards();
        System.out.println("\nCurrent Hand (" + cards.size() + "/" + playerHand.getMaxSize() + "):");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i));
        }
    }
    
    /**
     * Play cards
     */
    private boolean playCards() {
        List<Card> hand = playerHand.getCards();
        
        if (hand.isEmpty()) {
            System.out.println("Hand is empty, cannot play cards.");
            return false;
        }
        
        System.out.print("\nPlease select cards to play (enter indices separated by spaces, e.g.: 1 2 3): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("No cards selected.");
            return false;
        }
        
        String[] indices = input.split("\\s+");
        List<Card> selectedCards = new ArrayList<>();
        
        try {
            for (String idxStr : indices) {
                int idx = Integer.parseInt(idxStr) - 1;
                if (idx >= 0 && idx < hand.size()) {
                    selectedCards.add(hand.get(idx));
                } else {
                    System.out.println("Invalid card index: " + (idx + 1));
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Input format error, please enter numbers.");
            return false;
        }
        
        // Check for duplicate selections
        Set<Card> unique = new HashSet<>(selectedCards);
        if (unique.size() < selectedCards.size()) {
            System.out.println("Cannot select duplicate cards.");
            return false;
        }
        
        // Check if these cards can be played
        if (!scoreCalculator.canPlayHand(selectedCards)) {
            System.out.println("These cards cannot form a valid hand type (at least a pair required, or meet joker requirements).");
            return false;
        }
        
        // Calculate score
        int score = scoreCalculator.calculateScore(selectedCards);
        
        if (score == 0) {
            System.out.println("These cards cannot score (at least a pair required).");
            return false;
        }
        
        // Display hand type
        HandEvaluator.HandType handType = HandEvaluator.evaluateHand(selectedCards);
        System.out.println("\nPlayed cards: " + selectedCards);
        System.out.println("Hand Type: " + handType.getName());
        System.out.println("Score: " + score);
        
        // Remove from hand
        playerHand.removeCards(selectedCards);
        
        // Update total score
        totalScore += score;
        System.out.println("Total Score: " + totalScore);
        
        return true;
    }
    
    /**
     * Display jokers
     */
    private void displayJokers() {
        System.out.println("\nYour Jokers:");
        for (int i = 0; i < jokers.size(); i++) {
            System.out.println((i + 1) + ". " + jokers.get(i));
        }
        System.out.println();
    }
    
    /**
     * Shop system
     */
    private void enterShop() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("🛒 Shop Phase: You can adjust deck or pick new jokers.");
        boolean stay = true;
        
        while (stay) {
            System.out.println("\nShop Options:");
            System.out.println("1. Draw and select a new joker card");
            System.out.println("2. Remove an existing joker card");
            System.out.println("3. Leave shop, continue current round");
            System.out.print("Please enter option (1-3): ");
            
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    if (jokerPurchasedThisRound) {
                        System.out.println("Purchase quota for this round is exhausted, can only remove or leave shop.");
                    } else if (handleJokerSelection()) {
                        jokerPurchasedThisRound = true;
                    }
                    break;
                case "2":
                    handleRemoveJoker();
                    break;
                case "3":
                    stay = false;
                    break;
                default:
                    System.out.println("Invalid option, please enter again.");
            }
        }
        
        System.out.println("-".repeat(50));
    }
    
    private boolean handleJokerSelection() {
        List<Joker> available = new ArrayList<>(JokerDeck.getAllJokers());
        available.removeIf(joker -> jokers.stream()
            .anyMatch(existing -> existing.getName().equals(joker.getName())));
        
        if (available.isEmpty()) {
            System.out.println("All jokers unlocked, cannot get new jokers.");
            return false;
        }
        
        Collections.shuffle(available, random);
        int offerCount = Math.min(3, available.size());
        List<Joker> offers = available.subList(0, offerCount);
        
        System.out.println("\nAvailable Jokers:");
        for (int i = 0; i < offers.size(); i++) {
            System.out.println((i + 1) + ". " + offers.get(i));
        }
        System.out.print("Please select joker to get (enter number, 0 to cancel): ");
        
        int selection = readIntChoice(0, offerCount);
        if (selection == 0) {
            System.out.println("Joker purchase cancelled.");
            return false;
        }
        
        Joker newJoker = offers.get(selection - 1);
        jokers.add(newJoker);
        scoreCalculator.updateJokers(jokers);
        refreshHandConfiguration();
        
        System.out.println("Obtained new joker: " + newJoker.getName());
        displayJokers();
        return true;
    }
    
    private void handleRemoveJoker() {
        if (jokers.isEmpty()) {
            System.out.println("You currently have no jokers to remove.");
            return;
        }
        
        System.out.println("\nCurrent Jokers:");
        for (int i = 0; i < jokers.size(); i++) {
            System.out.println((i + 1) + ". " + jokers.get(i));
        }
        System.out.print("Please select joker to remove (enter number, 0 to cancel): ");
        
        int choice = readIntChoice(0, jokers.size());
        if (choice == 0) {
            System.out.println("Joker removal cancelled.");
            return;
        }
        
        Joker removed = jokers.remove(choice - 1);
        scoreCalculator.updateJokers(jokers);
        refreshHandConfiguration();
        
        System.out.println("Removed joker: " + removed.getName());
        displayJokers();
    }
    
    private void handleRequestCards() {
        if (remainingSpecialActions <= 0) {
            System.out.println("Special action quota for this round is exhausted, cannot request cards.");
            return;
        }
        List<Card> selected = selectCardsForUtility("request", Math.min(2, remainingSpecialActions));
        if (selected == null || selected.isEmpty()) {
            return;
        }
        
        for (Card card : selected) {
            Card copy = new Card(card.getSuit(), card.getRank());
            playerHand.forceAddCard(copy);
        }
        remainingSpecialActions -= selected.size();
        System.out.println("Duplicated " + selected.size() + " cards, current hand size: " + playerHand.size() +
            " | Remaining special quota: " + remainingSpecialActions);
        displayHand();
    }
    
    private void handleDiscardCards() {
        if (remainingSpecialActions <= 0) {
            System.out.println("Special action quota for this round is exhausted, cannot discard cards.");
            return;
        }
        List<Card> selected = selectCardsForUtility("discard", Math.min(2, remainingSpecialActions));
        if (selected == null || selected.isEmpty()) {
            return;
        }
        
        playerHand.removeCards(selected);
        remainingSpecialActions -= selected.size();
        System.out.println("Discarded " + selected.size() + " cards, current hand size: " + playerHand.size() +
            " | Remaining special quota: " + remainingSpecialActions);
        displayHand();
    }
    
    private List<Card> selectCardsForUtility(String actionName, int maxAllowed) {
        List<Card> hand = playerHand.getCards();
        if (hand.isEmpty()) {
            System.out.println("Hand is empty, cannot perform " + actionName + ".");
            return null;
        }
        
        System.out.print("\nPlease select cards to " + actionName + " (enter indices separated by spaces, max " + maxAllowed + " cards): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No cards selected.");
            return null;
        }
        
        String[] parts = input.split("\\s+");
        if (parts.length > maxAllowed) {
            System.out.println("Can only select up to " + maxAllowed + " cards at once.");
            return null;
        }
        
        List<Card> selected = new ArrayList<>();
        try {
            for (String part : parts) {
                int idx = Integer.parseInt(part) - 1;
                if (idx >= 0 && idx < hand.size()) {
                    selected.add(hand.get(idx));
                } else {
                    System.out.println("Invalid card index: " + (idx + 1));
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Input format error, please enter numbers.");
            return null;
        }
        
        Set<Card> unique = new HashSet<>(selected);
        if (unique.size() < selected.size()) {
            System.out.println("Cannot select duplicate cards.");
            return null;
        }
        return selected;
    }
    
    private int readIntChoice(int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid input, please enter " + min + "-" + max + ": ");
        }
    }
    
    private void refreshHandConfiguration() {
        if (playerHand == null) {
            return;
        }
        int newMax = scoreCalculator.getModifiedHandSize(BASE_HAND_SIZE);
        playerHand.setMaxSize(newMax);
    }
    
    /**
     * Game over
     */
    private void endGame() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Game Over!");
        System.out.println("Final Score: " + totalScore);
        System.out.println("Target Score: " + WIN_SCORE);
        
        if (totalScore >= WIN_SCORE) {
            System.out.println("🎉 Congratulations! You completed the game!");
        } else {
            System.out.println("Unfortunately, target score not achieved.");
        }
        
        System.out.println("=".repeat(50));
    }
}

