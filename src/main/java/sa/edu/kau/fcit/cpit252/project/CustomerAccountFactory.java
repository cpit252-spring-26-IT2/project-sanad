package sa.edu.kau.fcit.cpit252.project;

// The customer account factory concrete creator class.
// This concrete creator class implements the factory method to create and
// return a customer account object.
// It encapsulates the instantiation of the customer account object.

public class CustomerAccountFactory extends AccountFactory {

    private String name;
    private String username;
    private String password;

    public CustomerAccountFactory(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // Implements the factory method to return a newly created customer account
    @Override
    public Account createAccount() {
        return new CustomerAccount(name, username, password);
    }
}