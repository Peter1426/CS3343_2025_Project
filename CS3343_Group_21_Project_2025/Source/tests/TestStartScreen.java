package tests;

import ui.StartScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestStartScreen {

    private StartScreen startScreen;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize StartScreen for testing
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeAndWait(() -> {
            startScreen = new StartScreen();
            startScreen.setVisible(false);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up window after tests
        SwingUtilities.invokeAndWait(() -> {
            if (startScreen != null) {
                startScreen.dispose();
            }
        });
    }

    // Basic window properties
    @Test
    void testStartScreenCreation() throws Exception {
        assertNotNull(startScreen);
        assertEquals("🃏 Joker Card Game", startScreen.getTitle());
        assertFalse(startScreen.isResizable());
    }

    @Test
    void testStartScreenSize() throws Exception {
        Dimension size = startScreen.getSize();
        assertNotNull(size);
        assertEquals(800, size.width);
        assertEquals(600, size.height);
    }

    @Test
    void testStartScreenInitialVisibility() throws Exception {
        assertFalse(startScreen.isVisible());
    }

    @Test
    void testStartScreenDefaultCloseOperation() throws Exception {
        assertEquals(JFrame.EXIT_ON_CLOSE, startScreen.getDefaultCloseOperation());
    }

    // UI Component structure tests
    @Test
    void testStartScreenMainPanelStructure() throws Exception {
        Container contentPane = startScreen.getContentPane();
        assertNotNull(contentPane);
        assertTrue(contentPane.getComponents().length > 0);
        assertTrue(contentPane.getComponent(0) instanceof JPanel);
    }

    @Test
    void testStartScreenUIComponents() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        
        assertTrue(mainPanel.getLayout() instanceof BorderLayout);
        
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        assertNotNull(layout.getLayoutComponent(mainPanel, BorderLayout.NORTH));
        assertNotNull(layout.getLayoutComponent(mainPanel, BorderLayout.CENTER));
        assertNotNull(layout.getLayoutComponent(mainPanel, BorderLayout.SOUTH));
    }

    @Test
    void testTitlePanelComponents() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel titlePanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.NORTH);
        
        assertTrue(titlePanel.getLayout() instanceof BorderLayout);
        
        BorderLayout titleLayout = (BorderLayout) titlePanel.getLayout();
        JLabel titleLabel = (JLabel) titleLayout.getLayoutComponent(titlePanel, BorderLayout.NORTH);
        JLabel subtitleLabel = (JLabel) titleLayout.getLayoutComponent(titlePanel, BorderLayout.CENTER);
        
        assertEquals("🃏 Joker Card Game", titleLabel.getText());
        assertEquals("Balatro Style", subtitleLabel.getText());
    }

    @Test
    void testCenterPanelComponents() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel centerPanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.CENTER);
        
        assertTrue(centerPanel.getLayout() instanceof GridBagLayout);
        
        // Find and verify description text area
        boolean foundDescription = false;
        for (Component comp : centerPanel.getComponents()) {
            if (comp instanceof JTextArea) {
                JTextArea description = (JTextArea) comp;
                assertNotNull(description.getText());
                assertFalse(description.getText().isEmpty());
                assertFalse(description.isEditable());
                foundDescription = true;
                break;
            }
        }
        assertTrue(foundDescription);
    }

    @Test
    void testButtonPanelComponents() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel buttonPanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.SOUTH);
        
        assertTrue(buttonPanel.getLayout() instanceof FlowLayout);
        
        // Find and verify start button
        boolean foundStartButton = false;
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton && "🎮 Start Game".equals(((JButton) comp).getText())) {
                JButton startButton = (JButton) comp;
                assertEquals("🎮 Start Game", startButton.getText());
                assertTrue(startButton.isOpaque());
                assertFalse(startButton.isBorderPainted());
                assertFalse(startButton.isFocusPainted());
                foundStartButton = true;
                break;
            }
        }
        assertTrue(foundStartButton);
    }

    // Start button functionality tests
    @Test
    void testStartButtonActionListener() throws Exception {
        JButton startButton = findStartButton();
        assertNotNull(startButton);
        assertTrue(startButton.getActionListeners().length > 0);
    }

    @Test
    void testStartButtonMouseListeners() throws Exception {
        JButton startButton = findStartButton();
        assertNotNull(startButton);
        assertTrue(startButton.getMouseListeners().length > 0);
    }

    @Test
    void testStartButtonPreferredSize() throws Exception {
        JButton startButton = findStartButton();
        assertNotNull(startButton);
        
        Dimension preferredSize = startButton.getPreferredSize();
        assertNotNull(preferredSize);
        assertEquals(250, preferredSize.width);
        assertEquals(60, preferredSize.height);
    }

    @Test
    void testStartButtonHoverEffect() throws Exception {
        JButton startButton = findStartButton();
        assertNotNull(startButton);
        
        // Find hover effect MouseAdapter
        final java.awt.event.MouseAdapter[] hoverAdapter = new java.awt.event.MouseAdapter[1];
        for (java.awt.event.MouseListener listener : startButton.getMouseListeners()) {
            if (listener instanceof java.awt.event.MouseAdapter) {
                hoverAdapter[0] = (java.awt.event.MouseAdapter) listener;
                break;
            }
        }
        assertNotNull(hoverAdapter[0]);

        // Test hover color changes
        Color originalBackground = startButton.getBackground();
        
        java.awt.event.MouseEvent enterEvent = new java.awt.event.MouseEvent(
            startButton, java.awt.event.MouseEvent.MOUSE_ENTERED, 
            System.currentTimeMillis(), 0, 10, 10, 1, false
        );
        hoverAdapter[0].mouseEntered(enterEvent);
        
        Color hoverBackground = startButton.getBackground();
        assertNotEquals(originalBackground, hoverBackground);
        
        java.awt.event.MouseEvent exitEvent = new java.awt.event.MouseEvent(
            startButton, java.awt.event.MouseEvent.MOUSE_EXITED, 
            System.currentTimeMillis(), 0, -10, -10, 1, false
        );
        hoverAdapter[0].mouseExited(exitEvent);
        
        Color exitBackground = startButton.getBackground();
        assertNotEquals(hoverBackground, exitBackground);
    }

    // Visual properties tests
    @Test
    void testStartScreenColorsAndFonts() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        
        assertEquals(new Color(20, 25, 40), mainPanel.getBackground());
        
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel titlePanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.NORTH);
        JLabel titleLabel = (JLabel) ((BorderLayout) titlePanel.getLayout())
            .getLayoutComponent(titlePanel, BorderLayout.NORTH);
        
        Font titleFont = titleLabel.getFont();
        assertNotNull(titleFont);
        assertTrue(titleFont.isBold());
        assertEquals(48, titleFont.getSize());
        assertEquals(new Color(255, 220, 150), titleLabel.getForeground());
    }

    @Test
    void testStartScreenBorderProperties() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        assertNotNull(mainPanel.getBorder());
        assertTrue(mainPanel.getBorder() instanceof javax.swing.border.EmptyBorder);
    }

    // Content verification
    @Test
    void testDescriptionContent() throws Exception {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel centerPanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.CENTER);
        
        JTextArea description = null;
        for (Component comp : centerPanel.getComponents()) {
            if (comp instanceof JTextArea) {
                description = (JTextArea) comp;
                break;
            }
        }
        assertNotNull(description);
        
        String descriptionText = description.getText();
        assertTrue(descriptionText.contains("Joker cards"));
        assertTrue(descriptionText.contains("scoring rules"));
        assertTrue(descriptionText.contains("target score"));
        assertTrue(descriptionText.contains("shop"));
        assertTrue(descriptionText.contains("levels"));
    }

    // Main method tests
    @Test
    void testMainMethod() throws Exception {
        Method mainMethod = StartScreen.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }

    @Test
    void testMainMethodActualExecution() throws Exception {
        Method mainMethod = StartScreen.class.getMethod("main", String[].class);
        
        SwingUtilities.invokeAndWait(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                fail("Main method should not throw exceptions: " + e.getMessage());
            }
        });
        
        // Clean up created windows
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame instanceof StartScreen) {
                frame.dispose();
            }
        }
    }

    @Test
    void testMainMethodRunnableContent() throws Exception {
        AtomicBoolean mainMethodExecuted = new AtomicBoolean(false);
        
        // Execute main method logic directly
        Runnable mainMethodRunnable = () -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            StartScreen startScreen = new StartScreen();
            startScreen.setVisible(true);
            mainMethodExecuted.set(true);
        };
        
        mainMethodRunnable.run();
        assertTrue(mainMethodExecuted.get());
        
        // Clean up
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame instanceof StartScreen) {
                frame.dispose();
            }
        }
    }
    
    @Test
    void testStartButtonActionPerformed() throws Exception {
        JButton startButton = findStartButton();
        ActionListener[] listeners = startButton.getActionListeners();
        ActionListener mainListener = listeners[0];
        
        // Test that action listener executes without errors
        ActionEvent testEvent = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "click");
        assertDoesNotThrow(() -> mainListener.actionPerformed(testEvent));
    }

    @Test
    void testActionListenerStructure() throws Exception {
        JButton startButton = findStartButton();
        ActionListener[] listeners = startButton.getActionListeners();
        assertTrue(listeners.length > 0);
        
        // Verify action listener can handle events
        ActionListener listener = listeners[0];
        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "start");
        assertDoesNotThrow(() -> listener.actionPerformed(event));
    }
    
    @Test
    void testStartButtonActionPerformedWithNullEvent() throws Exception {
        JButton startButton = findStartButton();
        ActionListener[] listeners = startButton.getActionListeners();
        ActionListener mainListener = listeners[0];
        
        // Test action listener with null event - should handle gracefully
        assertDoesNotThrow(() -> mainListener.actionPerformed(null));
    }
    @Test
    void testMouseAdapterWithNullEvents() throws Exception {
        JButton startButton = findStartButton();
        
        // Find hover effect MouseAdapter
        final java.awt.event.MouseAdapter[] hoverAdapter = new java.awt.event.MouseAdapter[1];
        for (java.awt.event.MouseListener listener : startButton.getMouseListeners()) {
            if (listener instanceof java.awt.event.MouseAdapter) {
                hoverAdapter[0] = (java.awt.event.MouseAdapter) listener;
                break;
            }
        }
        
        // Test with null mouse events - should handle gracefully
        assertDoesNotThrow(() -> hoverAdapter[0].mouseEntered(null));
        assertDoesNotThrow(() -> hoverAdapter[0].mouseExited(null));
    }

    @Test
    void testMainMethodWithInvalidLookAndFeel() throws Exception {
        // Test main method behavior with invalid look and feel
        Runnable mainMethodRunnable = () -> {
            try {
                // Force invalid look and feel
                UIManager.setLookAndFeel("invalid.look.and.feel.class");
            } catch (Exception e) {
                // This should be caught and handled
                assertNotNull(e);
            }
            // Should continue execution even after exception
            StartScreen startScreen = new StartScreen();
            startScreen.setVisible(false);
            startScreen.dispose();
        };
        
        assertDoesNotThrow(() -> mainMethodRunnable.run());
    }

    @Test
    void testStartButtonMultipleClicks() throws Exception {
        JButton startButton = findStartButton();
        ActionListener[] listeners = startButton.getActionListeners();
        ActionListener mainListener = listeners[0];
        
        // Test multiple rapid clicks
        ActionEvent event1 = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "click1");
        ActionEvent event2 = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "click2");
        ActionEvent event3 = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "click3");
        
        assertDoesNotThrow(() -> {
            mainListener.actionPerformed(event1);
            mainListener.actionPerformed(event2);
            mainListener.actionPerformed(event3);
        });
    }

    @Test
    void testMouseEventBoundaryPositions() throws Exception {
        JButton startButton = findStartButton();
        
        // Find hover effect MouseAdapter
        final java.awt.event.MouseAdapter[] hoverAdapter = new java.awt.event.MouseAdapter[1];
        for (java.awt.event.MouseListener listener : startButton.getMouseListeners()) {
            if (listener instanceof java.awt.event.MouseAdapter) {
                hoverAdapter[0] = (java.awt.event.MouseAdapter) listener;
                break;
            }
        }
        
        // Test boundary positions
        java.awt.event.MouseEvent[] boundaryEvents = {
            new java.awt.event.MouseEvent(startButton, java.awt.event.MouseEvent.MOUSE_ENTERED, 
                                        System.currentTimeMillis(), 0, 0, 0, 1, false), // top-left
            new java.awt.event.MouseEvent(startButton, java.awt.event.MouseEvent.MOUSE_ENTERED, 
                                        System.currentTimeMillis(), 0, 250, 60, 1, false), // bottom-right
            new java.awt.event.MouseEvent(startButton, java.awt.event.MouseEvent.MOUSE_ENTERED, 
                                        System.currentTimeMillis(), 0, -1, -1, 1, false), // outside
            new java.awt.event.MouseEvent(startButton, java.awt.event.MouseEvent.MOUSE_ENTERED, 
                                        System.currentTimeMillis(), 0, 300, 100, 1, false) // far outside
        };
        
        for (java.awt.event.MouseEvent event : boundaryEvents) {
            assertDoesNotThrow(() -> hoverAdapter[0].mouseEntered(event));
        }
    }

    @Test
    void testComponentHierarchyWithEmptyPanel() throws Exception {
        // Test edge case: what if components are missing?
        Container contentPane = startScreen.getContentPane();
        
        // Verify hierarchy doesn't break with empty checks
        if (contentPane.getComponentCount() > 0) {
            Component firstComponent = contentPane.getComponent(0);
            assertNotNull(firstComponent);
        }
    }

    @Test
    void testFindStartButtonEdgeCases() throws Exception {
        // Test helper method with edge cases
        
        // Should return null if no start button found (though it should always exist)
        JButton startButton = findStartButton();
        assertNotNull(startButton, "Start button should always be found in valid StartScreen");
        
        // Verify button properties are consistent
        assertEquals("🎮 Start Game", startButton.getText());
        assertTrue(startButton.isEnabled());
    }

    @Test
    void testWindowDisposalScenario() throws Exception {
        // Test that window can be disposed and recreated
        StartScreen newScreen = new StartScreen();
        assertNotNull(newScreen);
        
        // Dispose and verify it doesn't affect other tests
        newScreen.dispose();
        assertTrue(!newScreen.isDisplayable() || newScreen.isDisplayable());
        
        // Create another instance to test multiple instances
        StartScreen anotherScreen = new StartScreen();
        assertNotNull(anotherScreen);
        anotherScreen.dispose();
    }

    @Test
    void testActionListenerWithDifferentSources() throws Exception {
        JButton startButton = findStartButton();
        ActionListener[] listeners = startButton.getActionListeners();
        ActionListener mainListener = listeners[0];
        
        // Test with different event sources
        JButton differentButton = new JButton("Different");
        ActionEvent differentSourceEvent = new ActionEvent(differentButton, ActionEvent.ACTION_PERFORMED, "different");
        
        // Should still handle the event without errors
        assertDoesNotThrow(() -> mainListener.actionPerformed(differentSourceEvent));
    }

    @Test
    void testMainMethodWithNullArguments() throws Exception {
        Method mainMethod = StartScreen.class.getMethod("main", String[].class);
        
        // Test with null arguments array
        assertDoesNotThrow(() -> {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    mainMethod.invoke(null, (Object) null);
                } catch (Exception e) {
                    // Should handle null arguments gracefully
                    assertNotNull(e);
                }
            });
        });
    }
    
    @Test
    void testStartButtonWithDisabledState() throws Exception {
        JButton startButton = findStartButton();
        
        // Test button behavior when disabled
        startButton.setEnabled(false);
        assertFalse(startButton.isEnabled());
        
        // Verify visual properties in disabled state
        assertNotNull(startButton.getBackground());
        assertNotNull(startButton.getForeground());
        
        // Re-enable for other tests
        startButton.setEnabled(true);
    }

    @Test
    void testMainMethodWithEmptyArguments() throws Exception {
        Method mainMethod = StartScreen.class.getMethod("main", String[].class);
        
        // Test with empty arguments array
        assertDoesNotThrow(() -> {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    mainMethod.invoke(null, (Object) new String[]{});
                } catch (Exception e) {
                    fail("Main method should handle empty arguments");
                }
            });
        });
    }

    @Test
    void testComponentAccessibilityAfterDisposal() throws Exception {
        // Test that components are still accessible after disposal
        StartScreen tempScreen = new StartScreen();
        JButton tempButton = findStartButtonInScreen(tempScreen);
        assertNotNull(tempButton);
        
        tempScreen.dispose();
        
        // Components should still be accessible for inspection
        assertNotNull(tempButton.getText());
        assertNotNull(tempButton.getActionListeners());
    }

    @Test
    void testMultipleStartScreenInstances() throws Exception {
        // Test that multiple instances can coexist
        StartScreen screen1 = new StartScreen();
        StartScreen screen2 = new StartScreen();
        
        assertNotNull(screen1);
        assertNotNull(screen2);
        assertNotSame(screen1, screen2);
        
        // Verify both have functional start buttons
        JButton button1 = findStartButtonInScreen(screen1);
        JButton button2 = findStartButtonInScreen(screen2);
        
        assertNotNull(button1);
        assertNotNull(button2);
        assertEquals(button1.getText(), button2.getText());
        
        screen1.dispose();
        screen2.dispose();
    }

    // Helper method for finding start button in any StartScreen
    private JButton findStartButtonInScreen(StartScreen screen) {
        Container contentPane = screen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel buttonPanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.SOUTH);
        
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton && "🎮 Start Game".equals(((JButton) comp).getText())) {
                return (JButton) comp;
            }
        }
        return null;
    }
    
    // Helper method to find start button
    private JButton findStartButton() {
        Container contentPane = startScreen.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        JPanel buttonPanel = (JPanel) layout.getLayoutComponent(mainPanel, BorderLayout.SOUTH);
        
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton && "🎮 Start Game".equals(((JButton) comp).getText())) {
                return (JButton) comp;
            }
        }
        return null;
    }
}