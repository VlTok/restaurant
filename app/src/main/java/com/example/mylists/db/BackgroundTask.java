package com.example.mylists.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.model.Dish;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BackgroundTask extends AsyncTask<String, Void, String> {
    @SuppressLint("StaticFieldLeak")
    Context context;
    public BackgroundTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        DbOperations dbOperations = new DbOperations(context);
        String method = strings[0];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        switch (method) {
            case "add_info": {
                SQLiteDatabase db = dbOperations.getWritableDatabase();
                Dish dish = gson.fromJson(strings[1], Dish.class);
                Log.d("Add", dish.getTitle());
                dbOperations.addInfoDish(db, dish);
                break;
            }
            case "delete_dish": {
                SQLiteDatabase db = dbOperations.getWritableDatabase();
                Dish dish = gson.fromJson(strings[1], Dish.class);
                Log.d("Delete", dish.getTitle());
                dbOperations.deleteDish(db, dish);
                break;
            }
            case "get_dishes": {
                SQLiteDatabase db = dbOperations.getReadableDatabase();
                int categoryId = Integer.parseInt(strings[1]);
                Log.d("Get ", "dishes");
                dbOperations.getAllDishes(db, categoryId);
                break;
            }
            case "get_categories": {
                SQLiteDatabase db = dbOperations.getReadableDatabase();
                Log.d("Get ", "categories");
                dbOperations.addCategories(db);
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity.loadCategoryIntoNavigationView();
        MainActivity.mDishListAdapter.notifyDataSetChanged();
    }
}
