// Assignment 10
// Cherry Alexander
// acherry
// Davis Jack
// jdavis

import tester.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javalib.colors.*;
import javalib.impworld.*;
import javalib.worldimages.*;

// represents a list
interface IList<T> extends Iterable<T> {
    // Computes the size of this list
    int length();
    // creates a new list with the given item added to the front
    IList<T> add(T item);
    // map the given IFunc over the entire list
    <R> IList<R> map(IFunc<T,R> func);
    // append this list onto the given one
    IList<T> append(IList<T> other);
    // reverse this list
    IList<T> rev();
    IList<T> revT(IList<T> acc);
    // Is this list empty?
    boolean isEmpty();
}

// represents a function object that takes an A and returns an R
interface IFunc<A, R> {
    // Apply the function
    R apply(A a);
}

// represents a predicate object that operates on T's
interface IPred<T> {
    // Apply the function
    boolean apply(T t);
}

// represents a function that returns the x of a posn
class ToString implements IFunc<Integer, String> {
    public String apply(Integer i) {
        return (String)i.toString();
    }
}

// represents a predicate that filters out duplicate Edges
class NoDups<Edge> implements IPred<Edge> {
    
    Edge toCompare;
    
    NoDups(Edge toCompare) { this.toCompare = toCompare; }
    
    public boolean apply(Edge e) {
        
        return this.toCompare.equals(e);
        
    }
    
}

class IListIterator<T> implements Iterator<T> {
    
    IList<T> src;
    
    IListIterator(IList<T> src) { this.src = src; }
    
    public boolean hasNext() {
        
        return !this.src.isEmpty();
        
    }
    
    public T next() {
        
        if (!this.hasNext()) {
            throw new RuntimeException();
        }
        
        Cons<T> sourceAsCons = (Cons<T>)this.src;
        T result = sourceAsCons.first;
        this.src = sourceAsCons.rest;
        return result;
        
    }
    
    public void remove() {
        
        throw new RuntimeException("What are you doing with your life?");
        
    }
    
}

// represents a non-empty list
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }
    // Computes the size of this list
    public int length() {
        return 1 + this.rest.length();
    }
    // creates a new list with the given item added to the front
    public IList<T> add(T item) {
        return new Cons<T>(item, this);
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Cons<R>(func.apply(this.first), this.rest.map(func));
    }
    // appends this list onto the given one
    public IList<T> append(IList<T> other) {
        IList<T> result = this;
        for(T t: other) {
            result = this.add(t);
        }
        return result;
    }
    // reverses this list
    public IList<T> rev() {
        return this.revT(new Mt<T>());
    }
    
    public IList<T> revT(IList<T> acc) {
        return this.rest.revT(new Cons<T>(this.first, acc));
    }
    
    // Is this list empty?
    public boolean isEmpty() { return false; }
    
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
} 

// represents an empty list
class Mt<T> implements IList<T> {
    // Computes the size of this list
    public int length() {
        return 0;
    }
    // creates a new list with the given item added to the front
    public IList<T> add(T item) {
        return new Cons<T>(item, this);
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Mt<R>();
    }
    // appends this list onto the given one
    public IList<T> append(IList<T> other) {
        return other;
    }
    // reverses this list
    public IList<T> rev() {
        return this;
    }
    
    public IList<T> revT(IList<T> acc) {
        return acc;
    }
    
    // Is this list empty?
    public boolean isEmpty() { return true; }
    
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
}

//represents the deque collection of items
class Deque<T> {
  Sentinel<T> header;
  // initializes the deque with a new Sentinel
  Deque() {
      this.header = new Sentinel<T>();
  }
  // initializes the deque with the given Sentinel
  Deque(Sentinel<T> header) {
      this.header = header;
  }
  // counts the number of nodes in this deque
  int size() {
      return this.header.next.countNodes();
  }
  // EFFECTS: Mutates the header and first item of the Deque's prev and next field
  // adds a node to the beginning of the deque
  void addAtHead(T t) {
      new Node<T>(t, this.header.next, this.header);
  }
  // EFFECTS: Mutates the header and last item of the Deque's prev and next field
  // adds a node to the beginning of the deque
  void addAtTail(T t) {
      new Node<T>(t, this.header, this.header.prev);
  }
  // EFFECTS: Mutates the header and first item of the Deque's prev and next field
  // adds a node to the beginning of the deque
  T removeFromHead() {
      if (!this.header.next.isNode()) {
          throw new RuntimeException("cannot remove first item from empty list");
      }
      else {
          T temp = ((Node<T>)(this.header.next)).data;
          this.header.next = this.header.next.next;
          this.header.next.prev = this.header;
          return temp;
      }
  }
  // EFFECTS: Mutates the header and last item of the Deque's prev and next field
  // adds a node to the beginning of the deque
  T removeFromTail() {
      if (!this.header.prev.isNode()) {
          throw new RuntimeException("cannot remove last item from empty list");
      }
      else {
          T temp = ((Node<T>)(this.header.prev)).data;
          this.header.prev = this.header.prev.prev;
          this.header.prev.next = this.header;
          return temp;
      }
  }
  // removes the given node from the deque
  void removeNode(ANode<T> n) {
      if (n.isNode()) {
          n.prev.next = n.next;
          n.next.prev = n.prev;
      }
  }
}

//represents a node in a deck
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  ANode(ANode<T> next, ANode<T> prev) {
      this.next = next;
      this.prev = prev;
  }
  // EFFECTS: Mutates the prev or next field
  // updates this node with a new prev or next node
  void updateSelf(ANode<T> n, boolean isPrev) {
      if (isPrev) {
          this.prev = n;
      }
      else {
          this.next = n;
      }
  }
  // determines whether this node is a non-header node
  boolean isNode() {
      return true;
  }
  // counts how many nodes come after this one inclusively
  int countNodes() {
      return 1 + this.next.countNodes();
  }
}

//represents the header node of a deque
class Sentinel<T> extends ANode<T> {
  Sentinel() {
      super(null, null);
      this.next = this;
      this.prev = this;
  }
  // updates this sentinel with a new prev or next node
  void updateSelf(ANode<T> n, boolean isPrev) {
      if (isPrev) {
          this.prev = n;
      }
      else {
          this.next = n;
      }
  }
  // determines that this is not a non-header node
  boolean isNode() {
      return false;
  }
  // counts how many nodes come after this one inclusively
  int countNodes() {
      return 0;
  } 
}

//represents a non-header node of a deque
class Node<T> extends ANode<T> {
  T data;
  // creates a node with no connecting nodes
  Node(T data) {
      super(null, null);
      this.data = data;
  }
  // EFFECTS: Mutates the prev and next's prev and next field
  // creates a node with connecting nodes
  Node(T data, ANode<T> next, ANode<T> prev) {
      super(null, null);
      this.data = data;
      if (next == null) {
          throw new IllegalArgumentException("next node cannot be null");
      }
      else if (prev == null) {
          throw new IllegalArgumentException("prev node cannot be null");
      }
      else {
          this.next = next;
          this.prev = prev;
          this.next.updateSelf(this, true);
          this.prev.updateSelf(this, false);
      }
  }
}

//represents a Stack
//Used for Depth First Search
class Stack<T> {

    Deque<T> contents;
    Stack(Deque<T> contents) { 
        this.contents = contents; 
    }
    // Add an item to the head of the list
    void push(T item) {
        this.contents.addAtHead(item);
    }
    // Kinda self-explanatory
    boolean isEmpty() {
        return this.contents.size() == 0;
    }
    // Removes and returns the head of the list
    T pop() {
        return this.contents.removeFromHead();
    }
}

// represents a Queue
// Used for Breadth First Search
class Queue<T> {

    Deque<T> contents;

    Queue(Deque<T> contents) {
        this.contents = contents;
    }

    // Adds an item to the tail of this list
    void enqueue(T item) {
        this.contents.addAtTail(item);
    }

    boolean isEmpty() {
        return this.contents.size() == 0;
    }

    // Removes and returns the head of the list
    T dequeue() {
        return this.contents.removeFromHead();
    }
}

// represents a maze cell
class Vertex {
    
    IList<Edge> edges;
    boolean wasSearched;
    boolean correctPath;
    
    int x;
    int y;
    
    Vertex(int x, int y) {
        this.edges = new Mt<Edge>();
        this.wasSearched = false;
        this.correctPath = false;
        
        this.x = x;
        this.y = y;
    }
    
    // Add an Edge with a random weight
    void addRandomEdge(Vertex other) {
        Random randy = new Random();
        Edge toAdd = new Edge(this, other, randy.nextInt());
        this.edges.add(toAdd);
        other.edges.add(toAdd);
    }
    
}

// represents an edge of the maze graph
class Edge {
    Vertex from;
    Vertex to;
    int weight;
    Edge(Vertex from, Vertex to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}

class MazeWorld extends World {
    // Size of the game
    int gameSizeX;
    int gameSizeY;
    
    MazeWorld(int gameSizeX, int gameSizeY) {
        this.gameSizeX = gameSizeX;
        this.gameSizeY = gameSizeY;
    }
    
    // Create a grid of blank Vertices
    ArrayList<ArrayList<Vertex>> createGrid() {
        ArrayList<ArrayList<Vertex>> result = new ArrayList<ArrayList<Vertex>>();
        
        for(int i = 0; i < gameSizeX; i += 1) {
            
            result.add(new ArrayList<Vertex>());
            
        }
        
        for(int i = 0; i < gameSizeX; i += 1) {
            
            for(int i2 = 0; i2 < gameSizeY; i2 += 1) {
                
                result.get(i).add(new Vertex(i, i2));
                
            }
            
        }
        
        return result;
        
    }
    
    // Add edges to the given ArrayList<ArrayList<Vertex>>
    void addEdges(ArrayList<ArrayList<Vertex>> grid) {
        
        // Connections to the left/right
        for(int i = 1; i < grid.size(); i += 1) {
            
            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {
                
                grid.get(i).get(i2).addRandomEdge(grid.get(i - 1).get(i2));
                
            }
            
        }
        
        // Connections to the top/bottom
        for(int i = 0; i < grid.size(); i += 1) {
            
            for(int i2 = 1; i2 < grid.get(i).size(); i2 += 1) {
                
                grid.get(i).get(i2).addRandomEdge(grid.get(i).get(i2 - 1));
                
            }
            
        }
        
    }
    
    // Convert a 2D ArrayList of Vertices to a 1D ArrayList of Edges
    ArrayList<Edge> vertexToEdge(ArrayList<ArrayList<Vertex>> grid) {
        
        ArrayList<IList<Edge>> listOfLists = new ArrayList<IList<Edge>>();
        IList<Edge> edges = new Mt<Edge>();
        
        // Copy all Vertices' Edge lists in grid into listOfLists
        for(int i = 0; i < grid.size(); i += 1) {
            
            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {
                
                listOfLists.add(grid.get(i).get(i2).edges);
                
            }
        }
        
        edges = this.vertexToEdgeHelp(listOfLists);
        
    }
    
    // Concatenates all the lists in the given ArrayList<Edge> into an IList<Edge>
    IList<Edge> vertexToEdgeHelp(ArrayList<IList<Edge>> listOfLists) {
        
        IList<Edge> edges = new Mt<Edge>();
        
        // Append those lists into one large IList<Edge>
        for(IList<Edge> e: listOfLists) {
            
            edges = edges.append(e);
            
        }
        
        return edges;
        
    }
    
    // Implement Union/Find data structure while applying
    // Kruskel's algorithm.
    // EFFECT: mutates the edge lists in each Vertex in the given ArrayList
    ArrayList<ArrayList<Vertex>> kruskel(ArrayList<ArrayList<Vertex>> grid) {
        HashMap<String, String> representatives = new HashMap<String, String>();
        ArrayList<ArrayList<Vertex>> worklist = grid;
        
        // populate hashmap
        for(Integer i = 0; i < grid.size(); i += 1) {
            
            for(Integer i2 = 0; i2 < grid.get(i).size(); i2 += 1) {
                
                // Vertices are represented as their coordinates separated
                // by a dash. i.e. (1, 1) is 1-1.
                String toPut = i.toString() + "-" + i2.toString();
                
                // All values are initialized the same value as the key
                representatives.put(toPut, toPut);
                
            }
            
        }
        
        while(worklist.size() > 0) {
            
            
            
        }
        
        return grid; //THIS IS A STUB: TODO
    }

    // Draws the world TODO
    public WorldImage makeImage() {
        return null;
    }

}


class ExamplesMaze {
    MazeWorld maze0 = new MazeWorld(0, 0);
    MazeWorld maze5 = new MazeWorld(5, 5);

    ArrayList<Vertex> aV0 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV1 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV2 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV3 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV4 = new ArrayList<Vertex>();
    ArrayList<ArrayList<Vertex>> aVFinal = new ArrayList<ArrayList<Vertex>>();
    ArrayList<ArrayList<Vertex>> aVCopy = new ArrayList<ArrayList<Vertex>>();
    
    // List test lists
    IList<Integer> mTI = new Mt<Integer>();
    IList<Integer> listI1 = new Cons<Integer>(1, new Cons<Integer>(2, 
            new Cons<Integer>(3, new Cons<Integer>(4, mTI))));
    IList<Integer> listI2 = new Cons<Integer>(5, this.listI1);
    
    // Function objects
    ToString tS = new ToString();
    
    void initialize() {
        this.aV0.clear();
        this.aV0.add(new Vertex(0, 0));
        this.aV0.add(new Vertex(0, 1));
        this.aV0.add(new Vertex(0, 2));
        this.aV0.add(new Vertex(0, 3));
        this.aV0.add(new Vertex(0, 4));
        this.aV1.clear();
        this.aV1.add(new Vertex(1, 0));
        this.aV1.add(new Vertex(1, 1));
        this.aV1.add(new Vertex(1, 2));
        this.aV1.add(new Vertex(1, 3));
        this.aV1.add(new Vertex(1, 4));
        this.aV2.clear();
        this.aV2.add(new Vertex(2, 0));
        this.aV2.add(new Vertex(2, 1));
        this.aV2.add(new Vertex(2, 2));
        this.aV2.add(new Vertex(2, 3));
        this.aV2.add(new Vertex(2, 4));
        this.aV3.clear();
        this.aV3.add(new Vertex(3, 0));
        this.aV3.add(new Vertex(3, 1));
        this.aV3.add(new Vertex(3, 2));
        this.aV3.add(new Vertex(3, 3));
        this.aV3.add(new Vertex(3, 4));
        this.aV4.clear();
        this.aV4.add(new Vertex(4, 0));
        this.aV4.add(new Vertex(4, 1));
        this.aV4.add(new Vertex(4, 2));
        this.aV4.add(new Vertex(4, 3));
        this.aV4.add(new Vertex(4, 4));
        this.aVFinal.clear();
        this.aVFinal.add(aV0);
        this.aVFinal.add(aV1);
        this.aVFinal.add(aV2);
        this.aVFinal.add(aV3);
        this.aVFinal.add(aV4);
        
        
        this.aVCopy.clear();
        for(int i = 0; i < aVFinal.size(); i += 1) {
            aVCopy.add(aVFinal.get(i)); 
        }
    }
    
    // initializes Vertices in aVCopy 
    void initializeV() {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(0, 1);
        Vertex v3 = new Vertex(0, 2);
        Vertex v4 = new Vertex(1, 0);
        Vertex v5  = new Vertex(1, 0);
        Vertex v6 = new Vertex(1, 2);
        Vertex v7 = new Vertex(2, 0);
        Vertex v8 = new Vertex(2, 1);
        Vertex v9 = new Vertex(2, 2);
        // TODO make edge weights correct and double check edges
        // v2 
        Edge e11 = new Edge(v2, v1, 0);
        // v3
        Edge e12 = new Edge(v3, v2, 0);
        // v4
        Edge e1 = new Edge(v4, v1, 0);
        // v5
        Edge e2 = new Edge(v5, v2, 0);
        Edge e3 = new Edge(v5, v4, 0);
        // v6 
        Edge e4 = new Edge(v6, v3, 0);
        Edge e5 = new Edge(v6, v5, 0);
        // v7 
        Edge e6 = new Edge(v7, v4, 0);
        // v8
        Edge e7 = new Edge(v8, v5, 0);
        Edge e8 = new Edge(v8, v7, 0);
        // v9 
        Edge e9 = new Edge(v9, v6, 0);
        Edge e10 = new Edge(v9, v8, 0);
        IList<Edge> mTE = new Mt<Edge>();
        // row 1
        IList<Edge> l1 = new Cons<Edge>(e1, new Cons<Edge>(e11 , mTE));
        IList<Edge> l2 = new Cons<Edge>(e2, new Cons<Edge>(e11, new Cons<Edge>(e12, mTE)));
        IList<Edge> l3 = new Cons<Edge>(e4, new Cons<Edge>(e12, mTE));
        // row 2
        IList<Edge> l4 = new Cons<Edge>(e1, new Cons<Edge>(e6, new Cons<Edge>(e3, mTE)));
        IList<Edge> l5 = new Cons<Edge>(e2, new Cons<Edge>(e2, new Cons<Edge>(e7, 
                new Cons<Edge>(e3, new Cons<Edge>(e5, mTE)))));
        IList<Edge> l6 = new Cons<Edge>(e4, new Cons<Edge>(e9, new Cons<Edge>(e5,mTE)));
        // row 3
        IList<Edge> l7 = new Cons<Edge>(e6, new Cons<Edge>(e8, mTE));
        IList<Edge> l8 = new Cons<Edge>(e7, new Cons<Edge>(e8, new Cons<Edge>(e10, mTE)));
        IList<Edge> l9 = new Cons<Edge>(e9, new Cons<Edge>(e10, mTE));      
        
    }
    
    // tests length for the interface IList<T> TODO 
    void testLength(Tester t) {
        t.checkExpect(mTI.length(), 0);
        t.checkExpect(listI1.length(), 4);
        t.checkExpect(listI2.length(), 5);
    }
    // tests add for the interface IList<T> TODO
    void testAdd(Tester t) {
        t.checkExpect(mTI.add(2), new Cons<Integer>(2, mTI));
        t.checkExpect(listI1.add(5), listI2);
    }
    // tests apply for the function ToString
    void testToString(Tester t) {
        
        t.checkExpect(tS.apply(2), "2");
        t.checkExpect(tS.apply(-3), "-3");
    }
    // tests map
    void testMap(Tester t) {
        t.checkExpect(listI1.map(tS), new Cons<String>("1",
                new Cons<String>("2", new Cons<String>("3", 
                        new Cons<String>("4", new Mt<String>())))));
        t.checkExpect(mTI.map(tS), new Mt<String>());
    }
    // tests createGrid for the class MazeWorld
    void testCreateGrid(Tester t) {
        this.initialize();
        t.checkExpect(maze5.createGrid(), this.aVFinal);
        t.checkExpect(maze0.createGrid(), new ArrayList<ArrayList<Vertex>>());
    }
    // tests addEdges for the class MazeWorld
    void testAddEdges(Tester t) {
        this.initialize();
        
    }
}