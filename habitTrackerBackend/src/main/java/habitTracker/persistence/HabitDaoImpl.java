package habitTracker.persistence;

import habitTracker.business.Habit;
import lombok.extern.slf4j.Slf4j;

import habitTracker.business.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;


@Repository
@Slf4j
public class HabitDaoImpl extends MySQLDao implements HabitDao {

    /**
     * get the database information from a particular database
     * @param databaseName is the database being searched
     */
    public HabitDaoImpl(String databaseName){
        super(databaseName);
    }

    public HabitDaoImpl(Connection conn){
        super(conn);
    }
    public HabitDaoImpl(){
        super();
    }



    // getting all habits for a username

    @Override
    public ArrayList<Habit> getAllHabitsByUsername(String username){

        ArrayList<Habit> habitArrayList = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM habits where username = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            while(rs.next()){
                Habit h = mapRow(rs);
                habitArrayList.add(h);

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
        return habitArrayList;
    }



    // get habit by id

    @Override
    public Habit getHabitById(int id){

        Habit habit = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM habits where habit_id = ?";
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

// get habit by id and username

    @Override
    public Habit getHabitByIdAndUsername(String username, int id){

        Habit habit = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM habits where habit_id = ? AND username = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, username);
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
    public int addHabit(Habit habit){
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
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO habits (username, activity_type_id, description, habit_reminder, habit_target, habit_frequency, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, habit.getUsername());
            ps.setInt(2, habit.getActivity_type_id());
            ps.setString(3, habit.getDescription());
            ps.setString(4, habit.getHabit_reminder());
            ps.setInt(5, habit.getHabit_target());
            ps.setInt(6, habit.getHabit_frequency());
            ps.setBoolean(7, habit.is_active());

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


    // update a habit

    @Override
    public int updateHabit(Habit habit) {
        Connection con = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            con = getConnection();

            String query = "UPDATE habits SET activity_type_id = ?, description = ?, habit_reminder = ?, habit_target = ?, habit_frequency = ?, is_active = ? WHERE habit_id = ?";

            ps = con.prepareStatement(query);

            ps.setInt(1, habit.getActivity_type_id());
            ps.setString(2, habit.getDescription());
            ps.setString(3, habit.getHabit_reminder());
            ps.setInt(4, habit.getHabit_target());
            ps.setInt(5, habit.getHabit_frequency());
            ps.setBoolean(6, habit.is_active());
            ps.setInt(7, habit.getHabit_id());


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

    // delete habit

    @Override
    public int deleteHabit(String username, int habitId){
        int rowsAffected = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try{

            con = getConnection();

            String query = "DELETE from habits where username = ? and habit_id = ?";

            ps = con.prepareStatement(query);
            ps.setString(1,username);
            ps.setInt(2,habitId);

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



    // get latest habit for user

    @Override
    public Habit getLatestHabitForUser(String username){

        Habit habit = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM habits WHERE username = ? ORDER BY habit_id DESC LIMIT 1";
            ps = con.prepareStatement(query);
            ps.setString(1, username);
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

    // find habit with remidners


    @Override
    public ArrayList<Habit> getAllActiveHabitsWithReminders(){

        ArrayList<Habit> habitArrayList = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM habits where is_active = TRUE AND habit_reminder IS NOT NULL";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            rs = ps.executeQuery();

            while(rs.next()){
                Habit h = mapRow(rs);
                habitArrayList.add(h);

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
        return habitArrayList;
    }



    /**
     * Search through each row in the user
     * @param rs is the query for user to be searched
     * @return the user information
     * @throws SQLException is username and email isn't unique
     */
    private Habit mapRow(ResultSet rs) throws SQLException {

        Habit h = new Habit(

                rs.getInt("habit_id"),
                rs.getString("username"),
                rs.getInt("activity_type_id"),
                rs.getString("description"),
                rs.getString("habit_reminder"),
                rs.getInt("habit_target"),
                rs.getInt("habit_frequency"),
                rs.getBoolean("is_active")
        );

        return h;
    }

}
