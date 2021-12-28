package com.example.mylists.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mylists.R;
import com.example.mylists.model.Dish;

import java.util.ArrayList;

public class DishListAdapter extends BaseAdapter {
    ArrayList<Dish> mDishes = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;

    public DishListAdapter(ArrayList<Dish> dishes, Context context) {
        mDishes = dishes;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() { return mDishes.size(); }

    @Override
    public Object getItem(int position){ return mDishes.get(position); }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View view, ViewGroup parent){
        view = mInflater.inflate(R.layout.dish_element, parent, false);
        if (mDishes.isEmpty()) return view;
        ((TextView) view.findViewById(R.id.tvElementTitle)).setText(mDishes.get(position).getTitle());
//        ((TextView) view.findViewById(R.id.tvElementFaculty)).setText(mStudents.get(position).getNameFaculty());
//        ((TextView) view.findViewById(R.id.tvElementGroup)).setText(mStudents.get(position).getGroup());
//        if(position%2==1) ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
//                mContext.getResources().getColor(R.color.odd_element)
//        );
        return view;
    }

//    public void colorChecked(int position, AdapterView<?> parent){
//        View view;
//        ListView listView = parent.findViewById(R.id.lvList2);
//        for (int i = 0; i < mStudents.size(); ++i){
//            view = parent.getChildAt(i);
//            if (i % 2 == 1)
//                ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
//                        mContext.getResources().getColor(R.color.odd_element));
//            else ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
//                    mContext.getResources().getColor(R.color.white));
//
//        }
//        view = parent.getChildAt(position);
//        if(listView.isSelected())
//            ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
//                    mContext.getResources().getColor(R.color.checked_element));
//    }
}
