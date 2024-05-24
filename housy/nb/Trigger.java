package housy.nb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Trigger extends Thread {
    
    private volatile boolean armed = false;
    private volatile boolean saved = false;
    private volatile long tag;
    private long minDelay = 500;
    
    private Runnable function;
    private Collection<Integer> delayData;
    private List<ActionListener> actionListeners;
    
    public Trigger(Collection<Integer> delays, Runnable runnable) {
        start();
        function = runnable;
        delayData = delays;
        actionListeners = new ArrayList<>();
    }
    
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }
    
    private double calculateStandardDeviation(Collection<Integer> values) {
        double mean = calculateMean(values);
        
        double squaredSum = 0;
        
        for (int val : values) {
            double diff = val - mean;
            squaredSum += diff * diff;
        }
        
        double variance = squaredSum / values.size();
        
        return Math.sqrt(variance);
    }

    private double calculateMean(Collection<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().getAsDouble();
    }
    
    private int getDelay() {
        if (delayData.size() < 3) {
            return (int) minDelay;
        }
        
        double scaleFactor = 1;
        double stdDev = calculateStandardDeviation(delayData);
        double delay = calculateMean(delayData) + stdDev * scaleFactor;
                
        return (int) Math.max(delay, minDelay);
    }
    
    @Override
    public void run() {
        long updateTime = 1_000 / 30;
        
        while (true) {
            try {
                
                if (!armed) {
                    Thread.sleep(updateTime);
                    continue;
                }
                
                int delay = getDelay();
                
                if (tag + delay < System.currentTimeMillis()) {
                    function.run(); 
                    armed = false;
                    saved = true;
                    actionListeners.forEach(x -> x.actionPerformed(new ActionEvent(this, -1, "saved")));
                    continue;
                }
                
                Thread.sleep(updateTime);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void reset() {
        saved = false;
    }
    
    public boolean hasSaved() {
        return saved;
    }
    
    public void pushBack() {
        tag = System.currentTimeMillis();
    }
    
    public void trigger() {
        armed = true;
        tag = System.currentTimeMillis();
        saved = false;
    }
    
    public boolean isArmed() {
        return armed;
    }
}
