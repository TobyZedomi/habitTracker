package habitTracker.persistence;

import lombok.extern.slf4j.Slf4j;

import habitTracker.business.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import java.sql.*;


@Repository
@Slf4j
public class UserDaoImpl extends MySQLDao implements UserDao {

    /**
     * get the database information from a particular database
     * @param databaseName is the database being searched
     */
    public UserDaoImpl(String databaseName){
        super(databaseName);
    }

    public UserDaoImpl(Connection conn){
        super(conn);
    }
    public UserDaoImpl(){
        super();
    }


    /**
     * Add a new user to the database
     * @param newUser is the user being added
     * @return 1 is user was added and -1 if not added
     */
    @Override
    public int registerUser(User newUser){
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
        try(PreparedStatement ps = conn.prepareStatement("insert into users values(?, ?, ?, ?, ?, ?, " +
                "?,?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, newUser.getUsername());
            ps.setString(2, newUser.getDisplay_name());
            ps.setString(3, newUser.getEmail());
            ps.setString(4, hashPassword(newUser.getPassword()));
            ps.setDate(5, Date.valueOf(newUser.getDateOfBirth()));
            ps.setBoolean(6, newUser.isAdmin());
            ps.setTimestamp(7, Timestamp.valueOf(newUser.getCreatedAt()));
            ps.setString(8, newUser.getUser_image());

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


    @Override
    public User findUserByUsername(String username){

        User user = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{


            con = getConnection();

            String query = "SELECT * FROM users where username = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();



            if(rs.next()){

                user = mapRow(rs);
            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
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
                System.out.println("Exception occurred in the finally section of the method: " + e.getMessage());
            }
        }
        return user;
    }

    @Override
    public String getPasswordByUsername(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        Connection conn = super.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getPasswordHashByUsername: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            super.freeConnection(conn);
        }
    }


    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        Connection conn = super.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        } finally {
            super.freeConnection(conn);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        Connection conn = super.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        } finally {
            super.freeConnection(conn);
        }
    }


    private static int workload = 12;

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);


        return(hashed_password);
    }

    /**
     * Search through each row in the user
     * @param rs is the query for user to be searched
     * @return the user information
     * @throws SQLException is username and email isn't unique
     */
    private User mapRow(ResultSet rs)throws SQLException {

        User u = new User(

                rs.getString("username"),
                rs.getString("display_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getDate("date_of_birth").toLocalDate(),
                rs.getBoolean("is_admin"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("user_image")
        );
        return u;
    }

}
