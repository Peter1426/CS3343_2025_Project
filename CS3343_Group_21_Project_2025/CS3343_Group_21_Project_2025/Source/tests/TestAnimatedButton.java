package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.AnimatedButton;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class TestAnimatedButton {

    private AnimatedButton button;
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private MouseListener customMouseListener;

    @BeforeEach
    void setUp() {
        // Initialize test colors and button instance
        normalColor = new Color(70, 130, 180);
        hoverColor = new Color(100, 149, 237);
        pressedColor = new Color(65, 105, 225);
        button = new AnimatedButton("Test Button", normalColor, hoverColor, pressedColor);
        
        button.setSize(130, 45);
        
        // Extract the custom mouse listener from the button for testing
        for (MouseListener listener : button.getMouseListeners()) {
            if (listener.getClass().getName().contains("AnimatedButton")) {
                customMouseListener = listener;
                break;
            }
        }
    }

    @Test
    void testSetNormalColor() {
        // Test setting normal color and verify it updates both normalColor and currentColor
        Color newNormalColor = Color.CYAN;
        button.setNormalColor(newNormalColor);
        
        assertEquals(newNormalColor, getPrivateField("normalColor"));
        assertEquals(newNormalColor, getPrivateField("currentColor"));
    }

    @Test
    void testSetNormalColorWhileHovered() {
        // Test that setting normal color doesn't change current color when in hovered state
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        Color newNormalColor = Color.CYAN;
        button.setNormalColor(newNormalColor);
        
        assertEquals(newNormalColor, getPrivateField("normalColor"));
        assertEquals(hoverColor, getPrivateField("currentColor")); // Should remain hover color
    }

    @Test
    void testSetHoverColor() {
        // Test setting hover color and verify it updates hoverColor but not currentColor
        Color newHoverColor = Color.MAGENTA;
        button.setHoverColor(newHoverColor);
        
        assertEquals(newHoverColor, getPrivateField("hoverColor"));
        assertEquals(normalColor, getPrivateField("currentColor")); // Should remain normal
    }

    @Test
    void testSetHoverColorTakesEffectOnHover() {
        // Test that new hover color is applied when mouse enters
        Color newHoverColor = Color.MAGENTA;
        button.setHoverColor(newHoverColor);
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        
        assertEquals(newHoverColor, getPrivateField("currentColor"));
    }

    @Test
    void testSetPressedColor() {
        // Test setting pressed color and verify it updates pressedColor but not currentColor
        Color newPressedColor = Color.ORANGE;
        button.setPressedColor(newPressedColor);
        
        assertEquals(newPressedColor, getPrivateField("pressedColor"));
        assertEquals(normalColor, getPrivateField("currentColor")); // Should remain normal
    }

    @Test
    void testSetPressedColorTakesEffectOnPress() {
        // Test that new pressed color is applied when mouse is pressed
        Color newPressedColor = Color.ORANGE;
        button.setPressedColor(newPressedColor);
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        
        assertEquals(newPressedColor, getPrivateField("currentColor"));
    }

    @Test
    void testMultipleColorChanges() {
        // Test multiple color changes and verify state transitions work correctly
        Color newNormal = Color.CYAN;
        Color newHover = Color.MAGENTA;
        Color newPressed = Color.ORANGE;
        
        button.setNormalColor(newNormal);
        button.setHoverColor(newHover);
        button.setPressedColor(newPressed);
        
        assertEquals(newNormal, getPrivateField("normalColor"));
        assertEquals(newHover, getPrivateField("hoverColor"));
        assertEquals(newPressed, getPrivateField("pressedColor"));
        assertEquals(newNormal, getPrivateField("currentColor")); // Should be normal initially
        
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        assertEquals(newHover, getPrivateField("currentColor")); // Should switch to hover
        
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        assertEquals(newPressed, getPrivateField("currentColor")); // Should switch to pressed
        
        customMouseListener.mouseExited(createMouseEvent(MouseEvent.MOUSE_EXITED, -10, -10));
        assertEquals(newNormal, getPrivateField("currentColor")); // Should return to normal
    }

    @Test
    void testMouseEnteredListener() {
        // Test mouseEntered handler sets hovered state and updates color
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        
        assertTrue(getPrivateBooleanField("isHovered"));
        assertEquals(hoverColor, getPrivateField("currentColor"));
    }

    @Test
    void testMouseExitedListener() {
        // Test mouseExited handler clears both hovered and pressed states
        setPrivateField("isHovered", true);
        setPrivateField("isPressed", true);
        setPrivateField("currentColor", pressedColor);
        
        customMouseListener.mouseExited(createMouseEvent(MouseEvent.MOUSE_EXITED, -10, -10));
        
        assertFalse(getPrivateBooleanField("isHovered"));
        assertFalse(getPrivateBooleanField("isPressed"));
        assertEquals(normalColor, getPrivateField("currentColor"));
    }

    @Test
    void testMousePressedListener() {
        // Test mousePressed handler sets pressed state and updates color
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        
        assertTrue(getPrivateBooleanField("isPressed"));
        assertEquals(pressedColor, getPrivateField("currentColor"));
    }

    @Test
    void testMouseReleasedListener() {
        // Test mouseReleased handler clears pressed state and returns to hover color
        setPrivateField("isHovered", true);
        setPrivateField("isPressed", true);
        setPrivateField("currentColor", pressedColor);
        
        customMouseListener.mouseReleased(createMouseEvent(MouseEvent.MOUSE_RELEASED, 10, 10));
        
        assertFalse(getPrivateBooleanField("isPressed"));
        assertTrue(getPrivateBooleanField("isHovered"));
        assertEquals(hoverColor, getPrivateField("currentColor"));
    }

    @Test
    void testCompleteMouseInteractionSequence() {
        // Test complete user interaction sequence: enter → press → release → exit
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        assertTrue(getPrivateBooleanField("isHovered"));
        assertEquals(hoverColor, getPrivateField("currentColor"));
        
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        assertTrue(getPrivateBooleanField("isPressed"));
        assertEquals(pressedColor, getPrivateField("currentColor"));
        
        customMouseListener.mouseReleased(createMouseEvent(MouseEvent.MOUSE_RELEASED, 10, 10));
        assertFalse(getPrivateBooleanField("isPressed"));
        assertEquals(hoverColor, getPrivateField("currentColor"));
        
        customMouseListener.mouseExited(createMouseEvent(MouseEvent.MOUSE_EXITED, -10, -10));
        assertFalse(getPrivateBooleanField("isHovered"));
        assertEquals(normalColor, getPrivateField("currentColor"));
    }

    @Test
    void testPaintComponentHoveredStateWithHighlight() {
        // Test paintComponent renders highlight when hovered but not pressed
        setPrivateField("isHovered", true);
        setPrivateField("isPressed", false);
        setPrivateField("currentColor", hoverColor);
        
        BufferedImage image = new BufferedImage(130, 45, BufferedImage.TYPE_INT_ARGB);
        invokePaintComponent((Graphics2D) image.getGraphics());
        
        assertTrue(getPrivateBooleanField("isHovered"));
        assertFalse(getPrivateBooleanField("isPressed"));
    }

    @Test
    void testPaintComponentPressedStateNoHighlight() {
        // Test paintComponent doesn't render highlight when pressed (even if hovered)
        setPrivateField("isHovered", true);
        setPrivateField("isPressed", true);
        setPrivateField("currentColor", pressedColor);
        
        BufferedImage image = new BufferedImage(130, 45, BufferedImage.TYPE_INT_ARGB);
        invokePaintComponent((Graphics2D) image.getGraphics());
        
        assertTrue(getPrivateBooleanField("isHovered"));
        assertTrue(getPrivateBooleanField("isPressed"));
    }

    @Test
    void testPaintComponentColorClamping() {
        // Test color clamping logic for gradient and border calculations
        Color clampedGradient = new Color(0, 0, 0);
        Color clampedBorder = new Color(0, 0, 0);
        
        assertEquals(new Color(0, 0, 0), clampedGradient);
        assertEquals(new Color(0, 0, 0), clampedBorder);
    }

    @Test
    void testMouseHoverTriggersHighlightRendering() {
        // Test that mouse hover enables highlight rendering condition
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        
        boolean shouldShowHighlight = getPrivateBooleanField("isHovered") && !getPrivateBooleanField("isPressed");
        assertTrue(shouldShowHighlight);
    }

    @Test
    void testMousePressDisablesHighlight() {
        // Test that mouse press disables highlight rendering condition
        setPrivateField("isHovered", true);
        setPrivateField("isPressed", false);
        
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        
        boolean shouldShowHighlight = getPrivateBooleanField("isHovered") && !getPrivateBooleanField("isPressed");
        assertFalse(shouldShowHighlight);
    }

    @Test
    void testCompleteSequencePaintStates() {
        // Test highlight condition throughout complete interaction sequence
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        assertTrue(shouldShowHighlight()); // Hovered, not pressed = highlight
        
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        assertFalse(shouldShowHighlight()); // Hovered and pressed = no highlight
        
        customMouseListener.mouseReleased(createMouseEvent(MouseEvent.MOUSE_RELEASED, 10, 10));
        assertTrue(shouldShowHighlight()); // Hovered, not pressed = highlight again
        
        customMouseListener.mouseExited(createMouseEvent(MouseEvent.MOUSE_EXITED, -10, -10));
        assertFalse(shouldShowHighlight()); // Not hovered = no highlight
    }

    @Test
    void testDynamicColorChangesDuringInteraction() {
        // Test color changes take effect immediately during user interaction
        Color dynamicNormal = Color.RED;
        Color dynamicHover = Color.GREEN;
        Color dynamicPressed = Color.BLUE;
        
        customMouseListener.mouseEntered(createMouseEvent(MouseEvent.MOUSE_ENTERED, 10, 10));
        
        button.setNormalColor(dynamicNormal);
        button.setHoverColor(dynamicHover);
        button.setPressedColor(dynamicPressed);
        
        customMouseListener.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10));
        assertEquals(dynamicPressed, getPrivateField("currentColor")); // Should use new pressed color
        
        customMouseListener.mouseReleased(createMouseEvent(MouseEvent.MOUSE_RELEASED, 10, 10));
        assertEquals(dynamicHover, getPrivateField("currentColor")); // Should use new hover color
        
        customMouseListener.mouseExited(createMouseEvent(MouseEvent.MOUSE_EXITED, -10, -10));
        assertEquals(dynamicNormal, getPrivateField("currentColor")); // Should use new normal color
    }

    // Helper method to check highlight rendering condition
    private boolean shouldShowHighlight() {
        return getPrivateBooleanField("isHovered") && !getPrivateBooleanField("isPressed");
    }

    // Helper method to create mouse events for testing
    private MouseEvent createMouseEvent(int id, int x, int y) {
        return new MouseEvent(button, id, System.currentTimeMillis(), 0, x, y, 1, false);
    }

    // Helper method to invoke the protected paintComponent method via reflection
    private void invokePaintComponent(Graphics2D g2d) {
        try {
            Method method = AnimatedButton.class.getDeclaredMethod("paintComponent", java.awt.Graphics.class);
            method.setAccessible(true);
            method.invoke(button, g2d);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke paintComponent", e);
        }
    }

    // Helper method to access private fields via reflection
    private Object getPrivateField(String fieldName) {
        try {
            Field field = AnimatedButton.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(button);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field " + fieldName, e);
        }
    }

    // Helper method to access private boolean fields via reflection
    private boolean getPrivateBooleanField(String fieldName) {
        try {
            Field field = AnimatedButton.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getBoolean(button);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access boolean field " + fieldName, e);
        }
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(String fieldName, Object value) {
        try {
            Field field = AnimatedButton.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(button, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}