package com.models;


public class ExpenseAndCurrencyModel {

    String id;
    String name;
    String desc;

    public ExpenseAndCurrencyModel(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
