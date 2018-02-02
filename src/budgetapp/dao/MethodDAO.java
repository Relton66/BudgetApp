package budgetapp.dao;

import budgetapp.Main;
import budgetapp.model.MethodTableEntry;
import budgetapp.util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles method related database operations.
 */
public class MethodDAO {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(MethodDAO.class); 
    
    /**
     * This method retrieves the method ID for a given method type.
     * 
     * @param methodType - the method type
     * @return the method ID     
     */
    public static int findMethodId(String methodType) {
        LOG.info("Attempting to find method ID for method type {}", methodType);
        int methodId = 0;
        String query = "SELECT method_id FROM method WHERE method_type = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(methodType);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                methodId = results.getInt("METHOD_ID");
            }
            LOG.info("Retrieved method ID successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("findMethodId has failed", e);            
        }
        return methodId;
    }
    
    /**
     * This method gets all the methods.
     * 
     * @return the list of methods     
     */
    public static List<MethodTableEntry> getAllMethods() {
        LOG.info("Attempting to get all methods");
        List<MethodTableEntry> methodEntriesList = new ArrayList<>();
        String query = "SELECT method_type, active FROM method ORDER BY active, method_type";
        List<Object> parameters = new ArrayList<>();
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                MethodTableEntry methodEntry = new MethodTableEntry(results.getString("METHOD_TYPE"), results.getBoolean("ACTIVE"));
                methodEntriesList.add(methodEntry);
            }
            LOG.info("Retrieved methods successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getAllMethods has failed", e);           
        }
        return methodEntriesList;
    }
    
    /**
     * This method gets all the active method types.
     * 
     * @return the list of active method types     
     */
    public static List<String> getActiveMethodTypes() {
        LOG.info("Attempting to get all active method types");
        List<String> methodTypesList = new ArrayList<>();
        String query = "SELECT method_type FROM method WHERE active = '1' ORDER BY method_type";
        List<Object> parameters = new ArrayList<>();
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {                
                methodTypesList.add(results.getString("METHOD_TYPE"));                
            }
            LOG.info("Retrieved active method types successfully");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getActiveMethodTypes has failed", e);            
        }
        return methodTypesList;
    }
    
    /**
     * This method saves a method type.
     * 
     * @param methodEntry - the method table entry     
     */
    public static void saveMethod(MethodTableEntry methodEntry) {
        LOG.info("Attempting to save method {}", methodEntry.getMethodType());
        String query;
        if(Main.USE_DERBY) {
            query = "INSERT INTO method (method_type, active) VALUES (?, ?)";
        } else {
            query = "INSERT INTO method (method_id, method_type, active) VALUES (method_seq.nextval, ?, ?)";
        }
        int newId = 0;
        List<Object> parameters = new ArrayList<>();
        parameters.add(methodEntry.getMethodType());
        parameters.add(methodEntry.getActive());
        try {
           newId = DBUtil.dbExecuteUpdate(query, parameters, "METHOD_ID");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("Method {} has failed to save", methodEntry.getMethodType(), e);            
        }
        LOG.info("Method {} was saved successfully and new ID is {}", methodEntry.getMethodType(), String.valueOf(newId));
    }
    
    /**
     * This method updates a method type.
     * 
     * @param methodEntry - the method table entry     
     */
    public static void updateMethod(MethodTableEntry methodEntry) {
        LOG.info("Attempting to update method {}", methodEntry.getMethodType());
        String query = "UPDATE method SET active = ? WHERE method_type = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(methodEntry.getActive());
        parameters.add(methodEntry.getMethodType());
        try {
           DBUtil.dbExecuteUpdate(query, parameters, "");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("Method {} has failed to update", methodEntry.getMethodType(), e);            
        }
        LOG.info("Method {} was updated successfully", methodEntry.getMethodType());
    }   
}