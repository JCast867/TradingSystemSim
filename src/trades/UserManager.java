package trades;

import java.util.Random;
import exceptions.BadParameterException;
import exceptions.DataValidationException;
import exceptions.NullParameterException;

import java.util.HashMap;

public final class UserManager {

    private static UserManager instance;
    private static HashMap<String, User> userMap = new HashMap<>();

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public void init(String[] userIn) throws NullParameterException, BadParameterException, DataValidationException {
        for (String userId : userIn) {
            if (userId == null) {
                throw new DataValidationException("User id cannot be null.");
            }
            userMap.put(userId, new User(userId));
        }
    }

    public User getRandomUser() {
        if (userMap.isEmpty()) {
            return null;
        }
        Object[] userArray = userMap.values().toArray();
        return (User) userArray[new Random().nextInt(userArray.length)];
    }

    public void addToUser(String userId, TradableDTO o) throws BadParameterException, NullParameterException, DataValidationException {
        if (userId == null || o == null) {
            throw new DataValidationException("User id can't be null.");
        }
        if (getUser(userId) == null) {
            throw new DataValidationException("User does not exist.");
        }
        User user = getUser(userId);

        user.addTradable(o);

    }

    public User getUser(String userId) throws BadParameterException, NullParameterException, DataValidationException {
        if (userId == null) {
            throw new DataValidationException("User id cannot be null.");
        }
        if (userMap.get(userId) == null) {
            throw new DataValidationException("User does not exist.");
        }
        return userMap.get(userId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        userMap.values().forEach(userMap -> builder.append(userMap.toString()).append("\n"));
        return builder.toString();
    }
}
