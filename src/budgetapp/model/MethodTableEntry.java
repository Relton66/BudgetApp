package budgetapp.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the objects in the method table.
 */
public class MethodTableEntry {
    
    /** The method type. */
    private final SimpleStringProperty methodType;
    /** The active flag. */
    private final SimpleBooleanProperty active;
    
    /**
     * The constructor.
     * 
     * @param methodType - the method type
     * @param active - the active flag 
     */
    public MethodTableEntry(String methodType, boolean active) {        
        this.methodType = new SimpleStringProperty(methodType);
        this.active = new SimpleBooleanProperty(active);
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
