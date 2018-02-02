package budgetapp.dao;

import budgetapp.Main;
import budgetapp.model.Budget;
import budgetapp.util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles budget related database operations.
 */
public class BudgetDAO {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(BudgetDAO.class); 
    
    /**
     * This method saves the new budget.
     * 
     * @param budget - the Budget model to save
     * @return newId - the budget ID created after insert    
     */
    public static int saveNewBudget(Budget budget) {
        LOG.info("Attempting to save the budget");
        int newId = 0;
        String query;
        if(Main.USE_DERBY) {
            query = "INSERT INTO budget (budget_name, start_date, end_date, start_balance, "
                + "current_balance) VALUES (?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO budget (budget_id, budget_name, start_date, end_date, start_balance, "
                + "current_balance) VALUES (budget_seq.nextval, ?, ?, ?, ?, ?)";
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(budget.getBudgetName());
        parameters.add(budget.getStartDate());
        parameters.add(budget.getEndDate());
        parameters.add(budget.getStartBalance());
        // Current balance starts out the same as starting balance
        parameters.add(budget.getStartBalance());
        try {
            newId = DBUtil.dbExecuteUpdate(query, parameters, "BUDGET_ID");
            LOG.info("Budget was saved successfully!");
        } catch (SQLException | ClassNotFoundException ex) {
            LOG.error("saveNewBudget has failed", ex);            
        }
        return newId;
    }
    
    /**
     * This method updates an existing budget.
     * 
     * @param budget - the budget model  
     */
    public static void updateBudget(Budget budget) {
        LOG.info("Attempting to update budget ID {}", budget.getBudgetId());
        String query = "UPDATE budget set budget_name = ?, start_date = ?, end_date = ?, start_balance = ? WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budget.getBudgetName());
        parameters.add(budget.getStartDate());
        parameters.add(budget.getEndDate());
        parameters.add(budget.getStartBalance());
        parameters.add(budget.getBudgetId());
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Budget was saved successfully!");
        } catch (SQLException | ClassNotFoundException ex) {
            LOG.error("updateBudget has failed", ex);            
        }
    }
    
    /**
     * This method retrieves all the existing budgets.
     * 
     * @return a list of existing budgets     
     */
    public static List<Budget> getExistingBudgets() {
        LOG.info("Attempting to get all existing budgets");        
        String query = "SELECT * FROM budget order by start_date";
        ObservableList<Budget> existingBudgetsList = FXCollections.observableArrayList();
        List<Object> parameters = new ArrayList<>();
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                Budget budget = new Budget();
                budget.setBudgetId(results.getInt("BUDGET_ID"));
                budget.setBudgetName(results.getString("BUDGET_NAME"));
                budget.setStartDate(results.getDate("START_DATE"));
                budget.setEndDate(results.getDate("END_DATE"));
                budget.setStartBalance(results.getDouble("START_BALANCE"));
                budget.setCurrentBalance(results.getDouble("CURRENT_BALANCE"));
                budget.setActive(results.getBoolean("ACTIVE"));
                existingBudgetsList.add(budget);
            }
            LOG.info("Retrieved all budgets successfully");
        } catch (SQLException | ClassNotFoundException ex) {
            LOG.error("getExistingBudgets has failed", ex);            
        }
        return existingBudgetsList;
    }
    
    /**
     * This method retrieves all the existing budget names.
     * 
     * @return a list of existing budget names    
     */
    public static List<String> getExistingBudgetNames() {
        LOG.info("Attempting to get all existing budget names");        
        String query = "SELECT budget_name FROM budget order by start_date";
        ObservableList<String> existingBudgetNamesList = FXCollections.observableArrayList();
        List<Object> parameters = new ArrayList<>();
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                existingBudgetNamesList.add(results.getString("BUDGET_NAME"));
            }
            LOG.info("Retrieved all budget names successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getExistingBudgetNames has failed", e);          
        }
        return existingBudgetNamesList;
    }
    
    /**
     * This method retrieves the budget ID for a given budget name.
     * 
     * @param budgetName - the budget name
     * @return the budget ID     
     */
    public static int findBudgetId(String budgetName) {
        LOG.info("Attempting to find budget ID for budget name {}", budgetName);
        int budgetId = 0;
        String query = "SELECT budget_id FROM budget WHERE budget_name = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetName);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                budgetId = results.getInt("BUDGET_ID");
            }
            LOG.info("Retrieved budget ID successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findBudgetId has failed", e);           
        }
        return budgetId;
    }
    
    /**
     * This method updates the active flag for a budget.
     * 
     * @param budgetId - the budget ID
     * @param activeFlag - the active value     
     */
    public static void updateActiveBudget(int budgetId, boolean activeFlag) {
        LOG.info("Attempting to update active field for budget ID {}", budgetId);
        String query = "UPDATE budget SET ACTIVE = ? WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(activeFlag);
        parameters.add(budgetId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Active budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateActiveBudget has failed", e);            
        }       
    }
    
    /**
     * This method clears the active flag for all budgets.     
     */
    public static void clearActiveBudget() {
        LOG.info("Attempting to clear active fields for all budgets)");
        String query = "UPDATE budget SET ACTIVE = false";
        List<Object> parameters = new ArrayList<>();
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Active budgets cleared successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("clearActiveBudget has failed", e);            
        }       
    }
    
    /**
     * This method updates the budgets current balance.
     * 
     * @param budgetId - the budget ID
     * @param isIncome - if true, add amount, else subtract
     * @param amount - the amount     
     */
    public static void updateBudgetBalance(int budgetId, boolean isIncome, double amount) {
        LOG.info("Attempting to update budget ID {}", budgetId);
        String query = "UPDATE budget SET current_balance = CASE WHEN ? = 'Y' THEN current_balance + ? ELSE"
                + " current_balance - ? END WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(isIncome ? "Y" : "N");
        parameters.add(amount);
        parameters.add(amount);
        parameters.add(budgetId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateBudget has failed", e);            
        }        
    }
    
    /**
     * This method retrieves the budget current balance for a given ID.
     * 
     * @param budgetId - the budget ID
     * @return the current balance     
     */
    public static double findBudgetCurrentBalance(int budgetId) {
        LOG.info("Attempting to find current balance for budget ID {}", budgetId);
        double currentBalance = 0D;
        String query = "SELECT current_balance FROM budget WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                currentBalance = results.getDouble("CURRENT_BALANCE");
            }
            LOG.info("Retrieved current balance successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findBudgetCurrentBalance has failed", e);            
        }
        return currentBalance;
    }
    
    /**
     * This method updates the budget balance after a 
     * transaction has been deleted.
     * 
     * @param budgetId - the budget ID
     * @param isIncome - if true, need to subtract amount, else add
     * @param amount - the amount     
     */
    public static void rollbackBudgetBalance(int budgetId, boolean isIncome, double amount) {
        LOG.info("Attempting to rollback budget ID {}", budgetId);
        String query = "UPDATE budget SET current_balance = CASE WHEN ? = 'Y' THEN current_balance - ? ELSE"
                + " current_balance + ? END WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(isIncome ? "Y" : "N");
        parameters.add(amount);
        parameters.add(amount);
        parameters.add(budgetId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("rollbackBudgetBalance has failed", e);           
        }        
    }
    
    /**
     * This method retrieves the budget model for a given ID.
     * 
     * @param budgetId - the budget ID
     * @return the budget model     
     */
    public static Budget findBudgetById(int budgetId) {
        LOG.info("Attempting to find budget for budget ID {}", budgetId);
        Budget budget = new Budget();
        String query = "SELECT * FROM budget WHERE budget_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                budget.setBudgetId(results.getInt("BUDGET_ID"));
                budget.setBudgetName(results.getString("BUDGET_NAME"));
                budget.setStartDate(results.getDate("START_DATE"));
                budget.setEndDate(results.getDate("END_DATE"));
                budget.setStartBalance(results.getDouble("START_BALANCE"));
                budget.setCurrentBalance(results.getDouble("CURRENT_BALANCE"));
                budget.setActive(results.getBoolean("ACTIVE"));
            }
            LOG.info("Retrieved budget successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findBudgetById has failed", e);            
        }
        return budget;
    }
}