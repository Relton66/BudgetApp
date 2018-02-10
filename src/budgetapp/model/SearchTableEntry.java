package budgetapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the objects in the search table.
 */
public class SearchTableEntry {
    
    /** The budget name. */
    private final SimpleStringProperty budgetName;
    /** The transaction date. */
    private final SimpleStringProperty transDate;
    /** The vendor name. */
    private final SimpleStringProperty vendorName;
    /** The amount. */
    private final SimpleStringProperty amount;
    /** The income flag. */
    private final SimpleStringProperty income;
    /** The category name. */
    private final SimpleStringProperty categoryName;
    /** The method name. */
    private final SimpleStringProperty methodType;
    /** The comments. */
    private final SimpleStringProperty comments;
    
    /**
     * The constructor.
     * 
     * @param budgetName - the budget name
     * @param transDate - the transaction date
     * @param vendorName - the vendor name
     * @param amount - the amount
     * @param income - the income
     * @param categoryName - the category name
     * @param methodType - the method type
     * @param comments - the comments
     */
    public SearchTableEntry(String budgetName, String transDate, String vendorName,
            String amount, String income, String categoryName, String methodType, String comments) {
        this.budgetName = new SimpleStringProperty(budgetName);
        this.transDate = new SimpleStringProperty(transDate);
        this.vendorName = new SimpleStringProperty(vendorName);
        this.amount = new SimpleStringProperty(amount);
        this.income = new SimpleStringProperty(income);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.methodType = new SimpleStringProperty(methodType);
        this.comments = new SimpleStringProperty(comments);
    }
    
    /**
     * Gets budget name value.
     * 
     * @return budgetName value
     */
    public final String getBudgetName() {
        return budgetName.get();
    }

    /**
     * Sets budget name value.
     * 
     * @param budgetName - the budget name to set.
     */
    public final void setBudgetName(String budgetName) {
        this.budgetName.set(budgetName);
    }
    
    /**
     * Gets budget name property.
     * 
     * @return budgetName property
     */
    public SimpleStringProperty budgetNameProperty() {
        return budgetName;
    }
    
    /**
     * Gets transaction date value.
     * 
     * @return transDate value
     */
    public final String getTransDate() {
        return transDate.get();
    }

    /**
     * Sets transaction date value.
     * 
     * @param transDate - the transaction date to set.
     */
    public final void setTransDate(String transDate) {
        this.transDate.set(transDate);
    }
    
    /**
     * Gets transaction date property.
     * 
     * @return transDate property
     */
    public SimpleStringProperty transDateProperty() {
        return transDate;
    }
    
    /**
     * Gets vendor name value.
     * 
     * @return vendorName value
     */
    public final String getVendorName() {
        return vendorName.get();
    }
    
    /**
     * Sets vendor name value.
     * 
     * @param vendorName - the vendor name value to set.
     */
    public final void setVendorName(String vendorName) {
        this.vendorName.set(vendorName);
    }
    
    /**
     * Gets vendor name property.
     * 
     * @return vendorName property
     */
    public StringProperty vendorNameProperty() {
        return vendorName;
    }
    
    /**
     * Gets amount value.
     * 
     * @return amount value
     */
    public final String getAmount() {
        return amount.get();
    }

    /**
     * Sets amount value.
     * 
     * @param amount - the amount value to set.
     */
    public final void setAmount(String amount) {
        this.amount.set(amount);
    }
    
    /**
     * Gets amount property.
     * 
     * @return amount property
     */
    public StringProperty amountProperty() {
        return amount;
    }

    /**
     * Gets income value.
     * 
     * @return income value
     */
    public final String getIncome() {
        return income.get();
    }

    /**
     * Sets income value.
     * 
     * @param income - the income value to set.
     */
    public final void setIncome(String income) {
        this.income.set("1".equals(income) ? "YES" : "NO");
    }
    
    /**
     * Gets income property.
     * 
     * @return income property
     */
    public StringProperty incomeProperty() {
        return income;
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
     * Gets method type value.
     * 
     * @return methodType value
     */
    public final String getMethodType() {
        return methodType.get();
    }
    
    /**
     * Sets method type value.
     * 
     * @param methodType - the method type value to set.
     */
    public final void setMethodType(String methodType) {
        this.methodType.set(methodType);
    }
    
    /**
     * Gets method type property.
     * 
     * @return methodType property
     */
    public StringProperty methodTypeProperty() {
        return methodType;
    }
    
    /**
     * Gets comments value.
     * 
     * @return comments value
     */
    public final String getComments() {
        return comments.get();
    }
    
    /**
     * Sets comments value.
     * 
     * @param comments - the comments value to set.
     */
    public final void setComments(String comments) {
        this.comments.set(comments);
    }
    
    /**
     * Gets comments property.
     * 
     * @return comments property
     */
    public StringProperty commentsProperty() {
        return comments;
    }
}
