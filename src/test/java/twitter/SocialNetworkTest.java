package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy for guessFollowsGraph:
     *  - no tweets
     *  - single tweet with one mention
     *  - multiple tweets with overlapping mentions
     *  - case-insensitive usernames
     * 
     * Testing strategy for influencers:
     *  - empty graph
     *  - single influencer
     *  - multiple influencers ordered by count
     */

    private static final Instant t = Instant.parse("2016-02-17T10:00:00Z");

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // ----- guessFollowsGraph() -----
    @Test
    public void testGuessFollowsGraphEmptyList() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet tweet = new Tweet(1, "alice", "Hi @bob", t);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet));
        assertTrue(followsGraph.get("alice").contains("bob"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentionsAndUsers() {
        Tweet t1 = new Tweet(1, "alice", "@bob @carol check this out", t);
        Tweet t2 = new Tweet(2, "bob", "hello @alice", t);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(t1, t2));
        assertTrue(followsGraph.get("alice").containsAll(Arrays.asList("bob", "carol")));
        assertTrue(followsGraph.get("bob").contains("alice"));
    }

    // ----- influencers() -----
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>(Arrays.asList("bob")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("bob should be top influencer", "bob", influencers.get(0));
    }

    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("a", new HashSet<>(Arrays.asList("x", "y")));
        followsGraph.put("b", new HashSet<>(Arrays.asList("x")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("x should be first influencer", "x", influencers.get(0));
    }
}
