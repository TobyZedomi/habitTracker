package habitTracker.persistence;

import org.springframework.stereotype.Repository;

import habitTracker.business.DeviceToken;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class DeviceTokenDaoImpl extends MySQLDao implements DeviceTokenDao{


    public DeviceTokenDaoImpl   (String databaseName){
        super(databaseName);
    }

    public DeviceTokenDaoImpl(Connection conn){
        super(conn);
    }
    public DeviceTokenDaoImpl(){
        super();
    }


    // save token

    @Override
    public int saveToken(DeviceToken token){
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
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO device_tokens (username, fcm_token, platform) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE fcm_token = VALUES(fcm_token)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, token.getUsername());
            ps.setString(2, token.getFcmToken());
            ps.setString(3, token.getPlatform());

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


    // get token by username


    @Override
    public ArrayList<DeviceToken> getTokensByUsername(String username){

        ArrayList<DeviceToken> deviceTokenArrayList = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM device_tokens WHERE username = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            while(rs.next()){
                DeviceToken deviceToken = mapRow(rs);
                deviceTokenArrayList.add(deviceToken);

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
        return deviceTokenArrayList;
    }



    // get all tokens



    @Override
    public ArrayList<DeviceToken> getAllTokens(){

        ArrayList<DeviceToken> deviceTokenArrayList = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM device_tokens WHERE sns_endpoint_arn IS NOT NULL";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            rs = ps.executeQuery();

            while(rs.next()){
                deviceTokenArrayList.add(mapRow(rs));
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
        return deviceTokenArrayList;
    }



    // update token


    @Override
    public int updateEndpointArn(String fcmToken, String arn) {
        Connection con = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            con = getConnection();

            String query = "UPDATE device_tokens SET sns_endpoint_arn = ? WHERE fcm_token = ?";

            ps = con.prepareStatement(query);

            ps.setString(1, arn);
            ps.setString(2, fcmToken);


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

    private DeviceToken mapRow(ResultSet rs) throws SQLException {
        return DeviceToken.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .fcmToken(rs.getString("fcm_token"))
                .snsEndpointArn(rs.getString("sns_endpoint_arn"))
                .platform(rs.getString("platform"))
                .build();
    }
}
