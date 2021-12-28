package com.example.mylists.compare;

import com.example.mylists.model.Dish;

import java.util.Comparator;

public class CompareByFio implements Comparator<Dish> {
    @Override
    public int compare(Dish t1, Dish t2) {
        return t1.getFIO().compareTo(t2.getFIO());
    }
}
