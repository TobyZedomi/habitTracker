package habitTracker.persistence;

import habitTracker.business.Habit;
import habitTracker.business.HabitTrackerLog;
import lombok.extern.slf4j.Slf4j;

import habitTracker.business.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


@Repository
@Slf4j
public class HabitTrackerLogDaoImpl extends MySQLDao implements HabitTrackerLogDao {

    /**
     * get the database information from a particular database
     * @param databaseName is the database being searched
     */
    public HabitTrackerLogDaoImpl(String databaseName){
        super(databaseName);
    }

    public HabitTrackerLogDaoImpl(Connection conn){
        super(conn);
    }
    public HabitTrackerLogDaoImpl(){
        super();
    }



    // getting all habits logged for one habit

    @Override
    public ArrayList<HabitTrackerLog> getAllHabitsByHabitId(int id){

        ArrayList<HabitTrackerLog> habitTrackerLogs = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM habit_tracker_log where habit_id = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setInt(1, id);
            rs = ps.executeQuery();

            while(rs.next()){
                HabitTrackerLog h = mapRow(rs);
                habitTrackerLogs.add(h);

            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
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
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return habitTrackerLogs;
    }


    // getting all habits by username

    @Override
    public ArrayList<HabitTrackerLog> getAllHabitsByUsername(String username){

        ArrayList<HabitTrackerLog> habitTrackerLogs = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT habit_tracker_log.* FROM habit_tracker_log JOIN habits ON habit_tracker_log.habit_id = habits.habit_id WHERE habits.username = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            while(rs.next()){
                HabitTrackerLog h = mapRow(rs);
                habitTrackerLogs.add(h);

            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
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
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return habitTrackerLogs;
    }


    @Override
    public ArrayList<HabitTrackerLog> getAllHabitsByUsernameLimitBy10(String username){

        ArrayList<HabitTrackerLog> habitTrackerLogs = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT habit_tracker_log.* FROM habit_tracker_log JOIN habits ON habit_tracker_log.habit_id = habits.habit_id WHERE habits.username = ?  ORDER BY habit_tracker_log.created_at DESC LIMIT 10";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            while(rs.next()){
                HabitTrackerLog h = mapRow(rs);
                habitTrackerLogs.add(h);

            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
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
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return habitTrackerLogs;
    }


    // get habitTrackerLog by habit id

    @Override
    public HabitTrackerLog getHabitTrackerLogById(int id){

        HabitTrackerLog habit = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM habit_tracker_log where habit_id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){

                habit = mapRow(rs);
            }


        } catch (SQLException e) {
            System.out.println("Exception occured in the getCartByIdAndUsername() method: " + e.getMessage());
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
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return habit;
    }


    // add a habit

    @Override
    public int addHabitToTracker(HabitTrackerLog habitTrackerLog){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected
        int rowsAffected = 0;


        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO habit_tracker_log " +
                "(habit_id, date_of_activity, duration_minutes, distance_km, calories_burned, notes, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setInt(1, habitTrackerLog.getHabit_id());
            ps.setDate(2, Date.valueOf(habitTrackerLog.getDate_of_activity()));
            ps.setInt(3, habitTrackerLog.getDuration_minutes());
            ps.setDouble(4, habitTrackerLog.getDistance_km());
            ps.setInt(5, habitTrackerLog.getCalories_burned());
            ps.setString(6, habitTrackerLog.getNotes());
            ps.setTimestamp(7, Timestamp.valueOf(habitTrackerLog.getCreated_at()));

            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)
            rowsAffected = ps.executeUpdate();
        }// Add an extra exception handling block for where there is already an entry
        // with the primary key specified
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            // Set the rowsAffected to -1, this can be used as a flag for the display section
            rowsAffected = -1;
        }catch(SQLException e){
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return rowsAffected;
    }



    // delete habit logged


    @Override
    public int deleteHabitLogged( int habitId){
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try{

            con = getConnection();

            String query = "DELETE from habit_tracker_log where habit_tracker_log_id = ?";

            ps = con.prepareStatement(query);
            ps.setInt(1,habitId);

            rowsAffected = ps.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Exception occured in the updateProductName() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the updateProductName() method");
                e.getMessage();
            }
        }

        return rowsAffected;

    }


    @Override
    public int countLogsForHabitInWeek(int habitId, LocalDate weekStart, LocalDate weekEnd) {
        int count = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT COUNT(*) FROM habit_tracker_log WHERE habit_id = ? AND date_of_activity >= ? AND date_of_activity <= ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, habitId);
            ps.setDate(2, Date.valueOf(weekStart));
            ps.setDate(3, Date.valueOf(weekEnd));
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in countLogsForHabitInWeek: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the updateProductName() method");
                e.getMessage();
            }
        }
        return count;
    }

    @Override
    public ArrayList<HabitTrackerLog> getAllHabitsByUsernameAndDays(String username, int days) {
        ArrayList<HabitTrackerLog> habitTrackerLogs = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT habit_tracker_log.* FROM habit_tracker_log JOIN habits ON habit_tracker_log.habit_id = habits.habit_id WHERE habits.username = ? AND habit_tracker_log.date_of_activity >= DATE_SUB(CURDATE(), INTERVAL ? DAY) ORDER BY habit_tracker_log.created_at DESC";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setInt(2, days);
            rs = ps.executeQuery();
            while (rs.next()) {
                habitTrackerLogs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getAllHabitsByUsernameAndDays: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the updateProductName() method");
                e.getMessage();
            }
        }
        return habitTrackerLogs;
    }


    @Override
    public HabitTrackerLog getLatestLogByUsername(String username) {
        HabitTrackerLog log = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            String query = "SELECT habit_tracker_log.* FROM habit_tracker_log JOIN habits ON habit_tracker_log.habit_id = habits.habit_id WHERE habits.username = ? ORDER BY habit_tracker_log.created_at DESC LIMIT 1";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                log = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getLatestLogByUsername: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the updateProductName() method");
                e.getMessage();
            }
        }
        return log;
    }

    @Override
    public int updateHabitLog(int habitTrackerLogId, int durationMinutes, double distanceKm, int caloriesBurned, String notes) {
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            String query = "UPDATE habit_tracker_log SET duration_minutes = ?, distance_km = ?, calories_burned = ?, notes = ? WHERE habit_tracker_log_id = ?";
            ps = con.prepareStatement(query);
            ps.setObject(1, durationMinutes);
            ps.setObject(2, distanceKm);
            ps.setObject(3, caloriesBurned);
            ps.setString(4, notes);
            ps.setInt(5, habitTrackerLogId);
            rowsAffected = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception occurred in the updateHabitLog() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of updateHabitLog(): " + e.getMessage());
            }
        }

        return rowsAffected;
    }

    /**
     * Search through each row in the user
     * @param rs is the query for user to be searched
     * @return the user information
     * @throws SQLException is username and email isn't unique
     */
    private HabitTrackerLog mapRow(ResultSet rs) throws SQLException {

        HabitTrackerLog h = new HabitTrackerLog(

                rs.getInt("habit_tracker_log_id"),
                rs.getInt("habit_id"),
                rs.getDate("date_of_activity").toLocalDate(),
                rs.getInt("duration_minutes"),
                rs.getDouble("distance_km"),
                rs.getInt("calories_burned"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        return h;
    }

}
