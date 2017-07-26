package function;
import java.io.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import elements.Element;
import elements.MapData;
import elements.Node;
import elements.NodeManager;
import elements.Relation;
import elements.Way;
import elements.WayManager;
import gui.MainFrame;

/**
 * Sample parser for reading Open Street Map XML format files. Illustrates the
 * use of SAXParser to parse XML.
 *
 * @author E. Stark
 * @date September 20, 2009
 */
public class OSMParser {

    /** OSM file from which the input is being taken. */
    private File file;

    /** A mapData object that hands data over to respective Classes to be managed */
    private MapData mapData;


    /**
     * Initialize an OSMParser that takes data from a specified file.
     *
     * @param s The file to read.
     * @throws IOException
     */
    public OSMParser(File f) {
	file = f;
	mapData = new MapData();
    }

    /**
     * Parse the OSM file underlying this OSMParser.
     */
    public void parse()
	    throws IOException, ParserConfigurationException, SAXException {
	SAXParserFactory spf = SAXParserFactory.newInstance();
	spf.setValidating(false);
	SAXParser saxParser = spf.newSAXParser();
	XMLReader xmlReader = saxParser.getXMLReader();
	OSMHandler handler = new OSMHandler(); // handler does all real work
	xmlReader.setContentHandler(handler);
	InputStream stream = null;
	try {
	    stream = new FileInputStream(file);
	    InputSource source = new InputSource(stream);
	    xmlReader.parse(source); // call backs to handler
	} catch (IOException x) {
	    throw x;
	} finally {
	    if (stream != null)
		stream.close();
	}
    }

    /**
     * Handler class used by the SAX XML parser. The methods of this class are
     * called back by the parser when XML elements are encountered.
     */
    class OSMHandler extends DefaultHandler {

	/** Current character data. */
	private String cdata;

	/** Attributes of the current element. */
	private Attributes attributes;

	/**
	 * ID of the most recent element. I didn't know how else to access the
	 * element ID.
	 */
	private String idTemp;

	/**
	 * The most recently encountered Element object
	 */
	private Element currentElement;

	/**
	 * Get the most recently encountered CDATA.
	 */
	public String getCdata() {
	    return cdata;
	}

	/**
	 * Get the attributes of the most recently encountered XML element.
	 */
	public Attributes getAttributes() {
	    return attributes;
	}

	/**
	 * Get most recent element ID
	 */
	private String getID() {
	    return idTemp;
	}

	/**
	 * Method called by SAX parser when start of document is encountered.
	 */
	public void startDocument() {
	    // System.out.println("startDocument");
	}

	/**
	 * Method called by SAX parser when end of document is encountered.
	 */
	public void endDocument() {
	    // System.out.println("endDocument");
	}

	/**
	 * Method called by SAX parser when start tag for XML element is
	 * encountered.
	 */
	public void startElement(String namespaceURI, String localName,
		String qName, Attributes atts) {
	    attributes = atts;
	    // System.out.println("startElement: " + namespaceURI + ","
	    // + localName + "," + qName);

	    // This parsing relies on the parsing to be in order of
	    // the text. No skipping of CDATA parts. 
	    // so we can go sequentially. 

	    //-------For knowing Map bounds to this specific map-------//
	    // I'm thinking this may be useful for loading certain 
	    // parts of map at a given time?

	    //-------Making Node objects-----------------------------//

	    if(qName.equals("node")){
		Node n = mapData.newNode(atts);
		currentElement = n;
	    }

	    //-------Making Way objects------------------------------//

	    if(qName.equals("way")){
		Way way = mapData.newWay(atts);
		currentElement = way;
	    }

	    if(qName.equals("nd")){
		NodeManager tempManager = mapData.getNodeManager();
		Node node = tempManager.getNode(atts.getValue("ref"));

		if(node != null){  // node is the node we are currently grasped. 
		    Way w = (Way) currentElement;
		    w.addNode(node);}
	    }


	    //-------Making Relation objects--------------------------//
	    if(qName.equals("relation")){
		currentElement = mapData.newRelation(atts);
	    }

	    if(qName.equals("member")){
		Relation r = (Relation) currentElement;
		Way member = null;

		if(atts.getValue("type").equals("way")){
		    String memberID = atts.getValue("ref");
		    WayManager tempManager = mapData.getWayManager();
		    member = tempManager.getWay(memberID);
		    if(member != null){
			r.addMember(member);
		    }
		}

	    }

	    //------General Tag adding----------------------------//
	    if(qName.equals("tag")){ 
		currentElement.addTagValues(atts);
	    }

	    //------------------- Bounds -------------------//
	    if(qName.equals("bounds")){
		mapData.addBounds(atts);
	    }

	    if(qName.endsWith("bound")){
		mapData.addBoundBox(atts);
	    }

	    //-------------------------------------//
	    if (atts.getLength() > 0)
		showAttrs(atts, qName);
	}

	/**
	 * Method called by SAX parser when end tag for XML element is
	 * encountered. This can occur even if there is no explicit end tag
	 * present in the document.
	 */
	public void endElement(String namespaceURI, String localName,
		String qName) throws SAXParseException {
	    //System.out.println("endElement: " + namespaceURI + ","
	    // + localName + "," + qName);
	}

	/**
	 * Method called by SAX parser when character data is encountered.
	 */
	public void characters(char[] ch, int start, int length)
		throws SAXParseException {
	    // OSM files apparently do not have interesting CDATA.
	    // System.out.println("cdata(" + length + "): '"
	    // + new String(ch, start, length) + "'");
	    cdata = (new String(ch, start, length)).trim();
	}

	/**
	 * Auxiliary method to display the most recently encountered attributes.
	 */
	private void showAttrs(Attributes atts, String elementName) {
	    for (int i = 0; i < atts.getLength(); i++) {
		String qName = atts.getQName(i);
		String value = atts.getValue(i);
		String type = atts.getType(i);

		// System.out.println("\t" + qName + "=" + value
		// + "[" + type + "]");
	    }
	}
    }

    /** Get map data */
    public MapData getData(){
	mapData.finalizeData();
	return mapData;
    }

    public void setFile(File file){
	this.file = file;
    }

    public File getFile(){
	return file;
    }

    /**
     * Test driver. Takes filenames to be parsed as command-line arguments.
     * Main method.
     */
    public static void main(String[] args) throws Exception {
	OSMParser prsr = null;
	for (int i = 0; i < args.length; i++) {
	    prsr = new OSMParser(new File(args[i]));
	    prsr.parse();
	}
	MainFrame frame = new MainFrame( prsr.getData(), prsr.getFile() );

    }
}
