package jason.tuchler.com.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    VolleyApi volleyApi;
    RecyclerView recyclerView;

    MoviesAdapter adapter;

    ArrayList<Movie> arrFavoriteMovies= new ArrayList<Movie>();
    ArrayList<Movie> arrMostPopular= new ArrayList<Movie>();
    ArrayList<Movie> arrTopRated= new ArrayList<Movie>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Popular Movies");

        recyclerView = ( RecyclerView ) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        getFromDB();

        volleyApi = VolleyApi.getInstance();
        volleyApi.getAllMovies(this, Constants.API_URL_POPULAR, new VolleyApi.MovieResultListener() {


            @Override
            public void onMoviesResult(List<Movie> result) {
                arrMostPopular = new ArrayList<Movie>();

                for (Movie movie : result) {

                    // check if the movie is favorite or not, if favorite then 1 else 0
                    for(Movie movie1 : arrFavoriteMovies){
                        if(movie1.title.equals(movie.title))  {
                            movie.isFavorite = "1";
                        }

                    }
                    arrMostPopular.add(movie);

                }
                adapter = new MoviesAdapter(arrMostPopular);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("title", (String) getTitle());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String title = String.valueOf(savedInstanceState.get("title"));

        if(title.equals("Popular Movies")){
            getFromDB();
            volleyApi = VolleyApi.getInstance();
            volleyApi.getAllMovies(this, Constants.API_URL_POPULAR, new VolleyApi.MovieResultListener() {

                @Override
                public void onMoviesResult(List<Movie> result) {
                    arrMostPopular = new ArrayList<Movie>();

                    for (Movie movie : result) {

                        // check if the movie is favorite or not, if favorite then 1 else 0
                        for(Movie movie1 : arrFavoriteMovies){
                            if(movie1.title.equals(movie.title))  {
                                movie.isFavorite = "1";
                            }
                        }
                        arrMostPopular.add(movie);
                    }
                    adapter = new MoviesAdapter(arrMostPopular);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        else if(title.equals("Top Rated Movies")){
            getFromDB();
            volleyApi = VolleyApi.getInstance();
            volleyApi.getAllMovies(this, Constants.API_URL_TOP_RATED, new VolleyApi.MovieResultListener() {

                @Override
                public void onMoviesResult(List<Movie> result) {

                    arrTopRated = new ArrayList<Movie>();

                    for (Movie movie : result) {

                        // check if the movie is favorite or not, if favorite then 1 else 0
                        for(Movie movie1 : arrFavoriteMovies){
                            if(movie1.title.equals(movie.title))  {
                                movie.isFavorite = "1";
                            }
                        }
                        arrTopRated.add(movie);

                    }
                    adapter = new MoviesAdapter(arrTopRated);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        else if(title.equals("Favorite Movies")){
            getFromDB();
            adapter = new MoviesAdapter(arrFavoriteMovies);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
// if view comes again update the values

        if(getTitle().equals("Popular Movies")){
            getFromDB();
            volleyApi = VolleyApi.getInstance();
            volleyApi.getAllMovies(this, Constants.API_URL_POPULAR, new VolleyApi.MovieResultListener() {

                @Override
                public void onMoviesResult(List<Movie> result) {
                    arrMostPopular = new ArrayList<Movie>();

                    for (Movie movie : result) {

                        // check if the movie is favorite or not, if favorite then 1 else 0
                        for(Movie movie1 : arrFavoriteMovies){
                            if(movie1.title.equals(movie.title))  {
                                movie.isFavorite = "1";
                            }
                        }
                        arrMostPopular.add(movie);
                    }
                    adapter = new MoviesAdapter(arrMostPopular);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        else if(getTitle().equals("Top Rated Movies")){
            getFromDB();
            volleyApi = VolleyApi.getInstance();
            volleyApi.getAllMovies(this, Constants.API_URL_TOP_RATED, new VolleyApi.MovieResultListener() {

                @Override
                public void onMoviesResult(List<Movie> result) {

                    arrTopRated = new ArrayList<Movie>();

                    for (Movie movie : result) {

                        // check if the movie is favorite or not, if favorite then 1 else 0
                        for(Movie movie1 : arrFavoriteMovies){
                            if(movie1.title.equals(movie.title))  {
                                movie.isFavorite = "1";
                            }
                        }
                        arrTopRated.add(movie);

                    }
                    adapter = new MoviesAdapter(arrTopRated);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        else if(getTitle().equals("Favorite Movies")){
            getFromDB();
            adapter = new MoviesAdapter(arrFavoriteMovies);
            recyclerView.setAdapter(adapter);
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
        private List<Movie> movies;

        MoviesAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_movie_recycler_view, parent, false);
            return new MoviesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MoviesViewHolder holder, int position) {
            Movie movie = movies.get(position);
            holder.bindTo(movie);
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView movieImage;
            Movie movie;

            public MoviesViewHolder(View itemView) {
                super(itemView);
                movieImage = ( ImageView ) itemView.findViewById(R.id.movieImage);
                movieImage.setOnClickListener(this);
            }

            public void bindTo(Movie movie) {
                Log.d("IMAGE_PATH: ", movie.getImagePath());
                this.movie = movie;
                Picasso.with(movieImage.getContext()).load(movie.imagePath).placeholder(R.drawable.loading).error(R.drawable.error).into(movieImage);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.movieImage:
                        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                        intent.putExtra(Constants.CURRENT_MOVIE, movie);
                        startActivity(intent);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                item.setChecked(true);
                setTitle("Popular Movies");
                getFromDB();

                volleyApi = VolleyApi.getInstance();
                volleyApi.getAllMovies(this, Constants.API_URL_POPULAR, new VolleyApi.MovieResultListener() {

                    @Override
                    public void onMoviesResult(List<Movie> result) {
                        arrMostPopular = new ArrayList<Movie>();

                        for (Movie movie : result) {

                            // check if the movie is favorite or not, if favorite then 1 else 0
                            for(Movie movie1 : arrFavoriteMovies){
                                if(movie1.title.equals(movie.title))  {
                                    movie.isFavorite = "1";
                                }
                            }
                            arrMostPopular.add(movie);

                        }
                        adapter = new MoviesAdapter(arrMostPopular);
                        recyclerView.setAdapter(adapter);
                    }
                });

                return true;

            case R.id.sort_by_top_rated:
                item.setChecked(true);
                setTitle("Top Rated Movies");
                getFromDB();
                volleyApi = VolleyApi.getInstance();
                volleyApi.getAllMovies(this, Constants.API_URL_TOP_RATED, new VolleyApi.MovieResultListener() {

                    @Override
                    public void onMoviesResult(List<Movie> result) {

                        arrTopRated = new ArrayList<Movie>();

                        for (Movie movie : result) {

                            // check if the movie is favorite or not, if favorite then 1 else 0
                            for(Movie movie1 : arrFavoriteMovies){
                                if(movie1.title.equals(movie.title))  {
                                    movie.isFavorite = "1";
                                }
                            }
                            arrTopRated.add(movie);
                        }
                        adapter = new MoviesAdapter(arrTopRated);
                        recyclerView.setAdapter(adapter);
                    }
                });

                return true;

            case R.id.favorite:
                item.setChecked(true);
                setTitle("Favorite Movies");

                getFromDB();
                adapter = new MoviesAdapter(arrFavoriteMovies);
                recyclerView.setAdapter(adapter);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getFromDB(){

        arrFavoriteMovies = new ArrayList<>();


        // Retrieve Movie records
        String URL = "content://jason.tuchler.com.popularmovies.MoviesContentProvider";

        Uri movies = Uri.parse(URL);
        Cursor c = managedQuery(movies, null, null, null, MoviesContentProvider.COLUMN_NAME_MOVIEID);


        if(c.moveToFirst()){
            do{
                // getting values
                String id = c.getString(c.getColumnIndex(MoviesContentProvider._ID));
                String imagePath = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_IMAGEPATH));
                String movieId = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_MOVIEID));
                String overview = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_OVERVIEW));
                String releaseDate = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_RELEASEDATE));
                String title = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_TITLE));
                String voteCount = c.getString(c.getColumnIndex(MoviesContentProvider.COLUMN_NAME_VOTECOUNT));


                Movie movieObject = new Movie();

                movieObject.imagePath = imagePath;
                movieObject.movieId = Integer.parseInt(movieId);
                movieObject.overview = overview;
                movieObject.releaseDate = releaseDate;
                movieObject.title = title;
                movieObject.voteCount = voteCount;
                movieObject.isFavorite = "1";

                arrFavoriteMovies.add(movieObject);

                //Do something Here with values

            }while(c.moveToNext());
        }






    }


}


