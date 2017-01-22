package com.saket.attendanceplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;


/**
 * Created by HP on 22-01-2017.
 */

public class customListAdapter extends ArrayAdapter<String> {

    public customListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public customListAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_button_view, null);
        }

        String p = getItem(position);

        if (p != null) {
            Button element =(Button) v.findViewById(R.id.button2);
            element.setTag(p);
            element.setText(p);
        }

        return v;
    }

}