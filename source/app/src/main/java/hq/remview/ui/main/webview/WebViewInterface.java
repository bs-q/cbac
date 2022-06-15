package hq.remview.ui.main.webview;

import android.webkit.JavascriptInterface;

import hq.remview.ui.base.fragment.BaseFragmentViewModel;
import lombok.Setter;
import timber.log.Timber;

public class WebViewInterface {
    public interface WebViewInterfaceHandler {
        BaseFragmentViewModel getViewModel();
        void handleResponse(String cmd, String subCmd, String data, String error);
        void handleError(String errorCode);
        void handlePath(String path);
        void handleBack(String path);
    }

    @Setter
    private WebViewInterfaceHandler webviewInterfaceHandler;


    @JavascriptInterface
    public void response(String cmd, String subCmd, String data, String error){
        Timber.d("Response from nodejs");
        if(webviewInterfaceHandler!=null){
            webviewInterfaceHandler.handleResponse(cmd,subCmd,data,error);
        }
    }

    @JavascriptInterface
    public void showLoading(String message){
        Timber.d("Show loading from nodejs");
        if(webviewInterfaceHandler.getViewModel()!=null){
            webviewInterfaceHandler.getViewModel().showLoading();
        }
    }

    @JavascriptInterface
    public void hideLoading(){
        Timber.d("Hide loading from nodejs");
        if(webviewInterfaceHandler.getViewModel()!=null){
            webviewInterfaceHandler.getViewModel().hideLoading();
        }
    }

    @JavascriptInterface
    public void handleLoadCompleted(String path){
        Timber.d("handle load completed from NodeJS");
        if(webviewInterfaceHandler.getViewModel()!=null){
            webviewInterfaceHandler.getViewModel().hideLoading();
            webviewInterfaceHandler.handlePath(path);
        }

    }
    @JavascriptInterface
    public void handleBackCompleted(String path){
        Timber.d("back from nodejs");
        if(webviewInterfaceHandler.getViewModel()!=null){
            webviewInterfaceHandler.getViewModel().hideLoading();
            webviewInterfaceHandler.handleBack(path);
        }
    }

    @JavascriptInterface
    public void handleError(String errorCode){
        Timber.d("Hide loading from nodejs");
        if(webviewInterfaceHandler.getViewModel()!=null){
            webviewInterfaceHandler.getViewModel().hideLoading();
            webviewInterfaceHandler.handleError(errorCode);
        }
    }
}
