package hq.remview.ui.main.revenue.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import hq.remview.R;

public class HeaderItem
        extends AbstractHeaderItem<HeaderItem.HeaderViewHolder>  {

    private Integer id;
    private Integer name;
    private String date = "";

    public void setDate(String date) {
        this.date = date;
    }

    RevenueItemClick listener;

    public HeaderItem(Integer id,Integer name,RevenueItemClick listener) {
        super();
        this.id = id;
        this.listener = listener;
        this.name = name;
        setDraggable(true);
        setSelectable(true);
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HeaderItem) {
            HeaderItem inItem = (HeaderItem) inObject;
            return this.getId().equals(inItem.getId());
        }
        return false;
    }

    private Integer getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }



    @Override
    public int getLayoutRes() {
        return R.layout.layout_revenue_grid_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        HeaderViewHolder viewHolder = new HeaderViewHolder(view,adapter);
        return viewHolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        holder.name.setText(holder.itemView.getResources().getString(name));
    }

    static class HeaderViewHolder extends FlexibleViewHolder {
        TextView name;
        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);//True for sticky
            this.name = view.findViewById(R.id.name);
            this.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @Override
    public String toString() {
        return "HeaderItem";
    }

}