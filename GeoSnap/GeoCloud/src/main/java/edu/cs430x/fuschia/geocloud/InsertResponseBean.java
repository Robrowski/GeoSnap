package edu.cs430x.fuschia.geocloud;


/**
 * Created by Matt on 4/10/2015.
 */

public class InsertResponseBean {

    private int status;
    private long insertedID;

    public int getStatus() {
        return status;
    }
    public void setStatus(int data) {
        status = data;
    }

    public long getInsertedID() { return insertedID; }
    public void setID(long ID) { insertedID = ID; }
}
