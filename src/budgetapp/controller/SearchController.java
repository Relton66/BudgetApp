package budgetapp.controller;

import budgetapp.dao.CategoryDAO;
import budgetapp.dao.MethodDAO;
import budgetapp.dao.TransactionDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Category;
import budgetapp.model.Method;
import budgetapp.model.SearchTableEntry;
import budgetapp.model.Vendor;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * The Search controller class.
 */
public class SearchController implements Initializable {

    /** The border pane. */
    @FXML
    private BorderPane searchBorderPane;
    /** The start date. */
    @FXML
    private DatePicker startDate;
    /** The end date. */
    @FXML
    private DatePicker endDate;
    /** The amount field. */
    @FXML
    private TextField amountField;
    /** The income only radio button. */
    @FXML
    private RadioButton incomeOnlyRadioBtn;
    /** The expense only radio button. */
    @FXML
    private RadioButton expenseOnlyRadioBtn;
    /** The vendor list. */
    @FXML
    private ListView vendorList;
    /** The category list. */
    @FXML
    private ListView categoryList;
    /** The method list. */
    @FXML
    private ListView methodList;
    /** The comments. */
    @FXML
    private TextArea commentArea;
    /** The search table. */
    @FXML
    private TableView searchTable;
    /** The budget column. */
    @FXML
    private TableColumn budgetCol;
    /** The date column. */
    @FXML
    private TableColumn dateCol;
    /** The vendor column. */
    @FXML
    private TableColumn vendorCol;
    /** The amount column. */
    @FXML
    private TableColumn amountCol;
    /** The category column. */
    @FXML
    private TableColumn categoryCol;
    /** The method column. */
    @FXML
    private TableColumn methodCol;
    /** The comment column. */
    @FXML
    private TableColumn commentCol;
    /** The radio button toggle group. */
    private ToggleGroup radioBtnGroup;
    /** The map for vendor IDs and names. */
    private final Map<String, Integer> vendorMap = new HashMap<>();
    /** The map for method IDs and names. */
    private final Map<String, Integer> methodMap = new HashMap<>();
    /** The map for category IDs and names. */
    private final Map<String, Integer> categoryMap = new HashMap<>();
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SearchController.class);
    
    /**
     * This method is called when class is loaded.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadVendorList();
        loadCategoryList();
        loadMethodList();
        vendorList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        methodList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        radioBtnGroup = new ToggleGroup();
        incomeOnlyRadioBtn.setToggleGroup(radioBtnGroup);
        expenseOnlyRadioBtn.setToggleGroup(radioBtnGroup);
    }
    
    /**
     * This method builds the vendor list.
     */
    private void loadVendorList() {
        ObservableList<String> vendorNameList = FXCollections.observableArrayList();
        for(Vendor vendor : VendorDAO.getExistingVendors()) {
            vendorNameList.add(vendor.getVendorName());
            vendorMap.put(vendor.getVendorName(), vendor.getVendorId());
        }        
        vendorList.setItems(vendorNameList);
    }
    
    /**
     * This method builds the method list.
     */
    private void loadMethodList() {
        ObservableList<String> methods = FXCollections.observableArrayList();
        for(Method method : MethodDAO.getAllMethods()) {
            methods.add(method.getMethodType());
            methodMap.put(method.getMethodType(), method.getMethodId());
        }
        methodList.setItems(methods);
    }
    
    /**
     * This method builds the category list.
     */
    private void loadCategoryList() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        for(Category category : CategoryDAO.getExistingCategories()) {
            categories.add(category.getCategoryName());
            categoryMap.put(category.getCategoryName(), category.getCategoryId());
        }       
        categoryList.setItems(categories);
    }
    
    
    /**
     * This method handles the Search button action.
     */
    public void onSearchBtnAction() {        
        List<Integer> categoryIdList = generateCategoryIdList();
        List<Integer> vendorIdList = generateVendorIdList();
        List<Integer> methodIdList = generateMethodIdList();
        Date startDateVal = startDate.getValue() == null ? null : Date.valueOf(startDate.getValue());
        Date endDateVal = endDate.getValue() == null ? null : Date.valueOf(endDate.getValue());
        List<SearchTableEntry> searchTableList = TransactionDAO.searchTransactions(startDateVal, endDateVal, 
            amountField.getText(), incomeOnlyRadioBtn.isSelected(), expenseOnlyRadioBtn.isSelected(), 
            commentArea.getText(), categoryIdList, vendorIdList, methodIdList);
        ObservableList data = FXCollections.observableList(searchTableList);
        
        budgetCol.setCellValueFactory(new PropertyValueFactory("budgetName"));
        budgetCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        dateCol.setCellValueFactory(new PropertyValueFactory("transDate"));
        dateCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        vendorCol.setCellValueFactory(new PropertyValueFactory("vendorName"));
        vendorCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        amountCol.setCellValueFactory(new PropertyValueFactory("amount"));
        amountCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        categoryCol.setCellValueFactory(new PropertyValueFactory("categoryName"));
        categoryCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        methodCol.setCellValueFactory(new PropertyValueFactory("methodType"));
        methodCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        commentCol.setCellValueFactory(new PropertyValueFactory("comments"));
        commentCol.setCellFactory(TextFieldTableCell.<SearchTableEntry>forTableColumn());
        
        searchTable.setItems(data);
    }
    
    /**
     * This method takes the selected vendor names and builds a list of their IDs.
     * 
     * @return the list of selected vendor IDs
     */
    private List<Integer> generateVendorIdList() {
        List<Integer> vendorIdList = new ArrayList<>();
        List<String> selectedVendors = vendorList.getSelectionModel().getSelectedItems();
        for(String vendor : selectedVendors) {
            vendorIdList.add(vendorMap.get(vendor));            
        }
        return vendorIdList;
    }
    
    /**
     * This method takes the selected method names and builds a list of their IDs.
     * 
     * @return the list of selected method IDs
     */
    private List<Integer> generateMethodIdList() {
        List<Integer> methodIdList = new ArrayList<>();
        List<String> selectedMethods = methodList.getSelectionModel().getSelectedItems();
        for(String method : selectedMethods) {
            methodIdList.add(methodMap.get(method));
        }
        return methodIdList;
    }
    
    /**
     * This method takes the selected category names and builds a list of their IDs.
     * 
     * @return the list of selected category IDs
     */
    private List<Integer> generateCategoryIdList() {
        List<Integer> categoryIdList = new ArrayList<>();
        List<String> selectedCategories = categoryList.getSelectionModel().getSelectedItems();
        for(String category : selectedCategories) {
            categoryIdList.add(categoryMap.get(category));
        }
        return categoryIdList;
    }
    
    /**
     * This method handles the Reset button action.
     */
    public void onResetBtnAction() {
        amountField.setText("");        
        commentArea.setText("");
        startDate.setValue(null);
        endDate.setValue(null);
        incomeOnlyRadioBtn.setSelected(false);
        expenseOnlyRadioBtn.setSelected(false);
        categoryList.getSelectionModel().clearSelection();
        methodList.getSelectionModel().clearSelection();
        vendorList.getSelectionModel().clearSelection();
    }
    
    /**
     * This method handles the Close button action.
     */
    public void onCloseBtnAction() {
        Stage stage = (Stage) searchBorderPane.getScene().getWindow();
        stage.close();
    }
}
