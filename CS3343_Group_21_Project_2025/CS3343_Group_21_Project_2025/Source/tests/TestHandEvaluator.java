package tests;

import model.Card;
import service.HandEvaluator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static service.HandEvaluator.HandType;

class TestHandEvaluator {
	
	@Test
	void testHandEvaluatorConstructor() {
	    new HandEvaluator();
	}

    // Test empty hand
    @Test
    void testEvaluateHand_EmptyHand() {
        List<Card> emptyHand = Collections.emptyList();
        assertEquals(HandType.HIGH_CARD, HandEvaluator.evaluateHand(emptyHand));
        assertFalse(HandEvaluator.isValidHand(emptyHand));
        assertFalse(HandEvaluator.canScore(emptyHand));
    }

    // Test null hand
    @Test
    void testEvaluateHand_NullHand() {
        assertEquals(HandType.HIGH_CARD, HandEvaluator.evaluateHand(null));
        assertFalse(HandEvaluator.isValidHand(null));
        assertFalse(HandEvaluator.canScore(null));
    }

    // Test single card
    @Test
    void testEvaluateHand_SingleCard() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.TEN);
        List<Card> singleCard = Collections.singletonList(card);
        
        assertEquals(HandType.HIGH_CARD, HandEvaluator.evaluateHand(singleCard));
        assertTrue(HandEvaluator.isValidHand(singleCard));
        assertFalse(HandEvaluator.canScore(singleCard));
    }

    // Test high card
    @Test
    void testEvaluateHand_HighCard() {
        List<Card> highCardHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TWO),
            new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING)
        );
        
        assertEquals(HandType.HIGH_CARD, HandEvaluator.evaluateHand(highCardHand));
        assertTrue(HandEvaluator.isValidHand(highCardHand));
        assertFalse(HandEvaluator.canScore(highCardHand));
    }

    // Test pair - basic case
    @Test
    void testEvaluateHand_Pair() {
        List<Card> pairHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        
        assertEquals(HandType.PAIR, HandEvaluator.evaluateHand(pairHand));
        assertTrue(HandEvaluator.isValidHand(pairHand));
        assertTrue(HandEvaluator.canScore(pairHand));
    }

    @Test
    void testEvaluateHand_PairWithMultiplePairs() {
        List<Card> fullhouse = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.TEN),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN) 
        );
        
        assertEquals(HandType.FULL_HOUSE, HandEvaluator.evaluateHand(fullhouse));
    }

    // Test two pair
    @Test
    void testEvaluateHand_TwoPair() {
        List<Card> twoPairHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.TEN),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        
        assertEquals(HandType.TWO_PAIR, HandEvaluator.evaluateHand(twoPairHand));
        assertTrue(HandEvaluator.isValidHand(twoPairHand));
        assertTrue(HandEvaluator.canScore(twoPairHand));
    }

    // Test three of a kind - basic case
    @Test
    void testEvaluateHand_ThreeOfAKind() {
        List<Card> threeOfAKindHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        
        assertEquals(HandType.THREE_OF_A_KIND, HandEvaluator.evaluateHand(threeOfAKindHand));
        assertTrue(HandEvaluator.isValidHand(threeOfAKindHand));
        assertTrue(HandEvaluator.canScore(threeOfAKindHand));
    }

    // Test three of a kind with full house potential
    @Test
    void testEvaluateHand_ThreeOfAKindNotFullHouse() {
        List<Card> threeOfAKindHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK) // Different cards, not a pair
        );
        
        // Should be three of a kind, not full house
        assertEquals(HandType.THREE_OF_A_KIND, HandEvaluator.evaluateHand(threeOfAKindHand));
    }

    // Test four of a kind - basic case
    @Test
    void testEvaluateHand_FourOfAKind() {
        List<Card> fourOfAKindHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.EIGHT),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );
        
        assertEquals(HandType.FOUR_OF_A_KIND, HandEvaluator.evaluateHand(fourOfAKindHand));
        assertTrue(HandEvaluator.isValidHand(fourOfAKindHand));
        assertTrue(HandEvaluator.canScore(fourOfAKindHand));
    }

    // Test four of a kind with straight flush potential
    @Test
    void testEvaluateHand_FourOfAKindNotStraightFlush() {
        List<Card> fourOfAKindHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.EIGHT),
            new Card(Card.Suit.HEARTS, Card.Rank.NINE) // Not a straight flush
        );
        
        // Should be four of a kind, not straight flush
        assertEquals(HandType.FOUR_OF_A_KIND, HandEvaluator.evaluateHand(fourOfAKindHand));
    }

    // Test straight - normal straight
    @Test
    void testEvaluateHand_Straight() {
        List<Card> straightHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.SIX),
            new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.NINE),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN)
        );
        
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(straightHand));
        assertTrue(HandEvaluator.isValidHand(straightHand));
        assertTrue(HandEvaluator.canScore(straightHand));
    }

    // Test straight - A-2-3-4-5
    @Test
    void testEvaluateHand_StraightAceLow() {
        List<Card> straightHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(straightHand));
        assertTrue(HandEvaluator.isValidHand(straightHand));
        assertTrue(HandEvaluator.canScore(straightHand));
    }

    // Test flush
    @Test
    void testEvaluateHand_Flush() {
        List<Card> flushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TWO),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING)
        );
        
        assertEquals(HandType.FLUSH, HandEvaluator.evaluateHand(flushHand));
        assertTrue(HandEvaluator.isValidHand(flushHand));
        assertTrue(HandEvaluator.canScore(flushHand));
    }

    // Test full house
    @Test
    void testEvaluateHand_FullHouse() {
        List<Card> fullHouseHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
            new Card(Card.Suit.SPADES, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN)
        );
        
        assertEquals(HandType.FULL_HOUSE, HandEvaluator.evaluateHand(fullHouseHand));
        assertTrue(HandEvaluator.isValidHand(fullHouseHand));
        assertTrue(HandEvaluator.canScore(fullHouseHand));
    }

    // Test straight flush - not royal flush
    @Test
    void testEvaluateHand_StraightFlushNotRoyal() {
        List<Card> straightFlushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.SIX),
            new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.HEARTS, Card.Rank.NINE),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN)
        );
        
        // Should be straight flush, not royal flush
        assertEquals(HandType.STRAIGHT_FLUSH, HandEvaluator.evaluateHand(straightFlushHand));
        assertTrue(HandEvaluator.isValidHand(straightFlushHand));
        assertTrue(HandEvaluator.canScore(straightFlushHand));
    }

    // Test straight flush with more than 5 cards
    @Test
    void testEvaluateHand_StraightFlushMoreThan5Cards() {
        List<Card> straightFlushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.SIX),
            new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.HEARTS, Card.Rank.NINE),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK)
        );
        
        // Should be straight flush, not royal flush (more than 5 cards)
        assertEquals(HandType.STRAIGHT_FLUSH, HandEvaluator.evaluateHand(straightFlushHand));
    }

    // Test royal flush
    @Test
    void testEvaluateHand_RoyalFlush() {
        List<Card> royalFlushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        
        assertEquals(HandType.ROYAL_FLUSH, HandEvaluator.evaluateHand(royalFlushHand));
        assertTrue(HandEvaluator.isValidHand(royalFlushHand));
        assertTrue(HandEvaluator.canScore(royalFlushHand));
    }

    // Test royal flush condition - isFlush && isStraight && cards.size() == 5
    @Test
    void testEvaluateHand_RoyalFlushCondition_AllTrue() {
        List<Card> royalFlushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        
        // All conditions true: isFlush=true, isStraight=true, cards.size()=5
        assertEquals(HandType.ROYAL_FLUSH, HandEvaluator.evaluateHand(royalFlushHand));
    }

    // Test royal flush condition - isFlush=true, isStraight=true, cards.size()!=5
    @Test
    void testEvaluateHand_RoyalFlushCondition_Not5Cards() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.HEARTS, Card.Rank.NINE) // Extra card
        );
        
        // isFlush=true, isStraight=true, but cards.size()!=5
        assertEquals(HandType.STRAIGHT_FLUSH, HandEvaluator.evaluateHand(hand));
    }

    // Test royal flush condition - isFlush=true, isStraight=false, cards.size()=5
    @Test
    void testEvaluateHand_RoyalFlushCondition_NotStraight() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.TWO) // Not a straight
        );
        
        // isFlush=true, isStraight=false, cards.size()=5
        assertEquals(HandType.FLUSH, HandEvaluator.evaluateHand(hand));
    }

    // Test royal flush condition - isFlush=false, isStraight=true, cards.size()=5
    @Test
    void testEvaluateHand_RoyalFlushCondition_NotFlush() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.DIAMONDS, Card.Rank.JACK),
            new Card(Card.Suit.CLUBS, Card.Rank.QUEEN),
            new Card(Card.Suit.SPADES, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        
        // isFlush=false, isStraight=true, cards.size()=5
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(hand));
    }

    // Test royal flush inner condition - values.get(0)==10 && values.get(4)==14
    @Test
    void testEvaluateHand_RoyalFlushInnerCondition_True() {
        List<Card> royalFlushHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        
        // values.get(0)==10 && values.get(4)==14
        assertEquals(HandType.ROYAL_FLUSH, HandEvaluator.evaluateHand(royalFlushHand));
    }

    // Test royal flush inner condition - values.get(0)!=10
    @Test
    void testEvaluateHand_RoyalFlushInnerCondition_FirstNot10() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.NINE),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING)
        );
        
        // values.get(0)!=10 (it's 9)
        assertEquals(HandType.STRAIGHT_FLUSH, HandEvaluator.evaluateHand(hand));
    }

    // Test royal flush inner condition - values.get(4)!=14
    @Test
    void testEvaluateHand_RoyalFlushInnerCondition_LastNot14() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.HEARTS, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
            new Card(Card.Suit.HEARTS, Card.Rank.KING),
            new Card(Card.Suit.HEARTS, Card.Rank.NINE) // Not ACE
        );
        
        // values.get(4)!=14 (it's 13)
        assertEquals(HandType.STRAIGHT_FLUSH, HandEvaluator.evaluateHand(hand));
    }

    @Test
    void testEvaluateHand_ThreeCardStraight() {
        List<Card> threeCardStraight = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.SIX),
            new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)
        );
        
        assertNotEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(threeCardStraight));
    }

    // Test that 2 cards cannot form a straight
    @Test
    void testEvaluateHand_TwoCardStraight() {
        List<Card> twoCardStraight = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.SIX),
            new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)
        );
        
        assertNotEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(twoCardStraight));
    }

    // Test the specific condition in isStraight method for low straight with exactly A-2-3-4-5
    @Test
    void testIsStraight_LowStraightExact() {
        List<Card> lowStraightExact = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        
        // This should trigger the first condition: sorted.equals(lowStraight)
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(lowStraightExact));
    }

    // Test the specific condition in isStraight method for low straight with extra cards
    @Test
    void testIsStraight_LowStraightWithExtraCards() {
        List<Card> lowStraightWithExtra = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TEN) // extra card
        );
        
        // This should trigger the second condition: sorted.size() >= 5 && sorted.subList(0, 4).equals(Arrays.asList(2, 3, 4, 5)) && sorted.contains(14)
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(lowStraightWithExtra));
    }

    // Test the OR condition in isStraight - first condition false, second condition true
    @Test
    void testIsStraight_OrCondition_FirstFalseSecondTrue() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.SIX) // Makes it not exactly A-2-3-4-5
        );
        
        // First condition (sorted.equals(lowStraight)) is false
        // Second condition (sorted.size() >= 5 && sorted.subList(0, 4).equals(Arrays.asList(2, 3, 4, 5)) && sorted.contains(14)) is true
        assertEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(hand));
    }

    // Test the OR condition in isStraight - both conditions false
    @Test
    void testIsStraight_OrCondition_BothFalse() {
        List<Card> hand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.SIX) // Missing 5, so not a low straight
        );
        
        // Both conditions false
        assertNotEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(hand));
    }

    // Test edge case where cards contain A-2-3-4 but not 5
    @Test
    void testIsStraight_LowStraightIncomplete() {
        List<Card> incompleteLowStraight = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
            new Card(Card.Suit.CLUBS, Card.Rank.THREE),
            new Card(Card.Suit.SPADES, Card.Rank.FOUR),
            new Card(Card.Suit.HEARTS, Card.Rank.SEVEN) // breaks the straight
        );
        
        // Should not be a straight
        assertNotEquals(HandType.STRAIGHT, HandEvaluator.evaluateHand(incompleteLowStraight));
    }

    // Test isValidHand with multiple cards
    @Test
    void testIsValidHand_MultipleCards() {
        List<Card> multipleCards = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.DIAMONDS, Card.Rank.KING),
            new Card(Card.Suit.CLUBS, Card.Rank.QUEEN)
        );
        
        // Should return true for multiple cards
        assertTrue(HandEvaluator.isValidHand(multipleCards));
    }

    // Test isValidHand edge case - exactly 1 card (minimum valid)
    @Test
    void testIsValidHand_ExactlyOneCard() {
        List<Card> oneCard = Collections.singletonList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE)
        );
        
        // Should return true for exactly 1 card
        assertTrue(HandEvaluator.isValidHand(oneCard));
    }

    // Test HandType getName method for all hand types
    @Test
    void testHandTypeGetName() {
        assertEquals("High Card", HandType.HIGH_CARD.getName());
        assertEquals("Pair", HandType.PAIR.getName());
        assertEquals("Two Pair", HandType.TWO_PAIR.getName());
        assertEquals("Three of a Kind", HandType.THREE_OF_A_KIND.getName());
        assertEquals("Straight", HandType.STRAIGHT.getName());
        assertEquals("Flush", HandType.FLUSH.getName());
        assertEquals("Full House", HandType.FULL_HOUSE.getName());
        assertEquals("Four of a Kind", HandType.FOUR_OF_A_KIND.getName());
        assertEquals("Straight Flush", HandType.STRAIGHT_FLUSH.getName());
        assertEquals("Royal Flush", HandType.ROYAL_FLUSH.getName());
    }

    // Test HandType getRank and getBaseScore methods for completeness
    @Test
    void testHandTypeProperties() {
        // Test a few representative types to ensure properties are accessible
        assertEquals(1, HandType.HIGH_CARD.getRank());
        assertEquals(5, HandType.HIGH_CARD.getBaseScore());
        
        assertEquals(10, HandType.ROYAL_FLUSH.getRank());
        assertEquals(800, HandType.ROYAL_FLUSH.getBaseScore());
        
        assertEquals(6, HandType.FLUSH.getRank());
        assertEquals(150, HandType.FLUSH.getBaseScore());
    }

    // Parameterized test for hand evaluation
    @ParameterizedTest
    @MethodSource("provideHandsForEvaluation")
    void testEvaluateHand_Parameterized(List<Card> hand, HandType expectedType) {
        assertEquals(expectedType, HandEvaluator.evaluateHand(hand));
    }

    private static Stream<Arguments> provideHandsForEvaluation() {
        return Stream.of(
            // High card
            Arguments.of(Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.TWO),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.JACK)
            ), HandType.HIGH_CARD),
            
            // Pair
            Arguments.of(Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
            ), HandType.PAIR),
            
            // Two pair
            Arguments.of(Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
            ), HandType.TWO_PAIR),
            
            // Three of a kind
            Arguments.of(Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.QUEEN)
            ), HandType.THREE_OF_A_KIND)
        );
    }

    // Test flush with less than 5 cards
    @Test
    void testEvaluateHand_ShortFlush() {
        List<Card> shortFlush = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TWO),
            new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );
        
        // 3 cards cannot form a flush
        assertNotEquals(HandType.FLUSH, HandEvaluator.evaluateHand(shortFlush));
    }

    // Test hand validity - valid hand
    @Test
    void testIsValidHand_Valid() {
        List<Card> validHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.DIAMONDS, Card.Rank.JACK)
        );
        
        assertTrue(HandEvaluator.isValidHand(validHand));
    }

    // Test scoring capability - scorable hand
    @Test
    void testCanScore_ScorableHand() {
        List<Card> scorableHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT) // Pair, can score
        );
        
        assertTrue(HandEvaluator.canScore(scorableHand));
    }

    // Test scoring capability - non-scorable hand
    @Test
    void testCanScore_NonScorableHand() {
        List<Card> nonScorableHand = Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
            new Card(Card.Suit.DIAMONDS, Card.Rank.NINE) // High card, cannot score
        );
        
        assertFalse(HandEvaluator.canScore(nonScorableHand));
    }
    
    
}