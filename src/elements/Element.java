package elements;
import java.util.HashMap;

import org.xml.sax.Attributes;

/**
 * Subclasses of this class are Elements as specified in the XML file with (e).
 * Node, way and relation extend this superclass. Because they all have this 
 * addTagValues() method and getTag() method.
 */
public abstract class Element {

    /** The associated tags (k, v) of the Element object */
    private HashMap<String, String> tagMap; 


    // -- end of fields -- //

    /**
     * The Constructor for this Element object
     */
    public Element(){
	tagMap = new HashMap<String, String>();
    }

    /** 
     * Add the (k, v ) of the tag of this way.
     * @param atts The attributes of the elementName = tag.
     */
    public void addTagValues( Attributes atts ){
	String key = atts.getValue("k");
	String value = atts.getValue("v");
	tagMap.put(key, value);
    }

    /**
     * Get a tag of this Element. Specified tag key may not exist.
     * For example, if the key is "name", then return value will be name.
     * If name does not exist, then the return value will be null.
     * @param key The key of the tag.
     * @return value The value associated with the key.
     * @return null If the key does not exist for this Element.
     */
    public String getTag( String key ){ 
	String value = tagMap.get(key);
	return value;
    }


}
