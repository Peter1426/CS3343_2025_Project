package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import model.Joker;
import model.Card;
import service.HandEvaluator;
import java.util.Arrays;
import java.util.List;

public class TestJoker{

    @Test
    public void testJokerCreation() {
        // Test basic joker creation
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
            .withScoreModifier((type, baseScore, cards) -> baseScore * 2);
        
        Joker joker = new Joker("Test Joker", "Doubles score", effect);
        
        assertNotNull("Joker should be created successfully", joker);
        assertEquals("Name should match", "Test Joker", joker.getName());
        assertEquals("Description should match", "Doubles score", joker.getDescription());
        assertNotNull("Effect should not be null", joker.getEffect());
    }

    @Test
    public void testEffectWithScoreModifier() {
        // Test score modifier effect
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
            .withScoreModifier((type, baseScore, cards) -> baseScore + 100);
        
        assertEquals("Effect type should be SCORE_MODIFIER", 
                     Joker.EffectType.SCORE_MODIFIER, effect.getType());
        assertNotNull("Score modifier should not be null", effect.getScoreModifier());
        
        // Test the modifier function
        List<Card> testCards = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        int result = effect.getScoreModifier().apply(
            HandEvaluator.HandType.PAIR, 50, testCards
        );
        assertEquals("Score should be increased by 100", 150, result);
    }

    @Test
    public void testEffectWithHandSizeModifier() {
        // Test hand size modifier effect
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.HAND_SIZE_MODIFIER)
            .withHandSizeModifier(size -> size + 2);
        
        assertEquals("Effect type should be HAND_SIZE_MODIFIER", 
                     Joker.EffectType.HAND_SIZE_MODIFIER, effect.getType());
        assertNotNull("Hand size modifier should not be null", effect.getHandSizeModifier());
        
        int result = effect.getHandSizeModifier().applyAsInt(5);
        assertEquals("Hand size should be increased by 2", 7, result);
    }

    @Test
    public void testEffectWithDrawModifier() {
        // Test draw modifier effect
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.DRAW_MODIFIER)
            .withDrawModifier(count -> count + 1);
        
        assertEquals("Effect type should be DRAW_MODIFIER", 
                     Joker.EffectType.DRAW_MODIFIER, effect.getType());
        assertNotNull("Draw modifier should not be null", effect.getDrawModifier());
        
        int result = effect.getDrawModifier().applyAsInt(3);
        assertEquals("Draw count should be increased by 1", 4, result);
    }

    @Test
    public void testEffectWithCardFilter() {
        // Test card filter effect
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.CARD_FILTER)
            .withCardFilter(card -> card.isRed());
        
        assertEquals("Effect type should be CARD_FILTER", 
                     Joker.EffectType.CARD_FILTER, effect.getType());
        assertNotNull("Card filter should not be null", effect.getCardFilter());
        
        Card redCard = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        Card blackCard = new Card(Card.Suit.SPADES, Card.Rank.KING);
        
        assertTrue("Red card should pass filter", effect.getCardFilter().test(redCard));
        assertFalse("Black card should not pass filter", effect.getCardFilter().test(blackCard));
    }

    @Test
    public void testMultipleEffectTypes() {
        // Test that effect type is properly set when multiple modifiers are added
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
            .withScoreModifier((type, baseScore, cards) -> baseScore * 2)
            .withHandSizeModifier(size -> size + 1);
        
        // The effect type should be the last one set
        assertEquals("Effect type should be HAND_SIZE_MODIFIER", 
                     Joker.EffectType.HAND_SIZE_MODIFIER, effect.getType());
        
        assertNotNull("Score modifier should still be available", effect.getScoreModifier());
        assertNotNull("Hand size modifier should be available", effect.getHandSizeModifier());
    }

    @Test
    public void testToString() {
        // Test string representation
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
            .withScoreModifier((type, baseScore, cards) -> baseScore);
        
        Joker joker = new Joker("Test Joker", "Test Description", effect);
        String result = joker.toString();
        
        assertTrue("ToString should contain name", result.contains("Test Joker"));
        assertTrue("ToString should contain description", result.contains("Test Description"));
        assertEquals("ToString format should be correct", 
                     "Test Joker: Test Description", result);
    }

    @Test
    public void testEffectTypeEnum() {
        // Test EffectType enum values
        Joker.EffectType[] types = Joker.EffectType.values();
        assertEquals("Should have 5 effect types", 5, types.length);
        
        // Test specific types exist
        assertTrue("SCORE_MODIFIER should exist", 
                   containsEffectType(types, Joker.EffectType.SCORE_MODIFIER));
        assertTrue("HAND_SIZE_MODIFIER should exist", 
                   containsEffectType(types, Joker.EffectType.HAND_SIZE_MODIFIER));
        assertTrue("DRAW_MODIFIER should exist", 
                   containsEffectType(types, Joker.EffectType.DRAW_MODIFIER));
        assertTrue("CARD_FILTER should exist", 
                   containsEffectType(types, Joker.EffectType.CARD_FILTER));
        assertTrue("SPECIAL_RULE should exist", 
                   containsEffectType(types, Joker.EffectType.SPECIAL_RULE));
    }
    
    private boolean containsEffectType(Joker.EffectType[] types, Joker.EffectType target) {
        for (Joker.EffectType type : types) {
            if (type == target) return true;
        }
        return false;
    }

    @Test
    public void testNullSafety() {
        // Test null safety in effect methods
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.SCORE_MODIFIER);
        
        assertNull("Score modifier should be null if not set", effect.getScoreModifier());
        assertNull("Hand size modifier should be null if not set", effect.getHandSizeModifier());
        assertNull("Draw modifier should be null if not set", effect.getDrawModifier());
        assertNull("Card filter should be null if not set", effect.getCardFilter());
    }
}