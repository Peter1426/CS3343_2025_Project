package model;

import java.util.ArrayList;
import java.util.List;

/**
 * hand
 */
public class Hand {
    private List<Card> cards;
    private int maxSize;
    
    public Hand(int maxSize) {
        this.cards = new ArrayList<>();
        this.maxSize = maxSize;
    }
    
    public void addCard(Card card) {
        if (cards.size() < maxSize) {
            cards.add(card);
        }
    }
    
    public void forceAddCard(Card card) {
        cards.add(card);
    }
    
    public void addCards(List<Card> newCards) {
        for (Card card : newCards) {
            if (cards.size() < maxSize) {
                cards.add(card);
            }
        }
    }
    
    public Card removeCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.remove(index);
        }
        return null;
    }
    
    public void removeCards(List<Card> toRemove) {
        if (toRemove == null || toRemove.isEmpty()) return;
        // Remove one occurrence per requested card. Using remove(Object) removes the first
        // matching element (by equals), avoiding removing all duplicates at once.
        for (Card c : toRemove) {
            for (int i = 0; i < cards.size(); i++) {
                Card existing = cards.get(i);
                if (existing.equals(c)) {
                    cards.remove(i);
                    break; // remove only one occurrence for this requested card
                }
            }
        }
    }
    
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    public int size() {
        return cards.size();
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public boolean isFull() {
        return cards.size() >= maxSize;
    }
    
    public void clear() {
        cards.clear();
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}