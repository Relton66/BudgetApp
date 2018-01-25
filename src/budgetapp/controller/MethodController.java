package budgetapp.controller;

import budgetapp.dao.MethodDAO;
import budgetapp.model.Method;
import budgetapp.model.MethodTableEntry;
import budgetapp.util.CommonUtil;
import budgetapp.util.StringUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

/**
 * The Method controller class.
 */
public class MethodController implements Initializable {

    /** The border pane. */
    @FXML
    private BorderPane methodBorderPane;
    /** The method type field. */
    @FXML
    private TextField methodTypeField;    
    /** The method type table. */
    @FXML
    private TableView methodTable;
    /** The method type column. */
    @FXML
    private TableColumn methodTypeCol;
    /** The active column. */
    @FXML
    private TableColumn activeCol;    
    /** The close button. */
    @FXML
    private Button closeBtn;
    /** The status message label. */
    @FXML
    private Label statusMessage;
    /** The list of methods in the table. */
    private List<MethodTableEntry> methodList = new ArrayList<>();    
    /** The HomeController instance. */
    private HomeController homeController;    
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MethodController.class);
    
    /**
     * This method is called when class is loaded.
     * 
     * @param url - the URL
     * @param rb - the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
        methodList = MethodDAO.getAllMethods();
        populateMethodsTable();        
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
     * This method handles the add button action.    
     */
    public void onAddBtnAction() {
        if(!StringUtil.isAlphaNumeric(methodTypeField.getText())) {
            CommonUtil.displayMessage(statusMessage, "Method type must be alphanumeric.", false);
        } else if(!methodTypeIsUnique(methodTypeField.getText())) {
            CommonUtil.displayMessage(statusMessage, "Method type already exists in table.", false);
        } else {   
            addToMethodTable(methodTypeField.getText());
            methodTypeField.clear();
        }
    }
    
    /**
     * This method determines if the method type exists in table already.
     * 
     * @param methodType - the method type
     * @return true if it does not exist in the table
     */
    private boolean methodTypeIsUnique(String methodType) {
        boolean isUnique = true;
        for(MethodTableEntry methodEntry : methodList) {
            if(methodEntry.getMethodType().equalsIgnoreCase(methodType)) {
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }
    
    /**
     * This method handles the save button action.    
     */
    public void onSaveBtnAction() {
        List<MethodTableEntry> savedMethodsList = MethodDAO.getAllMethods();        
        for(MethodTableEntry currentMethodEntry : methodList) {
            boolean newMethod = true;
            for(MethodTableEntry savedMethodEntry : savedMethodsList) {
                if(currentMethodEntry.getMethodType().equalsIgnoreCase(savedMethodEntry.getMethodType())) {
                    newMethod = false;
                    if(!Objects.equals(currentMethodEntry.getActive(), savedMethodEntry.getActive())) {
                        MethodDAO.updateMethod(currentMethodEntry);
                    }
                    break;
                }
            }
            if(newMethod) {
                MethodDAO.saveMethod(currentMethodEntry);
            }            
        }
        homeController.loadActiveMethodTypes();
        CommonUtil.displayMessage(statusMessage, "Method types saved successfully.", true);
        
    }
    
    /**
     * This method handles the close button action.
     */
    public void onCloseBtnAction() {
        Stage stage = (Stage) methodBorderPane.getScene().getWindow();
        stage.close();
    }
    
    /**
     * This method adds a method type to the table.
     * 
     * @param methodType - the method type    
     */
    private void addToMethodTable(String methodType) {
        MethodTableEntry methodEntry = new MethodTableEntry(methodType, true);       
        methodList.add(methodEntry); 
        populateMethodsTable();
    }
    
    /**
     * This method populates the method table.     
     */
    private void populateMethodsTable() {              
        ObservableList data = FXCollections.observableList(methodList);
        methodTypeCol.setCellValueFactory(new PropertyValueFactory("methodType"));
        methodTypeCol.setCellFactory(TextFieldTableCell.<Method>forTableColumn());
        activeCol.setCellValueFactory(new Callback<CellDataFeatures<MethodTableEntry,Boolean>,
                                        ObservableValue<Boolean>>() {            
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<MethodTableEntry, Boolean> param)
            {   
                return param.getValue().activeProperty();
            }   
        });
        activeCol.setCellFactory(CheckBoxTableCell.forTableColumn(activeCol) );       
        methodTable.setItems(data);
    }
}
