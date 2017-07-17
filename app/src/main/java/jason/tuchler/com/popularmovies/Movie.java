package jason.tuchler.com.popularmovies;


import java.io.Serializable;

public class Movie implements Serializable {

    String id;
    String title;
    String imagePath;
    String overview;
    String release_date;
    String voteCount;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", voteCount='" + voteCount + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date +
                '}';
    }
}
