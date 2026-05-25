package habitTracker.persistence;

import habitTracker.business.DeviceToken;

import java.util.ArrayList;

public interface DeviceTokenDao {

    public int saveToken(DeviceToken token);

    public ArrayList<DeviceToken> getTokensByUsername(String username);

    public ArrayList<DeviceToken> getAllTokens();

    public int updateEndpointArn(String fcmToken, String arn);

}
