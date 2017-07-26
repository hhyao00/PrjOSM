package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import elements.MapData;
import elements.WayManager;

/**
 * Displays all Way names and has a search box for searching a Way 
 * object by name. Also contains GPSInfoPanel that updates with 
 * GPSEvents depending on if displayPanel isListening().
 */
public class DetailsPanel extends JPanel{

    /** The wayManager object */
    private WayManager wayManager;

    /** 
     * a Panel that displays the way names searched,
     * or by default all way names. 
     */
    private JPanel waysPanel;

    /** The DisplayPanel */
    private DisplayPanel displayPanel;

    /** The panel that updates with GPSEvents */
    private GPSInfoPanel gpsInfo;

    // --- end of fields --- // 

    /** 
     * Constructor for the details panel.
     * @param data The MapData.
     */
    public DetailsPanel(MapData data , DisplayPanel display, GPSInfoPanel gpsPanel){ 
	wayManager = data.getWayManager();
	displayPanel = display;
	gpsInfo = gpsPanel;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setUpSearchPanel();
	setUpWaysPanel();
	setUpGPSPanel();
	revalidate();
    }


    /** Helper method for setting up layout: search box part. */
    private void setUpSearchPanel(){ 
	JPanel searchPanel = new JPanel();
	JTextField searchBox = new JTextField("search by name");
	searchBox.setColumns(10);

	JButton searchButton = new JButton("Enter");
	searchButton.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e) {
		String input = searchBox.getText();
		search(input);
	    }
	});
	searchPanel.add(searchBox);
	searchPanel.add(searchButton);
	add(searchPanel);
    }

    /** 
     * Helper method for setting up layout: Set up 
     * the panel containing Way names and search results. 
     */
    private void setUpWaysPanel(){ 
	waysPanel = new JPanel();
	waysPanel.setLayout(new GridLayout(0, 1));
	Dimension d = waysPanel.getPreferredSize();
	d.width = 300;
	d.height = 400;
	waysPanel.setPreferredSize(d);
	waysPanel.setBackground(Color.WHITE);

	updateSearchResults(wayManager.getAlphabeticalOrderedNames());
	add(waysPanel);
    }

    /** 
     * Helper method for performing search operation.
     * A very rudimentary and poor search method.
     * @return The matching search results, or similar ones. 
     */
    private void search( String input ){
	if( input.trim().isEmpty() ) return;

	String[] inputChunks = input.toLowerCase().split("\\s+");
	ArrayList<String> namesSet = wayManager.getAlphabeticalOrderedNames();
	ArrayList<String> results = new ArrayList<String>();
	for(String name : namesSet){
	    if (name.toLowerCase().contains(inputChunks[0])){
		results.add(name);
	    }
	}
	updateSearchResults(results);
    }

    /** Update the results panel with search results */
    private void updateSearchResults( ArrayList<String> stuff ){ 
	JList list = new JList<Object>(stuff.toArray());
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
	list.addListSelectionListener(new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		String name = (String) list.getSelectedValue();
		displayPanel.highlightWays(name);
	    }
	});
	
	JScrollPane sp = new JScrollPane(list);
	waysPanel.removeAll();
	waysPanel.add(sp);
	revalidate();
    }

    /** Update results panel with GPS details */
    private void setUpGPSPanel(){
	JLabel gpsLabel = new JLabel("Latest GPS Info:");
	add(gpsLabel);
	gpsInfo.setLayout(new GridLayout(0, 1));
	
	Dimension d = gpsInfo.getPreferredSize();
	d.width = 300;
	d.height = 100;
	gpsInfo.setPreferredSize(d);
	
	add(gpsInfo);
    }

}


