package elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.xml.sax.Attributes;

/**
 * Class for creating and managing Relation objects. 
 * Includes a HashMap to map relations to their proper ID. 
 */
public class RelationManager {

    /** HashMap for mapping relations to their IDs */
    private HashMap<String, Relation> relationMap;

    /** HashMap for mapping relations to their names (if any) */
    private HashMap<String, Relation> relationNameMap;

    // ---- end of fields ---- //

    /**
     * The constructor. Constructs a RelationManager object
     * that will manage Relation objects.
     */
    public RelationManager(){
	relationMap = new HashMap<String, Relation>();
	relationNameMap = new HashMap<String, Relation>();
    }

    /** 
     * Creates a new Relation object.
     * @param atts The attributes of the Relation object.
     */
    public Relation newRelation(Attributes atts){ 
	String id = atts.getValue("id");
	Relation r = new Relation(id, atts);
	relationMap.put(id, r);
	return r;
    }

    /**
     * Get a Relation object given either its ID or name.
     * @param ref The Relation object want to get.
     * @return the Relation object of the desired name.
     * @return null if the object does not exist.
     */
    public Relation getRelation(String ref){
	return relationMap.get(ref);
    }

    /**
     * Get all Relation objects themselves.
     * @return the set of all of Relation objects.
     */
    public Collection<Relation> getAllRelations(){
	return relationMap.values();
    }

    /**
     * Get all Relation objects by their id.
     * @return the set of all of Relation object IDs
     */
    public Set<String> getAllRelationIDs(){
	return relationMap.keySet();
    }

    /**
     * Get all Relation objects that have a name.
     * @return the set of all nameable Relation objects.
     */
    public Collection<Relation> getNameableRelations(){
	return relationNameMap.values();
    }

    /**
     * Get all Relation objects that have a name by their names.
     * @return the set of all nameable Relation object names.
     */
    public Set<String> getNameableRelationNames(){
	return relationNameMap.keySet();
    }



}
