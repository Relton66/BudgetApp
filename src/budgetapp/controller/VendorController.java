package budgetapp.controller;

import budgetapp.dao.CategoryDAO;
import budgetapp.dao.VendorDAO;
import budgetapp.model.Category;
import budgetapp.model.CategoryBudgetTableEntry;
import budgetapp.model.MethodTableEntry;
import budgetapp.model.Vendor;
import budgetapp.model.VendorTableEntry;
import budgetapp.util.CommonUtil;
import budgetapp.util.StringUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

/**
 * The Vendor controller class.
 */
public class VendorController implements Initializable {

    /** The border pane. */
    @FXML
    private BorderPane vendorBorderPane;
    /** The vendor list. */
    @FXML
    private ChoiceBox vendorList;
    /** The category list. */
    @FXML
    private ChoiceBox categoryList;
    /** The vendor table. */
    @FXML
    private TableView vendorTable;
    /** The vendor ID column. */
    @FXML
    private TableColumn vendorIdCol;
    /** The vendor name column. */
    @FXML
    private TableColumn vendorNameCol;
    /** The category name column. */
    @FXML
    private TableColumn categoryNameCol;
    /** The active column. */
    @FXML
    private TableColumn activeCol;    
    /** The status message label. */
    @FXML
    private Label statusMessage;
    /** The list of vendor table entries. */
    private List<VendorTableEntry> vendorTableList = new ArrayList();   
    /** The budget ID. */
    private int budgetId;
    /** The HomeController instance. */
    private HomeController homeController;    
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VendorController.class);
    
    /**
     * This method is called when class is loaded.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
        CommonUtil.loadExistingVendors(vendorList, false);
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
     * This method the populates fields.
     * 
     * @param budgetId - the budget ID
     */
    public void populateFields(int budgetId) {
        this.budgetId = budgetId;
        loadExistingCategories();
        populateVendorTable();
    }
    
    /**
     * This method loads the existing categories. 
     */    
    public void loadExistingCategories() {
        List<Category> currentCategoryList = CategoryDAO.getCategoriesByBudgetId(budgetId);
        // We need a list of strings for the choicebox
        ObservableList<String> currentCategoryNameList = FXCollections.observableArrayList(); 
        if(!currentCategoryList.isEmpty()) {
            for(Category category : currentCategoryList) {
                currentCategoryNameList.add(category.getCategoryName());
            }
            categoryList.setItems(currentCategoryNameList);
            categoryList.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * This method handles the update vendor button action.
     */
    public void onUpdateVendorBtnAction() {
        String vendorName = vendorList.getSelectionModel().getSelectedItem().toString();
        String categoryName = categoryList.getSelectionModel().getSelectedItem().toString();
        Vendor vendor = VendorDAO.findVendorByName(vendorName);
        int vendorId = vendor.getVendorId();
        int oldCategoryId = vendor.getCategoryId();
        int categoryId = CategoryDAO.findCategoryByName(categoryName).getCategoryId();
        if(oldCategoryId != categoryId) {
            VendorDAO.updateVendorActive(vendorId, false);
            VendorDAO.saveVendor(vendorName, categoryName);
            CommonUtil.displayMessage(statusMessage, "Vendor has been updated successfully!", true);
            homeController.loadExistingVendors();
        } else {
            CommonUtil.displayMessage(statusMessage, "Vendor is already linked to this category.", false);
        }   
    }
    
    private void populateVendorTable() {
        vendorTableList = VendorDAO.getVendorTableList();
        ObservableList data = FXCollections.observableList(vendorTableList);
        vendorIdCol.setCellValueFactory(new PropertyValueFactory("vendorId"));
        vendorIdCol.setCellFactory(TextFieldTableCell.<VendorTableEntry>forTableColumn());
        vendorIdCol.setVisible(false);
        
        vendorNameCol.setCellValueFactory(new PropertyValueFactory("vendorName"));
        vendorNameCol.setCellFactory(TextFieldTableCell.<VendorTableEntry>forTableColumn());
        vendorNameCol.setEditable(true);
        
        categoryNameCol.setCellValueFactory(new PropertyValueFactory("categoryName"));
        categoryNameCol.setCellFactory(TextFieldTableCell.<VendorTableEntry>forTableColumn());
        categoryNameCol.setEditable(false);
        
        activeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VendorTableEntry,Boolean>,
                                        ObservableValue<Boolean>>() {            
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<VendorTableEntry, Boolean> param)
            {   
                VendorDAO.updateVendorActive(Integer.parseInt(param.getValue().getVendorId()),
                    param.getValue().getActive());
                return param.getValue().activeProperty();
            }   
        });
        activeCol.setCellFactory(CheckBoxTableCell.forTableColumn(activeCol) );      
        
        vendorTable.setItems(data);
        vendorTable.setEditable(true);
    }
    
    /**
     * This handles the action when user edits a vendor name table cell.
     *
     * @param t - the CellEditEvent instance
     */
    public void onVendorNameEditCommit(TableColumn.CellEditEvent<VendorTableEntry, String> t) {     
        int vendorId = Integer.parseInt(((VendorTableEntry) t.getTableView().getItems().get(
                t.getTablePosition().getRow())).getVendorId());
        String newName = t.getNewValue();
        if(StringUtil.isAlphaNumeric(newName)) {
            VendorDAO.renameVendor(vendorId, newName);
            ((VendorTableEntry) t.getTableView().getItems().get(
                t.getTablePosition().getRow())
                ).setVendorName(newName);
            CommonUtil.loadExistingVendors(vendorList, false);
        } else {
            CommonUtil.displayMessage(statusMessage, "Vendor name must be alphanumeric.", false);  
        }       
    }
    
    /**
     * This method handles the close button action.
     */
    public void onCloseBtnAction() {
        Stage stage = (Stage) vendorBorderPane.getScene().getWindow();
        stage.close();
    }
}
