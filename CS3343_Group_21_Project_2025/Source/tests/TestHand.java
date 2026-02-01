package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import model.Hand;
import model.Card;
import model.Card.Suit;
import model.Card.Rank;
import java.util.List;

import java.util.Arrays;

public class TestHand {

    @Test
    public void testHandCreation() {
        Hand hand = new Hand(5);
        assertNotNull("Hand should be created successfully", hand);
        assertEquals("Initial size should be 0", 0, hand.size());
        assertEquals("Max size should be 5", 5, hand.getMaxSize());
        assertTrue("New hand should be empty", hand.isEmpty());
    }

    @Test
    public void testAddCardWithinLimit() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        
        hand.addCard(card1);
        assertEquals("Size should be 1 after adding first card", 1, hand.size());
        assertFalse("Hand should not be empty", hand.isEmpty());
        
        hand.addCard(card2);
        assertEquals("Size should be 2 after adding second card", 2, hand.size());
    }

    @Test
    public void testAddCardBeyondLimit() {
        Hand hand = new Hand(2);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3); // Attempt to add beyond limit
        
        assertEquals("Size should remain 2", 2, hand.size());
        assertTrue("Hand should be full", hand.isFull());
    }

    @Test
    public void testGetCards() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        
        hand.addCard(card1);
        hand.addCard(card2);
        
        List<Card> cards = hand.getCards();
        assertEquals("Should return 2 cards", 2, cards.size());
        assertTrue("Should contain card1", cards.contains(card1));
        assertTrue("Should contain card2", cards.contains(card2));
    }

    @Test
    public void testRemoveCards() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        
        java.util.List<Card> toRemove = java.util.Arrays.asList(card2);
        hand.removeCards(toRemove);
        
        assertEquals("Size should be 2 after removal", 2, hand.size());
        List<Card> remaining = hand.getCards();
        assertTrue("Should contain card1", remaining.contains(card1));
        assertFalse("Should not contain card2", remaining.contains(card2));
        assertTrue("Should contain card3", remaining.contains(card3));
    }

    @Test
    public void testClear() {
        Hand hand = new Hand(3);
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        assertEquals("Size should be 2 before clear", 2, hand.size());
        hand.clear();
        assertEquals("Size should be 0 after clear", 0, hand.size());
        assertTrue("Hand should be empty after clear", hand.isEmpty());
    }
    
    @Test
    public void testAddCardsPartialAddition() {
        // Test addCards when only some cards can be added due to size limit
        Hand hand = new Hand(2);
        List<Card> cardsToAdd = Arrays.asList(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING),
            new Card(Suit.CLUBS, Rank.QUEEN) // This one shouldn't be added
        );
        
        hand.addCards(cardsToAdd);
        
        assertEquals("Should only add up to max size", 2, hand.size());
        assertTrue("Hand should be full", hand.isFull());
        
        List<Card> currentCards = hand.getCards();
        assertTrue("Should contain first card", currentCards.contains(cardsToAdd.get(0)));
        assertTrue("Should contain second card", currentCards.contains(cardsToAdd.get(1)));
        assertFalse("Should not contain third card", currentCards.contains(cardsToAdd.get(2)));
    }

    @Test
    public void testForceAddCardBeyondLimit() {
        // Test forceAddCard adds cards beyond normal max size
        Hand hand = new Hand(2);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        
        // Normal add should not work
        hand.addCard(card3);
        assertEquals("Normal add should not exceed max size", 2, hand.size());
        
        // Force add should work
        hand.forceAddCard(card3);
        assertEquals("Force add should exceed max size", 3, hand.size());
        
        // According to the isFull() implementation: return cards.size() >= maxSize;
        // So with 3 cards and maxSize=2, isFull() should return TRUE
        assertTrue("Hand should be full when card count >= maxSize", hand.isFull());
    }

    @Test
    public void testSetMaxSizeEdgeCases() {
        // Test setMaxSize with various edge cases
        Hand hand = new Hand(3);
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        // Test setting to same size
        hand.setMaxSize(3);
        assertEquals("Max size should remain 3", 3, hand.getMaxSize());
        assertEquals("Card count should remain 2", 2, hand.size());
        assertFalse("Hand should not be full when card count < maxSize", hand.isFull());
        
        // Test setting to smaller than current cards
        hand.setMaxSize(1);
        assertEquals("Max size should be updated to 1", 1, hand.getMaxSize());
        assertEquals("Card count should remain 2 (no auto-removal)", 2, hand.size());
        
        // According to isFull(): cards.size() >= maxSize, so 2 >= 1 = TRUE
        assertTrue("Hand should be full when card count >= maxSize", hand.isFull());
        
        // Test setting to zero
        hand.setMaxSize(0);
        assertEquals("Max size should be 0", 0, hand.getMaxSize());
        
        // 2 >= 0 = TRUE
        assertTrue("Zero max size hand should be full when cards present", hand.isFull());
    }

    @Test
    public void testIsFullEdgeCases() {
        // Test isFull with various scenarios
        Hand emptyHand = new Hand(3);
        assertFalse("Empty hand should not be full", emptyHand.isFull());
        
        Hand partiallyFull = new Hand(3);
        partiallyFull.addCard(new Card(Suit.HEARTS, Rank.ACE));
        assertFalse("Partially full hand should not be full", partiallyFull.isFull());
        
        Hand exactlyFull = new Hand(2);
        exactlyFull.addCard(new Card(Suit.HEARTS, Rank.ACE));
        exactlyFull.addCard(new Card(Suit.SPADES, Rank.KING));
        assertTrue("Exactly full hand should be full", exactlyFull.isFull());
        
        Hand overFull = new Hand(1);
        overFull.addCard(new Card(Suit.HEARTS, Rank.ACE));
        overFull.forceAddCard(new Card(Suit.SPADES, Rank.KING));
        
        // 2 >= 1 = TRUE
        assertTrue("Over-full hand should be full when card count >= maxSize", overFull.isFull());
        
        Hand zeroSizeHand = new Hand(0);
        // 0 >= 0 = TRUE
        assertTrue("Zero max size hand should be full", zeroSizeHand.isFull());
        
        // Additional test: zero size hand with no cards
        Hand zeroSizeEmptyHand = new Hand(0);
        // 0 >= 0 = TRUE
        assertTrue("Zero size empty hand should be full", zeroSizeEmptyHand.isFull());
    }
    
    @Test
    public void testRemoveFirstCard() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        
        Card removed = hand.removeCard(0);
        assertEquals("Should remove first card", card1, removed);
        assertEquals("Size should decrease by 1", 2, hand.size());
        assertFalse("Should not contain removed card", hand.getCards().contains(card1));
        assertTrue("Should still contain other cards", hand.getCards().contains(card2));
        assertTrue("Should still contain other cards", hand.getCards().contains(card3));
    }

    @Test
    public void testRemoveMiddleCard() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        
        Card removed = hand.removeCard(1); // Remove middle card (card2)
        assertEquals("Should remove middle card", card2, removed);
        assertEquals("Size should decrease by 1", 2, hand.size());
        assertTrue("Should still contain first card", hand.getCards().contains(card1));
        assertFalse("Should not contain removed card", hand.getCards().contains(card2));
        assertTrue("Should still contain last card", hand.getCards().contains(card3));
    }

    @Test
    public void testRemoveLastCard() {
        Hand hand = new Hand(3);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        
        int lastIndex = hand.size() - 1;
        Card removed = hand.removeCard(lastIndex);
        assertEquals("Should remove last card", card3, removed);
        assertEquals("Size should decrease by 1", 2, hand.size());
        assertTrue("Should still contain first card", hand.getCards().contains(card1));
        assertTrue("Should still contain second card", hand.getCards().contains(card2));
        assertFalse("Should not contain removed card", hand.getCards().contains(card3));
    }

    @Test
    public void testRemoveCardInvalidIndex() {
        // Test all types of invalid indices
        Hand hand = new Hand(2);
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        // Test negative index
        Card result = hand.removeCard(-1);
        assertNull("Should return null for negative index", result);
        assertEquals("Size should remain unchanged", 2, hand.size());
        
        // Test index equal to size (just beyond last element)
        result = hand.removeCard(2);
        assertNull("Should return null for index equal to size", result);
        assertEquals("Size should remain unchanged", 2, hand.size());
        
        // Test index greater than size
        result = hand.removeCard(5);
        assertNull("Should return null for index greater than size", result);
        assertEquals("Size should remain unchanged", 2, hand.size());
        
        // Test with empty hand
        Hand emptyHand = new Hand(3);
        result = emptyHand.removeCard(0);
        assertNull("Should return null for any index on empty hand", result);
        result = emptyHand.removeCard(-1);
        assertNull("Should return null for negative index on empty hand", result);
    }

    @Test
    public void testRemoveCardBoundaryConditions() {
        // Test boundary conditions for removeCard
        Hand hand = new Hand(3);
        
        // Test with single card hand
        Card singleCard = new Card(Suit.HEARTS, Rank.ACE);
        hand.addCard(singleCard);
        
        Card removed = hand.removeCard(0);
        assertEquals("Should remove the only card", singleCard, removed);
        assertEquals("Hand should be empty after removal", 0, hand.size());
        assertTrue("Hand should be empty", hand.isEmpty());
        
        // Test removing from index 0 multiple times
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        hand.addCard(new Card(Suit.CLUBS, Rank.QUEEN));
        
        // Remove from beginning multiple times
        hand.removeCard(0); // Remove first card
        assertEquals("Size should be 2 after first removal", 2, hand.size());
        hand.removeCard(0); // Remove new first card  
        assertEquals("Size should be 1 after second removal", 1, hand.size());
        hand.removeCard(0); // Remove last card
        assertEquals("Size should be 0 after third removal", 0, hand.size());
        assertTrue("Hand should be empty", hand.isEmpty());
    }

    @Test
    public void testRemoveCardMaintainsOrder() {
        // Test that removal maintains proper order of remaining cards
        Hand hand = new Hand(5);
        List<Card> originalCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING), 
            new Card(Suit.CLUBS, Rank.QUEEN),
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.HEARTS, Rank.TEN)
        );
        
        for (Card card : originalCards) {
            hand.addCard(card);
        }
        
        // Remove middle card (index 2 - QUEEN)
        Card removed = hand.removeCard(2);
        assertEquals("Should remove QUEEN", originalCards.get(2), removed);
        
        List<Card> remaining = hand.getCards();
        assertEquals("Should have 4 cards remaining", 4, remaining.size());
        
        // Verify order is maintained: ACE, KING, JACK, TEN
        assertEquals("First card should be ACE", originalCards.get(0), remaining.get(0));
        assertEquals("Second card should be KING", originalCards.get(1), remaining.get(1));
        assertEquals("Third card should be JACK", originalCards.get(3), remaining.get(2));
        assertEquals("Fourth card should be TEN", originalCards.get(4), remaining.get(3));
    }

    @Test
    public void testRemoveCardAndIsFullInteraction() {
        // Test interaction between removeCard and isFull
        Hand hand = new Hand(2);
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        
        hand.addCard(card1);
        hand.addCard(card2);
        
        assertTrue("Hand should be full before removal", hand.isFull());
        
        // Remove a card
        Card removed = hand.removeCard(0);
        assertNotNull("Should successfully remove card", removed);
        assertEquals("Size should be 1 after removal", 1, hand.size());
        assertFalse("Hand should not be full after removal", hand.isFull());
        
        // Add card back
        hand.addCard(card1);
        assertTrue("Hand should be full again", hand.isFull());
    }
    
    @Test
    public void testRemoveCardAllScenarios() {
        // Comprehensive test covering all removeCard scenarios
        Hand hand = new Hand(4);
        
        // Test 1: Remove from empty hand
        Card result = hand.removeCard(0);
        assertNull("Should return null when removing from empty hand", result);
        
        // Add some cards
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        Card card3 = new Card(Suit.CLUBS, Rank.QUEEN);
        
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        
        // Test 2: Remove valid middle index
        result = hand.removeCard(1);
        assertEquals("Should remove middle card", card2, result);
        assertEquals("Size should be 2", 2, hand.size());
        
        // Test 3: Remove last index
        result = hand.removeCard(1); // Now only card1 and card3 remain, card3 is at index 1
        assertEquals("Should remove last card", card3, result);
        assertEquals("Size should be 1", 1, hand.size());
        
        // Test 4: Remove first and only card
        result = hand.removeCard(0);
        assertEquals("Should remove first card", card1, result);
        assertEquals("Size should be 0", 0, hand.size());
        assertTrue("Hand should be empty", hand.isEmpty());
        
        // Test 5: Try to remove from empty hand again
        result = hand.removeCard(0);
        assertNull("Should return null when removing from empty hand", result);
    }
}