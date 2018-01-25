package budgetapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the objects in the category budget table.
 */
public class CategoryBudgetTableEntry {
    
    /** The category name. */
    private final SimpleStringProperty categoryName;
    /** The budget starting amount. */
    private final SimpleStringProperty budgetStarting;
    /** The budget remaining amount. */
    private final SimpleStringProperty budgetRemaining;
 
    /**
     * The constructor for home screen.
     * 
     * @param categoryName - the category name
     * @param budgetStarting - the budget starting amount
     * @param budgetRemaining - the budget remaining amount
     */
    public CategoryBudgetTableEntry(String categoryName, String budgetStarting, String budgetRemaining) {
        this.categoryName = new SimpleStringProperty(categoryName);
        this.budgetStarting = new SimpleStringProperty(budgetStarting);
        this.budgetRemaining = new SimpleStringProperty(budgetRemaining);   
    }
    
    /**
     * The constructor for add budget screen.
     * 
     * @param categoryName - the category name
     * @param budgetStarting - the budget starting amount     
     */
    public CategoryBudgetTableEntry(String categoryName, String budgetStarting) {
        this.categoryName = new SimpleStringProperty(categoryName);
        this.budgetStarting = new SimpleStringProperty(budgetStarting);
        this.budgetRemaining = this.budgetStarting;
    }
 
    /**
     * Gets category name value.
     * 
     * @return categoryName value
     */
    public final String getCategoryName() {
        return categoryName.get();
    }
    
    /**
     * Sets category name value.
     * 
     * @param categoryName - the category name value to set.
     */
    public final void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }
    
    /**
     * Gets category name property.
     * 
     * @return categoryName property
     */
    public StringProperty categoryNameProperty() {
        return categoryName;
    }
    
    /**
     * Gets budget starting value.
     * 
     * @return budgetStarting value
     */
    public final String getBudgetStarting() {
        return budgetStarting.get();
    }
    
    /**
     * Sets budget starting value.
     * 
     * @param budgetStarting - the budget starting to set.
     */
    public final void setBudgetStarting(String budgetStarting) {
        this.budgetStarting.set(budgetStarting);
    }
    
    /**
     * Gets budget starting property.
     * 
     * @return budgetStarting property
     */
    public StringProperty budgetStartingProperty() {
        return budgetStarting;
    }        
    
    /**
     * Gets budget remaining value.
     * 
     * @return budgetRemaining value
     */
    public final String getBudgetRemaining() {
        return budgetRemaining.get();
    }
    
    /**
     * Sets budget remaining value.
     * 
     * @param budgetRemaining - the budget remaining to set.
     */
    public final void setBudgetRemaining(String budgetRemaining) {
        this.budgetRemaining.set(budgetRemaining);
    }
    
    /**
     * Gets budget remaining property.
     * 
     * @return budgetRemaining property
     */
    public StringProperty budgetRemainingProperty() {
        return budgetRemaining;
    }        
}