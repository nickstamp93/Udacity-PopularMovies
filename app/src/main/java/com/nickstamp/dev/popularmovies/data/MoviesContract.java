package com.nickstamp.dev.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dev on 26/1/2017.
 */

public class MoviesContract {

    public static final String AUTHORITY = "com.nickstamp.dev.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_MOVIE_CACHE = "cache";



    public static final String _ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ORIGINAL_TITLE = "original_title";
    public static final String COLUMN_POSTER = "poster";
    public static final String COLUMN_BACKDROP = "backdrop";
    public static final String COLUMN_PLOT = "plot_synopsis";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_RELEASE_DATE = "release_date";

    public static final class CacheMovieEntry implements  BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_CACHE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MOVIE_CACHE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MOVIE_CACHE;

        public static final String TABLE_NAME = "cache";


        /*

                                            MOVIE CACHE
        -----------------------------------------------------------------------------------------------------------------------
        |   id   |   title   |  original_title  |   poster   |  backdrop  |   plot_synopsis   |   rating   |   release_date   |
        -----------------------------------------------------------------------------------------------------------------------
        |   1    |  Movie 1  |  original_title  |   http://  |  http://.  |       .....       |    6.5     |   21 Feb 2015    |
        -----------------------------------------------------------------------------------------------------------------------
        |   2    |  Movie 2  |  original_title  |   http://  |  http://.  |       .....       |    8.2     |   16 Apr 1999    |

        .

        .

        .

        -----------------------------------------------------------------------------------------------------------------------
        |   85   |   title   |  original_title  |   http://   |  http://.  |       .....      |     7.7    |    12 Dec 2005   |

        */

        public static Uri buildMovieCacheUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        /*

                                            FAVORITES
        -----------------------------------------------------------------------------------------------------------------------
        |   id   |   title   |  original_title  |   poster   |  backdrop  |   plot_synopsis   |   rating   |   release_date   |
        -----------------------------------------------------------------------------------------------------------------------
        |   1    |  Movie 1  |  original_title  |   local:// |  local://  |       .....       |    6.5     |   21 Feb 2015    |
        -----------------------------------------------------------------------------------------------------------------------
        |   2    |  Movie 2  |  original_title  |   local:// |  local://  |       .....       |    8.2     |   16 Apr 1999    |

        .

        .

        .

        -----------------------------------------------------------------------------------------------------------------------
        |   85   |   title   |  original_title  |   local:// |  local://  |       ....        |     7.7    |    12 Dec 2005   |

        */

        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
