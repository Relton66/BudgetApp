package budgetapp.controller;

import budgetapp.dao.BudgetDAO;
import budgetapp.dao.CategoryBudgetDAO;
import budgetapp.dao.CategoryDAO;
import budgetapp.dao.TransactionDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Budget;
import budgetapp.model.Category;
import budgetapp.model.CategoryBudgetTableEntry;
import budgetapp.model.Transaction;
import budgetapp.util.CommonUtil;
import budgetapp.util.StringUtil;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

/**
 * The Budget controller class.
 */
public class BudgetController implements Initializable {
    
    /** The border pane. */
    @FXML
    private BorderPane budgetBorderPane;
    /** The budget name. */
    @FXML
    private TextField nameField;
    /** The starting balance. */
    @FXML
    private TextField startBalance;
    /** The start date. */
    @FXML
    private DatePicker startDate;
    /** The end date. */
    @FXML
    private DatePicker endDate;
    /** The category field. */
    @FXML
    private TextField categoryField;
    /** The budget amount field. */
    @FXML
    private TextField budgetAmountField;
    /** The category budget table. */
    @FXML
    private TableView categoryBudgetTable;
    /** The category ID column. */
    @FXML
    private TableColumn categoryIdColumn;
    /** The category column. */
    @FXML
    private TableColumn categoryColumn;
    /** The budget amount column. */
    @FXML
    private TableColumn budgetStartingCol;
    /** The budget status message. */
    @FXML
    private Label budgetStatusMessage;
    /** The category status message. */
    @FXML
    private Label categoryStatusMessage;
    /** The previous budget list. */
    @FXML
    private ChoiceBox previousBudgetList;
    /** The create recurring transactions check box. */
    @FXML
    private CheckBox createRecurringChkBox;
    /** The budget total field. */
    @FXML
    private TextField budgetTotalField;
    /** The budget total amount. */
    private double budgetTotalAmount=0;
    /** The list of table entries. */
    private List<CategoryBudgetTableEntry> categoryTableList = new ArrayList();   
    /** The HomeController instance. */
    private HomeController homeController;
    /** The originalBudgetName used for edit budget. */
    private String originalBudgetName;
    /** The original budget starting balance. */
    private Double originalBudgetStartBalance;
    /** The list of table entries for edit. */
    private final List<CategoryBudgetTableEntry> originalCategoryList = new ArrayList();
    /** The budget ID that is being edited. */
    private int editedBudgetId = 0;
    /** The edit budget flag. */
    private boolean isEdit = false;
    /** The loaded budget ID. */
    private int loadedBudgetId;
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BudgetController.class);
        
    /**
     * This method is called when class is loaded.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {         
        budgetTotalField.setEditable(false);
        budgetTotalField.setAlignment(Pos.CENTER_RIGHT);
        loadExistingBudgets();
        
        // When user types in here, it's implied they aren't wanting to use the
        // existing vendor field so change it to default.
        startBalance.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateBudgetTotal();
        });
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
     * This method loads the existing budgets choice box.     
     */
    private void loadExistingBudgets() {
        ObservableList<String> existingBudgetNamesList = FXCollections.observableArrayList();        
        existingBudgetNamesList.addAll(BudgetDAO.getExistingBudgetNames());
        previousBudgetList.setItems(existingBudgetNamesList);
    }
    
    /**
     * This method handles the load budgets button action.     
     */
    public void onLoadBudgets() {
        //Retrieve all categories for budget ID.
        if(previousBudgetList.getSelectionModel().getSelectedItem() == null) {
            CommonUtil.displayMessage(categoryStatusMessage, "No budget was selected.", false);
        } else {
            loadedBudgetId = BudgetDAO.findBudgetId(previousBudgetList.getSelectionModel()
                    .getSelectedItem().toString());
            // Get all categories for budget ID
            List<Category> categoryList = CategoryDAO.getCategoriesByBudgetId(loadedBudgetId);
            // Only add if they aren't in the table already
            for (Category category : categoryList) {
                if(!categoryAlreadyExists(category.getCategoryName())) {
                    String catStartBalance = CategoryBudgetDAO.getCategoryStartBalance(
                            loadedBudgetId, category.getCategoryId());
                    addToCategoryTable(category.getCategoryName(), catStartBalance);
                }
            }            
        }
    }
    
    /**
     * This handles the action when user edits a category table cell.
     *
     * @param t - the CellEditEvent instance
     */
    public void onCategoryEditCommit(CellEditEvent<CategoryBudgetTableEntry, String> t) {
        ((CategoryBudgetTableEntry) t.getTableView().getItems().get(
            t.getTablePosition().getRow())
            ).setCategoryName(t.getNewValue());
    }
    
    /**
     * This handles the action when user edits a budget amount table cell.
     *
     * @param t - the CellEditEvent instance
     */
    public void onBudgetAmountEditCommit(CellEditEvent<CategoryBudgetTableEntry, String> t) {        
        String newValue = t.getNewValue().replaceFirst("\\$", "");
        if(StringUtil.isValidDollarAmount(newValue)) {
            newValue = Double.toString(StringUtil.convertFromDollarFormat(newValue));
            newValue = StringUtil.convertToDollarFormat(newValue);
            ((CategoryBudgetTableEntry) t.getTableView().getItems().get(
                t.getTablePosition().getRow())
                ).setBudgetStarting(newValue);
            calculateBudgetTotal();
        } else {
            CommonUtil.displayMessage(budgetStatusMessage, "Budget amount is invalid.", false);  
        }       
    }
            
    /**
     * This method handles the Add Category button action.
     */
    public void onAddCategory() {
        resetStatusMessages();
        if(categoryInputIsValid()) {
            addToCategoryTable(categoryField.getText(), budgetAmountField.getText());                     
            categoryField.clear();
            budgetAmountField.clear();            
        }
    }
    
    /**
     * This method clears the status messages.
     */
    private void resetStatusMessages() {
        categoryStatusMessage.setText("");
        budgetStatusMessage.setText("");
    }
    
    /**
     * This method adds a category and budget to the table.
     * 
     * @param categoryName - the category name
     * @param budgetStarting - the budget starting amount
     */
    private void addToCategoryTable(String categoryName, String budgetStarting) {
        CategoryBudgetTableEntry categoryBudget = new CategoryBudgetTableEntry();
        categoryBudget.setCategoryName(categoryName);
        categoryBudget.setBudgetStarting(StringUtil.convertToDollarFormat(budgetStarting.replace(",", "")));
        categoryTableList.add(categoryBudget); 
        populateCategoryBudgetTable();
    }
    
    /**
     * This method updates the budget total label
     */
    private void calculateBudgetTotal() {
        budgetTotalAmount = 0;        
        if(StringUtil.isValidDollarAmount(startBalance.getText())) {
            categoryTableList.forEach((categoryBudget) -> {
                budgetTotalAmount += StringUtil.convertFromDollarFormat(categoryBudget.getBudgetStarting());
            });
            if(budgetTotalAmount >  StringUtil.convertFromDollarFormat(startBalance.getText())) {
                budgetTotalField.setStyle("-fx-text-inner-color: red;");
            } else {
                budgetTotalField.setStyle("-fx-text-inner-color: green;");
            }
            budgetTotalField.setText(StringUtil.convertToDollarFormat(Double.toString(budgetTotalAmount)));  
        }
    }
    
    /**
     * This method determines if category and budget amount inputs are valid.
     */
    private boolean categoryInputIsValid() {
        boolean isValid = false;
        if(!StringUtil.isAlphaNumeric(categoryField.getText())) {
            CommonUtil.displayMessage(categoryStatusMessage, "Category name must be alphanumeric.", false);
        } else if(!StringUtil.isValidDollarAmount(budgetAmountField.getText())) {
            CommonUtil.displayMessage(categoryStatusMessage, "Budget amount is not a valid format.", false);
        } else if(categoryAlreadyExists(categoryField.getText())) {
            CommonUtil.displayMessage(categoryStatusMessage, "Category is already in the table.", false);
        } else {
            isValid = true;
        }
        return isValid;
    }
    
    /**
     * This method determines if a category name is already in the table.
     * 
     * @param categoryName - the category name
     * @returns true if category already exists in the table
     */
    private boolean categoryAlreadyExists(String categoryName) {
        List<String> currentCategoryList = new ArrayList<>();
        categoryTableList.forEach((entry) -> {
            currentCategoryList.add(entry.getCategoryName().toUpperCase(Locale.ENGLISH));
        });
        return currentCategoryList.contains(categoryName.toUpperCase(Locale.ENGLISH));
    }
    
    /**
     * This method saves the budget.     
     */
    public void saveBudget() {
        Budget budget = new Budget();
        budget.setBudgetName(nameField.getText());
        budget.setStartDate(Date.valueOf(startDate.getValue()));
        budget.setEndDate(Date.valueOf(endDate.getValue()));
        budget.setStartBalance(StringUtil.convertFromDollarFormat(startBalance.getText()));
        if(isEdit) {
            budget.setBudgetId(editedBudgetId);
            BudgetDAO.updateBudget(budget, originalBudgetStartBalance);
            saveNewCategories();
            updateCategoryBudgets(editedBudgetId);
            homeController.loadExistingBudgets(true);            
            homeController.populateCategoryBudgetTable(editedBudgetId);
            setEditVariables(editedBudgetId, budget.getBudgetName(), budget.getStartBalance());            
        } else {        
            int budgetId = BudgetDAO.saveNewBudget(budget);           
            saveNewCategories();
            saveCategoryBudgets(budgetId);
            if(createRecurringChkBox.isSelected()) {
                createRecurringTransactions(budgetId, loadedBudgetId);
                createRecurringChkBox.setDisable(true);
            } else {
                homeController.refreshBudgetList(budgetId, budget);
            }
            setEditVariables(budgetId, budget.getBudgetName(), budget.getStartBalance());
            Stage stage = (Stage) budgetBorderPane.getScene().getWindow();
            stage.setTitle("Edit Budget");
        }
        populateCategoryBudgetTable();
    }
    
    /**
     * This method creates the recurring transactions to insert into the
     * new budget.
     * 
     * @param budgetId - the new budget ID
     * @param loadedBudgetId - the budget ID to get recurring transactions from
     */
    private void createRecurringTransactions(int budgetId, int loadedBudgetId) {
        List<Transaction> transList = TransactionDAO.getRecurringTransactions(loadedBudgetId);
        for(Transaction transaction : transList) {
            transaction.setBudgetId(budgetId);
            transaction.setTransDate(Date.valueOf(transaction.getTransDate().toLocalDate().plusMonths(1)));
            TransactionDAO.saveTransaction(transaction);        
            BudgetDAO.updateBudgetBalance(budgetId, transaction.getIncome(),
                transaction.getAmount());
            // Update category budget balance
            int categoryId = VendorDAO.findCategoryByVendorId(transaction.getVendorId()).getCategoryId();
            CategoryBudgetDAO.updateCategoryBalance(budgetId, categoryId, transaction.getIncome(),
                transaction.getAmount());
        }
        homeController.loadExistingBudgets(true); 
    }
    
    /**
     * This method saves the data in the category budget table.
     * 
     * @param budgetId - the budget ID   
     */
    private void saveCategoryBudgets(int budgetId) {
        categoryTableList.forEach((entry) -> {
            CategoryBudgetDAO.saveCategoryBudget(budgetId, CategoryDAO.findCategoryByName(
                    entry.getCategoryName()).getCategoryId(),
                    StringUtil.convertFromDollarFormat(entry.getBudgetStarting()));
        });
    }
    
    /**
     * This method updates the data in the category budget table.
     * 
     * @param budgetId - the budget ID   
     */
    private void updateCategoryBudgets(int budgetId) {        
        for(CategoryBudgetTableEntry currentEntry : categoryTableList) {
            boolean needToAdd = true;
            for(CategoryBudgetTableEntry existingEntry : originalCategoryList) {
                if(currentEntry.getCategoryName().equalsIgnoreCase(existingEntry.getCategoryName())) {
                    needToAdd = false;
                    updateBalances(budgetId, currentEntry.getCategoryName(),
                        currentEntry.getBudgetStarting(), existingEntry.getBudgetStarting());
                    break;
                }
            }
            if(needToAdd) {            
                CategoryBudgetDAO.saveCategoryBudget(budgetId, CategoryDAO.findCategoryByName(
                    currentEntry.getCategoryName()).getCategoryId(), StringUtil.convertFromDollarFormat(
                    currentEntry.getBudgetStarting()));
            }
        }
    }
    
    /**
     * This method updates the start and current balances for category budgets.
     * 
     * @param budgetId - the budget ID
     * @param categoryName - the category name
     * @param currentStartAmount - the current start amount
     * @param existingStartAmount - the existing start amount
     */
    private void updateBalances(int budgetId, String categoryName, String currentStartAmount,
                            String existingStartAmount) {
        double currentAmount = StringUtil.convertFromDollarFormat(currentStartAmount);
        double existingAmount = StringUtil.convertFromDollarFormat(existingStartAmount);
        boolean amountIncreased = existingAmount < currentAmount;
        double currentBalanceAdjustment;
        if(amountIncreased) {
            currentBalanceAdjustment = currentAmount - existingAmount;
        } else {
            currentBalanceAdjustment = existingAmount - currentAmount;
        }
        int categoryId = CategoryDAO.findCategoryByName(categoryName).getCategoryId();
        if(currentAmount != existingAmount) {
            CategoryBudgetDAO.updateStartingBalance(budgetId, categoryId, currentAmount,
                    currentBalanceAdjustment, amountIncreased);
        }   
    }
    
    /**
     * This method will save new categories or update an existing one
     * with the new name.
     */
    private void saveNewCategories() {
        List<String> existingCategories = CategoryDAO.getExistingCategoryNames();
        for(CategoryBudgetTableEntry entry : categoryTableList) {
            // First we make sure the current entry doesn't exist in our category list
            if(!existingCategories.contains(entry.getCategoryName())) {
                // If the ID is not present in entry, it's a brand new category
                CategoryDAO.saveCategory(entry.getCategoryName());
            }
        }
        // Need to loop through original and if not in current, delete it
        for(CategoryBudgetTableEntry originalEntry : originalCategoryList) {
            boolean wasDeleted = true;
            for(CategoryBudgetTableEntry newEntry : categoryTableList) {
                if(originalEntry.getCategoryName().equals(newEntry.getCategoryName())) {
                    wasDeleted = false;
                    break;
                }
            }
            if(wasDeleted) {
                CategoryBudgetDAO.deleteCategoryBudget(editedBudgetId, 
                    Integer.parseInt(originalEntry.getCategoryId()));
            }
        }
    }
    
    /**
     * This method handles the Save button action.     
     */
    public void onSaveAction() {
        resetStatusMessages();
        if(inputIsValid()) {
            saveBudget();
            CommonUtil.displayMessage(budgetStatusMessage, "Budget saved!", true);          
        }
    }
    /** 
     * This method handles the Close button action.
     */
    public void onCloseAction() {
        Stage stage = (Stage) budgetBorderPane.getScene().getWindow();
        stage.close();
    }
    
    /**
     * This method determines if all input is valid.
     * 
     * @return true if all input is valid    
     */
    private boolean inputIsValid() {
        boolean isValid = false;        
        if(!StringUtil.isAlphaNumeric(nameField.getText())) {
            CommonUtil.displayMessage(budgetStatusMessage, "Budget name must be alphanumeric.", false);
        } else if(nameField.getText().length() > 50) {
            CommonUtil.displayMessage(budgetStatusMessage, "Budget name cannot be more than 50 characters.", false);
        } else if(!budgetNameIsUnique(nameField.getText())) {
            CommonUtil.displayMessage(budgetStatusMessage, "Budget name already exists.", false);         
        } else if(!StringUtil.isValidDollarAmount(startBalance.getText())) {
            CommonUtil.displayMessage(budgetStatusMessage, "Starting balance entered has incorrect format.", false);
        } else if(startBalance.getText().length() > 50) {
            CommonUtil.displayMessage(budgetStatusMessage, "Starting balance cannot be more than 50 characters.", false);
        } else if(startDate.getValue() == null) {           
            CommonUtil.displayMessage(budgetStatusMessage, "Start date is missing.", false);
        } else if(endDate.getValue() == null) {           
            CommonUtil.displayMessage(budgetStatusMessage, "End date is missing.", false);
        } else if(endDate.getValue().isBefore(startDate.getValue())) {
            CommonUtil.displayMessage(budgetStatusMessage, "End date must be after start date.", false);
        } else if(categoryTableIsInvalid()) {
            CommonUtil.displayMessage(budgetStatusMessage, "There's an invalid entry in the category table.", false);
        } else if(exceededBudgetBalance()) {
            CommonUtil.displayMessage(budgetStatusMessage, "Category budget amount(s) exceed starting balance.", false);
        } else if(createRecurringChkBox.isSelected() && previousBudgetList.getSelectionModel().getSelectedItem() == null) {
            CommonUtil.displayMessage(categoryStatusMessage, "No budget selected for Create Recurring Transactions.", false);
        } else {
            isValid = true;
        }        
        return isValid;
    }
    
    /**
     * This method determines if budget name is unique.
     * 
     * @param budgetName - the budget name
     * @return true if budget name is unique     
     */
    private boolean budgetNameIsUnique(String budgetName) {
        List<String> budgetNamesList = BudgetDAO.getExistingBudgetNames()
                .stream().map(String::toUpperCase).collect(Collectors.toList());
        boolean isUnique = !budgetNamesList.contains(budgetName.toUpperCase(Locale.ENGLISH));        
        if(!isUnique && isEdit) {
            isUnique = originalBudgetName.toUpperCase(Locale.ENGLISH).equalsIgnoreCase(
                    budgetName.toUpperCase(Locale.ENGLISH));
        }
        return isUnique;
    }
    
    /**
     * This method determines if the sum of category budget amounts exceeds
     * the starting balance of the budget.
     * 
     * @return true if the category budget amounts exceed starting balance
     */
    private boolean exceededBudgetBalance() {
        Double remainingBalance = Double.valueOf(startBalance.getText().replace(",", ""));
        for(CategoryBudgetTableEntry entry : categoryTableList) {
            remainingBalance -= StringUtil.convertFromDollarFormat(entry.getBudgetStarting());
        }
        return remainingBalance < 0D;        
    }
    
    /**
     * This method determines if the category budget table
     * has any valid entries.
     */
    private boolean categoryTableIsInvalid() {
        boolean isInvalid = categoryBudgetTable.getItems().isEmpty();
        if(!isInvalid) {
            for(CategoryBudgetTableEntry row : categoryTableList) {
                if(!StringUtil.isAlphaNumeric(row.getCategoryName()) ||
                        !StringUtil.isValidDollarAmount(row.getBudgetStarting().replaceFirst("\\$", ""))) {
                    isInvalid = true;
                    break;
                }
            }
        }
        return isInvalid;
    }  
    
    /**
     * This method builds the category budget table.
     */
    private void populateCategoryBudgetTable() {
        ObservableList data = FXCollections.observableList(categoryTableList);
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory("categoryId"));
        categoryIdColumn.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());
        categoryIdColumn.setVisible(false);
        
        categoryColumn.setCellValueFactory(new PropertyValueFactory("categoryName"));
        categoryColumn.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());
        budgetStartingCol.setCellValueFactory(new PropertyValueFactory("budgetStarting"));
        budgetStartingCol.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());        
        categoryBudgetTable.setItems(data);
        categoryBudgetTable.setEditable(true);
        
        categoryBudgetTable.setRowFactory(new Callback<TableView<CategoryBudgetTableEntry>,
                                            TableRow<CategoryBudgetTableEntry>>() {  
            @Override
            public TableRow<CategoryBudgetTableEntry> call(TableView<CategoryBudgetTableEntry> tableView) {  
                final TableRow<CategoryBudgetTableEntry> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  
                final MenuItem removeMenuItem = new MenuItem("Remove Category Budget");  
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                    @Override  
                    public void handle(ActionEvent event) {  
                        categoryBudgetTable.getItems().remove(row.getItem());
                        removeFromCategoryTableList(row.getItem().getCategoryName());
                        calculateBudgetTotal();
                    }  
                });  
                contextMenu.getItems().add(removeMenuItem);  
                // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row ;  
            }  
        });
        calculateBudgetTotal();
    }
    
    /**
     * This method removes the category from our list after it has been
     * removed from the table.
     * 
     * @param categoryName - the category name
     */
    private void removeFromCategoryTableList(String categoryName) {
        for(int i=0; i < categoryTableList.size(); i++) {
            if(categoryName.equalsIgnoreCase(categoryTableList.get(i).getCategoryName())) {
                categoryTableList.remove(i);
                break;
            }
        }
    }
    
    /**
     * This method populates the dialog for editing.
     * 
     * @param budgetId - the budget ID
     */
    public void populateForEdit(int budgetId) {
        Budget budget = BudgetDAO.findBudgetById(budgetId);        
        nameField.setText(budget.getBudgetName());
        startBalance.setText(String.format("%.2f", budget.getStartBalance()));
        startDate.setValue(budget.getStartDate().toLocalDate());
        endDate.setValue(budget.getEndDate().toLocalDate());
        setEditVariables(budgetId, budget.getBudgetName(), budget.getStartBalance());
        populateCategoryBudgetTable();        
    }
    
    /**
     * This method sets up the special variables needed for the
     * edit screen.
     * 
     * @param budgetId - the budget ID
     * @param budgetName - the budget name
     * @param budgetStartBalance - the budget starting balance
     */
    private void setEditVariables(int budgetId, String budgetName, Double budgetStartBalance) {
        isEdit = true;
        originalCategoryList.clear();
        categoryTableList = CategoryBudgetDAO.getCategoryBudgetsForTable(budgetId);
        for(int i=0; i<categoryTableList.size(); i++) {
            try {
                originalCategoryList.add(categoryTableList.get(i).clone());
            } catch (CloneNotSupportedException ex) {
                LOG.error("CloneNotSupported exception in setEditVariables", ex);
            }
        }
        editedBudgetId = budgetId;
        originalBudgetName = budgetName;
        originalBudgetStartBalance = budgetStartBalance;
    }
}