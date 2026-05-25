package habitTracker.persistence;


import habitTracker.business.User;
public interface UserDao {

    public int registerUser(User newUser);

    public User findUserByUsername(String username);

    public String getPasswordByUsername(String username);

    public boolean usernameExists(String username);

    public boolean emailExists(String email);

}
