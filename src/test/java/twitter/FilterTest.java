package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy for writtenBy:
     *  - author matches one, multiple, or no tweets
     * 
     * Testing strategy for inTimespan:
     *  - all tweets inside, none inside, some inside
     * 
     * Testing strategy for containing:
     *  - no words match
     *  - one word matches multiple tweets
     *  - case-insensitive match
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "Lunch time soon?", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // ----- writtenBy() -----
    @Test
    public void testWrittenBySingleMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        assertEquals("expected one tweet", 1, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByMultipleMatches() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        assertEquals("expected 2 tweets", 2, writtenBy.size());
    }

    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nonexistent");
        assertTrue("expected empty list", writtenBy.isEmpty());
    }

    // ----- inTimespan() -----
    @Test
    public void testInTimespanAllTweetsInside() {
        Timespan span = new Timespan(d1, d3);
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), span);
        assertEquals("expected all tweets", 3, inTimespan.size());
    }

    @Test
    public void testInTimespanNoneInside() {
        Timespan span = new Timespan(Instant.parse("2016-02-16T00:00:00Z"), Instant.parse("2016-02-16T23:59:59Z"));
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), span);
        assertTrue("expected no tweets", inTimespan.isEmpty());
    }

    // ----- containing() -----
    @Test
    public void testContainingOneWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertEquals("expected both tweets contain 'talk'", 2, containing.size());
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("RIVEST"));
        assertEquals("expected both tweets contain rivest", 2, containing.size());
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("pizza"));
        assertTrue("expected empty list", containing.isEmpty());
    }
}
