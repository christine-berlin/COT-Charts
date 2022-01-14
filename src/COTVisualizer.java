import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import java.awt.Cursor;
/**
 * Class <code>COTVisualizes</code> defines the GUI of the Application.
 * 
 * @author Christine Merkel
 *
 */
public class COTVisualizer {
	/** Frame of the GUI */
    public static JFrame gui;
    
    /** Menubar in the top of the GUI */
    public static JMenuBar tb;
    
    public static String[] comboBoxList;
    public static String[] financials;
    public static String[] commodities;
    public static JSlider sliderx;
    public static JSlider slidery;
    public static JPanel panelpaint;
    public static String selected = "";
    public static JPanel oszillator;
    public static String[] dates;
    public static Integer[] commercials;
    public static Integer[] largetraders;
    public static Integer[] smalltraders;
    public static Integer[] oscillator;
    public static int crosshairx;
    public static int crosshairy;
    public static boolean drawcrosshair = false;
    public static boolean grid = false;
    public static JCheckBox grid_box;
    public static JCheckBox crosshair_box;
    public static int drag_x;
    public static Point mousePT;
    public static int dx = 0;
    public static int dy = 0;
    public static int delta_x = 5;
    public static JButton update;
    public static UpdateExcelFiles updateExcelFiles;
    public static int Max;

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        updateExcelFiles = new UpdateExcelFiles();
        updateExcelFiles.init();
        gui = new JFrame("COTViz");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // repaint after resize
        gui.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!selected.equals("")) {
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                    gui.repaint();
                }
            }
        });

        addComponentsToPane(gui.getContentPane());

        gui.setJMenuBar(tb);
        gui.pack();
        gui.setVisible(true);
        gui.repaint();
    }

    /**
     * Defines the GUI components.
     * 
     * @param pane  Container of the GUI
     */
    public static void addComponentsToPane(Container pane) {
        tb = new JMenuBar();
        JLabel label = new JLabel("Select:  ");
        comboBoxList = updateExcelFiles.getFuturesList();
        JComboBox<String> mycombobox = new JComboBox<String>(comboBoxList);
        mycombobox.setMaximumSize(new Dimension(300, 30));

        mycombobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                JComboBox<String> comboboxitem = (JComboBox<String>) event.getSource();
                selected = (String) comboboxitem.getSelectedItem();
                String str;
                List<String> dates_list = new ArrayList<String>();
                List<Integer> commercials_list = new ArrayList<Integer>();
                List<Integer> largetraders_list = new ArrayList<Integer>();
                List<Integer> smalltraders_list = new ArrayList<Integer>();
                BufferedReader in;

                try {
                    File tablesFolder = new File("tables");
                    if (tablesFolder.isDirectory()) {
                        String selected_path = "tables/" + selected;
                        in = new BufferedReader(new FileReader(selected_path));
                        while ((str = in.readLine()) != null) {
                            String[] tokens = str.split("\\s+");
                            dates_list.add(tokens[0]);
                            commercials_list.add(Integer.valueOf(tokens[1]));
                            largetraders_list.add(Integer.valueOf(tokens[2]));
                            smalltraders_list.add(Integer.valueOf(tokens[3]));
                        }

                        Collections.reverse(dates_list);
                        dates = dates_list.toArray(new String[dates_list.size()]);
                        Collections.reverse(commercials_list);
                        commercials = commercials_list.toArray(new Integer[commercials_list.size()]);
                        Collections.reverse(largetraders_list);
                        largetraders = largetraders_list.toArray(new Integer[largetraders_list.size()]);
                        Collections.reverse(smalltraders_list);
                        smalltraders = smalltraders_list.toArray(new Integer[smalltraders_list.size()]);

                        //Max value of commercials
                        Max = Math.abs(commercials[0]);
                        for (int i = 1; i < commercials.length; i++ ) {
                            if(Math.abs(commercials[i]) > Max)  Max = Math.abs(commercials[i]);
                        }

                        for (int i = 1; i < largetraders.length; i++ ) {
                            if(Math.abs(largetraders[i]) > Max) Max = Math.abs(largetraders[i]);
                        }

                        oscillator = new Integer[dates.length - 26];

                        List<Integer> oszillator26_list = new ArrayList<Integer>();
                        int t = 0;
                        while (t < oscillator.length) {
                            oszillator26_list = commercials_list.subList(t, 26 + t);
                            int min26 = t + oszillator26_list.indexOf(Collections.min(oszillator26_list));
                            int max26 = t + oszillator26_list.indexOf(Collections.max(oszillator26_list));

                            int d = commercials[t];
                            int f = commercials[max26];
                            int g = commercials[min26];
                            int o = 0;
                            if ((f - g) != 0)
                                o = 100 * (d - g) / (f - g);

                            oscillator[t] = o;
                            t++;
                        }

                        dx = 0;
                        dy = 0;
                        OscillatorPanel.showOscillator = true;
                        COTPanel.showCOTChart = true;
                        gui.repaint();

                    }
                }

                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        grid_box = new JCheckBox();
        grid_box.setText("grid");
        grid_box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                grid = !grid;
                if (!selected.equals("")) {
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                }
                gui.repaint();
            }
        });

        crosshair_box = new JCheckBox();
        crosshair_box.setText("crosshair");

        crosshair_box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                drawcrosshair = !drawcrosshair;
                if (!selected.equals("")) {
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                }
                gui.repaint();
            }
        });


        update = new JButton("update COT");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                COTPanel.updatingExcelFiles = true;

                //show updating message
                gui.repaint();

                //update has to run concurrent to showing the update message
                new Thread(new Runnable() {
                    @Override public void run() {
                        updateExcelFiles.readhead();

                        COTPanel.downloadingExcelFiles = true;
                        gui.repaint();
                        updateExcelFiles.downloadCOT();


                        COTPanel.writingTableFiles = true;
                        COTPanel.downloadingExcelFiles = false;
                        gui.repaint();
                        updateExcelFiles.update();

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                COTPanel.updatingExcelFiles = false;
                                COTPanel.writingTableFiles = false;
                                COTPanel.showUpdatngMessage = false;
                                gui.repaint();
                            }
                        });
                    }
                }).start();
            }
        });

        tb.add(label);
        tb.add(mycombobox);
        JLabel dummy = new JLabel("                                                                         ");
        tb.add(grid_box);
        tb.add(crosshair_box);
        tb.add(update);
        tb.add(dummy);
        oszillator = new OscillatorPanel();
        oszillator.setPreferredSize(new Dimension(gui.getWidth(), 150));
        oszillator.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        panelpaint = new COTPanel();
        panelpaint.setPreferredSize(new Dimension(gui.getWidth(), 500));
        panelpaint.setBackground(Color.DARK_GRAY);

        panelpaint.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!selected.equals("")) {
                    mousePT = e.getPoint();
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                    gui.repaint();
                }
            }
        });

        panelpaint.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                if ((!selected.equals("")) && (arg0.getX() < COTPanel.width - COTPanel.space_right)) {
                    dx = arg0.getX() - mousePT.x;
                    dy = arg0.getY() - mousePT.y;
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                    gui.repaint();
                }

                if ((!selected.equals("")) && (arg0.getX() > COTPanel.width - COTPanel.space_right)
                        && (arg0.getY() < COTPanel.height - COTPanel.space_buttom)) {
                    dy = arg0.getY() - mousePT.y;
                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                    gui.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent arg0) {
                crosshairx = arg0.getX();
                crosshairy = arg0.getY();

                File tablesFolder = new File("tables");

                if (!selected.equals("") && (tablesFolder.isDirectory())) {

                    COTPanel.showCOTChart = true;
                    OscillatorPanel.showOscillator = true;
                    gui.repaint();
                }
            }
        });

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(panelpaint);
        pane.add(oszillator);
    }
}
