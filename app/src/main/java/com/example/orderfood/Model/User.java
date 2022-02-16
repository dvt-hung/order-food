package com.example.orderfood.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String name,id;
    private boolean admin;


    public User() {
    }

    public User(String name, boolean admin) {
        this.name = name;
        this.admin = admin;
    }

    public User(String name, String id, boolean admin) {
        this.name = name;
        this.id = id;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
