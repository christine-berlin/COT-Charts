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
 * Class <code>COTExcelParser</code> parses the Excel files from the COT report.
 * @author Christine Merkel
 *
 */
public class ExcelParser {
    int end;
    String[] futureslist;
    File[] list_of_files;
    String folder;
    HashMap<String, String> hash;
    
    
    public ExcelParser(String folder, String[] futureslist,  File[] list_of_files, HashMap<String, String> hash) {
        this.folder = folder;
        this.futureslist = futureslist;
        end = hash.size()-1;
        this.list_of_files = list_of_files;
        this.hash = hash;
    }

    public void start(){
        try {
            for (int l = 0; l < list_of_files.length; l++) {
                InputStream fs = new FileInputStream(list_of_files[l]);
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                int r = sheet.getLastRowNum();

                for (int j = r - 1; j >= 0; j--) {
                    Row row = sheet.getRow(j);
                    Cell cell0 = row.getCell(0);
                    String celltext0 = cell0.getStringCellValue();
                    String line = "";

                    for (int k = 0; k <= end; k++) {
                        String name = futureslist[k];

                        if (celltext0.contains(hash.get(name)))
                        {
                            String path = "";
                            String OS = System.getProperty("os.name");
                            if (OS.startsWith("Windows")) path = folder + "\\" + name;
                            if (!OS.startsWith("Windows")) path = folder + "/" + name;
                            File f = new File(path);
                            ChartsPanel.test = true;
                            ChartsPanel.filename = name;
                            COTVisualizer.myframe.repaint();

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



