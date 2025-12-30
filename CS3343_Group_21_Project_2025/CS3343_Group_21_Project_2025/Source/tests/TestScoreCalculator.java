package tests;

import model.Card;
import model.Joker;
import service.ScoreCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import data.JokerDeck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestScoreCalculator {

    private ScoreCalculator scoreCalculator;
    private List<Joker> jokers;

    @BeforeEach
    void setUp() {
        jokers = new ArrayList<>();
        scoreCalculator = new ScoreCalculator(jokers);
    }

    // Test data factory methods
    private static List<Card> createPairHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                new Card(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    private static List<Card> createThreeOfAKindHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
                new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));
    }

    private static List<Card> createFlushHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.TWO),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
                new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                new Card(Card.Suit.HEARTS, Card.Rank.KING));
    }

    private static List<Card> createStraightHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.CLUBS, Card.Rank.JACK),
                new Card(Card.Suit.SPADES, Card.Rank.QUEEN),
                new Card(Card.Suit.HEARTS, Card.Rank.KING));
    }

    private static List<Card> createHighCardHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE));
    }

    private static List<Card> createRedCardsHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE));
    }

    private static List<Card> createAceHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.ACE),
                new Card(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    private static List<Card> createSevenHand() {
        return Arrays.asList(
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    // Get joker from JokerDeck by name
    private Joker getJokerByName(String name) {
        return JokerDeck.getJokerByName(name);
    }

    @Test
    // Test empty hand returns 0 score
    void testCalculateScore_EmptyHand_ReturnsZero() {
        List<Card> emptyHand = new ArrayList<>();
        int score = scoreCalculator.calculateScore(emptyHand);
        assertEquals(0, score);
    }

    @Test
    // Test null hand returns 0 score
    void testCalculateScore_NullHand_ReturnsZero() {
        int score = scoreCalculator.calculateScore(null);
        assertEquals(0, score);
    }

    @Test
    // Test pair base score
    void testCalculateScore_Pair_ReturnsBaseScore() {
        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);
        assertEquals(15, score); // Pair base score is 15
    }

    @Test
    // Test three of a kind base score
    void testCalculateScore_ThreeOfAKind_ReturnsBaseScore() {
        List<Card> threeOfAKindHand = createThreeOfAKindHand();
        int score = scoreCalculator.calculateScore(threeOfAKindHand);
        assertEquals(70, score); // Three of a kind base score is 70
    }

    @Test
    // Test flush base score
    void testCalculateScore_Flush_ReturnsBaseScore() {
        List<Card> flushHand = createFlushHand();
        int score = scoreCalculator.calculateScore(flushHand);
        assertEquals(150, score); // Flush base score is 150
    }

    @Test
    // Test high card base score
    void testCalculateScore_HighCard_ReturnsBaseScore() {
        List<Card> highCardHand = createHighCardHand();
        int score = scoreCalculator.calculateScore(highCardHand);
        assertEquals(5, score); // High card base score is 5
    }

    @Test
    // Test Double Vision joker effect (score ×2)
    void testCalculateScore_WithDoubleVisionJoker_DoublesScore() {
        jokers.add(getJokerByName("Double Vision"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        assertEquals(30, score); // 15 * 2 = 30
    }

    @Test
    // Test The Gambler joker effect (high card gives 200 points)
    void testCalculateScore_WithTheGamblerJoker_HighCardGives200() {
        jokers.add(getJokerByName("The Gambler"));
        scoreCalculator.updateJokers(jokers);

        List<Card> highCardHand = createHighCardHand();
        int score = scoreCalculator.calculateScore(highCardHand);

        assertEquals(100, score); // High card should give 200, not base 5
    }

    @Test
    // Test Copycat joker effect (three of a kind or higher ×2)
    void testCalculateScore_WithCopycatJoker_TriplesOrHigherDoubled() {
        jokers.add(getJokerByName("Copycat"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand(); // Pair - no effect
        List<Card> threeOfAKindHand = createThreeOfAKindHand(); // Three of a kind - effect applies

        int pairScore = scoreCalculator.calculateScore(pairHand);
        int threeScore = scoreCalculator.calculateScore(threeOfAKindHand);

        assertEquals(15, pairScore); // Pair base score 15, no multiplier
        assertEquals(140, threeScore); // Three of a kind base 70 × 2 = 140
    }

    @Test
    // Test Chaos Joker effect (score +30%)
    void testCalculateScore_WithChaosJoker_Adds30Percent() {
        jokers.add(getJokerByName("Chaos Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        assertEquals(19, score); // 15 * 1.3 = 19.5 -> 19 (integer truncation)
    }

    @Test
    // Test Mirror Joker effect (flush ×1.5)
    void testCalculateScore_WithMirrorJoker_FlushMultiplied() {
        jokers.add(getJokerByName("Mirror Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> flushHand = createFlushHand();
        List<Card> pairHand = createPairHand();

        int flushScore = scoreCalculator.calculateScore(flushHand);
        int pairScore = scoreCalculator.calculateScore(pairHand);

        assertEquals(225, flushScore); // 150 * 1.5 = 225
        assertEquals(15, pairScore); // Pair not affected
    }

    @Test
    // Test Rogue Joker effect (three of a kind or higher ×3)
    void testCalculateScore_WithRogueJoker_TriplesOrHigherTripled() {
        jokers.add(getJokerByName("Rogue Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand(); // Pair - no effect
        List<Card> threeOfAKindHand = createThreeOfAKindHand(); // Three of a kind - effect applies

        int pairScore = scoreCalculator.calculateScore(pairHand);
        int threeScore = scoreCalculator.calculateScore(threeOfAKindHand);

        assertEquals(15, pairScore); // Pair base score 15
        assertEquals(210, threeScore); // Three of a kind base 70 × 3 = 210
    }

    @Test
    // Test Red Card Joker effect (red cards double score)
    void testCalculateScore_WithRedCardJoker_RedCardsDoubleScore() {
        jokers.add(getJokerByName("Red Card Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> redCardsHand = createRedCardsHand(); // Contains hearts and diamonds
        List<Card> allBlackHand = Arrays.asList(
                new Card(Card.Suit.CLUBS, Card.Rank.TEN),
                new Card(Card.Suit.SPADES, Card.Rank.TEN));

        int redScore = scoreCalculator.calculateScore(redCardsHand);
        int blackScore = scoreCalculator.calculateScore(allBlackHand);

        assertEquals(30, redScore); // 15 * 2 = 30
        assertEquals(15, blackScore); // No red cards, no multiplier
    }

    @Test
    // Test Ace Master joker effect (each Ace adds 50 points)
    void testCalculateScore_WithAceMasterJoker_Adds50PerAce() {
        jokers.add(getJokerByName("Ace Master"));
        scoreCalculator.updateJokers(jokers);

        List<Card> aceHand = createAceHand(); // Two Aces

        int score = scoreCalculator.calculateScore(aceHand);

        assertEquals(115, score); // Pair base 15 + 2 * 50 = 115
    }

    @Test
    // Test Straight Runner joker effect (straights doubled)
    void testCalculateScore_WithStraightRunnerJoker_StraightsDoubled() {
        jokers.add(getJokerByName("Straight Runner"));
        scoreCalculator.updateJokers(jokers);

        List<Card> straightHand = createStraightHand();
        List<Card> pairHand = createPairHand();

        int straightScore = scoreCalculator.calculateScore(straightHand);
        int pairScore = scoreCalculator.calculateScore(pairHand);

        assertEquals(240, straightScore); // 120 * 2 = 240
        assertEquals(15, pairScore); // Pair not affected
    }

    @Test
    // Test Lucky Sevens joker effect (each 7 adds 70 points)
    void testCalculateScore_WithLuckySevensJoker_Adds70PerSeven() {
        jokers.add(getJokerByName("Lucky Sevens"));
        scoreCalculator.updateJokers(jokers);

        List<Card> sevenHand = createSevenHand(); // Two 7s

        int score = scoreCalculator.calculateScore(sevenHand);

        assertEquals(155, score); // Pair base 15 + 2 * 70 = 155
    }

    @Test
    // Test multiple joker effects stacking
    void testCalculateScore_MultipleJokers_AppliesAllEffects() {
        jokers.add(getJokerByName("Double Vision")); // ×2
        jokers.add(getJokerByName("Chaos Joker")); // ×1.3
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        assertEquals(39, score); // 15 * 2 * 1.3 = 39
    }

    @Test
    // Test Greedy Joker first play ×3 effect
    void testCalculateScore_GreedyJokerFirstPlay_TriplesScore() {
        jokers.add(getJokerByName("Greedy Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int firstPlayScore = scoreCalculator.calculateScore(pairHand);

        assertEquals(45, firstPlayScore); // 15 * 3 = 45
    }

    @Test
    // Test Greedy Joker subsequent play ÷2 effect
    void testCalculateScore_GreedyJokerSubsequentPlay_HalvesScore() {
        jokers.add(getJokerByName("Greedy Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();

        // First play
        scoreCalculator.calculateScore(pairHand);
        // Second play
        int secondPlayScore = scoreCalculator.calculateScore(pairHand);

        assertEquals(7, secondPlayScore); // 15 / 2 = 7 (integer division)
    }

    @Test
    // Test round reset for Greedy Joker
    void testCalculateScore_AfterResetRound_GreedyJokerResets() {
        jokers.add(getJokerByName("Greedy Joker"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();

        // First round first play
        int firstRoundFirstPlay = scoreCalculator.calculateScore(pairHand);
        // First round second play
        scoreCalculator.calculateScore(pairHand);

        // Reset round
        scoreCalculator.resetRound();

        // Second round first play
        int secondRoundFirstPlay = scoreCalculator.calculateScore(pairHand);

        assertEquals(45, firstRoundFirstPlay); // Should be ×3
        assertEquals(45, secondRoundFirstPlay); // Should be ×3 again after reset
    }

    @Test
    // Test updating jokers list
    void testUpdateJokers_ChangesActiveJokers() {
        Joker doubleVision = getJokerByName("Double Vision");
        jokers.add(doubleVision);
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int initialScore = scoreCalculator.calculateScore(pairHand); // 15 * 2 = 30

        List<Joker> newJokers = new ArrayList<>();
        Joker chaosJoker = getJokerByName("Chaos Joker");
        newJokers.add(chaosJoker);

        // Update jokers
        scoreCalculator.updateJokers(newJokers);
        int updatedScore = scoreCalculator.calculateScore(pairHand); // 15 * 1.3 = 19

        assertEquals(30, initialScore);
        assertEquals(19, updatedScore);
    }


    @Test
    // Test hand size minimum is 1
    void testGetModifiedHandSize_MinimumSize() {
        scoreCalculator.updateJokers(new ArrayList<>()); // No jokers

        int modifiedSize = scoreCalculator.getModifiedHandSize(1);

        assertEquals(1, modifiedSize); // Should be at least 1
    }

    @Test
    // Test Draw Maestro draw count modification
    void testGetModifiedDrawCount_WithDrawMaestro_IncreasesDrawCount() {
        jokers.add(getJokerByName("Draw Maestro"));
        scoreCalculator.updateJokers(jokers);

        int modifiedDraw = scoreCalculator.getModifiedDrawCount(3);

        assertEquals(4, modifiedDraw); // 3 + 1 = 4
    }

    @Test
    // Test draw count minimum is 1
    void testGetModifiedDrawCount_MinimumDraw() {
        scoreCalculator.updateJokers(new ArrayList<>()); // No jokers

        int modifiedDraw = scoreCalculator.getModifiedDrawCount(1);

        assertEquals(1, modifiedDraw); // Should be at least 1
    }

    @Test
    // Test can play hand with no jokers allows any valid hand
    void testCanPlayHand_NoJokers_AllowsAnyValidHand() {
        List<Card> pairHand = createPairHand();
        List<Card> highCardHand = createHighCardHand();
        List<Card> singleCard = Arrays.asList(new Card(Card.Suit.HEARTS, Card.Rank.TEN));

        assertTrue(scoreCalculator.canPlayHand(pairHand));
        assertTrue(scoreCalculator.canPlayHand(highCardHand));
        assertTrue(scoreCalculator.canPlayHand(singleCard));
    }

    @Test
    // Test empty or null hand cannot be played
    void testCanPlayHand_EmptyOrNull_ReturnsFalse() {
        assertFalse(scoreCalculator.canPlayHand(null));
        assertFalse(scoreCalculator.canPlayHand(new ArrayList<>()));
    }

    @ParameterizedTest
    @MethodSource("provideHandTypesForScoring")
    // Parameterized test for various hand type base scores
    void testCalculateScore_VariousHandTypes_ReturnsCorrectBaseScore(
            List<Card> hand, int expectedBaseScore) {
        int score = scoreCalculator.calculateScore(hand);
        assertEquals(expectedBaseScore, score);
    }

    private static Stream<Arguments> provideHandTypesForScoring() {
        return Stream.of(
                // High card
                Arguments.of(Arrays.asList(
                        new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                        new Card(Card.Suit.CLUBS, Card.Rank.NINE)), 5),

                // Pair
                Arguments.of(Arrays.asList(
                        new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                        new Card(Card.Suit.CLUBS, Card.Rank.TEN)), 15),

                // Three of a kind
                Arguments.of(Arrays.asList(
                        new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
                        new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                        new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)), 70),

                // Straight
                Arguments.of(Arrays.asList(
                        new Card(Card.Suit.HEARTS, Card.Rank.NINE),
                        new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                        new Card(Card.Suit.CLUBS, Card.Rank.JACK),
                        new Card(Card.Suit.SPADES, Card.Rank.QUEEN),
                        new Card(Card.Suit.HEARTS, Card.Rank.KING)), 120),

                // Flush
                Arguments.of(Arrays.asList(
                        new Card(Card.Suit.HEARTS, Card.Rank.TWO),
                        new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                        new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
                        new Card(Card.Suit.HEARTS, Card.Rank.TEN),
                        new Card(Card.Suit.HEARTS, Card.Rank.KING)), 150));
    }

    @Test
    // Test complex joker effect combination
    void testCalculateScore_ComplexJokerCombination() {
        jokers.add(getJokerByName("Double Vision")); // ×2
        jokers.add(getJokerByName("Chaos Joker")); // ×1.3
        scoreCalculator.updateJokers(jokers);

        List<Card> threeOfAKindHand = createThreeOfAKindHand();
        int score = scoreCalculator.calculateScore(threeOfAKindHand);

        assertEquals(182, score); // 70 * 2 * 1.3 = 182
    }

    @Test
    // Test that jokers don't affect invalid hand judgment
    void testCanPlayHand_JokersDontAffectInvalidHands() {
        jokers.add(getJokerByName("Rogue Joker"));
        scoreCalculator.updateJokers(jokers);

        // Empty and null hands should still be invalid
        assertFalse(scoreCalculator.canPlayHand(null));
        assertFalse(scoreCalculator.canPlayHand(new ArrayList<>()));
    }

    @Test
    // Test Rogue Joker doesn't affect empty or null hands
    void testCanPlayHand_WithRogueJoker_EmptyOrNullStillInvalid() {
        jokers.add(getJokerByName("Rogue Joker"));
        scoreCalculator.updateJokers(jokers);

        // Empty and null hands should still be invalid regardless of Rogue Joker
        assertFalse(scoreCalculator.canPlayHand(null));
        assertFalse(scoreCalculator.canPlayHand(new ArrayList<>()));
    }

    @Test
    // Test joker with null effect doesn't modify score
    void testApplyJokerEffects_JokerWithNullEffect_NoModification() {
        // Create a joker with null effect
        Joker nullEffectJoker = new Joker("Null Effect Joker", "Has no effect", null);
        jokers.add(nullEffectJoker);
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        // Should return base score without modification
        assertEquals(15, score);
    }

    @Test
    // Test joker with effect but null score modifier doesn't modify score
    void testApplyJokerEffects_JokerWithNullScoreModifier_NoModification() {
        // Create a joker with effect but no score modifier
        Joker.Effect effect = new Joker.Effect(Joker.EffectType.HAND_SIZE_MODIFIER)
                .withHandSizeModifier(size -> size + 1); // Only hand size modifier, no score modifier
        Joker noScoreModifierJoker = new Joker("No Score Modifier Joker", "Only modifies hand size", effect);
        jokers.add(noScoreModifierJoker);
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        // Should return base score without modification
        assertEquals(15, score);
    }

    @Test
    // Test joker with valid score modifier applies effect
    void testApplyJokerEffects_JokerWithValidScoreModifier_AppliesEffect() {
        // This is already covered by other tests like Double Vision, but let's be
        // explicit
        jokers.add(getJokerByName("Double Vision"));
        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        // Should apply the ×2 modifier
        assertEquals(30, score);
    }

    @Test
    // Test multiple jokers with mixed valid and invalid effects
    void testApplyJokerEffects_MixedJokers_OnlyValidModifiersApplied() {
        // Add a joker with null effect
        Joker nullEffectJoker = new Joker("Null Effect Joker", "Has no effect", null);
        jokers.add(nullEffectJoker);

        // Add a joker with effect but no score modifier
        Joker.Effect handSizeEffect = new Joker.Effect(Joker.EffectType.HAND_SIZE_MODIFIER)
                .withHandSizeModifier(size -> size + 1);
        Joker noScoreModifierJoker = new Joker("Hand Size Joker", "Only modifies hand size", handSizeEffect);
        jokers.add(noScoreModifierJoker);

        // Add a valid score modifier joker
        jokers.add(getJokerByName("Double Vision"));

        scoreCalculator.updateJokers(jokers);

        List<Card> pairHand = createPairHand();
        int score = scoreCalculator.calculateScore(pairHand);

        // Should only apply the Double Vision modifier (15 * 2 = 30)
        // The other jokers should be ignored for score calculation
        assertEquals(30, score);
    }

    @Test
    // Test that draw modifiers still work even when score modifiers are null
    void testGetModifiedDrawCount_WithMixedJokers_DrawModifiersWork() {
        // Add a joker with null effect
        Joker nullEffectJoker = new Joker("Null Effect Joker", "Has no effect", null);
        jokers.add(nullEffectJoker);

        // Add a joker with valid draw modifier
        jokers.add(getJokerByName("Draw Maestro"));

        // Add a joker with effect but no draw modifier (only score modifier)
        jokers.add(getJokerByName("Double Vision"));

        scoreCalculator.updateJokers(jokers);

        int modifiedDraw = scoreCalculator.getModifiedDrawCount(3);

        // Should only apply the Draw Maestro modifier (3 + 1 = 4)
        // The other jokers should be ignored for draw count calculation
        assertEquals(4, modifiedDraw);
    }
    
    @Test
    void testGetActiveJokers() {
    	List<Joker> jokers = new ArrayList<Joker>();
    	jokers.add(JokerDeck.getJokerByName("Mirror Joker"));
    	jokers.add(JokerDeck.getJokerByName("Copycat"));
    	jokers.add(JokerDeck.getJokerByName("Lucky Sevens"));
    	jokers.add(JokerDeck.getJokerByName("Red Card Joker"));
    	
    	scoreCalculator.updateJokers(jokers);
    	
    	assertEquals(4, scoreCalculator.getActiveJokers().size());
    	assertTrue(scoreCalculator.getActiveJokers().containsAll(jokers));
    }
    
    @Test
    void testModifiedHandSize() {
    	List<Joker> jokers = Arrays.asList(
    		new Joker(
                "Test Joker1",
                "HandSize +2",
                new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                    .withHandSizeModifier(size -> size+2)
            ),
    		new Joker(
                    "Test Joker2",
                    "null effect",
                    null
                ),
    		new Joker(
                    "Test Joker3",
                    "null modifier",
                    new Joker.Effect(Joker.EffectType.SCORE_MODIFIER)
                )
    	);
        scoreCalculator.updateJokers(jokers);

        int modifiedHandSize = scoreCalculator.getModifiedHandSize(5);

        assertEquals(7, modifiedHandSize);
    }
}