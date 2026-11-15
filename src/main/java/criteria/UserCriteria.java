package criteria;

import domain.User;

import java.util.function.Predicate;

public final class UserCriteria {

    private UserCriteria(){

    }

    public static Predicate<User> lastNameStartsWith(String prefix){
        return user -> user.getLastName().startsWith(prefix);
    }

    public static Predicate<User> firstNameStartsWith(String prefix){
        return user -> user.getFirstName().startsWith(prefix);
    }

    public static Predicate<User> idGreaterThan(Long id){
        return user -> user.getId() > id;
    }
}
