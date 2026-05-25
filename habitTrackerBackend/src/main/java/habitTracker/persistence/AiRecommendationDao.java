package habitTracker.persistence;

import habitTracker.business.AiRecommendationRecord;

import java.util.ArrayList;

public interface AiRecommendationDao {

    public int saveRecommendation(AiRecommendationRecord recommendation);

    public ArrayList<AiRecommendationRecord> getRecommendationsByUsername(String username);

    public ArrayList<AiRecommendationRecord> getLatestRecommendationsByUsername(String username);

    public int deleteRecommendationsByUsername(String username);

    int markRecommendationsAsStale(String username);

}
