package com.example.mylists.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import java.util.Objects;

public class Dish implements Parcelable {
    private int id;
    private String mTitle;
    private String mCode;
    private Integer IdCategory;
    private String nameCategory;
    private String mDescription;

    public Dish(String title,String code, Category category, String description) {
        mTitle = title;
        mCode = code;
        nameCategory = category.getName();
        IdCategory = category.getId();
        mDescription = description;
        id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, mTitle,mCode, IdCategory, nameCategory, mDescription);
    }

    protected Dish(Parcel in) {
        mTitle = in.readString();
        nameCategory = in.readString();
        IdCategory = in.readInt();
        mCode = in.readString();
        mDescription = in.readString();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mCode);
        dest.writeString(nameCategory);
        dest.writeLong(IdCategory);
        dest.writeString(mDescription);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public Integer getIdCategory() {
        return IdCategory;
    }

    public void setIdCategory(Integer idCategory) {
        IdCategory = idCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getDesciption() {
        return mDescription;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", mTitle='" + mTitle + '\'' +
                ", mCode='" + mCode + '\'' +
                ", IdCategory=" + IdCategory +
                ", nameCategory='" + nameCategory + '\'' +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }

    public Dish() {
        mTitle = "";
        mCode = "";
        nameCategory = "";
        IdCategory = -1;
        mDescription = "";
        id = -1;
    }

    public static final class DishContract {
        public static abstract class DishEntry {
            public static final String ID = "id";
            public static final String ID_CATEGORY = "id_category";
            public static final String DESCRIPTION = "description";
            public static final String TITLE = "title";
            public static final String CODE = "code";
            public static final String TABLE_NAME = "dish_table";
        }
    }
}
