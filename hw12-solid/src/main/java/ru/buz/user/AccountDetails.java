package ru.buz.user;

public class AccountDetails {
    private final String ACCOUNT_ID;
    private final String CURRENCY;
    private final String BALANCE;

    AccountDetails(String account_id, String currency, String balance) {
        ACCOUNT_ID = account_id;
        CURRENCY = currency;
        BALANCE = balance;
    }

    public String getACCOUNT_ID() {
        return ACCOUNT_ID;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public String getBALANCE() {
        return BALANCE;
    }
}
