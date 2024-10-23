import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * Class <code>COTExcelParser</code> parses the Excel files from the COT report and extracts
 * the date, the net long of the commercials, large traders, and small traders, for every Future
 * and writes this numbers as tables to files. The files are in the folder /tables.
 * 
 * @author Christine Merkel
 *
 */
public class ExcelParser {
	/** Contains the Abbreviations of the Future Names. The Abbreviatiosn are used in the GUI. */
    String[] futureNameAbbreviations;
    
    /** List that contains the COT excel files. */
    File[] excelFiles;
    
    /** The folder that contains the table files.*/
    String folderOfTables;
    
    /** HashMap that stores Future name and Abbreviation pairs. */
    HashMap<String, String> nameAbbreviationPairs;
    
    /**
     * Constructor
     * 
     * @param folderOfTables           folder that contains the table files 
     * @param futureNameAbbreviations  list that contains the Abbreviations of the Future names
     * @param excelFiles               list that contains the COT excel files
     * @param nameAbbreviationPairs    HashMap that stores Future name and Abbreviation pairs
     */
    public ExcelParser(String folderOfTables, String[] futureNameAbbreviations,  File[] excelFiles, HashMap<String, String> nameAbbreviationPairs) {
        this.folderOfTables = folderOfTables;
        this.futureNameAbbreviations = futureNameAbbreviations;
        this.excelFiles = excelFiles;
        this.nameAbbreviationPairs = nameAbbreviationPairs;
    }

    /**
     * Does the excel file parsing and writing table files.
     */
    public void start(){
        try {
            for (int l = 0; l < excelFiles.length; l++) {
                InputStream fs = new FileInputStream(excelFiles[l]);
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                int r = sheet.getLastRowNum();

                for (int j = r - 1; j >= 0; j--) {
                    Row row = sheet.getRow(j);
                    Cell cell0 = row.getCell(0);
                    String celltext0 = cell0.getStringCellValue();
                    String line = "";

                    for (int k = 0; k <= nameAbbreviationPairs.size()-1; k++) {
                        String name = futureNameAbbreviations[k];

                        if (celltext0.contains(nameAbbreviationPairs.get(name)))
                        {
                            String path = "";
                            String OS = System.getProperty("os.name");
                            if (OS.startsWith("Windows")) path = folderOfTables + "\\" + name;
                            if (!OS.startsWith("Windows")) path = folderOfTables + "/" + name;
                            File f = new File(path);
                            COTPanel.showUpdatngMessage = true;
                            COTPanel.nameOfTableFile = name;
                            COTVisualizer.gui.repaint();

                            FileWriter tablefw = new FileWriter(f, true);
                            // Date
                            Cell cell2 = row.getCell(2);
                            Date date = new Date();
                            date = cell2.getDateCellValue();

                            DateFormat df = new SimpleDateFormat("MM/yy");
                            DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
                            String datestring = df.format(date);

                            line += datestring;
                            line += " ";

                            // Commercials
                            Cell cell11 = row.getCell(11);
                            Cell cell12 = row.getCell(12);
                            double result = cell11.getNumericCellValue() - cell12.getNumericCellValue();
                            int commercials = (int) result;
                            line += String.valueOf(commercials);
                            line += " ";

                            // Large Traders
                            Cell cell8 = row.getCell(8);
                            Cell cell9 = row.getCell(9);
                            double result2 = cell8.getNumericCellValue() - cell9.getNumericCellValue();
                            int largetraders = (int) result2;
                            line += String.valueOf(largetraders);
                            line += " ";

                            // Small Traders
                            Cell cell15 = row.getCell(15);
                            Cell cell16 = row.getCell(16);
                            double result3 = cell15.getNumericCellValue() - cell16.getNumericCellValue();
                            int smalltraders = (int) result3;
                            line += String.valueOf(smalltraders);

                            synchronized(this) {
                                tablefw.write(line + "\n");
                                tablefw.close();
                            }
                        }
                    }
                }

                wb.close();
                fs.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }
}



