package hq.remview.ui.main.news.detail;

import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import hq.remview.BR;
import hq.remview.BuildConfig;
import hq.remview.R;
import hq.remview.databinding.ActivityNewDetailBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;

public class NewsDetailActivity extends BaseActivity<ActivityNewDetailBinding,NewsDetailViewModel> {
    public static String url;
    @Override
    public int getLayoutId() {
        return R.layout.activity_new_detail;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.showLoading();
        viewBinding.wv.getSettings().setDomStorageEnabled(true);
        viewBinding.wv.getSettings().setJavaScriptEnabled(true);
        viewBinding.wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        viewBinding.wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                viewModel.hideLoading();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                viewModel.hideLoading();
            }
        });
        String detail = BuildConfig.WEB_URL+"news-detail?id="+url;
        viewBinding.wv.loadUrl(detail);
    }
}
