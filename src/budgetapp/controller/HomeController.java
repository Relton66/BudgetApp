package budgetapp.controller;

import budgetapp.Main;
import budgetapp.dao.BudgetDAO;
import budgetapp.dao.CategoryBudgetDAO;
import budgetapp.dao.CategoryDAO;
import budgetapp.dao.MethodDAO;
import budgetapp.dao.TransactionDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Budget;
import budgetapp.model.Category;
import budgetapp.model.CategoryBudgetTableEntry;
import budgetapp.model.Transaction;
import budgetapp.model.TransactionTableEntry;
import budgetapp.util.CommonUtil;
import budgetapp.util.Constants;
import budgetapp.util.FileUtil;
import budgetapp.util.StringUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

/**
 * The home (transaction) controller class.
 */
public class HomeController implements Initializable {
    
    /** The border pane. */
    @FXML
    private BorderPane homeBorderPane;
    /** The amount field. */
    @FXML
    private TextField amountField;
    /** The income check box. */
    @FXML
    private CheckBox incomeCheckBoxField;
    /** The recurring check box. */
    @FXML
    private CheckBox recurringCheckBoxField;
    /** The date field. */
    @FXML
    private DatePicker transDateField;
    /** The existing vendor list. */
    @FXML
    private ChoiceBox existingVendorList;
    /** The vendor category field to show what is linked. */
    @FXML
    private TextField vendorCategoryField;
    /** The new vendor field. */
    @FXML
    private TextField newVendorField;
    /** The category list. */
    @FXML
    private ChoiceBox categoryList;    
    /** The status message. */
    @FXML
    private Label statusMessage;
    /** The starting balance label. */
    @FXML
    private Label startingBalanceLabel;
    /** The current balance label. */
    @FXML
    private Label currentBalanceLabel;
    /** The budget list. */
    @FXML
    private ChoiceBox budgetList;
    /** The category tab pane. */
    @FXML
    private TabPane categoryTabPane;
    /** The budget category table. */
    @FXML
    private TableView categoryBudgetTable;
    /** The category ID column. */
    @FXML
    private TableColumn categoryIdColumn;
    /** The category column. */
    @FXML
    private TableColumn categoryColumn;
    /** The budget starting column. */
    @FXML
    private TableColumn budgetStartingCol;
    /** The budget remaining column. */
    @FXML
    private TableColumn budgetRemainingCol;
    /** The method list. */
    @FXML
    private ChoiceBox methodList;   
    /** The comments box. */
    @FXML
    private TextArea commentArea;
    /** The active budget check box. */
    @FXML
    private CheckBox currentBudgetCheckBox;    
    /** The existing budget list. */
    private List<Budget> existingBudgetList = new ArrayList<>();    
    /** The selected budget ID. */
    private int selectedBudgetId = 0;
    /** The current categories list. */
    private List<Category> currentCategoryList = new ArrayList<>();
    /** The list of items in the category budget table. */
    private List<CategoryBudgetTableEntry> categoryBudgetList = new ArrayList<>();
    /** The all transactions table. */
    private TableView allTransTable = new TableView();
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HomeController.class);
    
    /**
     * This method is called when class is loaded.  It sets up all
     * the lists and builds the tab pane and budget category table.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        loadExistingVendors();
        loadActiveMethodTypes();
        boolean budgetsExist = loadExistingBudgets(false);
        if(budgetsExist) {
            loadExistingCategories();
            // Default date to today
            transDateField.setValue(LocalDate.now());
        } else {
            // No budgets so automatically open Add Budget dialog
            try {
                onAddNewBudget();
            } catch (IOException ex) {
                LOG.error("IO exception in initialize", ex);
            }
        }
           
        // This handles the action when budget list changes
        budgetList.setOnAction((event) -> {
            if(budgetList.getSelectionModel().getSelectedItem() != null) {
                currentBudgetCheckBox.setSelected(false); 
                String newBudgetName = budgetList.getSelectionModel().getSelectedItem().toString();
                selectedBudgetId = getSelectedBudgetId(newBudgetName);
                // Now we need to find the selected budget model and get balances
                for(Budget budget : existingBudgetList) {
                    if(budget.getBudgetId() == selectedBudgetId) {
                        displayBalanceLabel(startingBalanceLabel, budget.getStartBalance());
                        displayBalanceLabel(currentBalanceLabel, budget.getCurrentBalance());
                        break;
                    }
                }            
                loadExistingCategories();
            }
        });
        
        // This handles when the vendor list changes
        existingVendorList.setOnAction((event) -> {
            if(existingVendorList.getSelectionModel().getSelectedItem() != null) {
                ObservableList<String> vendorsList = FXCollections.observableArrayList();
                String vendorName = existingVendorList.getSelectionModel().getSelectedItem().toString();
                if(!Constants.LIST_NONE_OPTION.equals(vendorName)) {
                    String categoryName = VendorDAO.findCategoryByVendorId(VendorDAO.findVendorByName(vendorName)
                            .getVendorId()).getCategoryName();
                    vendorCategoryField.setText(categoryName);                    
                } else {
                    vendorCategoryField.setText("");
                }
            }
        });
        
        // When user types in here, it's implied they aren't wanting to use the
        // existing vendor field so change it to default.
        newVendorField.textProperty().addListener((observable, oldValue, newValue) -> {
            existingVendorList.getSelectionModel().selectFirst();
            vendorCategoryField.setText("");
        });
    }
    
    /**
     * This method calls the common method to load existing vendors.
     */
    public void loadExistingVendors() {
        CommonUtil.loadExistingVendors(existingVendorList, true);
    }
    
    /**
     * This method handles the submit button action.
     */
    public void onSubmitAction() {
        if(inputIsValid()) {
            saveNewTransaction();
        }        
    }
    
    /**
     * This method handles the reset button action.
     */
    public void onResetAction() {
        resetFields(true);        
    }
    
    /**
     * This method loads the existing categories.
     */    
    public void loadExistingCategories() {
        currentCategoryList = CategoryDAO.getCategoriesByBudgetId(selectedBudgetId);
        // We need a list of strings for the choicebox
        ObservableList<String> currentCategoryNameList = FXCollections.observableArrayList(); 
        if(!currentCategoryList.isEmpty()) {
            currentCategoryList.forEach((category) -> {
                currentCategoryNameList.add(category.getCategoryName());
            });
            categoryList.setItems(currentCategoryNameList);
            categoryList.getSelectionModel().selectFirst();
            populateCategoryBudgetTable(selectedBudgetId);
            buildTransactionTabPane(selectedBudgetId, currentCategoryList);
        }
    }
    
    /**
     * This method refreshes the tables after a transaction is edited.
     */
    public void refreshTablesAfterEdit() {
        populateCategoryBudgetTable(selectedBudgetId);
        buildTransactionTabPane(selectedBudgetId, currentCategoryList);
    }
    
    /**
     * This method updates the balance label after a transaction is edited.
     */
    public void updateBalanceAfterEdit() {
        displayBalanceLabel(currentBalanceLabel, BudgetDAO.findBudgetCurrentBalance(selectedBudgetId));
    }
    
    /**
     * This method loads the existing budgets and selects the current one.
     * 
     * @param fromEdit - true if we're coming from edit budget
     * @return true if there are budgets     
     */    
    public boolean loadExistingBudgets(boolean fromEdit) {
        existingBudgetList = BudgetDAO.getExistingBudgets();
        // We only need to store the names for the choicebox, so we build a new list
        ObservableList<String> existingBudgetNamesList = FXCollections.observableArrayList();
        int currentBudgetIndex = 0;
        // If we're coming from edit, need to store the currently selected budget
        if(fromEdit && budgetList.getSelectionModel().getSelectedItem() != null) {
            currentBudgetIndex = budgetList.getSelectionModel().getSelectedIndex();
        }
        // If no budgets exist, no need to set anything else up
        if(!existingBudgetList.isEmpty()) {
            for(int i=0; i< existingBudgetList.size(); i++) {
                existingBudgetNamesList.add(existingBudgetList.get(i).getBudgetName());     
                // If we're coming from edit, don't look for currentFlag value
                if(!fromEdit && existingBudgetList.get(i).getCurrentFlag()) {                    
                    selectedBudgetId = existingBudgetList.get(i).getBudgetId();
                    currentBudgetIndex = i;
                    currentBudgetCheckBox.setSelected(true);                               
                }
            }
            budgetList.getItems().clear();
            budgetList.setItems(existingBudgetNamesList);
            // We set selection to either first one in list, or current
            budgetList.getSelectionModel().select(currentBudgetIndex);
            // If there is no current, need to set selectedBudgetId here
            selectedBudgetId = existingBudgetList.get(currentBudgetIndex).getBudgetId();
            displayBalanceLabel(startingBalanceLabel, existingBudgetList
                    .get(currentBudgetIndex).getStartBalance());
            displayBalanceLabel(currentBalanceLabel, existingBudgetList
                    .get(currentBudgetIndex).getCurrentBalance());
            populateCategoryBudgetTable(selectedBudgetId);
        }
        return !existingBudgetList.isEmpty();
    }   
    
    /**
     * This method loads all the active method types.     
     */
    public void loadActiveMethodTypes() {
        ObservableList<String> activeMethodTypesList = FXCollections.observableArrayList();
        activeMethodTypesList.add(Constants.LIST_NONE_OPTION);
        activeMethodTypesList.addAll(MethodDAO.getActiveMethodTypes());
        methodList.setItems(activeMethodTypesList);
        methodList.getSelectionModel().selectFirst();
    }
    
    /**
     * This method refreshes the budget list after a new one was added.
     * 
     * @param budgetId - the budget ID
     * @param inBudget - the budget model          
     */    
    public void refreshBudgetList(int budgetId, Budget inBudget) {        
        Budget budget = new Budget();
        budget.setBudgetId(budgetId);
        budget.setBudgetName(inBudget.getBudgetName());
        budget.setStartDate(inBudget.getStartDate());
        budget.setEndDate(inBudget.getEndDate());
        budget.setStartBalance(inBudget.getStartBalance());
        budget.setCurrentBalance(inBudget.getStartBalance());
        existingBudgetList.add(budget);
        budgetList.getItems().add(budget.getBudgetName());     
    }
    
    /**
     * This method determines if all the input fields are valid.
     * 
     * @return true if all input is valid     
     */    
    public boolean inputIsValid() {
        boolean validInput = false;
        
        if(budgetList.getValue() == null) {
            CommonUtil.displayMessage(statusMessage, "No budget was selected.", false);
        } else if(!StringUtil.isValidDollarAmount(amountField.getText())) {                        
            CommonUtil.displayMessage(statusMessage, "Amount entered has incorrect format.", false);
        } else if(transDateField.getValue() == null) {           
            CommonUtil.displayMessage(statusMessage, "Date is missing.", false);
        } else if(commentArea.getText().length() > 100) {
            CommonUtil.displayMessage(statusMessage, "Comments cannot be more than 100 characters.", false);
        } else if(Constants.LIST_NONE_OPTION.equalsIgnoreCase(existingVendorList.getValue().toString())) {
            if(!StringUtil.isAlphaNumeric(newVendorField.getText())) {
                CommonUtil.displayMessage(statusMessage, "New vendor name must be alphanumeric.", false);
            } else if(newVendorField.getText().length() > 50) {
                CommonUtil.displayMessage(statusMessage,
                        "New vendor name cannot be more than 50 characters.", false);                
            } else if(VendorDAO.vendorNameAlreadyExists(newVendorField.getText())) {
                CommonUtil.displayMessage(statusMessage, "Vendor already exists.", false);
            } else {
                validInput = true;
            }
        } else if(!categoryList.getItems().contains(vendorCategoryField.getText())) {
            CommonUtil.displayMessage(statusMessage, "Existing Vendor Category needs to be updated.", false);
        } else {
            validInput = true;
        }
        return validInput;
    }
    
    /**
     * This method saves the transaction.     
     */   
    public void saveNewTransaction() {
        Transaction transaction = CommonUtil.generateTransactionModel(StringUtil.convertFromDollarFormat(
            amountField.getText()), incomeCheckBoxField.isSelected(), recurringCheckBoxField.isSelected(), 
            Date.valueOf(transDateField.getValue()), selectedBudgetId, methodList, existingVendorList.getSelectionModel()
            .getSelectedItem().toString(), newVendorField.getText(), categoryList.getSelectionModel().getSelectedItem()
            .toString(), commentArea.getText());       
        TransactionDAO.saveTransaction(transaction);        
        updateBalances(transaction);        
        displayMessage("Transaction successfully saved!", true);
        resetFields(false);
    }
    
    /**
     * This method updates the budget and category budget balances.
     * 
     * @param transaction - the transaction entity     
     */
    public void updateBalances(Transaction transaction) {        
        BudgetDAO.updateBudgetBalance(selectedBudgetId, transaction.getIncome(),
                transaction.getAmount());
        // Update category budget balance
        int categoryId = VendorDAO.findCategoryByVendorId(transaction.getVendorId()).getCategoryId();
        CategoryBudgetDAO.updateCategoryBalance(selectedBudgetId, categoryId, transaction.getIncome(),
                transaction.getAmount());
        // Now update the label and category budget table.
        displayBalanceLabel(currentBalanceLabel, BudgetDAO.findBudgetCurrentBalance(selectedBudgetId));
        populateCategoryBudgetTable(selectedBudgetId);
        buildTransactionTabPane(selectedBudgetId, currentCategoryList);
    }
    
    /**
     * This method appends a $ symbol and adjusts the color for the balance label.
     * 
     * @param label - the label to set
     * @param balance - the balance amount
     */
    private void displayBalanceLabel(Label label, Double balance) {
        label.setText(StringUtil.convertToDollarFormat(balance.toString()));
        label.setTextFill(balance >= 0D ? Color.BLACK : Color.RED);
    }
    
    /**
     * This method displays a message at the bottom of the dialog.
     * 
     * @param message - the message to display
     * @param success - if true, message is in green, otherwise red
     */
    private void displayMessage(String message, boolean success) {
        statusMessage.setText(message);
        statusMessage.setTextFill(success? Color.GREEN : Color.RED);
        statusMessage.setAlignment(Pos.CENTER);
    }
    
    /**
     * This method resets all input fields.
     * 
     * @param resetMessage - true resets the status message    
     */
    private void resetFields(boolean resetMessage) {
        amountField.setText("");
        incomeCheckBoxField.setSelected(false);
        recurringCheckBoxField.setSelected(false);
        transDateField.setValue(LocalDate.now());
        loadExistingVendors();
        vendorCategoryField.setText("");
        newVendorField.setText("");
        commentArea.setText("");
        categoryList.getSelectionModel().selectFirst();
        methodList.getSelectionModel().selectFirst();
        if(resetMessage) {
            statusMessage.setText("");  
        }
    }
    
    /**
     * This method updates the budget current value.     
     */    
    public void onBudgetCurrentAction() {
        // First, set current = false for whatever previous budget was selected.
        BudgetDAO.clearCurrentFlagBudget();
        // Since the checkbox is cleared when selection changes, we need to find
        // new activeBudgetId if checkbox was selected.
        if(currentBudgetCheckBox.isSelected()) {
            selectedBudgetId = getSelectedBudgetId(budgetList.getSelectionModel()
                    .getSelectedItem().toString());
        }
        // If check box was unchecked then previous activeBudgetId is same as current.
        BudgetDAO.updateCurrentFlagBudget(selectedBudgetId, currentBudgetCheckBox.isSelected());
    }
    
    /**
     * This method determines the selected budget ID.
     * 
     * @param budgetName - the budget name
     * @return the selected budget ID
     */
    private int getSelectedBudgetId(String budgetName) {
        int budgetId = 0;
        for(Budget budget : existingBudgetList) {
            if(budget.getBudgetName().equals(budgetName)) {
                budgetId = budget.getBudgetId();
                break;
            }
        }
        return budgetId;
    }    
        
    /**
     * This method adds a category and budget to the table.
     * 
     * @param budgetId - the budget ID     
     */
    public void populateCategoryBudgetTable(int budgetId) {
        categoryBudgetList = CategoryBudgetDAO.getCategoryBudgetsForTable(budgetId);
        ObservableList data = FXCollections.observableList(categoryBudgetList);
        
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory("categoryId"));
        categoryIdColumn.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());
        categoryIdColumn.setVisible(false);        
        categoryColumn.setCellValueFactory(new PropertyValueFactory("categoryName"));
        categoryColumn.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());        
        budgetRemainingCol.setCellValueFactory(new PropertyValueFactory("budgetRemaining"));
        budgetRemainingCol.setStyle("-fx-alignment: CENTER-RIGHT;");        
        budgetRemainingCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<CategoryBudgetTableEntry, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            if(item.contains("-")) {
                                this.setTextFill(Color.RED);
                            } else {
                                this.setTextFill(Color.GREEN);
                            }
                            setText(item);
                        }
                    }
                };
            }
        });
        
        budgetStartingCol.setCellValueFactory(new PropertyValueFactory("budgetStarting"));
        budgetStartingCol.setCellFactory(TextFieldTableCell.<CategoryBudgetTableEntry>forTableColumn());
        budgetStartingCol.setStyle("-fx-alignment: CENTER-RIGHT;");       
        categoryBudgetTable.setItems(data);
    }
    
    /**
     * This class builds the transaction tab pane.
     * 
     * @param budgetId - the budget ID
     * @param categoryList - the category list         
     */
    public void buildTransactionTabPane(int budgetId, List<Category> categoryList) {
        // First clear the previous one
        categoryTabPane.getTabs().clear();
        // Set up the All Transactions tab
        Tab tab = new Tab("All");
        allTransTable = buildTransactionTable(budgetId, 0, false);
        tab.setContent(allTransTable);
        categoryTabPane.getTabs().add(tab);
        // Set up the category tabs
        for(Category category : categoryList) {
            tab = new Tab(category.getCategoryName());
            TableView table = buildTransactionTable(budgetId, category.getCategoryId(), true);
            tab.setContent(table);
            categoryTabPane.getTabs().add(tab);
        }        
    }
    
    /**
     * This method builds the transaction table for categories.
     * 
     * @param budgetId - the budget ID
     * @param categoryId - the category ID
     * @param perCategory - if true, search by category
     * @return the table populated with transaction data    
     */
    public TableView buildTransactionTable(int budgetId, int categoryId, boolean perCategory) {
        TableView transactionTable = new TableView();
        List<TransactionTableEntry> transactionList = TransactionDAO
                .getTransactionsForTable(budgetId, categoryId, perCategory);
        ObservableList data = FXCollections.observableList(transactionList);
        
        // ID column is just so we know the ID per row, so it will be hidden
        TableColumn idColumn = new TableColumn("Transaction ID");
        idColumn.setCellValueFactory(new PropertyValueFactory("transactionId"));
        idColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        idColumn.setVisible(false);
        
        TableColumn dateColumn = new TableColumn("Transaction Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory("transDate"));
        dateColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        dateColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn vendorColumn = new TableColumn("Vendor");
        vendorColumn.setCellValueFactory(new PropertyValueFactory("vendorName"));
        vendorColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        vendorColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn amountColumn = new TableColumn("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory("amount"));
        amountColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        amountColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn categoryNameColumn = new TableColumn("Category Name");
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory("categoryName"));
        categoryNameColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        categoryNameColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn methodColumn = new TableColumn("Method Type");
        methodColumn.setCellValueFactory(new PropertyValueFactory("methodType"));
        methodColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        methodColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn commentsColumn = new TableColumn("Comments");
        commentsColumn.setCellValueFactory(new PropertyValueFactory("comments"));
        commentsColumn.setCellFactory(TextFieldTableCell.<TransactionTableEntry>forTableColumn());
        commentsColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        transactionTable.getColumns()
                .addAll(dateColumn, vendorColumn, amountColumn, categoryNameColumn, methodColumn, commentsColumn);
        transactionTable.setItems(data);
        transactionTable.setEditable(false);
        transactionTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        transactionTable.setTableMenuButtonVisible(true);
        
        transactionTable.setRowFactory(new Callback<TableView<TransactionTableEntry>,
                                        TableRow<TransactionTableEntry>>() {  
            @Override
            public TableRow<TransactionTableEntry> call(TableView<TransactionTableEntry> tableView) {  
                final TableRow<TransactionTableEntry> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  
                final MenuItem editMenuItem = new MenuItem("Edit Transaction");  
                editMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                    @Override  
                    public void handle(ActionEvent event) {
                        try {             
                            onEditTransaction(row.getItem());
                        } catch (IOException ex) {
                            LOG.error("IO Exception in edit transaction action", ex);
                        }
                    }  
                });
                
                final MenuItem deleteMenuItem = new MenuItem("Delete Transaction");  
                deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                    @Override  
                    public void handle(ActionEvent event) {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Delete Transaction?");
                        alert.setHeaderText(null);
                        alert.setContentText("Are you sure you want to delete this transaction?.");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK){
                            transactionTable.getItems().remove(row.getItem());
                            // Update balances and tables                                    
                            updateAfterTransactionDelete(row.getItem());                               
                        }                     
                    }  
                });  
                contextMenu.getItems().add(editMenuItem);
                contextMenu.getItems().add(deleteMenuItem);                
                // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row ;  
            }  
        });
        
        return transactionTable;
    }
    
    /**
     * This method updates the balances and tables after a transaction deletion.
     * 
     * @param tableEntry - the table entry being deleted     
     */
    private void updateAfterTransactionDelete(TransactionTableEntry tableEntry) {
        TransactionDAO.deleteTransaction(Integer.parseInt(tableEntry.getTransactionId()));
        boolean isIncome = "YES".equalsIgnoreCase(tableEntry.getIncome());
        double amount = StringUtil.convertFromDollarFormat(tableEntry.getAmount());
        int categoryId = CategoryDAO.findCategoryByName(tableEntry.getCategoryName()).getCategoryId();
        BudgetDAO.rollbackBudgetBalance(selectedBudgetId, isIncome, amount);
        CategoryBudgetDAO.rollbackBalance(selectedBudgetId, isIncome, amount, categoryId);
        displayBalanceLabel(currentBalanceLabel, BudgetDAO.findBudgetCurrentBalance(selectedBudgetId));
        populateCategoryBudgetTable(selectedBudgetId);
        buildTransactionTabPane(selectedBudgetId, currentCategoryList);
    }
    
    /**
     * This method handles the edit budget button action.
     * 
     * @throws java.io.IOException - the IO exception
     */
    public void onEditBudgetBtnAction() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Edit Budget");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/budget.fxml"));
        BorderPane border = (BorderPane) loader.load();
        BudgetController bController = loader.getController();
        bController.setHomeContoller(this);
        bController.populateForEdit(selectedBudgetId);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method sets up the edit transaction dialog.
     * 
     * @param transactionEntry - the transaction row entry
     * @throws IOException - the IO exception
     */
    private void onEditTransaction(TransactionTableEntry transactionEntry) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Edit Transaction");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/editTransaction.fxml"));
        BorderPane border = (BorderPane) loader.load();
        EditTransactionController etController = loader.getController();
        etController.setHomeContoller(this);
        etController.populateFields(selectedBudgetId, transactionEntry);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method handles the add new budget menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onAddNewBudget() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Add New Budget");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/budget.fxml"));
        BorderPane border = (BorderPane) loader.load();
        BudgetController bController = loader.getController();
        bController.setHomeContoller(this);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method handles the edit category menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onEditCategoryAction() throws IOException {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Coming Soon!");
        alert.setHeaderText(null);
        alert.setContentText("This feature is not implemented yet.");
        alert.showAndWait(); 
    }
    
    /**
     * This method handles the edit vendor menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onEditVendorAction() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Edit Vendors");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/vendor.fxml"));
        BorderPane border = (BorderPane) loader.load();
        VendorController vController = loader.getController();
        vController.setHomeContoller(this);
        vController.populateFields(selectedBudgetId);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method handles the edit vendor category button action.
     * 
     * @throws IOException - the IO exception
     */
    public void onEditCategoryBtnAction() throws IOException {
        onEditVendorAction();
    }
    
    /**
     * This method handles the manage method type menu item.   
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onManageMethodAction() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Manage Methods");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/method.fxml"));
        BorderPane border = (BorderPane) loader.load();
        MethodController mController = loader.getController();
        mController.setHomeContoller(this);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method handles the About menu item.
     */
    @FXML
    public void onAboutMenu() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Budget App v1.0");
        alert.setHeaderText(null);
        alert.setContentText("This applicatoin allows you to manage a budget and keep track of "
                + "the balances per category.");
        alert.showAndWait();
    }
    
    /**
     * This method handles the save to file menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onSaveToFileAction() throws IOException {
        if(allTransTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("There is no data to export.");
            alert.showAndWait();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save All Transactions");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Files", "*.xls");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog((Stage) homeBorderPane.getScene().getWindow());
            if (file != null) {
                FileUtil.exportTransactionsToExcelFile(file, allTransTable.getItems());
            }
        }   
    }
    
    /**
     * This method handles the view search menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onViewSearchAction() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Search");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/search.fxml"));
        BorderPane border = (BorderPane) loader.load();
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * This method handles the view reports menu item.
     * 
     * @throws IOException - the IO exception
     */
    @FXML
    public void onViewReportsAction() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Reports");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/reports.fxml"));
        BorderPane border = (BorderPane) loader.load();
        ReportController rController = loader.getController();       
        rController.setBudgetId(selectedBudgetId);
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    /**
     * This method handles the exit menu item.
     */
    @FXML
    public void onExitMenuItem() {
        Platform.exit();
    }    
}
