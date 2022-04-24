package com.ismt.journeyjournal.journal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Journal")
public class JournalEntities implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private  int id;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "subtitle")
    String subTitle;

    @ColumnInfo(name = "dateTime")
    String dateTime;

    @ColumnInfo(name = "imageJournal")
    String imageJournal;

    @ColumnInfo(name = "lat")
    Double lat;

    @ColumnInfo(name = "lng")
    Double lng;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageJournal() {
        return imageJournal;
    }

    public void setImageJournal(String imageJournal) {
        this.imageJournal = imageJournal;
    }


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " : " + dateTime;
    }
}

