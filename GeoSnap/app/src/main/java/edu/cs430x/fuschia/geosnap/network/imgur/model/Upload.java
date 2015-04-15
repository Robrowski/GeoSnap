package edu.cs430x.fuschia.geosnap.network.imgur.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by AKiniyalocts on 2/24/15. https://github.com/AKiniyalocts/imgur-android
 *
 * Basic object for upload.
 */
public class Upload {
  public File image;
  public String title;
  public String description;
  public String albumId;
  public Bitmap bm;
}
