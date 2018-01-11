package es.ibrands.popularmoviesstage1;

public class Trailer
{
    private final String id;
    private final String name;
    private final String key;

    public Trailer(
        String id,
        String name,
        String key
    ) {
        this.id = id;
        this.name = name;
        this.key = key;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getKey()
    {
        return key;
    }
}
