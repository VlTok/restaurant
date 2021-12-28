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
            Dish.DishContract.DishEntry.FIO + " TEXT ," +
            Dish.DishContract.DishEntry.ID_CATEGORY + " INTEGER NOT NULL " + "," +
            Dish.DishContract.DishEntry.GROUP + " TEXT ," +
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
     * Добавление факультетов
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
            getAllFacultets(db);
        }
    }

    /**
     * Проверка является ли таблица факультетов пустой
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
     * Добавление инфы о студенте или обновление
     * @param db
     * @param dish
     */
    public void addInfoStudent(SQLiteDatabase db, Dish dish) {
        if(existStudent(db, dish)) {
            updateStudent(db, dish);
            Log.d("Database operations", "One row updated...");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Dish.DishContract.DishEntry.ID_CATEGORY, dish.getIdCategory());
            contentValues.put(Dish.DishContract.DishEntry.FIO, dish.getFIO());
            contentValues.put(Dish.DishContract.DishEntry.GROUP, dish.getGroup());
            db.insert(Dish.DishContract.DishEntry.TABLE_NAME, null, contentValues);
            Log.d(TAG, "One row inserted...");
        }
    }

    /**
     * Удаление студента
     * @param db
     * @param dish
     */
    public void deleteStudent(SQLiteDatabase db, Dish dish) {
        if(existStudent(db, dish)) {
            db.delete(
                    Dish.DishContract.DishEntry.TABLE_NAME,
                    Dish.DishContract.DishEntry.ID + " = ?",
                    new String[]{String.valueOf(dish.getId())});
        }
    }

    /**
     * Обновление студента
     * @param db
     * @param dish
     */
    public void updateStudent(SQLiteDatabase db, Dish dish) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Dish.DishContract.DishEntry.ID_CATEGORY, dish.getIdCategory());
        contentValues.put(Dish.DishContract.DishEntry.FIO, dish.getFIO());
        contentValues.put(Dish.DishContract.DishEntry.GROUP, dish.getGroup());
        db.update(Dish.DishContract.DishEntry.TABLE_NAME, contentValues, Dish.DishContract.DishEntry.ID + " = ?", new String[] {String.valueOf(dish.getId())});
    }

    /**
     * Получение факультета по id
     * @param db
     * @param id
     * @return
     */
    @SuppressLint("Range")
    public Category getFacultetById(SQLiteDatabase db, int id) {
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
     * Получение списка студентов по id факультета
     * @param db
     * @param id_faculty
     */
    public void getAllStudents(SQLiteDatabase db, int id_faculty) {
        String[] projections = {
                Dish.DishContract.DishEntry.ID,
                Dish.DishContract.DishEntry.FIO,
                        Dish.DishContract.DishEntry.ID_CATEGORY,
                        Dish.DishContract.DishEntry.GROUP
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mDishes.clear();
        String selection = Dish.DishContract.DishEntry.ID_CATEGORY + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id_faculty)};
        /**
         * имя таблицы, что достаём, условие, аргументы подставляемые в условие
         */
        Cursor cursor = db.query(Dish.DishContract.DishEntry.TABLE_NAME, projections,
                selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.ID));
            @SuppressLint("Range") String fio = cursor.getString(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.FIO));
            @SuppressLint("Range") String group = cursor.getString(
                    cursor.getColumnIndex(Dish.DishContract.DishEntry.GROUP));
            Dish dish = new Dish(fio, getFacultetById(db, id_faculty), group);
            dish.setId(id);
            MainActivity.mDishes.add(dish);
        }
    }

    /**
     * Получение списка факультетов
     *
     * @param db
     */
    public void getAllFacultets(SQLiteDatabase db) {
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
    public boolean existStudent(SQLiteDatabase db, Dish dish) {
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
