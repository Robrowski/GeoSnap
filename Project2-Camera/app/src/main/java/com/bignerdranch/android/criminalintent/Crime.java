package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

public class Crime {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    
    private UUID mId;
    private String mTitle = "";
    private Date mDate;
    private boolean mSolved;
    private LinkedList<Photo> mList = new LinkedList<Photo>();
    
    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        for (int i=0; i < 4; i++){
            if (json.has(JSON_PHOTO+Integer.toString(i))){
                Log.v("CriminalIntent","loaded photo: " + JSON_PHOTO+i);
                mList.addLast(new Photo(json.getJSONObject(JSON_PHOTO+Integer.toString(i))));
            }
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);


        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
        for (int i = 0; i < mList.size(); i++){
            Log.v("CriminalIntent","saved photo: " + i);
            json.put(JSON_PHOTO+Integer.toString(i),mList.get(i).toJSON());
        }
        return json;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void addPhoto(Photo photo,Context ctx) {
        if (mList.size() > 3){
            Photo remove = mList.get(mList.size()-1);
            ctx.deleteFile(remove.getFilename());
            mList.remove(mList.size() - 1);
        }
        mList.addFirst(photo);
    }

    public LinkedList<Photo> getPhotos() { return mList; }
    
}
