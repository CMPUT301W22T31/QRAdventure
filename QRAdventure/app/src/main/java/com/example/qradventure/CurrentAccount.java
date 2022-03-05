package com.example.qradventure;


/**
 * Lazy Singleton
 * Holds the Account that is currently logged into the device
 */
public class CurrentAccount {
    private static CurrentAccount instance;
    private Account account;

    /**
     * Lazy singleton constructor is private. We have nothing to set inside it.
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
    public static CurrentAccount getInstance() {
        if (instance == null) {
            instance = new CurrentAccount();
        }
        return instance;
    }

    public void setAccount(Account acc) {
        account = acc;
    }

    /**
     * Setter method
     * Takes in a username and constructors the associated Account object.
     * Sets account to this reconstructed created Account.
     * @param username
     *          String - unique username of account
     */
    public void setByUsername(String username) {
        // TODO: implement? scrap?
    }

    public Account getCurrentAccount() {
		return account;
    }

}