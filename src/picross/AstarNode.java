package picross;

/**
 * We use the A* algorithm on arrays. We associate a parent node and a cost to
 * each array.
 *
 * @author arthurmanoha
 */
public class AstarNode {

    // The array
    private int[] array;

    // The cost necessary to reach this array
    private int cost;

    // The node that was modified to reach the current node
    private AstarNode parent;

    private static int NB_ASTARNODES_CREATED = 0;
    private int id;

    public AstarNode(int paramArray[], AstarNode newParent, int newCost) {
        this.array = paramArray;
        this.parent = newParent;
        this.cost = newCost;
        this.id = NB_ASTARNODES_CREATED;
        NB_ASTARNODES_CREATED++;
    }

    public int[] getArray() {
        return this.array;
    }

    public int getCost() {
        return this.cost;
    }

    public String toString() {
        String res = "Node " + id
                + (id < 10 ? " " : "")
                + " of parent " + (parent == null ? "__" : parent.id
                        + (parent.id < 10 ? " " : ""))
                + ": ";
        for (int i = 0; i < array.length; i++) {
            res += array[i] + " ";
        }
        return res;
    }

    public void printHierarchy() {
        System.out.println("AstarNode hierarchy:");
        AstarNode currentNode = this;
        while (currentNode != null) {
            System.out.println("    " + currentNode);
            currentNode = currentNode.getParent();
        }
        System.out.println("end hierarchy.");
    }

    public AstarNode getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }
}
