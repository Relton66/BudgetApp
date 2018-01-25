package budgetapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Method model class.
 */
public class Method {
    
    /** The method ID. */
    private final IntegerProperty methodId;
    /** The method type. */
    private final StringProperty methodType;
    /** The active flag. */
    private final BooleanProperty active;
    
    /**
     * The constructor.
     */
    public Method() {
        this.methodId = new SimpleIntegerProperty();
        this.methodType = new SimpleStringProperty();
        this.active = new SimpleBooleanProperty();
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
    public final boolean getActive() {
        return active.get();
    }

    /**
     * Sets active value.
     * 
     * @param active - the active value to set.
     */
    public final void setActive(boolean active) {
        this.active.set(active);
    }
    
    /**
     * Gets active property.
     * 
     * @return active property
     */
    public BooleanProperty activeProperty() {
        return active;
    }
}
