package hq.remview.ui.main.setting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.ISectionable;
import hq.remview.R;

public class SettingItem extends AbstractFlexibleItem<SettingItem.TestItemViewHolder>
        implements ISectionable<SettingItem.TestItemViewHolder, HeaderItem>, Serializable {

    SettingItemClick listener;
    HeaderItem headerItem;
    private Integer id;
    private Integer name;
    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_revenue_grid_item;
    }

    @Override
    public TestItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new TestItemViewHolder(view);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TestItemViewHolder holder, int position, List<Object> payloads) {
        holder.textView.setText(holder.itemView.getResources().getString(name));
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),id));
        holder.itemView.setOnClickListener(s->listener.itemClick(name));
    }

    @Override
    public HeaderItem getHeader() {
        return headerItem;
    }

    @Override
    public void setHeader(HeaderItem header) {
        this.headerItem = header;
    }

    private SettingItem(Integer id){
        super();
        this.id = id;

    }

    public SettingItem(Integer id, HeaderItem headerItem, Integer name, SettingItemClick listener){
        this(id);
        this.headerItem = headerItem;
        this.name = name;
        this.listener = listener;
    }

    public class TestItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public TestItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.img);
            this.textView = itemView.findViewById(R.id.txt);
        }
    }
}
