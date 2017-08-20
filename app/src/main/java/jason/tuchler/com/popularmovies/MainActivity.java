package jason.tuchler.com.popularmovies;



import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    String apiUrlPopular = "http://api.themoviedb.org/3/movie/popular?api_key=" + Constants.API_KEY;
    String apiUrlTopRated = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + Constants.API_KEY;
    String apiUrlVideo = "https://api.themoviedb.org/3/movie/119450/videos?api_key=" + Constants.API_KEY + "&language=en-US";

    MovieDetails.MovieDatabaseContract.FavoriteMovieDBHelper mDbHelper = new MovieDetails.MovieDatabaseContract.FavoriteMovieDBHelper(this);
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
        volleyApi.getAllMovies(this, apiUrlPopular, new VolleyApi.MovieResultListener() {


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
    public void onRestart() {
        super.onRestart();
// if view comes again update the values

        if(getTitle().equals("Popular Movies")){
            getFromDB();
            volleyApi = VolleyApi.getInstance();
            volleyApi.getAllMovies(this, apiUrlPopular, new VolleyApi.MovieResultListener() {

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
            volleyApi.getAllMovies(this, apiUrlTopRated, new VolleyApi.MovieResultListener() {

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
                movieImage = ( ImageView ) itemView.findViewById(R.id.movie_image);
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
                    case R.id.movie_image:
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
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                item.setChecked(true);
                setTitle("Popular Movies");
                getFromDB();

                volleyApi = VolleyApi.getInstance();
                volleyApi.getAllMovies(this, apiUrlPopular, new VolleyApi.MovieResultListener() {

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
                volleyApi.getAllMovies(this, apiUrlTopRated, new VolleyApi.MovieResultListener() {

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

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MovieDetails.MovieDatabaseContract.FavoriteMovie.TABLE_NAME, null);
        if(c.moveToFirst()){
            do{
                //assing values
                String id = c.getString(0);
                String imagePath = c.getString(1);
                String movieId = c.getString(2);
                String overview = c.getString(3);
                String releaseDate = c.getString(4);
                String title = c.getString(5);
                String voteCount = c.getString(6);


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
        c.close();
        db.close();
    }


}


