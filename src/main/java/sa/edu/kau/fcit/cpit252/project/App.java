package sa.edu.kau.fcit.cpit252.project;

// TESTING ENVIRONMENT PLEASE IGNORE CODE QUALITY IN THIS FILE
// FINAL PROGRAM WILL BE A WEB APPLICATION

public class App {

    // Client class for demonstrating our current implementations

    public static void main(String[] args) {
        System.out.println("\nWelcome to the demo of the project!\n");
        System.out.println("Note: this demo is not the final product.");
        System.out.println("It is only meant to demonstrate the functionality of our current progress.");
        System.out.println("The final product will be a web application.\n");

        // [CREATIONAL] FACTORY METHOD DESIGN PATTERN IMPLEMENTATION TESTING

        // Instantiate the concrete creator for a customer account
        AccountFactory customerAccountFactory = new CustomerAccountFactory("Riyad Mahrez", "mahrez", "galeno123");

        // Call the factory method to build the concrete product
        Account customerAccount = customerAccountFactory.createAccount();
        System.out.println("Customer account created:\n" + customerAccount);

        // Instantiate the concrete creator for a shop account
        AccountFactory shopAccountFactory = new ShopAccountFactory("Toney for Flooring", "toneyflooring", "bestshop123", "Flooring");

        // Call the factory method to build the concrete product
        Account shopAccount = shopAccountFactory.createAccount();
        System.out.println("Shop account created:\n" + shopAccount);

        // [CREATIONAL] END OF FACTORY METHOD DESIGN PATTERN TEST




        // [STRUCTURAL] COMPOSITE DESIGN PATTERN TESTING



        // [STRUCTURAL] END OF COMPOSITE DESIGN PATTERN TESTING
    }
}
