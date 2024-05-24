package housy.nb;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Controller implements ActionListener, WindowListener, KeyListener {

    private FontChooser fontChooser;
    private ColorChooser colorChooser;
    private MainFrame mainFrame;
    private Trigger saveTrigger;
    private Model model;

    private long lastKeyRelease = 0;
    private CircularBuffer<Integer> keyTimeBuffer;

    public Controller(Model model) {
        this.model = model;
        keyTimeBuffer = new CircularBuffer<>(16);
        saveTrigger = new Trigger(keyTimeBuffer, () -> {
            try {
                FileHandler.saveTextToFile(mainFrame.getDisplayText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        saveTrigger.addActionListener(this);
    }

    public FontChooser getFontChooser() {
        return fontChooser;
    }

    public void setFontChooser(FontChooser fontChooser) {
        this.fontChooser = fontChooser;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("saved")) {
            new Thread(() -> {
                try {
                    Color old = model.fgColor;
                    model.fgColor = Colors.getSaveEffectColor(model.fgColor);
                    mainFrame.update();
                    Thread.sleep(200);
                    model.fgColor = old;
                    mainFrame.update();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();
            return;
        }

        if (e.getActionCommand().equals("selectFontStyle")) {
            selectFontStyle(e);
            return;
        }

        if (e.getActionCommand().equals("showFontSettings")) {
            if (fontChooser == null) {
                fontChooser = new FontChooser(this, model);
            }
            FrameManager.open(fontChooser, mainFrame);
            return;
        }

        if (e.getActionCommand().equals("showColorSettings")) {
            if (colorChooser == null) {
                colorChooser = new ColorChooser(this, model);
            }
            FrameManager.open(colorChooser, mainFrame);
            return;
        }

        if (e.getActionCommand().equals("cancelFontSettings")) {
            fontChooser.dispose();
            return;
        }

        if (e.getActionCommand().equals("resetFontSettings")) {
            fontChooser.reset();
            return;
        }

        if (e.getActionCommand().equals("acceptFontSettings")) {
            fontChooser.dispose();
            model.font = fontChooser.createFont();
            mainFrame.update();
            return;
        }

        if (e.getActionCommand().equals("resetColorSettings")) {
            colorChooser.reset();
            return;
        }

        if (e.getActionCommand().equals("cancelColorSettings")) {
            colorChooser.dispose();
            return;
        }

        if (e.getActionCommand().equals("acceptColorSettings")) {
            model.fgColor = colorChooser.getSelectedTextColor();
            model.bgColor = colorChooser.getSelectedBgColor();
            colorChooser.dispose();
            mainFrame.update();
            return;
        }
    }

    private void selectFontStyle(ActionEvent e) throws IllegalStateException {
        if (e.getSource() instanceof Component comp) {
            fontChooser.selectFontStyle(comp.getName());
        } else {
            throw new IllegalStateException("Can't convert the source object into an component!");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            FileHandler.saveModelToFile(model);
            FileHandler.saveTextToFile(mainFrame.getDisplayText());

            InstanceLock.stop();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Failed to save some data!");
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!e.getComponent().getName().equals("mainTextField")) {
            return;
        }

        if (saveTrigger.isArmed()) {
            saveTrigger.pushBack();
            return;
        }

        saveTrigger.trigger();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!e.getComponent().getName().equals("mainTextField")) {
            return;
        }

        if (e.isActionKey()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastKeyRelease;
        lastKeyRelease = currentTime;

        if (elapsedTime >= 2_000) {
            lastKeyRelease = currentTime;
            return;
        }

        if (elapsedTime <= 50) {
            keyTimeBuffer.add(50);
            return;
        }

        keyTimeBuffer.add((int) elapsedTime);
    }

}
