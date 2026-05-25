package habitTracker.persistence;

import habitTracker.business.AiConversation;

import java.util.ArrayList;
public interface AiConversationDao {
    public int saveConversation(AiConversation conversation);

    public int updateConversation(String conversationUuid, String s3Key);

    public AiConversation getConversationByUuid(String conversationUuid);

    public ArrayList<AiConversation> getAllConversationsByUsername(String username);

    public int deleteConversation(String conversationUuid);
}
