package twitter;

import java.util.*;
import java.util.stream.Collectors;

public class SocialNetwork {

    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.singletonList(tweet));

            followsGraph.putIfAbsent(author, new HashSet<>());

            for (String mentionedUser : mentionedUsers) {
                if (!mentionedUser.equals(author)) {
                    followsGraph.get(author).add(mentionedUser);
                }
            }
        }

        return followsGraph;
    }

    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        for (String user : followsGraph.keySet()) {
            for (String followed : followsGraph.get(user)) {
                followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
            }
        }

        return followerCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
