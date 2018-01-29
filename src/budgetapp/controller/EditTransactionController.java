package budgetapp.controller;

import budgetapp.dao.BudgetDAO;
import budgetapp.dao.CategoryBudgetDAO;
import budgetapp.dao.CategoryDAO;
import budgetapp.dao.MethodDAO;
import budgetapp.dao.TransactionDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Category;
import budgetapp.model.Transaction;
import budgetapp.model.TransactionTableEntry;
import budgetapp.util.CommonUtil;
import budgetapp.util.Constants;
import budgetapp.util.StringUtil;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * The Edit Transaction controller class.
 */
public class EditTransactionController implements Initializable {

    /** The edit transaction border pane. */
    @FXML
    private BorderPane editTransBorderPane;
    /** The date field. */
    @FXML
    private DatePicker dateField;
    /** The existing vendor list. */
    @FXML
    private ChoiceBox existingVendorList;
    /** The new vendor field. */
    @FXML
    private TextField newVendorField;
    /** The amount field. */
    @FXML
    private TextField amountField;
    /** The income check box. */
    @FXML
    private CheckBox incomeCheckBox;
    /** The category list. */
    @FXML
    private ChoiceBox categoryList;
    /** The method list. */
    @FXML
    private ChoiceBox methodList;
    /** The status message. */
    @FXML
    private Label statusMessage;
    /** The list of category entities. */
    private List<Category> categoryEntityList = new ArrayList<>();
    /** The transaction ID. */
    private int transactionId;
    /** The budget ID. */
    private int budgetId;
    /** The original amount. */
    private double originalAmount;
    /** The original category ID. */
    private int originalCategoryId;
    /** The original income flag value. */
    private boolean originalIncomeValue;
     /** The HomeController instance. */
    private HomeController homeController;
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EditTransactionController.class);
    
    /**
     * This method is called when class is loaded.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // When user types in here, it's implied they aren't wanting to use the
        // existing vendor field so change it to default.
        newVendorField.textProperty().addListener((observable, oldValue, newValue) -> {
            existingVendorList.getSelectionModel().selectFirst();
        });
    }    
    
    /**
     * This method handles the submit button action.
     */
    public void onSubmitBtnAction() {
        if(inputIsValid()) {
            updateTransaction();
        }        
    }
    
    /**
     * This method saves the edit and refreshes the tables.
     */
    private void updateTransaction() {
        double newAmount = StringUtil.convertFromDollarFormat(amountField.getText());
        String categoryName = categoryList.getSelectionModel().getSelectedItem().toString();
        int categoryId = findCategoryId(categoryName);
        saveTransaction(newAmount, categoryName);
        updateBalances(originalAmount, newAmount, categoryId);
        homeController.refreshTablesAfterEdit();
        CommonUtil.displayMessage(statusMessage, "Transaction updated successfully", true);
    }
    
    /**
     * This method updates the budget and category budget balances.
     * 
     * @param originalAmount - the original amount
     * @param newAmount - the new amount
     * @param categoryId - the category ID
     */
    private void updateBalances(double originalAmount, double newAmount, int categoryId) {
        boolean newIncomeValue = incomeCheckBox.isSelected();
        double amountChanged = Math.abs(originalAmount - newAmount);
        boolean needToUpdateBalances = (amountChanged > 0) || (originalIncomeValue != newIncomeValue);
        if(needToUpdateBalances) {            
            // Need to rollback, essentially delete, old transaction amounts.
            BudgetDAO.rollbackBudgetBalance(budgetId, originalIncomeValue, originalAmount);           
            // Now apply new amounts
            BudgetDAO.updateBudgetBalance(budgetId, newIncomeValue, newAmount);
            homeController.updateBalanceAfterEdit();
        }
               
        if(categoryId == originalCategoryId) {
            // Original category needs to rollback amount changed
            if(needToUpdateBalances) {
                CategoryBudgetDAO.rollbackBalance(budgetId, originalIncomeValue, originalAmount, originalCategoryId);
                CategoryBudgetDAO.updateCategoryBalance(budgetId, originalCategoryId, newIncomeValue, newAmount);
            }
        } else {
            // Original category needs to rollback original amount
            CategoryBudgetDAO.rollbackBalance(budgetId, originalIncomeValue, originalAmount, originalCategoryId);
            // New category needs to add new amount
            CategoryBudgetDAO.updateCategoryBalance(budgetId, categoryId, newIncomeValue, newAmount);
        }
    }
    
    /**
     * This method saves the transaction only.
     * 
     * @param newAmount - the new amount value
     * @param categoryName - the category name
     */
    private void saveTransaction(double newAmount, String categoryName) {
        Transaction transaction = CommonUtil.generateTransactionModel(newAmount,
            incomeCheckBox.isSelected(), Date.valueOf(dateField.getValue()), budgetId,
            methodList, existingVendorList.getSelectionModel().getSelectedItem().toString(),
            newVendorField.getText(), categoryName);
        transaction.setTransactionId(transactionId);
        TransactionDAO.updateTransaction(transaction);
    }
    
    /**
     * This method finds the current category ID selected.
     * 
     * @param categoryName - the current category name selected
     * @return the current category ID selected
     */
    private int findCategoryId(String categoryName) {
        int categoryId = 0;
        for(Category category : categoryEntityList) { 
            if(categoryName.equalsIgnoreCase(category.getCategoryName())) {
                categoryId = category.getCategoryId();
                break;
            }
        }
        return categoryId;
    }
    
    /**
     * This method sets the homeController instance.
     * 
     * @param homeController - the homeController
     */
    public void setHomeContoller(HomeController homeController) {
        this.homeController = homeController;
    }
    
    /**
     * This method handles the close button action.
     */
    public void onCloseBtnAction() {
        Stage stage = (Stage) editTransBorderPane.getScene().getWindow();
        stage.close();
    }
    
    /**
     * This method populates the edit dialog fields.
     * 
     * @param budgetId - the budget ID
     * @param transactionEntry - the transaction entity
     */
    public void populateFields(int budgetId, TransactionTableEntry transactionEntry) {
        this.budgetId = budgetId;
        transactionId = Integer.valueOf(transactionEntry.getTransactionId());
        Transaction transaction = TransactionDAO.getTransaction(transactionId);
        dateField.setValue(transaction.getTransDate().toLocalDate());
        amountField.setText(String.format("%.2f", transaction.getAmount()));
        originalAmount = transaction.getAmount();
        incomeCheckBox.setSelected(transaction.getIncome());
        originalIncomeValue = transaction.getIncome();
        loadVendorList(transactionEntry.getVendorName());
        loadCategoryList(budgetId, transactionEntry.getCategoryName());
        loadMethodList(transactionEntry.getMethodType());
    }
    
    /**
     * This method builds the vendor list and selects the current one.
     * 
     * @param vendorName - the vendor name
     */
    private void loadVendorList(String vendorName) {
        ObservableList<String> vendorsList = FXCollections.observableArrayList();
        vendorsList.add(Constants.LIST_NONE_OPTION);
        vendorsList.addAll(VendorDAO.getExistingVendors());
        existingVendorList.setItems(vendorsList);
        existingVendorList.getSelectionModel().selectFirst();
        for(int i=0; i<existingVendorList.getItems().size(); i++) {
            if(vendorName.equalsIgnoreCase(existingVendorList.getItems().get(i).toString())) {
                existingVendorList.getSelectionModel().select(i);
                break;
            }
        }
    }
    
    /**
     * This method builds the category list and selects the current one.
     * 
     * @param budgetId - the budget ID
     * @param currentCategory - the current category
     */
    private void loadCategoryList(int budgetId, String currentCategory) {
        int index = 0;
        categoryEntityList = CategoryDAO.getCategoriesByBudgetId(budgetId);
        // We need a list of strings for the choicebox
        ObservableList<String> categoryNameList = FXCollections.observableArrayList(); 
        for(int i=0; i < categoryEntityList.size(); i++) {
            categoryNameList.add(categoryEntityList.get(i).getCategoryName());
            if(currentCategory.equalsIgnoreCase(categoryEntityList.get(i).getCategoryName())) {
                index = i;
                originalCategoryId = categoryEntityList.get(i).getCategoryId();
            }
        }
        categoryList.setItems(categoryNameList);
        categoryList.getSelectionModel().select(index);    
    }
    
    /**
     * This method builds the method types list and selects the current one.
     * 
     * @param currentMethod - the current method
     */
    private void loadMethodList(String currentMethod) {
        ObservableList<String> activeMethodTypesList = FXCollections.observableArrayList();
        activeMethodTypesList.add(Constants.LIST_NONE_OPTION);
        activeMethodTypesList.addAll(MethodDAO.getActiveMethodTypes());
        methodList.setItems(activeMethodTypesList);
        if(currentMethod != null) {
            for(int i=0; i<methodList.getItems().size(); i++) {
                if(currentMethod.equalsIgnoreCase(methodList.getItems().get(i).toString())) {
                    methodList.getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            methodList.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * This method determines if the input is valid.
     * 
     * @return true if all input is valid
     */
    private boolean inputIsValid() {
        boolean validInput = false;
        
        if(!StringUtil.isValidDollarAmount(amountField.getText())) {                        
            CommonUtil.displayMessage(statusMessage, "Amount entered has incorrect format.", false);
        } else if(dateField.getValue() == null) {           
            CommonUtil.displayMessage(statusMessage, "Date is missing.", false);
        } else if(Constants.LIST_NONE_OPTION.equalsIgnoreCase(existingVendorList.getValue().toString())) {
            if(!StringUtil.isAlphaNumeric(newVendorField.getText())) {
                CommonUtil.displayMessage(statusMessage, "New vendor name must be alphanumeric.", false);
            } else if(newVendorField.getText().length() > 50) {
                CommonUtil.displayMessage(statusMessage,
                        "New vendor name must be less than 51 characters.", false);                
            } else if(VendorDAO.vendorNameAlreadyExists(newVendorField.getText())) {
                CommonUtil.displayMessage(statusMessage, "Vendor already exists.", false);
            } else {
                validInput = true;
            }
        } else {
            validInput = true;
        }
        return validInput;
    }
}
