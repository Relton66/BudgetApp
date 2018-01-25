package budgetapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Category model class.
 */
public class Category {
    
    /** The category ID. */
    private final IntegerProperty categoryId;
    /** The category name. */
    private final StringProperty categoryName;
    
    /**
     * The constructor.
     */
    public Category() {
        this.categoryId = new SimpleIntegerProperty();
        this.categoryName = new SimpleStringProperty();
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
}
