package jason.tuchler.com.popularmovies;

public class Constants {
    public static final String IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/";
    public static final String CURRENT_MOVIE = "jason.tuchler.com.CURRENT_MOVIE";
    public static final String MOVIE_SIZE = "w185";
    public static final String API_KEY="e54b52b33ce13626cf87ca053b7aa19a"; // Enter themoviedb.org API key here.
    public static final String MOVIE_URL_PREFIX = "https://api.themoviedb.org/3/movie/";
    public static final String API_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    public static final String API_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    /* Sample movie preview video url: https://api.themoviedb.org/3/movie/119450/videos?api_key=e54b52b33ce13626cf87ca053b7aa19a&language=en-US
    id and key needed */

    /* Sample reviews url: https://api.themoviedb.org/3/movie/119450/reviews?api_key=e54b52b33ce13626cf87ca053b7aa19a&language=en-US&page=1
     author and content needed */
}
