package budgetapp.dao;

import budgetapp.Main;
import budgetapp.model.Category;
import budgetapp.model.Vendor;
import budgetapp.model.VendorTableEntry;
import budgetapp.util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles vendor related database operations.
 */
public class VendorDAO {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(VendorDAO.class);
    
    /**
     * This method will attempt to find a vendor by name.
     * 
     * @param vendorName - the vendor name
     * @return a Vendor object if found, otherwise null     
     */
    public static Vendor findVendorByName(String vendorName) {
        LOG.info("Attempting to find vendor {}", vendorName);
        Vendor vendor = null;
        String query;
        if(Main.USE_DERBY) {
            query = "SELECT * FROM vendor WHERE vendor_name = ? AND active = true";
        } else {
            query = "SELECT * FROM vendor WHERE vendor_name = ? AND active = '1'";
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(vendorName);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);            
            if(results.next()) {
                vendor = new Vendor();
                vendor.setVendorId(results.getInt("VENDOR_ID"));
                vendor.setVendorName(results.getString("VENDOR_NAME"));
                vendor.setCategoryId(results.getInt("CATEGORY_ID"));
                vendor.setActive(results.getBoolean("ACTIVE"));
                LOG.info("Vendor {} was found", vendorName);
            } else {
                LOG.info("Vendor {} was not found", vendorName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findVendorByName has failed", e);           
        }
        return vendor;
    }
    
    /**
     * This method saves the vendor to the database.
     * 
     * @param vendorName - the vendor name
     * @param categoryName - the category name     
     * @return primary key id of newly saved vendor     
     */
    public static int saveVendor(String vendorName, String categoryName) {
        LOG.info("Attempting to save vendor {} and category {}", vendorName, categoryName);
        int vendorId = 0;
        String query;
        if(Main.USE_DERBY) {
            query = "INSERT INTO vendor (vendor_name, category_id, active) VALUES (?, ?, true)";
        } else {
            query = "INSERT INTO vendor (vendor_id, vendor_name, category_id, active) VALUES (vendor_seq.nextval, ?, ?, '1')";
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(vendorName);
        parameters.add(CategoryDAO.findCategoryByName(categoryName).getCategoryId());       
        try {
           vendorId = DBUtil.dbExecuteUpdate(query, parameters, "VENDOR_ID");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("saveVendor has failed", e);            
        }
        LOG.info("Vendor {} was saved successfully!", vendorName);
        return vendorId;
    }
    
    
    
    /**
     * This method determines if the vendor name already exists.
     * 
     * @param vendorName - the vendor name
     * @return true if vendor exists     
     */
    public static boolean vendorNameAlreadyExists(String vendorName) {
        Vendor vendor = findVendorByName(vendorName);
        return vendor != null;
    }
    
    /**
     * This method gets all the existing vendor names.
     * 
     * @return a list of all vendor names
     */
    public static ObservableList<String> getExistingVendorNames() {
        LOG.info("Attempting to retrieve all existing vendor names");
        ObservableList<String> existingVendorList = FXCollections.observableArrayList();
        String query;
        if(Main.USE_DERBY) {
            query = "SELECT * FROM vendor WHERE active = true ORDER BY vendor_name";
        } else {
            query = "SELECT * FROM vendor WHERE active = '1' ORDER BY vendor_name";
        }
        List<Object> parameters = new ArrayList<>();
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               existingVendorList.add(results.getString("VENDOR_NAME"));
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getExistingVendorNames has failed", e);            
        }
        LOG.info("Vendor names retrieved successfully!");
        return existingVendorList;
    }
    
    /**
     * This method retrieves the category for a given vendor ID.
     * 
     * @param vendorId - the vendor ID
     * @return the category for the given vendor ID     
     */
    public static Category findCategoryByVendorId(int vendorId) {
        LOG.info("Attemping to retrieve the cagetory for vendor ID {}", vendorId);
        Category category = new Category();
        String query = "SELECT * FROM category cat JOIN vendor ven ON ven.category_id = cat.category_id WHERE ven.vendor_id = ?";
        if(Main.USE_DERBY) {
            query += " AND ven.active = true";
        } else {
            query += " AND ven.active = '1'";
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(vendorId);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);            
            if(results.next()) {
                category.setCategoryId(results.getInt("CATEGORY_ID"));
                category.setCategoryName(results.getString("CATEGORY_NAME"));
                LOG.info("Category ID {} and name {} was found", category.getCategoryId(), 
                        category.getCategoryName());
            } else {
                LOG.info("Category was not found for vendor ID {}", vendorId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findCategoryByVendorId has failed", e);            
        }
        return category;
    }
    
    /**
     * This method updates the vendor active flag.
     * 
     * @param vendorId - the vendor ID
     * @param active - the active flag
     */
    public static void updateVendorActive(int vendorId, boolean active) {
        LOG.info("Attempting to update vendor ID {} to {}", vendorId, active);
        String query = "UPDATE vendor SET active = ? WHERE vendor_id = ?";
        List<Object> parameters = new ArrayList<>();
        if(Main.USE_DERBY) {
            parameters.add(active);
        } else {
            parameters.add(active ? "1" : "0");
        }
        parameters.add(vendorId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateVendorActive has failed", e);            
        }
        LOG.info("Vendor ID {} was updated successfully!", vendorId);
    }
    
    /**
     * This method gets all the data for the vendor category table.
     * 
     * @return a list of vendor table entries
     */
    public static List<VendorTableEntry> getVendorTableList() {
        LOG.info("Attempting to get the vendor table data");
        List<VendorTableEntry> vendorTableList = new ArrayList<>();
        String query = "SELECT ven.vendor_id, ven.vendor_name, cat.category_name, ven.active FROM vendor ven JOIN "
            + "category cat ON ven.category_id = cat.category_id AND ";
        if(Main.USE_DERBY) {
            query += "ven.active = true ORDER BY ven.vendor_name";
        } else {
            query += "ven.active = '1' ORDER BY ven.vendor_name";
        }
        List<Object> parameters = new ArrayList<>();       
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);            
            while(results.next()) {
                VendorTableEntry vendorTableEntry = new VendorTableEntry(results.getString("VENDOR_ID"),
                    results.getString("VENDOR_NAME"), results.getString("CATEGORY_NAME"), results.getBoolean("ACTIVE"));
                vendorTableList.add(vendorTableEntry);
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getVendorTableList has failed", e);            
        }
        return vendorTableList;
    }
    
    /**
     * This method renames a vendor.
     * 
     * @param vendorId - the vendor ID
     * @param vendorName - the new vendor name
     */
    public static void renameVendor(int vendorId, String vendorName) {
        LOG.info("Attempting to rename vendor ID {} to new name {}", vendorId, vendorName);
        String query = "UPDATE vendor SET vendor_name = ? WHERE vendor_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(vendorName);       
        parameters.add(vendorId);
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("renameVendor has failed", e);            
        }
        LOG.info("Vendor ID {} was renamed successfully!", vendorId);
    }
    
    /**
     * This method gets all the existing vendors.
     * 
     * @return a list of all vendors     
     */
    public static List<Vendor> getExistingVendors() {
        LOG.info("Attempting to retrieve all existing vendors");
        List<Vendor> existingVendorList = new ArrayList<>();
        String query;
        if(Main.USE_DERBY) {
            query = "SELECT * FROM vendor WHERE active = true ORDER BY vendor_name";
        } else {
            query = "SELECT * FROM vendor WHERE active = '1' ORDER BY vendor_name";
        }
        List<Object> parameters = new ArrayList<>();
        try {
           ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
           while(results.next()) {
               Vendor vendor = new Vendor();
               vendor.setVendorId(results.getInt("VENDOR_ID"));
               vendor.setVendorName(results.getString("VENDOR_NAME"));
               vendor.setCategoryId(results.getInt("CATEGORY_ID"));
               vendor.setActive(results.getBoolean("ACTIVE"));
               existingVendorList.add(vendor);
           }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getExistingVendors has failed", e);            
        }
        LOG.info("Vendors retrieved successfully!");
        return existingVendorList;
    }
}