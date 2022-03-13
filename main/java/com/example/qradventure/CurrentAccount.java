package com.example.qradventure;


import java.util.ArrayList;

/**
 * Lazy Singleton
 * Holds the Account that is currently logged into the device
 */
public class CurrentAccount {
    private static CurrentAccount instance;
    private static Account account;

    /**
     * Lazy singleton constructor is private.
     */
    private CurrentAccount() {
    }

    /**
     * Lazy singleton implementation: getInstance() is static
     * @return
     *      CurrentAccount object - the only instance of this class.
     */
    // DOCS: Lazy singleton implementation
    // getInstance() is static so it can be called without making a new instance of the class.
    // *returns: CurrentAccount - the only instance of this singleton class
    public static Account getAccount() {
        if (account == null) {
            account = new Account("Filler", "filler", "filler", "filler",
                    "filler", new ArrayList<Record>() );
        }
        return account;
    }

    public static void setAccount(Account acc) {
        account = acc;
    }

    public Account getCurrentAccount() {
		return account;
    }

    /**
     * Setter method
     * Takes in a username and constructs the associated Account object from firestore fields.
     * Sets account to this reconstructed Account.
     * @param username
     *          String - unique username of account
     */
    public void setByUsername(String username) {
        // This might be useful for LoginQRActivity?
    }

}