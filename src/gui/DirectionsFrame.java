package gui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import elements.MapData;
import elements.Node;
import elements.Way;

/** Pop up frame showing directions */
public class DirectionsFrame extends JFrame{

    private String DOUBLE_LINE = "\n\n";
    private String VIA = " via ";
    private String DISTANCE =  "Distance: ";
    private String MILES = " miles";

    /** Map data object */
    private MapData data;

    /** The directions to display */
    private ArrayList<Node> directions;
    
    // --- end of fields --- // 

    /** Constructor for a pop up directions Panel */
    public DirectionsFrame(MapData data, ArrayList<Node> path){
	super("Directions");
	this.data = data;
	directions = path;

	setUp();

	setSize(500, 400);
	revalidate();
	setVisible(true);
    }

    /** Set up everything */
    private void setUp(){

	JTextArea textArea = new JTextArea();
	textArea.setColumns(50);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setEnabled(false);
	textArea.setDisabledTextColor(Color.BLACK);

	int seg = 0;
	double distance = 0;

	String dir = "";
	
	for(int i = directions.size()-1; i > 0; i--){
	    
	    seg++;
	    Node n = directions.get(i);
	    Node n2 = directions.get(i-1);

	    String nStr = n.toString();
	    String nStr2 = n2.toString();

	    double dist = n.distFrom(n2);
	    distance += dist;

	    Way w = data.connectedBy(n, n2);
	    String wName = w.getName();
	    if(wName == null){ wName = w.getID(); }

	    dir += DOUBLE_LINE + "Segment " + seg + ":\n[ " + nStr + " ] to \n[ " + nStr2 + " ]\n"
		    + DISTANCE + dist + MILES + "\n" + VIA + wName;
	}

	String n1ID = directions.get(directions.size()-1).getID();
	String n2ID = directions.get(0).getID();
	
	String heading = "Directions:\n"
		+ "Total Distance: " + distance + MILES+ "; Segments: " + seg;
	String fromTo = "\nFrom Node " + n1ID + " to Node " + n2ID;
	
	textArea.setText(heading + fromTo + dir);
	JScrollPane sp = new JScrollPane(textArea);
	add(sp);
    }


}
