package tests;

import model.Card;
import model.Hand;
import service.GameLogicManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestGameLogicManager {

    private List<Card> deck;
    private Hand playerHand;

    @BeforeEach
    void setUp() {
        deck = GameLogicManager.createDeck();
        playerHand = new Hand(7);
    }

    @Test
    void testCreateStandard52CardDeck() {
        List<Card> newDeck = GameLogicManager.createDeck();

        assertEquals(52, newDeck.size());

        Set<String> uniqueCards = new HashSet<>();
        for (Card card : newDeck) {
            String cardKey = card.getSuit() + "-" + card.getRank();
            uniqueCards.add(cardKey);
        }

        assertEquals(52, uniqueCards.size());
    }

    @Test
    void testDeckContainsAllSuits() {
        List<Card> newDeck = GameLogicManager.createDeck();

        long heartsCount = newDeck.stream().filter(c -> c.getSuit() == Card.Suit.HEARTS).count();
        long diamondsCount = newDeck.stream().filter(c -> c.getSuit() == Card.Suit.DIAMONDS).count();
        long clubsCount = newDeck.stream().filter(c -> c.getSuit() == Card.Suit.CLUBS).count();
        long spadesCount = newDeck.stream().filter(c -> c.getSuit() == Card.Suit.SPADES).count();

        assertEquals(13, heartsCount);
        assertEquals(13, diamondsCount);
        assertEquals(13, clubsCount);
        assertEquals(13, spadesCount);
    }

    @Test
    void testShufflePreservesDeckSize() {
        int originalSize = deck.size();

        GameLogicManager.shuffleDeck(deck);

        assertEquals(originalSize, deck.size());
    }

    @Test
    void testMultipleShufflesProduceDifferentOrders() {
        List<Card> originalOrder = new ArrayList<>(deck);
        List<Card> firstShuffle = new ArrayList<>(deck);
        List<Card> secondShuffle = new ArrayList<>(deck);

        GameLogicManager.shuffleDeck(firstShuffle);
        GameLogicManager.shuffleDeck(secondShuffle);

        boolean allSame = originalOrder.equals(firstShuffle) && 
                         firstShuffle.equals(secondShuffle) && 
                         originalOrder.equals(secondShuffle);
        
        assertFalse(allSame);
    }

    @Test
    void testDrawSpecifiedNumberOfCards() {
        int initialDeckSize = deck.size();
        int initialHandSize = playerHand.size();
        int cardsToDraw = 5;

        GameLogicManager.drawCards(deck, playerHand, cardsToDraw);

        assertEquals(initialHandSize + cardsToDraw, playerHand.size());
        assertEquals(initialDeckSize - cardsToDraw, deck.size());
    }

    @Test
    void testStopDrawingWhenHandFull() {
        Hand smallHand = new Hand(2);
        smallHand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        int initialDeckSize = deck.size();

        GameLogicManager.drawCards(deck, smallHand, 5);

        assertEquals(2, smallHand.size());
        assertEquals(initialDeckSize - 1, deck.size());
    }

    @Test
    void testNotDrawWhenCountZero() {
        int initialDeckSize = deck.size();
        int initialHandSize = playerHand.size();

        GameLogicManager.drawCards(deck, playerHand, 0);

        assertEquals(initialHandSize, playerHand.size());
        assertEquals(initialDeckSize, deck.size());
    }

    @Test
    void testNotDrawWhenCountNegative() {
        int initialDeckSize = deck.size();
        int initialHandSize = playerHand.size();

        GameLogicManager.drawCards(deck, playerHand, -1);

        assertEquals(initialHandSize, playerHand.size());
        assertEquals(initialDeckSize, deck.size());
    }

    @Test
    void testReshuffleWhenDeckEmpty() {
        List<Card> emptyDeck = new ArrayList<>();
        int cardsToDraw = 5;

        GameLogicManager.drawCards(emptyDeck, playerHand, cardsToDraw);

        assertEquals(cardsToDraw, playerHand.size());
        assertTrue(emptyDeck.size() < 52);
    }

    @Test
    void testAnalyzeEmptyHand() {
        List<Card> emptyHand = new ArrayList<>();

        String analysis = GameLogicManager.analyzeHand(emptyHand);

        assertEquals("No cards selected.", analysis);
    }

    @Test
    void testDetectPairs() {
        List<Card> handWithPair = new ArrayList<>();
        handWithPair.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        handWithPair.add(new Card(Card.Suit.DIAMONDS, Card.Rank.ACE));
        handWithPair.add(new Card(Card.Suit.CLUBS, Card.Rank.KING));

        String analysis = GameLogicManager.analyzeHand(handWithPair);

        assertTrue(analysis.contains("Pair(s) available"));
        assertTrue(analysis.contains("A×2"));
    }

    @Test
    void testDetectFlushDraw() {
        List<Card> flushDraw = new ArrayList<>();
        Card.Suit mainSuit = Card.Suit.HEARTS;
        Card.Suit offSuit = Card.Suit.SPADES;
        
        flushDraw.add(new Card(mainSuit, Card.Rank.ACE));
        flushDraw.add(new Card(mainSuit, Card.Rank.KING));
        flushDraw.add(new Card(mainSuit, Card.Rank.QUEEN));
        flushDraw.add(new Card(mainSuit, Card.Rank.JACK));
        flushDraw.add(new Card(offSuit, Card.Rank.TEN));

        String analysis = GameLogicManager.analyzeHand(flushDraw);

        assertTrue(analysis.contains("Almost a flush"));
    }

    @Test
    void testProvideAdviceForNoPairHand() {
        List<Card> noPairHand = new ArrayList<>();
        noPairHand.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        noPairHand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.KING));

        String analysis = GameLogicManager.analyzeHand(noPairHand);

        assertTrue(analysis.contains("No pairs"));
        assertTrue(analysis.contains("Tip:"));
    }

    @Test
    void testCalculateCurrentLevelTarget() {
        int[] levelTargets = {100, 200, 300, 400, 500};
        int currentLevel = 3;

        int target = GameLogicManager.getCurrentLevelTarget(currentLevel, levelTargets);

        assertEquals(300, target);
    }

    @Test
    void testReturnLastLevelTargetWhenLevelOutOfRange() {
        int[] levelTargets = {100, 200, 300};
        int invalidLevel = 5;

        int target = GameLogicManager.getCurrentLevelTarget(invalidLevel, levelTargets);

        assertEquals(300, target);
    }

    @Test
    void testAllowSkipLevelWhenTargetAchieved() {
        int[] levelTargets = {100, 200, 300};
        int currentLevel = 1;
        int levelScore = 150;
        int totalLevels = 3;

        boolean canSkip = GameLogicManager.canSkipLevel(levelScore, currentLevel, levelTargets, totalLevels);

        assertTrue(canSkip);
    }

    @Test
    void testNotAllowSkipLevelWhenTargetNotAchieved() {
        int[] levelTargets = {100, 200, 300};
        int currentLevel = 1;
        int levelScore = 50;
        int totalLevels = 3;

        boolean canSkip = GameLogicManager.canSkipLevel(levelScore, currentLevel, levelTargets, totalLevels);

        assertFalse(canSkip);
    }

    @Test
    void testDetectAllLevelsCompleted() {
        int[] levelTargets = {100, 200, 300};
        int currentLevel = 3;
        int levelScore = 350;
        int totalLevels = 3;

        boolean allCompleted = GameLogicManager.areAllLevelsCompleted(currentLevel, levelScore, levelTargets, totalLevels);

        assertTrue(allCompleted);
    }

    @Test
    void testReturnFalseWhenNotAllLevelsCompleted() {
        int[] levelTargets = {100, 200, 300};
        int currentLevel = 2;
        int levelScore = 150;
        int totalLevels = 3;

        boolean allCompleted = GameLogicManager.areAllLevelsCompleted(currentLevel, levelScore, levelTargets, totalLevels);

        assertFalse(allCompleted);
    }

    @Test
    void testFullGameFlowIntegration() {
        List<Card> gameDeck = GameLogicManager.createDeck();
        assertEquals(52, gameDeck.size());

        GameLogicManager.shuffleDeck(gameDeck);
        assertEquals(52, gameDeck.size());

        Hand gameHand = new Hand(5);
        GameLogicManager.drawCards(gameDeck, gameHand, 5);
        assertEquals(5, gameHand.size());
        assertEquals(47, gameDeck.size());

        String analysis = GameLogicManager.analyzeHand(gameHand.getCards());
        assertNotNull(analysis);
        assertFalse(analysis.isEmpty());

        int[] levelTargets = {50, 100, 150};
        int currentLevel = 1;
        int playerScore = 75;

        boolean canSkip = GameLogicManager.canSkipLevel(playerScore, currentLevel, levelTargets, 3);
        assertTrue(canSkip);

        boolean allCompleted = GameLogicManager.areAllLevelsCompleted(currentLevel, playerScore, levelTargets, 3);
        assertFalse(allCompleted);
    }

    @Test
    void testDeckExhaustionAndAutoRefill() {
        List<Card> smallDeck = new ArrayList<>();
        smallDeck.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        smallDeck.add(new Card(Card.Suit.DIAMONDS, Card.Rank.KING));
        
        Hand testHand = new Hand(10);

        GameLogicManager.drawCards(smallDeck, testHand, 5);

        assertEquals(5, testHand.size());
        assertTrue(smallDeck.size() < 52);
    }
    
    @Test
    void testConstructor() {
    	new GameLogicManager();
    }
}
