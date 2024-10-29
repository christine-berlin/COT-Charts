import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class <code>COTVisualizes</code> defines the GUI of the Application.
 * This application visualizes COT charts and oscillators based on user-selected futures data.
 * @author Christine Merkel
 *
 */
public class COTVisualizer {
    /**
     * Frame of the GUI
     */
    private JFrame gui;

    private static final Logger logger = LogManager.getLogger(COTVisualizer.class);

    /**
     * MenuBar in the top of the GUI
     */
    private JMenuBar menuBar;

    /**
     * List with the Future Name Abbreviations that can be selected in the ComboBox.
     */
    private String[] comboBoxList;

    /**
     * The Panel for the visualization of the COT charts.
     */
    private JPanel cotPanel;

    /**
     * The Panel for the visualization of the oscillatorValues
     */
    private JPanel oscillatorPanel;

    /**
     * The name of the selected Future in the CpmboBox.
     */
    private String selectedFuture = "";

    /**
     * List that contains all the dates of the COT excel files.
     */
    private String[] dates;

    /**
     * List that contains all the net long positions of the commercials.
     */
    private Integer[] commercials;

    /**
     * List that contains all the net long positions of the large traders.
     */
    private Integer[] largeTraders;

    /**
     * List that contains all the net long positions of the small traders.
     */
    private Integer[] smallTraders;

    /**
     * List contains all the yOffset-values of the oscillatorValues.
     */
    private Integer[] oscillatorValues;

    /**
     * X coordinate of the current position of the cross of the crosshair.
     */
    private int crosshairX;

    /**
     * Y coordinate of the current position of the cross of the crosshair.
     */
    private int crosshairY;

    /**
     * Show crosshair or not.
     */
    private boolean drawCrosshair = false;

    /**
     * Show showGrid or not.
     */
    private boolean showGrid = false;

    /**
     * Select showGrid.
     */
    private JCheckBox gridCheckbox;

    /**
     * Select crosshair.
     */
    private JCheckBox crosshairCheckbox;

    /**
     * Current position of the mouse cursor.
     */
    private Point mousePoint;

    /**
     * X coordinate of the current mouse cursor position.
     */
    private int xOffset = 0;

    /**
     * Y coordinate of the current mouse cursor position.
     */
    private int yOffset = 0;

    /**
     * Update button.
     */
    private JButton updateButton;

    private boolean updatingExcelFiles;

    /**
     * Instance of UpdateExcelFiles.
     */
    private UpdateExcelFiles updateExcelFiles;

    /**
     * The maximum net long positions of the commercials.
     */
    private int maxNetLongPositions;

    /**
     * Constant that defines the size if the showGrid.
     */
    final int deltaX = 5;

    private boolean NoData;

    /**
     * Offset for oscillator calculation
     */
    private final int OSCILLATOR_OFFSET = 26;

    /**
     * Directory for table files
     */
    private final String TABLES_DIRECTORY = "tables/";

    /**
     * Error message prefix
     */
    private final String ERROR_FILE_NOT_FOUND = "File not found: ";

    /**
     * Error message prefix
     */
    private final String ERROR_READING_FILE = "Error reading file: ";

    private boolean showCOTChart;

    private boolean downloadingExcelFiles;

    private boolean writingTableFiles;

    private boolean showUpdatingMessage;

    private String nameOfTableFile;

    private boolean showOscillator;

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new COTVisualizer().initialize());
    }

    /**
     * Initializes the GUI components and layout.
     */
    private void initialize() {
        updateExcelFiles = new UpdateExcelFiles(this);
        updateExcelFiles.init();

        gui = new JFrame("COTViz");

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gui.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });

        setupMenu();
        setupPanels();

        gui.pack();
        gui.setVisible(true);
        gui.repaint();
    }

    public void setShowUpdatingMessage(boolean state){
        showUpdatingMessage = state;
    }

    /**
     *  Getter Method for updatingExcelFiles
     */
    public boolean getDownloadingExcelFiles() {
        return downloadingExcelFiles;
    }

    /**
     *  Getter Method for writingTableFiles
     */
    public boolean getWritingTableFiles() {
        return writingTableFiles;
    }

    /**
     *  Getter Method for showUpdatingMessage
     */
    public boolean getShowUpdatingMessage() {
        return showUpdatingMessage;
    }

    /**
     *  Getter Method for showOscillator
     */
    public boolean getShowOscillator() {
        return showOscillator;
    }

    /**
     *  Getter Method for nameOfTableFile
     */
    public String getNameOfTableFile() {
        return nameOfTableFile;
    }

    /**
     *  Setter Method for nameOfTableFile
     */
    public void setNameOfTableFile(String name) {
        nameOfTableFile = name;
    }

    /**
     *  Getter Method for updatingExcelFiles
     */
    public boolean getUpdatingExcelFiles() {
        return updatingExcelFiles;
    }

    /**
     *  Getter Method for the gui
     */
    public JFrame getGui() {
        return gui;
    }

    /**
     *  Getter Method for oscillatorPanel
     */
    public JPanel getOscillatorPanel() {
        return oscillatorPanel;
    }


    /**
     *  Getter Method for oscillatorValues
     */
    public Integer[] getOscillatorValues() {
        return oscillatorValues;
    }


    /**
     *  Getter Method for cotPanel
     */
    public JPanel getCotPanel() {
        return cotPanel;
    }

    /**
     *  Getter Method for dates[]
     */
    public String[] getDates() {
        return dates;
    }

    /**
     *  Getter Method for the largeTraders[]
     */
    public Integer[] getLargeTraders() {
        return largeTraders;
    }

    /**
     *  Getter Method for the smallTraders[]
     */
    public Integer[] getSmallTraders() {
        return smallTraders;
    }

    /**
     *  Getter Method for the commercials[]
     */
    public Integer[] getCommercials() {
        return commercials;
    }

    /**
     *  Getter Method for crosshairX
     */
    public int getCrosshairX() {
        return crosshairX;
    }

    /**
     *  Getter Method for deltaX
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     *  Getter Method for showCOTCharts
     */
    public boolean getShowCOTChart() {
        return showCOTChart;
    }

    /**
     *  Getter Method for maxNetLongPositions
     */
    public int getMaxNetLongPositions() {
        return maxNetLongPositions;
    }

    /**
     *  Getter Method for xOffset
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     *  Setter Method for crosshairX
     */
    public void setCrosshairX(int new_x) {
        crosshairX = new_x;
    }

    /**
     *  Getter Method for crosshairY
     */
    public int getCrosshairY() {
        return crosshairY;
    }

    /**
     *  Getter Method for drawCrosshair
     */
    public  boolean getDrawCrosshair() {
        return drawCrosshair;
    }


    public boolean getShowGrid() {
        return showGrid;
    }

    /**
     * Sets up the menu bar with components.
     */
    private void setupMenu() {
        menuBar = new JMenuBar();
        JLabel label = new JLabel("Select:  ");
        comboBoxList = updateExcelFiles.getFuturesList();
        JComboBox<String> futuresComboBox = new JComboBox<>(comboBoxList);
        futuresComboBox.setMaximumSize(new Dimension(300, 30));
        futuresComboBox.addActionListener(this::onFutureSelected);

        gridCheckbox = new JCheckBox("Show Grid");
        gridCheckbox.addItemListener(e -> toggleGrid());

        crosshairCheckbox = new JCheckBox("Show Crosshair");
        crosshairCheckbox.addItemListener(e -> toggleCrosshair());

        updateButton = new JButton("Update COT");
        updateButton.addActionListener(e -> updateCOTData());

        menuBar.add(label);
        menuBar.add(futuresComboBox);
        menuBar.add(gridCheckbox);
        menuBar.add(crosshairCheckbox);
        menuBar.add(updateButton);
        gui.setJMenuBar(menuBar);
    }

    /**
     * Sets up the panels for visualization.
     */
    private void setupPanels() {
        oscillatorPanel = new OscillatorPanel(this);

        oscillatorPanel.setPreferredSize(new Dimension(gui.getWidth(), 150));
        cotPanel = new COTPanel(this);
        cotPanel.setPreferredSize(new Dimension(gui.getWidth(), 500));
        cotPanel.setBackground(Color.DARK_GRAY);

        cotPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!selectedFuture.isEmpty()) {
                    mousePoint = e.getPoint();
                    refreshPanels();
                }
            }
        });

        cotPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateMouseOffsets(e);
                refreshPanels();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                crosshairX = e.getX();
                crosshairY = e.getY();
                refreshPanels();
            }
        });

        Container pane = gui.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(cotPanel);
        pane.add(oscillatorPanel);
    }

    /**
     * Updates the selected future and refreshes the data.
     */
    private void onFutureSelected(ActionEvent event) {
        JComboBox<String> comboBox = (JComboBox<String>) event.getSource();
        selectedFuture = (String) comboBox.getSelectedItem();

        if (!Files.exists(Path.of(TABLES_DIRECTORY))) {
            JOptionPane.showMessageDialog(gui, "No data available. Please UPDATE COT", "Data Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadCOTData();
    }

    /**
     *  Getter Method for the selectedFuture
     */
    public String getSelectedFuture() {
        return selectedFuture;
    }

    /**
     *  Getter Method for the NoData
     */
    public boolean getNoData()  {
        return NoData;
    }

    /**
     * Loads COT data based on the selected future.
     */
    private void loadCOTData() {
        logger.info("Loading COT CHart for selected future...");
        List<String> datesList = new ArrayList<>();
        List<Integer> commercialsList = new ArrayList<>();
        List<Integer> largeTradersList = new ArrayList<>();
        List<Integer> smallTradersList = new ArrayList<>();

        NoData = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(TABLES_DIRECTORY + selectedFuture))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                datesList.add(tokens[0]);
                commercialsList.add(Integer.valueOf(tokens[1]));
                largeTradersList.add(Integer.valueOf(tokens[2]));
                smallTradersList.add(Integer.valueOf(tokens[3]));
            }

            Collections.reverse(datesList);
            Collections.reverse(commercialsList);
            Collections.reverse(largeTradersList);
            Collections.reverse(smallTradersList);

            dates = datesList.toArray(new String[0]);
            commercials = commercialsList.toArray(new Integer[0]);
            largeTraders = largeTradersList.toArray(new Integer[0]);
            smallTraders = smallTradersList.toArray(new Integer[0]);

            calculateMaxNetLongPositions();
            calculateOscillatorValues();

            refreshPanels();
        } catch (FileNotFoundException e) {
            showError(ERROR_FILE_NOT_FOUND + e.getMessage());
        } catch (IOException e) {
            showError(ERROR_READING_FILE + e.getMessage());
        }
    }

    /**
     * Calculates the maximum net long positions from commercials and large traders.
     */
    private void calculateMaxNetLongPositions() {
        maxNetLongPositions = Arrays.stream(commercials)
                .map(Math::abs)
                .max(Integer::compareTo)
                .orElse(0);

        maxNetLongPositions = Math.max(maxNetLongPositions,
                Arrays.stream(largeTraders)
                        .map(Math::abs)
                        .max(Integer::compareTo)
                        .orElse(0));

    }

    /**
     * Calculates the oscillator values based on the loaded COT data.
     */
    private void calculateOscillatorValues() {
        oscillatorValues = new Integer[dates.length - OSCILLATOR_OFFSET];

        for (int i = 0; i < oscillatorValues.length; i++) {
            List<Integer> subList = Arrays.asList(commercials).subList(i, OSCILLATOR_OFFSET + i);
            int minIndex = i + subList.indexOf(Collections.min(subList));
            int maxIndex = i + subList.indexOf(Collections.max(subList));

            int currentValue = commercials[i];
            int maxValue = commercials[maxIndex];
            int minValue = commercials[minIndex];

            oscillatorValues[i] = (maxValue - minValue) != 0 ?
                    100 * (currentValue - minValue) / (maxValue - minValue) : 0;
        }
    }

    /**
     * Updates the mouse offsets based on the current mouse position.
     *
     * @param event The mouse event containing the current position.
     */
    private void updateMouseOffsets(MouseEvent event) {
        if (!selectedFuture.isEmpty()) {
            xOffset = event.getX() - mousePoint.x;
            yOffset = event.getY() - mousePoint.y;
        }
    }

    /**
     * Toggles the visibility of the grid.
     */
    private void toggleGrid() {
        showGrid = gridCheckbox.isSelected();
        refreshPanels();
    }

    /**
     * Toggles the visibility of the crosshair.
     */
    private void toggleCrosshair() {
        drawCrosshair = crosshairCheckbox.isSelected();
        refreshPanels();
    }

    /**
     * Updates the COT data in a separate thread.
     */
    private void updateCOTData() {
        new Thread(() -> {
            // Show updating message
            updatingExcelFiles = true;
            gui.repaint();

            updateExcelFiles.readhead();
            downloadingExcelFiles = true;
            gui.repaint();

            updateExcelFiles.downloadCOT();

            writingTableFiles = true;
            downloadingExcelFiles = false;
            gui.repaint();

            updateExcelFiles.update();

            SwingUtilities.invokeLater(() -> {
                updatingExcelFiles = false;
                writingTableFiles = false;
                showUpdatingMessage = false;
                gui.repaint();
                //JOptionPane.showMessageDialog(gui, "UPDATE IS READY", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
            });
        }).start();
    }

    /**
     * Displays an error message in a dialog.
     *
     * @param message The message to display.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(gui, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Refreshes or repaints the COT and Oscillator panels.
     */
    private void refreshPanels() {
        if (!selectedFuture.isEmpty()) {
            // Repaint both the COT and Oscillator panels
            showCOTChart = true;
            showOscillator = true;
            gui.repaint();  // Repaint the GUI to reflect the changes
        }
    }
}