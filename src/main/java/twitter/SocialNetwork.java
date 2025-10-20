package twitter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SocialNetwork {

    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            // Process each tweet individually to get mentions
            Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.singletonList(tweet))
                .stream()
                .map(String::toLowerCase)
                .filter(user -> !user.equals(author)) // Filter out self-mentions
                .filter(user -> !user.equals("mentions")) // Filter out the "mentions" keyword
                .collect(Collectors.toSet());

            // Initialize the author's follow set if not present
            followsGraph.putIfAbsent(author, new HashSet<>());

            // Only add valid mentions
            followsGraph.get(author).addAll(mentionedUsers);
        }

        return followsGraph;
    }

    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            Set<String> followedUsers = entry.getValue();
            
            for (String followed : followedUsers) {
                followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
            }
        }

        return followerCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}