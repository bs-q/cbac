package hq.remview.ui.base.activity;

public interface BaseCallback {
    void doError(Throwable error);
    void doSuccess();
    default void doFail(){
        // do nothing
    };
}
