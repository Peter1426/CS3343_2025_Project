package model;

/**
 * 
 */
public class Card {
    public enum Suit {
        HEARTS("♥", "Red"),
        DIAMONDS("♦", "Red"),
        CLUBS("♣", "Black"),
        SPADES("♠", "Black");
        
        private final String symbol;
        private final String color;
        
        Suit(String symbol, String color) {
            this.symbol = symbol;
            this.color = color;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public String getColor() {
            return color;
        }
        
        public boolean isRed() {
            return color.equalsIgnoreCase("Red");
        }
        
        public boolean isBlack() {
            return color.equalsIgnoreCase("Black");
        }
    }
    
    public enum Rank {
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        JACK(11, "J"),
        QUEEN(12, "Q"),
        KING(13, "K"),
        ACE(14, "A");
        
        private final int value;
        private final String display;
        
        Rank(int value, String display) {
            this.value = value;
            this.display = display;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getDisplay() {
            return display;
        }
    }
    
    private final Suit suit;
    private final Rank rank;
    
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public int getValue() {
        return rank.getValue();
    }
    
    public boolean isRed() {
        return suit.isRed();
    }
    
    public boolean isBlack() {
        return suit.isBlack();
    }
    
    public boolean isClubs() {
        return suit == Suit.CLUBS;
    }
    
    @Override
    public String toString() {
        return rank.getDisplay() + suit.getSymbol();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit == card.suit && rank == card.rank;
    }
}











