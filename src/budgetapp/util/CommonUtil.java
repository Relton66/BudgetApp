package budgetapp.util;

import budgetapp.dao.MethodDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Transaction;
import budgetapp.model.Vendor;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * This class contains common methods needed throughout the project.
 */
public class CommonUtil {
    
    /**
     * This method sets a status message for a label.
     * 
     * @param label - the label to set
     * @param message - the message to display
     * @param success - if true, message is in green, otherwise red
     */
    public final static void displayMessage(Label label, String message, boolean success) {
        label.setText(message);
        label.setTextFill(success? Color.GREEN : Color.RED);
        label.setAlignment(Pos.CENTER);
    }
    
    /**
     * This method builds the Transaction model.
     * 
     * @param amount - the amount
     * @param isIncome - true if transaction is income
     * @param isRecurring - true if transaction is recurring
     * @param transDate - the transaction date
     * @param budgetId - the budget ID
     * @param methodList - the method list
     * @param vendorName - the vendor name
     * @param newVendorName - the new vendor name
     * @param categoryName - the category name
     * @param comments - the comments
     * @return the transaction model with all data set
     */   
    public static Transaction generateTransactionModel(double amount, boolean isIncome, boolean isRecurring, 
            Date transDate, int budgetId, ChoiceBox methodList, String vendorName, String newVendorName, 
            String categoryName, String comments) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setIncome(isIncome);
        transaction.setRecurring(isRecurring);
        transaction.setTransDate(transDate);
        transaction.setBudgetId(budgetId);
        transaction.setComments(comments);
        transaction.setVendorId(processVendorId(vendorName, newVendorName, categoryName));
        if(methodList.getValue() != null) {
            transaction.setMethodId(MethodDAO.findMethodId(methodList.getSelectionModel()
                    .getSelectedItem().toString()));
        }
        return transaction;
    }
    
    /**
     * This method determines the vendor ID.
     * 
     * @param vendorName - the vendor name
     * @param newVendorName - the new vendor name
     * @param categoryName - the category name
     * @return the vendor ID
     */   
    private static int processVendorId(String vendorName, String newVendorName, String categoryName) {        
        int vendorId;
        if(Constants.LIST_NONE_OPTION.equalsIgnoreCase(vendorName)) {
            // Need to save new vendor first.
            vendorId = VendorDAO.saveVendor(newVendorName, categoryName);
        } else {
            // Vendor already exists so we just retrieve the ID.
            Vendor vendor = VendorDAO.findVendorByName(vendorName);
            vendorId = vendor.getVendorId();
        }
        return vendorId;
    }
    
    /**
     * This method loads the existing vendors. 
     * 
     * @param existingVendorList - the existing vendor list
     * @param addNone - if true, add None option
     */    
    public static void loadExistingVendors(ChoiceBox existingVendorList, boolean addNone) {
        ObservableList<String> vendorsList = FXCollections.observableArrayList();
        if(addNone) {
            vendorsList.add(Constants.LIST_NONE_OPTION);
        }
        vendorsList.addAll(VendorDAO.getExistingVendorNames());
        existingVendorList.setItems(vendorsList);
        existingVendorList.getSelectionModel().selectFirst();
    }
}
