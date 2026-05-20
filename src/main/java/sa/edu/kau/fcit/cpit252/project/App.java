package sa.edu.kau.fcit.cpit252.project;

import sa.edu.kau.fcit.cpit252.project.composite.categories.CatalogComponent;
import sa.edu.kau.fcit.cpit252.project.composite.categories.ToolCategory;
import sa.edu.kau.fcit.cpit252.project.composite.categories.ToolItem;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.FilterCriteria;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductFilterService;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductListing;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.*;
import sa.edu.kau.fcit.cpit252.project.reviews.Review;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewService;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewSummary;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.HighestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.LowestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.PriceComparisonService;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println("Customer account created:");
        System.out.println("Account Type: " + customerAccount.getAccountType());
        System.out.println("Name: " + customerAccount.getName());
        System.out.println("Username: " + customerAccount.getUsername() + "\n");

        // Instantiate the concrete creator for a shop account
        AccountFactory shopAccountFactory = new ShopAccountFactory("Toney for Flooring", "toneyflooring", "bestshop123", "Flooring");

        // Call the factory method to build the concrete product
        Account shopAccount = shopAccountFactory.createAccount();
        System.out.println("Shop account created:");
        System.out.println("Account Type: " + shopAccount.getAccountType());
        System.out.println("Shop Name: " + shopAccount.getName());
        System.out.println("Username: " + shopAccount.getUsername());
        if (shopAccount instanceof ShopAccount) {
            System.out.println("Shop Category: " + ((ShopAccount) shopAccount).getShopCategory());
        }
        System.out.println();
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

        // [BEHAVIORAL] STRATEGY DESIGN PATTERN TESTING
        System.out.println("Strictly testing strategy design pattern in this section.");
        System.out.println("==========\n");

        List<ProductOffer> pipeOffers = new ArrayList<>();
        pipeOffers.add(new ProductOffer("Pipe", "Copper", "Toney for Flooring", 25.00));
        pipeOffers.add(new ProductOffer("Pipe", "Copper", "BuildHub Materials", 21.50));
        pipeOffers.add(new ProductOffer("Pipe", "Copper", "Al-Sanad Hardware", 27.75));

        PriceComparisonService priceComparisonService = new PriceComparisonService(new LowestPriceStrategy());
        List<ProductOffer> lowestToHighest = priceComparisonService.compareOffers(pipeOffers);

        System.out.println("Offers sorted by LOWEST price:");
        for (ProductOffer offer : lowestToHighest) {
            System.out.println(offer);
        }

        priceComparisonService.setStrategy(new HighestPriceStrategy());
        List<ProductOffer> highestToLowest = priceComparisonService.compareOffers(pipeOffers);

        System.out.println("\nOffers sorted by HIGHEST price:");
        for (ProductOffer offer : highestToLowest) {
            System.out.println(offer);
        }

        System.out.println("\n==========\n");
        // [BEHAVIORAL] END OF STRATEGY DESIGN PATTERN TESTING

        // [BEHAVIORAL] CHAIN OF RESPONSIBILITY DESIGN PATTERN TESTING
        System.out.println("Chain of Responsibility Demo - Advanced Filtering System");
        System.out.println("==========\n");

        List<ProductListing> listings = new ArrayList<>();
        listings.add(new ProductListing("Ceramic Floor Tile", "Flooring", "Matte", "Toney Flooring", 35.0, true, 4.5));
        listings.add(new ProductListing("Copper Pipe", "Plumbing", "3/4 inch", "Jeddah Plumbing Supplies", 25.0, true, 4.2));
        listings.add(new ProductListing("Plastic Pipe", "Plumbing", "1/2 inch", "Jeddah Plumbing Supplies", 10.0, true, 3.9));
        listings.add(new ProductListing("Cement Bag", "Building Materials", "50 KG", "BuildPro Materials", 18.0, true, 4.0));
        listings.add(new ProductListing("Paint Bucket", "Paint", "White", "Red Sea Paints", 75.0, false, 4.7));
        listings.add(new ProductListing("Hammer", "Tools", "Wooden Handle", "BuildPro Materials", 22.0, true, 4.1));
        listings.add(new ProductListing("Bathroom Sink", "Bathroom Fixtures", "Porcelain", "Toney Flooring", 240.0, true, 4.6));
        listings.add(new ProductListing("Electrical Cable", "Electrical", "10m", "Al Noor Electrical", 60.0, true, 4.3));

        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setCategory("Plumbing");
        filterCriteria.setMinPrice(10.0);
        filterCriteria.setMaxPrice(30.0);
        filterCriteria.setAvailableOnly(true);
        filterCriteria.setMinRating(4.0);

        ProductFilterService productFilterService = new ProductFilterService();
        List<ProductListing> filteredListings = productFilterService.filterProducts(listings, filterCriteria);

        System.out.println("Original product list:");
        for (ProductListing listing : listings) {
            System.out.println(listing);
        }

        System.out.println("\nFiltered result:");
        for (ProductListing listing : filteredListings) {
            System.out.println(listing);
        }

        System.out.println("\n==========\n");
        // [BEHAVIORAL] END OF CHAIN OF RESPONSIBILITY DESIGN PATTERN TESTING

        // REVIEWS SYSTEM DEMO
        System.out.println("Reviews System Demo - 5-Star Ratings");
        System.out.println("==========\n");

        ReviewService reviewService = new ReviewService();

        reviewService.addReview(new Review("Riyad Mahrez", ReviewTargetType.PRODUCT, "Copper Pipe", 5));
        reviewService.addReview(new Review("Ammar", ReviewTargetType.PRODUCT, "Copper Pipe", 4));
        reviewService.addReview(new Review("Abdulaziz", ReviewTargetType.PRODUCT, "Plastic Pipe", 3));
        reviewService.addReview(new Review("Abdullah", ReviewTargetType.SHOP, "Toney Flooring", 5));
        reviewService.addReview(new Review("Riyad Mahrez", ReviewTargetType.SHOP, "Jeddah Plumbing Supplies", 4));

        System.out.println("Individual reviews:");
        for (Review review : reviewService.getAllReviews()) {
            System.out.println(review);
        }

        ReviewSummary copperPipeSummary = reviewService.getSummary(ReviewTargetType.PRODUCT, "Copper Pipe");
        ReviewSummary toneyFlooringSummary = reviewService.getSummary(ReviewTargetType.SHOP, "Toney Flooring");

        System.out.println("\nProduct: " + copperPipeSummary.getTargetName());
        System.out.println("Average Rating: " + copperPipeSummary.getAverageRating() + " / 5");
        System.out.println("Visual Rating: " + copperPipeSummary.getVisualRating());
        System.out.println("Total Reviews: " + copperPipeSummary.getTotalReviews());

        System.out.println("\nShop: " + toneyFlooringSummary.getTargetName());
        System.out.println("Average Rating: " + toneyFlooringSummary.getAverageRating() + " / 5");
        System.out.println("Visual Rating: " + toneyFlooringSummary.getVisualRating());
        System.out.println("Total Reviews: " + toneyFlooringSummary.getTotalReviews());

        System.out.println("\n==========\n");
    }
}