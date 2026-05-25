package habitTracker.persistence;

import habitTracker.business.AiRecommendationRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
@Slf4j
public class AiRecommendationDaoImpl extends MySQLDao implements AiRecommendationDao {

    public AiRecommendationDaoImpl(String databaseName) {
        super(databaseName);
    }

    public AiRecommendationDaoImpl(Connection conn) {
        super(conn);
    }

    public AiRecommendationDaoImpl() {
        super();
    }

    @Override
    public int saveRecommendation(AiRecommendationRecord recommendation) {
        int rowsAffected = 0;
        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ai_recommendations (username, type, title, author, channel, description, url, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, recommendation.getUsername());
            ps.setString(2, recommendation.getType());
            ps.setString(3, recommendation.getTitle());
            ps.setString(4, recommendation.getAuthor());
            ps.setString(5, recommendation.getChannel());
            ps.setString(6, recommendation.getDescription());
            ps.setString(7, recommendation.getUrl());
            ps.setTimestamp(8, Timestamp.valueOf(recommendation.getCreated_at()));

            rowsAffected = ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            rowsAffected = -1;
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }

        return rowsAffected;
    }

    @Override
    public ArrayList<AiRecommendationRecord> getRecommendationsByUsername(String username) {
        ArrayList<AiRecommendationRecord> recommendations = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT * FROM ai_recommendations WHERE username = ? ORDER BY created_at DESC";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                recommendations.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Exception occurred in the getRecommendationsByUsername() method: " + e.getMessage());
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
                System.out.println("Exception occurred in the finally section of getRecommendationsByUsername(): " + e.getMessage());
            }
        }

        return recommendations;
    }

    @Override
    public ArrayList<AiRecommendationRecord> getLatestRecommendationsByUsername(String username) {
        ArrayList<AiRecommendationRecord> recommendations = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT * FROM ai_recommendations WHERE username = ? ORDER BY created_at DESC LIMIT 4";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                recommendations.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Exception occurred in the getLatestRecommendationsByUsername() method: " + e.getMessage());
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
                System.out.println("Exception occurred in the finally section of getLatestRecommendationsByUsername(): " + e.getMessage());
            }
        }

        return recommendations;
    }

    @Override
    public int deleteRecommendationsByUsername(String username) {
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            String query = "DELETE FROM ai_recommendations WHERE username = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the deleteRecommendationsByUsername() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of deleteRecommendationsByUsername(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }

    @Override
    public int markRecommendationsAsStale(String username) {
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            String query = "UPDATE ai_recommendations SET is_current = FALSE WHERE username = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in markRecommendationsAsStale(): " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception in finally of markRecommendationsAsStale(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }

    private AiRecommendationRecord mapRow(ResultSet rs) throws SQLException {
        AiRecommendationRecord r = new AiRecommendationRecord();
        r.setRecommendation_id(rs.getInt("recommendation_id"));
        r.setUsername(rs.getString("username"));
        r.setType(rs.getString("type"));
        r.setTitle(rs.getString("title"));
        r.setAuthor(rs.getString("author"));
        r.setChannel(rs.getString("channel"));
        r.setDescription(rs.getString("description"));
        r.setUrl(rs.getString("url"));
        r.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        r.set_current(rs.getBoolean("is_current"));
        return r;
    }
}