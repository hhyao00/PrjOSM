package gui;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * It will present the Way object the mouse hovers over.
 * If the way has a name, otherwise shows ID.
 */
public class MouseTrackerPanel extends JPanel{

    private final String LOCATION = "Mouse is near route: ";
 
    /** JLabel to display the area name (if any) */
    private JLabel locationLabel;

    /** Constructor */
    public MouseTrackerPanel(){
	setLayout(new GridLayout(0, 1));
	locationLabel = new JLabel(LOCATION);
	add(locationLabel);
    }
    
    /** Update the label */
    public void updateName(String name){
	locationLabel.setText(LOCATION + name);
    }

}
