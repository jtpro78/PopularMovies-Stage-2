package jason.tuchler.com.popularmovies;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.LayoutInflater;
import com.squareup.picasso.Picasso;


import java.util.List;


public class MainActivity extends AppCompatActivity {

    VolleyApi volleyApi;
    RecyclerView recyclerView;

    MoviesAdapter adapter;
    String apiUrlPopular = "http://api.themoviedb.org/3/movie/popular?" + Constants.API_KEY;
    String apiUrlTopRated = "http://api.themoviedb.org/3/movie/top_rated?" + Constants.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = ( RecyclerView ) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        volleyApi = VolleyApi.getInstance();
        volleyApi.getAllMovies(this, apiUrlPopular, new VolleyApi.MovieResultListener() {

            @Override
            public void onMoviesResult(List<Movie> result) {
                for (Movie movie : result) {
                    adapter = new MoviesAdapter(result);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
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
                Picasso.with(movieImage.getContext()).load(movie.imagePath).into(movieImage);
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
                volleyApi = VolleyApi.getInstance();
                volleyApi.getAllMovies(this, apiUrlPopular, new VolleyApi.MovieResultListener() {

                    @Override
                    public void onMoviesResult(List<Movie> result) {
                        for (Movie movie : result) {
                            adapter = new MoviesAdapter(result);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });

                return true;

            case R.id.sort_by_top_rated:
                item.setChecked(true);
                volleyApi = VolleyApi.getInstance();
                volleyApi.getAllMovies(this, apiUrlTopRated, new VolleyApi.MovieResultListener() {

                    @Override
                    public void onMoviesResult(List<Movie> result) {
                        for (Movie movie : result) {
                            adapter = new MoviesAdapter(result);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    }

