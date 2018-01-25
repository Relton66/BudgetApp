package budgetapp.controller;

import budgetapp.dao.BudgetDAO;
import budgetapp.dao.CategoryBudgetDAO;
import budgetapp.dao.CategoryDAO;
import budgetapp.model.Budget;
import budgetapp.model.Category;
import budgetapp.model.CategoryBudgetTableEntry;
import budgetapp.util.CommonUtil;
import budgetapp.util.StringUtil;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    /** The save button. */
    @FXML
    private Button saveBtn;       
    /** The list of table entries. */
    private final List<CategoryBudgetTableEntry> categoryTableList = new ArrayList();   
    /** The HomeController instance. */
    private HomeController homeController;    
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
        loadExistingBudgets();       
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
            int budgetId = BudgetDAO.findBudgetId(previousBudgetList.getSelectionModel()
                    .getSelectedItem().toString());
            // Get all categories for budget ID
            List<Category> categoryList = CategoryDAO.getCategoriesByBudgetId(budgetId);
            // Only add if they aren't in the table already
            for (Category category : categoryList) {
                if(!categoryAlreadyExists(category.getCategoryName())) {
                    addToCategoryTable(category.getCategoryName(), "0.00");
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
        ((CategoryBudgetTableEntry) t.getTableView().getItems().get(
            t.getTablePosition().getRow())
            ).setBudgetStarting(StringUtil.convertToDollarFormat(t.getNewValue()));
    }
            
    /**
     * This method handles the Add Category button action.
     */
    public void onAddCategory() {
        if(categoryInputIsValid()) {
            addToCategoryTable(categoryField.getText(), budgetAmountField.getText());                     
            categoryField.clear();
            budgetAmountField.clear();            
            categoryStatusMessage.setText("");
        }
    }
    
    /**
     * This method adds a category and budget to the table.
     * 
     * @param categoryName - the category name
     * @param budgetStarting - the budget starting amount
     */
    private void addToCategoryTable(String categoryName, String budgetStarting) {
        categoryTableList.add(new CategoryBudgetTableEntry(categoryName, 
                StringUtil.convertToDollarFormat(budgetStarting.replace(",", "")))); 
        populateCategoryBudgetTable();
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
        for(CategoryBudgetTableEntry entry : categoryTableList) {
            currentCategoryList.add(entry.getCategoryName());
        }
        return currentCategoryList.contains(categoryName);
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
        int budgetId = BudgetDAO.saveNewBudget(budget);           
        saveNewCategories();
        saveCategoryBudgets(budgetId);
        homeController.refreshBudgetList(budgetId, budget);
        saveBtn.setDisable(true);
    }
    
    /**
     * This method saves the data in the category budget table.
     * 
     * @param budgetId - the budget ID   
     */
    private void saveCategoryBudgets(int budgetId) {
        for(CategoryBudgetTableEntry entry : categoryTableList) {
            CategoryBudgetDAO.saveCategoryBudget(budgetId, CategoryDAO.findCategoryByName(
                    entry.getCategoryName()).getCategoryId(), 
                    StringUtil.convertFromDollarFormat(entry.getBudgetStarting()));
        }
    }
    
    /**
     * This method will save any new categories.     
     */
    private void saveNewCategories() {
        List<String> existingCategories = CategoryDAO.getExistingCategories();        
        for(CategoryBudgetTableEntry entry : categoryTableList) {
            if(!existingCategories.contains(entry.getCategoryName())) {
                CategoryDAO.saveCategory(entry.getCategoryName());
            }
        }        
    }
    
    /**
     * This method handles the Save button action.     
     */
    public void onSaveAction() {
        if(inputIsValid()) {
            saveBudget();
            CommonUtil.displayMessage(budgetStatusMessage, "Budget saved!", true);
            categoryStatusMessage.setText("");            
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
            CommonUtil.displayMessage(budgetStatusMessage, "Budget name must be less than 51 characters.", false);
        } else if(!budgetNameIsUnique(nameField.getText())) {
            CommonUtil.displayMessage(budgetStatusMessage, "Budget name already exists.", false);         
        } else if(!StringUtil.isValidDollarAmount(startBalance.getText())) {
            CommonUtil.displayMessage(budgetStatusMessage, "Starting balance entered has incorrect format.", false);
        } else if(startBalance.getText().length() > 50) {
            CommonUtil.displayMessage(budgetStatusMessage, "Starting balance must be less than 51 characters.", false);
        } else if(startDate.getValue() == null) {           
            CommonUtil.displayMessage(budgetStatusMessage, "Start date is missing.", false);
        } else if(endDate.getValue() == null) {           
            CommonUtil.displayMessage(budgetStatusMessage, "End date is missing.", false);
        } else if(endDate.getValue().isBefore(startDate.getValue())) {
            CommonUtil.displayMessage(budgetStatusMessage, "End date cannot be equal to or earlier than start date.", false);
        } else if(categoryTableIsInvalid()) {
            CommonUtil.displayMessage(budgetStatusMessage, "There's an invalid entry in the category table.", false);
        } else if(exceededBudgetBalance()) {
            CommonUtil.displayMessage(budgetStatusMessage, "Category budget amount(s) exceed starting balance.", false);
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
        return !budgetNamesList.contains(budgetName.toUpperCase());        
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
    }
}