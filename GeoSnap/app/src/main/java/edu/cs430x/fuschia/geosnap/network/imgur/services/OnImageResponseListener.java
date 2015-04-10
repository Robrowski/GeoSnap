package edu.cs430x.fuschia.geosnap.network.imgur.services;


import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;

/**
 * Created by AKiniyalocts on 1/14/15. https://github.com/AKiniyalocts/imgur-android
 *
 * Listener for when an image is uploaded
 */
public interface OnImageResponseListener {
    public void onImageResponse(ImageResponse response);
}
