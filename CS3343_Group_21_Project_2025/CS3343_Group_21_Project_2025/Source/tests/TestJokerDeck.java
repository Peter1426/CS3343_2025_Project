package tests;

import model.Joker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.JokerDeck;
import model.Card;
import service.HandEvaluator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class TestJokerDeck {

    private List<Joker> allJokers;

    @BeforeEach
    void setUp() {
        allJokers = JokerDeck.getAllJokers();
    }
    
    @Test
    void testClass() {
    	new JokerDeck();
    }
    @Test
    void testInitializeAllJokers() {
        assertNotNull(allJokers);
        assertFalse(allJokers.isEmpty());
    }

    @Test
    void testGetAllJokersAsNewList() {
        List<Joker> firstCall = JokerDeck.getAllJokers();
        List<Joker> secondCall = JokerDeck.getAllJokers();
        
        assertNotNull(firstCall);
        assertNotNull(secondCall);
        assertEquals(firstCall.size(), secondCall.size());
        assertNotSame(firstCall, secondCall);
    }

    @Test
    void testGetRandomJokersWithValidCount() {
        int requestedCount = 5;
        List<Joker> randomJokers = JokerDeck.getRandomJokers(requestedCount);
        
        assertNotNull(randomJokers);
        assertEquals(requestedCount, randomJokers.size());
        
        for (Joker joker : randomJokers) {
            assertTrue(allJokers.contains(joker));
        }
    }

    @Test
    void testHandleRandomJokersExceedingAvailableCount() {
        int largeCount = allJokers.size() + 10;
        List<Joker> randomJokers = JokerDeck.getRandomJokers(largeCount);
        
        assertNotNull(randomJokers);
        assertEquals(allJokers.size(), randomJokers.size());
    }

    @Test
    void testHandleZeroCountForRandomJokers() {
        List<Joker> randomJokers = JokerDeck.getRandomJokers(0);
        
        assertNotNull(randomJokers);
        assertTrue(randomJokers.isEmpty());
    }

    @Test
    void testReturnNullForNonExistentJokerName() {
        Joker joker = JokerDeck.getJokerByName("Non-Existent Joker");
        
        assertNull(joker);
    }

    @Test
    void testHandleNullJokerName() {
        Joker joker = JokerDeck.getJokerByName(null);
        
        assertNull(joker);
    }

    @Test
    void testVerifySpecificJokerEffects() {
        Joker doubleVision = JokerDeck.getJokerByName("Double Vision");
        assertNotNull(doubleVision);
        assertEquals(Joker.EffectType.SCORE_MODIFIER, doubleVision.getEffect().getType());
        
        Joker flowerChild = JokerDeck.getJokerByName("Flower Child");
        assertNotNull(flowerChild);
        assertEquals(Joker.EffectType.CARD_FILTER, flowerChild.getEffect().getType());
        
    }

    @Test
    void testVerifyJokerDescriptionsAreNotEmpty() {
        for (Joker joker : allJokers) {
            assertNotNull(joker.getDescription());
            assertFalse(joker.getDescription().trim().isEmpty());
        }
    }

    @Test
    void testVerifyJokerNamesAreUnique() {
        long uniqueNameCount = allJokers.stream()
            .map(Joker::getName)
            .distinct()
            .count();
        
        assertEquals(allJokers.size(), uniqueNameCount);
    }

    @Test
    void testVerifyAllJokersHaveValidEffects() {
        for (Joker joker : allJokers) {
            Joker.Effect effect = joker.getEffect();
            assertNotNull(effect);
            assertNotNull(effect.getType());
            
            switch (effect.getType()) {
                case SCORE_MODIFIER:
                    assertNotNull(effect.getScoreModifier());
                    break;
                case DRAW_MODIFIER:
                    assertNotNull(effect.getDrawModifier());
                    break;
                case CARD_FILTER:
                    assertNotNull(effect.getCardFilter());
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    void testTestTheGamblerJokerWithHighCardHandType() {
        Joker gambler = JokerDeck.getJokerByName("The Gambler");
        assertNotNull(gambler);
        
        Joker.ScoreModifier scoreModifier = gambler.getEffect().getScoreModifier();
        assertNotNull(scoreModifier);
        
        List<Card> sampleCards = List.of(
            new Card(Card.Suit.HEARTS, Card.Rank.TWO),
            new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
            new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)
        );
        
        int baseScore = 50;
        int modifiedScore = scoreModifier.apply(HandEvaluator.HandType.HIGH_CARD, baseScore, sampleCards);
        assertEquals(100, modifiedScore);
    }

    @Test
    void testTestTheGamblerJokerWithNonHighCardHandType() {
        Joker gambler = JokerDeck.getJokerByName("The Gambler");
        assertNotNull(gambler);
        
        Joker.ScoreModifier scoreModifier = gambler.getEffect().getScoreModifier();
        assertNotNull(scoreModifier);
        
        List<HandEvaluator.HandType> nonHighCardTypes = List.of(
            HandEvaluator.HandType.PAIR,
            HandEvaluator.HandType.TWO_PAIR,
            HandEvaluator.HandType.THREE_OF_A_KIND,
            HandEvaluator.HandType.STRAIGHT,
            HandEvaluator.HandType.FLUSH,
            HandEvaluator.HandType.FULL_HOUSE,
            HandEvaluator.HandType.FOUR_OF_A_KIND,
            HandEvaluator.HandType.STRAIGHT_FLUSH,
            HandEvaluator.HandType.ROYAL_FLUSH
        );
        
        List<Card> sampleCards = List.of(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.CLUBS, Card.Rank.KING)
        );
        
        int baseScore = 100;
        
        for (HandEvaluator.HandType handType : nonHighCardTypes) {
            int modifiedScore = scoreModifier.apply(handType, baseScore, sampleCards);
            assertEquals(baseScore, modifiedScore);
        }
    }
    
    @Test
    void testTestFlowerChildJokerCardFilter() {
        Joker flowerChild = JokerDeck.getJokerByName("Flower Child");
        assertNotNull(flowerChild);
        
        Joker.CardFilter cardFilter = flowerChild.getEffect().getCardFilter();
        assertNotNull(cardFilter);
        
        Card clubsCard = new Card(Card.Suit.CLUBS, Card.Rank.ACE);
        assertTrue(cardFilter.test(clubsCard));
        
        Card heartsCard = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        assertFalse(cardFilter.test(heartsCard));
        
        Card diamondsCard = new Card(Card.Suit.DIAMONDS, Card.Rank.KING);
        assertFalse(cardFilter.test(diamondsCard));
        
        Card spadesCard = new Card(Card.Suit.SPADES, Card.Rank.QUEEN);
        assertFalse(cardFilter.test(spadesCard));
    }
}
