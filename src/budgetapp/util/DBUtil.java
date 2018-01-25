package budgetapp.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("Oracle JDBC Driver is missing", e);            
        }
        try {
            conn = DriverManager.getConnection(CONN_STR);
        } catch (SQLException e) {
            LOG.debug("Oracle JDBC Driver Registered!");
            LOG.error("Connection Failed! Check output console", e);
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
}