import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Class <code>UpdateExcelFiles</code>  checks whether the Excel files are up to date, if not, it 
 * downloads the missing Excel files as a zipfile and unzips them.
 *  
 * 
 * @author Christine Merkel
 *
 */
public class UpdateExcelFiles {
    private File folder_futures;
    private File[] excelFiles;
    private String folder = "";
    private int lastYear;
    private String lastDate;
    private String currentDate;
    private HashMap<String, String> futureNames; 
    private String[] futureNameAbbreviations; 
    
    /**
     *  Initializes the Hashmap futureNames.
     */
    public void init() {
        initializeFutureNamesHashMap();
        initializeFutureNameAbbreviations();
    }


    /*
     * Updates the Excel files if necessary. 
     */
    public void update() {
        if (checkupdate()) {
            writeTables();
            writehead();
        }
    }

    /**
     * Writes the date the last downloaded COT report was published in a file named 'head'. 
     */
    private void writehead() {
        File headfile = new File("head");
        try {
            FileWriter fw = new FileWriter(headfile, false);
            fw.write(currentDate);
            fw.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Reads the head file if it exists.
     */
    public void readhead() {
        File file = new File("head");
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                lastDate = br.readLine();
                StringTokenizer st = new StringTokenizer(lastDate);
                int yy = Integer.valueOf(st.nextToken().substring(6, 8));
                if (yy >= 0)
                    lastYear = 2000 + yy;
                if (yy < 0)
                    lastYear = 1900 + yy;

                br.close();
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!file.exists()) {
            lastYear = 1986;
        }
    }

    /**
     * Checks if an update of the excel files is necessary.
     * 
     * @return update is necessary or not
     * 
     */
    private boolean checkupdate() {
        File headfile = new File("head");

        InputStream fs;
        try {
            fs = new FileInputStream(excelFiles[0]);
            HSSFWorkbook wb;
            wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell0 = row.getCell(0);
            String celltext0 = cell0.getStringCellValue();
            String line = "";


            // Date
            Cell cell2 = row.getCell(2);
            Date date = new Date();
            date = cell2.getDateCellValue();

            DateFormat df = new SimpleDateFormat("MM/yy");
            DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
            String datestring = df.format(date);

            boolean check = false;
            if (headfile.exists()) {
                Date lastdate = df2.parse(lastDate);
                if (date.compareTo(lastdate) <= 0) check = false;
                if (date.compareTo(lastdate) > 0) {
                    check = true;
                }
            }

            if ((check) || (!headfile.exists())) {
                line += datestring;
                line += " ";
                return true;
            }

            fs.close();
            wb.close();
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates the table files.
     */
    private void writeTables() {
    	File headfile = new File("head");
    	File dir = new File("tables");
    	if (!dir.exists()) {
    		dir.mkdir();
    	}

    	folder = dir.getPath();
    	ExcelParser parser = new ExcelParser(folder, futureNameAbbreviations, excelFiles, futureNames);
    	parser.start();

        // currentdate_string
        InputStream is;
        try {
            int l = excelFiles.length;
            is = new FileInputStream(excelFiles[l - 1]);
            HSSFWorkbook hssfwb = new HSSFWorkbook(is);
            HSSFSheet sheet = hssfwb.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell2 = row.getCell(2);
            Date date = new Date();
            date = cell2.getDateCellValue();
            DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
            currentDate = df2.format(date);
        }
        catch (IOException e) {e.printStackTrace();}

        // delete folder unzip/
        File fileunzip = new File("unzip/");
        File[] f = fileunzip.listFiles();
        for (File s : f) {
            s.delete();
        }

        fileunzip.delete();
    }

    /**
     *  Downloads the COT excel files.
     */
    public void downloadCOT() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        File dir = new File("cot-excel");
        dir.mkdir();
        for (int i = lastYear; i <= 2003; i++) {
            try (BufferedInputStream inputStream = new BufferedInputStream(
                    new URL("https://www.cftc.gov/sites/default/files/files/dea/history/deafut_xls_" + i + ".zip")
                            .openStream());
                 FileOutputStream fileOS = new FileOutputStream("cot-excel/" + i + "dea_fut_xls_.zip")) {
                byte data[] = new byte[1024];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
            }
            catch (IOException e) { }
        }

        for (int i = lastYear; i <= year; i++) {
            try (BufferedInputStream inputStream = new BufferedInputStream(
                    new URL("https://www.cftc.gov/sites/default/files/files/dea/history/dea_fut_xls_" + i + ".zip")
                            .openStream());
                 FileOutputStream fileOS = new FileOutputStream("cot-excel/" + i + "dea_fut_xls_.zip")) {
                byte data[] = new byte[1024];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
            }

            catch (IOException e) {
            }
        }
        unzipCOT();
        folder_futures = new File("unzip");
        excelFiles = folder_futures.listFiles();

        Arrays.sort(excelFiles);
    }

    /**
     * Iterates through all the zipded excel files and passes them to method unzip.
     */
    private void unzipCOT() {
        File dir = new File("cot-excel");
        File[] zipfiles = dir.listFiles();
        String destDir = "unzip/";
        File dir1 = new File(destDir);
        if (!dir1.exists())
            dir1.mkdirs();

        try {
            for (int i = 0; i < zipfiles.length; i++) {
                File zipfile = zipfiles[i];
                String prefix = zipfile.getName().substring(0, 4);
                String zipFilePath = zipfile.getAbsolutePath();
                unzip(zipFilePath, destDir, prefix);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (File s : zipfiles) {
            s.delete();
        }

        dir.delete();
    }

    /**
     * Unzips zipped excel files.
     * 
     * @param zipFilePath   path of the zipped excel file   
     * @param destDir       destination path of the unzipped excel file
     * @param prefix        destination file name
     * @throws IOException  Exception thrown in case of error
     */
    private void unzip(String zipFilePath, String destDir, String prefix) throws IOException {
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(destDir + File.separator + prefix + ".xls");
            new File(newFile.getParent()).mkdirs();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        fis.close();
    }

    /**
     * Returns an Array of Future Name Abbreviations.
     * @return
     */
    public String[] getFuturesList() {
        return futureNameAbbreviations;
    }

    /**
     * Initializes the List futureNameAbbreviations.
     */
    public void initializeFutureNameAbbreviations() {
    	futureNameAbbreviations =  new String[] { "LEANHOGS", "FEEDERCATTLE", "LIVECATTLE", "LUMBER", "SUGARNo11", "COFFEE",
        "ORANGEJUICE", "COTTON", "COCOA", "SOYBEANOIL", "SOYBEANMEAL", "SOYBEANS", "OATS", "RICE", "WHEAT",
        "CORN", "ETHANOL", "NATURALGAS", "HEATINGOIL", "GASOLINE", "WTI", "COPPER", "PALLADIUM", "GOLD",
        "SILVER", "PLATINUM", "S&P", "DJIA", "NASDAQ", "RUSSELL2000MINI", "NIKKEI", "USTREASURYBONDS",
        "2YEARUSTREASURYNOTES", "5YEARUSTREASURYNOTES", "10YEARUSTREASURYNOTES", "30DAYFEDERALFUNDS",
        "AUSTRALIANDOLLAR", "BRAZILIANREAL", "BRITISHPOUNDSTERLING", "EUROFX", "JAPANESEYEN", "CANADIANDOLLAR",
        "MEXICANPESO", "NEWZEALANDDOLLAR", "RUSSIANRUBLE", "BITCOIN", "SWISSFRANC" };

    }
    
    /**
     * Initializes the HashMap futureNames.
     */
    private void initializeFutureNamesHashMap() {
    	futureNames = new HashMap<String, String>();
        futureNames.put("LEANHOGS", "LEAN HOGS - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("FEEDERCATTLE", "FEEDER CATTLE - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("LIVECATTLE", "LIVE CATTLE - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("LUMBER", "RANDOM LENGTH LUMBER - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("SUGARNo11", "SUGAR NO. 11 - ICE FUTURES U.S.");
        futureNames.put("COFFEE", "COFFEE C - ICE FUTURES U.S.");
        futureNames.put("ORANGEJUICE", "FRZN CONCENTRATED ORANGE JUICE - ICE FUTURES U.S.");
        futureNames.put("COTTON", "COTTON NO. 2 - ICE FUTURES U.S.");
        futureNames.put("COCOA", "COCOA - ICE FUTURES U.S.");
        futureNames.put("SOYBEANOIL", "SOYBEAN OIL - CHICAGO BOARD OF TRADE");
        futureNames.put("SOYBEANMEAL", "SOYBEAN MEAL - CHICAGO BOARD OF TRADE");
        futureNames.put("SOYBEANS", "SOYBEANS - CHICAGO BOARD OF TRADE");
        futureNames.put("OATS", "OATS - CHICAGO BOARD OF TRADE");
        futureNames.put("RICE", "ROUGH RICE - CHICAGO BOARD OF TRADE");
        futureNames.put("WHEAT", "WHEAT-SRW - CHICAGO BOARD OF TRADE");
        futureNames.put("CORN", "CORN - CHICAGO BOARD OF TRADE");
        futureNames.put("ETHANOL", "CBT ETHANOL - CHICAGO BOARD OF TRADE");
        futureNames.put("NATURALGAS", "NATURAL GAS - NEW YORK MERCANTILE EXCHANGE");
        futureNames.put("HEATINGOIL", "#2 HEATING OIL");
        futureNames.put("GASOLINE", "GASOLINE BLENDSTOCK (RBOB) - NEW YORK MERCANTILE EXCHANGE");
        futureNames.put("WTI", "CRUDE OIL, LIGHT SWEET - NEW YORK MERCANTILE EXCHANGE");
        futureNames.put("COPPER", "COPPER-GRADE #1 - COMMODITY EXCHANGE INC.");
        futureNames.put("PALLADIUM", "PALLADIUM - NEW YORK MERCANTILE EXCHANGE");
        futureNames.put("GOLD", "GOLD - COMMODITY EXCHANGE INC.");
        futureNames.put("SILVER", "SILVER - COMMODITY EXCHANGE INC.");
        futureNames.put("PLATINUM", "PLATINUM - NEW YORK MERCANTILE EXCHANGE");
        futureNames.put("S&P", "S&P 500 Consolidated - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("DJIA", "DJIA Consolidated - CHICAGO BOARD OF TRADE");
        futureNames.put("NASDAQ", "NASDAQ-100 Consolidated - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("RUSSELL2000MINI", "RUSSELL 2000 MINI INDEX FUTURE - ICE FUTURES U.S.");
        futureNames.put("NIKKEI", "NIKKEI STOCK AVERAGE - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("USTREASURYBONDS", "U.S. TREASURY BONDS - CHICAGO BOARD OF TRADE");
        futureNames.put("2YEARUSTREASURYNOTES", "2-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
        futureNames.put("5YEARUSTREASURYNOTES", "5-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
        futureNames.put("10YEARUSTREASURYNOTES", "10-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
        futureNames.put("30DAYFEDERALFUNDS", "30-DAY FEDERAL FUNDS - CHICAGO BOARD OF TRADE");
        futureNames.put("AUSTRALIANDOLLAR", "AUSTRALIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("BRAZILIANREAL", "BRAZILIAN REAL - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("BRITISHPOUNDSTERLING", "BRITISH POUND STERLING - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("EUROFX", "EURO FX - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("JAPANESEYEN", "JAPANESE YEN - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("CANADIANDOLLAR", "CANADIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("MEXICANPESO", "MEXICAN PESO - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("NEWZEALANDDOLLAR", "NEW ZEALAND DOLLAR - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("RUSSIANRUBLE", "RUSSIAN RUBLE - CHICAGO MERCANTILE EXCHANGE");
        futureNames.put("BITCOIN", "BITCOIN-USD - CBOE FUTURES EXCHANGE");
        futureNames.put("SWISSFRANC", "SWISS FRANC - CHICAGO MERCANTILE EXCHANGE");
    }
}