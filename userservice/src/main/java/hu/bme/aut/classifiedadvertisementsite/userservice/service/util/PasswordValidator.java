package hu.bme.aut.classifiedadvertisementsite.userservice.service.util;

public class PasswordValidator {

    public static boolean validatePassoword(String password) {
        return password.length() >= 8;
    }
}
