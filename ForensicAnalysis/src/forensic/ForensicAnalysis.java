package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {

        // WRITE YOUR CODE HERE
        int str1 = StdIn.readInt();

        STR[] arr = new STR[str1];

        for(int i = 0; i < str1; i++) {
            String str2 = StdIn.readString();
            int occurences = StdIn.readInt();
            STR str3 = new STR(str2, occurences);

            for (int j = 0; j < arr.length; j++) {
                if (arr[j] == null) {
                    arr[j] = str3;
                    break;
                }
            }
        }

        Profile profile = new Profile(arr);

        return profile; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        // WRITE YOUR CODE HERE
        TreeNode treeNode = new TreeNode();
        treeNode.setName(name);
        treeNode.setProfile(newProfile);

        if (treeRoot == null) {
            treeRoot = treeNode;
        } else {
            TreeNode nodeBST = treeRoot;
            TreeNode previousNode = null;

            while (nodeBST != null) {
                previousNode = nodeBST;

                if (name.compareTo(nodeBST.getName()) < 0) {
                    nodeBST = nodeBST.getLeft();
                } else if (name.compareTo(nodeBST.getName()) > 0) {
                    nodeBST = nodeBST.getRight();
                }
            }

            if (name.compareTo(previousNode.getName()) < 0) {
                previousNode.setLeft(treeNode);
            } else { previousNode.setRight(treeNode); }
        }
    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */

    private int tick = 0;

    private void getMatchingProfileCount(TreeNode nodeBST, boolean isOfInterest) {
        if (nodeBST.getProfile().getMarkedStatus() == isOfInterest) {
            tick++;
        }
        if (nodeBST.getLeft() != null) {
            getMatchingProfileCount(nodeBST.getLeft(), isOfInterest);
        }
        if (nodeBST.getRight() != null) {
            getMatchingProfileCount(nodeBST.getRight(), isOfInterest);
        }
    }

    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // WRITE YOUR CODE HERE

        TreeNode nodeBST = treeRoot;
        getMatchingProfileCount(nodeBST, isOfInterest);
        int result = tick;
        tick = 0;
        return result; // update this line
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {

        // WRITE YOUR CODE HERE
        TreeNode nodeBST = treeRoot;
        flagProfilesOfInterest(nodeBST);
    }

    private void flagProfilesOfInterest(TreeNode nodeBST) {
        STR[] arr = nodeBST.getProfile().getStrs();
        int tick = 0;

        for (int i = 0; i < arr.length; i++) {
            if (numberOfOccurrences(firstUnknownSequence + secondUnknownSequence, arr[i].getStrString()) == arr[i].getOccurrences()) {
                tick++;
            }
        }

        if (tick >= Math.ceil((double)arr.length/2)){
            nodeBST.getProfile().setInterestStatus(true);
        }
        if (nodeBST.getLeft() != null ) {
            flagProfilesOfInterest(nodeBST.getLeft());
        }
        if (nodeBST.getRight() != null) {
            flagProfilesOfInterest(nodeBST.getRight());
        }
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        // WRITE YOUR CODE HERE
        int length = getMatchingProfileCount(false);
        String[] arr = new String[length];

        Queue<TreeNode> queue = new Queue<>();
        queue.enqueue(treeRoot);

        while (!queue.isEmpty()) {
            TreeNode nodeBST = queue.dequeue();
            if (nodeBST.getProfile().getMarkedStatus() == false) {
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] == null) {
                        arr[i] = nodeBST.getName();
                        break;
                    }
                }
            }

            if (nodeBST.getLeft() != null) {
                queue.enqueue(nodeBST.getLeft());
            }
            if (nodeBST.getRight() != null) {
                queue.enqueue(nodeBST.getRight());
            }

        }
        return arr; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        // WRITE YOUR CODE HERE
        treeRoot = removePerson(treeRoot, fullName);
        
    }

    private TreeNode removePerson(TreeNode nodeBST, String fullName) {
        if (nodeBST == null) {return null;}

        if (fullName.compareTo(nodeBST.getName()) < 0) {nodeBST.setLeft(removePerson(nodeBST.getLeft(), fullName));}
        else if (fullName.compareTo(nodeBST.getName()) > 0) {nodeBST.setRight(removePerson(nodeBST.getRight(), fullName));}
        else {
            if (nodeBST.getLeft() == null) { return nodeBST.getRight(); }
            if (nodeBST.getRight() == null) { return nodeBST.getLeft(); }

            TreeNode pointer = nodeBST;
            TreeNode minNode = pointer.getRight();
            while (minNode.getLeft() != null) { minNode = minNode.getLeft(); }
            nodeBST = minNode;
            nodeBST.setRight(deleteMinimum(pointer.getRight()));
            nodeBST.setLeft(pointer.getLeft());
        }
        return nodeBST;
    }

    private TreeNode deleteMinimum(TreeNode nodeBST) {
        if (nodeBST == null) {return null;}
        if (nodeBST.getLeft() == null) {return nodeBST.getRight();}
        nodeBST.setLeft(deleteMinimum(nodeBST.getLeft()));
        return nodeBST;
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        // WRITE YOUR CODE HERE
        String[] arr = getUnmarkedPeople();
        for (int i = 0; i < arr.length; i++) {
            removePerson(arr[i]);
        }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
