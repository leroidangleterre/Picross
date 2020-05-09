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
    private int score;

    // The node that was modified to reach the current node
    private AstarNode parent;

    private static int NB_ASTARNODES_CREATED = 0;
    private int id;

    private String optionText;

    public AstarNode(int paramArray[], AstarNode newParent) {
        this(paramArray, newParent, Integer.MAX_VALUE, "_creation");
    }

    public AstarNode(int paramArray[], AstarNode newParent, int score, String options) {
        this.array = paramArray;
        this.parent = newParent;
        this.score = score;
        this.id = NB_ASTARNODES_CREATED;
        NB_ASTARNODES_CREATED++;
        optionText = options;
    }

    public int[] getArray() {
        return this.array;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public int getScore() {
        return this.score;
    }

    public String toString() {
        String res = "Node " + id
                + (id < 10 ? " " : "")
                + " of parent " + (parent == null ? "__" : parent.id
                        + (parent.id < 10 ? " " : ""))
                + ": {";
        for (int i = 0; i < array.length; i++) {
//            res += array[i] + " ";
            switch (array[i]) {
            case Grid.EMPTY:
                res += Grid.emptyCharacter;
                break;
            case Grid.FILLED:
                res += Grid.filledCharacter;
                break;
//            case Grid.NOT_PROCESSED:
//                res += Grid.notProcessedCharacter;
//                break;
            default:
                break;
            }
            res += " ";
        }
        res += "} (d=" + this.score + ") " + optionText;
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
