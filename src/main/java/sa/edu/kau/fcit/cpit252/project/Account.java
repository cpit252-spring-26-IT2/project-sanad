package sa.edu.kau.fcit.cpit252.project;


// The Account interface (product interface).
// This interface has the common attributes that both the customer and shop accounts share.

public interface Account {
    String getAccountType();
    String getName();
    String getUsername();
    String getPassword();
}