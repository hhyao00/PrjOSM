package elements;

import java.util.ArrayList;

import org.xml.sax.Attributes;

/**
 * Relation class representing associations of ways ( members ) and geographical areas.
 */
public class Relation extends Element{

    /** unique ID of this object */
    private String id;

    /** The name of the Relation object (may be null) */
    private String name;

    /** The attributes of the Relation object */
    private Attributes attributes;

    /** The members of the Relation object*/
    private ArrayList<Way> members;

    /**
     * The constructor;
     * @param atts The attributes of this Relation
     */
    public Relation( String id, Attributes atts ){ 
	this.id = id;
	attributes = atts;
	members = new ArrayList<Way>();
    }

    /** Get the id of this object. */
    public String getID(){ return id; }

    /**
     * Add the relation object's members.
     * @param way The member, that is a way, to be added.
     */
    public void addMember(Way w){
	members.add(w);
    }

    /**
     * Get members of this Relation.
     * @return the members of this relation
     */
    public ArrayList<Way> getMembers(){
	return members;
    }

    /**
     * Get the name of this relation object.
     * @return the name of this object
     * @return null if there is no name.
     */
    public String getName(){ 
	name = getTag("name");
	return name;
    }

    /**
     * Check if this relation object contains a certain Way object.
     * @param way the Way object possibly contained
     * @return true if this relation object contains a certain Way object.
     */
    public boolean containsWay( Way way ){ 
	return members.contains(way);
    }

    /**
     * "equals" is defined by having the same ID number.
     * @param otherString
     * @return true if otherRef and this obj are equal.
     */
    public boolean equals(String otherString) {
	if (otherString.equals(id)) {
	    return true;
	}
	return false;
    }

    /**
     * "equals" is defined by having same ID number.
     */
    @Override
    public int hashCode() {
	int hashC = Integer.parseInt(id);
	return hashC;
    }

}
