package com.example.admin.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Admin on 4/21/2017.
 */

public class Detection implements Serializable {
    private int id;
    private Bitmap photoScaned;
    private String detectType;
    private String colorType;

    public Detection() {
    }

    public Detection(Bitmap photoScaned, String detectType, String colorType) {
        this.photoScaned = photoScaned;
        this.detectType = detectType;
        this.colorType = colorType;
    }

    public Detection(int id, Bitmap photoScaned, String detectType, String colorType) {
        this.id = id;
        this.photoScaned = photoScaned;
        this.detectType = detectType;
        this.colorType = colorType;
    }

    public int getId() {
        return id;
    }
    public Bitmap getPhotoScaned() {
        return photoScaned;
    }

    public String getDetectType() {
        return detectType;
    }

    public String getColorType() {
        return colorType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhotoScaned(Bitmap photoScaned) {
        this.photoScaned = photoScaned;
    }

    public void setDetectType(String detectType) {
        this.detectType = detectType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

}
