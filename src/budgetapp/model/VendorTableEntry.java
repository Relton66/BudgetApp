package budgetapp.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the objects in the vendor table.
 */
public class VendorTableEntry {

    /** The vendor ID. */
    private final SimpleStringProperty vendorId;
    /** The vendor name. */
    private final SimpleStringProperty vendorName;
    /** The category name. */
    private final SimpleStringProperty categoryName;
    /** The active flag. */
    private final SimpleBooleanProperty active;
    
    /**
     * The constructor.
     * 
     * @param vendorId - the vendor ID
     * @param vendorName - the vendor name
     * @param categoryName - the category name
     * @param active - the active flag 
     */
    public VendorTableEntry(String vendorId, String vendorName, String categoryName, boolean active) {        
        this.vendorId = new SimpleStringProperty(vendorId);
        this.vendorName = new SimpleStringProperty(vendorName);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.active = new SimpleBooleanProperty(active);
    }
    
    /**
     * Gets vendor ID value.
     * 
     * @return vendorId value
     */
    public final String getVendorId() {
        return vendorId.get();
    }
    
    /**
     * Sets vendor ID value.
     * 
     * @param vendorId - the vendor ID value to set.
     */
    public final void setVendorId(String vendorId) {
        this.vendorId.set(vendorId);
    }
    
    /**
     * Gets vendor ID property.
     * 
     * @return vendorId property
     */
    public StringProperty vendorIdProperty() {
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
     * Gets active value.
     * 
     * @return active value
     */
    public final Boolean getActive() {
        return active.get();
    }
    
    /**
     * Sets active value.
     * 
     * @param active - the active value to set.
     */
    public final void setActive(Boolean active) {
        this.active.set(active);
    }
    
    /**
     * Gets active property.
     * 
     * @return active property
     */
    public SimpleBooleanProperty activeProperty() {
        return active;
    }
}
