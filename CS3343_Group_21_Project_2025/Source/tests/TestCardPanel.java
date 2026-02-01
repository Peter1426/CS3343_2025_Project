package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import ui.CardPanel;
import model.Card;
import model.Card.Suit;
import model.Card.Rank;
import java.awt.Dimension;

public class TestCardPanel {

    @Test
    public void testCardPanelCreation() {
        // Test basic CardPanel creation
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        CardPanel panel = new CardPanel(card);
        
        assertNotNull("CardPanel should be created successfully", panel);
        assertEquals("Card should be stored correctly", card, panel.getCard());
        assertFalse("Initially should not be selected", panel.isSelected());
        
        // Test preferred size
        Dimension size = panel.getPreferredSize();
        assertNotNull("Preferred size should not be null", size);
        assertEquals("Width should match CARD_WIDTH", 80, size.width);
        assertEquals("Height should match CARD_HEIGHT", 120, size.height);
    }

    @Test
    public void testSelectionState() {
        // Test selection state changes
        Card card = new Card(Suit.SPADES, Rank.KING);
        CardPanel panel = new CardPanel(card);
        
        // Test initial state
        assertFalse("Should not be selected initially", panel.isSelected());
        
        // Test setting selected
        panel.setSelected(true);
        assertTrue("Should be selected after setSelected(true)", panel.isSelected());
        
        // Test setting unselected
        panel.setSelected(false);
        assertFalse("Should not be selected after setSelected(false)", panel.isSelected());
        
        // Test toggle
        panel.setSelected(true);
        panel.setSelected(true); // Set same state again
        assertTrue("Should remain selected when set to same state", panel.isSelected());
    }

    @Test
    public void testGetCard() {
        // Test card retrieval
        Card originalCard = new Card(Suit.DIAMONDS, Rank.QUEEN);
        CardPanel panel = new CardPanel(originalCard);
        
        Card retrievedCard = panel.getCard();
        assertNotNull("Retrieved card should not be null", retrievedCard);
        assertEquals("Retrieved card should match original", originalCard, retrievedCard);
        
        // Test that it's the same object reference
        assertSame("Should return the same card object", originalCard, retrievedCard);
    }

    @Test
    public void testDifferentCards() {
        // Test CardPanel with different card types
        Card heartsAce = new Card(Suit.HEARTS, Rank.ACE);
        Card spadesKing = new Card(Suit.SPADES, Rank.KING);
        Card clubsTwo = new Card(Suit.CLUBS, Rank.TWO);
        Card diamondsTen = new Card(Suit.DIAMONDS, Rank.TEN);
        
        CardPanel panel1 = new CardPanel(heartsAce);
        CardPanel panel2 = new CardPanel(spadesKing);
        CardPanel panel3 = new CardPanel(clubsTwo);
        CardPanel panel4 = new CardPanel(diamondsTen);
        
        assertEquals("Panel1 should have hearts ace", heartsAce, panel1.getCard());
        assertEquals("Panel2 should have spades king", spadesKing, panel2.getCard());
        assertEquals("Panel3 should have clubs two", clubsTwo, panel3.getCard());
        assertEquals("Panel4 should have diamonds ten", diamondsTen, panel4.getCard());
    }

    @Test
    public void testSelectionIndependence() {
        // Test that selection state is independent between panels
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.KING);
        
        CardPanel panel1 = new CardPanel(card1);
        CardPanel panel2 = new CardPanel(card2);
        
        // Select only panel1
        panel1.setSelected(true);
        
        assertTrue("Panel1 should be selected", panel1.isSelected());
        assertFalse("Panel2 should not be selected", panel2.isSelected());
        
        // Select panel2, panel1 should remain selected
        panel2.setSelected(true);
        
        assertTrue("Panel1 should remain selected", panel1.isSelected());
        assertTrue("Panel2 should be selected", panel2.isSelected());
        
        // Deselect panel1 only
        panel1.setSelected(false);
        
        assertFalse("Panel1 should not be selected", panel1.isSelected());
        assertTrue("Panel2 should remain selected", panel2.isSelected());
    }

    @Test
    public void testNullCard() {
        // Test behavior with null card (shouldn't normally happen, but test for robustness)
        try {
            CardPanel panel = new CardPanel(null);
            assertNotNull("CardPanel should still be created", panel);
            assertNull("Card should be null", panel.getCard());
            
            // These should not throw exceptions
            panel.setSelected(true);
            panel.setSelected(false);
            panel.isSelected();
            
        } catch (Exception e) {
            fail("CardPanel should handle null card without throwing exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testToolTipText() {
        // Test tool tip text setting - FIXED: Tooltip is set in paintComponent, not in constructor
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        CardPanel panel = new CardPanel(card);
        
        // Tooltip is initially null until paintComponent is called
        // This is expected behavior since paintComponent sets the tooltip
        assertNull("Tooltip should be null initially (set in paintComponent)", panel.getToolTipText());
        
        // We can set it explicitly and test that it works
        panel.setToolTipText("Click to select/deselect");
        assertEquals("Tooltip text should be set when explicitly set", 
                     "Click to select/deselect", panel.getToolTipText());
    }

    @Test
    public void testOpaqueProperty() {
        // Test that CardPanel is not opaque (for custom painting)
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        CardPanel panel = new CardPanel(card);
        
        assertFalse("CardPanel should not be opaque", panel.isOpaque());
    }

    @Test
    public void testMultipleSelectionChanges() {
        // Test rapid selection state changes
        Card card = new Card(Suit.CLUBS, Rank.QUEEN);
        CardPanel panel = new CardPanel(card);
        
        // Rapid toggling
        for (int i = 0; i < 10; i++) {
            panel.setSelected(i % 2 == 0);
            assertEquals("Selection state should match set value", i % 2 == 0, panel.isSelected());
        }
        
        // Final state check
        panel.setSelected(true);
        assertTrue("Final state should be selected", panel.isSelected());
    }
    
    @Test
    public void testCardPanelEdgeCases() {
        // Test edge cases for CardPanel
        Card card = new Card(Suit.CLUBS, Rank.TWO);
        CardPanel panel = new CardPanel(card);
        
        // Test multiple selection changes to cover all branches
        panel.setSelected(true);
        assertTrue("Should be selected after setSelected(true)", panel.isSelected());
        
        panel.setSelected(false);
        assertFalse("Should not be selected after setSelected(false)", panel.isSelected());
        
        panel.setSelected(true);
        panel.setSelected(true); // Set to same state
        assertTrue("Should remain selected when set to same state", panel.isSelected());
        
        // Test that getCard returns the same reference
        assertSame("getCard should return the same card object", card, panel.getCard());
        
        // Tooltip should be null since paintComponent hasn't been called
        assertNull("Tooltip should be null in unit test environment", panel.getToolTipText());
    }

}