package tests;

import ui.ScoreAnimation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class TestScoreAnimation {

    private ScoreAnimation scoreAnimation;
    private JPanel parentPanel;

    @BeforeEach
    void setUp() {
        parentPanel = new JPanel();
        parentPanel.setSize(200, 100);
        scoreAnimation = new ScoreAnimation(100);
        parentPanel.add(scoreAnimation);
        
        parentPanel.doLayout();
    }

    @Test
    void testConstructorInitialization() {
        assertEquals(100, getPrivateIntField("score"));
        assertEquals(1.0f, getPrivateFloatField("alpha"), 0.001f);
        assertEquals(0.5f, getPrivateFloatField("scale"), 0.001f);
        assertNotNull(getPrivateField("animationTimer"));
        
        assertEquals("+100 pts", scoreAnimation.getText());
        assertEquals(JLabel.CENTER, scoreAnimation.getHorizontalAlignment());
        assertFalse(scoreAnimation.isOpaque());
        
        Font expectedFont = new Font(Font.SANS_SERIF, Font.BOLD, 36);
        assertEquals(expectedFont, scoreAnimation.getFont());
        assertEquals(new Color(255, 220, 0), scoreAnimation.getForeground());
    }

    @Test
    void testAnimationTimerCreation() {
        Timer timer = (Timer) getPrivateField("animationTimer");
        assertNotNull(timer);
        assertTrue(timer.isRunning());
        assertEquals(16, timer.getDelay());
    }

    @Test
    void testAnimationScaleIncrease() {
        Timer timer = (Timer) getPrivateField("animationTimer");
        float initialScale = getPrivateFloatField("scale");
        
        safelyInvokeTimerAction(timer);
        
        float newScale = getPrivateFloatField("scale");
        assertTrue(newScale > initialScale);
        assertEquals(initialScale + 0.05f, newScale, 0.001f);
    }

    @Test
    void testScaleClampingAtMaximum() {
        setPrivateField("scale", 1.2f);
        Timer timer = (Timer) getPrivateField("animationTimer");
        
        safelyInvokeTimerAction(timer);
        
        assertEquals(1.2f, getPrivateFloatField("scale"), 0.001f);
    }

    @Test
    void testAlphaDecreaseAfterReachingMaxScale() {
        setPrivateField("scale", 1.2f);
        float initialAlpha = getPrivateFloatField("alpha");
        Timer timer = (Timer) getPrivateField("animationTimer");
        
        safelyInvokeTimerAction(timer);
        
        float newAlpha = getPrivateFloatField("alpha");
        assertTrue(newAlpha < initialAlpha);
        assertEquals(initialAlpha - 0.02f, newAlpha, 0.001f);
    }

    @Test
    void testRemoveFromParentWhenAnimationComplete() {
        setPrivateField("alpha", 0.0f);
        Timer timer = (Timer) getPrivateField("animationTimer");
        
        assertTrue(parentPanel.getComponents().length > 0);
        assertTrue(containsComponent(parentPanel, scoreAnimation));
        
        safelyInvokeTimerAction(timer);
        
        waitForSwing();
        
        assertFalse(containsComponent(parentPanel, scoreAnimation));
    }

    @Test
    void testAnimationProgressionWithoutParent() {
        ScoreAnimation standalone = new ScoreAnimation(50);
        
        Timer timer = (Timer) getPrivateField(standalone, "animationTimer");
        assertNotNull(timer);
        
        timer.stop();
        
        assertEquals(1.0f, getPrivateFloatField(standalone, "alpha"), 0.001f);
        assertEquals(0.5f, getPrivateFloatField(standalone, "scale"), 0.001f);
    }

    @Test
    void testPaintComponentMethods() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        invokePaintComponent(g2d);
        
        g2d.dispose();
        
        assertTrue(true);
    }

    @Test
    void testDifferentScoreValues() {
        JPanel testPanel = new JPanel();
        testPanel.setSize(200, 100);
        
        ScoreAnimation score50 = new ScoreAnimation(50);
        testPanel.add(score50);
        assertEquals("+50 pts", score50.getText());
        
        ScoreAnimation score1000 = new ScoreAnimation(1000);
        testPanel.add(score1000);
        assertEquals("+1000 pts", score1000.getText());
        
        ScoreAnimation score0 = new ScoreAnimation(0);
        testPanel.add(score0);
        assertEquals("+0 pts", score0.getText());
        
        stopAnimationTimer(score50);
        stopAnimationTimer(score1000);
        stopAnimationTimer(score0);
    }

    @Test
    void testAnimationWithZeroScore() {
        JPanel testPanel = new JPanel();
        testPanel.setSize(200, 100);
        
        ScoreAnimation zeroScore = new ScoreAnimation(0);
        testPanel.add(zeroScore);
        
        assertEquals("+0 pts", zeroScore.getText());
        
        Timer timer = (Timer) getPrivateField(zeroScore, "animationTimer");
        assertNotNull(timer);
        
        stopAnimationTimer(zeroScore);
    }

    @Test
    void testAnimationStateTransitions() {
        Timer timer = (Timer) getPrivateField("animationTimer");
        
        float initialScale = getPrivateFloatField("scale");
        float initialAlpha = getPrivateFloatField("alpha");
        
        safelyInvokeTimerAction(timer);
        
        float newScale = getPrivateFloatField("scale");
        float newAlpha = getPrivateFloatField("alpha");
        
        assertTrue(newScale > initialScale);
        assertEquals(initialAlpha, newAlpha, 0.001f); // alpha 在 scale < 1.2 时不应变化
    }

    private void safelyInvokeTimerAction(Timer timer) {
        try {
            for (ActionListener listener : timer.getActionListeners()) {
                listener.actionPerformed(null);
            }
        } catch (Exception e) {
            System.out.println("Expected exception during timer action: " + e.getMessage());
        }
    }

    private void invokePaintComponent(Graphics g) {
        try {
            Method method = ScoreAnimation.class.getDeclaredMethod("paintComponent", Graphics.class);
            method.setAccessible(true);
            method.invoke(scoreAnimation, g);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke paintComponent", e);
        }
    }

    private Object getPrivateField(String fieldName) {
        return getPrivateField(scoreAnimation, fieldName);
    }
    
    private Object getPrivateField(Object instance, String fieldName) {
        try {
            Field field = ScoreAnimation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field " + fieldName, e);
        }
    }

    private int getPrivateIntField(String fieldName) {
        try {
            Field field = ScoreAnimation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(scoreAnimation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access int field " + fieldName, e);
        }
    }


    private float getPrivateFloatField(String fieldName) {
        try {
            Field field = ScoreAnimation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getFloat(scoreAnimation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access float field " + fieldName, e);
        }
    }

    private float getPrivateFloatField(Object instance, String fieldName) {
        try {
            Field field = ScoreAnimation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getFloat(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access float field " + fieldName, e);
        }
    }

    private void setPrivateField(String fieldName, Object value) {
        try {
            Field field = ScoreAnimation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(scoreAnimation, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private boolean containsComponent(Container container, Component component) {
        for (Component comp : container.getComponents()) {
            if (comp == component) {
                return true;
            }
        }
        return false;
    }

    private void waitForSwing() {
        try {
            SwingUtilities.invokeAndWait(() -> {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to wait for Swing events", e);
        }
    }
    
    private void stopAnimationTimer(ScoreAnimation animation) {
        try {
            Timer timer = (Timer) getPrivateField(animation, "animationTimer");
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        } catch (Exception e) {
        }
    }
}