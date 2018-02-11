package budgetapp.util;

import budgetapp.model.SearchTableEntry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods related to File actions.
 */
public class FileUtil {

    /** The workbook. */
    private static final Workbook book = new HSSFWorkbook();
    /** The logger. */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileUtil.class);     
    
    /**
     * This method creates the header row for an excel file.
     * 
     * @param sheet - the excel sheet
     * @param headerNames - list of header names
     */
    private static void createHeaderRow(Sheet sheet, List<String> headerNames) {
        // Create the first row
        Row row = sheet.createRow(0);
        // Create the style
        CellStyle headerStyle = getHeaderStyle();
        // Loop through header names and build the cells
        for(int i=0; i<headerNames.size(); i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(headerNames.get(i));
            cell.setCellStyle(headerStyle);            
        }
    }
    
    /**
     * This method creates and returns the style for a header column.
     * 
     * @return the style for a header column
     */
    private static CellStyle getHeaderStyle() {
        // create font
        Font font = book.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setUnderline(Font.U_SINGLE);
                                    
         // Create cell style 
        CellStyle style = book.createCellStyle();;
        style.setAlignment(HorizontalAlignment.CENTER);
        // Setting font to style
        style.setFont(font);
        return style;
    }
    
    /**
     * This method creates and returns the style for a string column.
     * 
     * @return the style for a string column
     */
    private static CellStyle getStringStyle() {
        // create font
        Font font = book.createFont();
        font.setFontName("Arial");
                                    
         // Create cell style 
        CellStyle style = book.createCellStyle();;
        style.setAlignment(HorizontalAlignment.LEFT);
        // Setting font to style
        style.setFont(font);
        return style;
    }
    
    /**
     * This method creates and returns the style for a number column.
     * 
     * @return the style for a number column
     */
    private static CellStyle getNumberStyle() {
        // create font
        Font font = book.createFont();
        font.setFontName("Arial");
                                    
         // Create cell style 
        CellStyle style = book.createCellStyle();;
        style.setAlignment(HorizontalAlignment.RIGHT);
        // Setting font to style
        style.setFont(font);
        return style;
    }
    
    /**
     * This method exports the search data to an excel file.
     * 
     * @param file - the file to write to
     * @param data - the data to write
     */
    public static void exportSearchToExcel(File file, List<SearchTableEntry> data) {
        
        final int BUDGET_NAME_COL = 0;
        final int TRANS_DATE_COL = 1;
        final int VENDOR_NAME_COL = 2;
        final int AMOUNT_COL = 3;
        final int CATEGORY_NAME_COL = 4;
        final int METHOD_TYPE_COL = 5;
        final int COMMENTS_COL = 6;
        final List<String> headerNames = Arrays.asList("Budget Name", "Transaction Date", "Vendor Name",
                "Amount", "Category Name", "Method Type", "Comments");
        
        CellStyle stringStyle = getStringStyle();
        CellStyle numberStyle = getNumberStyle();
        
        Sheet sheet = book.createSheet("BudgetApp Data");
        createHeaderRow(sheet, headerNames);
        
        for(int i=0; i<data.size(); i++) {
            SearchTableEntry entry = data.get(i);
            Row row = sheet.createRow(i+1);
            Cell budgetCell = row.createCell(BUDGET_NAME_COL, CellType.STRING);
            budgetCell.setCellValue(entry.getBudgetName());
            budgetCell.setCellStyle(stringStyle);
            
            Cell dateCell = row.createCell(TRANS_DATE_COL, CellType.STRING);
            dateCell.setCellValue(entry.getTransDate());
            dateCell.setCellStyle(numberStyle);
            
            Cell vendorCell = row.createCell(VENDOR_NAME_COL, CellType.STRING);
            vendorCell.setCellValue(entry.getVendorName());
            vendorCell.setCellStyle(stringStyle);
            
            Cell amountCell = row.createCell(AMOUNT_COL, CellType.STRING);
            amountCell.setCellValue(entry.getAmount());
            amountCell.setCellStyle(numberStyle);
            
            Cell categoryCell = row.createCell(CATEGORY_NAME_COL, CellType.STRING);
            categoryCell.setCellValue(entry.getCategoryName());
            categoryCell.setCellStyle(stringStyle);
            
            Cell methodCell = row.createCell(METHOD_TYPE_COL, CellType.STRING);
            methodCell.setCellValue(entry.getMethodType());
            methodCell.setCellStyle(stringStyle);
            
            Cell commentsCell = row.createCell(COMMENTS_COL, CellType.STRING);
            commentsCell.setCellValue(entry.getComments());
            commentsCell.setCellStyle(stringStyle);        
        }
        
        sheet.autoSizeColumn(BUDGET_NAME_COL);
        sheet.autoSizeColumn(TRANS_DATE_COL);
        sheet.autoSizeColumn(VENDOR_NAME_COL);
        sheet.autoSizeColumn(AMOUNT_COL);
        sheet.autoSizeColumn(CATEGORY_NAME_COL);
        sheet.autoSizeColumn(METHOD_TYPE_COL);
        sheet.autoSizeColumn(COMMENTS_COL);

        try {
            // Now, its time to write content of Excel into File
            FileOutputStream fileStream = new FileOutputStream(file);
            book.write(fileStream);
            book.close();
            fileStream.close();
        } catch (IOException ex) {
            LOG.error("IO Exception in exportSearchToExcelFile", ex);
        }
    }
}
