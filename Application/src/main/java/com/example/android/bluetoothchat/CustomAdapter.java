package com.example.android.bluetoothchat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
public class CustomAdapter extends ArrayAdapter<ParamDetails> {
    private LayoutInflater mlayoutInflater;

    public CustomAdapter(Context context, int textViewResourceId, List<ParamDetails> objects) {
        super(context, textViewResourceId, objects);
        mlayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        ParamDetails item = getItem(position);
        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = mlayoutInflater.inflate(R.layout.param_list, null);
        }
        TextView tv1,tv2,tv3,tv4,tv5;
        tv1 = (TextView) convertView.findViewById(R.id.noTextView);
        tv1.setText(item.getParam_no());

        tv2 = (TextView) convertView.findViewById(R.id.idTextView);
        tv2.setText(item.getParam_id());

        tv3 = (TextView) convertView.findViewById(R.id.valueTextView);
        tv3.setText(String.valueOf(item.getParam_value())); //valueのみintで格納

        tv4 = (TextView) convertView.findViewById(R.id.unitTextView);
        tv4.setText(item.getParam_unit());

        tv5 = (TextView) convertView.findViewById(R.id.nameTextView);
        tv5.setText(item.getParam_name());

        return convertView;
    }
}
