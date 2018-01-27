package budgetapp.dao;

import budgetapp.model.CategoryBudgetTableEntry;
import budgetapp.util.DBUtil;
import budgetapp.util.StringUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles category budget related database operations.
 */
public class CategoryBudgetDAO {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(CategoryBudgetDAO.class); 
        
    /**
     * This method saves the category budget to the database.
     * 
     * @param budgetId - the budget ID to save
     * @param categoryId - the category ID to save
     * @param startBalance - the start balance to save
     * @return the new budget ID     
     */
    public static int saveCategoryBudget(int budgetId, int categoryId, double startBalance) {
        LOG.info("Attempting to save category budget for budget ID {} and category ID {}", budgetId, categoryId);
        int newId = 0;
        String query = "INSERT INTO category_budget (category_budget_id, budget_id, category_id, start_balance, current_balance)"
                + " values (category_budget_seq.nextval, ?, ?, ?, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        parameters.add(categoryId);
        parameters.add(startBalance);
        parameters.add(startBalance);
        try {
           newId = DBUtil.dbExecuteUpdate(query, parameters, "CATEGORY_BUDGET_ID");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("Failed to save in saveCategoryBudget", e);          
        }
        LOG.info("Budget category was saved successfully and new ID is {}", String.valueOf(newId));
        return newId;
    }
    
    /**
     * This method will retrieve all category budgets for a budget ID.
     * 
     * @param budgetId - the budget ID
     * @return the list of category budgets     
     */
    public static List<CategoryBudgetTableEntry> getCategoryBudgetsForTable(int budgetId) {
        LOG.info("Attempting to get category budgets for budget ID {}", budgetId);
        List<CategoryBudgetTableEntry> categoryBudgetList = new ArrayList<>();
        String query = "SELECT cat.category_id, cat.category_name, catbud.start_balance, catbud.current_balance "
                + "FROM category_budget catbud JOIN category cat ON catbud.category_id = cat.category_id "
                + "WHERE catbud.budget_id = ? ORDER BY cat.category_name";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               CategoryBudgetTableEntry categoryBudget = new CategoryBudgetTableEntry();
               categoryBudget.setCategoryId(results.getString("CATEGORY_ID"));
               categoryBudget.setCategoryName(results.getString("CATEGORY_NAME"));
               categoryBudget.setBudgetStarting(StringUtil.convertToDollarFormat(results.getString("START_BALANCE")));
               categoryBudget.setBudgetRemaining(StringUtil.convertToDollarFormat(results.getString("CURRENT_BALANCE")));               
               categoryBudgetList.add(categoryBudget);
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getCategoryBudgetsForTable has failed", e);           
        }
        LOG.info("Category budgets retrieved successfully!");
        return categoryBudgetList;
    }
    
    /**
     * This method updates the category budget current balance.
     * 
     * @param budgetId - the budget ID
     * @param categoryId - the category ID
     * @param isIncome - if true, add amount, else subtract
     * @param amount - the amount    
     */
    public static void updateCategoryBalance(int budgetId, int categoryId, boolean isIncome, double amount) {
        LOG.info("Attempting to update category budget for budget ID {} and category ID {}", budgetId, categoryId);
        String query = "UPDATE category_budget SET current_balance = CASE WHEN ? = 'Y' THEN current_balance + ? ELSE"
                + " current_balance - ? END WHERE budget_id = ? and category_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(isIncome ? "Y" : "N");
        parameters.add(amount);
        parameters.add(amount);
        parameters.add(budgetId);
        parameters.add(categoryId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Category budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateCategoryBalance has failed", e);            
        }        
    }
    
    /**
     * This method reverts the balance of a category budget after a
     * transaction is deleted.
     * 
     * @param budgetId - the budget ID
     * @param isIncome - true if transaction was income
     * @param amount - the amount
     * @param categoryId - the category ID     
     */
    public static void rollbackBalance(int budgetId, boolean isIncome, double amount, int categoryId) {
        LOG.info("Attempting to rollback category budget balance for budget ID {} and category ID {}", budgetId, categoryId);
        String query = "UPDATE category_budget SET current_balance = CASE WHEN ? = 'Y' THEN current_balance - ? ELSE"
                + " current_balance + ? END WHERE budget_id = ? and category_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(isIncome ? "Y" : "N");
        parameters.add(amount);
        parameters.add(amount);
        parameters.add(budgetId);
        parameters.add(categoryId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Category budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("rollbackBalance has failed", e);           
        }        
    }
    
    /**
     * This method will retrieve all data for the category report.
     * 
     * @param budgetId - the budget ID
     * @return the list of category budgets for category report     
     */
    public static List<CategoryBudgetTableEntry> getDataForCategoryReport(int budgetId) {
        LOG.info("Attempting to get data for category report for budget ID {}", budgetId);
        List<CategoryBudgetTableEntry> categoryDataList = new ArrayList<>();
        String query = "SELECT cat.category_name, (catbud.start_balance - catbud.current_balance) AS amount_spent "
                + "FROM category_budget catbud JOIN category cat ON catbud.category_id = cat.category_id WHERE"
                + " catbud.budget_id = ? ORDER BY cat.category_name";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               CategoryBudgetTableEntry categoryBudget = new CategoryBudgetTableEntry();
               categoryBudget.setCategoryName(results.getString("CATEGORY_NAME"));
               categoryBudget.setBudgetRemaining(results.getString("AMOUNT_SPENT"));
               categoryDataList.add(categoryBudget);
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getDataForCategoryReport has failed", e);           
        }
        LOG.info("Category data retrieved successfully!");
        return categoryDataList;
    }
    
    /**
     * This method updates the category budget current balance.
     * 
     * @param budgetId - the budget ID
     * @param categoryId - the category ID
     * @param startBalance - the starting balance    
     * @param currentBalanceAdjustment - the amount to adjust current balance
     * @param amountIncreased - if true need to increase current balance
     */
    public static void updateStartingBalance(int budgetId, int categoryId, double startBalance,
            double currentBalanceAdjustment, boolean amountIncreased) {
        LOG.info("Attempting to update starting balance for budget ID {} and category ID {}", budgetId, categoryId);
        String query = "UPDATE category_budget SET start_balance = ?, ";
        if(amountIncreased) {
            query += "current_balance = (current_balance + ?) ";
        } else {
            query += "current_balance = (current_balance - ?) ";
        }
        query += "WHERE budget_id = ? and category_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(startBalance);
        parameters.add(currentBalanceAdjustment);
        parameters.add(budgetId);
        parameters.add(categoryId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Category budget updated successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateStartingBalance has failed", e);            
        }        
    }
    
    /**
     * This method deletes a category budget.
     * 
     * @param budgetId - the budget ID
     * @param categoryId - the category ID
     */
    public static void deleteCategoryBudget(int budgetId, int categoryId) {
        LOG.info("Attempting to delete category budget with budget ID {} and category ID {}", budgetId, categoryId);
        String query = "DELETE FROM category_budget WHERE budget_id = ? and category_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        parameters.add(categoryId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Category budget deleted successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("deleteCategoryBudget has failed", e);           
        }        
    }
}