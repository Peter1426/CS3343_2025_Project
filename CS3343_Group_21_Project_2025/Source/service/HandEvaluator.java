package service;

import model.Card;
import java.util.*;

//
public class HandEvaluator {
    public enum HandType {
        HIGH_CARD(1, "High Card", 5),
        PAIR(2, "Pair", 15),
        TWO_PAIR(3, "Two Pair", 35),
        THREE_OF_A_KIND(4, "Three of a Kind", 70),
        STRAIGHT(5, "Straight", 120),
        FLUSH(6, "Flush", 150),
        FULL_HOUSE(7, "Full House", 250),
        FOUR_OF_A_KIND(8, "Four of a Kind", 400),
        STRAIGHT_FLUSH(9, "Straight Flush", 600),
        ROYAL_FLUSH(10, "Royal Flush", 800);
        
        private final int rank;
        private final String name;
        private final int baseScore;
        
        HandType(int rank, String name, int baseScore) {
            this.rank = rank;
            this.name = name;
            this.baseScore = baseScore;
        }
        
        public int getRank() {
            return rank;
        }
        
        public String getName() {
            return name;
        }
        
        public int getBaseScore() {
            return baseScore;
        }
    }
    
   
    public static HandType evaluateHand(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return HandType.HIGH_CARD;
        }
        
        if (cards.size() == 1) {
            return HandType.HIGH_CARD;
        }
        
        // cal the count of ranks and suits
        Map<Integer, Integer> rankCount = new HashMap<>();
        Map<Card.Suit, Integer> suitCount = new HashMap<>();
        
        for (Card card : cards) {
            rankCount.put(card.getValue(), rankCount.getOrDefault(card.getValue(), 0) + 1);
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
        }
        
        List<Integer> counts = new ArrayList<>(rankCount.values());
        Collections.sort(counts, Collections.reverseOrder());
        
        boolean isFlush = suitCount.size() == 1 && cards.size() >= 5;
        boolean isStraight = isStraight(cards);
        
        // Royal Flush (A-K-Q-J-10 Flush)
        if (isFlush && isStraight && cards.size() == 5) {
            List<Integer> values = new ArrayList<>();
            for (Card card : cards) {
                values.add(card.getValue());
            }
            Collections.sort(values);
            if (values.get(0) == 10 && values.get(4) == 14) {
                return HandType.ROYAL_FLUSH;
            }
        }
        
        // Straight Flush
        if (isFlush && isStraight) {
            return HandType.STRAIGHT_FLUSH;
        }
        
        // Four of a Kind
        if (counts.size() >= 1 && counts.get(0) == 4) {
            return HandType.FOUR_OF_A_KIND;
        }
        
        // Full House
        if (counts.size() >= 2 && counts.get(0) == 3 && counts.get(1) == 2) {
            return HandType.FULL_HOUSE;
        }
        
        // Flush
        if (isFlush) {
            return HandType.FLUSH;
        }
        
        // Straight
        if (isStraight) {
            return HandType.STRAIGHT;
        }
        
        // Three of a Kind
        if (counts.size() >= 1 && counts.get(0) == 3) {
            return HandType.THREE_OF_A_KIND;
        }
        
        // Two Pair
        if (counts.size() >= 2 && counts.get(0) == 2 && counts.get(1) == 2) {
            return HandType.TWO_PAIR;
        }
        
        // Pair
        if (counts.size() >= 1 && counts.get(0) == 2) {
            return HandType.PAIR;
        }
        
        return HandType.HIGH_CARD;
    }
    
    /**
     *
     */
    private static boolean isStraight(List<Card> cards) {
        if (cards.size() < 5) return false;
        
        List<Integer> values = new ArrayList<>();
        for (Card card : cards) {
            values.add(card.getValue());
        }
        Collections.sort(values);
        
        // 检查连续（允许A-2-3-4-5这样的顺子）
        boolean isConsecutive = true;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) != values.get(i-1) + 1) {
                isConsecutive = false;
                break;
            }
        }
        
        // A-2-3-4-5
        if (!isConsecutive && values.size() >= 5) {
            if (values.contains(14)) { // 有A
                List<Integer> lowStraight = Arrays.asList(2, 3, 4, 5, 14);
                List<Integer> sorted = new ArrayList<>(values);
                Collections.sort(sorted);
                if (sorted.equals(lowStraight) || 
                    (sorted.size() >= 5 && sorted.subList(0, 4).equals(Arrays.asList(2, 3, 4, 5)) && sorted.contains(14))) {
                    return true;
                }
            }
        }
        
        return isConsecutive;
    }
    
    /**
     * ）
     * 
     */
    public static boolean isValidHand(List<Card> cards) {
        if (cards == null) {
            return false;
        }
        
        // 
        return cards.size() >= 1;
    }
    
    /**
     *
     */
    public static boolean canScore(List<Card> cards) {
        if (!isValidHand(cards)) {
            return false;
        }
        
        HandType type = evaluateHand(cards);
        return type.getRank() >= HandType.PAIR.getRank();
    }
}

