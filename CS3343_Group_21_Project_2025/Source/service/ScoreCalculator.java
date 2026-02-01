package service;

import model.*;
import java.util.*;

/**
 * score calculator - handle the effects of jokers
 */
public class ScoreCalculator {
    private List<Joker> activeJokers;
    private boolean isFirstPlayThisRound;
    
    public ScoreCalculator(List<Joker> activeJokers) {
        this.activeJokers = new ArrayList<>(activeJokers);
        this.isFirstPlayThisRound = true;
    }
    
    /**
     * update the list of active jokers
     */
    public void updateJokers(List<Joker> newJokers) {
        this.activeJokers = new ArrayList<>(newJokers);
    }
    
    /**
     * calculate the score
     */
    public int calculateScore(List<Card> playedCards) {
        if (playedCards == null || playedCards.isEmpty()) {
            return 0;
        }
        
        HandEvaluator.HandType handType = HandEvaluator.evaluateHand(playedCards);
        
        // now high card can also score
        
        int baseScore = handType.getBaseScore();
        
        // handle the special logic: Greedy Joker (apply first, before other multipliers)
        int score = applyGreedyJoker(baseScore);
        
        // apply all other joker effects (excluding Greedy Joker which is already applied)
        int finalScore = applyJokerEffectsExcludingGreedy(handType, score, playedCards);
        
        isFirstPlayThisRound = false;
        
        return finalScore;
    }
    
    /**
     * apply all joker effects (excluding Greedy Joker which is handled separately)
     */
    private int applyJokerEffectsExcludingGreedy(HandEvaluator.HandType handType, int baseScore, List<Card> cards) {
        int score = baseScore;
        
        for (Joker joker : activeJokers) {
            // Skip Greedy Joker as it's handled separately before this method
            if (joker.getName().contains("Greedy Joker")) {
                continue;
            }
            
            Joker.Effect effect = joker.getEffect();
            
            if (effect != null && effect.getScoreModifier() != null) {
                score = effect.getScoreModifier().apply(handType, score, cards);
            }
        }
        
        return score;
    }
    
    /**
     * handle the special logic of Greedy Joker (first play each round ×3, subsequent plays ÷2)
     * This should be applied BEFORE other multiplier effects
     */
    private int applyGreedyJoker(int score) {
        boolean hasGreedyJoker = activeJokers.stream()
            .anyMatch(j -> j.getName().contains("Greedy Joker"));
        
        if (hasGreedyJoker) {
            if (isFirstPlayThisRound) {
                return score * 3;
            } else {
                return score / 2;
            }
        }
        
        return score;
    }
    
    /**
     * reset the round state (called when a new round starts)
     */
    public void resetRound() {
        isFirstPlayThisRound = true;
    }
    
    /**
     * get the modified hand size
     */
    public int getModifiedHandSize(int baseSize) {
        int size = baseSize;
        
        for (Joker joker : activeJokers) {
            Joker.Effect effect = joker.getEffect();
            
            if (effect != null && effect.getHandSizeModifier() != null) {
                size = effect.getHandSizeModifier().applyAsInt(size);
            }
        }
        
        return Math.max(1, size); // at least keep 1 card
    }
    
    /**
        * get the modified draw count
     */
    public int getModifiedDrawCount(int baseCount) {
        int count = baseCount;
        
        for (Joker joker : activeJokers) {
            Joker.Effect effect = joker.getEffect();
            
            if (effect != null && effect.getDrawModifier() != null) {
                count = effect.getDrawModifier().applyAsInt(count);
            }
        }
        
        return Math.max(1, count); // 
    }
    
    /**
     * check if the hand is valid
                * allow high card, but the score is 0
     * Rogue Joker: cannot play pairs or lower, only Three of a Kind or higher
     */
    public boolean canPlayHand(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return false;
        }
        
        // Check if hand is valid
        if (!HandEvaluator.isValidHand(cards)) {
            return false;
        }
        
        // Check for Rogue Joker restriction
        boolean hasRogueJoker = activeJokers.stream()
            .anyMatch(j -> j.getName().contains("Rogue Joker"));
        
        if (hasRogueJoker) {
            // Rogue Joker: cannot play pairs or lower, only Three of a Kind or higher
            HandEvaluator.HandType handType = HandEvaluator.evaluateHand(cards);
            if (handType.getRank() < HandEvaluator.HandType.THREE_OF_A_KIND.getRank()) {
                return false; // Cannot play High Card, Pair, or Two Pair
            }
        }
        
        // allow any hand type (including high card), if the score is 0, it can also be played
        return true;
    }
    
    public List<Joker> getActiveJokers() {
        return new ArrayList<>(activeJokers);
    }
}

