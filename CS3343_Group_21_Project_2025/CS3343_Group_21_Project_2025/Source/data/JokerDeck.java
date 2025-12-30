package data;

import model.Joker;
import model.Card;
import service.HandEvaluator;
import java.util.*;

/**
 * Joker Card Database - Contains all available joker card definitions
 */
public class JokerDeck {
    private static List<Joker> allJokers = new ArrayList<>();
    
    static {
        initializeJokers();
    }
    
    /**
     * Initialize all joker cards
     */
    private static void initializeJokers() {
        // 1. Double Vision - All scores ×2
        allJokers.add(new Joker(
            "Double Vision",
            "All scores ×2",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> baseScore * 2)
        ));
        
        // 2. Greedy Joker - First play each round ×3, subsequent plays ÷2
        allJokers.add(new Joker(
            "Greedy Joker",
            "First play each round ×3, subsequent plays ÷2",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> baseScore) // 
        ));
        
        // 3. The Gambler - If hand is all single cards (no hand type), gain 200 points
        allJokers.add(new Joker(
            "The Gambler",
            "If hand is all single cards (no hand type), gain 100 points",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    if (type == HandEvaluator.HandType.HIGH_CARD) {
                        return 100;
                    }
                    return baseScore;
                })
        ));
        
        // 4. Flower Child - Clubs cards are wild (can be any suit/rank)
        allJokers.add(new Joker(
            "Flower Child",
            "Clubs cards are wild (can be any suit/rank)",
            new Joker.Effect(Joker.EffectType.CARD_FILTER)
                .withCardFilter(card -> card.isClubs())
        ));
        
        // 5. Copycat - Copy last round's hand type and get same score (simplified: Three of a Kind or higher ×2)
        allJokers.add(new Joker(
            "Copycat",
            "Three of a Kind or higher ×2",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    if (type.getRank() >= HandEvaluator.HandType.THREE_OF_A_KIND.getRank()) {
                        return baseScore * 2;
                    }
                    return baseScore;
                })
        ));
        
        // 7. Chaos Joker - 30% chance to turn a card into joker when drawing (simplified: All scores +30%)
        allJokers.add(new Joker(
            "Chaos Joker",
            "All scores +30%",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> (int)(baseScore * 1.3))
        ));
        
        // 8. Mirror Joker - Black cards score = Red cards score (take higher) (simplified: Flush ×1.5)
        allJokers.add(new Joker(
            "Mirror Joker",
            "Flush ×1.5",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    if (type == HandEvaluator.HandType.FLUSH || 
                        type == HandEvaluator.HandType.STRAIGHT_FLUSH ||
                        type == HandEvaluator.HandType.ROYAL_FLUSH) {
                        return (int)(baseScore * 1.5);
                    }
                    return baseScore;
                })
        ));
        
        // 9. Rogue Joker - Cannot play pairs or lower, but Three of a Kind or higher ×3
        allJokers.add(new Joker(
            "Rogue Joker",
            "Three of a Kind or higher ×3",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    if (type.getRank() >= HandEvaluator.HandType.THREE_OF_A_KIND.getRank()) {
                        return baseScore * 3;
                    }
                    return baseScore;
                })
        ));
        
        // Additional joker cards
        
        // 10. Red Card Joker - Red cards score ×2 (all cards must be red)
        allJokers.add(new Joker(
            "Red Card Joker",
            "Red cards score ×2",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    boolean allRed = cards.stream().allMatch(Card::isRed);
                    return allRed ? baseScore * 2 : baseScore;
                })
        ));
        
        // 11. Ace Master - Each Ace played, extra score +50
        allJokers.add(new Joker(
            "Ace Master",
            "Each Ace played, extra score +50",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    long aceCount = cards.stream()
                        .filter(c -> c.getRank() == Card.Rank.ACE)
                        .count();
                    return (int)(baseScore + aceCount * 50);
                })
        ));
        
        // 12. Straight Runner - Straight, Straight Flush, Royal Flush ×2
        allJokers.add(new Joker(
            "Straight Runner",
            "Straight, Straight Flush, Royal Flush ×2",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    if (type == HandEvaluator.HandType.STRAIGHT ||
                        type == HandEvaluator.HandType.STRAIGHT_FLUSH ||
                        type == HandEvaluator.HandType.ROYAL_FLUSH) {
                        return baseScore * 2;
                    }
                    return baseScore;
                })
        ));
        
        // 13. Lucky Sevens - Each 7 played, extra +70
        allJokers.add(new Joker(
            "Lucky Sevens",
            "Each 7 played, extra score +70",
            new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                .withScoreModifier((type, baseScore, cards) -> {
                    long sevenCount = cards.stream()
                        .filter(c -> c.getRank() == Card.Rank.SEVEN)
                        .count();
                    return (int)(baseScore + sevenCount * 70);
                })
        ));
        
        // 14. Draw Maestro - Draw count +1
        allJokers.add(new Joker(
            "Draw Maestro",
            "Draw 1 extra card when refilling",
            new Joker.Effect(Joker.EffectType.DRAW_MODIFIER)
                .withDrawModifier(count -> count + 1)
        ));
        
       
    }
    
    /**
     * Get random number of joker cards
     */
    public static List<Joker> getRandomJokers(int count) {
        List<Joker> available = new ArrayList<>(allJokers);
        Collections.shuffle(available);
        
        int actualCount = Math.min(count, available.size());
        return new ArrayList<>(available.subList(0, actualCount));
    }
    
    /**
     * Get all joker cards
     */
    public static List<Joker> getAllJokers() {
        return new ArrayList<>(allJokers);
    }
    
    /**
     * Get joker card by name
     */
    public static Joker getJokerByName(String name) {
        return allJokers.stream()
            .filter(j -> j.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}

