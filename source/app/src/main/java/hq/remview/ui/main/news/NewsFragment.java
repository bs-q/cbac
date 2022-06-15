package hq.remview.ui.main.news;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hq.remview.R;
import hq.remview.databinding.FragmentNewsBinding;
import hq.remview.di.component.FragmentComponent;
import hq.remview.ui.base.fragment.BaseFragment;
import hq.remview.ui.main.MainCalback;
import hq.remview.ui.main.news.adapter.NewsAdapter;
import hq.remview.ui.main.news.detail.NewsDetailActivity;

public class NewsFragment extends BaseFragment<FragmentNewsBinding,NewsViewModel> implements
        View.OnClickListener, NewsAdapter.NewsAdapterCallback {
    NewsAdapter adapter;
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void performDataBinding() {
        binding.setA(this);
        binding.setVm(viewModel);
        adapter = new NewsAdapter();
        adapter.setCallback(this);
        binding.rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rv.setAdapter(adapter);
        binding.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && viewModel.items.hasNext()) { //1 for down
                    getNextNewsPage(viewModel.items.getNext(),10);
                }
            }
        });
    }


    private void getNextNewsPage(Integer page, Integer size){
        viewModel.getNews(page,size,new MainCalback() {
            @Override
            public void doError(Throwable error) {
                // do nothing
            }

            @Override
            public void doSuccess() {
                adapter.setItems(viewModel.items.getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && viewModel.items==null){
            viewModel.showLoading();
            viewModel.getNews(new MainCalback() {
                @Override
                public void doError(Throwable error) {
                    // do nothing
                    viewModel.hideLoading();
                }

                @Override
                public void doSuccess() {
                    adapter.setItems(viewModel.items.getData());
                    adapter.notifyDataSetChanged();
                    viewModel.hideLoading();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.showLoading();
        viewModel.getNews(new MainCalback() {
            @Override
            public void doError(Throwable error) {
                // do nothing
            }

            @Override
            public void doSuccess() {
                adapter.setItems(viewModel.items.getData());
                adapter.notifyDataSetChanged();
                binding.getRoot().postDelayed(()->{
                    viewModel.hideLoading();
                },30000  );
//                viewModel.hideLoading();
            }
        });
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void itemClick(Long value) {
        viewModel.showLoading();
        viewModel.getNewsDetail(value, new MainCalback() {
            @Override
            public void doError(Throwable error) {
                // do nothing
            }

            @Override
            public void doSuccess() {
                Intent it = new Intent(requireActivity(), NewsDetailActivity.class);
                NewsDetailActivity.url = viewModel.detail.getId().toString();
                startActivity(it);
                viewModel.hideLoading();
            }
        });
    }
}
