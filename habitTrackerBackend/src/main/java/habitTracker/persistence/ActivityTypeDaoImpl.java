package habitTracker.persistence;

import habitTracker.business.ActivityType;
import habitTracker.business.Habit;
import habitTracker.business.HabitTrackerLog;
import lombok.extern.slf4j.Slf4j;

import habitTracker.business.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;


@Repository
@Slf4j
public class ActivityTypeDaoImpl extends MySQLDao implements ActivityTypeDao {

    /**
     * get the database information from a particular database
     * @param databaseName is the database being searched
     */
    public ActivityTypeDaoImpl(String databaseName){
        super(databaseName);
    }

    public ActivityTypeDaoImpl(Connection conn){
        super(conn);
    }
    public ActivityTypeDaoImpl(){
        super();
    }



    // get all activity types

    @Override
    public ArrayList<ActivityType> getAllActivityTypes() {
        ArrayList<ActivityType> activityTypes = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try {


            con = getConnection();

            // String query = "SELECT * from movieProduct";
            String query = "Select * FROM activity_types";
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                ActivityType m = mapRow(rs);
                activityTypes.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Exception occured in the getAllProducts() method: " + e.getMessage());
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
                System.out.println("Exception occured in the finally section of the getAllProducts() method: " + e.getMessage());
            }
        }

        return activityTypes;
    }



    //get one activity type by id


    @Override
    public ActivityType getActivityById(int id){

        ActivityType activityType = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try  {

            con = getConnection();

            String query = "SELECT * FROM activity_types where activity_type_id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){

                activityType = mapRow(rs);
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
        return activityType;
    }

    /**
     * Search through each row in the user
     * @param rs is the query for user to be searched
     * @return the user information
     * @throws SQLException is username and email isn't unique
     */
    private ActivityType mapRow(ResultSet rs) throws SQLException {

        ActivityType a = new ActivityType(

                rs.getInt("activity_type_id"),
                rs.getString("name"),
                rs.getString("activity_done")
        );

        return a;
    }

}
