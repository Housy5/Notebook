package housy.nb;

import java.io.IOException;
import javax.swing.JOptionPane;

public class InstanceLock {

    static long delay = 10_000; // 10 seconds.
    static Thread thread;
    static volatile boolean running = false;
    
    static boolean canStart() {
        try {
            return System.currentTimeMillis() > FileHandler.readLockFile() + delay;
        } catch (IOException ex) {
            return true; // this would mean the file doesn't exist yet so no instance is running.
        }
    }
    
    static void stop() throws InterruptedException, IOException {
        running = false;
        thread.join(50);
        FileHandler.emptyLockFile();
    }
    
    static void attemptStart() {
        if (!canStart()) {
            throw new IllegalStateException("Can't run the program when there is an other instance active!\nIf there is no other instance please wait atleast 10 seconds!");
        }
        
        thread = new Thread(() -> {
            while (running) {                
                try {
                    FileHandler.updateLockFile();
                    Thread.sleep(delay - 100);
                } catch (InterruptedException | IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    System.exit(-1);
                }
            }
        });
        
        running = true;
        
        thread.start();
    }
}
