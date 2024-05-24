package housy.nb;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    private static Controller controller;
    private static Model model;
    private static MainFrame mainFrame;
    
    private static void err(String message, boolean fatal) {
        JOptionPane.showMessageDialog(null, message, "E R R O R!", JOptionPane.ERROR_MESSAGE);
        if (fatal) {
            System.exit(-1);
        }
    }
    
    private static void launchUI(JFrame frame) {
        EventQueue.invokeLater(() -> {
            frame.setVisible(true);
        });
    }
    
    private static void initLaf() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            err(ex.getMessage(), true);
        }
    }
    
    private static void initText() {
        try {
            mainFrame.display(FileHandler.loadTextFromFile());
        } catch (IOException ex) {
        }
    }
    
    private static void initModel() {
        try {
            model = FileHandler.loadModelFromFile();
        } catch (IOException | ClassNotFoundException e) {
            model = new Model();
        }
    }
    
    private static void initInstanceLock() {
        if (!InstanceLock.canStart()) {
            err("Can't start the program while there is another instance!\n If there isn't another instance running please wait for 10 seconds before trying again.", true);
            return;
        }
        
        InstanceLock.attemptStart();
    }
    
    public static void main(String[] args) {
        initLaf();
        initInstanceLock();
        initModel();
        controller = new Controller(model);
        mainFrame = new MainFrame(controller, model);
        controller.setMainFrame(mainFrame);
        mainFrame.setController(controller);
        initText();
        launchUI(mainFrame);
    }
    
}
