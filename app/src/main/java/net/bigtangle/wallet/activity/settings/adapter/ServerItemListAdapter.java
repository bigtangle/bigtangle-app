package net.bigtangle.wallet.activity.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.model.ServerInfoItem;

import java.util.List;

public class ServerItemListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ServerInfoItem> itemList;

    public ServerItemListAdapter(Context context, List<ServerInfoItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
        convertView = _LayoutInflater.inflate(R.layout.list_server_item, null);
        if (convertView != null) {
            TextView serverNameTextView = convertView.findViewById(R.id.server_name_text_view);
            TextView connectionUrlTextView = convertView.findViewById(R.id.connection_url_text_view);
            ServerInfoItem serverInfoItem = this.itemList.get(position);
            serverNameTextView.setText(serverInfoItem.getServerName());
            connectionUrlTextView.setText(serverInfoItem.getConnectionURL());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
