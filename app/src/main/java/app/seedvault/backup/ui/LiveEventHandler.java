package app.seedvault.backup.ui;

public interface LiveEventHandler<T> {
    void onEvent(T t);
}
