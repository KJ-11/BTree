import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * B+Tree Structure Key - StudentId Leaf Node should contain [ key,recordId ]
 */
class BTree {

  /**
   * Pointer to the root node.
   */
  private BTreeNode root;
  /**
   * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
   **/
  private int t;

  BTree(int t) {
    this.root = null;
    this.t = t;
  }

  long searchHelper(long studentId, BTreeNode currNode) {
    
    // helper method to search through nodes recursively
    
    if (currNode.leaf) {
      // leaf node found, value should be in this node
      for (int i = 0; i < currNode.n; i++) {
        if (studentId == currNode.keys[i]) {
          return currNode.values[i];
        }
      }
      return -1; // return val if record not found
    } else {
      // find pointer to node where value should be
      int index = -1; // initialize to -1 (out of bounds)
      for (int i = 0; i < currNode.n; i++) {
        if (studentId < currNode.keys[i]) {
          index = i; // children index found
          break;
        }
      }
      if (index == -1) {
        index = currNode.children.length - 1; // assign children index to max val as currid > greatest id at currNode
      }
      return searchHelper(studentId, currNode.children[index]); // recursively search child
    }
  }

  long search(long studentId) {
    /**
     * TODO: Implement this function to search in the B+Tree. Return recordID for the given
     * StudentID. Otherwise, print out a message that the given studentId has not been found in the
     * table and return -1.
     */

    if (root == null) {
      return -1;
    }
    
    return searchHelper(studentId, root);

  }

  void sortArray(BTreeNode toSort) {

    // bubble sort algorithm to sort keys and values (only sorts filled vals i.e non 0)

    long tempValue, tempKey;
    boolean swapped;

    for (int i = 0; i < toSort.n - 1; i++) {

      swapped = false;

      for (int j = 0; j < toSort.n - i - 1; j++) {

        if (toSort.keys[j] > toSort.keys[j + 1]) {
          // swap
          tempValue = toSort.values[j];
          tempKey = toSort.keys[j];
          toSort.values[j] = toSort.values[j + 1];
          toSort.keys[j] = toSort.keys[j + 1];
          toSort.values[j + 1] = tempValue;
          toSort.keys[j + 1] = tempValue;
          // set swapped to true
          swapped = true;
        }

      }

      // if no elements swapped by inner loop, sorting over and break
      if (swapped == false) {
        break;
      }

    }
  }

  BTreeNode findLeaf(Student student, BTreeNode node) {

    // recursively finds which node the student should be inserted at

    if (node.leaf) {

      // leaf node found
      return node;

    } else {

      int index = -1;
      for (int i = 0; i < node.n; i++) {
        if (student.studentId < node.keys[i]) {
          index = i; // index found
          break;
        }
      }

      if (index == -1) {
        index = 2 * t; // assign index to max val as currid > greatest id at node
      }

      return findLeaf(student, node.children[index]);
    }
  }

  BTree insert(Student student) {
    /**
     * TODO: Implement this function to insert in the B+Tree. Also, insert in student.csv after
     * inserting in B+Tree.
     */

    // step 1: fill root node if it doesn't exist
    if (this.root == null) {

      // if tree is empty

      BTreeNode newNode = new BTreeNode(t, true);
      newNode.keys[0] = student.studentId;
      newNode.values[0] = student.recordId;
      newNode.n++;
      this.root = newNode; // initialize root
      return this;
    } else {
      // step 2: find where new student should go

      BTreeNode location = findLeaf(student, this.root);

      // step 3: try inserting at node, rebalance recursively if needed

      // case 1: leaf has space, add and we are done
      if (location.n < 2 * t) {
        for (int i = 0; i < location.keys.length; i++) {
          if (location.keys[i] == 0) {
            // current value of keys is empty
            location.keys[i] = student.studentId;
            location.values[i] = student.recordId;
            location.n++;
            // sort array
            sortArray(location);
            break;
          }
        }
      } else {
        
        // case 2: overflow in leaf node, need to split
        int index = -1;
        for (int i = 0; i < location.n; i++) {
          if (student.studentId < location.keys[i]) {
            index = i; // index found
            break;
          }
        }

        if (index == -1) {
          index = 2 * t; // assign index to max val as currid > greatest id at node
        }
        
        BTreeNode left = new BTreeNode(t, true);
        BTreeNode right = new BTreeNode(t, true);
        
        
        if (index < t) {
          // new student goes to left leaf node
          for (int i = 0; i < t - 1; i++) {
            left.keys[i] = location.keys[i];
            left.values[i] = location.values[i];
            left.n++;
          }
          left.keys[t-1] = student.studentId;
          left.values[t-1] = student.recordId;
          left.n++;
          left.next = right;
          sortArray(left);
          
          for (int i = t - 1; i < location.n; i++) {
            // figure this out
          }
          // set parents and check overflow
        } else {
          // goes to right leaf node
          for (int i = 0; i < t; i++) {
            left.keys[i] = location.keys[i];
            left.values[i] = location.values[i];
            left.n++;
          }
          left.next = right;
          for (int i = t; i < location.n; i++) {
            // figure this out
          }
          right.keys[t] = student.studentId;
          right.values[t] = student.recordId;
          right.n++;
          sortArray(right);
          // set parents and check overflow
        }
      }



      // subcase 2: overflow in leaf node recursively causes overflow in non-leaf node

    }
    return this;
  }

  boolean delete(long studentId) {
    /**
     * TODO: Implement this function to delete in the B+Tree. Also, delete in student.csv after
     * deleting in B+Tree, if it exists. Return true if the student is deleted successfully
     * otherwise, return false.
     */
    return true;
  }

  List<Long> print() {

    List<Long> listOfRecordID = new ArrayList<>();

    /**
     * TODO: Implement this function to print the B+Tree. Return a list of recordIDs from left to
     * right of leaf nodes.
     *
     */
    BTreeNode currNode = this.root; // initialize first node

    while (currNode.leaf == false) {
      currNode = currNode.children[0]; // find left most leaf node
    }

    if (currNode.next == null && currNode.leaf) {
      // case where there is only one leaf node
      for (int i = 0; i < currNode.n; i++) {
        listOfRecordID.add(currNode.values[i]); // add all values in current leaf node
      }
    }

    while (currNode.next != null) {
      // case where there are multiple leaf nodes
      for (int i = 0; i < currNode.n; i++) {
        listOfRecordID.add(currNode.values[i]); // add all values in current leaf node
      }
      currNode = currNode.next;
    }

    if (currNode.next == null && currNode.leaf) {
      // last leaf node
      for (int i = 0; i < currNode.n; i++) {
        listOfRecordID.add(currNode.values[i]); // add all values in current leaf node
      }
    }
    return listOfRecordID;
  }
}
