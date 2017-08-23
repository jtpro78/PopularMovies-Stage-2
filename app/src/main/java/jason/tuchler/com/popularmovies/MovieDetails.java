package jason.tuchler.com.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

    CoordinatorLayout coordinatorLayout;

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
        listViewReview = (ListView) findViewById(R.id.listViewReviews);

        adapterReview = new AdapterReviews(arrReviews, arrNamesReview ,this);

        listViewReview.setAdapter(adapterReview);

        // trailers listview
        listView = (ListView) findViewById(R.id.listView);

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
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/watch?v=" + String.valueOf(arrKeys.get(position))));
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

    // restoring state

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("check", check);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        check = savedInstanceState.getInt("check");


        ImageView imageFavorite = (ImageView) findViewById(R.id.imgFavorite);

        if (check == 0){

            imageFavorite.setImageResource(R.drawable.favorite_uncheck);

        }

        else if(check == 1){
            imageFavorite.setImageResource(R.drawable.favorite_checked);
        }
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

        TextView movieTitle = (TextView)findViewById(R.id.movieDetailTitle);
        TextView overView = (TextView)findViewById(R.id.movieDescription);
        TextView releaseDate = (TextView)findViewById(R.id.releaseDate);
        TextView movieRating = (TextView)findViewById(R.id.movieRating);
        ImageView poster = (ImageView)findViewById(R.id.movieDetailPoster);
        ImageView favorite = (ImageView)findViewById(R.id.imgFavorite);

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

        final String url = Constants.MOVIE_URL_PREFIX + String.valueOf(movie.movieId) + "/videos?api_key=" +
                Constants.API_KEY + "&language=en-US";

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

        final String url = Constants.MOVIE_URL_PREFIX + String.valueOf(movie.movieId) + "/reviews?api_key=" +
                Constants.API_KEY + "&language=en-US&page=1";

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

        ImageView imageFavorite = (ImageView) findViewById(R.id.imgFavorite);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // mark as favorite and store value in database
        if (check == 0) {
            imageFavorite.setImageResource(R.drawable.favorite_checked);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Movie added to Favorites", Snackbar.LENGTH_LONG);

            snackbar.show();

            check = 1;
            storeInDB();
        }
        else  if (check == 1) {
            imageFavorite.setImageResource(R.drawable.favorite_uncheck);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Movie removed Favorites", Snackbar.LENGTH_LONG);

            snackbar.show();
            check = 0;
            deleteFromDb();
        }
    }

    public void storeInDB(){

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MoviesContentProvider.COLUMN_NAME_IMAGEPATH, movie.imagePath);
        values.put(MoviesContentProvider.COLUMN_NAME_MOVIEID, movie.movieId);
        values.put(MoviesContentProvider.COLUMN_NAME_OVERVIEW, movie.overview);
        values.put(MoviesContentProvider.COLUMN_NAME_RELEASEDATE, movie.releaseDate);
        values.put(MoviesContentProvider.COLUMN_NAME_TITLE, movie.title);
        values.put(MoviesContentProvider.COLUMN_NAME_VOTECOUNT, movie.voteCount);

        // Insert the new row, returning the primary key value of the new row
        Uri uri = getContentResolver().insert(
                MoviesContentProvider.CONTENT_URI, values);
    }

    public void deleteFromDb()
    {
        String selection = MoviesContentProvider.COLUMN_NAME_MOVIEID + "=" + movie.movieId;
        int uri = getContentResolver().delete(MoviesContentProvider.CONTENT_URI, selection, null);
    }
}




