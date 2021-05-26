package net.bigtangle.wallet.activity.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.bigtangle.utils.MarketOrderItem;
import net.bigtangle.wallet.R;

import java.util.List;

public class CurAdapter extends BaseAdapter {
    private List<MarketOrderItem> itemList;
    private Context mContext;

    public CurAdapter(Context context, List<MarketOrderItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_content_delete, null);
        }

        ((TextView) convertView.findViewById(R.id.tv_content)).setText((CharSequence) itemList.get(position));
        return convertView;
    }
}
