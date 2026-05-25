package habitTracker.persistence;

import habitTracker.business.AiConversation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
@Slf4j
public class AiConversationDaoImpl extends MySQLDao implements AiConversationDao {

    public AiConversationDaoImpl(String databaseName) {
        super(databaseName);
    }

    public AiConversationDaoImpl(Connection conn) {
        super(conn);
    }

    public AiConversationDaoImpl() {
        super();
    }


    @Override
    public int saveConversation(AiConversation conversation) {

        int rowsAffected = 0;
        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ai_conversations (username, conversation_uuid, s3_key, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?)")) {

            ps.setString(1, conversation.getUsername());
            ps.setString(2, conversation.getConversation_uuid());
            ps.setString(3, conversation.getS3_key());
            ps.setTimestamp(4, Timestamp.valueOf(conversation.getCreated_at()));
            ps.setTimestamp(5, Timestamp.valueOf(conversation.getUpdated_at()));

            rowsAffected = ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            rowsAffected = -1;
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return rowsAffected;
    }


    @Override
    public int updateConversation(String conversationUuid, String s3Key) {

        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            String query = "UPDATE ai_conversations SET s3_key = ?, updated_at = ? WHERE conversation_uuid = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, s3Key);
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(3, conversationUuid);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the updateConversation() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of updateConversation(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }


    @Override
    public AiConversation getConversationByUuid(String conversationUuid) {

        AiConversation conversation = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String query = "SELECT * FROM ai_conversations WHERE conversation_uuid = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, conversationUuid);
            rs = ps.executeQuery();

            if (rs.next()) {
                conversation = mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Exception occurred in the getConversationByUuid() method: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of getConversationByUuid(): " + e.getMessage());
            }
        }

        return conversation;
    }


    @Override
    public ArrayList<AiConversation> getAllConversationsByUsername(String username) {

        ArrayList<AiConversation> conversations = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String query = "SELECT * FROM ai_conversations WHERE username = ? ORDER BY updated_at DESC";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                AiConversation c = mapRow(rs);
                conversations.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Exception occurred in the getAllConversationsByUsername() method: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of getAllConversationsByUsername(): " + e.getMessage());
            }
        }

        return conversations;
    }


    @Override
    public int deleteConversation(String conversationUuid) {

        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            String query = "DELETE FROM ai_conversations WHERE conversation_uuid = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, conversationUuid);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the deleteConversation() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of deleteConversation(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }


    private AiConversation mapRow(ResultSet rs) throws SQLException {

        AiConversation c = new AiConversation(
                rs.getInt("conversation_id"),
                rs.getString("username"),
                rs.getString("conversation_uuid"),
                rs.getString("s3_key"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );

        return c;
    }

}