package budgetapp.dao;

import budgetapp.model.Category;
import budgetapp.util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles category related database operations.
 */
public class CategoryDAO {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(CategoryDAO.class); 
    
    /**
     * This method attempts to find a category in the database by name.
     * 
     * @param categoryName - the category name for which to search
     * @return the Category object, if not found then it is null     
     */
    public static Category findCategoryByName(String categoryName) {
        LOG.info("Attempting to find category {}", categoryName);
        Category category = null;
        String query = "SELECT * FROM category WHERE category_name = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(categoryName);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);            
            if(results.next()) {
                category = new Category();
                category.setCategoryId(results.getInt("CATEGORY_ID"));
                category.setCategoryName(results.getString("CATEGORY_NAME"));
                LOG.info("Category {} was found", categoryName);
            } else {
                LOG.info("Category {} was not found", categoryName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findCategoryByName has failed", e);           
        }
        return category;
    }
    
    /**
     * This method saves the category to the database.
     * 
     * @param categoryName - the category name to save
     * @return the primary key id of the newly added entry     
     */
    public static int saveCategory(String categoryName) {
        LOG.info("Attempting to save category {}", categoryName);
        int newId = 0;
        String query = "INSERT INTO category (category_id, category_name) VALUES (category_seq.nextval, ?)";
        List<Object> parameters = new ArrayList<>();
        parameters.add(categoryName);
        try {
           newId = DBUtil.dbExecuteUpdate(query, parameters, "CATEGORY_ID");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("Category {} has failed to save", categoryName, e);           
        }
        LOG.info("Category {} was saved successfully and new ID is {}", categoryName, String.valueOf(newId));
        return newId;
    }
    
    /**
     * This method gets all the existing categories.
     * 
     * @return a list of all categories     
     */
    public static List<String> getExistingCategories() {
        LOG.info("Attempting to retrieve all existing categories");
        List<String> existingCategoriesList = FXCollections.observableArrayList();
        String query = "SELECT * FROM category ORDER BY category_name";
        List<Object> parameters = new ArrayList<>();
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               existingCategoriesList.add(results.getString("CATEGORY_NAME"));
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getExistingCategories has failed", e);            
        }
        LOG.info("Categories retrieved successfully!");
        return existingCategoriesList;
    }
    
    /**
     * This method retrieves all categories for a budget ID.
     * 
     * @param budgetId - the budget ID
     * @return list of categories     
     */
    public static List<Category> getCategoriesByBudgetId(int budgetId) {
        LOG.info("Attempting to retrieve categories for budget ID {}", budgetId);
        List<Category> categoryList = new ArrayList<>();
        String query = "select * from category cat where exists (select 1 from category_budget catbud "
                + "where catbud.category_id = cat.category_id and catbud.budget_id = ?) ORDER BY cat.category_name";
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               Category category = new Category();
               category.setCategoryId(results.getInt("CATEGORY_ID"));
               category.setCategoryName(results.getString("CATEGORY_NAME"));
               categoryList.add(category);
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getCategoriesByBudgetId has failed", e);            
        }
        LOG.info("Categories retrieved successfully!");
        return categoryList;
    }
}