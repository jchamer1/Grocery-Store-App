package edu.hfcc.grocery.client;

import edu.hfcc.grocery.Objects.Cart;
import edu.hfcc.grocery.Objects.Grocery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientTools {
    DecimalFormat df = new DecimalFormat("#.00");

    public void itemSelection(Client client) {
        int menuInput;
        int quantity;
        List<Integer> userItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the ID for the item you want or enter -1 to exit: ");
        System.out.print("Selection: ");
        menuInput = in.nextInt();
        checkIfGroceryItemExists(client, menuInput);
        while(menuInput != -1) {
            userItems.add(menuInput);
            System.out.println("How many would you like: ");
            System.out.print("Selection: ");
            quantity = in.nextInt();
            while(quantity < 0) {
                System.out.println("Invalid input. Please try again: ");
                quantity = in.nextInt();
            }
            while(!checkQuantity(client, menuInput, quantity)) {
                System.out.println("Invalid input. Please try again: ");
                quantity = in.nextInt();
            }
            quantities.add(quantity);
            System.out.println("Enter the ID for the item you want or enter -1 to exit: ");
            System.out.print("Selection: ");
            menuInput = in.nextInt();
        }

        client.setIdSelection(userItems);
        client.setQuantityOfItem(quantities);
    }

    public void outputItems(Client client) {
        for(Grocery g : client.getGroceriesSelected()) {
            System.out.println("ID: " + g.id + " Name: " + g.name + " Type: " + g.type + " Price: $" + df.format(Double.parseDouble(g.price)) + " Quantity: " + g.quantity);
        }
    }

    public void outputCart(Client client) {
        System.out.println("Number of items in cart: " + client.getCart().size());
        for(Cart c : client.getCart()) {
            System.out.println("ID: " + c.id + " Name: " + c.name + " Type: " + c.type + " Price: $" + df.format(Double.parseDouble(c.price)) + " Quantity: " + c.quantity);
        }
        System.out.println("Total: $" + df.format(client.getOrderTotal()));
    }

    public void cartRemovalMenu(Client client) {
        List<Integer> idOfItems = new ArrayList<>();
        List<Integer> quantityToRemove = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        int menuInput = 0;
        int quantity;

        outputCart(client);
        System.out.println("Enter the ID of the item you want to remove or -1 to exit: ");
        System.out.print("Selection: ");
        menuInput = in.nextInt();
        while (!checkIfCartItemExists(client, menuInput)) {
            System.out.println("Item ID doesn't match any items in the cart. Please try again.");
            System.out.print("Selection: ");
            menuInput = in.nextInt();
        }

        while (menuInput != -1) {
            idOfItems.add(menuInput);
            System.out.println("Enter the quantity you want to remove: ");
            quantity = in.nextInt();
            while (quantity < 0) {
                System.out.println();
                System.out.println("Invalid quantity selected. Please try again.");
                System.out.print("Selection: ");
                quantity = in.nextInt();
            }
            while (!checkCartQuantity(client, menuInput, quantity)) {
                System.out.println("Invalid quantity selected. Please try again.");
                System.out.print("Selection: ");
                quantity = in.nextInt();
            }
            quantityToRemove.add(quantity);
            System.out.println("Enter the ID of the item you want to remove or -1 to exit: ");
            System.out.print("Selection: ");
            menuInput = in.nextInt();
            while (!checkIfCartItemExists(client, menuInput)) {
                System.out.println("Item ID doesn't match any items in the cart. Please try again.");
                System.out.print("Selection: ");
                menuInput = in.nextInt();
            }
        }

        client.setGroceriesToRemove(idOfItems);
        client.setQuantityToRemove(quantityToRemove);
    }

    public boolean checkIfCartItemExists(Client client, int itemId) {
        boolean itemExists = false;
        if(itemId == -1) {
            return true;
        }
        for(Cart c : client.getCart()) {
            if(itemId == c.id) {
                itemExists = true;
                break;
            }
        }

        return itemExists;
    }

    public boolean checkCartQuantity(Client client, int itemId, int quantity) {
        boolean isQuantityValid = false;
        for(Cart c : client.getCart()) {
            if(c.id == itemId) {
                if(c.quantity > quantity) {
                    isQuantityValid = true;
                    break;
                }
            }
        }
        return isQuantityValid;
    }

    public boolean checkIfGroceryItemExists(Client client, int menuInput) {
        boolean itemExists = false;
        for(Grocery g : client.getGroceriesSelected()) {
            if(g.id == menuInput) {
                itemExists = true;
                break;
            }
        }

        return itemExists;
    }

    public int isValidInput(int min, int max, int menuInput) {
        Scanner in = new Scanner(System.in);
        while(menuInput < min || menuInput > max) {
            System.out.println("Invalid input please try again:");
            System.out.print("Selection: ");
            menuInput = in.nextInt();
        }

        return menuInput;
    }

    public boolean checkQuantity(Client client, int groceryId, int quantity) {
        boolean quantityIsEnough = false;

        for(Grocery g : client.getGroceriesSelected()) {
            if(g.id == groceryId) {
                if(g.quantity >= quantity) {
                    quantityIsEnough = true;
                    break;
                }
            }
        }

        return quantityIsEnough;
    }


}
