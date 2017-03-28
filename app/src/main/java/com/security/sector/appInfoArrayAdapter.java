package com.security.sector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ViewHolder {
 TextView appName;
}

class appInfoArrayAdapter extends ArrayAdapter<appInfo> {
    private final Context context;
    private final appInfo[] values;

    appInfoArrayAdapter(Context context, appInfo[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            mViewHolder.appName = (TextView) convertView.findViewById(R.id.list_item_label);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.appName.setText(values[position].appName);
        return convertView;
    }

}
