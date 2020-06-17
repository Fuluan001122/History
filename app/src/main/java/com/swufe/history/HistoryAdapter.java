package com.swufe.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private Context context = null;
    private List<History> list;


    public HistoryAdapter(Context context, List<History> list) {
        this.context = context;
        this.list = list;
    }

    public void clearList() {
        if (list != null) {
            list.clear();
            notifyDataSetInvalidated();
        }
    }

    public void setList(List<History> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_history, null, true);
            mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
            mHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            mHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            mHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).getTitle().trim().isEmpty()){
            mHolder.layout.setVisibility(View.GONE);
        }else {
            mHolder.layout.setVisibility(View.VISIBLE);
            mHolder.tvTitle.setText(list.get(position).getTitle());
            mHolder.tvDate.setText(list.get(position).getDate());
            Glide.with(context).load(list.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(mHolder.imageView);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private ImageView imageView;
        private LinearLayout layout;
    }

}
