package habitTracker.persistence;

import habitTracker.business.Habit;
import habitTracker.business.HabitTrackerLog;
import habitTracker.business.Streak;
import habitTracker.business.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;

@Repository
public class StreakDaoImpl extends MySQLDao implements StreakDao{

    /**
     * get the database information from a particular database
     * @param databaseName is the database being searched
     */
    public StreakDaoImpl(String databaseName){
        super(databaseName);
    }

    public StreakDaoImpl(Connection conn){
        super(conn);
    }
    public StreakDaoImpl(){
        super();
    }



    // find streak by habit id

    @Override
    public Streak findStreakByHabitId(int id){

        Streak streak = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM streaks where habit_id= ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){

                streak = mapRow(rs);
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
        return streak;
    }


    // add to streak


    @Override
    public int addStreak(Streak streak){
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
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO streaks (habit_id, current_streak, longest_streak, last_completed_date) " +
                "VALUES (?, ?, ?, ?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setInt(1, streak.getHabit_id());
            ps.setInt(2, streak.getCurrent_streak());
            ps.setInt(3, streak.getLongest_streak());
            ps.setDate(4, Date.valueOf(streak.getLast_completed_date()));


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


    // update current streak


    // update a habit

    @Override
    public int updateCurrentStreak(int currentStreak, int habitId) {
        Connection con = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            con = getConnection();

            String query = "UPDATE streaks SET current_streak = ? WHERE habit_id = ?";

            ps = con.prepareStatement(query);

            ps.setInt(1, currentStreak);
            ps.setInt(2, habitId);


            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the updateReview() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of the method: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return rowsAffected;
    }



    @Override
    public int updateLongestStreak(int longestStreak, int habitId) {
        Connection con = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            con = getConnection();

            String query = "UPDATE streaks SET longest_streak = ? WHERE habit_id = ?";

            ps = con.prepareStatement(query);

            ps.setInt(1, longestStreak);
            ps.setInt(2, habitId);


            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the updateReview() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of the method: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return rowsAffected;
    }


    // update last completed date


    @Override
    public int updateLastCompletedDateOfStreak(LocalDate lastDate, int habitId) {
        Connection con = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            con = getConnection();

            String query = "UPDATE streaks SET last_completed_date = ? WHERE habit_id = ?";

            ps = con.prepareStatement(query);

            ps.setDate(1, Date.valueOf(lastDate));
            ps.setInt(2, habitId);


            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception occurred in the updateReview() method: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occurred in the finally section of the method: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return rowsAffected;
    }



    private Streak mapRow(ResultSet rs)throws SQLException {

        Streak s = new Streak(

                rs.getInt("streak_id"),
                rs.getInt("habit_id"),
                rs.getInt("current_streak"),
                rs.getInt("longest_streak"),
                rs.getDate("last_completed_date").toLocalDate()
        );
        return s;
    }


}
