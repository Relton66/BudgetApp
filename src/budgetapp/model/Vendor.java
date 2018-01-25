package budgetapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Vendor model class.
 */
public class Vendor {
    
    /** The vendor ID. */
    private final IntegerProperty vendorId;
    /** The vendor name. */
    private final StringProperty vendorName;
    /** The category ID. */
    private final IntegerProperty categoryId;
    
    /**
     * The constructor.
     */
    public Vendor() {
        this.vendorId = new SimpleIntegerProperty();
        this.vendorName = new SimpleStringProperty();
        this.categoryId = new SimpleIntegerProperty();
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
     * @return vendorId property
     */
    public IntegerProperty vendorIdProperty() {
        return vendorId;
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
}
