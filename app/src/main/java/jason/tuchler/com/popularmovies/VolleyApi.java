package jason.tuchler.com.popularmovies;


import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class VolleyApi {
    private static VolleyApi volleyapi;
    RequestQueue requestQueue;


    public void getAllMovies(Context context, String url, final MovieResultListener listener) {
        requestQueue = Volley.newRequestQueue(context);
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray jsonArray = responseObject.getJSONArray("results");
                    List<Movie> movies = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Movie movie = new Movie();
                        JSONObject movieObject = jsonArray.getJSONObject(i);

                        if (movieObject.has("poster_path")) {
                            String posterPath = movieObject.getString("poster_path");
                            Log.d("CURRENT MOVIE IMAGE ", posterPath);
                            movie.setImagePath(Constants.IMAGE_URL_PREFIX + Constants.MOVIE_SIZE + posterPath);
                            Log.d("IMAGEPATH ", movie.imagePath);
                        }

                        if (movieObject.has("title")) {
                            String title = movieObject.getString("title");
                            movie.setTitle(title);
                            Log.d("CURRENT TITLE ", title);
                        }

                        if (movieObject.has("vote_average")) {
                            String voteAverage = movieObject.getString("vote_average");
                            movie.setVoteCount(voteAverage);
                            Log.d("CURRENT VOTE AVERAGE ", String.valueOf(voteAverage));
                        }

                        if (movieObject.has("overview")) {
                            String overview = movieObject.getString("overview");
                            movie.setOverview(overview);
                            Log.d("OVERVIEW ", String.valueOf(overview));
                        }

                        if (movieObject.has("release_date")) {
                            String releaseDate = movieObject.getString("release_date");
                            movie.setReleaseDate(releaseDate);
                            Log.d("RELEASE_DATE ", String.valueOf(releaseDate));
                        }

                        if (movieObject.has("id")) {
                            int movieId = movieObject.getInt("id");
                            movie.setMovieId(movieId);
                            Log.d("ID ", String.valueOf(movieId));
                        }

                        if (movieObject.has("key")) {
                            String movieVideo = movieObject.getString("key");
                            movie.setMovieVideo(movieVideo);
                            Log.d("VIDEOS", String.valueOf(movieVideo));
                        }

                        movies.add(movie);
                    }
                    listener.onMoviesResult(movies);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        });

        requestQueue.add(request);
    }

    public static VolleyApi getInstance() {
        if (volleyapi == null) {
            synchronized (VolleyApi.class) {
                if (volleyapi == null) {
                    volleyapi = new VolleyApi();
                }
            }
        }
        return volleyapi;
    }

    public interface MovieResultListener {
        void onMoviesResult(List<Movie> result);
    }
}
