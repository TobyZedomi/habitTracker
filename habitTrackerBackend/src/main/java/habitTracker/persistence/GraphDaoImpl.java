package habitTracker.persistence;

import habitTracker.dto.GraphDataPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GraphDaoImpl extends MySQLDao implements GraphDao {

    public GraphDaoImpl(String databaseName) {
        super(databaseName);
    }

    public GraphDaoImpl(Connection conn) {
        super(conn);
    }

    public GraphDaoImpl() {
        super();
    }

    @Override
    public List<GraphDataPoint> getDailyCompletionsLast30Days(String username) {
        List<GraphDataPoint> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String query =
                    "SELECT DATE_FORMAT(htl.date_of_activity, '%Y-%m-%d') AS label, COUNT(*) AS value " +
                            "FROM habit_tracker_log htl " +
                            "JOIN habits h ON h.habit_id = htl.habit_id " +
                            "WHERE h.username = ? AND htl.date_of_activity >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                            "GROUP BY DATE_FORMAT(htl.date_of_activity, '%Y-%m-%d') " +
                            "ORDER BY label ASC";

            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new GraphDataPoint(rs.getString("label"), rs.getInt("value")));
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception in getDailyCompletionsLast30Days: " + e.getMessage());
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
                System.out.println("Exception in finally of getDailyCompletionsLast30Days: " + e.getMessage());
            }
        }

        return list;
    }

    @Override
    public List<GraphDataPoint> getWeeklyCompletionsLast12Weeks(String username) {
        List<GraphDataPoint> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String query =
                    "SELECT CONCAT(YEAR(htl.date_of_activity), '-W', LPAD(WEEK(htl.date_of_activity, 1), 2, '0')) AS label, COUNT(*) AS value " +
                            "FROM habit_tracker_log htl " +
                            "JOIN habits h ON h.habit_id = htl.habit_id " +
                            "WHERE h.username = ? AND htl.date_of_activity >= DATE_SUB(CURDATE(), INTERVAL 84 DAY) " +
                            "GROUP BY label " +
                            "ORDER BY label ASC";

            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new GraphDataPoint(rs.getString("label"), rs.getInt("value")));
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception in getWeeklyCompletionsLast12Weeks: " + e.getMessage());
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
                System.out.println("Exception in finally of getWeeklyCompletionsLast12Weeks: " + e.getMessage());
            }
        }

        return list;
    }

}
