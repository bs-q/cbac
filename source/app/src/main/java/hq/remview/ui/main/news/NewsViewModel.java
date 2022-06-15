package hq.remview.ui.main.news;

import androidx.databinding.ObservableBoolean;

import java.io.IOException;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.Repository;
import hq.remview.data.model.api.ResponseListObj;
import hq.remview.data.model.api.response.news.NewsDetailResponse;
import hq.remview.data.model.api.response.news.NewsResponse;
import hq.remview.ui.base.activity.BaseCallback;
import hq.remview.ui.base.fragment.BaseFragmentViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class NewsViewModel extends BaseFragmentViewModel {
    // check connection flag
    public ObservableBoolean connection = new ObservableBoolean(true);
    public NewsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    ResponseListObj<NewsResponse> items;
    NewsDetailResponse detail;
    public void getNews(BaseCallback callback){
        compositeDisposable.add(
                repository.getApiService().listNews(0,10).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result->{
                            hideLoading();
                            if (result.getData()!=null){
                                items = result.getData();
                            }
                            callback.doSuccess();
                        },throwable -> {
                            if (throwable instanceof IOException){
                                showErrorMessage(application.getString(R.string.newtwork_error));
                            }
                            hideLoading();
                            Timber.d(throwable);
                        }
                )
        );
    }
    public void getNews(Integer page,Integer size,BaseCallback callback){
        showLoading();
        compositeDisposable.add(
                repository.getApiService().listNews(page,size).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result->{
                                    hideLoading();
                                    if (result.getData()!=null){
                                        items.copy(result.getData());
                                    }
                                    callback.doSuccess();
                                },throwable -> {
                                    if (throwable instanceof IOException){
                                        showErrorMessage(application.getString(R.string.newtwork_error));
                                    }
                                    hideLoading();
                                    Timber.d(throwable);
                                }
                        )
        );
    }
    public void getNewsDetail(Long id,BaseCallback callback){
        compositeDisposable.add(
                repository.getApiService().newsDetail(id).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result->{
                                    if (result.getData()!=null){
                                        detail = result.getData();
                                    }
                                    callback.doSuccess();
                                },throwable -> {
                                    if (throwable instanceof IOException){
                                        showErrorMessage(application.getString(R.string.newtwork_error));
                                    }
                                    hideLoading();
                                    Timber.d(throwable);
                                }
                        )
        );
    }
}
