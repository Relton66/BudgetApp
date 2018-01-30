package budgetapp.dao;

import budgetapp.model.Transaction;
import budgetapp.model.TransactionTableEntry;
import budgetapp.util.DBUtil;
import budgetapp.util.StringUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles transaction related database operations.
 */
public class TransactionDAO {
    
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(TransactionDAO.class);

    /**
     * This method saves the transaction.
     * 
     * @param transaction - the Transaction class with all the required data
     * @return the primary key ID of the newly saved transaction    
     */
    public static int saveTransaction(Transaction transaction) {
        LOG.info("Attempting to save transaction");
        int newId = 0;
        boolean noMethodId = transaction.getMethodId() == 0;
        String query;        
        if(noMethodId) {
            query = "INSERT INTO transaction (transaction_id, amount, income, trans_date, vendor_id, comments, budget_id) values (transaction_seq.nextval, ?, ?, ?, ?, ?, ?)";    
        } else {
            query = "INSERT INTO transaction (transaction_id, amount, income, trans_date, vendor_id, comments, budget_id, method_id) values (transaction_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";    
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(transaction.getAmount());
        parameters.add(transaction.getIncome());
        parameters.add(transaction.getTransDate());             
        parameters.add(transaction.getVendorId());
        parameters.add(transaction.getComments());
        parameters.add(transaction.getBudgetId());
        if(transaction.getMethodId() != 0) {
            parameters.add(transaction.getMethodId());
        }
        try {
            newId = DBUtil.dbExecuteUpdate(query, parameters, "TRANSACTION_ID");
            LOG.info("Transaction saved successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("saveTransaction has failed", e);            
        }
        return newId;
    }
    
    /**
     * This method gets all the transactions for a budget and category.
     * 
     * @param budgetId - the budget ID
     * @param categoryId - the category ID
     * @param perCategory - if true, search by category
     * @return the list of transaction table entries     
     */
    public static List<TransactionTableEntry> getTransactionsForTable(int budgetId, int categoryId, boolean perCategory) {
        LOG.info("Attempting to retrieve all transactions for budget ID {}, category ID {}, perCategory is {}", budgetId, categoryId, perCategory);
        List<TransactionTableEntry> transactionList = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        String query = "SELECT transaction_id, TO_CHAR(trans_date, 'Mon-DD-YYYY') AS trans_date, vendor_name, amount, (CASE WHEN income = 1 THEN 'YES' ELSE 'NO' END) AS income, "
                + "category_name, method_type, comments FROM transaction tran JOIN vendor ven ON tran.vendor_id = ven.vendor_id JOIN category cat "
                + "ON ven.category_id = cat.category_id LEFT OUTER JOIN method met ON tran.method_id = met.method_id "
                + "WHERE tran.budget_id = ? ";
        if(perCategory) {
            query += "AND cat.category_id = ? ";
             parameters.add(categoryId);
        }
        query += "ORDER BY trans_date";             
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                String convertedAmount = StringUtil.convertToDollarFormat(results.getString("AMOUNT"));
                if("YES".equals(results.getString("INCOME"))) {
                    convertedAmount = "+" + convertedAmount;
                } else {
                    convertedAmount = "-" + convertedAmount;
                }                
                TransactionTableEntry transTableEntry = new TransactionTableEntry(results.getString("TRANSACTION_ID"),
                    results.getString("TRANS_DATE"), results.getString("VENDOR_NAME"), convertedAmount,
                    results.getString("INCOME"), results.getString("CATEGORY_NAME"), results.getString("METHOD_TYPE"),
                    results.getString("COMMENTS"));
                transactionList.add(transTableEntry);
            } 
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getTransactionsForTable has failed", e);            
        }
        return transactionList;
    }
    
    /**
     * This method retrieves a transaction by ID.
     * 
     * @param transactionId - the transaction ID
     * @return the transaction entity
     */
    public static Transaction getTransaction(int transactionId) {
        LOG.info("Attempting to find transaction for ID {}", transactionId);
        String query = "SELECT * FROM transaction WHERE transaction_id = ?";
        Transaction transaction = new Transaction();
        List<Object> parameters = new ArrayList<>();
        parameters.add(transactionId);
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                transaction.setTransactionId(results.getInt("TRANSACTION_ID"));
                transaction.setTransDate(results.getDate("TRANS_DATE"));
                transaction.setAmount(results.getDouble("AMOUNT"));
                transaction.setIncome(results.getBoolean("INCOME"));
                transaction.setVendorId(results.getInt("VENDOR_ID"));
                transaction.setMethodId(results.getInt("METHOD_ID"));
                transaction.setBudgetId(results.getInt("BUDGET_ID"));
                transaction.setComments(results.getString("COMMENTS"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getTransaction has failed", e);
        }
        return transaction;
    }
    
    /**
     * This method deletes a transaction.
     * 
     * @param transactionId - the transaction ID     
     */
    public static void deleteTransaction(int transactionId) {
        LOG.info("Attempting to delete transaction ID {}", transactionId);
        String query = "DELETE FROM transaction WHERE transaction_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(transactionId);      
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Transaction saved successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("saveTransaction has failed", e);            
        }
    }
    
    /**
     * This method updates the transaction.
     * 
     * @param transaction - the Transaction class with all the required data    
     */
    public static void updateTransaction(Transaction transaction) {
        LOG.info("Attempting to update transaction ID {}", transaction.getTransactionId());
        boolean methodExists = transaction.getMethodId() != 0;
        String query = "UPDATE transaction SET amount = ?, income = ?, trans_date = ?, vendor_id = ?, comments = ?";
        if(methodExists) {
            query += ", method_id = ?";    
        }
        query += " WHERE transaction_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(transaction.getAmount());
        parameters.add(transaction.getIncome());
        parameters.add(transaction.getTransDate());             
        parameters.add(transaction.getVendorId());
        parameters.add(transaction.getComments());
        if(methodExists) {
            parameters.add(transaction.getMethodId());
        }
        parameters.add(transaction.getTransactionId());
        try {
            DBUtil.dbExecuteUpdate(query, parameters, "");
            LOG.info("Transaction updated successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("updateTransaction has failed", e);            
        }
    }
}