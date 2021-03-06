package edu.cs430x.fuschia.geosnap.network.imgur;

/**
 * Created by AKiniyalocts on 2/23/15.
 */
public class Constants {
  /*
    Logging flag
   */
  public static final boolean LOGGING = true;

  /*
    Your imgur client id. You need this to upload to imgur.

    More here: https://api.imgur.com/
   */
  public static final String MY_IMGUR_CLIENT_ID = "1a487befe168fec";

  /*
    Client Auth
   */
  public static String getClientAuth(){
    return "Client-ID " + MY_IMGUR_CLIENT_ID;
  }

}
