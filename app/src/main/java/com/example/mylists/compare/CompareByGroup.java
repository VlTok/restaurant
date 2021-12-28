package com.example.mylists.compare;

import com.example.mylists.model.Dish;

import java.util.Comparator;

public class CompareByGroup implements Comparator<Dish>{
    @Override
    public int compare(Dish t1, Dish t2) {
        return t1.getGroup().compareTo(t2.getGroup());
    }
}