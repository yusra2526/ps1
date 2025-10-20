package twitter;

import java.time.Instant;
import java.util.Date;

public class Tweet {
    private final long id;
    private final String author;
    private final String text;
    private final Instant timestamp;
    
    // Constructor with Instant
    public Tweet(long id, String author, String text, Instant timestamp) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }
    
    // Alternative constructor with Date for backward compatibility
    public Tweet(long id, String author, String text, Date date) {
        this(id, author, text, date.toInstant());
    }
    
    public long getId() { return id; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
    public Instant getTimestamp() { return timestamp; }
}