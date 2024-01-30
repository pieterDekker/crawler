package crawler.runners;

@FunctionalInterface
public interface BlockingSupplier<T> {
    public T get() throws InterruptedException;
}
