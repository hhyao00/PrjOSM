package function;
import java.util.HashMap;

/**
 * A Scale class for performing conversions between pixels and 
 * longitude/latitude values. Longitude is equivalent to x, and
 * latitude is equivalent to y on a plane.
 */
public class Scale{

    //default values are for entire world
    private double MIN_LAT = -90.0; // y
    private double MIN_LON = -180.0; // x
    private double MAX_LAT = 90.0;
    private double MAX_LON = 180.0;

    /** Latitude scale factor */
    private double scaleFactor;

    /** Center latitude value given bounds */
    private double latCenter;

    /** Center longitude value given bounds */
    private double lonCenter;

    /** Center x value of the display panel */
    private double centerX;

    /** Center y value of the display panel */
    private double centerY;

    /** The constructor */
    public Scale( double width, double height, HashMap<String, String> bounds) { 
	centerX = width/2;
	centerY = height/2;

	// obtain for specific map and overwrite 
	for(int i = 0 ; i < bounds.size() ; i++ ){
	    MIN_LAT = Double.parseDouble(bounds.get("minlat"));
	    MAX_LAT = Double.parseDouble(bounds.get("maxlat"));
	    MIN_LON = Double.parseDouble(bounds.get("minlon"));
	    MAX_LON = Double.parseDouble(bounds.get("maxlon"));
	}

	//pixels per degree
	scaleFactor = 1300 / Math.abs(MAX_LAT - MIN_LAT);

	latCenter = (MIN_LAT + MAX_LAT)/2; 
	lonCenter = (MIN_LON + MAX_LON)/2; 
    }

    /**
     * Scale latitude value to pixel.
     * @param lat the latitude of the Node.
     * @return the y-value corresponding to latitude.
     */
    public double latToPix(double lat) { 
	double pixY = ((lat - latCenter) * scaleFactor) + centerY;
	return  -(pixY);
    }

    /** 
     * Scale longitude value to pixel. 
     * @param lon the longitude value
     * @param lat the latitude value since longitude depends on latitude.
     * @return the x-value corresponding to longitude.
     */
    public double lonToPix(double lon, double lat) { 
	double radLat = Math.cos(Math.toRadians(lat));
	double pixX = ((lon - lonCenter) * (scaleFactor * radLat)) + centerX; 
	return pixX;
    }

    /**
     * Converts pixel (x value) value to longitude.
     * @param pixX the x pixel value.
     * @param pixY the y pixel value; this is because longitude depends on latitude.
     * @return the longitude, at (pixX, pixY)
     */
    public double pixToLon( double pixX , double pixY ) { 
	double lat = pixToLat(pixY);
	double radLat = Math.cos(Math.toRadians(Math.abs(lat)));
	double lon = ((pixX - centerX) / (radLat * scaleFactor)) + lonCenter;
	return lon;
    }

    /** 
     * Converts a pixel (y value) value to latitude.
     * @param pixY the y pixel value.
     * @return the latitude value given y-pixel value. 
     */
    public double pixToLat( double pixY ){ 
	double lat = ((-pixY - centerY)/scaleFactor) + latCenter;
	return lat;
    }



}
