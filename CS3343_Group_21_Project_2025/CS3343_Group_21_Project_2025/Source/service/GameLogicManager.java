package service;

import model.*;
import java.util.*;

/**
 * Game Logic Manager - Handles core game logic operations
 * Extracted from GameWindow to separate UI from business logic
 */
public class GameLogicManager {
    
    /**
     * Create a standard 52-card deck
     */
    public static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }
    
    /**
     * Shuffle the deck
     */
    public static void shuffleDeck(List<Card> deck) {
        Collections.shuffle(deck);
    }
    
    /**
     * Draw specified number of cards from deck to hand
     */
    public static void drawCards(List<Card> deck, Hand playerHand, int count) {
        if (count <= 0) {
            return; // No need to draw
        }
        
        int drawn = 0;
        int handMaxSize = playerHand.getMaxSize();
        
        // Draw cards until hand limit or desired count is reached
        for (int i = 0; i < count && !deck.isEmpty() && playerHand.size() < handMaxSize; i++) {
            Card card = deck.remove(0);
            playerHand.addCard(card);
            drawn++;
        }
        
        // If deck is empty, reshuffle and continue drawing
        if (deck.isEmpty() && drawn < count && playerHand.size() < handMaxSize) {
            deck.addAll(createDeck());
            shuffleDeck(deck);
            
            // Continue drawing
            int remaining = count - drawn;
            for (int i = 0; i < remaining && !deck.isEmpty() && playerHand.size() < handMaxSize; i++) {
                Card card = deck.remove(0);
                playerHand.addCard(card);
                drawn++;
            }
        }
    }
    
    /**
     * Analyze hand and provide suggestions
     */
    public static String analyzeHand(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return "No cards selected.";
        }
        
        StringBuilder analysis = new StringBuilder();
        
        // Count ranks and suits
        Map<Integer, Integer> rankCount = new HashMap<>();
        Map<Card.Suit, Integer> suitCount = new HashMap<>();
        
        for (Card card : cards) {
            rankCount.put(card.getValue(), rankCount.getOrDefault(card.getValue(), 0) + 1);
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
        }
        
        // Check for pairs
        boolean hasPair = rankCount.values().stream().anyMatch(count -> count >= 2);
        if (!hasPair && cards.size() >= 2) {
            analysis.append("⚠️ No pairs: all ranks are unique\\n");
        }
        
        // Check suits
        int maxSuitCount = suitCount.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (cards.size() == 5 && maxSuitCount == 4) {
            analysis.append("⚠️ Almost a flush: 4 cards share the same suit but 1 differs\\n");
        } else if (maxSuitCount < 5 && cards.size() == 5) {
            analysis.append("ℹ️ Suit distribution: ");
            suitCount.forEach((suit, count) -> {
                if (count > 1) {
                    analysis.append(suit.getSymbol()).append("×").append(count).append(" ");
                }
            });
            analysis.append("\\n");
        }
        
        // Check for possible pairs
        List<Integer> possiblePairs = new ArrayList<>();
        rankCount.forEach((rank, count) -> {
            if (count >= 2) {
                possiblePairs.add(rank);
            }
        });
        
        if (!possiblePairs.isEmpty()) {
            analysis.append("✅ Pair(s) available: ");
            for (int rank : possiblePairs) {
                String rankName = Card.Rank.values()[rank - 2].getDisplay();
                analysis.append(rankName).append("×").append(rankCount.get(rank)).append(" ");
            }
            analysis.append("\\n");
        } else if (cards.size() < 5) {
            analysis.append("💡 Tip: Try selecting more cards, or only select two cards of the same rank\\n");
        }
        
        return analysis.toString();
    }
    
    /**
     * Calculate current level target score
     */
    public static int getCurrentLevelTarget(int currentLevel, int[] levelTargets) {
        if (currentLevel >= 1 && currentLevel <= levelTargets.length) {
            return levelTargets[currentLevel - 1];
        }
        return levelTargets[levelTargets.length - 1];
    }
    
    /**
     * Check if level can be skipped (target achieved)
     */
    public static boolean canSkipLevel(int levelScore, int currentLevel, int[] levelTargets, int totalLevels) {
        return currentLevel < totalLevels && levelScore >= getCurrentLevelTarget(currentLevel, levelTargets);
    }
    
    /**
     * Check if all levels are completed
     */
    public static boolean areAllLevelsCompleted(int currentLevel, int levelScore, int[] levelTargets, int totalLevels) {
        return currentLevel > totalLevels || 
               (currentLevel == totalLevels && levelScore >= levelTargets[totalLevels - 1]);
    }
}