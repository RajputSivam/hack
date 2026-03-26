eimport java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class rr {

    static final int MAXN = 200001;
    static int[] values = new int[MAXN];
    static List<Integer>[] g = new ArrayList[MAXN];
    static int[] subtree_sum = new int[MAXN];
    static int[] dist = new int[MAXN];
    static long maxi = 0;

    static void preCalc(int node, int p)
    {
        subtree_sum[node] = values[node];
        dist[node] = 0;

        for (int c : g[node]) {
            if (c != p) {
                preCalc(c, node);
                subtree_sum[node] += subtree_sum[c];
                dist[node] += (subtree_sum[c]) + (dist[c]);
            }
        }
    }

    static void reroot(int node, int p) {
    maxi = Math.max(maxi, dist[node]);

    for (int c : g[node]) {
        if (c != p) {
            // Remove child c from node
            subtree_sum[node] -= subtree_sum[c];
            dist[node] -= (dist[c] + subtree_sum[c]);

            // Add node as child of c
            subtree_sum[c] += subtree_sum[node];
            dist[c] += (dist[node] + subtree_sum[node]);

            reroot(c, node);

            // Restore c (subtree_sum[node] is still the modified/reduced value — correct to subtract)
            subtree_sum[c] -= subtree_sum[node];
            dist[c] -= (dist[node] + subtree_sum[node]);

            // Restore node (subtree_sum[c] is now back to original)
            subtree_sum[node] += subtree_sum[c];
            dist[node] += (dist[c] + subtree_sum[c]);
        }
    }
}

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        // Initialize adjacency list
        for (int i = 0; i <= n; i++) {
            g[i] = new ArrayList<>();
        }

        // Read node values
        for (int i = 1; i <= n; i++) {
            values[i] = sc.nextInt();
        }

        // Read edges (assuming n-1 edges for a tree)
        for (int i = 0; i < n - 1; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            g[u].add(v);
            g[v].add(u);
        }

        preCalc(1, 0);
        reroot(1, 0);

        System.out.println(maxi);
    }
}