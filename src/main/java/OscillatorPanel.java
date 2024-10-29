
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;


/**
 * Class <code>OscillatorPanel</code> is the bottom panel. It is used to visualize the oscillatorValues.
 * 
 * @author Christine Merkel
 *
 */
public class OscillatorPanel extends JPanel{
	/** Width of the gray area on the right side of the GUI. */
    static final int space_right = 80;

    private COTVisualizer cotVisualizer;

    public OscillatorPanel(COTVisualizer cotVisualizer) {
        this.cotVisualizer = cotVisualizer;
    }

    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Font font_small = new Font("Verdana",Font.PLAIN, 10);
        Point point1 = new Point(getWidth()/2, 0);
        Point point2 = new Point(getWidth()/2, getHeight());


        g.setColor(Color.BLUE);
        Font font = new Font("Verdana", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Oscillator", 10, 20);

        g.setColor(Color.BLACK);
        g.drawLine(getWidth()-space_right, 0, getWidth()-space_right, getHeight());


        if(cotVisualizer.getShowOscillator()){
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, getHeight()/2, getWidth()-space_right, getHeight()/2);
            g.drawLine(0, getHeight()/2-25, getWidth()-space_right, getHeight()/2-25);
            g.drawLine(0, getHeight()/2-50, getWidth()-space_right, getHeight()/2-50);
            g.drawLine(0, getHeight()/2+25, getWidth()-space_right, getHeight()/2+25);
            g.drawLine(0, getHeight()/2+50, getWidth()-space_right, getHeight()/2+50);

            g.setColor(Color.BLUE);
            g.drawLine(getWidth()-space_right, getHeight()/2, getWidth()-space_right+5, getHeight()/2);
            g.drawLine(getWidth()-space_right, getHeight()/2-25, getWidth()-space_right+5, getHeight()/2-25);
            g.drawLine(getWidth()-space_right, getHeight()/2-50, getWidth()-space_right+5, getHeight()/2-50);
            g.drawLine(getWidth()-space_right, getHeight()/2+25, getWidth()-space_right+5, getHeight()/2+25);
            g.drawLine(getWidth()-space_right, getHeight()/2+50, getWidth()-space_right+5, getHeight()/2+50);
            g.setFont(font_small);
            g.drawString("0",getWidth()-space_right+10 , getHeight()/2+5+50);
            g.drawString("25",getWidth()-space_right+10 , getHeight()/2+5+25);
            g.drawString("50",getWidth()-space_right+10 , getHeight()/2+5);
            g.drawString("75",getWidth()-space_right+10 , getHeight()/2+5-25);
            g.drawString("100",getWidth()-space_right+10 , getHeight()/2+5-50);

            g.setColor(Color.GREEN);
            int x= getWidth()-space_right+cotVisualizer.getXOffset();

            for(int j = 0; j<cotVisualizer.getOscillatorValues().length-1; j++){
                if(x-j*5 <=cotVisualizer.getCotPanel().getWidth()-space_right){
                    g.drawLine(x-j*5,
                            getHeight()/2+50-cotVisualizer.getOscillatorValues()[j],
                            x-(j+1)*5,
                            getHeight()/2+50-cotVisualizer.getOscillatorValues()[j+1]);
                }
            }
        }
    }
}