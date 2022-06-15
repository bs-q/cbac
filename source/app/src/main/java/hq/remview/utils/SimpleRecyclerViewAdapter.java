package hq.remview.utils;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SimpleRecyclerViewAdapter<T extends ViewDataBinding, D> extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleRecyclerViewAdapterViewHolder<T>> {

    @Getter
    @Setter
    List<D> items;
    public interface SimpleRecyclerViewCallback<T extends ViewDataBinding>{
        T setUpView(@NonNull ViewGroup parent, int viewType);
        void bindData(SimpleRecyclerViewAdapterViewHolder<T> holder, int position);
        int size();
    }
    private final SimpleRecyclerViewCallback<T> callback;
    public SimpleRecyclerViewAdapter(SimpleRecyclerViewCallback<T> callback){
        this.callback = callback;
    }
    @NonNull
    @Override
    public SimpleRecyclerViewAdapterViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleRecyclerViewAdapterViewHolder<>(callback.setUpView(parent, viewType));
    }

    @Override
    public int getItemCount() {
        return callback.size();
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleRecyclerViewAdapterViewHolder<T> holder, int position) {
        callback.bindData(holder,position);
    }

    public static class SimpleRecyclerViewAdapterViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
        @Getter
        private final T view;
        public SimpleRecyclerViewAdapterViewHolder(@NonNull T view) {
            super(view.getRoot());
            this.view = view;
        }
    }
}
