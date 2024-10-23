import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * Class <code>COTPanel</code> is the top panel. It is used to visualize the COT charts.
 * 
 * @author Christine Merkel
 *
 */
public class COTPanel extends JPanel {
	/** Width of the gray area on the right side of the GUI. In this area the mouse dragging of the CPT chart is not possible. */
    static final int space_right = 80;
    
    /** Height of the bottom area where the mouse dragging of the COT chart is not possible. */
    static final int space_buttom = 20;
    
    /** Width of the GUI */
    static int width;
    
    /** Height of the GUI */
    static int height;
    
    /** COT Chart is painted or not */
    static boolean showCOTChart;
    
    /** Excel files are currently updating or not */
    public static boolean updatingExcelFiles;
    
    /** Excel files are currently downloading or not */
    public static boolean downloadingExcelFiles;
    
    /** The table files are currently being created or not */
    public static boolean writingTableFiles;
    
    /** The message that the Application is beeing updated is shown or not */
    public static boolean showUpdatngMessage;
    
    /** The name of the Future table file that is currently being created */
    public static String nameOfTableFile="";

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        height = getHeight();
        width = getWidth();
        g.setColor(Color.RED);

        g.drawString("Commercials", 10, 10);
        g.setColor(Color.BLUE);
        g.drawString("Large Traders", 10, 30);
        g.setColor(Color.GREEN);
        g.drawString("Small Traders", 10, 50);
        g.setColor(Color.GRAY);

        Font font_small = new Font("Verdana", Font.PLAIN, 10);
        Font font = new Font("Verdana", Font.BOLD, 20);
        g.setFont(font);
        g.drawString(COTVisualizer.selected, 200, 20);

        g.setColor(Color.BLACK);
        g.drawLine(width - space_right, 0, width - space_right, height);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(width - space_right + 1, 0, width, height);

        if (updatingExcelFiles) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
        }

        if (downloadingExcelFiles) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Downloading new COT report....", 100, 120);
        }

        if (writingTableFiles) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
        }

        if (showUpdatngMessage) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
            g.drawString("table "+nameOfTableFile, 100, 140);
        }

        // DRAW CROSSHAIR
        if ((COTVisualizer.drawCrosshair) && (showCOTChart)) {
            g.setColor(Color.YELLOW);
            if (COTVisualizer.crosshairX > (width - space_right)) {
                COTVisualizer.crosshairX = width - space_right;
            }
            g.drawLine(COTVisualizer.crosshairX, 0, COTVisualizer.crosshairX, COTVisualizer.cotPanel.getHeight());
            g.drawLine(0, COTVisualizer.crosshairY, COTVisualizer.cotPanel.getWidth() - space_right,
                    COTVisualizer.crosshairY);
        }

        // DRAW GRID
        if (COTVisualizer.grid) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, height / 2, width - space_right - 1, height / 2);
            g.drawLine(0, height / 2 + 1, width - space_right - 1, height / 2 + 1);
            g.drawLine(0, height / 2 - 1, width - space_right - 1, height / 2 - 1);
            g.setColor(Color.LIGHT_GRAY);
            int x = width - space_right - 1;
            while (x > 0) {
                g.drawLine(x, 0, x, height);
                x -= 5 * COTVisualizer.deltaX;
            }

            int y = height / 2;
            while (y > 0) {
                g.drawLine(0, y, width - space_right - 1, y);
                g.drawLine(0, height / 2 + (height / 2 - y), width - space_right - 1, height / 2 + (height / 2 - y));
                y -= 10 * 10;
            }
        }

        if (showCOTChart) {
            g.setColor(Color.GRAY);
            g.drawLine(0, height/2, width-75, height/2);
            g.setFont(font_small);
            g.setColor(Color.RED);
            g.drawString(String.valueOf(COTVisualizer.commercials[0]), 100, 10);
            g.setColor(Color.BLUE);
            g.drawString(String.valueOf(COTVisualizer.largetraders[0]), 100, 30);
            g.setColor(Color.GREEN);
            g.drawString(String.valueOf(COTVisualizer.smalltraders[0]), 100, 50);
            int start_x = width - space_right - 1 + COTVisualizer.x;
            int pos = 0;
            while ((start_x > COTVisualizer.deltaX) && (pos < COTVisualizer.commercials.length - 1)) {
                if (start_x <= width - space_right) {
                    // DRAW COMMERCIALS
                    g.setColor(Color.RED);
                    g.drawLine(start_x - COTVisualizer.deltaX,
                            height/2 - ((height-20)/2)* COTVisualizer.commercials[pos+1]/COTVisualizer.Max,
                            start_x,
                            height/2 - ((height-20)/2)* COTVisualizer.commercials[pos]/COTVisualizer.Max);


                    // DRAW LARGETRADERS
                    g.setColor(Color.BLUE);
                    g.drawLine(start_x - COTVisualizer.deltaX,
                            height/2 - ((height-20)/2)* COTVisualizer.largetraders[pos+1]/COTVisualizer.Max,
                            start_x,
                            height/2 - ((height-20)/2)* COTVisualizer.largetraders[pos]/COTVisualizer.Max);


                    // DRAW SMALLTRADERS
                    g.setColor(Color.GREEN);
                    g.drawLine(start_x - COTVisualizer.deltaX,
                            height/2 - ((height-20)/2)* COTVisualizer.smalltraders[pos+1]/COTVisualizer.Max,
                            start_x,
                            height/2 - ((height-20)/2)* COTVisualizer.smalltraders[pos]/COTVisualizer.Max);


                    // DRAW X COORDINATES
                    g.setColor(Color.ORANGE);
                    g.setFont(font_small);
                    if (pos % 10 == 0) {
                        g.drawString(COTVisualizer.dates[pos], start_x - 15, height - 10);
                        g.drawLine(start_x, height, start_x, height - 10);
                    }
                }
                
                start_x -= COTVisualizer.deltaX;
                pos += 1;
            }

            // DRAW THE CURRENT NUMBERS OF EACH NET LONG POSITION ON THE RIGHT SIDE
            g.setColor(Color.BLUE);
            g.drawString(Integer.toString(COTVisualizer.largetraders[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* COTVisualizer.largetraders[0]/COTVisualizer.Max);
            g.setColor(Color.RED);
            g.drawString(Integer.toString(COTVisualizer.commercials[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* COTVisualizer.commercials[0]/COTVisualizer.Max);
            g.setColor(Color.GREEN);
            g.drawString(Integer.toString(COTVisualizer.smalltraders[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* COTVisualizer.smalltraders[0]/COTVisualizer.Max);
            g.drawString("0", width + 10 - space_right, height / 2 + 5);
            g.setColor(Color.GRAY);
            g.drawString("0", width + 10 - space_right, height / 2 + 5);

            showCOTChart = false;
        }
    }
}
