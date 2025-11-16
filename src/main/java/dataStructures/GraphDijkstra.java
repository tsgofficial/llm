package dataStructures;

import java.util.*;

public class GraphDijkstra {

    // Dijkstra algorithm: эх зангилаанаас бусад руу хамгийн богино зам олно.
    public static void dijkstra(int[][] graph, int start) {

        int n = graph.length;

        int[] dist = new int[n];      // dist[i] = start → i хүртэлх хамгийн бага зай
        boolean[] used = new boolean[n]; // тухайн орой shortest path нь батлагдсан эсэх

        // Эхний утгууд
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        // Гол цикл
        for (int step = 0; step < n; step++) {

            // 1) Одоогоор батлагдаагүй, хамгийн бага dist-тэй оройг олно
            int u = -1;
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!used[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }
            }

            if (u == -1) break;  // цааш зам байхгүй

            used[u] = true; // u орой батлагдлаа

            // 2) u-аас хүрэх боломжтой хөршүүдийг шинэчилнэ
            for (int v = 0; v < n; v++) {
                if (graph[u][v] > 0) { // 0 гэдэг нь холбогдоогүй гэсэн үг
                    int newDist = dist[u] + graph[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                    }
                }
            }
        }

        // Үр дүнг хэвлэх
        System.out.println("ehleh oroi = " + start);
        for (int i = 0; i < n; i++) {
            System.out.println(" -> " + start + " -> " + i + " hamgiin baga zai = " + dist[i]);
        }
    }

    public static void main(String[] args) {

    
        int[][] graph = {
            {0, 4, 0, 0, 0, 0},
            {4, 0, 8, 0, 0, 0},
            {0, 8, 0, 7, 0, 4},
            {0, 0, 7, 0, 9,14},
            {0, 0, 0, 9, 0,10},
            {0, 0, 4,14,10, 0}
        };

        dijkstra(graph, 0);  // орой 0-оос эхлэнэ
    }
}
