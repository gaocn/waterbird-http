package waterbird.space.http.concurrent;

public abstract class PriorityRunnable implements Runnable {

    int priority;

    protected PriorityRunnable(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
