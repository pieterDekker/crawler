package crawler.runners;

import java.util.function.Function;

public class StepRunnable<T,O> implements Runnable {
    private BlockingSupplier<T> supplier;
    private Function<T, O> function;
    private BlockingConsumer<O> consumer;

    public StepRunnable(BlockingSupplier<T> supplier, Function<T, O> function, BlockingConsumer<O> consumer) {
        this.supplier = supplier;
        this.function = function;
        this.consumer = consumer;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                step();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    private void step() throws InterruptedException {
        T t = supplier.get();
        O o = function.apply(t);
        consumer.accept(o);
    }

    public synchronized void stop() {
        Thread.currentThread().interrupt();
    }
}
