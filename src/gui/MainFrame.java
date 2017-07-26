package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import elements.MapData;
import elements.Node;
import function.OSMParser;
import function.PositionTracker;

import com.starkeffect.highway.GPSDevice;

/**
 * The Application frame.
 * @param <OSMParser>
 */
public class MainFrame extends JFrame{

    /** Node/position Tracker */
    private PositionTracker positionTracker;

    /** Panel in which to display the Map */
    private DisplayPanel displayPanel;

    /** Panel in which serves as a details Panel */
    private DetailsPanel detailsPanel;
    
    /** Small accessory to display Way near mouse */
    private MouseTrackerPanel mouseTracker;

    /** Panel that has buttons */
    private JPanel buttonPanel;

    private MapData data;


    //-- end of fields --//

    /**
     * Constructor for mainFrame.
     * @param MapData data that was parsed from the usb.osm
     */
    public MainFrame(MapData dataHere, File file){  

	super.setTitle("OSM Project: " + file.getName());
	setSize(1000, 700);
	Container contentPane = getContentPane();
	((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));

	GPSDevice device = new GPSDevice(file.getName());
	data = dataHere; 
	positionTracker = new PositionTracker(data, device);
	
	mouseTracker = new MouseTrackerPanel();
	displayPanel = new DisplayPanel(data, positionTracker, mouseTracker);
	GPSInfoPanel gpsInfo = new GPSInfoPanel(displayPanel, positionTracker);
	detailsPanel = new DetailsPanel(data , displayPanel, gpsInfo);
	buttonPanel = new JPanel();

	setUp();
	configureMenu();

	validate();
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setVisible(true);
    }

    /** 
     * Helper method to setup the layout, to keep the constructor clean 
     */
    private void setUp(){ 

	setUpButtonPanel();

	// The display panel and the button panel both go in 
	// at the bottom of BorderLayout.CENTER, which contains eastPanel. 
	JPanel innerBotPanel = new JPanel();
	innerBotPanel.setLayout(new GridLayout(1,2));
	innerBotPanel.add(mouseTracker);
	innerBotPanel.add(buttonPanel);

	JPanel eastPanel = new JPanel();
	eastPanel.setLayout(new BorderLayout());
	eastPanel.add(displayPanel, BorderLayout.CENTER);
	eastPanel.add(innerBotPanel, BorderLayout.SOUTH);

	JSplitPane sPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
		detailsPanel, eastPanel);
	add(sPane);
    }

    /** 
     * Helper method to configure menu bar on top.
     */
    private void configureMenu(){ 
	JMenuBar menuBar = new JMenuBar();
	add(menuBar, BorderLayout.NORTH);

	// ---------- fileMenu ----------------
	JMenu fileMenu = new JMenu("File");

	JMenuItem menuItem = new JMenuItem("Open File...");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		openFile();
	    }
	});
	fileMenu.add(menuItem);
	
	fileMenu.addSeparator();
	
	menuItem = new JMenuItem("Exit OSMProject");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});
	fileMenu.add(menuItem);

	// ---------- navigateMenu ----------------
	JMenu navigateMenu = new JMenu("Navigate");
	
	menuItem = new JMenuItem("Select Places");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		showLocationMenu();
	    }
	});
	navigateMenu.add(menuItem);

	menuItem = new JMenuItem("Select Points");
	menuItem.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e) {
		showNodeMenu();
	    }
	});
	navigateMenu.add(menuItem);

	menuItem = new JMenuItem("Clear Selected");
	menuItem.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e){
		if( !displayPanel.isListening() || 
			!positionTracker.isDriving() ){
		    positionTracker.clear();
		    displayPanel.clearNodes();
		    repaint();
		} else {
		    JOptionPane.showMessageDialog(null, 
			    "Unable. You are driving.");
		}
	    }
	});
	navigateMenu.add(menuItem);

	navigateMenu.addSeparator(); //---------------

	menuItem = new JMenuItem("Get Directions");
	menuItem.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e){
		getDirections();
	    }
	});
	navigateMenu.add(menuItem);

	menuItem = new JMenuItem("Enable Drive Mode");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		displayPanel.startListening();
		if( !positionTracker.isDriving() ){
		    JOptionPane.showMessageDialog(null, "No satellite reception.");
		}
	    }
	});
	navigateMenu.add(menuItem);

	menuItem = new JMenuItem("Disable Drive Mode");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		if( !displayPanel.isListening() ){
		    JOptionPane.showMessageDialog(null, "Already disabled.");
		}
		displayPanel.stopListening();
	    }
	});
	navigateMenu.add(menuItem);

	navigateMenu.addSeparator(); 

	menuBar.add(fileMenu);
	menuBar.add(navigateMenu);

	// ---------- aboutMenu -------------
	JMenu helpMenu = new JMenu("Help");
	menuItem = new JMenuItem("Help");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		HelpFrame hf = new HelpFrame();
		hf.setVisible(true);
	    }
	});
	helpMenu.add(menuItem);
	
	helpMenu.addSeparator();
	
	String about = "CSE 260 Project:\nGPS Navigation System\nVer. 4.0. 20161204";
	menuItem = new JMenuItem("About");
	menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(null, about);
	    }
	});
	helpMenu.add(menuItem);
	
	menuBar.add(helpMenu);
    }

    /**
     * Open function: open a file chooser to select a file,
     * and then display the chosen usb.osm file.
     */
    private void openFile(){	

	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
	int result = fileChooser.showOpenDialog(this);

	if(result != JFileChooser.APPROVE_OPTION){ return; } // canceled
	File file = fileChooser.getSelectedFile();
	String name = file.getName();

	String extension = "";
	try {
	    extension = name.substring(name.lastIndexOf(".") + 1);
	} catch (Exception e) {
	    return;
	}

	// if file not osm format
	if(!extension.equals("osm")) {   
	    JOptionPane.showMessageDialog(this,
		    "This file is not osm format.", "Type Error",
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	// parse file.
	OSMParser prsr = new OSMParser(file);
	try {
	    this.dispose();
	    prsr.parse();
	    MainFrame newFrame = new MainFrame( prsr.getData(), file );
	    newFrame.setTitle(name);
	    newFrame.setVisible(true);
	    System.gc();

	} catch (Exception e){
	    e.printStackTrace();
	    JOptionPane.showMessageDialog(this, "Failed to load.");
	    return;
	}
    }
    
    /** 
     * Show nodes that are locations, in other words, nodes with names. 
     * Realized halfway through that some aren't "drive able" to.
     */
    private void showLocationMenu(){
	JFrame locFrame = new JFrame("Location Selection");

	ArrayList<String> nodeNames = data.getNodeManager().getNodeNames();
	JList<String> list = new JList(nodeNames.toArray());
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.addListSelectionListener(new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		String name = (String) list.getSelectedValue().trim();
		Node n = data.getNodeManager().getByName(name);
		displayPanel.markLocation(n);
	    }
	});
	JScrollPane sp = new JScrollPane(list);
	locFrame.add(sp, BorderLayout.CENTER);
	locFrame.setSize(300, 400);
	locFrame.revalidate();
	locFrame.setVisible(true);
    }

    /**
     * Set up node selection menu. A pop up frame.
     * Select the node by ID and use the button to set
     * node as either start or end.
     */
    private void showNodeMenu(){
	JFrame nodeFrame = new JFrame("Point Selection");
	nodeFrame.setLayout(new BorderLayout());

	// list of nodes by their IDs
	Set<String> nodeIDs = data.getNodeManager().getNodeIDs();
	JList list = new JList<Object>(nodeIDs.toArray());
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.addListSelectionListener(new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		String id = (String) list.getSelectedValue();
		Node n = data.getNodeManager().getNode(id);
		displayPanel.markNode(n); // mark the node selected
	    }
	});
	JScrollPane sp = new JScrollPane(list);
	nodeFrame.add(sp, BorderLayout.CENTER);

	JPanel bPanel = new JPanel();
	bPanel.setLayout(new GridLayout(0,1));

	// button to set as start
	JButton sb = new JButton("Set as Start");
	sb.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		Node n = displayPanel.getNodeToMark();
		setStartNode(n);
	    }
	});
	bPanel.add(sb);
	if(displayPanel.isListening() && positionTracker.isDriving()){
	    sb.setEnabled(false);
	}

	// button to set node as end
	JButton eb = new JButton("Set as End");
	eb.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		Node n = displayPanel.getNodeToMark();
		setEndNode(n);
	    }
	});
	bPanel.add(eb);

	// ended up adding because convenience
	JButton dir = new JButton("Generate Directions");
	dir.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		getDirections();
	    }
	});
	bPanel.add(dir);

	nodeFrame.add(bPanel, BorderLayout.WEST);
	nodeFrame.setSize(430, 300);
	revalidate();
	nodeFrame.setVisible(true);
    }

    /** Helper method; set Start node */
    private void setStartNode(Node n){
	positionTracker.setStartNode(n);
	displayPanel.setStartNode(n);
    }

    /** Helper method; set End node */
    private void setEndNode(Node n){
	positionTracker.setEndNode(n);
	displayPanel.setEndNode(n);
    }

    /** Helper method: the call to get directions */
    private void getDirections(){
	ArrayList<Node> path = positionTracker.getDirections();
	if( path != null ){
	    if(displayPanel.isListening() && positionTracker.isDriving()){
		displayPanel.setGPSPath(path);
	    } else {
		displayPanel.setDirectionsPath(path);
	    }
	    DirectionsFrame dp = new DirectionsFrame(data, path);
	} else {
	    JOptionPane.showMessageDialog(null, "Failed to find route.");
	}
    }

    /**
     * Method to set up the buttonPanel.
     * Contains a [zoomIn] [zoomOut] [re"set"] button.
     */
    private void setUpButtonPanel(){

	buttonPanel.setLayout(new GridLayout(1, 0));

	JButton inButton = new JButton("Zoom in");
	inButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		displayPanel.zoomIn();
	    } 
	});
	buttonPanel.add(inButton);

	JButton outButton = new JButton("Zoom out");
	outButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		displayPanel.zoomOut();
	    }
	});
	buttonPanel.add(outButton);

	JButton centerButton = new JButton("Reset Zoom");
	centerButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
		displayPanel.reset();
	    }
	});
	buttonPanel.add(centerButton);

    }

}

