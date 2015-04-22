package edu.cs430x.fuschia.geocloud;

import java.util.ArrayList;

/**
 * Created by Matt on 4/21/2015.
 */
public class GeoQueryResponseBean {

    public Boolean foundImages;
    public ArrayList<ImageEntity> images;

    public void setImages(ArrayList<ImageEntity> images){
        this.images = images;
    }
    public void  setFoundImages(Boolean found){
        this.foundImages = found;
    }
}
