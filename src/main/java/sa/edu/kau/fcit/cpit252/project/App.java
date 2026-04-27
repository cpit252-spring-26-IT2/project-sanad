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
        System.out.println("Strictly testing composite design pattern in this section.");
        System.out.println("==========\n");

        // Create individual items
        CatalogComponent copperPipe = new ToolItem("Pipe", "Copper", 25.00);
        CatalogComponent plasticPipe = new ToolItem("Pipe", "Plastic", 10.00);

        CatalogComponent woodHammer = new ToolItem("Hammer", "Wooden Handle", 15.00);
        CatalogComponent glassHammer = new ToolItem("Hammer", "Fiberglass Handle", 22.00);

        // Create a category (group) for pipes and hammers
        ToolCategory pipeGroup = new ToolCategory("Pipes");
        pipeGroup.addComponent(copperPipe);
        pipeGroup.addComponent(plasticPipe);

        ToolCategory hammerGroup = new ToolCategory("Hammers");
        hammerGroup.addComponent(woodHammer);
        hammerGroup.addComponent(glassHammer);

        // Create the main categories for plumbing supplies and hand tools
        ToolCategory plumbing = new ToolCategory("Plumbing Supplies");
        plumbing.addComponent(pipeGroup);

        ToolCategory handTools = new ToolCategory("Hand Tools");
        handTools.addComponent(hammerGroup);

        // Create the full store category
        ToolCategory fullStore = new ToolCategory("Complete Building Store");
        fullStore.addComponent(plumbing);
        fullStore.addComponent(handTools);

        String categoryName = pipeGroup.getName();
        double storeTotal = fullStore.getPrice();

        // Print the tree structure of the full store
        System.out.println(fullStore);

        // Print the category name and the total store value
        System.out.println("Category Name: " + categoryName);
        System.out.println("Total Store Value: SAR " + storeTotal);
        System.out.println("\n==========\n");

        // Print all categories in the store
        System.out.println(fullStore.getAllCategories());
        // [STRUCTURAL] END OF COMPOSITE DESIGN PATTERN TESTING
    }
}
