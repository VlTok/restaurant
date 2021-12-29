package com.example.mylists.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mylists.R;
import com.example.mylists.adapter.CategoryListAdapter;
import com.example.mylists.db.BackgroundTask;
import com.example.mylists.model.Category;
import com.example.mylists.model.Dish;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class DishInfoActivity extends AppCompatActivity {

    private Dish dish;
    private ArrayList<Category> categories;
    private Spinner mySpinner;
    private CategoryListAdapter categoryListAdapter;
    private int currentIdCategoryChose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        currentIdCategoryChose = getIntent().getIntExtra("currentCategory",-1);
        dish = getIntent().getParcelableExtra("dish");
        categories = getIntent().getParcelableArrayListExtra("categories");
        System.out.println("categories in DishInfoActivity " + categories);

        ((EditText) findViewById(R.id.editTitle)).setText(dish.getTitle());
        /**
         *  Спиннер для выбора категории блюда
         */
        mySpinner = (Spinner) findViewById(R.id.editCategory);
        categoryListAdapter = new CategoryListAdapter(this, android.R.layout.simple_spinner_item, categories);
        mySpinner.setAdapter(categoryListAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Category currentCategory = categoryListAdapter.getItem(position);
                dish.setIdCategory(currentCategory.getId());
                dish.setNameCategory(currentCategory.getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        mySpinner.setSelection(dish.getIdCategory() - 1);

        ((EditText) findViewById(R.id.editDescription)).setText(dish.getDescription());
        ((EditText) findViewById(R.id.editCode)).setText(dish.getCode());
    }

    public void clSave(View view) {
        dish.setTitle(((EditText) findViewById(R.id.editTitle)).getText().toString());
        dish.setCode(((EditText) findViewById(R.id.editCode)).getText().toString());
        dish.setDescription(((EditText) findViewById(R.id.editDescription)).getText().toString());
        Category category = categoryListAdapter.getItem(mySpinner.getSelectedItemPosition());
        dish.setIdCategory(category.getId());
        dish.setNameCategory(category.getName());
        saveData(dish);
        Intent intent = new Intent();
        intent.putExtra("dish", dish);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clExit(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public int getIdCategory(String name) {
        for(Category category : categories) {
            if(category.getName().equals(name)) {
                return category.getId();
            }
        }
        return -1;
    }
    public String getNameCategory() {
        String nameCategory = "";
        for(Category category : categories) {
            if(category.getId() == currentIdCategoryChose) {
                nameCategory = category.getName();
            }
        }
        return nameCategory;
    }
    @Override
    public void onBackPressed() {
        boolean err = false;
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editTitle)).getText().toString())){
            ((EditText) findViewById(R.id.editTitle)).setError("Не указано название");
            err = true;
        }
        if(!mySpinner.isSelected()){
            dish.setIdCategory(currentIdCategoryChose);
            dish.setNameCategory(getNameCategory());
        }
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editDescription)).getText().toString())){
            ((EditText) findViewById(R.id.editDescription)).setError("Не указано описание");
            err = true;
        }
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editCode)).getText().toString())){
            ((EditText) findViewById(R.id.editCode)).setError("Не указан код блюда");
            err = true;
        }
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        if(!err) {


            quitDialog.setTitle("Сохранить изменения?");
            quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clSave(null);
                }
            })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clExit(null);
                        }
                    })
            ;

        }
        else {
            quitDialog = new AlertDialog.Builder(
                    this);
            quitDialog.setTitle("Закрыть информацию о блюде?")
                    .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clExit(null);
                        }
                    })
                     .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                         }
                     })

            ;

        }
        quitDialog.show();

    }
    public void saveData(Dish dish) {
        // проверка на существование записи о студенте
        // если есть запись, то изменить студента
        // если нет то этот блок
        System.out.println("dish " + dish);
        BackgroundTask backgroundTask = new BackgroundTask(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        backgroundTask.execute("add_info", gson.toJson(dish));

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}