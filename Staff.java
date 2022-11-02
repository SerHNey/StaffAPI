package com.example.appforstaff;

import android.graphics.Bitmap;

public class Staff {
    String id,name, phone, email;
    Bitmap image;
    public  Staff(String name, String phone, String email, String id, Bitmap image ){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.image = image;

    }
}
