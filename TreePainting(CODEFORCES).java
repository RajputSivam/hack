import java.util.*;
import java.io.*;

public class  rr{
    static List<Integer>[] adj;
    static long[] sub;
    static long[] f;
    static int[] par;
    static int n;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine().trim());

        adj = new ArrayList[n + 1];
        sub = new long[n + 1];
        f   = new long[n + 1];
        par = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            adj[i] = new ArrayList<>();
            sub[i] = 1;
        }

        for (int i = 0; i < n - 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            adj[u].add(v);
            adj[v].add(u);
        }

        // Step 1: Iterative DFS to get subtree sizes rooted at 1
        int[] order = new int[n];
        int idx = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        par[1] = 0;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            order[idx++] = u;
            for (int v : adj[u]) {
                if (v != par[u]) {
                    par[v] = u;
                    stack.push(v);
                }
            }
        }

        // Bottom-up: compute sub[v]
        for (int i = n - 1; i >= 0; i--) {
            int u = order[i];
            if (par[u] != 0) {
                sub[par[u]] += sub[u];
            }
        }

        // Step 2: f[1] = sum of all sub[v]
        for (int v = 1; v <= n; v++) {
            f[1] += sub[v];
        }

        // Step 3: Top-down rerooting: f[child] = f[parent] + n - 2*sub[child]
        for (int i = 0; i < n; i++) {
            int u = order[i];
            for (int v : adj[u]) {
                if (v != par[u]) { // v is child of u
                    f[v] = f[u] + n - 2 * sub[v];
                }
            }
        }

        // Step 4: Answer = max f[v]
        long ans = 0;
        for (int v = 1; v <= n; v++) {
            ans = Math.max(ans, f[v]);
        }

        System.out.println(ans);
    }
}