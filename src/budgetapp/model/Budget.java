package budgetapp.model;

import java.sql.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Budget model class.
 */
public class Budget {
    
    /** The budget ID. */
    private final IntegerProperty budgetId;
    /** The budget name. */
    private final StringProperty budgetName;
    /** The budget start date. */
    private final SimpleObjectProperty<Date> startDate;
    /** The budget end date. */
    private final SimpleObjectProperty<Date> endDate;
    /** The budget starting balance. */
    private final DoubleProperty startBalance;
    /** The budget current balance. */
    private final DoubleProperty currentBalance;
    /** The active flag. */
    private final BooleanProperty currentFlag;
    
    /**
     * The constructor.
     */
    public Budget() {
        this.budgetId = new SimpleIntegerProperty();
        this.budgetName = new SimpleStringProperty();
        this.startDate = new SimpleObjectProperty<>();
        this.endDate = new SimpleObjectProperty<>();
        this.startBalance = new SimpleDoubleProperty();
        this.currentBalance = new SimpleDoubleProperty();
        this.currentFlag = new SimpleBooleanProperty();
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
     * @param budgetName - the budget name value to set.
     */
    public final void setBudgetName(String budgetName) {
        this.budgetName.set(budgetName);
    }
    
    /**
     * Gets budget name property.
     * 
     * @return budgetName property
     */
    public StringProperty budgetNameProperty() {
        return budgetName;
    }
    
    /**
     * Gets budget start date value.
     * 
     * @return startDate value
     */
    public final Date getStartDate() {
        return startDate.get();
    }

    /**
     * Sets budget start date value.
     * 
     * @param startDate - the budget start date to set.
     */
    public final void setStartDate(Date startDate) {
        this.startDate.set(startDate);
    }
    
    /**
     * Gets budget start date property.
     * 
     * @return startDate property
     */
    public SimpleObjectProperty<Date> startDateProperty() {
        return startDate;
    }
    
    /**
     * Gets budget end date value.
     * 
     * @return endDate value
     */
    public final Date getEndDate() {
        return endDate.get();
    }

    /**
     * Sets budget end date value.
     * 
     * @param endDate - the budget end date to set.
     */
    public final void setEndDate(Date endDate) {
        this.endDate.set(endDate);
    }
    
    /**
     * Gets budget end date property.
     * 
     * @return endDate property
     */
    public SimpleObjectProperty<Date> endDateProperty() {
        return endDate;
    }
    
    /**
     * Gets budget start balance value.
     * 
     * @return startBalance value
     */
    public final double getStartBalance() {
        return startBalance.get();
    }
    
    /**
     * Sets budget start balance value.
     * 
     * @param startBalance - the budget start balance to set.
     */
    public final void setStartBalance(double startBalance) {
        this.startBalance.set(startBalance);
    }
    
    /**
     * Gets budget start balance property.
     * 
     * @return startBalance property
     */
    public DoubleProperty startBalanceProperty() {
        return startBalance;
    }
    
    /**
     * Gets budget current balance value.
     * 
     * @return currentBalance value
     */
    public final double getCurrentBalance() {
        return currentBalance.get();
    }
    
    /**
     * Sets budget current balance value.
     * 
     * @param currentBalance - the budget current balance to set.
     */
    public final void setCurrentBalance(double currentBalance) {
        this.currentBalance.set(currentBalance);
    }
    
    /**
     * Gets budget current balance property.
     * 
     * @return currentBalance property
     */
    public DoubleProperty currentBalanceProperty() {
        return currentBalance;
    }
    
    /**
     * Gets currentFlag value.
     * 
     * @return currentFlag value
     */
    public final boolean getCurrentFlag() {
        return currentFlag.get();
    }

    /**
     * Sets currentFlag value.
     * 
     * @param currentFlag - the currentFlag value to set.
     */
    public final void setCurrentFlag(boolean currentFlag) {
        this.currentFlag.set(currentFlag);
    }
    
    /**
     * Gets currentFlag property.
     * 
     * @return currentFlag property
     */
    public BooleanProperty currentFlagProperty() {
        return currentFlag;
    }
}
