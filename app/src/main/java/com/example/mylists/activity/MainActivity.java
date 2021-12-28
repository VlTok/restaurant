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
import com.example.mylists.compare.CompareByCode;
import com.example.mylists.compare.CompareByTitle;
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
    public static int mCategorySelectedId = -1;
    public static Map<Integer, ArrayList<Dish>> mapIdCategoryToDishes;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private static boolean was_updated = false;
    private static String TAG = "MainActivity";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_two);
        checkedItemPosition = -1;
        if(mapIdCategoryToDishes == null) {
            mapIdCategoryToDishes = new HashMap<>();
        }
        was_updated = false;
        //если программа открывется в первый раз, создаём список блюд
        //если список пустой, проверяем, нет ли сохранённых данных о блюдах. Если есть - добавляем в список.
        createCategoryList();
        if(mDishes == null) createDishList();
        loadDishes();

        ActionBar toolbar = getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.app_name, R.string.app_name);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        loadCategoryIntoNavigationView();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setDisplayHomeAsUpEnabled(true);


        mIntentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            Dish s = intent.getParcelableExtra("dish");
                            if(mPosition  == mDishes.size() + 1) {
                                if(s.getIdCategory() != mCategorySelectedId) {
                                    ArrayList<Dish> oldDish = mapIdCategoryToDishes.get(s.getIdCategory());
                                    if(oldDish == null) {
                                        oldDish = new ArrayList<Dish>();
                                    }
                                    oldDish.add(s);
                                    mapIdCategoryToDishes.put(s.getIdCategory(), oldDish);
                                }
                                else mDishes.add(s);
                            }
                            else {
                                if(s.getIdCategory() != mCategorySelectedId) {
                                    ArrayList<Dish> oldDish = mapIdCategoryToDishes.get(s.getIdCategory());
                                    if(oldDish == null) {
                                        oldDish = new ArrayList<Dish>();
                                    }
                                    oldDish.add(s);
                                    mapIdCategoryToDishes.put(s.getIdCategory(), oldDish);
                                    mDishes.remove(mPosition);
                                }
                                else mDishes.set(mPosition, s);
                            }
                            mDishListAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),
                                    "Блюдо: " + s.getTitle().toString() + "\nУспешно сохранено", Toast.LENGTH_SHORT).show();
                        }else if (result.getResultCode() == Activity.RESULT_CANCELED){

                        }
                    }
                }
        );

        View.OnLongClickListener OLCL_TITLE= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByTitle());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        View.OnLongClickListener OLCL_CATEGORY= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByCategory());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        View.OnLongClickListener OLCL_CODE= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Collections.sort(mDishes,new CompareByCode());
                mDishListAdapter.notifyDataSetChanged();
                ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.GONE);
                ListView listView = findViewById(R.id.lvList2);
                listView.setItemChecked(checkedItemPosition,false);
                listView.setSelected(false);
                checkedItemPosition = -1;
                return false;
            }
        };

        ((LinearLayout) findViewById(R.id.llInfo_Title)).setOnLongClickListener(OLCL_TITLE);
        ((TextView) findViewById(R.id.tvInfo_Title)).setOnLongClickListener(OLCL_TITLE);
        ((TextView) findViewById(R.id.textView7)).setOnLongClickListener(OLCL_TITLE);
        ((LinearLayout) findViewById(R.id.llInfo_Category)).setOnLongClickListener(OLCL_CATEGORY);
        ((TextView) findViewById(R.id.tvInfo_Category)).setOnLongClickListener(OLCL_CATEGORY);
        ((TextView) findViewById(R.id.textView14)).setOnLongClickListener(OLCL_CATEGORY);
        ((LinearLayout) findViewById(R.id.llInfo_Code)).setOnLongClickListener(OLCL_CODE);
        ((TextView) findViewById(R.id.tvInfo_Code)).setOnLongClickListener(OLCL_CODE);
        ((TextView) findViewById(R.id.textView15)).setOnLongClickListener(OLCL_CODE);
    }

    /**
     * Загрузка списка категорий в меню навигации
     */
    public static void loadCategoryIntoNavigationView() {
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
     * Загрузка списка блюд, если выбрана категория
     */
    public void loadDishes() {
        if(mCategories == null) return;
        Category currentCategory = getById(mCategorySelectedId);
        if(currentCategory != null) {
            loadDishesFromDB();
            Log.d(TAG, "loaded dishes");
        }
        mDishListAdapter.notifyDataSetChanged();
    }

    /**
     * Сокрытие/пока пунктов добавления, удаления, редактирования
     * @param menu
     * @param hidden
     */
    @SuppressLint("ResourceType")
    public void hideMenuForDishes(Menu menu, boolean hidden) {
        menu.findItem(R.id.miAdd).setVisible(!hidden);
        menu.findItem(R.id.miDelete).setVisible(!hidden);
        menu.findItem(R.id.miEdit).setVisible(!hidden);
    }

    /**
     * Вызов функции сокрытия пунктов меню при отстутствии выбранной категории
     * @param menu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mCategorySelectedId != -1) {
            hideMenuForDishes(menu, false);
        } else {
            hideMenuForDishes(menu, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Получение категории по выбранному пункту меню навигации
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
     * Получение категории по названию
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
    public void createCategoryList() {
        if(mCategories == null) mCategories = new ArrayList<>();
        loadCategories();
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
                    intent.putExtra("dish", mDishes.get(position));
                    intent.putParcelableArrayListExtra("categories", mCategories);
                    intent.putExtra("currentCategory", mCategorySelectedId);
                    mPosition=position;
                    mIntentActivityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Блюдо не выбрано", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAdd:{
                Intent intent = new Intent(MainActivity.this, DishInfoActivity.class);

                Dish s = new Dish();
                s.setIdCategory(mCategorySelectedId);
                s.setNameCategory(getById(mCategorySelectedId).getName());
                intent.putExtra("dish", s);
                intent.putExtra("currentCategory", mCategorySelectedId);
                intent.putParcelableArrayListExtra("categories", mCategories);
                mPosition= mDishes.size() + 1;
                mIntentActivityResultLauncher.launch(intent);
                return true;
            }
            case R.id.miDelete:{
                if(listView.isSelected()) {
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                            MainActivity.this);
                    quitDialog.setTitle("Удалить блюдо \"" + mDishes.get(position).getTitle() + "\"?");

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
                            "Блюдо не выбрано", Toast.LENGTH_SHORT).show();
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
     * Создание списка блюд
     */
    public void createDishList() {
        mDishes =new ArrayList<>();
        ListView listView = findViewById(R.id.lvList2);
        mDishListAdapter =new DishListAdapter(mDishes,this);
        listView.setAdapter(mDishListAdapter);
        AdapterView.OnItemClickListener clDish = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((((LinearLayout) findViewById(R.id.ll_info_dish)).getVisibility()==View.GONE) || checkedItemPosition != position){
                    listView.setItemChecked(position,true);
                    listView.setSelected(true);
                    ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tvInfo_Title)).setText(mDishes.get(position).getTitle());
                    ((TextView) findViewById(R.id.tvInfo_Category)).setText(mDishes.get(position).getNameCategory());
                    ((TextView) findViewById(R.id.tvInfo_Code)).setText(mDishes.get(position).getCode());
                    checkedItemPosition = position;
                }else {
                    ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.GONE);
                    listView.setItemChecked(position,false);
                    listView.setSelected(false);
                    checkedItemPosition = -1;
                }
            }
        };
        listView.setOnItemClickListener(clDish);
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
            Log.d(TAG, "category not null");
            ArrayList<Dish> oldDishes = new ArrayList<>();
            oldDishes.addAll(mDishes);
            mapIdCategoryToDishes.put(mCategorySelectedId, oldDishes);
            mCategorySelectedId = (int) category.getId();
            ((LinearLayout) findViewById(R.id.ll_info_dish)).setVisibility(View.GONE);
            ListView listView = findViewById(R.id.lvList2);
            listView.setItemChecked(checkedItemPosition,false);
            listView.setSelected(false);
            checkedItemPosition = -1;
            loadDishes();
        }
        else {
            Log.d(TAG, "category is null");
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
     * Сохранение общей инфы о блюде
     */
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

    /**
     * Загрузка блюд из бд
     */
    public void loadDishesFromDB() {
        if(mCategorySelectedId != -1) {
            mDishes.clear();
            if(mapIdCategoryToDishes.containsKey(mCategorySelectedId)) {
                ArrayList<Dish> oldDishes = mapIdCategoryToDishes.get(mCategorySelectedId);
                if(!oldDishes.isEmpty()) {
                    mDishes.addAll(oldDishes);
                    mDishListAdapter.notifyDataSetChanged();
                }

            }
            else {
                BackgroundTask backgroundTask = new BackgroundTask(this);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                backgroundTask.execute("get_dishes", String.valueOf(getById(mCategorySelectedId).getId()));
            }
        }
    }

    /**
     * Удаление блюда
     * @param dish
     */
    public void deleteData(Dish dish) {
        BackgroundTask backgroundTask = new BackgroundTask(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        backgroundTask.execute("delete_dish", gson.toJson(dish));
    }

    /**
     * Загрузка категории
     */
    public void loadCategories() {
        if(mCategories.isEmpty()) {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("get_categories");
        }
    }
}