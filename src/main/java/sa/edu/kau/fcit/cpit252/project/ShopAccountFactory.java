package sa.edu.kau.fcit.cpit252.project;

// The shop account factory concrete creator class.
// This concrete creator class implements the factory method to create and
// return a shop account object.
// It encapsulates the instantiation of the shop account object.
// Compared to the CustomerAccountFactory, it includes an extra attribute called shop category,
// which is specific to the shop account.

public class ShopAccountFactory extends AccountFactory {

    private String shopName;
    private String username;
    private String password;
    private String shopCategory;

    public ShopAccountFactory(String shopName, String username, String password, String shopCategory) {
        this.shopName = shopName;
        this.username = username;
        this.password = password;
        this.shopCategory = shopCategory;
    }

    // Implements the factory method to return a newly created shop account
    @Override
    public Account createAccount() {
        return new ShopAccount(shopName, username, password, shopCategory);
    }
}