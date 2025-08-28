import java.awt.image.BufferedImage;
import java.util.*;

class Node {
    int x, y;
    int gCost, hCost;
    Node parent;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = Integer.MAX_VALUE; 
        this.hCost = 0;
    }

    int getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

public class AStarPathfinding {
    private static final int[] dX = {0, 0, -1, 1};
    private static final int[] dY = {-1, 1, 0, 0};
    private int mapWidth;
    private int mapHeight;
    private BufferedImage mapImage;
    private Node startNode;
    private Node endNode;

    public AStarPathfinding(BufferedImage mapImage) {
        this.mapHeight = mapImage.getHeight();
        this.mapWidth = mapImage.getWidth();
        this.mapImage = mapImage;
    }

    public ArrayList<Node> aStar(Node startNode, Node endNode) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getFCost));
        Map<String, Node> allNodes = new HashMap<>(); 

        this.startNode = startNode;
        this.endNode = endNode;
        if (!isInBorders(startNode.x, startNode.y) || !isInBorders(endNode.x, endNode.y)) {return new ArrayList<>();}

        Set<Node> closedList = new HashSet<>();

        startNode.gCost = 0;
        startNode.hCost = heuristic(startNode, endNode);
        openList.add(startNode);
        allNodes.put(nodeKey(startNode.x, startNode.y), startNode);

        while (!openList.isEmpty() && GameWindow.isGameRunning) {
            Node current = openList.poll();

            if (current.equals(endNode)) {
                return reconstructPath(current);
            }

            closedList.add(current);
            for (int i = 0; i < 4; i++) {
                int newX = current.x + dX[i];
                int newY = current.y + dY[i];

                if (!isInBorders(newX, newY) || isBlocked(mapImage, newX, newY)) {
                    continue;
                }

                String key = nodeKey(newX, newY);
                Node neighbor = allNodes.getOrDefault(key, new Node(newX, newY));
                allNodes.putIfAbsent(key, neighbor);

                if (closedList.contains(neighbor)) {
                    continue;
                }

                int tentativeGCost = current.gCost + 1;

                if (tentativeGCost < neighbor.gCost || !openList.contains(neighbor)) {
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = heuristic(neighbor, endNode);
                    neighbor.parent = current;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    } else {
                        openList.remove(neighbor);
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); 
    }

    private ArrayList<Node> reconstructPath(Node node) {
        ArrayList<Node> path = new ArrayList<>();
        while (node != null  && GameWindow.isGameRunning) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private boolean isInBorders(int x, int y) {
        return x >= 0 && y >= 0 && x < mapWidth && y < mapHeight;
    }

    public boolean isBlocked(BufferedImage map, int x, int y) {
        return true;
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private String nodeKey(int x, int y) {
        return x + "," + y;
    }

    public static void compressPath(ArrayList<Node> road, int factor) {
        for (int i = road.size() - 2; i > 0; i -= factor) {
            road.remove(i);
        }
    }
}
