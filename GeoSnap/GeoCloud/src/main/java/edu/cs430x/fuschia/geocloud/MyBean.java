package edu.cs430x.fuschia.geocloud;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The object model for the data we are sending through endpoints
 */
public class MyBean {

    private String myData;

    public String getData() {
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }
}

@Entity
class Car {
    @Id String vin; // Can be Long, long, or String
    String color;
}