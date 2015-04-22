package edu.cs430x.fuschia.geocloud;

/**
 * Created by Matt on 4/16/2015.
 */
public class Constants {

    public class Discoverability {
        public static final String DISC_SECRET = "secret";
        public static final String DISC_MEDIUM = "medium";
        public static final String DISC_FAR    = "far";
    }

    public class DiscoverRadius {
        public static final int RAD_SECRET = 50;
        public static final int RAD_MEDIUM = 100;
        public static final int RAD_FAR    = 200;
    }

    public class StatusCodes {
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_ERROR = 500;
    }
}


