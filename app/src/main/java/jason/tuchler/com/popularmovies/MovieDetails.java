package jason.tuchler.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details2);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigateBack();
        getMovie();
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
            Movie movie = ( Movie ) intent.getSerializableExtra(Constants.CURRENT_MOVIE);
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

        Picasso.with(this).load(movie.imagePath).into(poster);
        movieTitle.setText(movie.getTitle());
        overView.setText(movie.getOverview());
        releaseDate.setText(movie.getRelease_date());
        movieRating.setText(movie.getVoteCount() + Count);

    }
}
