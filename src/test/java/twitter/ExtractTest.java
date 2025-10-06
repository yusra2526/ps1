package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy for getTimespan:
     *  - number of tweets: 0, 1, >1
     *  - same timestamp vs different timestamps
     * 
     * Testing strategy for getMentionedUsers:
     *  - no mentions
     *  - one mention
     *  - multiple mentions
     *  - duplicate mentions
     *  - mentions with punctuation
     *  - case-insensitive usernames
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "@Alyssa @bob let's meet!", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // ----- getTimespan() -----
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));
        assertEquals("start = end for single tweet", d1, timespan.getStart());
        assertEquals("start = end for single tweet", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleTweetsDifferentTimes() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        assertEquals("expected earliest start", d1, timespan.getStart());
        assertEquals("expected latest end", d3, timespan.getEnd());
    }

    // ----- getMentionedUsers() -----
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue("expected user alyssa", mentionedUsers.contains("alyssa"));
    }

    @Test
    public void testGetMentionedUsersMultipleTweetsDuplicateMentions() {
        Tweet t1 = new Tweet(4, "x", "@bob good morning", d1);
        Tweet t2 = new Tweet(5, "y", "hello @Bob!", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        assertEquals("expected one unique mention", 1, mentionedUsers.size());
        assertTrue("expected lowercase username", mentionedUsers.contains("bob"));
    }

    @Test
    public void testGetMentionedUsersWithPunctuation() {
        Tweet t = new Tweet(6, "x", "Thanks, @Alice!", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("expected alice", mentionedUsers.contains("alice"));
    }
}
