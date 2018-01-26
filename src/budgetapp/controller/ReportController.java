package budgetapp.controller;

import budgetapp.dao.BudgetDAO;
import budgetapp.dao.CategoryBudgetDAO;
import budgetapp.model.CategoryBudgetTableEntry;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * The Method controller class.
 */
public class ReportController implements Initializable {

    /** The constant for category report title. */
    private static final String CATEGORY_REPORT = "Category Report";
    /** The border pane. */
    @FXML
    private BorderPane reportBorderPane;
    /** The reports list choice box. */
    @FXML
    private ChoiceBox reportTypeList;
    /** The start date field. */
    @FXML
    private DatePicker startDateField;
    /** The end date field. */
    @FXML
    private DatePicker endDateField;
    /** The pie chart field. */
    @FXML
    private PieChart pieChartField;
    /** The status message. */
    @FXML
    private Label reportStatusMessage;
    /** The HomeController instance. */
    private HomeController homeController;
    /** The budget ID. */
    private int budgetId;
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
          loadReportList();
    }
    
    /**
     * This method builds the reports list choice box.
     */
    private void loadReportList() {
        ObservableList<String> reportNameList = FXCollections.observableArrayList();        
        reportNameList.add(CATEGORY_REPORT);
        reportTypeList.setItems(reportNameList);
        reportTypeList.getSelectionModel().selectFirst();
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
     * This method sets the current budget ID.
     * 
     * @param budgetId - the budget ID
     */
    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }
    
    /**
     * This method handles the generate reports button action.
     */
    public void onGenerateBtnAction() {
        reportStatusMessage.setText("");
        // Determine which report was chosen
        if(CATEGORY_REPORT.equals(reportTypeList.getSelectionModel().getSelectedItem().toString())) {
            generateCategoryReport();     
        }
    }
    
    /**
     * This method generates the category report.
     */
    private void generateCategoryReport() {
        double currentBalance = BudgetDAO.findBudgetCurrentBalance(budgetId);
        List<CategoryBudgetTableEntry> categoryBudgetList = CategoryBudgetDAO.getDataForCategoryReport(budgetId);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.add(new PieChart.Data("Amount Remaining", currentBalance));
        for(CategoryBudgetTableEntry categoryBudget : categoryBudgetList) {
            pieChartData.add(new PieChart.Data(categoryBudget.getCategoryName(), Double.parseDouble(
                    categoryBudget.getBudgetRemaining())));
        }
        pieChartField.setData(pieChartData);           
    }
    
    /**
     * This method handles the close button action.
     */
    public void onCloseBtnAction() {
        Stage stage = (Stage) reportBorderPane.getScene().getWindow();
        stage.close();
    }
}
