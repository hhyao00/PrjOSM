package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import function.PositionTracker;

/** 
 * Purpose of this class is to update in accordance with GPS events and
 * make the program slower. To show the User information on GPS movement.
 */
public class GPSInfoPanel extends JPanel{

    private String NO_SATELLITE_RECP = "No GPSDevice event updates.";

    /** The GPSListener object */
    private PositionTracker positionTracker;

    /** The display panel */
    private DisplayPanel display;

    /** Drive Mode enabled or disabled string */
    private String DRIVEMODE = "DriveMode: ";

    /** The text area that contains directions.*/
    private JTextArea textArea = new JTextArea();

    // --- end of fields --- // 

    /** 
     * The constructor.
     * @param tracker, the GPSListener class we 
     * want to update in accordance to.
     */
    public GPSInfoPanel(DisplayPanel display, PositionTracker tracker){
	this.display = display;
	positionTracker = tracker;
	positionTracker.addInfoListener(this);

	textArea.setText(NO_SATELLITE_RECP + "\n");
	JScrollPane sp = new JScrollPane(textArea);
	add(sp);
    }

    /**
     * Update to the latest GPSListener info.
     * Updating is done from the positionTracker class
     */
    public void update(String info){
	textArea.setText(DRIVEMODE + display.isListening() + "\n"
		+ info + "\n");
    }

    public void reset(){
	textArea.setText("");
    }

}
