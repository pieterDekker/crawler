package crawler.runners; //TODO: change the name of this namespace

@FunctionalInterface
public interface BlockingConsumer<T> {
    public void accept(T t) throws InterruptedException;
}
