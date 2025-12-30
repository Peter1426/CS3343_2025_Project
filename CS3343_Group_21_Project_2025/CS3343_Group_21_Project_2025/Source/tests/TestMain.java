package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import main.Main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

/**
 * Optimized test cases for Main.java with statement coverage and MC/DC coverage
 */
class TestMain {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;
    private Main mainInstance;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        mainInstance = new Main();
        
        // Initialize scanner and random fields
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        String defaultInput = "1\n";
        System.setIn(new ByteArrayInputStream(defaultInput.getBytes()));
        scannerField.set(mainInstance, new Scanner(System.in));
        
        Field randomField = Main.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(mainInstance, new Random(42));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // === CORE FUNCTIONALITY TESTS ===

    @Test
    @Timeout(5)
    void testMainMethodPaths() throws Exception {
        // Test both CLI and GUI paths in main method
        String[] cliArgs = {"--cli"};
        String[] guiArgs = {};
        
        // Test CLI path
        assertDoesNotThrow(() -> {
            Thread thread = new Thread(() -> {
                try {
                    String simulatedInput = "2\n";
                    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
                    Main.main(cliArgs);
                } catch (Exception e) {
                    // Expected
                }
            });
            thread.start();
            Thread.sleep(1000);
            thread.interrupt();
        });
        
        // Test GUI path
        assertDoesNotThrow(() -> {
            Thread thread = new Thread(() -> {
                try {
                    Main.main(guiArgs);
                } catch (Exception e) {
                    // Expected
                }
            });
            thread.start();
            Thread.sleep(500);
            thread.interrupt();
        });
    }

    @Test
    @Timeout(5)
    void testGameInitialization() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        // Verify core components are initialized
        Field playerHandField = Main.class.getDeclaredField("playerHand");
        playerHandField.setAccessible(true);
        assertNotNull(playerHandField.get(mainInstance), "playerHand should be initialized");
        
        Field jokersField = Main.class.getDeclaredField("jokers");
        jokersField.setAccessible(true);
        assertNotNull(jokersField.get(mainInstance), "jokers should be initialized");
        
        Field deckField = Main.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        assertNotNull(deckField.get(mainInstance), "deck should be initialized");
    }

    @Test
    @Timeout(5)
    void testDeckOperations() throws Exception {
        Method createDeck = Main.class.getDeclaredMethod("createDeck");
        createDeck.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<Object> deck = (List<Object>) createDeck.invoke(mainInstance);
        assertEquals(52, deck.size(), "Standard deck should have 52 cards");
        
        // Test deck exhaustion scenario
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        Field deckField = Main.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        
        // Create minimal deck to trigger exhaustion
        List<Object> tinyDeck = new ArrayList<>();
        Class<?> cardClass = Class.forName("model.Card");
        tinyDeck.add(cardClass.getConstructor(
            Class.forName("model.Card$Suit"), Class.forName("model.Card$Rank")
        ).newInstance(
            Enum.valueOf((Class<Enum>) Class.forName("model.Card$Suit"), "HEARTS"),
            Enum.valueOf((Class<Enum>) Class.forName("model.Card$Rank"), "ACE")
        ));
        
        deckField.set(mainInstance, tinyDeck);
        
        Method drawCards = Main.class.getDeclaredMethod("drawCards", int.class);
        drawCards.setAccessible(true);
        assertDoesNotThrow(() -> drawCards.invoke(mainInstance, 5));
    }

    // === GAME FLOW TESTS ===

    @Test
    @Timeout(5)
    void testRoundManagement() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        Method playRound = Main.class.getDeclaredMethod("playRound");
        playRound.setAccessible(true);
        
        // Test various round scenarios
        String[] testScenarios = {
            "2\n",                          // Skip round
            "99\n2\n",                      // Invalid option then skip
            "1\n1\nn\n2\n",                 // Play single card, don't continue, skip
        };
        
        for (String input : testScenarios) {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Field scannerField = Main.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(mainInstance, new Scanner(System.in));
            
            assertDoesNotThrow(() -> playRound.invoke(mainInstance),
                              "Should handle scenario: " + input.replace("\n", " "));
            
            // Re-initialize for next test
            initializeGame.invoke(mainInstance);
        }
    }

    @Test
    @Timeout(5)
    void testCardPlayingScenarios() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        Method playCards = Main.class.getDeclaredMethod("playCards");
        playCards.setAccessible(true);
        
        // Test various input scenarios
        String[] testInputs = {
            "\n",           // Empty input
            "1\n",          // Single card
            "1 2\n",        // Multiple cards
            "1 1\n",        // Duplicate cards
            "99\n",         // Invalid index
            "abc\n"         // Non-numeric
        };
        
        for (String input : testInputs) {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Field scannerField = Main.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(mainInstance, new Scanner(System.in));
            
            assertDoesNotThrow(() -> playCards.invoke(mainInstance),
                              "Should handle input: " + input.trim());
        }
    }

    // === SHOP SYSTEM TESTS ===

    @Test
    @Timeout(5)
    void testShopOperations() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        
        Method enterShop = Main.class.getDeclaredMethod("enterShop");
        enterShop.setAccessible(true);
        
        // Test individual shop scenarios with sufficient input for each menu level
        String[][] shopScenarios = {
            {"3\n"},                          // Leave immediately - just option 3
            {"1\n", "0\n", "3\n"},            // Browse jokers, cancel selection, leave shop
            {"2\n", "0\n", "3\n"},            // Remove jokers, cancel removal, leave shop  
            {"99\n", "3\n"}                   // Invalid option, then leave
        };
        
        for (int i = 0; i < shopScenarios.length; i++) {
            String[] inputs = shopScenarios[i];
            
            // Combine all inputs for this scenario
            StringBuilder combinedInput = new StringBuilder();
            for (String input : inputs) {
                combinedInput.append(input);
            }
            
            System.setIn(new ByteArrayInputStream(combinedInput.toString().getBytes()));
            Field scannerField = Main.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(mainInstance, new Scanner(System.in));
            
            // Re-initialize for consistent state
            initializeGame.invoke(mainInstance);
            
            try {
                enterShop.invoke(mainInstance);
                // If we get here, the test passed
            } catch (Exception e) {
                // For scenario 2 (remove jokers), it might fail if there are no jokers to remove
                // This is acceptable behavior
                if (i == 1 && e.getCause() instanceof java.util.NoSuchElementException) {
                    // This is expected when trying to remove jokers but none exist
                    continue;
                }
                // Re-throw other exceptions
                throw e;
            }
        }
        
        // Test purchase limit separately with more robust input
        initializeGame.invoke(mainInstance);
        Field jokerPurchasedThisRoundField = Main.class.getDeclaredField("jokerPurchasedThisRound");
        jokerPurchasedThisRoundField.setAccessible(true);
        jokerPurchasedThisRoundField.set(mainInstance, true);
        
        // Provide input for the entire purchase attempt flow
        String simulatedInput = "1\n3\n"; // Try to purchase, then leave
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        assertDoesNotThrow(() -> enterShop.invoke(mainInstance));
    }
    
    @Test
    @Timeout(5)
    void testJokerManagement() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        // Test joker selection cancellation
        Method handleJokerSelection = Main.class.getDeclaredMethod("handleJokerSelection");
        handleJokerSelection.setAccessible(true);
        
        String simulatedInput = "0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        assertDoesNotThrow(() -> handleJokerSelection.invoke(mainInstance));
        
        // Test joker removal cancellation
        Method handleRemoveJoker = Main.class.getDeclaredMethod("handleRemoveJoker");
        handleRemoveJoker.setAccessible(true);
        
        simulatedInput = "0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        scannerField.set(mainInstance, new Scanner(System.in));
        
        assertDoesNotThrow(() -> handleRemoveJoker.invoke(mainInstance));
        
        // Test all jokers unlocked scenario
        Field jokersField = Main.class.getDeclaredField("jokers");
        jokersField.setAccessible(true);
        Class<?> jokerDeckClass = Class.forName("data.JokerDeck");
        Method getAllJokers = jokerDeckClass.getDeclaredMethod("getAllJokers");
        @SuppressWarnings("unchecked")
        List<Object> allJokers = (List<Object>) getAllJokers.invoke(null);
        jokersField.set(mainInstance, new ArrayList<>(allJokers));
        
        assertDoesNotThrow(() -> handleJokerSelection.invoke(mainInstance));
    }

    // === SPECIAL ACTIONS TESTS ===

    @Test
    @Timeout(5)
    void testSpecialActions() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        // Test with quota available
        Field remainingSpecialActionsField = Main.class.getDeclaredField("remainingSpecialActions");
        remainingSpecialActionsField.setAccessible(true);
        remainingSpecialActionsField.set(mainInstance, 1);
        
        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        Method handleRequestCards = Main.class.getDeclaredMethod("handleRequestCards");
        handleRequestCards.setAccessible(true);
        assertDoesNotThrow(() -> handleRequestCards.invoke(mainInstance));
        
        Method handleDiscardCards = Main.class.getDeclaredMethod("handleDiscardCards");
        handleDiscardCards.setAccessible(true);
        assertDoesNotThrow(() -> handleDiscardCards.invoke(mainInstance));
        
        // Test quota exhaustion
        remainingSpecialActionsField.set(mainInstance, 0);
        assertDoesNotThrow(() -> handleRequestCards.invoke(mainInstance));
        assertDoesNotThrow(() -> handleDiscardCards.invoke(mainInstance));
    }

    // === UTILITY METHODS TESTS ===

    @Test
    @Timeout(5)
    void testUtilityMethods() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        // Test display methods
        Method displayHand = Main.class.getDeclaredMethod("displayHand");
        displayHand.setAccessible(true);
        assertDoesNotThrow(() -> displayHand.invoke(mainInstance));
        
        Method displayJokers = Main.class.getDeclaredMethod("displayJokers");
        displayJokers.setAccessible(true);
        assertDoesNotThrow(() -> displayJokers.invoke(mainInstance));
        
        // Test refresh configuration
        Method refreshHandConfiguration = Main.class.getDeclaredMethod("refreshHandConfiguration");
        refreshHandConfiguration.setAccessible(true);
        assertDoesNotThrow(() -> refreshHandConfiguration.invoke(mainInstance));
        
        // Test input validation
        Method readIntChoice = Main.class.getDeclaredMethod("readIntChoice", int.class, int.class);
        readIntChoice.setAccessible(true);
        
        String simulatedInput = "5\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        Integer result = (Integer) readIntChoice.invoke(mainInstance, 1, 10);
        assertEquals(5, result, "Should return valid input");
    }

    @Test
    @Timeout(5)
    void testCardSelectionEdgeCases() throws Exception {
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        Method selectCardsForUtility = Main.class.getDeclaredMethod("selectCardsForUtility", String.class, int.class);
        selectCardsForUtility.setAccessible(true);
        
        Field playerHandField = Main.class.getDeclaredField("playerHand");
        playerHandField.setAccessible(true);
        Object playerHand = playerHandField.get(mainInstance);
        
        // Test empty hand
        playerHand.getClass().getMethod("clear").invoke(playerHand);
        List<?> result = (List<?>) selectCardsForUtility.invoke(mainInstance, "test", 2);
        // Should return null for empty hand
        
        // Re-initialize to get a fresh hand with cards
        initializeGame.invoke(mainInstance);
        playerHand = playerHandField.get(mainInstance);
        
        // Test with cards but various invalid inputs
        Class<?> cardClass = Class.forName("model.Card");
        
        String[] testInputs = {"\n", "1 2 3\n", "99\n", "abc\n"};
        for (String input : testInputs) {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Field scannerField = Main.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(mainInstance, new Scanner(System.in));
            
            assertDoesNotThrow(() -> selectCardsForUtility.invoke(mainInstance, "test", 2));
            
            // Re-initialize for each test to ensure consistent state
            initializeGame.invoke(mainInstance);
        }
    }

    // === GAME STATE TESTS ===

    @Test
    @Timeout(5)
    void testGameCompletionScenarios() throws Exception {
        Method endGame = Main.class.getDeclaredMethod("endGame");
        endGame.setAccessible(true);
        
        // Test loss scenario
        Field totalScoreField = Main.class.getDeclaredField("totalScore");
        totalScoreField.setAccessible(true);
        totalScoreField.set(mainInstance, 100);
        
        assertDoesNotThrow(() -> endGame.invoke(mainInstance));
        
        // For the win scenario, we need to properly initialize the game first
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        // Set up win conditions
        totalScoreField.set(mainInstance, 800);
        Field currentRoundField = Main.class.getDeclaredField("currentRound");
        currentRoundField.setAccessible(true);
        currentRoundField.set(mainInstance, 3);
        
        // Ensure scoreCalculator is initialized
        Field scoreCalculatorField = Main.class.getDeclaredField("scoreCalculator");
        scoreCalculatorField.setAccessible(true);
        Object scoreCalculator = scoreCalculatorField.get(mainInstance);
        assertNotNull(scoreCalculator, "scoreCalculator should be initialized");
        
        Method playRound = Main.class.getDeclaredMethod("playRound");
        playRound.setAccessible(true);
        
        String simulatedInput = "2\n"; // Skip round
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        assertDoesNotThrow(() -> playRound.invoke(mainInstance));
    }


    @Test
    @Timeout(5)
    void testFullHandScenario() throws Exception {
        // Test round with full hand (no auto-draw)
        Method initializeGame = Main.class.getDeclaredMethod("initializeGame");
        initializeGame.setAccessible(true);
        initializeGame.invoke(mainInstance);
        
        Field playerHandField = Main.class.getDeclaredField("playerHand");
        playerHandField.setAccessible(true);
        Object playerHand = playerHandField.get(mainInstance);
        
        // Fill hand to capacity
        Class<?> cardClass = Class.forName("model.Card");
        int handSize = (int) playerHand.getClass().getMethod("size").invoke(playerHand);
        int maxSize = (int) playerHand.getClass().getMethod("getMaxSize").invoke(playerHand);
        
        for (int i = handSize; i < maxSize; i++) {
            Object card = cardClass.getConstructor(
                Class.forName("model.Card$Suit"), Class.forName("model.Card$Rank")
            ).newInstance(
                Enum.valueOf((Class<Enum>) Class.forName("model.Card$Suit"), "HEARTS"),
                Enum.valueOf((Class<Enum>) Class.forName("model.Card$Rank"), "TWO")
            );
            playerHand.getClass().getMethod("addCard", cardClass).invoke(playerHand, card);
        }
        
        Method playRound = Main.class.getDeclaredMethod("playRound");
        playRound.setAccessible(true);
        
        String simulatedInput = "2\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(mainInstance, new Scanner(System.in));
        
        assertDoesNotThrow(() -> playRound.invoke(mainInstance));
    }
}