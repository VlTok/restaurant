package com.example.mylists.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mylists.model.Category;

import java.util.ArrayList;


public class CategoryListAdapter extends ArrayAdapter<Category> {
   private Context context;
   private ArrayList<Category> categories = new ArrayList<>();

   public CategoryListAdapter(Context context, int textViewResourceId, ArrayList<Category> categories) {
       super(context, textViewResourceId, categories);
       this.context = context;
       this.categories = categories;
   }
   @Override
    public int getCount() {
       return categories.size();
   }
   @Override
    public Category getItem(int position) {
       return categories.get(position);
   }
   @Override
    public long getItemId(int position) {
       return position;
   }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(categories.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(categories.get(position).getName());

        return label;
    }
}
