package es.ibrands.popularmoviesstage1;

public class Movie
{
    private final String averageScore;
    private final String id;
    private final String title;
    private final String overview;
    private final String posterPath; // drawable reference id
    private final String releaseAt;
    private String runtime = "0";

    public Movie(
        String averageScore,
        String id,
        String title,
        String overview,
        String posterPath,
        String releaseAt
    ) {
        this.averageScore = averageScore;
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseAt = releaseAt;
    }

    public String getAverageScore()
    {
        return averageScore;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getOverview()
    {
        return overview;
    }

    public String getPosterPath()
    {
        return posterPath;
    }

    public String getReleaseAt()
    {
        return releaseAt;
    }

    public void setRuntime(String runtime) { this.runtime = runtime; }
    public String getRuntime() { return runtime; }
}