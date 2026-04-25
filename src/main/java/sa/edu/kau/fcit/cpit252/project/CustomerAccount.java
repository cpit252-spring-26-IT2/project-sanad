package sa.edu.kau.fcit.cpit252.project;

// The customer account concrete product.
// This concrete product implements the Account interface and has the appropriate "attributes"
// for the customer account. There are no additions made here in comparison to the interface.

public class CustomerAccount implements Account {
    private String customerName;
    private String username;
    private String password;

    public CustomerAccount(String customerName, String username, String password) {
        this.customerName = customerName;
        this.username = username;
        this.password = password;
    }

    // We will use this later in the UI to implement distinct features.
    // Such as: a switch between creating a shop account and a customer account.
    // So we will return the account type as "CUSTOMER" here.
    @Override
    public String getAccountType() {
        return "CUSTOMER";
    }

    @Override
    public String getName() {
        return customerName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "Account Type: " + getAccountType() +
                "\nName: " + getName() +
                "\nUsername: " + getUsername() +
                "\nPassword: " + getPassword() + "\n";
    }
}