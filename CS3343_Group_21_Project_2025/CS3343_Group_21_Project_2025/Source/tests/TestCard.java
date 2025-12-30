package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import model.Card;
import model.Card.Suit;
import model.Card.Rank;
import java.util.Arrays;
import java.util.List;

public class TestCard {

    @Test
    public void testCardCreation() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertNotNull("Card should be created successfully", card);
        assertEquals("Suit should be HEARTS", Suit.HEARTS, card.getSuit());
        assertEquals("Rank should be ACE", Rank.ACE, card.getRank());
    }

    @Test
    public void testGetValue() {
        Card two = new Card(Suit.SPADES, Rank.TWO);
        Card ace = new Card(Suit.HEARTS, Rank.ACE);
        
        assertEquals("TWO should have value 2", 2, two.getValue());
        assertEquals("ACE should have value 14", 14, ace.getValue());
    }

    @Test
    public void testIsRed() {
        Card hearts = new Card(Suit.HEARTS, Rank.ACE);
        Card diamonds = new Card(Suit.DIAMONDS, Rank.KING);
        Card clubs = new Card(Suit.CLUBS, Rank.QUEEN);
        Card spades = new Card(Suit.SPADES, Rank.JACK);
        
        assertTrue("HEARTS should be red", hearts.isRed());
        assertTrue("DIAMONDS should be red", diamonds.isRed());
        assertFalse("CLUBS should not be red", clubs.isRed());
        assertFalse("SPADES should not be red", spades.isRed());
    }

    @Test
    public void testIsBlack() {
        Card hearts = new Card(Suit.HEARTS, Rank.ACE);
        Card diamonds = new Card(Suit.DIAMONDS, Rank.KING);
        Card clubs = new Card(Suit.CLUBS, Rank.QUEEN);
        Card spades = new Card(Suit.SPADES, Rank.JACK);
        
        assertFalse("HEARTS should not be black", hearts.isBlack());
        assertFalse("DIAMONDS should not be black", diamonds.isBlack());
        assertTrue("CLUBS should be black", clubs.isBlack());
        assertTrue("SPADES should be black", spades.isBlack());
    }

    @Test
    public void testIsClubs() {
        Card hearts = new Card(Suit.HEARTS, Rank.ACE);
        Card clubs = new Card(Suit.CLUBS, Rank.QUEEN);
        
        assertFalse("HEARTS should not be clubs", hearts.isClubs());
        assertTrue("CLUBS should be clubs", clubs.isClubs());
    }

    @Test
    public void testToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        String result = card.toString();
        assertTrue("ToString should contain rank", result.contains("A"));
    }

    @Test
    public void testEquals() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.ACE);
        Card card3 = new Card(Suit.SPADES, Rank.ACE);
        
        // Test reflexivity
        assertEquals("Card should equal itself", card1, card1);
        
        // Test symmetry
        assertEquals("Equal cards should be equal", card1, card2);
        assertEquals("Equal cards should be equal", card2, card1);
        
        // Test different cards
        assertNotEquals("Different suits should not be equal", card1, card3);
        
        // Test null
        assertNotEquals("Card should not equal null", card1, null);
        
        // Test different type
        assertNotEquals("Card should not equal different type", card1, "Not a card");
    }

    @Test
    public void testSuitGetColor() {
        // Test getColor method for all suits
        assertEquals("HEARTS color should be red", "Red", Suit.HEARTS.getColor());
        assertEquals("DIAMONDS color should be red", "Red", Suit.DIAMONDS.getColor());
        assertEquals("CLUBS color should be black", "Black", Suit.CLUBS.getColor());
        assertEquals("SPADES color should be black", "Black", Suit.SPADES.getColor());
    }

    @Test
    public void testEqualsComprehensive() {
        // Comprehensive equals method testing
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.ACE);
        Card card3 = new Card(Suit.SPADES, Rank.ACE);
        Card card4 = new Card(Suit.HEARTS, Rank.KING);
        
        // Test null comparison
        assertFalse("Card should not equal null", card1.equals(null));
        
        // Test different class comparison
        assertFalse("Card should not equal different class", card1.equals("Not a card"));
        assertFalse("Card should not equal different class", card1.equals(Integer.valueOf(1)));
        
        // Test same object
        assertTrue("Card should equal itself", card1.equals(card1));
        
        // Test equal objects
        assertTrue("card1 should equal card2", card1.equals(card2));
        assertTrue("card2 should equal card1", card2.equals(card1));
        
        // Test different suit
        assertFalse("Different suit should not be equal", card1.equals(card3));
        
        // Test different rank
        assertFalse("Different rank should not be equal", card1.equals(card4));
        
        // Test transitivity
        Card card5 = new Card(Suit.HEARTS, Rank.ACE);
        assertTrue("card1 should equal card2", card1.equals(card2));
        assertTrue("card2 should equal card5", card2.equals(card5));
        assertTrue("card1 should equal card5", card1.equals(card5));
    }

    @Test
    public void testEqualsWithNullSuitOrRank() {
        // Test edge cases that might cause NullPointerException
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        
        // The equals method should handle null parameter gracefully
        boolean result = card.equals(null);
        assertFalse("equals(null) should return false", result);
    }

    @Test
    public void testAllRankValues() {
        // Test all rank values to ensure full coverage
        for (Rank rank : Rank.values()) {
            Card card = new Card(Suit.HEARTS, rank);
            assertNotNull("Card with " + rank + " should be created", card);
            assertEquals("Rank should match", rank, card.getRank());
            
            // Test specific value ranges to cover both true and false branches
            if (rank == Rank.TWO) {
                assertEquals("TWO should have value 2", 2, card.getValue());
            } else if (rank == Rank.ACE) {
                assertEquals("ACE should have value 14", 14, card.getValue());
            }
            
            // This should always be true for valid cards, but let's test boundaries
            assertTrue("Value should be at least 2", card.getValue() >= 2);
            assertTrue("Value should be at most 14", card.getValue() <= 14);
        }
    }

    @Test
    public void testAllSuitProperties() {
        // Test all suit properties comprehensively
        for (Suit suit : Suit.values()) {
            Card card = new Card(suit, Rank.ACE);
            
            // Test symbol is not null or empty
            assertNotNull("Suit symbol should not be null", suit.getSymbol());
            assertFalse("Suit symbol should not be empty", suit.getSymbol().isEmpty());
            
            // Test color is either "Red" or "Black" - break into separate conditions
            boolean isRed = suit.getColor().equalsIgnoreCase("Red");
            boolean isBlack = suit.getColor().equalsIgnoreCase("Black");
            assertTrue("Suit color should be either red or black", isRed || isBlack);
            
            // Test that only one color is true
            assertTrue("Suit should be either red or black, not both", isRed != isBlack);
            
            // Test isRed and isBlack are opposites
            assertEquals("isRed should be opposite of isBlack", suit.isRed(), !suit.isBlack());
            
            // Test specific suit properties with separate assertions
            if (suit == Suit.HEARTS || suit == Suit.DIAMONDS) {
                assertTrue(suit + " should be red", suit.isRed());
                assertFalse(suit + " should not be black", suit.isBlack());
            } else {
                assertFalse(suit + " should not be red", suit.isRed());
                assertTrue(suit + " should be black", suit.isBlack());
            }
        }
    }

    @Test
    public void testSuitGetColorComprehensive() {
        // Test getColor for all suits individually
        assertEquals("HEARTS should be red", "Red", Suit.HEARTS.getColor());
        assertEquals("DIAMONDS should be red", "Red", Suit.DIAMONDS.getColor());
        assertEquals("CLUBS should be black", "Black", Suit.CLUBS.getColor());
        assertEquals("SPADES should be black", "Black", Suit.SPADES.getColor());
        
        // Test that the color is consistent with isRed/isBlack
        for (Suit suit : Suit.values()) {
            if ("Red".equalsIgnoreCase(suit.getColor())) {
                assertTrue(suit + " should be red", suit.isRed());
                assertFalse(suit + " should not be black", suit.isBlack());
            } else {
                assertFalse(suit + " should not be red", suit.isRed());
                assertTrue(suit + " should be black", suit.isBlack());
            }
        }
    }
}
