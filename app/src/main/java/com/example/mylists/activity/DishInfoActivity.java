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

    private Dish s;
    private ArrayList<Category> categories;
    private int checkedItemPosition;
    private Spinner mySpinner;
    private CategoryListAdapter categoryListAdapter;
    private int currentIdCategoryChose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        checkedItemPosition = -1;
        currentIdCategoryChose = getIntent().getIntExtra("currentCategory", -1);
        s = getIntent().getParcelableExtra("student");
        categories = getIntent().getParcelableArrayListExtra("categories");
        System.out.println("categories in StudentInfoActivity " + categories);

        ((EditText) findViewById(R.id.editFIO)).setText(s.getFIO());
        /**
         *  Спиннер для выбора факультета
         */
        mySpinner = (Spinner) findViewById(R.id.editCategory);
        categoryListAdapter = new CategoryListAdapter(this, android.R.layout.simple_spinner_item, categories);
        mySpinner.setAdapter(categoryListAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Category currentCategory = categoryListAdapter.getItem(position);
                s.setIdCategory(currentCategory.getId());
                s.setNameCategory(currentCategory.getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        mySpinner.setSelection(s.getIdCategory() - 1);

        ((EditText) findViewById(R.id.editGroup)).setText(s.getGroup());
    }

    public void clSave(View view) {
        s.setFIO(((EditText) findViewById(R.id.editFIO)).getText().toString());
        s.setGroup(((EditText) findViewById(R.id.editGroup)).getText().toString());
        Category category = categoryListAdapter.getItem(mySpinner.getSelectedItemPosition());
        s.setIdCategory(category.getId());
        s.setNameCategory(category.getName());
        saveData(s);
        Intent intent = new Intent();
        intent.putExtra("student",s);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clExit(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public int getIdFaculty(String name) {
        for(Category category : categories) {
            if(category.getName().equals(name)) {
                return category.getId();
            }
        }
        return -1;
    }
    public String getNameFaculty() {
        String nameFaculty = "";
        for(Category category : categories) {
            if(category.getId() == currentIdCategoryChose) {
                nameFaculty = category.getName();
            }
        }
        return nameFaculty;
    }
    @Override
    public void onBackPressed() {
        boolean err = false;
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editFIO)).getText().toString())){
            ((EditText) findViewById(R.id.editFIO)).setError("Не указано ФИО");
            err = true;
        }
        if(!mySpinner.isSelected()){
            s.setIdCategory(currentIdCategoryChose);
            s.setNameCategory(getNameFaculty());
        }
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editGroup)).getText().toString())){
            ((EditText) findViewById(R.id.editGroup)).setError("Не указана группа");
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
            quitDialog.setTitle("Закрыть дополнительную информацию о студенте?")
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
        System.out.println("student " + dish);
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