package housy.nb;

import java.util.ArrayDeque;

public class CircularBuffer<T> extends ArrayDeque<T> {
    
    private int maxSize;

    public CircularBuffer(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }
    
    @Override
    public boolean add(T data) {
        if (size() == maxSize) {
            removeFirst();
        }
        return super.add(data);
    }
    
    @Override
    public boolean offer(T data) {
        if (size() == maxSize) {
            removeFirst();
        }
        return super.offer(data);
    }
    
}
