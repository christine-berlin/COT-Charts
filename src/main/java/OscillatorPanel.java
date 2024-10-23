
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;


/**
 * Class <code>OscillatorPanel</code> is the bottom panel. It is used to visualize the oscillator.
 * 
 * @author Christine Merkel
 *
 */
public class OscillatorPanel extends JPanel{
	/** Width of the gray area on the right side of the GUI. */
    static final int space_right = 80;
    
    /** The oscillator is shown or not */
    public static boolean showOscillator;
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Font font_small = new Font("Verdana",Font.PLAIN, 10);
        Point point1 = new Point(getWidth()/2, 0);
        Point point2 = new Point(getWidth()/2, getHeight());

        //Color color1 = Color.LIGHT_GRAY;
        //Color color2 = Color.BLACK;
        //final GradientPaint gp = new GradientPaint(point1, color1, point2, color2, true);
        //final Graphics2D g2 = (Graphics2D) g;
        //g2.setPaint(gp);
        //g.fillRect(0, 0, getWidth(), getHeight());

        //g.setColor(Color.LIGHT_GRAY);
        //g.fillRect(getWidth()-space_right+1, 0, getWidth(), getHeight());

        g.setColor(Color.BLUE);
        Font font = new Font("Verdana", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Oscillator", 10, 20);

        g.setColor(Color.BLACK);
        g.drawLine(getWidth()-space_right, 0, getWidth()-space_right, getHeight());


        if(showOscillator){
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, getHeight()/2, getWidth()-COTPanel.space_right, getHeight()/2);
            g.drawLine(0, getHeight()/2-25, getWidth()-COTPanel.space_right, getHeight()/2-25);
            g.drawLine(0, getHeight()/2-50, getWidth()-COTPanel.space_right, getHeight()/2-50);
            g.drawLine(0, getHeight()/2+25, getWidth()-COTPanel.space_right, getHeight()/2+25);
            g.drawLine(0, getHeight()/2+50, getWidth()-COTPanel.space_right, getHeight()/2+50);

            g.setColor(Color.BLUE);
            g.drawLine(getWidth()-COTPanel.space_right, getHeight()/2, getWidth()-COTPanel.space_right+5, getHeight()/2);
            g.drawLine(getWidth()-COTPanel.space_right, getHeight()/2-25, getWidth()-COTPanel.space_right+5, getHeight()/2-25);
            g.drawLine(getWidth()-COTPanel.space_right, getHeight()/2-50, getWidth()-COTPanel.space_right+5, getHeight()/2-50);
            g.drawLine(getWidth()-COTPanel.space_right, getHeight()/2+25, getWidth()-COTPanel.space_right+5, getHeight()/2+25);
            g.drawLine(getWidth()-COTPanel.space_right, getHeight()/2+50, getWidth()-COTPanel.space_right+5, getHeight()/2+50);
            g.setFont(font_small);
            g.drawString("0",getWidth()-COTPanel.space_right+10 , getHeight()/2+5+50);
            g.drawString("25",getWidth()-COTPanel.space_right+10 , getHeight()/2+5+25);
            g.drawString("50",getWidth()-COTPanel.space_right+10 , getHeight()/2+5);
            g.drawString("75",getWidth()-COTPanel.space_right+10 , getHeight()/2+5-25);
            g.drawString("100",getWidth()-COTPanel.space_right+10 , getHeight()/2+5-50);

            g.setColor(Color.GREEN);
            int x= getWidth()-COTPanel.space_right+COTVisualizer.x;

            for(int j=0;j<COTVisualizer.oscillator.length-1;j++){
                if(x-j*5 <=COTPanel.width-COTPanel.space_right){
                    g.drawLine(x-j*5,
                            getHeight()/2+50-COTVisualizer.oscillator[j],
                            x-(j+1)*5,
                            getHeight()/2+50-COTVisualizer.oscillator[j+1]);
                }
            }
            
            showOscillator = false;
        }
    }
}