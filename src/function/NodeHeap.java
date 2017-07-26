package function;

import java.util.ArrayList;
import java.util.Random;

import elements.Node;

public class NodeHeap {

    /** The array heap. */
    private ArrayList<DistNode> heap; // I SHOULD'VE USED A HASHMAP.

    /** "sentinel" index. If node doesn't exist. */
    private int sentinel = -1;


    //----- end of fields -----//

    // --- private inner class DistNode --- //

    public class DistNode{

	double dist = 0;
	Node node;

	public DistNode(Node n, double dist){
	    this.dist = dist;
	    node = n;
	}

	public double dist(){
	    return dist;
	}
	
	public Node node(){
	    return node;
	}
    }

    // --- Node Heap class --- //

    /** The Constructor */
    public NodeHeap(){
	heap = new ArrayList<DistNode>();
	buildHeap();
    }

    /** The Constructor
     * @param a specified array list */
    public NodeHeap(ArrayList<DistNode> aHeap){
	heap = aHeap;
	buildHeap();
    }

    /** Configure the heap */
    private void buildHeap(){
	for(int i = heap.size()/2 - 1; i >= 0; i--){
	    downwardReheapify(i);
	}
    }

    /** Add to the heap */
    public void add(Node n){
	heap.add(new DistNode(n, -1));
	upwardReheapify(heap.size()-1);
    }

    /** 
     * Remove from the heap. 
     * @return the double value at index 0.
     */
    public Node removeMin(){
	if( heap.isEmpty() ){ return null; }
	Node toReturn = heap.get(0).node;
	DistNode lastV = heap.get(heap.size()-1);
	heap.set(0, lastV);
	heap.remove(heap.size()-1);
	downwardReheapify(0);
	return toReturn;
    }

    /** Check if heap isEmpty() */
    public boolean isEmpty(){
	return heap.isEmpty();
    }

    /** Get the size of the heap */
    public int size(){
	return heap.size();
    }

    /** Check for contains */
    public boolean contains(Node n){
	for(DistNode distN: heap){
	    if(distN.node.equals(n)) return true;
	}
	return false;
    }
    
    /** get a Node's distance */
    public double get(Node n){
	for(DistNode distN: heap){
	    if(distN.node.equals(n)) return distN.dist;
	}
	return -1;
    }

    /**
     * Re establish the heap property after adding a new
     * value to the heap by performing upwardReheapify at
     * the newly added index ( size() - 1 )
     */
    private void upwardReheapify(int i){
	int index1 = parent(i);
	while( heap.get(i).dist < heap.get(index1).dist ){
	    swap( index1, i );
	    i = index1;
	    index1 = parent(i);
	}
    }

    /** 
     * Re heap by exchanging "heavier" values with
     * lighter values; until the lighter values are
     * on top of heavier ones.
     */
    private void downwardReheapify(int i){
	while( i < heap.size() ){
	    int leftIndex = leftChild(i);
	    int rightIndex = rightChild(i);
	    int minIndex = leftIndex;

	    if( leftIndex == sentinel ){ // no left child
		break;
	    }
	    if( rightIndex == sentinel ){ // no right child,
		minIndex = leftIndex;
		rightIndex = leftIndex;

	    } else { // there is both right and left child

		double leftChild = heap.get(leftIndex).dist;
		double rightChild = heap.get(rightIndex).dist;

		if( leftChild < rightChild ){ 
		    minIndex = leftIndex; 
		} else if( rightChild <= leftChild ){
		    minIndex = rightIndex;
		}
	    }

	    // if index (i) is greater than it's children, 
	    // let the lighter children be on top.
	    DistNode child = heap.get(minIndex);
	    DistNode parent = heap.get(i);

	    if( Double.compare(parent.dist, child.dist) >= 0){
		swap( i, minIndex );
		i = minIndex; 
		// the value at min now was the previous i after the swap,
		// we want to reassign i to i, essentially.
	    } else {
		break; // heap property holds; compare(parent, child) < 0
	    }
	}
    }

    /**
     * Index1 precedes index2, but index1 holds the larger value;
     * and large value should proceed the smaller value, which is 
     * held at index2. Swap these two values.
     * @param index1 Index that holds the larger value.
     * @param index2 Index that holds the smaller value.
     */
    private void swap(int index1, int index2){
	DistNode light = heap.get(index2); // smaller value needs to be earlier
	DistNode heavy = heap.get(index1);
	heap.set(index1, light); // set the light value at smaller index
	heap.set(index2, heavy);
    }

    /** 
     * @return the left child of this node.
     * Otherwise, returns sentinel if no left child.
     */
    private int leftChild(int index){
	int leftIndex = index*2 + 1;
	if(leftIndex >= heap.size()) return sentinel;
	return leftIndex;
    }

    /** 
     * @return the right child of this node.
     * Otherwise, returns sentinel if no right child.
     */
    private int rightChild(int index){
	int rightIndex = index*2 + 2;
	if(rightIndex >= heap.size()) return sentinel;
	return rightIndex; 
    }

    /** Returns the parent node of this node */
    private int parent(int index){
	int parentIndex = (index-1)/2;
	return parentIndex;
	// idk if it's ever possible for parent to be null/sentinel.
    }

    /** Print the heap */
    public void printHeap(){

	for(int i = 0; i < heap.size(); i++ ){
	    System.out.print("node:" + heap.get(i).dist);

	    if( leftChild(i) != sentinel){
		System.out.print("; left: " + heap.get(leftChild(i)).dist);
	    }

	    if( rightChild(i) != sentinel){
		System.out.print("; right: " + heap.get(rightChild(i)).dist);
	    }

	    System.out.println();
	}
    }


//
//    public static void main(String[] args){
//
//	NodeHeap nHeap = new NodeHeap();
//	Random rand = new Random();
//	DistNode n;
//
//	for(int i = 0; i < 10 ; i++){
//	    int d = rand.nextInt(10) + 1;
//	    n = new DistNode(d);
//	    nHeap.add(n);
//	}
//
//	for(int i = 0; i < 5; i++){
//	    nHeap.printHeap();
//	    System.out.println("---removeMin()-------------");
//	    nHeap.removeMin();
//	}
//
//	nHeap.add(new DistNode(7.7));
//	nHeap.printHeap();
//    }

}
