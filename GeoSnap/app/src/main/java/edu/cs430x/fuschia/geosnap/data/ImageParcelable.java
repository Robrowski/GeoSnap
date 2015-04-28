package edu.cs430x.fuschia.geosnap.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matt on 4/27/2015.
 */
public class ImageParcelable implements Parcelable {
    private long id;
    private int age;
    private String phone;
    private boolean registered;

    private String imageUrl;
    private String date;
    private double lat;
    private double lon;
    private String discoverability;

    // No-arg Ctor
    public ImageParcelable(){}

    public ImageParcelable(Cursor c){
        this.imageUrl = c.getString(DiscoveredProjection.COL_PHOTO_URL);
        this.date = c.getString(DiscoveredProjection.COL_TIMESTAMP);
        this.discoverability = c.getString(DiscoveredProjection.COL_DISCOVER);
        this.lat = c.getDouble(DiscoveredProjection.COL_COORD_LAT);
        this.lon = c.getDouble(DiscoveredProjection.COL_COORD_LON);

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDiscoverability() {
        return discoverability;
    }

    public void setDiscoverability(String discoverability) {
        this.discoverability = discoverability;
    }

    /** Used to give additional hints on how to process the received parcel.*/
    @Override
    public int describeContents() {
        // ignore for now
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(imageUrl);
        pc.writeString(discoverability);
        pc.writeString(date);
        pc.writeDouble(lat);
        pc.writeDouble(lon);
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<ImageParcelable> CREATOR = new Parcelable.Creator<ImageParcelable>() {
        public ImageParcelable createFromParcel(Parcel pc) {
            return new ImageParcelable(pc);
        }
        public ImageParcelable[] newArray(int size) {
            return new ImageParcelable[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public ImageParcelable(Parcel pc){
        imageUrl        = pc.readString();
        discoverability = pc.readString();
        date            = pc.readString();
        lat             = pc.readDouble();
        lon             = pc.readDouble();
    }
}
