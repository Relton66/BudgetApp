package budgetapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The CategoryBudget model class.
 */
public class CategoryBudget {
    
    /** The budget category ID. */
    private final IntegerProperty categoryBudgetId;
    /** The budget ID. */
    private final IntegerProperty budgetId;
    /** The category ID. */
    private final IntegerProperty categoryId;
    /** The category starting amount. */
    private final DoubleProperty startBalance;
    /** The category current amount. */
    private final DoubleProperty currentBalance;
  
    /**
     * The constructor.
     */
    public CategoryBudget() {
        this.categoryBudgetId = new SimpleIntegerProperty();
        this.budgetId = new SimpleIntegerProperty();
        this.categoryId = new SimpleIntegerProperty();
        this.startBalance = new SimpleDoubleProperty();
        this.currentBalance = new SimpleDoubleProperty();
    }
    
    /**
     * Gets category budget ID value.
     * 
     * @return categoryBudgetId value
     */
    public final int getCategoryBudgetId() {
        return categoryBudgetId.get();
    }
    
    /**
     * Sets category budget ID value.
     * 
     * @param categoryBudgetId - the category budget ID value to set.
     */
    public final void setCategoryBudgetId(int categoryBudgetId) {
        this.categoryBudgetId.set(categoryBudgetId);
    }
    
    /**
     * Gets category budget ID property.
     * 
     * @return categoryBudgetId property
     */
    public IntegerProperty categoryBudgetIdProperty() {
        return categoryBudgetId;
    }
    
    /**
     * Gets budget ID value.
     * 
     * @return budgetId value
     */
    public final int getBudgetId() {
        return budgetId.get();
    }
    
    /**
     * Sets budget ID value.
     * 
     * @param budgetId - the budget ID value to set.
     */
    public final void setBudgetId(int budgetId) {
        this.budgetId.set(budgetId);
    }
    
    /**
     * Gets budget ID property.
     * 
     * @return budgetId property
     */
    public IntegerProperty budgetIdProperty() {
        return budgetId;
    }
    
    /**
     * Gets category ID value.
     * 
     * @return categoryId value
     */
    public final int getCategoryId() {
        return categoryId.get();
    }
    
    /**
     * Sets category ID value.
     * 
     * @param categoryId - the category ID value to set.
     */
    public final void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }
    
    /**
     * Gets category ID property.
     * 
     * @return categoryId property
     */
    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }
    
    /**
     * Gets start balance value.
     * 
     * @return startBalance value
     */
    public final double getStartBalance() {
        return startBalance.get();
    }
    
    /**
     * Sets start balance value.
     * 
     * @param startBalance - the start balance value to set.
     */
    public final void setStartBalance(double startBalance) {
        this.startBalance.set(startBalance);
    }
    
    /**
     * Gets start balance property.
     * 
     * @return startBalance property
     */
    public DoubleProperty startBalanceProperty() {
        return startBalance;
    }
    
    /**
     * Gets current balance value.
     * 
     * @return currentBalance value
     */
    public final double getCurrentBalance() {
        return currentBalance.get();
    }
    
    /**
     * Sets current balance value.
     * 
     * @param currentBalance - the current balance value to set.
     */
    public final void setCurrentBalance(double currentBalance) {
        this.currentBalance.set(currentBalance);
    }
    
    /**
     * Gets current balance property.
     * 
     * @return currentBalance property
     */
    public DoubleProperty currentBalanceProperty() {
        return currentBalance;
    }
}
