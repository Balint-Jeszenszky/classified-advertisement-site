package hu.bme.aut.classifiedadvertisementsite.userservice.service.util;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.InternalServerErrorException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomStringGenerator {
    private static SecureRandom secureRandom;

    public static String generate(Integer length) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (secureRandom == null) {
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        }

        return secureRandom.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}