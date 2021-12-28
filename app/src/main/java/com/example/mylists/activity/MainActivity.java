package com.example.mylists.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylists.R;
import com.example.mylists.adapter.DishListAdapter;
import com.example.mylists.compare.CompareByCategory;
import com.example.mylists.compare.CompareByFio;
import com.example.mylists.compare.CompareByGroup;
import com.example.mylists.db.BackgroundTask;
import com.example.mylists.model.Category;
import com.example.mylists.model.Dish;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private int mPosition;
    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher;
    private int checkedItemPosition;
    public static ArrayList<Dish> mDishes;
    @SuppressLint("StaticFieldLeak")
    public static DishListAdapter mDishListAdapter;
    public static ArrayList<Category> mCategories;
    public static int mFacultySelectedId = -1;
    public static Map<Integer, ArrayList<Dish>> mapIdFacultyToStudents;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private static boolean was_updated = false;
    private static String TAG = "MainActivity";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ll);
        checkedItemPosition = -1;
        if(mapIdFacultyToStudents == null) {
            mapIdFacultyToStudents = new HashMap<>();
        }
        was_updated = false;
        //если программа открывется в первый раз, создаём список студентов
        //если список пустой, проверяем, нет ли сохранённых данных о студентах. Если есть - добавляем в список.
        createFacultetList();
        if(mDishes == null) createStudentList();
        loadStudents();

        ActionBar toolbar = getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.app_name, R.string.app_name);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        loadFacultyIntoNavigationView();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setDisplayHomeAsUpEnabled(true);


        mIntentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            Dish s = intent.getParcelableExtra("student");
                            if(mPosition  == mDishes.size() + 1) {
                                if(s.getIdCategory() != mFacultySelectedId) {
                                    ArrayList<Dish> oldDish = mapIdFacultyToStudents.get(s.getIdCategory());
                                    if(oldDish == null) {
                                        oldDish = new ArrayList<Dish>();
                                    }
                                    oldDish.add(s);
                                    mapIdFacultyToStudents.put(s.getIdCategory(), oldDish);
                                }
                                else mDishes.add(s);
                            }
                            else {
                                if(s.getIdCategory() != mFacultySelectedId) {
                                    ArrayList<Dish> oldDish = mapIdFacultyToStudents.get(s.getIdCategory());
                                    if(oldDish == null) {
                                        oldDish = new ArrayList<Dish>();
                                    }
                                    oldDish.add(s);
                                    mapIdFacultyToStudents.put(s.getIdCategory(), oldDish);
                                    mDishes.remove(mPosition);
                                }
                                else mDishes.set(mPosition, s);
                            }
                            mDishListAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),
                                    "Студент: " + s.getFIO().toString() + "\nУспешно сохранён", Toast.LENGTH_SHORT).show();
                        }else if (result.getResultCode() == Activity.RESULT_CANCELED){

                        }
                    }
                }
        );

        View.OnLongClickListener OLCL_FIO= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByFio());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        View.OnLongClickListener OLCL_Fac= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByCategory());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        View.OnLongClickListener OLCL_Group= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByGroup());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        ((LinearLayout) findViewById(R.id.llInfo_FIO)).setOnLongClickListener(OLCL_FIO);
        ((TextView) findViewById(R.id.tvInfo_FIO)).setOnLongClickListener(OLCL_FIO);
        ((TextView) findViewById(R.id.textView7)).setOnLongClickListener(OLCL_FIO);
        ((LinearLayout) findViewById(R.id.llInfo_Fac)).setOnLongClickListener(OLCL_Fac);
        ((TextView) findViewById(R.id.tvInfo_Fac)).setOnLongClickListener(OLCL_Fac);
        ((TextView) findViewById(R.id.textView14)).setOnLongClickListener(OLCL_Fac);
        ((LinearLayout) findViewById(R.id.llInfo_Group)).setOnLongClickListener(OLCL_Group);
        ((TextView) findViewById(R.id.tvInfo_Group)).setOnLongClickListener(OLCL_Group);
        ((TextView) findViewById(R.id.textView18)).setOnLongClickListener(OLCL_Group);
    }

    /**
     * Загрузка списка факультетов в меню навигации
     */
    public static void loadFacultyIntoNavigationView() {
        Menu drawerMenu = navigationView.getMenu();
        if(mCategories != null && !mCategories.isEmpty()) {
            if(was_updated) return;
            for (Category category : mCategories) {

                drawerMenu.add(category.getName()).setCheckable(true);
            }
            was_updated = true;
        }
    }

    /**
     * Загрузка списка студентов, если выбран факультет
     */
    public void loadStudents() {
        if(mCategories == null) return;
        Category currentCategory = getById(mFacultySelectedId);
        if(currentCategory != null) {
            loadStudentsFromDB();
            Log.d(TAG, "loaded students");
        }
        mDishListAdapter.notifyDataSetChanged();
    }

    /**
     * Сокрытие/пока пунктов добавления, удаления, редактирования
     * @param menu
     * @param hidden
     */
    @SuppressLint("ResourceType")
    public void hideMenuForStudents(Menu menu, boolean hidden) {
        menu.findItem(R.id.miAdd).setVisible(!hidden);
        menu.findItem(R.id.miDelete).setVisible(!hidden);
        menu.findItem(R.id.miEdit).setVisible(!hidden);
    }

    /**
     * Вызов функции сокрытия пунктов меню при отстутствии выбранного факультета
     * @param menu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mFacultySelectedId != -1) {
            hideMenuForStudents(menu, false);
        } else {
            hideMenuForStudents(menu, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Получения факультета по выбранному пункту меню навигации
     * @param id
     * @return
     */
    public static Category getById(int id) {
        for(Category category : mCategories) {
            if(category.getId() == id) {
                return category;
            }
        }
       return null;
    }

    /**
     * Получение факультета по названию
     * @param name
     * @return
     */
    public Category getByName(String name) {
        for(Category category : mCategories) {
            if(category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Загрузка списка факультетов
     */
    public void createFacultetList() {
        if(mCategories == null) mCategories = new ArrayList<>();
        loadFacultets();
    }

    /**
     * Создание меню
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Выбор пункта меню
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ListView listView = findViewById(R.id.lvList2);
        int position = listView.getCheckedItemPosition();

        switch (item.getItemId()){
            case android.R.id.home:
                if(drawer.isOpen()) {
                  drawer.close();
                }
                else {
                    drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.miEdit:{
                if(listView.isSelected()){
                    Intent intent = new Intent(MainActivity.this, DishInfoActivity.class);
                    intent.putExtra("student", mDishes.get(position));
                    intent.putParcelableArrayListExtra("facultets", mCategories);
                    intent.putExtra("currentFacultet", mFacultySelectedId);
                    mPosition=position;
                    mIntentActivityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Студент не выбран", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAdd:{
                Intent intent = new Intent(MainActivity.this, DishInfoActivity.class);

                //mStudents.add(new Student("",getById(currentId),"", ""));
                //intent.putExtra("student", mStudents.get(mStudents.size()-1));
                Dish s = new Dish();
                s.setIdCategory(mFacultySelectedId);
                s.setNameCategory(getById(mFacultySelectedId).getName());
                intent.putExtra("student", s);
                intent.putExtra("currentFacultet", mFacultySelectedId);
                intent.putParcelableArrayListExtra("facultets", mCategories);
                mPosition= mDishes.size() + 1;
                mIntentActivityResultLauncher.launch(intent);
                return true;
            }
            case R.id.miDelete:{
                if(listView.isSelected()) {
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                            MainActivity.this);
                    quitDialog.setTitle("Удалить студента \"" + mDishes.get(position).getFIO() + "\"?");

                    quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // удалить студента в БД
                            deleteData(mDishes.get(position));
                            mDishes.remove(position);
                            mDishListAdapter.notifyDataSetChanged();
                        }
                    })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    quitDialog.show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Студент не выбран", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAbout:{
                AlertDialog.Builder infoDialog = new AlertDialog.Builder(MainActivity.this);
                infoDialog.setTitle("О программе");
                infoDialog.setMessage("Задание на зачет.\n2021г\nКраснодар");
                infoDialog.setCancelable(false);
                infoDialog.setPositiveButton("Прочитано", null);
                infoDialog.show();
                return true;
            }
            case R.id.miExit:{
                finish();
                return true;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Создание списка студентов
     */
    public void createStudentList() {
        mDishes =new ArrayList<>();
        ListView listView = findViewById(R.id.lvList2);
        mDishListAdapter =new DishListAdapter(mDishes,this);
        listView.setAdapter(mDishListAdapter);
        AdapterView.OnItemClickListener clStudent = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((((LinearLayout) findViewById(R.id.ll_info_student)).getVisibility()==View.GONE) || checkedItemPosition != position){
                    listView.setItemChecked(position,true);
                    listView.setSelected(true);
                    ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tvInfo_FIO)).setText(mDishes.get(position).getFIO());
                    ((TextView) findViewById(R.id.tvInfo_Fac)).setText(mDishes.get(position).getNameCategory());
                    ((TextView) findViewById(R.id.tvInfo_Group)).setText(mDishes.get(position).getGroup());
                    checkedItemPosition = position;
                }else {
                    ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
                    listView.setItemChecked(position,false);
                    listView.setSelected(false);
                    checkedItemPosition = -1;
                }
//                mStudentListAdapter.colorChecked(position,parent);
            }
        };
        listView.setOnItemClickListener(clStudent);
    }

    /**
     * Сохраняем данные при остановке активити
     */
    @Override
    protected void onStop() {
        super.onStop();
      if (mDishes != null){
           for(Dish dish : mDishes) {
               Log.d(TAG, "saveData");
               saveData(dish);
           }
        }

    }

    /**
     * Выбор пункта меню навигации
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Category category = getByName((String) item.getTitle());
        if(category != null) {
            Log.d(TAG, "facultet not null");
            ArrayList<Dish> oldDishes = new ArrayList<>();
            oldDishes.addAll(mDishes);
            mapIdFacultyToStudents.put(mFacultySelectedId, oldDishes);
            mFacultySelectedId = category.getId();
            ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
            ListView listView = findViewById(R.id.lvList2);
            listView.setItemChecked(checkedItemPosition,false);
            listView.setSelected(false);
            checkedItemPosition = -1;
            loadStudents();
        }
        else {
            Log.d(TAG, "facultet is null");
        }

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        item.setChecked(true);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
        return false;
    }



    /**
     * Сохранение общей инфы о студенте
     */
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

    /**
     * Загрузка студентов из бд
     */
    public void loadStudentsFromDB() {
        if(mFacultySelectedId != -1) {
            mDishes.clear();
            if(mapIdFacultyToStudents.containsKey(mFacultySelectedId)) {
                ArrayList<Dish> oldDishes = mapIdFacultyToStudents.get(mFacultySelectedId);
                if(!oldDishes.isEmpty()) {
                    mDishes.addAll(oldDishes);
                    mDishListAdapter.notifyDataSetChanged();
                }

            }
            else {
                BackgroundTask backgroundTask = new BackgroundTask(this);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                backgroundTask.execute("get_students", String.valueOf(getById(mFacultySelectedId).getId()));
                //mStudentListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Удаление студента
     * @param dish
     */
    public void deleteData(Dish dish) {
        BackgroundTask backgroundTask = new BackgroundTask(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        backgroundTask.execute("delete_student", gson.toJson(dish));
    }

    /**
     * Загрузка факультетов
     */
    public void loadFacultets() {
        if(mCategories.isEmpty()) {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("get_facultets");
        }
    }
}