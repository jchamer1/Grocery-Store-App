package edu.hfcc.grocery;

import edu.hfcc.grocery.database.GroceryCreation;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GroceryCreationTest {

    @Test
    public void testTableInput() throws SQLException {
        GroceryCreation groceryCreation = new GroceryCreation();

        System.out.println("Running tests");

        groceryCreation.createListOfVegetable();
        groceryCreation.createListOfFruits();
    }

}
