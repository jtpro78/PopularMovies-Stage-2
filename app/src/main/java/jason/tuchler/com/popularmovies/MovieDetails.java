package jason.tuchler.com.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.android.volley.RequestQueue;


import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity {

    ArrayList<String> arrNames;
    ArrayList<String> arrKeys;

    ArrayList<String> arrReviews;
    ArrayList<String> arrNamesReview;

    ListView listView;
    Movie movie;
    AdapterTrailers adapter;

    ListView listViewReview;
    AdapterReviews adapterReview;

    // this check is for favorite image if it is 0 then movie is not favorite and if 1 then movie is favorite
    int check = 0;

    MovieDatabaseContract.FavoriteMovieDBHelper mDbHelper = new MovieDatabaseContract.FavoriteMovieDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details2);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigateBack();
        getMovie();

        arrNames = new ArrayList<String>();
        arrKeys = new ArrayList<String>();

        arrReviews = new ArrayList<String>();
        arrNamesReview = new ArrayList<String>();

        // reviews listview
        listViewReview = (ListView) findViewById(R.id.list_view_reviews);

        adapterReview = new AdapterReviews(arrReviews, arrNamesReview ,this);

        listViewReview.setAdapter(adapterReview);

        // trailers listview
        listView = (ListView) findViewById(R.id.list_view);

        adapter = new AdapterTrailers(arrNames,this);

        listView.setAdapter(adapter);


        // listview when clicked comes here, we show a alert

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {

                CharSequence selection[] = new CharSequence[] {"Click to Open Preview"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetails.this);
                builder.setTitle("Select option");
                builder.setItems(selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if(which == 0){
                            // open in browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + String.valueOf(arrKeys.get(position))));
                            startActivity(browserIntent);
                        }
//                        else if (which == 1){
//                            // open in apps webview
//
//                            SharedPreferences.Editor editor = getSharedPreferences("url", MODE_PRIVATE).edit();
//                            editor.putString("url", "https://www.youtube.com/watch?v=" + String.valueOf(arrKeys.get(position)));
//                            editor.commit();
//
//                            Intent intent  = new Intent(MovieDetails.this, WebBrowser.class);
//                            startActivity(intent);
//                        }
                    }
                });
                builder.show();
            }
        });

// get  trailers
        networkCallTrailers();
// get  reviews
        networkCallReviews();
    }

    private void navigateBack() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMovie() {
        Intent intent = getIntent();
        if(intent.hasExtra(Constants.CURRENT_MOVIE)) {
            movie = ( Movie ) intent.getSerializableExtra(Constants.CURRENT_MOVIE);
            populateContent(movie);
        }
    }

    private void populateContent(Movie movie) {
        String Count = getString(R.string.Vote_Count);

        TextView movieTitle = (TextView)findViewById(R.id.movie_detail_title);
        TextView overView = (TextView)findViewById(R.id.movie_description);
        TextView releaseDate = (TextView)findViewById(R.id.release_date);
        TextView movieRating = (TextView)findViewById(R.id.movie_rating);
        ImageView poster = (ImageView)findViewById(R.id.movie_detail_poster);
        ImageView favorite = (ImageView)findViewById(R.id.img_favorite);

        Picasso.with(this).load(movie.imagePath).into(poster);
        movieTitle.setText(movie.getTitle());
        overView.setText(movie.getOverview());
        releaseDate.setText(movie.getReleaseDate());
        movieRating.setText(movie.getVoteCount() + Count);


        // check for favorite star if it is already a favorite movie get its image
        if (movie.isFavorite != null){
            if(movie.isFavorite.equals("1")){
                favorite .setImageResource(R.drawable.favorite_checked);
                // this check is for favorite image if it is 0 then movie is not favorite and if 1 then movie is favorite
                check = 1;
            }}
    }

    public void networkCallTrailers(){

        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://api.themoviedb.org/3/movie/"+ String.valueOf(movie.movieId) +"/videos?api_key=" + Constants.API_KEY + "&language=en-US";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try {
                            JSONArray arrayResuts = response.getJSONArray("results");
                            for(int i = 0; i < arrayResuts.length(); i++){
                                JSONObject userData = (JSONObject) arrayResuts.get(i);
                                String name = (String) userData.get("name");
                                String key = (String) userData.get("key");
                                arrNames.add(name);
                                arrKeys.add(key);
                            }

                            addTrailersToListview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // JSONObject userData = (JSONObject) arrayResuts.get(0); 
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("Error.Response", response);
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);

    }

    public void addTrailersToListview(){

        // update Trailer listview values
        adapter.notifyDataSetChanged();
    }

    public void networkCallReviews(){

        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://api.themoviedb.org/3/movie/"+ String.valueOf(movie.movieId) +"/reviews?api_key=" + Constants.API_KEY + "&language=en-US&page=1";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try {
                            JSONArray arrayResults = response.getJSONArray("results");
                            for(int i = 0; i < arrayResults.length(); i++){
                                JSONObject userData = (JSONObject) arrayResults.get(i);
                                String review = (String) userData.get("content");
                                String name = (String) userData.get("author");

                                arrReviews.add(review);
                                arrNamesReview.add(name);
                            }

                            addReviewsToListview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // JSONObject userData = (JSONObject) arrayResuts.get(0); 
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("Error.Response", response);
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void addReviewsToListview(){
        // update Review listview values
        adapterReview.notifyDataSetChanged();
    }

    // Button Favorite tapped, now change image and save movie in database.
    public void btnFavoriteTapped(View view) {

        ImageView imageFavorite = (ImageView) findViewById(R.id.img_favorite);

        // mark as favorite and store value in database
        if (check == 0) {
            imageFavorite.setImageResource(R.drawable.favorite_checked);
            Toast.makeText(this , "Movie added to Favorites",
                    Toast.LENGTH_LONG).show();

            check = 1;
            storeInDB();
        }
        else  if (check == 1) {
            imageFavorite.setImageResource(R.drawable.favorite_uncheck);
            Toast.makeText(this , "Movie removed from Favorites",
                    Toast.LENGTH_LONG).show();
            check = 0;
            deleteFromDb();
        }
    }

    public void storeInDB(){

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // db.delete(MovieDatabaseContract.FavoriteMovie.TABLE_NAME, null,null);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_IMAGEPATH, movie.imagePath);
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_MOVIEID, movie.movieId);
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_OVERVIEW, movie.overview);
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_RELEASEDATE, movie.releaseDate);
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_TITLE, movie.title);
        values.put(MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_VOTECOUNT, movie.voteCount);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(MovieDatabaseContract.FavoriteMovie.TABLE_NAME, null, values);

    }

    public void getFromDB(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MovieDatabaseContract.FavoriteMovie.TABLE_NAME, null);
        if(c.moveToFirst()){
            do{
                // getting values
                String id = c.getString(0);
                String imagePath = c.getString(1);
                String movieId = c.getString(2);
                String overview = c.getString(3);
                String releaseDate = c.getString(4);
                String title = c.getString(5);
                String voteCount = c.getString(6);
                String nullValue = c.getString(7);

                //Do something Here with values

            }while(c.moveToNext());
        }
        c.close();
        db.close();
    }

    public void deleteFromDb()
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(MovieDatabaseContract.FavoriteMovie.TABLE_NAME, MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_MOVIEID + "=" + movie.movieId, null) ;
    }

    public static final class MovieDatabaseContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private MovieDatabaseContract() {}

        /* Inner class that defines the table contents */
        public class FavoriteMovie implements BaseColumns {
            public static final String TABLE_NAME = "Movie";
            public static final String COLUMN_NAME_IMAGEPATH = "imagePath";
            public static final String COLUMN_NAME_MOVIEID = "movieId";

            public static final String COLUMN_NAME_OVERVIEW = "overview";
            public static final String COLUMN_NAME_RELEASEDATE = "releaseDate";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_VOTECOUNT = "voteCount";

            public static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + MovieDetails.MovieDatabaseContract.FavoriteMovie.TABLE_NAME + " (" +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie._ID + " INTEGER PRIMARY KEY," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_IMAGEPATH + " TEXT," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_MOVIEID + " TEXT," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_OVERVIEW + " TEXT," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_RELEASEDATE + " TEXT," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_TITLE + " TEXT," +
                            MovieDetails.MovieDatabaseContract.FavoriteMovie.COLUMN_NAME_VOTECOUNT + " TEXT)";

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + MovieDetails.MovieDatabaseContract.FavoriteMovie.TABLE_NAME;
        }

        public static class FavoriteMovieDBHelper extends SQLiteOpenHelper {

            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "Movie.db";

            public FavoriteMovieDBHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(FavoriteMovie.SQL_CREATE_ENTRIES);
            }
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // This database is only a cache for online data, so its upgrade policy is
                // to simply to discard the data and start over
                db.execSQL(FavoriteMovie.SQL_DELETE_ENTRIES);
                onCreate(db);
            }
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onUpgrade(db, oldVersion, newVersion);
            }
        }
    }
}




