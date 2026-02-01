package model;

import java.util.List;
import java.util.function.*;

/**
 * class joker - 
 */
public class Joker {
    public enum EffectType {
        SCORE_MODIFIER,      // score modifier
        HAND_SIZE_MODIFIER,  // hand size modifier
        DRAW_MODIFIER,       // draw modifier
        CARD_FILTER,         // caed filter
        SPECIAL_RULE         // rules
    }
    
    private String name;
    private String description;
    private Effect effect;
    
    // score modifier: input(hand type, base score, cards) -> output modified score
    @FunctionalInterface
    public interface ScoreModifier extends Function3<service.HandEvaluator.HandType, Integer, List<Card>, Integer> {}
    
    // Hand size modifier: Input current hand size -> Output new hand size
    @FunctionalInterface
    public interface HandSizeModifier extends IntUnaryOperator {}
    
    // draw modifier: input current draw count -> output new draw count
    @FunctionalInterface
    public interface DrawModifier extends IntUnaryOperator {}
    
    // card filter: determine if the card should be special treated
    @FunctionalInterface
    public interface CardFilter extends Predicate<Card> {}
    
    
    @FunctionalInterface
    public interface Function3<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    
    public Joker(String name, String description, Effect effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Effect getEffect() {
        return effect;
    }
    
    /**
     * effect interface - will be implemented in ScoreCalculator
     */
    public static class Effect {
        private ScoreModifier scoreModifier;
        private HandSizeModifier handSizeModifier;
        private DrawModifier drawModifier;
        private CardFilter cardFilter;
        private EffectType type;
        
        public Effect(EffectType type) {
            this.type = type;
        }
        
        public Effect withScoreModifier(ScoreModifier modifier) {
            this.scoreModifier = modifier;
            this.type = EffectType.SCORE_MODIFIER;
            return this;
        }
        
        public Effect withHandSizeModifier(HandSizeModifier modifier) {
            this.handSizeModifier = modifier;
            this.type = EffectType.HAND_SIZE_MODIFIER;
            return this;
        }
        
        public Effect withDrawModifier(DrawModifier modifier) {
            this.drawModifier = modifier;
            this.type = EffectType.DRAW_MODIFIER;
            return this;
        }
        
        public Effect withCardFilter(CardFilter filter) {
            this.cardFilter = filter;
            this.type = EffectType.CARD_FILTER;
            return this;
        }
        
        public ScoreModifier getScoreModifier() {
            return scoreModifier;
        }
        
        public HandSizeModifier getHandSizeModifier() {
            return handSizeModifier;
        }
        
        public DrawModifier getDrawModifier() {
            return drawModifier;
        }
        
        public CardFilter getCardFilter() {
            return cardFilter;
        }
        
        public EffectType getType() {
            return type;
        }
    }
    
    @Override
    public String toString() {
        return name + ": " + description;
    }
}

