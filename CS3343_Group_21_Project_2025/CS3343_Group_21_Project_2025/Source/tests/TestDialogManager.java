package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.DialogManager;
import model.Card;
import model.Card.Suit;
import model.Card.Rank;
import model.Joker;
import data.JokerDeck;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;

class TestDialogManager {

    private JFrame parentFrame;
    private List<Card> testCards;
    private List<Joker> testJokers;

    @BeforeEach
    void setUp() {
        parentFrame = new JFrame();
        parentFrame.setSize(100, 100);
        testCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING)
        );
        testJokers = JokerDeck.getRandomJokers(2);
    }

    // Core functionality tests
    @Test
    void testCardSelectionDialog_EdgeCases() {
        // Covers: null cards, empty cards, valid cards (MC/DC for cards == null || cards.isEmpty())
        assertNull(DialogManager.showCardSelectionDialog(parentFrame, null, "Test", "Test"));
        assertNull(DialogManager.showCardSelectionDialog(parentFrame, new ArrayList<>(), "Test", "Test"));
        assertDoesNotThrow(() -> DialogManager.showCardSelectionDialog(parentFrame, testCards, "Test", "Test"));
    }

    @Test
    void testRewardDialog_AllActionCombinations() {
        // MC/DC coverage for both action conditions
        String status = "Test";
        Runnable action = () -> {};
        
        assertDoesNotThrow(() -> DialogManager.showRewardDialog(parentFrame, status, action, action));
        assertDoesNotThrow(() -> DialogManager.showRewardDialog(parentFrame, status, action, null));
        assertDoesNotThrow(() -> DialogManager.showRewardDialog(parentFrame, status, null, action));
        assertDoesNotThrow(() -> DialogManager.showRewardDialog(parentFrame, status, null, null));
    }

    @Test
    void testShopDialog_Functionality() {
        // Covers: with jokers, empty jokers, null listeners
        ActionListener listener = e -> {};
        assertDoesNotThrow(() -> DialogManager.showShopDialog(parentFrame, testJokers, listener, listener));
        assertDoesNotThrow(() -> DialogManager.showShopDialog(parentFrame, new ArrayList<>(), listener, listener));
        assertDoesNotThrow(() -> DialogManager.showShopDialog(parentFrame, testJokers, null, null));
    }

    @Test 
    void testLevelTargetsDialog_BoundaryConditions() {
        // Covers: normal case, edge cases, empty array
        int[] targets = {50, 100, 150};
        int[] emptyTargets = {};
        
        assertDoesNotThrow(() -> DialogManager.showLevelTargetsDialog(parentFrame, 1, 1, 0, targets, 5));
        assertDoesNotThrow(() -> DialogManager.showLevelTargetsDialog(parentFrame, 5, 3, 500, targets, 5));
        assertDoesNotThrow(() -> DialogManager.showLevelTargetsDialog(parentFrame, 1, 1, 0, emptyTargets, 5));
    }

    @Test
    void testHandTypeRulesDialog() {
        // Basic functionality
        assertDoesNotThrow(() -> DialogManager.showHandTypeRulesDialog(parentFrame));
        assertDoesNotThrow(() -> DialogManager.showHandTypeRulesDialog(null));
    }

    @Test
    void testHelperMethods_ButtonAndLabel() {
        // Covers button and label creation with various parameters
        JButton button = invokeCreateStyledButton("Test", java.awt.Color.BLUE);
        assertNotNull(button);
        assertEquals("Test", button.getText());
        
        JButton nullButton = invokeCreateStyledButton(null, java.awt.Color.RED);
        assertNotNull(nullButton);
        assertEquals("", nullButton.getText());
        
        JLabel label = invokeCreateStyledLabel("Test", null, null, null);
        assertNotNull(label);
        assertEquals("Test", label.getText());
    }

    @Test
    void testJokerShopPanel_CoreScenarios() {
        // Covers: available jokers, no available jokers (all owned)
        assertDoesNotThrow(() -> {
            JDialog dialog = new JDialog(parentFrame);
            JPanel panel = invokeCreateJokerShopPanel(dialog, testJokers, e -> {});
            assertNotNull(panel);
        });
        
        // Test when all jokers are owned (empty available list)
        assertDoesNotThrow(() -> {
            JDialog dialog = new JDialog(parentFrame);
            List<Joker> allJokers = new ArrayList<>(JokerDeck.getAllJokers());
            JPanel panel = invokeCreateJokerShopPanel(dialog, allJokers, e -> {});
            assertNotNull(panel);
        });
    }

    @Test
    void testRemoveJokerPanel_CoreScenarios() {
        // Covers: with jokers, empty jokers
        assertDoesNotThrow(() -> {
            JPanel panel = invokeCreateRemoveJokerPanel(testJokers, e -> {});
            assertNotNull(panel);
        });
        
        assertDoesNotThrow(() -> {
            JPanel panel = invokeCreateRemoveJokerPanel(new ArrayList<>(), e -> {});
            assertNotNull(panel);
        });
    }

    @Test
    void testNullParentHandling() {
        // Covers null parent for all major dialog methods
        assertDoesNotThrow(() -> DialogManager.showCardSelectionDialog(null, testCards, "Test", "Test"));
        assertDoesNotThrow(() -> DialogManager.showRewardDialog(null, "Test", () -> {}, () -> {}));
        assertDoesNotThrow(() -> DialogManager.showShopDialog(null, testJokers, e -> {}, e -> {}));
        assertDoesNotThrow(() -> DialogManager.showLevelTargetsDialog(null, 1, 1, 0, new int[]{50}, 5));
        assertDoesNotThrow(() -> DialogManager.showHandTypeRulesDialog(null));
    }

    // Reflection helper methods (same as before)
    private JButton invokeCreateStyledButton(String text, java.awt.Color color) {
        try {
            var method = DialogManager.class.getDeclaredMethod("createStyledButton", String.class, java.awt.Color.class);
            method.setAccessible(true);
            return (JButton) method.invoke(null, text, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke createStyledButton: " + e.getMessage(), e);
        }
    }

    private JLabel invokeCreateStyledLabel(String text, java.awt.Font font, java.awt.Color foreground, java.awt.Color background) {
        try {
            var method = DialogManager.class.getDeclaredMethod("createStyledLabel", String.class, java.awt.Font.class, java.awt.Color.class, java.awt.Color.class);
            method.setAccessible(true);
            return (JLabel) method.invoke(null, text, font, foreground, background);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke createStyledLabel: " + e.getMessage(), e);
        }
    }

    private JPanel invokeCreateJokerShopPanel(JDialog parent, List<Joker> ownedJokers, ActionListener listener) {
        try {
            var method = DialogManager.class.getDeclaredMethod("createJokerShopPanel", JDialog.class, List.class, ActionListener.class);
            method.setAccessible(true);
            return (JPanel) method.invoke(null, parent, ownedJokers, listener);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke createJokerShopPanel: " + e.getMessage(), e);
        }
    }

    private JPanel invokeCreateRemoveJokerPanel(List<Joker> ownedJokers, ActionListener listener) {
        try {
            var method = DialogManager.class.getDeclaredMethod("createRemoveJokerPanel", List.class, ActionListener.class);
            method.setAccessible(true);
            return (JPanel) method.invoke(null, ownedJokers, listener);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke createRemoveJokerPanel: " + e.getMessage(), e);
        }
    }
}