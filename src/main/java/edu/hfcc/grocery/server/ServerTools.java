package edu.hfcc.grocery.server;

import edu.hfcc.grocery.Objects.Cart;
import edu.hfcc.grocery.Objects.Grocery;
import edu.hfcc.grocery.client.Client;
import edu.hfcc.grocery.database.ConnectToDatabase;
import edu.hfcc.grocery.database.GroceryCreation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerTools {

    public static void createTable() throws SQLException {
        System.out.println("Creating tables");
        GroceryCreation.run();
    }

    public static void queryGroceries(Client client) throws SQLException {
        System.out.println("Querying database");
        List<Grocery> groceries = new ArrayList<>();
        Connection connection = ConnectToDatabase.connectToDatabase();

        PreparedStatement fruits = connection.prepareStatement("SELECT * FROM groceries WHERE type = 'fruit'");
        PreparedStatement vegetables = connection.prepareStatement("SELECT * FROM groceries WHERE type = 'vegetable'");
        ResultSet resultSet;

        if(client.getMenuOption() == 1) {
            resultSet = fruits.executeQuery();
        } else {
            resultSet = vegetables.executeQuery();
        }

        resultSet.next();

        while (!resultSet.isAfterLast()) {
            Grocery grocery = new Grocery();
            grocery.id = resultSet.getInt(1);
            grocery.name = resultSet.getString(2);
            grocery.type = resultSet.getString(3);
            grocery.price = Double.toString(resultSet.getDouble(4));
            grocery.quantity = resultSet.getInt(5);
            groceries.add(grocery);
            resultSet.next();
        }

        client.setGroceriesSelected(groceries);

        connection.close();
    }

    public static void createCart(Client client) throws SQLException {
        double orderTotal = client.getOrderTotal();
        List<Cart> cartContents = new ArrayList<>();
        for(Grocery g : client.getGroceriesSelected()) {
            for(int selection : client.getIdSelection()) {
                if (g.id == selection) {
                    Cart cart = new Cart();
                    cart.id = g.id;
                    cart.name = g.name;
                    cart.type = g.type;
                    cart.price = g.price;
                    cart.quantity = client.getQuantityOfItem().get(client.getIdSelection().indexOf(selection));
                    orderTotal += (Double.parseDouble(g.price) * cart.quantity);
                    cartContents.add(cart);
                }
            }
        }

        client.setCart(cartContents);
        client.setOrderTotal(orderTotal);

        addToCart(client);
        System.out.println("Cart created and items added");
    }

    public static void addToCart(Client client) throws SQLException {
        double price = 0;
        Connection connection = ConnectToDatabase.connectToDatabase();
        String sql;
        Statement statement = connection.createStatement();

        System.out.println("Adding to cart");

        for(Cart c : client.getCart()) {
            price = Double.parseDouble(c.price);
            sql = "INSERT INTO CART(id, name, type, price, quantity) VALUES(" + c.id + ",'" + c.name + "','" + c.type + "'," + price + "," + c.quantity + ")";
            statement.addBatch(sql);
        }

        statement.executeBatch();
        connection.close();
    }

    public static void removeFromCart(Client client) throws SQLException{
        Connection connection = ConnectToDatabase.connectToDatabase();
        Statement statement = connection.createStatement();
        String sql;

        for(int i : client.getGroceriesToRemove()) {
            int index = client.getGroceriesToRemove().indexOf(i);
            sql = "UPDATE cart SET quantity = " + client.getQuantityToRemove().get(index) + " WHERE id = " + i;
            statement.addBatch(sql);
        }

        statement.executeBatch();
        connection.close();
    }

    public static void updateStock(Client client) throws SQLException {
        Connection connection = ConnectToDatabase.connectToDatabase();
        Statement statement = connection.createStatement();
        String sql;
        System.out.println("Updating grocery stock");

        for(Cart c : client.getCart()) {
            sql = "UPDATE groceries SET quantity = quantity - " + c.quantity + " WHERE id = " + c.id;
            statement.addBatch(sql);
            System.out.println("Grocery ID: " + c.id + "Quantity removed: " + c.quantity);
        }


        statement.executeBatch();
        connection.close();
    }

    public static void updateOrderTotal(Client client) throws SQLException {
        Connection connection = ConnectToDatabase.connectToDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Cart");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Cart> items = new ArrayList<>();
        double orderTotal = 0;
        System.out.println("Updating order total");


        resultSet.next();

        while (!resultSet.isAfterLast()) {
            Cart cart = new Cart();
            cart.id = resultSet.getInt(1);
            cart.name = resultSet.getString(2);
            cart.type = resultSet.getString(3);
            cart.price = Double.toString(resultSet.getDouble(4));
            cart.quantity = resultSet.getInt(5);
            orderTotal += (Double.parseDouble(cart.price) * cart.quantity);
            resultSet.next();
            items.add(cart);
        }

        client.setCart(items);
        client.setOrderTotal(orderTotal);

        connection.close();
    }

    public static void addRemovedItemToStock(Client client) throws SQLException {
        Connection connection = ConnectToDatabase.connectToDatabase();
        Statement statement = connection.createStatement();
        String sql;

        for(int i : client.getGroceriesToRemove()) {
            int index = client.getGroceriesToRemove().indexOf(i);
            statement.addBatch("UPDATE groceries SET quantity = quantity + " + client.getQuantityToRemove().get(index));
        }
        statement.executeBatch();
        connection.close();
    }

    public static void viewCart(Client client) throws SQLException {
        Connection connection = ConnectToDatabase.connectToDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Cart");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Cart> cart = new ArrayList<>();
        resultSet.next();

        while(!resultSet.isAfterLast()) {
            Cart temp = new Cart();
            temp.id = resultSet.getInt(1);
            temp.name = resultSet.getString(2);
            temp.type = resultSet.getString(3);
            temp.price = Double.toString(resultSet.getDouble(4));
            temp.quantity = resultSet.getInt(5);
            cart.add(temp);
            resultSet.next();
        }

        client.setCart(cart);

        connection.close();
    }
}
