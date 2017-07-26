package gui;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Trying to display the text file as a frame. */
public class HelpFrame extends JFrame {

    public HelpFrame() {
	setUp();
	revalidate();
    }

    private void setUp() {
	setSize(500, 400);
	setTitle("Help Frame");
	setLayout(new BorderLayout());

	JTextArea textArea = new JTextArea();
	textArea.setLineWrap(true);
	JScrollPane sp = new JScrollPane(textArea);
	BufferedReader buffReader = null;

	try {
	    File file = new File("helpFile");
	    FileReader fileReader = new FileReader(file);
	    buffReader = new BufferedReader(fileReader);
	    textArea.read(buffReader, "file.txt");
	    buffReader.close();
	}  catch (Exception e) {
	    e.printStackTrace();
	}

	textArea.setEditable(false);;
	getContentPane().add(sp, BorderLayout.CENTER);
    }
}
