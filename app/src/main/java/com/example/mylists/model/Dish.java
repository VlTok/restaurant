package com.example.mylists.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import java.util.Objects;

public class Dish implements Parcelable {
    private int id;
    private String mTitle;
    private Integer IdCategory;
    private String nameCategory;
    private String mGroup;

    public Dish(String FIO, Category category, String group) {
        mTitle = FIO;
        nameCategory = category.getName();
        IdCategory = category.getId();
        mGroup = group;
        id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    /**
//     * Сравнение студентов
//     * @return
//     */
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Student student = (Student) o;
//        return id == student.id && mFIO.equals(student.mFIO) && IdFaculty.equals(student.IdFaculty) && nameFaculty.equals(student.nameFaculty) && mGroup.equals(student.mGroup) && Objects.equals(mSubjects, student.mSubjects);
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, mTitle, IdCategory, nameCategory, mGroup);
    }

    protected Dish(Parcel in) {
        mTitle = in.readString();
        nameCategory = in.readString();
        IdCategory = in.readInt();
        mGroup = in.readString();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(nameCategory);
        dest.writeInt(IdCategory);
        dest.writeString(mGroup);
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

    public String getFIO() {
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

    public String getGroup() {
        return mGroup;
    }

    public void setFIO(String FIO) {
        mTitle = FIO;
    }



    public void setGroup(String group) {
        mGroup = group;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", mFIO='" + mTitle + '\'' +
                ", IdFaculty=" + IdCategory +
                ", nameFaculty='" + nameCategory + '\'' +
                ", mGroup='" + mGroup +
                '}';
    }
    public Dish() {
        mTitle = "";
        nameCategory = "";
        IdCategory = -1;
        mGroup = "";
        id = -1;
    }

    public static final class DishContract {
        public static abstract class DishEntry {
            public static final String ID = "id";
            public static final String ID_CATEGORY = "id_category";
            public static final String GROUP = "student_group";
            public static final String FIO = "fio";
            public static final String TABLE_NAME = "dish_table";
        }
    }
}
