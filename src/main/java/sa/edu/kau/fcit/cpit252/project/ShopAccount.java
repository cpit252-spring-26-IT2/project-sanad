package sa.edu.kau.fcit.cpit252.project;

// The shop account concrete product.
// This concrete product implements the Account interface and has the appropriate "attributes"
// for the shop account.
// The shop account product has an additional attribute, called the shop category.

public class ShopAccount implements Account {
    private String shopName;
    private String username;
    private String password;
    private String shopCategory;

    public ShopAccount(String shopName, String username, String password, String shopCategory) {
        this.shopName = shopName;
        this.username = username;
        this.password = password;
        this.shopCategory = shopCategory;
    }

    // We will use this later in the UI to implement distinct features.
    // Such as: a switch between creating a shop account and a customer account.
    // So we will return the account type as "SHOP" here.
    @Override
    public String getAccountType() {
        return "SHOP";
    }

    @Override
    public String getName() {
        return shopName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getShopCategory() {
        return this.shopCategory;
    }


    @Override
    public String toString() {
        return "Account Type: " + getAccountType() +
                "\nShop Name: " + getName() +
                "\nUsername: " + getUsername() +
                "\nPassword: " + getPassword() +
                "\nShop Category: " + getShopCategory() + "\n";
    }
}