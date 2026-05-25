package habitTracker.persistence;

import habitTracker.business.AiInsightRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
@Slf4j
public class AiInsightDaoImpl extends MySQLDao implements AiInsightDao {

    public AiInsightDaoImpl(String databaseName) {
        super(databaseName);
    }

    public AiInsightDaoImpl(Connection conn) {
        super(conn);
    }

    public AiInsightDaoImpl() {
        super();
    }


    @Override
    public int saveInsight(AiInsightRecord insight) {

        int rowsAffected = 0;
        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ai_insights (username, insight_text, created_at) " +
                        "VALUES (?, ?, ?)")) {

            ps.setString(1, insight.getUsername());
            ps.setString(2, insight.getInsight_text());
            ps.setTimestamp(3, Timestamp.valueOf(insight.getCreated_at()));

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
    public AiInsightRecord getLatestInsightByUsername(String username) {

        AiInsightRecord insight = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String query = "SELECT * FROM ai_insights WHERE username = ? ORDER BY created_at DESC LIMIT 1";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                insight = mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Exception occurred in the getLatestInsightByUsername() method: " + e.getMessage());
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
                System.out.println("Exception occurred in the finally section of getLatestInsightByUsername(): " + e.getMessage());
            }
        }

        return insight;
    }


    @Override
    public int deleteInsightsByUsername(String username) {

        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            String query = "DELETE FROM ai_insights WHERE username = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, username);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the deleteInsightsByUsername() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of deleteInsightsByUsername(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }

    @Override
    public int markInsightsAsStale(String username) {
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            String query = "UPDATE ai_insights SET is_current = FALSE WHERE username = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in markInsightsAsStale(): " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of deleteInsightsByUsername(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }


    private AiInsightRecord mapRow(ResultSet rs) throws SQLException {

        AiInsightRecord i = new AiInsightRecord(
                rs.getInt("insight_id"),
                rs.getString("username"),
                rs.getString("insight_text"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_current")
        );

        return i;
    }

}
