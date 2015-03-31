package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_PHOTO2 = "photo2";
    private static final String JSON_PHOTO3 = "photo3";
    private static final String JSON_PHOTO4 = "photo4";
    private static final String JSON_NUMPHOTOS = "numPhotos";
    private static final int NUM_PHOTO_BLOCKS = 4;
    
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo[] mPhoto = new Photo[4];
    private int mNumPhotos = 0;
    
    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))
            mPhoto[0] = new Photo(json.getJSONObject(JSON_PHOTO));
        if (json.has(JSON_PHOTO2))
            mPhoto[1] = new Photo(json.getJSONObject(JSON_PHOTO2));
        if (json.has(JSON_PHOTO3))
            mPhoto[2] = new Photo(json.getJSONObject(JSON_PHOTO3));
        if (json.has(JSON_PHOTO4))
            mPhoto[3] = new Photo(json.getJSONObject(JSON_PHOTO4));
        mNumPhotos = json.getInt(JSON_NUMPHOTOS);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
        if (mPhoto[0] != null)
            json.put(JSON_PHOTO, mPhoto[0].toJSON());
        if (mPhoto[1] != null)
            json.put(JSON_PHOTO2, mPhoto[1].toJSON());
        if (mPhoto[2] != null)
            json.put(JSON_PHOTO3, mPhoto[2].toJSON());
        if (mPhoto[3] != null)
            json.put(JSON_PHOTO4, mPhoto[3].toJSON());
        json.put(JSON_NUMPHOTOS, mNumPhotos);
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

    public Photo getPhoto() {
        return mPhoto[0];
    }

	public Photo getPhoto(int index) {
		return mPhoto[index];
	}

	public void setPhoto(Photo photo) {
		mPhoto[(mNumPhotos++)%NUM_PHOTO_BLOCKS] = photo;
	}

    public int getMostRecentImageIndex()
    {
        return (mNumPhotos - 1) % NUM_PHOTO_BLOCKS;
    }

    public void DeleteAllPhotos()
    {
        mPhoto = new Photo[NUM_PHOTO_BLOCKS];
        mNumPhotos = 0;
    }
}
