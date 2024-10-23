import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

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
    public static JFrame gui;

    /**
     * MenuBar in the top of the GUI
     */
    public static JMenuBar menuBar;

    /**
     * List with the Future Name Abbreviations that can be selected in the ComboBox.
     */
    public static String[] comboBoxList;

    /**
     * The Panel for the visualization of the COT charts.
     */
    public static JPanel cotPanel;

    /**
     * The Panel for the visualization of the oscillatorValues
     */
    public static JPanel oscillatorPanel;

    /**
     * The name of the selected Future in the CpmboBox.
     */
    public static String selectedFuture = "";

    /**
     * List that contains all the dates of the COT excel files.
     */
    public static String[] dates;

    /**
     * List that contains all the net long positions of the commercials.
     */
    public static Integer[] commercials;

    /**
     * List that contains all the net long positions of the large traders.
     */
    public static Integer[] largeTraders;

    /**
     * List that contains all the net long positions of the small traders.
     */
    public static Integer[] smallTraders;

    /**
     * List contains all the yOffset-values of the oscillatorValues.
     */
    public static Integer[] oscillatorValues;

    /**
     * X coordinate of the current position of the cross of the crosshair.
     */
    public static int crosshairX;

    /**
     * Y coordinate of the current position of the cross of the crosshair.
     */
    public static int crosshairY;

    /**
     * Show crosshair or not.
     */
    public static boolean drawCrosshair = false;

    /**
     * Show showGrid or not.
     */
    public static boolean showGrid = false;

    /**
     * Select showGrid.
     */
    public static JCheckBox gridCheckbox;

    /**
     * Select crosshair.
     */
    public static JCheckBox crosshairCheckbox;

    /**
     * Current position of the mouse cursor.
     */
    public static Point mousePoint;

    /**
     * X coordinate of the current mouse cursor position.
     */
    public static int xOffset = 0;

    /**
     * Y coordinate of the current mouse cursor position.
     */
    public static int yOffset = 0;

    /**
     * Update button.
     */
    public static JButton updateButton;

    /**
     * Instance of UpdateExcelFiles.
     */
    public static UpdateExcelFiles updateExcelFiles;

    /**
     * The maximum net long positions of the commercials.
     */
    public static int maxNetLongPositions;

    /**
     * Constant that defines the size if the showGrid.
     */
    final static int deltaX = 5;

    /**
     * Offset for oscillator calculation
     */
    private static final int OSCILLATOR_OFFSET = 26;

    /**
     * Directory for table files
     */
    private static final String TABLES_DIRECTORY = "tables/";

    /**
     * Error message prefix
     */
    private static final String ERROR_FILE_NOT_FOUND = "File not found: ";

    /**
     * Error message prefix
     */
    private static final String ERROR_READING_FILE = "Error reading file: ";

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
        updateExcelFiles = new UpdateExcelFiles();
        updateExcelFiles.init();

        gui = new JFrame("COTViz");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.addComponentListener(new ResizeListener());

        setupMenu();
        setupPanels();

        gui.pack();
        gui.setVisible(true);
        gui.repaint();
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
        oscillatorPanel = new OscillatorPanel();
        oscillatorPanel.setPreferredSize(new Dimension(gui.getWidth(), 150));
        cotPanel = new COTPanel();
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
        loadCOTData();
    }

    /**
     * Loads COT data based on the selected future.
     */
    private void loadCOTData() {
        List<String> datesList = new ArrayList<>();
        List<Integer> commercialsList = new ArrayList<>();
        List<Integer> largeTradersList = new ArrayList<>();
        List<Integer> smallTradersList = new ArrayList<>();

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
            int minIndex = subList.indexOf(Collections.min(subList));
            int maxIndex = subList.indexOf(Collections.max(subList));

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
            COTPanel.updatingExcelFiles = true;
            gui.repaint();

            updateExcelFiles.readhead();
            COTPanel.downloadingExcelFiles = true;
            gui.repaint();

            updateExcelFiles.downloadCOT();

            COTPanel.writingTableFiles = true;
            COTPanel.downloadingExcelFiles = false;
            gui.repaint();

            updateExcelFiles.update();

            SwingUtilities.invokeLater(() -> {
                COTPanel.updatingExcelFiles = false;
                COTPanel.writingTableFiles = false;
                COTPanel.showUpdatingMessage = false;
                gui.repaint();
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
}