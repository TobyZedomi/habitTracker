package habitTracker.persistence;

import habitTracker.business.AiInsightRecord;

public interface AiInsightDao {

    public int saveInsight(AiInsightRecord insight);

    public AiInsightRecord getLatestInsightByUsername(String username);

    public int deleteInsightsByUsername(String username);

     public int markInsightsAsStale(String username);

}
