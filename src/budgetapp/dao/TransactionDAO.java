package budgetapp.dao;

import budgetapp.Main;
import budgetapp.model.SearchTableEntry;
import budgetapp.model.Transaction;
import budgetapp.model.TransactionTableEntry;
import budgetapp.util.DBUtil;
import budgetapp.util.StringUtil;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        if(Main.USE_DERBY) {
            if(noMethodId) {
                query = "INSERT INTO transactions (amount, income, recurring, trans_date, vendor_id, comments, "
                    + "budget_id) values (?, ?, ?, ?, ?, ?, ?)";    
            } else {
                query = "INSERT INTO transactions (amount, income, recurring, trans_date, vendor_id, comments, "
                    + "budget_id, method_id) values (?, ?, ?, ?, ?, ?, ?, ?)";    
            }
        } else {
            if(noMethodId) {
                query = "INSERT INTO transactions (transaction_id, amount, income, recurring, trans_date, vendor_id, "
                    + "comments, budget_id) values (transaction_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";    
            } else {
                query = "INSERT INTO transactions (transaction_id, amount, income, recurring, trans_date, vendor_id, "
                    + "comments, budget_id, method_id) values (transaction_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";    
            }
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(transaction.getAmount());
        parameters.add(transaction.getIncome());
        parameters.add(transaction.getRecurring());
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
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        List<Object> parameters = new ArrayList<>();
        parameters.add(budgetId);
        String query = "SELECT transaction_id, trans_date, vendor_name, amount, (CASE WHEN income = '1' THEN 'YES' ELSE 'NO' END) AS income, "
                + "category_name, method_type, comments FROM transactions tran JOIN vendor ven ON tran.vendor_id = ven.vendor_id JOIN category cat "
                + "ON ven.category_id = cat.category_id LEFT OUTER JOIN method met ON tran.method_id = met.method_id "
                + "WHERE tran.budget_id = ? ";
        if(perCategory) {
            query += "AND cat.category_id = ? ";
             parameters.add(categoryId);
        }
        query += "ORDER BY trans_date, vendor_name";             
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
                    format.format(results.getDate("TRANS_DATE")), results.getString("VENDOR_NAME"), convertedAmount,
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
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";
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
                transaction.setRecurring(results.getBoolean("RECURRING"));
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
        String query = "DELETE FROM transactions WHERE transaction_id = ?";
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
        String query = "UPDATE transactions SET amount = ?, income = ?, recurring = ?, trans_date = ?, vendor_id = ?, comments = ?";
        if(methodExists) {
            query += ", method_id = ?";    
        }
        query += " WHERE transaction_id = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(transaction.getAmount());
        parameters.add(transaction.getIncome());
        parameters.add(transaction.getRecurring());
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
    
    /**
     * This method gets all the recurring transactions for a budget.
     * 
     * @param budgetId - the budget ID
     * @return list of recurring transactions
     */
    public static List<Transaction> getRecurringTransactions(int budgetId) {
        LOG.info("Attempting to retrieve all recurring transactions for budget ID {}", budgetId);
        List<Transaction> transactionList = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE budget_id = ? AND recurring = ?";
        parameters.add(budgetId);
        parameters.add(Main.USE_DERBY ? true : "1");
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(results.getInt("TRANSACTION_ID"));
                transaction.setTransDate(results.getDate("TRANS_DATE"));
                transaction.setAmount(results.getDouble("AMOUNT"));
                transaction.setIncome(results.getBoolean("INCOME"));
                transaction.setRecurring(results.getBoolean("RECURRING"));
                transaction.setVendorId(results.getInt("VENDOR_ID"));
                transaction.setMethodId(results.getInt("METHOD_ID"));
                transaction.setBudgetId(results.getInt("BUDGET_ID"));
                String comments = results.getString("COMMENTS");
                if(comments == null) {
                    comments = "";
                }
                transaction.setComments(comments);
                transactionList.add(transaction);
            } 
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getRecurringTransactions has failed", e);            
        }
        return transactionList;
    }
    
    /**
     * This method searches for transactions matching all the input parameters.
     * 
     * @param startDate - the start date
     * @param endDate - the end date
     * @param amount - the amount
     * @param incomeOnly - the income only flag
     * @param expenseOnly - the expense only flag
     * @param comments - the comments
     * @param categories - the list of selected categories
     * @param vendors - the list of selected vendors
     * @param methods - the list of selected methods
     * @return the list of all entries matching the criteria
     */
    public static List<SearchTableEntry> searchTransactions(Date startDate, Date endDate, String amount, boolean incomeOnly,
            boolean expenseOnly, String comments, List<Integer> categories, List<Integer> vendors, List<Integer> methods) {
        LOG.info("Attempting to search for transactions");
        List<SearchTableEntry> searchTableList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        List<Object> parameters = new ArrayList<>();
        String query = "SELECT bud.budget_name, trans.trans_date, ven.vendor_name, cat.category_name, trans.amount, "
            + "(CASE WHEN trans.income = '1' THEN 'YES' ELSE 'NO' END) AS income, met.method_type, trans.comments FROM "
            + "transactions trans JOIN vendor ven ON trans.vendor_id = ven.vendor_id JOIN category cat ON "
            + "ven.category_id = cat.category_id LEFT JOIN method met ON trans.method_id = met.method_id "
            + "JOIN budget bud ON trans.budget_id = bud.budget_id ";
        query += processWhereClauses(startDate, endDate, amount, incomeOnly, expenseOnly, comments, categories, vendors, 
            methods, parameters);
        query += "ORDER BY trans.trans_date";        
        try {
            ResultSet results = DBUtil.dbExecuteSelectQuery(query, parameters);
            while(results.next()) {
                String convertedAmount = StringUtil.convertToDollarFormat(results.getString("AMOUNT"));
                if("YES".equals(results.getString("INCOME"))) {
                    convertedAmount = "+" + convertedAmount;
                } else {
                    convertedAmount = "-" + convertedAmount;
                }                
                SearchTableEntry searchEntry = new SearchTableEntry(results.getString("BUDGET_NAME"),
                    format.format(results.getDate("TRANS_DATE")), results.getString("VENDOR_NAME"),
                    convertedAmount, results.getString("INCOME"), results.getString("CATEGORY_NAME"), 
                    results.getString("METHOD_TYPE"), results.getString("COMMENTS"));
                searchTableList.add(searchEntry);
            } 
        } catch (SQLException | ClassNotFoundException e) {
            LOG.error("getTransactionsForTable has failed", e);            
        }        
        return searchTableList;
    }
    
    /**
     * This method appends all applicable WHERE clauses to the search query.
     * 
     * @param startDate - the start date
     * @param endDate - the end date
     * @param amount - the amount
     * @param incomeOnly - the income only flag
     * @param expenseOnly - the expense only flag
     * @param comments - the comments
     * @param categories - the list of selected categories
     * @param vendors - the list of selected vendors
     * @param methods - the list of selected methods
     * @param parameters - the list of parameters
     * @return the query with applicable WHERE clauses
     */
    private static String processWhereClauses(Date startDate, Date endDate, String amount, boolean incomeOnly, boolean expenseOnly,
            String comments, List<Integer> categories, List<Integer> vendors, List<Integer> methods, List<Object> parameters) {
        String returnval;
        StringBuilder query = new StringBuilder("WHERE ");
        if(startDate != null) {
            query.append("trans.trans_date >= ? AND ");
            parameters.add(startDate);
        }
        if(endDate != null) {
            query.append("trans.trans_date <= ? AND ");
            parameters.add(endDate);
        }
        if(!amount.isEmpty()) {
            query.append("trans.amount = ? AND ");
            parameters.add(amount);
        }
        if(incomeOnly) {
            query.append("trans.income = ? AND ");
            parameters.add(Main.USE_DERBY ? true : "1");
        } else if(expenseOnly) {
            query.append("trans.income = ? AND ");
            parameters.add(Main.USE_DERBY ? false : "0");
        }
        if(!comments.isEmpty()) {
            query.append("trans.comments like ? AND ");
            String containsComments = "%" + comments + "%";
            parameters.add(containsComments);
        }
        if(!categories.isEmpty()) {
            query.append("cat.category_id IN (");
            for(Integer id : categories) {
                query.append("?,");
                parameters.add(id);
            }
            query.deleteCharAt(query.length()-1);
            query.append(") AND ");
        }
        if(!vendors.isEmpty()) {
            query.append("trans.vendor_id IN (");
            for(Integer id : vendors) {
                query.append("?,");
                parameters.add(id);
            }
            query.deleteCharAt(query.length()-1);
            query.append(") AND ");
        }
        if(!methods.isEmpty()) {
            query.append("trans.method_id IN (");
            for(Integer id : methods) {
                query.append("?,");
                parameters.add(id);
            }
            query.deleteCharAt(query.length()-1);
            query.append(") AND ");
        }
        // If no where clauses were found, delete it.
        if("WHERE ".equals(query.toString())) {
            returnval = "";
        } else {
            returnval = query.toString().substring(0, query.length()-4);
        }
        return returnval;
    }
}