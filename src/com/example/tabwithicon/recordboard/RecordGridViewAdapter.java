package com.example.tabwithicon.recordboard;

import java.util.ArrayList;
import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.MultiDevInit;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * adapter class for player's panel
 * */
public class RecordGridViewAdapter extends ArrayAdapter<Item>{

    Context context;
    int layoutResourceId;
    ArrayList<Item> data = new ArrayList<Item>();
    
    /**
     * constructor
     * layoutResourceId:layout's id(row_grid.xml)
     * data:ArrayList of images
     * */
    public RecordGridViewAdapter(Context context, int layoutResourceId, ArrayList<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
    /**
     * method for init player's gridview
     * @return inited gridview of player's panel
     * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            //set layout's size
            row.getLayoutParams().height = MultiDevInit.pH;
            row.getLayoutParams().width = MultiDevInit.pW;

            row.setPadding(0, 0, 0, 0);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Item item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.imageItem.setImageBitmap(item.getImage());
        return row;
    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
    }
}
