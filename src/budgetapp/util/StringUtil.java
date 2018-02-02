package budgetapp.util;
/**
 * String utility class.
 */
public class StringUtil {

    /**
     * This method will ensure a string is not null
     * and contains at least one non-white space character.
     * 
     * @param str - the string to check
     * @return true if string is not null and contains at least one non-white space character.
     */
    public static boolean isValidString(String str) {
        return (str != null && !str.trim().isEmpty());
    }
    
    /**
     * This method will ensure a string is valid and alphanumeric.
     * 
     * @param str - the string to check
     * @return true if string is valid and alphanumeric.
     */
    public static boolean isAlphaNumeric(String str) {
        return isValidString(str) && str.matches("^[a-zA-Z0-9 ]*$");
    }
    
    /**
     * This method will ensure a string matches U.S. dollar format.
     * 
     * @param str - the string to check
     * @return true if string matches U.S. dollar format
     */
    public static boolean isValidDollarAmount(String str) {
        // This regex allows commas and one or two digits to right of decimal
        final String dollarFormat = "(([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d{1,2})?$";
        return str.matches(dollarFormat);
    }
    
    /**
     * This method will convert a string to match U.S. dollar
     * format ($X.XX).
     * 
     * @param str - the string to convert
     * @return the string in the format X.XX
     */
    public static String convertToDollarFormat(String str) {
        String convertedStr = String.format("%.2f", Double.parseDouble(str));
        if(convertedStr.startsWith("-")) {
            convertedStr = convertedStr.replace("-", "-$");
        } else {
            convertedStr = "$" + convertedStr;
        }
        return convertedStr;
    }
    
    /**
     * This method will convert a string from U.S. dollar
     * format ($X.XX) back to a double format.  It removes
     * any $, commas, + or - symbols.
     * 
     * @param str - the string to convert
     * @return the string in double format
     */
    public static double convertFromDollarFormat(String str) {
        return  Double.parseDouble(str.replace(",", "").replace("$", "").replace("-", "").replace("+", ""));
    }
}
