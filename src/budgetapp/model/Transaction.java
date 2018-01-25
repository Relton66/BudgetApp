package budgetapp.model;

import java.sql.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The Transaction model class.
 */
public class Transaction {
    
    /** The transaction ID. */
    private final IntegerProperty transactionId;
    /** The amount. */
    private final DoubleProperty amount;
    /** The income flag. */
    private final BooleanProperty income;
    /** The transaction date. */
    private final SimpleObjectProperty<Date> transDate;
    /** The method type. */
    private final IntegerProperty methodId;
    /** The vendor ID. */
    private final IntegerProperty vendorId;
    /** The budget ID. */
    private final IntegerProperty budgetId;
    
    /**
     * The constructor.
     */
    public Transaction() {
        this.transactionId = new SimpleIntegerProperty();
        this.amount = new SimpleDoubleProperty();
        this.income = new SimpleBooleanProperty();
        this.transDate = new SimpleObjectProperty<>();
        this.vendorId = new SimpleIntegerProperty();
        this.budgetId = new SimpleIntegerProperty();
        this.methodId = new SimpleIntegerProperty();
    }

    /**
     * Gets transaction ID value.
     * 
     * @return transactionId value
     */
    public final int getTransactionId() {
        return transactionId.get();
    }

    /**
     * Sets transaction ID value.
     * 
     * @param transactionId - the transaction ID value to set.
     */
    public final void setTransactionId(int transactionId) {
        this.transactionId.set(transactionId);
    }
    
    /**
     * Gets transaction ID property.
     * 
     * @return transactionId property
     */
    public IntegerProperty transactionIdProperty() {
        return transactionId;
    }

    /**
     * Gets amount value.
     * 
     * @return amount value
     */
    public final double getAmount() {
        return amount.get();
    }

    /**
     * Sets amount value.
     * 
     * @param amount - the amount value to set.
     */
    public final void setAmount(double amount) {
        this.amount.set(amount);
    }
    
    /**
     * Gets amount property.
     * 
     * @return amount property
     */
    public DoubleProperty amountProperty() {
        return amount;
    }

    /**
     * Gets income value.
     * 
     * @return income value
     */
    public final boolean getIncome() {
        return income.get();
    }

    /**
     * Sets income value.
     * 
     * @param income - the income value to set.
     */
    public final void setIncome(boolean income) {
        this.income.set(income);
    }
    
    /**
     * Gets income property.
     * 
     * @return income property
     */
    public BooleanProperty incomeProperty() {
        return income;
    }

    /**
     * Gets transaction date value.
     * 
     * @return transDate value
     */
    public final Object getTransDate() {
        return transDate.get();
    }

    /**
     * Sets transaction date value.
     * 
     * @param transDate - the transaction date to set.
     */
    public final void setTransDate(Date transDate) {
        this.transDate.set(transDate);
    }
    
    /**
     * Gets transaction date property.
     * 
     * @return transDate property
     */
    public SimpleObjectProperty<Date> transDateProperty() {
        return transDate;
    }
    
    /**
     * Gets method ID value.
     * 
     * @return methodId value
     */
    public final int getMethodId() {
        return methodId.get();
    }

    /**
     * Sets method ID value.
     * 
     * @param methodId - the method ID value to set.
     */
    public final void setMethodId(int methodId) {
        this.methodId.set(methodId);
    }
    
    /**
     * Gets method ID property.
     * 
     * @return methodId property
     */
    public IntegerProperty methodIdProperty() {
        return methodId;
    }

    /**
     * Gets vendor ID value.
     * 
     * @return vendorId value
     */
    public final int getVendorId() {
        return vendorId.get();
    }

    /**
     * Sets vendor ID value.
     * 
     * @param vendorId - the vendor ID value to set.
     */
    public final void setVendorId(int vendorId) {
        this.vendorId.set(vendorId);
    }
    
    /**
     * Gets vendor ID property.
     * 
     * @return vendorID property
     */
    public IntegerProperty vendorIdProperty() {
        return vendorId;
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
}
