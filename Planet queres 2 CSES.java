import java.util.*;
import java.io.*;

public class st {
    static final int LOG = 18;
    static int n, q;
    static int[] dest;
    static int[][] up;
    static int[] depth;      // steps from node to its cycle entry
    static int[] cycleId;    // which cycle this node belongs to (-1 if tail)
    static int[] cyclePos;   // position within the cycle
    static int[] cycleLen;   // length of each cycle
    static int[] color;      // 0=white, 1=gray, 2=black
    static int[] entryNode;  // cycle entry node for each node
    static int cycleCount = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        q = Integer.parseInt(st.nextToken());

        dest = new int[n + 1];
        up = new int[n + 1][LOG];
        depth = new int[n + 1];
        cycleId = new int[n + 1];
        cyclePos = new int[n + 1];
        color = new int[n + 1];
        entryNode = new int[n + 1];

        Arrays.fill(cycleId, -1);
        Arrays.fill(entryNode, -1);

        st = new StringTokenizer(br.readLine());
        for (int i = 1; i <= n; i++) {
            dest[i] = Integer.parseInt(st.nextToken());
            up[i][0] = dest[i];
        }

        // Step 1: Detect cycles using DFS coloring
        List<Integer> tempCycleLens = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            if (color[i] == 0) {
                detectCycle(i, tempCycleLens);
            }
        }

        cycleLen = new int[cycleCount];
        for (int i = 0; i < cycleCount; i++) {
            cycleLen[i] = tempCycleLens.get(i);
        }

        // Step 2: Compute depth[] and entryNode[] for tail nodes using BFS/DFS
        // For cycle nodes, depth = 0, entryNode = themselves
        // For tail nodes, depth = steps to reach cycle entry
        computeDepthAndEntry();

        // Step 3: Build binary lifting table
        for (int k = 1; k < LOG; k++) {
            for (int i = 1; i <= n; i++) {
                up[i][k] = up[up[i][k - 1]][k - 1];
            }
        }

        // Step 4: Answer queries
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q; i++) {
            st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            sb.append(solve(a, b)).append('\n');
        }

        pw.print(sb);
        pw.flush();
    }

    // DFS-based cycle detection
    static void detectCycle(int start, List<Integer> tempCycleLens) {
        // Iterative path tracing
        List<Integer> path = new ArrayList<>();
        int cur = start;

        while (color[cur] == 0) {
            color[cur] = 1; // gray
            path.add(cur);
            cur = dest[cur];
        }

        if (color[cur] == 1) {
            // Found a new cycle, mark cycle nodes
            int cid = cycleCount++;
            tempCycleLens.add(0);
            int pos = 0;
            boolean inCycle = false;

            // Find where cycle starts in path
            int cycleStart = cur;
            int cycleStartIdx = path.indexOf(cycleStart);

            // Mark cycle nodes
            for (int i = cycleStartIdx; i < path.size(); i++) {
                int node = path.get(i);
                cycleId[node] = cid;
                cyclePos[node] = pos++;
                entryNode[node] = node; // cycle nodes are their own entry
                depth[node] = 0;
                color[node] = 2;
            }
            tempCycleLens.set(cid, pos);

            // Mark tail nodes in this path
            for (int i = 0; i < cycleStartIdx; i++) {
                color[path.get(i)] = 2;
            }
        } else {
            // color[cur] == 2, path nodes are tails
            for (int node : path) {
                color[node] = 2;
            }
        }
    }

    // Compute depth and entryNode for all tail nodes
    static void computeDepthAndEntry() {
        // For nodes whose entryNode is already set (cycle nodes), skip
        // For tail nodes, follow path until we hit a known node
        for (int i = 1; i <= n; i++) {
            if (entryNode[i] == -1) {
                computeForNode(i);
            }
        }
    }

    static void computeForNode(int start) {
        List<Integer> path = new ArrayList<>();
        int cur = start;

        while (entryNode[cur] == -1) {
            path.add(cur);
            cur = dest[cur];
        }

        // Now cur has known entryNode and depth
        int baseDepth = depth[cur];
        int baseEntry = entryNode[cur];

        for (int i = path.size() - 1; i >= 0; i--) {
            int node = path.get(i);
            depth[node] = baseDepth + (path.size() - i);
            entryNode[node] = baseEntry;
        }
    }

    static long solve(int a, int b) {
        // Case 1: b is on the tail path from a (before reaching cycle)
        // Check if b is reachable within depth[a] steps
        if (isOnPath(a, b)) {
            return depth[a] - depth[b];
        }

        // Case 2: b is on the cycle that a leads into
        int ea = entryNode[a];
        int eb = entryNode[b];

        // b must be a cycle node and on the same cycle
        if (cycleId[b] == -1) return -1; // b is a tail node, not reachable
        if (cycleId[ea] != cycleId[b]) return -1; 

       
        int stepsInCycle = (cyclePos[b] - cyclePos[ea] + cycleLen[cycleId[ea]]) % cycleLen[cycleId[ea]];
        return (long) depth[a] + stepsInCycle;
    }

    static boolean isOnPath(int a, int b) {
     
        if (depth[a] < depth[b]) return false;
        if (entryNode[a] != entryNode[b] && cycleId[b] == -1) return false;
        if (cycleId[b] != -1 && entryNode[a] != b) return false;
        if (cycleId[b] != -1) {
           
            return entryNode[a] == b && depth[a] >= 0;
        }

        int steps = depth[a] - depth[b];
        int cur = a;
        for (int k = 0; k < LOG; k++) {
            if ((steps & (1 << k)) != 0) {
                cur = up[cur][k];
            }
        }
        return cur == b;
    }
}