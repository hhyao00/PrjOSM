package gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;

import elements.MapData;
import elements.Node;
import elements.Relation;
import elements.RelationManager;
import elements.Way;
import elements.WayManager;
import function.PositionTracker;
import function.PixNodeMap;
import function.Scale;
import function.PixWayMap;

/**
 * In real life, DisplayPanel would be equivalent to a tangible GPS device.
 * It represents the real life GPS in a car and the touch screen of it.
 */
public class DisplayPanel extends JPanel{

    private final Color BROWN = new Color(118, 63, 29);
    private final Color BUILDING = new Color(150, 98, 81);
    private final Color DARK_GREEN = new Color(52, 219, 34);
    private final Color DARK_ORANGE = new Color(255, 134, 9);
    private final Color DARK_PINK = new Color(255, 141, 214);
    private final Color DARK_RED = new Color(154, 75, 59);
    private final Color LIGHT_YELLOW = new Color(255, 230, 158);
    private final Color LIGHT_ORANGE = new Color(255, 195, 77);
    private final Color PURPLE = new Color(192, 125, 255);
    private final Color RESIDENTIAL = new Color(131, 109, 87);
    private final Color SERVICE = new Color(93, 152, 18);
    private final Color SEMI_RED = new Color(230, 203, 189);
    private final Color WATER = new Color(81, 169, 227);


    // for slug trail, doesn't really work like I wanted
    private final Stroke DASHED = new BasicStroke(4.3f, BasicStroke.CAP_SQUARE,
	    BasicStroke.JOIN_BEVEL, 5.0f, new float[]{5 ,20}, 0.03f); 

    private WayManager wayManager;
    private RelationManager relationManager;

    /** 
     * Scale used for converting longitude and latitude values
     * To pixels and vice versa.
     */
    private Scale scale;

    /** The node at/ location tracker */
    private PositionTracker positionTracker;

    /** Mouse Location trackers when mouse over name. */
    private MouseTrackerPanel mouseTracker;

    /** Point object to allow panning */
    private Point origin;

    /** Latest point of mouse */
    private Point2D lastPt;

    /** I want to get hit by a car sometimes */
    private double zoomFactor = 1;

    /** specific object Ways to highlight */
    private ArrayList<Way> waysToDraw = null;

    /** Node in Way map */
    private HashMap<String, ArrayList<Way>> nodeInWayMap;

    /** 
     * Path to draw. The set directions directionsPath. 
     * DirectionsPath should change depending:
     * startNode != null and endNode != null. 
     * It should path a non-null startNode to non-null end.
     * 
     * This is null when we are following the car.
     */
    private ArrayList<Node> directionsPath;

    /** 
     * Path from current position of car to destination.
     * indicated on this application (not the jar) 
     */
    private ArrayList<Node> gpsPath;

    /** 
     * Slug trail. Like how a slug crawls and leaves
     * a trail of slime. This is from where car starts to
     * where car currently is. The car is like a slug.
     */
    private ArrayList<String> slugTrail;

    /** To enable mouse click obtain corresponding Way name */
    private PixWayMap pixToWay;

    /** Enable mouse trace/movement to obtain a point Location */
    private PixNodeMap pixToNode;

    /** Point2D representing the car & car location */
    private Point2D car;

    /** 
     * If the car is currently driving, then driveMode is 
     * true; it will remain true until the driver reaches
     * or passes the endNode/destination at some point. 
     * While driveMode is true and car isListening() = true,
     * the startNode == car location.
     * 
     * driveMode may be true, but the display Panel may not
     * be listening/responsive to car motions. 
     * See listening boolean.
     */
    private boolean driveMode = false; 

    /** 
     * The car may or may not be driving. If the car is driving,
     * and listening = true, then startNode select is disabled,
     * and car = start positions with automatic mapping of path
     * to the endNode. If listening = false, then the car may still
     * be driving, however now selection of start and endNodes can
     * continue as usual.
     */
    private boolean listening = true; // default is on

    /** Node to mark; corresponds to pixToNode */
    private Node nodeToMark;

    /** Start node mark */
    private Node startNode;

    /** End node mark */
    private Node endNode;

    /** A node with a name is a location node */
    private Node locationNode;

    /** The set dimension width */
    private double width;

    /** The set dimension height */
    private double height;


    // ----- end of fields ----- //


    /** Constructor */
    public DisplayPanel( MapData data , PositionTracker tracker, MouseTrackerPanel mouseTracker ){

	Dimension d = getPreferredSize();
	d.setSize(700, 600);
	width = d.getWidth();
	height = d.getHeight();
	setPreferredSize(d);

	positionTracker = tracker;
	positionTracker.addGraphicListener(this);
	this.mouseTracker = mouseTracker;
	scale = new Scale( width, height, data.getBounds() );
	pixToWay = new PixWayMap();
	pixToNode = new PixNodeMap();
	slugTrail = new ArrayList<String>();

	wayManager = data.getWayManager();
	nodeInWayMap = wayManager.getnodeInWayMap();
	relationManager = data.getRelationManager();
	origin = new Point();

	configureMouseListeners();
	setBackground(Color.WHITE);
	validate();
	repaint();
    }


    /** Allow map to respond to clicks and drags of the mouse. */
    private void configureMouseListeners(){ 

	// ------ Mouse Click ------ //

	addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(MouseEvent e) {
		lastPt = e.getPoint(); // set the new mouse point.
	    }

	    @Override
	    public void mouseClicked(MouseEvent e){
		int xPt = (int) ((e.getX() - width/2));
		int yPt = (int) ((e.getY() - height/2));
		Point2D pt = new Point(xPt, yPt);

		if( pixToWay.containsPoint( pt ) ){ 
		    String name = pixToWay.getValue(pt);
		    highlightWays(name);
		}

		// clicked a node; a double click.
		if(e.getClickCount() == 2){ 
		    if( pixToNode.containsPoint( pt ) ){
			Node node = pixToNode.getPoint(pt);

			JPopupMenu pMenu = makeSetNodeMenu(node, pt);
			pMenu.show(DisplayPanel.this, e.getX(), e.getY());
		    }
		}
	    } 
	});

	// ------ Mouse Motion ------ //

	addMouseMotionListener(new MouseMotionAdapter() {
	    @Override
	    public void mouseDragged(MouseEvent e) {
		double dx = e.getX() - lastPt.getX();
		double dy = e.getY() - lastPt.getY();
		origin.setLocation(origin.getX() + dx, origin.getY() + dy);

		lastPt = e.getPoint();
		repaint();
	    }

	    String name = "n/a";

	    @Override
	    public void mouseMoved(MouseEvent e){

		int xPt = (int) ((e.getX() - width/2));
		int yPt = (int) ((e.getY() - height/2));
		Point2D pt = new Point(xPt, yPt);

		if( pixToNode.containsPoint(pt) ){
		    Node n = pixToNode.getPoint(pt);
		    nodeToMark = n;
		    ArrayList<Way> ways = wayManager.getnodeInWayMap().get(n.getID());
		    for( Way w: ways ){
			name = w.getName();
			DisplayPanel.this.setToolTipText(name);
			if( name == null ) name = w.getID();
		    }
		    repaint();
		}
		mouseTracker.updateName(name);
	    } 
	});

	//------ Mouse Wheel Listener ------//

	addMouseWheelListener(new MouseWheelListener() {
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
		int degrees = e.getWheelRotation();
		if ( degrees < 0 ){ 
		    zoomIn();
		} else {
		    zoomOut();
		}
	    }
	});
    }

    /** Draw the map. */
    private void drawMap( Graphics2D g2 ){ 
	setGraphicStroke(g2);

	for( Way w : wayManager.getAllWays() ) {
	    double p = w.getPriority();
	    if( zoomFactor <= 0.2 && p >= 1.5) continue;
	    if( zoomFactor <= 0.4 && p >= 2) continue;

	    g2.setColor(Color.DARK_GRAY);

	    String htag = w.getTag("highway");
	    if(htag != null){
		switch(htag){
		    case "residential":
			g2.setColor(RESIDENTIAL);
			break;
		    case "service":
			g2.setColor(SERVICE);
			break;
		    case "track" :
			g2.setColor(DARK_GREEN);
		    default:
			break;
		}
	    }
	    if(w.getTag("building")!=null) g2.setColor(BUILDING);
	    if(w.getTag("natural")!=null) g2.setColor(BROWN);
	    if(w.getTag("waterway")!=null) g2.setColor(WATER);
	    drawWay(g2, w);
	}

	for( Relation r : relationManager.getAllRelations() ){
	    String v = r.getTag("type");
	    if( v != null ){
		switch(v){
		    case "boundary":
			g2.setColor(DARK_RED);
			break;
		    case "route":
			g2.setColor(LIGHT_YELLOW);
			break;
		    case "restriction":
			g2.setColor(SEMI_RED);
			break;
		    default: 
			break;
		}
	    }
	    ArrayList<Way> wr = r.getMembers();
	    this.drawRelation(g2, wr);
	}
    } 

    /** Determine the width of Ways in accordance to zoomFactor. */
    private void setGraphicStroke(Graphics2D g2){
	if( zoomFactor < 1 ){ 
	    g2.setStroke(new BasicStroke((float) zoomFactor));
	} else if( zoomFactor > 2 ){ 
	    // 0.7 is rate of enlargement
	    g2.setStroke(new BasicStroke((float) (0.7 * zoomFactor)));
	}
    }

    /** 
     * Determine if this node lies within the boundaries of the graphic
     * display panel. This is in order to speed up loading/ drawing.
     */
    private boolean shouldDraw(Point2D pt){
	double MARGIN = 30;
	double x = pt.getX();
	double y = pt.getY();
	Rectangle bounds = this.getBounds();
	assert bounds != null;
	if( x > bounds.getMinX() - width/2 - MARGIN 
		&& x < bounds.getMaxX() + width/2 + MARGIN
		&& y > bounds.getMinY() - height/2  - MARGIN 
		&& y < bounds.getMaxY() + height/2 + MARGIN ){
	    return true;
	}
	return false;
    }

    /** Draw a single way 
     * @param w The Way object to be drawn
     * @param g2 Graphics2D object
     */
    private void drawWay( Graphics2D g2 , Way w ){ 
	ArrayList<Node> nodes = w.getNodeSeq();
	Node prevNode = nodes.get(0); // first node to start

	Point2D prevPt = nodeToPt(prevNode);
	double prevX = prevPt.getX();
	double prevY = prevPt.getY();

	savePointNode(prevX, prevY, prevNode); // for dot.

	// loop through the way's nodes
	for (int i = 1; i < nodes.size(); i++) {

	    // get node at i
	    Node node = nodes.get(i);
	    if (node == null) continue;

	    Point2D thisPt = nodeToPt(node);
	    double thisX = thisPt.getX();
	    double thisY = thisPt.getY();
	    if( !shouldDraw(thisPt) ){
		prevX = thisX;
		prevY = thisY;
		continue;
	    }

	    Shape line = new Line2D.Double(prevX, prevY, thisX, thisY);
	    g2.draw( line );

	    savePointWay(prevX, prevY, line, w); // these lines are why code duplication.
	    savePointNode(thisX, thisY, node);

	    //prepare for next loop
	    prevX = thisX;
	    prevY = thisY;

	}
    }

    /** 
     * Draw a specific connected node sequence.
     * @param path The path to draw
     */
    private void drawNodeSeq(Graphics2D g2, ArrayList<Node> path){

	Node prevNode = path.get(0); // first node to start

	Point2D prevPt = nodeToPt(prevNode);
	double prevX = prevPt.getX();
	double prevY = prevPt.getY();

	// loop through the way's nodes
	for (int i = 1; i < path.size(); i++) {

	    // get node at i
	    Node node = path.get(i);
	    if (node == null) continue;

	    Point2D thisPt = nodeToPt(node);
	    double thisX = thisPt.getX();
	    double thisY = thisPt.getY();
	    if( !shouldDraw(prevPt) ){
		prevX = thisX;
		prevY = thisY;
		continue;
	    }

	    Shape line = new Line2D.Double(prevX, prevY, thisX, thisY);
	    g2.draw( line );

	    //prepare for next loop
	    prevX = thisX;
	    prevY = thisY;
	}
    }   

    /** 
     * Draw a Relation consisting of ways.
     * @param members; the ways making up this relation.
     */
    private void drawRelation(Graphics2D g2, ArrayList<Way> members){
	for(Way w: members){
	    g2.setStroke(new BasicStroke((float)(2 * zoomFactor)));
	    this.drawWay(g2, w);
	}
    }

    /** Convert coordinate in longitude, latitude to pixel value. */
    private Point2D coordToPt(double lon, double lat){
	double x = (scale.lonToPix(lon, lat) + origin.getX())*zoomFactor;
	double y = (scale.latToPix(lat) + origin.getY())*zoomFactor;

	Point2D pt = new Point2D.Double(x, y);
	return pt;
    }

    /** Convert a node to pixel value. */
    private Point2D nodeToPt(Node n){
	if(n == null) return null;
	double lon = n.getLongitude();
	double lat = n.getLatitude();

	Point2D pt = coordToPt(lon, lat);
	return pt;
    }

    /** Save point in order to map it to the node. */
    private void savePointNode(double x, double y, Node n){
	double size = 8 * zoomFactor;
	Shape shape = new Rectangle2D.Double(x-size/2, y-size/2, size, size);
	pixToNode.put(shape, n);
    }

    /** Save point in order to map it to the way. */
    private void savePointWay(double x, double y, Shape l, Way w){
	String name = null;
	if( w.getName() != null ){
	    name = w.getName();
	}
	double MARGIN = 4;
	double size = MARGIN * zoomFactor;

	Rectangle2D rect = new Rectangle2D.Double(x-size/2, y-size/2, size, size);
	Point2D point = new Point.Double(x, y);

	pixToWay.put(point, name);   
	pixToWay.put(rect, name);
	pixToWay.put(l.getBounds2D(), name);
    }

    /** Mark node using Node object. Private helper method. */
    private void drawNode(Graphics2D g2, Node n){
	double lon = n.getLongitude();
	double lat = n.getLatitude();
	drawNode(g2, lon, lat);
    }

    /** Mark node position using lon and lat. Private helper method. */
    private void drawNode(Graphics2D g2, double lon, double lat){
	Point2D pt = coordToPt(lon, lat);
	int x = (int) pt.getX();
	int y = (int) pt.getY();
	int r = 5;
	g2.fillOval(x-r, y-r, r*2, r*2);
    }

    /** 
     * Draw an important node; a node with name.
     * Referenced for drawing of string on label:
     * www.stackoverflow.com/questions/6416201/
     * how-to-draw-string-with-background-on-graphics 
     */
    private void drawLocation(Graphics2D g2, Node n){
	double lon = n.getLongitude();
	double lat = n.getLatitude();
	drawNode(g2, lon, lat);
	Point2D pt = coordToPt(lon, lat);

	String name = n.getName();
	FontMetrics fm = g2.getFontMetrics();

	int shift = 3;
	g2.setStroke(new BasicStroke(1.0f));

	g2.setColor(Color.WHITE);
	Rectangle2D rect = fm.getStringBounds(name, g2);
	g2.fillRect((int)pt.getX() + shift,
		(int) pt.getY() - fm.getAscent() - shift,
		(int) rect.getWidth(),
		(int) rect.getHeight());

	g2.setColor(Color.GRAY);
	g2.drawRect((int)pt.getX() + shift,
		(int) pt.getY() - fm.getAscent() - shift,
		(int) rect.getWidth(),
		(int) rect.getHeight());

	g2.setColor(Color.BLACK);
	g2.drawString(name, (int)pt.getX() + shift, (int)pt.getY() - shift);
    }

    /** Draw car. This method mainly used for initialing purposes. */
    public void drawCar(double lon, double lat){
	if( car == null ){ car = new Point2D.Double(); }

	Point2D pt = coordToPt(lon, lat);
	if( !shouldDraw(pt) ) return;
	double x = pt.getX();
	double y = pt.getY();

	car.setLocation(x, y);
	carMoved(lon, lat);
	repaint();
    }

    /** Update the car movement by its longitude and latitude. */
    private void carMoved(double lon, double lat){
	String l = lon + " " + lat;
	slugTrail.add(l);
    }

    /** Draw the slugTrail of the car. */
    private void drawSlugTrail(Graphics2D g2){
	if( slugTrail.size() < 2 ) return;

	String prevT = slugTrail.get(0);
	String[] lonlat0 = prevT.split(" ");

	double pLon = Double.parseDouble(lonlat0[0]);
	double pLat = Double.parseDouble(lonlat0[1]);

	Point2D prevPt = coordToPt(pLon, pLat);
	double prevX = prevPt.getX();
	double prevY = prevPt.getY();

	for(int i = 1; i < slugTrail.size(); i++){
	    String thisT = slugTrail.get(i);
	    String[] lonlat = thisT.split(" ");

	    double thisLon = Double.parseDouble(lonlat[0]);
	    double thisLat = Double.parseDouble(lonlat[1]);

	    Point2D thisPt = coordToPt(thisLon, thisLat);
	    double thisX = thisPt.getX();
	    double thisY = thisPt.getY();
	    
	    if( !shouldDraw(prevPt) ){
		prevX = thisX;
		prevY = thisY;
		continue;
	    }

	    Shape line = new Line2D.Double(prevX, prevY, thisX, thisY);
	    g2.draw(line);

	    prevX = thisX;
	    prevY = thisY;
	}
    }

    /** Set start Node from outside class */
    public void setStartNode(Node n){
	startNode = n;
	positionTracker.setStartNode(n);
	directionsPath = null;
	repaint();
    }

    /** Set end Node from outside class */
    public void setEndNode(Node n){
	endNode = n;
	positionTracker.setEndNode(n);
	directionsPath = null;
	repaint();
    }

    /** Mark node */
    public void markNode(Node n){
	nodeToMark = n;
	repaint();
    }

    /** 
     * Mark a location; an important node w/ name.
     * @param n The node to mark.
     */
    public void markLocation(Node n){
	locationNode = n;
	repaint();
    }

    /** clear */
    public void clearNodes(){
	startNode = null;
	endNode = null;
	locationNode = null;
	directionsPath = null;
	repaint();
    }

    /** Get nodeToMark (not start or end node)*/
    public Node getNodeToMark(){
	return nodeToMark;
    }

    /** Tell Display to highlight ways by same reference. */
    public void highlightWays( String ref ){ 
	waysToDraw = wayManager.getByName(ref);
	repaint();
    }

    /**
     * The highlightPath() method for other classes to call
     * to tell Display to highlight a directionsPath.
     * @param directionsPath 
     */
    public void setDirectionsPath( ArrayList<Node> path ){ 
	directionsPath = path;
	System.out.println(directionsPath.size());
	repaint();
    }

    /** 
     * Path responsive to the car. Path from the
     * car's current position to the endNode.
     * @param path
     */
    public void setGPSPath( ArrayList<Node> path ){ 
	if(listening && driveMode){ // we still care about the driver and he's still driving.
	    startNode = null; // disable startNode selection.
	    directionsPath = null;
	    gpsPath = path;
	}
	repaint();
    }

    /** PaintComponent method to update the visual display */
    @Override
    public void paintComponent(Graphics g){ 
	super.paintComponent(g); 
	Graphics2D g2 = (Graphics2D) g;
	g2.translate(width/2, height/2); // center
	g2.setColor(Color.BLACK);

	pixToWay.clear();
	pixToNode.clear();

	drawMap(g2); // Draw entire map.
	// The rest is drawing specific stuff

	// if some Ways to draw
	if( waysToDraw != null ){
	    g2.setColor(DARK_PINK);
	    g2.setStroke(new BasicStroke(3.0f));
	    for( Way w : waysToDraw ){
		drawWay(g2, w);
	    }
	}

	// draw the directionsPath if set one.
	if( directionsPath != null ){
	    g2.setColor(Color.BLUE);
	    g2.setStroke(new BasicStroke(3.0f));
	    drawNodeSeq(g2, directionsPath);
	}

	// draw the current to end node if set one.
	if( gpsPath != null ){
	    g2.setColor(LIGHT_ORANGE);
	    g2.setStroke(new BasicStroke(5.0f));
	    drawNodeSeq(g2, gpsPath);
	}

	// the car traveled trail. I might rename later.
	if( isListening() && slugTrail.size() > 2 ){
	    g2.setColor(DARK_ORANGE);
	    g2.setStroke(DASHED);
	    drawSlugTrail(g2);
	}

	// if set a start Node
	if( startNode != null ){ 
	    g2.setColor(DARK_GREEN);
	    drawNode(g2, startNode); 
	}

	// if set an end Node
	if( endNode != null ){ 
	    g2.setColor(Color.RED);
	    drawNode(g2, endNode); 
	}

	// following mouse above node point
	if( nodeToMark != null ){
	    g2.setColor(DARK_PINK);
	    drawNode(g2, nodeToMark);
	}

	if( locationNode != null ){
	    g2.setColor(PURPLE);
	    drawLocation(g2, locationNode);
	}

	if( car != null ){
	    g2.setColor(Color.BLUE);
	    double[] c = positionTracker.getCurrentPosition();
	    double lon = c[0];
	    double lat = c[1];
	    drawNode(g2, lon, lat);
	}

	g2.setColor(Color.BLACK);
    }

    /** Tool tip, because I've always wanted to use a tool tip somewhere. */
    @Override
    public JToolTip createToolTip(){
	JToolTip tip = super.createToolTip();
	tip.setBackground(Color.WHITE);
	return tip;
    }

    /** Configure the pop up menu for setting start and end nodes */
    private JPopupMenu makeSetNodeMenu(Node node, Point2D pt){
	JPopupMenu pMenu = new JPopupMenu();
	JMenu setMenu = new JMenu("Set Node as...");

	JMenuItem startItem = new JMenuItem("Start Node");
	if(driveMode && listening){ 
	    startItem.setEnabled(false); // keep both hands on the wheel.
	} 
	startItem.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setStartNode(node);
		repaint();
	    }
	});
	JMenuItem endItem = new JMenuItem("End Node");
	endItem.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setEndNode(node);
		repaint();
	    }
	});
	setMenu.add(startItem);
	setMenu.addSeparator();
	setMenu.add(endItem);
	pMenu.add(setMenu);
	return pMenu;
    }


    /** Zoom In display. */
    public void zoomIn(){
	if(zoomFactor < 60) { zoomFactor += 0.2*zoomFactor; }
	repaint();
    }

    /** Zoom Out display. */
    public void zoomOut(){
	if(zoomFactor > 0.2) { zoomFactor -= 0.2*zoomFactor; }
	repaint();
    }

    /** Resets the zoomFactor to 1 */
    public void reset(){
	zoomFactor = 1;
	repaint();
    }

    /** 
     * Set whether or not driveMode is on.
     * driveMode is on if the car has not yet reached
     * or passed the set endNode when driving.
     */
    public void setDriveMode(boolean b) {
	driveMode = b;
    }

    /** 
     * Stop responding to the driver. This would be 
     * equivalent to the driver just having a GPS on
     * the dash board and doing nothing with it.
     */
    public void stopListening(){
	gpsPath = null;
	slugTrail.clear();
	clearNodes();
	positionTracker.clear();
	listening = false;
    }

    /**
     * Start responding to the driver. This would be
     * equivalent to the driver having the GPS on the
     * dashboard and wanting it to give him directions
     * to a destination (endNode).
     */
    public void startListening(){
	positionTracker.setEndNode(endNode);
	listening = true;
    }

    /** What the GPS is currently doing. */
    public boolean isListening(){
	return listening;
    }

}
