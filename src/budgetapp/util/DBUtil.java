package budgetapp.util;

import budgetapp.Main;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database utility class to abstract the specific connection details from the
 * rest of the application.
 */
public class DBUtil {

    /** The driver title constant. */
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    /** the DB connection. */
    private static Connection conn = null;
    /** Connection string to DB.  Format is "jdbc:oracle:thin:Username/Password@IP:Port/SID". */
    private static final String CONN_STR = "jdbc:oracle:thin:budgetapp/henry@localhost:1521/xe";
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DBUtil.class); 

    /**
     * This method connects to the database.
     * 
     * @throws SQLException - the SQL exception
     * @throws ClassNotFoundException - the ClassNotFound exception
     */
    public static void dbConnect() throws SQLException, ClassNotFoundException {
        try {
            // For development we need to load Oracle driver.
            if(!Main.IS_PRODUCTION && !Main.IS_TEST) {
                Class.forName(JDBC_DRIVER);
            }
        } catch (ClassNotFoundException e) {
            LOG.error("Oracle JDBC Driver is missing", e);            
        }
        try {
            if(Main.IS_PRODUCTION) {
                // Try to connect to prod embedded DB
                conn = DriverManager.getConnection("jdbc:derby:database/budgetAppDB");
                LOG.debug("Connected to database budgetAppDB successfully!");
            } else if(Main.IS_TEST) {
                // Try to connect to test embedded DB
                conn = DriverManager.getConnection("jdbc:derby:database/budgetAppDBTest");
                LOG.debug("Connected to database budgetAppDBTest successfully!");
            } else {
                // Try to connect to Oracle dev DB
                conn = DriverManager.getConnection(CONN_STR);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("XJ004")){
                // DB doesn't exist, have to recreate it
                if(Main.IS_PRODUCTION) {
                    conn = DriverManager.getConnection("jdbc:derby:database/budgetAppDB;create=true");
                } else {
                    conn = DriverManager.getConnection("jdbc:derby:database/budgetAppDBTest;create=true");
                }
                LOG.debug("Database created successfully!");
                try {
                    createDerbyTables();
                    LOG.debug("Tables created successfully!");
                } catch (SQLException ex) {
                    LOG.error("Could not create database!", ex);
                }
            } else {
                LOG.debug("Oracle JDBC Driver Registered!");
                LOG.error("Connection Failed! Check output console", e);
            }
        }
    }
    
    /**
     * This method disconnects the database connection.
     * 
     * @throws SQLException - the SQL exception
     */
    public static void dbDisconnect() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e){
            LOG.error("SQL Exception in dbDisconnect", e);
        }
    }

    /**
     * This method executes SELECT queries.
     * 
     * @param query - the query to execute
     * @param parameters - list of parameters to substitute
     * @return - result set if exists
     * @throws SQLException - the SQL exception
     * @throws ClassNotFoundException - the ClassNotFound exception
     */
    public static ResultSet dbExecuteSelectQuery(String query, List<Object> parameters) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        CachedRowSet crs = null;
        try {
            dbConnect();
            stmt = buildPreparedStatement(query, parameters, "");
            resultSet = stmt.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(resultSet);
        } catch (SQLException e) {
            LOG.error("Problem occurred at dbExecuteSelectQuery", e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }            
            dbDisconnect();
        }
        LOG.debug("Query executed successfully");
        return crs;
    }
    
    /**
     * This method executes update queries.
     * 
     * @param query - the query
     * @param parameters - the list of parameters for substitution
     * @param primaryKey - when primary keys need to be returned, this will contain the column name.
     * @return the newly created primary key id
     * @throws SQLException - the SQL exception
     * @throws ClassNotFoundException - the ClassNotFound exception
     */
    public static int dbExecuteUpdate(String query, List<Object> parameters, String primaryKey) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        int newId = 0;
        try {
            dbConnect();
            stmt = buildPreparedStatement(query, parameters, primaryKey);
            stmt.executeUpdate();
            // If no primary key was passed in, we're only doing an update or delete
            // so no need for generated keys.
            if(!"".equals(primaryKey)) {
                generatedKeys = stmt.getGeneratedKeys();
                if(generatedKeys != null && generatedKeys.next())
                {
                    newId = generatedKeys.getInt(1);
                }
            }            
            LOG.debug("Query executed successfully");
            
        } catch (SQLException e) {
            LOG.error("Problem occurred at dbExecuteUpdate", e);            
        } finally {
            if(generatedKeys != null) {
                generatedKeys.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();            
        }
        return newId;
    }
    
    /**
     * This method builds the statement with the given query and loops through the parameters for substitution.
     * 
     * @param query - the query
     * @param parameters - the list of parameters for substitution
     * @param primaryKey - when primary keys need to be returned, this will contain the column name.
     * @return the prepared statement
     * @throws SQLException - the SQL exception
     */
    private static PreparedStatement buildPreparedStatement(String query, List<Object> parameters, String primaryKey) throws SQLException {
        PreparedStatement stmt;
        if(!"".equals(primaryKey)) {
            String generatedColumns[] = { primaryKey };
            stmt = conn.prepareStatement(query, generatedColumns);
        } else {
            stmt = conn.prepareStatement(query);
        }
        LOG.debug("Statement to execute: " + query + "\n");
        for(int i=0; i < parameters.size(); i++) {
            if(parameters.get(i) instanceof String) {
                stmt.setString(i+1, parameters.get(i).toString());
                LOG.debug("Parameter {} is {}", i+1, parameters.get(i).toString());
            } else if (parameters.get(i) instanceof Integer) {
                stmt.setInt(i+1, Integer.parseInt(parameters.get(i).toString()));
                LOG.debug("Parameter {} is {}", i+1, Integer.parseInt(parameters.get(i).toString()));
            } else if (parameters.get(i) instanceof Double) {
                stmt.setDouble(i+1, Double.parseDouble(parameters.get(i).toString()));
                LOG.debug("Parameter {} is {}", i+1, Double.parseDouble(parameters.get(i).toString()));
            } else if (parameters.get(i) instanceof Date) {
                stmt.setDate(i+1, Date.valueOf(parameters.get(i).toString()));
                LOG.debug("Parameter {} is {}", i+1, Date.valueOf(parameters.get(i).toString()));
            } else if (parameters.get(i) instanceof Boolean) {
                stmt.setBoolean(i+1, Boolean.valueOf(parameters.get(i).toString()));
                LOG.debug("Parameter {} is {}", i+1, Boolean.valueOf(parameters.get(i).toString()));
            }
        }
        return stmt;
    }
    
    /**
     * This method creates the Derby DB tables and sequences.
     * 
     * @throws SQLException - the SQL exception
     */
    private static void createDerbyTables() throws SQLException {        
        try {
            // Create budget table
            executeQuery("CREATE TABLE budget (budget_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), budget_name VARCHAR(50), start_date DATE, end_date DATE, "
                + "start_balance DECIMAL(15,2), current_balance DECIMAL(15,2), current_flag BOOLEAN)");
                     
            // Create category table            
            executeQuery("CREATE TABLE category (category_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), category_name VARCHAR(50), active BOOLEAN)");
                        
            // Create vendor table            
            executeQuery("CREATE TABLE vendor (vendor_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), vendor_name VARCHAR(50), category_id INT, active BOOLEAN, CONSTRAINT "
                + "fk_vendor_category_id FOREIGN KEY (category_id) REFERENCES category(category_id))");
            
            // Create category budget table            
            executeQuery("CREATE TABLE category_budget (category_budget_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), budget_id INT, category_id INT, start_balance DECIMAL(15,2), "
                + "current_balance DECIMAL(15,2), CONSTRAINT fk_category_budget_budget_id FOREIGN KEY (budget_id) REFERENCES "
                + "budget(budget_id), CONSTRAINT fk_category_budget_category_id FOREIGN KEY (category_id) REFERENCES "
                + "category(category_id))");
            
            // Create method table
            executeQuery("CREATE TABLE method (method_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), method_type VARCHAR(50), active BOOLEAN)");
            
            // Create transactions table
            executeQuery("CREATE TABLE transactions (transaction_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY "
                + "(START WITH 1, INCREMENT BY 1), amount DECIMAL(15,2), "
                + "income BOOLEAN, trans_date DATE, method_id INT, vendor_id INT, budget_id INT, comments VARCHAR(100), "
                + "CONSTRAINT fk_transaction_vendor_id FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id), CONSTRAINT "
                + "fk_transaction_budget_id FOREIGN KEY (budget_id) REFERENCES budget(budget_id), CONSTRAINT "
                + "fk_transaction_method_id FOREIGN KEY (method_id) REFERENCES method(method_id))");            
                     
        } catch (SQLException e) {
            LOG.error("Problem occurred at createDerbyTables", e);
        }
    }
    
    /**
     * This method just executes a statement.
     * 
     * @param query - the query to execute
     * @throws SQLException - the SQL exception
     */
    private static void executeQuery(String query) throws SQLException {
        Statement stmt = null;
        try {
            //stmt = conn.prepareStatement(query);
            stmt = conn.createStatement();
            LOG.debug("Query to execute is: " + query);
            stmt.execute(query);
            LOG.debug("Query successful.");
        } catch (SQLException e) {
            LOG.error("Problem occurred at executeQuery", e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}