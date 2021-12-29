package com.example.mylists.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.model.Category;
import com.example.mylists.model.Dish;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DbOperations extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private final String TAG = "DB operation";

    private static final String DB_NAME = "restaurant.db";

    private static final String CREATE_DISH_TABLE = "create table if not exists " +
            Dish.DishContract.DishEntry.TABLE_NAME + "(" +
            Dish.DishContract.DishEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Dish.DishContract.DishEntry.TITLE + " TEXT ," +
            Dish.DishContract.DishEntry.CODE + " TEXT ," +
            Dish.DishContract.DishEntry.ID_CATEGORY + " INTEGER NOT NULL " + "," +
            Dish.DishContract.DishEntry.DESCRIPTION + " TEXT ," +
            "FOREIGN KEY (" + Dish.DishContract.DishEntry.ID_CATEGORY + ")" +
            " REFERENCES " + Category.CategoryContract.CategoryEntry.TABLE_NAME +
            "(" + Category.CategoryContract.CategoryEntry.ID + "));";

    private static final String CREATE_CATEGORY_TABLE = "create table if not exists " +
            Category.CategoryContract.CategoryEntry.TABLE_NAME + "(" +
            Category.CategoryContract.CategoryEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Category.CategoryContract.CategoryEntry.NAME + " TEXT " + ");";

    /**
     * База данных создаётся
     * @param context
     */
    DbOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "Database created...");
    }

    /**
     * Создание таблиц, если их нет
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_DISH_TABLE);
        Log.d(TAG, "Tables created...");
    }

    /**
     * Добавление категорий
     * @param db
     */
    public void addCategories(SQLiteDatabase db) {
        if(isEmptyCategoryTable(db)) {
            MainActivity.mCategories.clear();
            MainActivity.mCategories.add(new Category("Мясное", 1));
            MainActivity.mCategories.add(new Category("Овощное", 2));
            MainActivity.mCategories.add(new Category("Рыбное", 3));
            MainActivity.mCategories.add(new Category("Сладкое", 4));
            MainActivity.mCategories.add(new Category("Особое", 5));
            System.out.println("isEmptyCategoryTable(db) == true");
            for (Category category : MainActivity.mCategories) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Category.CategoryContract.CategoryEntry.NAME, category.getName());
                db.insert(Category.CategoryContract.CategoryEntry.TABLE_NAME, null, contentValues);
            }
            Log.d(TAG, "Category inserted into empty " +
                    Category.CategoryContract.CategoryEntry.TABLE_NAME + "...");
        } else {
            Log.d(TAG, "isEmptyCategoryTable(db) == false");
            getAllCategories(db);
        }
    }

    /**
     * Проверка является ли таблица категорий пустой
     * @param db
     * @return
     */
    public boolean isEmptyCategoryTable(SQLiteDatabase db) {
        String[] projections = {
                Category.CategoryContract.CategoryEntry.ID
        };

        Cursor cursor = db.query(Category.CategoryContract.CategoryEntry.TABLE_NAME,
                projections,null, null,
                null,null,null );
        return cursor.getCount() == 0;
    }

    /**
     * Добавление инфы о блюде или обновление
     * @param db
     * @param dish
     */
    public void addInfoDish(SQLiteDatabase db, Dish dish) {
        if(existDish(db, dish)) {
            updateDish(db, dish);
            Log.d("Database operations", "One row updated...");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Dish.DishContract.DishEntry.ID_CATEGORY, dish.getIdCategory());
            contentValues.put(Dish.DishContract.DishEntry.TITLE, dish.getTitle());
            contentValues.put(Dish.DishContract.DishEntry.CODE, dish.getCode());
            contentValues.put(Dish.DishContract.DishEntry.DESCRIPTION, dish.getDescription());
            db.insert(Dish.DishContract.DishEntry.TABLE_NAME, null, contentValues);
            Log.d(TAG, "One row inserted...");
        }
    }

    /**
     * Удаление студента
     * @param db
     * @param dish
     */
    public void deleteDish(SQLiteDatabase db, Dish dish) {
        if(existDish(db, dish)) {
            db.delete(
                    Dish.DishContract.DishEntry.TABLE_NAME,
                    Dish.DishContract.DishEntry.ID + " = ?",
                    new String[]{String.valueOf(dish.getId())});
        }
    }

    /**
     * Обновление блюда
     * @param db
     * @param dish
     */
    public void updateDish(SQLiteDatabase db, Dish dish) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Dish.DishContract.DishEntry.ID_CATEGORY, dish.getIdCategory());
        contentValues.put(Dish.DishContract.DishEntry.TITLE, dish.getTitle());
        contentValues.put(Dish.DishContract.DishEntry.CODE, dish.getCode());
        contentValues.put(Dish.DishContract.DishEntry.DESCRIPTION, dish.getDescription());
        db.update(Dish.DishContract.DishEntry.TABLE_NAME, contentValues, Dish.DishContract.DishEntry.ID + " = ?", new String[] {String.valueOf(dish.getId())});
    }

    /**
     * Получение категории по id
     * @param db
     * @param id
     * @return
     */
    @SuppressLint("Range")
    public Category getCategoryById(SQLiteDatabase db, int id) {
        String[] projections = {
                Category.CategoryContract.CategoryEntry.ID,
                Category.CategoryContract.CategoryEntry.NAME
        };
        String selection = Category.CategoryContract.CategoryEntry.ID + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = db.query(Category.CategoryContract.CategoryEntry.TABLE_NAME,
                projections,selection,selectionArgs,
                null,null,null );
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Category.CategoryContract.CategoryEntry.NAME));
        return new Category(name, id);
    }

    /**
     * Получение списка блюд по id категории
     * @param db
     * @param id_category
     */
    public void getAllDishes(SQLiteDatabase db, int id_category) {
        String[] projections = {
                Dish.DishContract.DishEntry.ID,
                Dish.DishContract.DishEntry.TITLE,
                Dish.DishContract.DishEntry.CODE,
                        Dish.DishContract.DishEntry.ID_CATEGORY,
                        Dish.DishContract.DishEntry.DESCRIPTION
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mDishes.clear();
        String selection = Dish.DishContract.DishEntry.ID_CATEGORY + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id_category)};
        /**
         * имя таблицы, что достаём, условие, аргументы подставляемые в условие
         */
        Cursor cursor = db.query(Dish.DishContract.DishEntry.TABLE_NAME, projections,
                selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.ID));
            @SuppressLint("Range") String title = cursor.getString(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.TITLE));
            @SuppressLint("Range") String code = cursor.getString(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.CODE));
            @SuppressLint("Range") String description = cursor.getString(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.DESCRIPTION));
            Dish dish = new Dish(title, getCategoryById(db, id_category), code, description);
            dish.setId(id);
            MainActivity.mDishes.add(dish);
        }
    }

    /**
     * Получение списка факультетов
     *
     * @param db
     */
    public void getAllCategories(SQLiteDatabase db) {
        String[] projections = {
                Category.CategoryContract.CategoryEntry.ID,
                Category.CategoryContract.CategoryEntry.NAME
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mCategories.clear();
        Cursor cursor = db.query(Category.CategoryContract.CategoryEntry.TABLE_NAME, projections,
                null,null,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Category.CategoryContract.CategoryEntry.ID));
            @SuppressLint("Range") String name = cursor.getString(
                    cursor.getColumnIndex(Category.CategoryContract.CategoryEntry.NAME));

            Category category = new Category(name, id);
            MainActivity.mCategories.add(category);
        }

    }

    /**
     * Проверка существует ли студент
     * @param db
     * @param dish
     * @return
     */
    public boolean existDish(SQLiteDatabase db, Dish dish) {
        String[] projections = {
                Dish.DishContract.DishEntry.ID
        };
        String selection = Dish.DishContract.DishEntry.ID + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(dish.getId())};
        Cursor cursor = db.query(Dish.DishContract.DishEntry.TABLE_NAME,
                projections,selection,selectionArgs,
                null,null,null );
        return cursor.getCount() > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
