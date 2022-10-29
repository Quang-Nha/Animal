package org.funix.asm3.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Animal implements Parcelable {

    private final Bitmap photo;
    private Bitmap photoBG;
    private final String path, name, content;
    private boolean isFav;

    public Animal(Bitmap photo, Bitmap photoBG, String path, String name, String content, boolean isFav) {
        this.photo = photo;
        this.photoBG = photoBG;
        this.path = path;
        this.name = name;
        this.content = content;
        this.isFav = isFav;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public Bitmap getPhotoBG() {
        return photoBG;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setPhotoBG(Bitmap photoBG) {
        this.photoBG = photoBG;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
