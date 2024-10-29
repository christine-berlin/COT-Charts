import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;


/**
 * Class <code>COTPanel</code> is the top panel. It is used to visualize the COT charts.
 * 
 * @author Christine Merkel
 *
 */
public class COTPanel extends JPanel {
	/** Width of the gray area on the right side of the GUI. In this area the mouse dragging of the CPT chart is not possible. */
    private final int space_right = 80;
    
    /** Width of the GUI */
    private int width;
    
    /** Height of the GUI */
    private int height;

    private COTVisualizer cotVisualizer;

    public COTPanel(COTVisualizer cotVisualizer) {
        this.cotVisualizer = cotVisualizer;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (cotVisualizer == null || cotVisualizer.getOscillatorValues() == null
             && !cotVisualizer.getUpdatingExcelFiles()) {
            return;
        }

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
        g.drawString(cotVisualizer.getSelectedFuture(), 200, 20);

        g.setColor(Color.BLACK);
        g.drawLine(width - space_right, 0, width - space_right, height);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(width - space_right + 1, 0, width, height);

        if (cotVisualizer.getUpdatingExcelFiles()) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
        }

        if (cotVisualizer.getNoData()) {
            g.drawString("NO DATA AVAILABLE", 100, 80);
        }

        if (cotVisualizer.getDownloadingExcelFiles()) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Downloading new COT report....", 100, 120);
        }

        if (cotVisualizer.getWritingTableFiles()) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
        }

        if (cotVisualizer.getShowUpdatingMessage()) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
            g.drawString("table "+cotVisualizer.getNameOfTableFile(), 100, 140);
        }

        // DRAW CROSSHAIR
        if ((cotVisualizer.getDrawCrosshair()) && (cotVisualizer.getShowCOTChart())) {
            g.setColor(Color.YELLOW);
            if (cotVisualizer.getCrosshairX() > (width - space_right)) {
                cotVisualizer.setCrosshairX(width - space_right);
            }
            g.drawLine(cotVisualizer.getCrosshairX(), 0, cotVisualizer.getCrosshairX(), cotVisualizer.getCotPanel().getHeight());
            g.drawLine(0, cotVisualizer.getCrosshairY(), cotVisualizer.getCotPanel().getWidth() - space_right,
                    cotVisualizer.getCrosshairY());
        }

        // DRAW GRID
        if (cotVisualizer.getShowGrid()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, height / 2, width - space_right - 1, height / 2);
            g.drawLine(0, height / 2 + 1, width - space_right - 1, height / 2 + 1);
            g.drawLine(0, height / 2 - 1, width - space_right - 1, height / 2 - 1);
            g.setColor(Color.LIGHT_GRAY);
            int x = width - space_right - 1;
            while (x > 0) {
                g.drawLine(x, 0, x, height);
                x -= 5 * cotVisualizer.getDeltaX();
            }

            int y = height / 2;
            while (y > 0) {
                g.drawLine(0, y, width - space_right - 1, y);
                g.drawLine(0, height / 2 + (height / 2 - y), width - space_right - 1, height / 2 + (height / 2 - y));
                y -= 10 * 10;
            }
        }


        if (cotVisualizer.getShowCOTChart()) {
            g.setColor(Color.GRAY);
            g.drawLine(0, height/2, width-75, height/2);
            g.setFont(font_small);
            g.setColor(Color.RED);
            g.drawString(String.valueOf(cotVisualizer.getCommercials()[0]), 100, 10);
            g.setColor(Color.BLUE);
            g.drawString(String.valueOf(cotVisualizer.getLargeTraders()[0]), 100, 30);
            g.setColor(Color.GREEN);
            g.drawString(String.valueOf(cotVisualizer.getSmallTraders()[0]), 100, 50);
            int start_x = width - space_right - 1 + cotVisualizer.getXOffset();
            int pos = 0;
            while ((start_x > cotVisualizer.getDeltaX()) && (pos < cotVisualizer.getCommercials().length - 1)) {
                if (start_x <= width - space_right) {
                    // DRAW COMMERCIALS
                    g.setColor(Color.RED);
                    g.drawLine(start_x - cotVisualizer.getDeltaX(),
                            height/2 - ((height-20)/2)* cotVisualizer.getCommercials()[pos+1]/cotVisualizer.getMaxNetLongPositions()
                            ,
                            start_x,
                            height/2 - ((height-20)/2)* cotVisualizer.getCommercials()[pos]/cotVisualizer.getMaxNetLongPositions());


                    // DRAW LARGETRADERS
                    g.setColor(Color.BLUE);
                    g.drawLine(start_x - cotVisualizer.getDeltaX(),
                            height/2 - ((height-20)/2)* cotVisualizer.getLargeTraders()[pos+1]/cotVisualizer.getMaxNetLongPositions(),
                            start_x,
                            height/2 - ((height-20)/2)* cotVisualizer.getLargeTraders()[pos]/cotVisualizer.getMaxNetLongPositions());


                    // DRAW SMALLTRADERS
                    g.setColor(Color.GREEN);
                    g.drawLine(start_x - cotVisualizer.getDeltaX(),
                            height/2 - ((height-20)/2)* cotVisualizer.getSmallTraders()[pos+1]/cotVisualizer.getMaxNetLongPositions(),
                            start_x,
                            height/2 - ((height-20)/2)* cotVisualizer.getSmallTraders()[pos]/cotVisualizer.getMaxNetLongPositions());


                    // DRAW X COORDINATES
                    g.setColor(Color.ORANGE);
                    g.setFont(font_small);
                    if (pos % 10 == 0) {
                        g.drawString(cotVisualizer.getDates()[pos], start_x - 15, height - 10);
                        g.drawLine(start_x, height, start_x, height - 10);
                    }
                }
                
                start_x -= cotVisualizer.getDeltaX();
                pos += 1;
            }

            // DRAW THE CURRENT NUMBERS OF EACH NET LONG POSITION ON THE RIGHT SIDE
            g.setColor(Color.BLUE);
            g.drawString(Integer.toString(cotVisualizer.getLargeTraders()[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* cotVisualizer.getLargeTraders()[0]/cotVisualizer.getMaxNetLongPositions());
            g.setColor(Color.RED);
            g.drawString(Integer.toString(cotVisualizer.getCommercials()[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* cotVisualizer.getCommercials()[0]/cotVisualizer.getMaxNetLongPositions());
            g.setColor(Color.GREEN);
            g.drawString(Integer.toString(cotVisualizer.getSmallTraders()[0]),
                    width + 10 - space_right,
                    height/2 - ((height-20)/2)* cotVisualizer.getSmallTraders()[0]/cotVisualizer.getMaxNetLongPositions());
            g.drawString("0", width + 10 - space_right, height / 2 + 5);
            g.setColor(Color.GRAY);
            g.drawString("0", width + 10 - space_right, height / 2 + 5);
        }
    }
}
