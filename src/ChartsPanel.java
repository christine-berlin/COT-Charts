import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class ChartsPanel extends JPanel {
    public static int space_right = 80;
    public static int space_buttom = 20;
    public static int width, height;
    public static String datum = "";
    static boolean drawgraph = false;
    public static boolean updating = false;
    public static boolean downloading = false;
    public static boolean creatingtables = false;
    public static boolean test = false;
    public static String filename="";

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

        if (updating) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
        }

        if (downloading) {
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Downloading new COT report....", 100, 120);
        }

        if (creatingtables) {
            //g.setColor(Color.GREEN);
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
        }

        if (test) {
            //g.setColor(Color.GREEN);
            g.drawString("UPDATING PLEASE WAIT", 100, 100);
            g.drawString("Writing tables....", 100, 120);
            g.drawString("table "+filename, 100, 140);
        }

        // crosshair
        if ((COTVisualizer.drawcrosshair) && (drawgraph)) {

            g.setColor(Color.YELLOW);
            if (COTVisualizer.crosshairx > (width - space_right)) {
                COTVisualizer.crosshairx = width - space_right;
            }
            g.drawLine(COTVisualizer.crosshairx, 0, COTVisualizer.crosshairx, COTVisualizer.panelpaint.getHeight());
            g.drawLine(0, COTVisualizer.crosshairy, COTVisualizer.panelpaint.getWidth() - space_right,
                    COTVisualizer.crosshairy);
//            g.fillRect(COTVisualizer.crosshairx - 20, COTVisualizer.panelpaint.getHeight() - 20, 40, 20);
//
//            g.fillRect(COTVisualizer.panelpaint.getWidth() - space_right + 1, COTVisualizer.crosshairy - 10, 60, 20);
//            g.setFont(font_small);

//            g.setColor(Color.CYAN);
//            int ywert = -(COTVisualizer.crosshairy - height / 2) * 1000;
//            g.drawString(String.valueOf(ywert), COTVisualizer.panelpaint.getWidth() - space_right + 9,
//                    COTVisualizer.crosshairy + 5);
//
//            int index_datum = (width - COTVisualizer.crosshairx - space_right) / 10;
//
//            datum = COTVisualizer.dates[index_datum];
//
//            g.drawString(datum, COTVisualizer.crosshairx - 15, height - 5);
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
                x -= 5 * COTVisualizer.delta_x;
            }

            int y = height / 2;
            while (y > 0) {
                g.drawLine(0, y, width - space_right - 1, y);
                g.drawLine(0, height / 2 + (height / 2 - y), width - space_right - 1, height / 2 + (height / 2 - y));
                y -= 10 * 10;
            }
        }

        if (drawgraph) {
            g.setColor(Color.GRAY);
            g.drawLine(0, height/2, width-75, height/2);
            g.setFont(font_small);
            g.setColor(Color.RED);
            g.drawString(String.valueOf(COTVisualizer.commercials[0]), 100, 10);
            g.setColor(Color.BLUE);
            g.drawString(String.valueOf(COTVisualizer.largetraders[0]), 100, 30);
            g.setColor(Color.GREEN);
            g.drawString(String.valueOf(COTVisualizer.smalltraders[0]), 100, 50);
            int start_x = width - space_right - 1 + COTVisualizer.dx;
            int pos = 0;
            while ((start_x > COTVisualizer.delta_x) && (pos < COTVisualizer.commercials.length - 1)) {
                if (start_x <= width - space_right) {
                    // DRAW COMMERCIALS


                    g.setColor(Color.RED);
                    g.drawLine(start_x - COTVisualizer.delta_x,
                            height/2 - ((height-20)/2)* COTVisualizer.commercials[pos+1]/COTVisualizer.Max,
                            start_x,
                            height/2 - ((height-20)/2)* COTVisualizer.commercials[pos]/COTVisualizer.Max);


                    // DRAW LARGETRADERS
                    g.setColor(Color.BLUE);

                    g.drawLine(start_x - COTVisualizer.delta_x,
                            height/2 - ((height-20)/2)* COTVisualizer.largetraders[pos+1]/COTVisualizer.Max,
                            start_x,
                            height/2 - ((height-20)/2)* COTVisualizer.largetraders[pos]/COTVisualizer.Max);


                    // DRAW SMALLTRADERS
                    g.setColor(Color.GREEN);

                    g.drawLine(start_x - COTVisualizer.delta_x,
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
                start_x -= COTVisualizer.delta_x;

                pos += 1;
            }

            // DRAW Y COORDINATES
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


            drawgraph = false;
        }
    }
}
