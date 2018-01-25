package budgetapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the objects in the transaction table.
 */
public class TransactionTableEntry {
    
    /** The transaction ID. */
    private final SimpleStringProperty transactionId;
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
    
    /**
     * The constructor.
     * 
     * @param transactionId - the transaction ID
     * @param transDate - the transaction date
     * @param vendorName - the vendor name
     * @param amount - the amount
     * @param income - the income
     * @param categoryName - the category name
     * @param methodType - the method type
     */
    public TransactionTableEntry(String transactionId, String transDate, String vendorName, String amount, String income, String categoryName, String methodType) {
        this.transactionId = new SimpleStringProperty(transactionId);
        this.transDate = new SimpleStringProperty(transDate);
        this.vendorName = new SimpleStringProperty(vendorName);
        this.amount = new SimpleStringProperty(amount);
        this.income = new SimpleStringProperty(income);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.methodType = new SimpleStringProperty(methodType);
    }

    /**
     * Gets transaction ID value.
     * 
     * @return transactionId value
     */
    public final String getTransactionId() {
        return transactionId.get();
    }

    /**
     * Sets transaction ID value.
     * 
     * @param transactionId - the transaction ID to set.
     */
    public final void setTransactionId(String transactionId) {
        this.transactionId.set(transactionId);
    }
    
    /**
     * Gets transaction ID property.
     * 
     * @return transactionId property
     */
    public SimpleStringProperty transactionIdProperty() {
        return transactionId;
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
    
}
