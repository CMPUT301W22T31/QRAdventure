package com.example.qradventure;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class is used to validate user info. It has three separate methods, each to verify a unique
 * piece of data from a users account.
 */
public class InputValidator {
    /**
     * Use to validate the users username
     * @param username
     *      username we are verifying
     * @return
     *      True if valid (between 3 and 18 characters inclusive), false otherwise
     */
    public boolean checkUser(String username) {
        return username.length()>=3&&username.length()<=18;
    }

    /**
     * Use to validate the users phone number
     * @param phone
     *      phone number we are verifying
     * @return
     *      True if valid (a ten digit numeric phone number), false otherwise
     *
     *      Citation
     *      Last Updated : 23 Dec, 2021
     *      Improved By: singghakshay, surinderdawra388
     *      https://www.geeksforgeeks.org/java-program-to-check-for-a-valid-mobile-number/
     */
    public boolean checkPhone(String phone) {
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(phone);
        return (m.matches());
    }

    /**
     * Use to validate the users email
     * @param email
     *      email we are verifying
     * @return
     *      True if valid email format, false if invalid
     *
     *      Citation
     *      Last Updated : 22 Oct, 2021
     *      Improved By : nishkarshgandhi
     *      // https://www.geeksforgeeks.org/check-email-address-valid-not-java/
     */

    public boolean checkEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                              "[a-zA-Z0-9_+&*-]+)*@" +
                              "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                              "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailPattern);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
