package com.example.graduation.wordbookp2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hkk32 on 2018-08-22.
 */

public class WBadapter extends BaseAdapter
{
    ArrayList<WBlayout> wordbookitem = new ArrayList<WBlayout>();
    public WBadapter(ArrayList<WBlayout> words)
    {
        wordbookitem = words;
    }

    @Override
    public int getCount() {
        return wordbookitem.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_wblayout, parent, false);
        }
        TextView wordnametext = (TextView) convertView.findViewById(R.id.wordname);
        TextView wordmeantext = (TextView) convertView.findViewById(R.id.wordmean);
        TextView wordexplaintext = (TextView) convertView.findViewById(R.id.wordexplain);

        WBlayout listViewItem = wordbookitem.get(position);

        wordnametext.setText((CharSequence) listViewItem.getwordname());
        wordmeantext.setText((CharSequence) listViewItem.getwordmean());
        wordexplaintext.setText((CharSequence) listViewItem.getwordexplain());

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return wordbookitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addword (String wordname, String wordmean, String wordexplain)
    {
        WBlayout item = new WBlayout(wordname, wordmean, wordexplain);

        item.setwordname(wordname);
        item.setwordmean(wordmean);
        item.setwordexplain(wordexplain);

        wordbookitem.add(item);
    }

    public void removeword (int position)
    {
        wordbookitem.remove(position);
    }
}
