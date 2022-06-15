package hq.remview.ui.main.news.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hq.remview.BuildConfig;
import hq.remview.R;
import hq.remview.data.model.api.response.news.NewsResponse;
import hq.remview.databinding.LayoutNewsItemBinding;
import lombok.Setter;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    public interface NewsAdapterCallback{
        void itemClick(Long value);
    }
    @Setter
    NewsAdapterCallback callback;

    @Setter
    List<NewsResponse> items = new ArrayList<>();
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutNewsItemBinding l = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_news_item,parent,false);
        return new NewsViewHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.l.setName((items.get(position).getTitle()));
        holder.l.getRoot().setOnClickListener(v->callback.itemClick(items.get(position).getId()));
        holder.l.setImage(BuildConfig.BASE_URL+"/v1/file/download"+items.get(position).getAvatar());
        holder.l.executePendingBindings();
    }

    @androidx.databinding.BindingAdapter("glide_load_image")
    public static void glideImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url)
                .centerInside()
                .placeholder(R.drawable.restaurant)
                .into(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        LayoutNewsItemBinding l;
        public NewsViewHolder(@NonNull LayoutNewsItemBinding l) {
            super(l.getRoot());
            this.l = l;
        }
    }
}
