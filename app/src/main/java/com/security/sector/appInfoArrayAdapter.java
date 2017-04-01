package com.security.sector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ViewHolder
 * This class is part of the recommended ViewHolder pattern for storing the used TextViews. This
 * way, if the View has already been created it won't recreate it.
 */
class ViewHolder {
 TextView appName;
}

/**
 * appInfoArrayAdapter
 * This class is used to implement my custom adapter that can handle a list of appInfo objects.
 */
class appInfoArrayAdapter extends ArrayAdapter<appInfo> {
    private final Context context;
    private final appInfo[] values;

    /**
     * appInfoArrayAdapter()
     * The constructor for the appInfoArrayAdapter class
     * @param context - The application context
     * @param values - The values I want to use to populate my ListView
     */
    appInfoArrayAdapter(Context context, appInfo[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    /**
     * getView()
     * This method is the meat and potatoes of the appInfoArrayAdapter class. It handles the
     * inflation other expensive UI stuff. This is used internally when the ListView is created.
     * @param position - The position of the item in the ListView.
     * @param convertView - The list item template view
     * @param parent - The ListView we want to populate
     * @return (View) - The newly populated ListView view
     */
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
