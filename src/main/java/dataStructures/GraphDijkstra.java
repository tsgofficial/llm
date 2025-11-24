package dataStructures;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class GraphDijkstra extends JFrame {

    // Графыг adjacency matrix хэлбэрээр хадгална
    // 0 = холбогдоогүй, >0 = жин
    private final int[][] graph = {
            {0, 4, 0, 0, 0, 0},
            {4, 0, 8, 0, 0, 0},
            {0, 8, 0, 7, 0, 4},
            {0, 0, 7, 0, 9,14},
            {0, 0, 0, 9, 0,10},
            {0, 0, 4,14,10, 0}
    };

    private JTextArea resultArea;
    private JComboBox<Integer> startCombo;
    private GraphPanel graphPanel;   // граф зурах тусгай panel
    private JButton runButton;       // товчийг disable хийхэд ашиглана

    public GraphDijkstra() {
        setTitle("Dijkstra – Богино замын бодлого");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // дэлгэцийн голд

        initUI();
    }

    private void initUI() {
        // Үндсэн layout
        setLayout(new BorderLayout(10, 10));

        // === Дээд хэсэг: хяналтын панел (эхлэх орой сонгох + товч) ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel startLabel = new JLabel("Эхлэх орой:");
        startCombo = new JComboBox<>();

        int n = graph.length;
        for (int i = 0; i < n; i++) {
            startCombo.addItem(i); // 0,1,2,... хэлбэрээр
        }

        runButton = new JButton("Тооцоолох");
        runButton.addActionListener(e -> runDijkstraAnimated());

        topPanel.add(startLabel);
        topPanel.add(startCombo);
        topPanel.add(runButton);

        add(topPanel, BorderLayout.NORTH);

        // === Баруун тал: үр дүнгийн текст ===
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Текстэн үр дүн"));

        // === Зүүн тал: граф зурах panel ===
        graphPanel = new GraphPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder("Графын дүрслэл"));

        // === Гол хэсэг: SplitPane-аар хоёр хэсэгт хуваана ===
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, scroll);
        split.setResizeWeight(0.6); // default-оор зургууд том талд
        add(split, BorderLayout.CENTER);

        // === Доод хэсэг: adjacency matrix харуулах ===
        JTextArea matrixArea = new JTextArea();
        matrixArea.setEditable(false);
        matrixArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        matrixArea.setText(buildMatrixText());
        matrixArea.setBorder(BorderFactory.createTitledBorder("Графын adjacency matrix"));
        matrixArea.setBackground(new Color(245, 245, 245));

        add(matrixArea, BorderLayout.SOUTH);
    }

    // Графын adjacency matrix-ийг текст хэлбэрээр буцаана
    private String buildMatrixText() {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int i = 0; i < graph.length; i++) {
            sb.append(String.format("%4d", i));
        }
        sb.append("\n");

        for (int i = 0; i < graph.length; i++) {
            sb.append(String.format("%2d:", i));
            for (int j = 0; j < graph.length; j++) {
                sb.append(String.format("%4d", graph[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // === Dijkstra-г анимацтайгаар ажиллуулах ===
    private void runDijkstraAnimated() {
        int start = (int) startCombo.getSelectedItem();
        runButton.setEnabled(false);
        resultArea.setText("Dijkstra алгоритм ажиллаж байна...\n");

        // Алгоритмыг тусдаа thread дээр ажиллуулна (GUI-г блоклохгүй)
        new Thread(() -> {
            DijkstraResult res = dijkstraWithAnimation(graph, start);

            // Дууссаны дараа текстэн үр дүнг гаргах
            SwingUtilities.invokeLater(() -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Эхлэх орой: ").append(start).append("\n");
                sb.append("Орой бүр рүү очих хамгийн богино зам:\n\n");

                for (int v = 0; v < graph.length; v++) {
                    if (res.dist[v] == Integer.MAX_VALUE) {
                        sb.append(String.format("%d -> %d : зам байхгүй\n", start, v));
                    } else {
                        String path = buildPath(res.prev, start, v);
                        sb.append(String.format(
                                "%d -> %d : зай = %d, зам = %s\n",
                                start, v, res.dist[v], path
                        ));
                    }
                }

                resultArea.setText(sb.toString());
                graphPanel.setResult(start, res.dist, res.prev);
                runButton.setEnabled(true);
            });
        }).start();
    }

    // prev массив дээр тулгуурлан path-ийг сэргээх
    private String buildPath(int[] prev, int start, int v) {
        if (start == v) {
            return String.valueOf(start);
        }

        int cur = v;
        java.util.List<Integer> path = new java.util.ArrayList<>();

        while (cur != -1) {
            path.add(0, cur);   // жагсаалтын эхэнд нэмнэ (reverse хэрэггүй)
            cur = prev[cur];
        }

        if (path.isEmpty() || path.get(0) != start) {
            return "зам байхгүй";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(path.get(i));
        }
        return sb.toString();
    }

    // === Dijkstra алгоритм + анимац ===
    private DijkstraResult dijkstraWithAnimation(int[][] graph, int start) {
        int n = graph.length;

        int[] dist = new int[n];      // start -> i хүртэлх хамгийн бага зай
        boolean[] used = new boolean[n]; // shortest path батлагдсан эсэх
        int[] prev = new int[n];      // path сэргээхэд ашиглах (өмнөх орой)

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        // Эхлэл frame: зөвхөн start node-г highlight хийнэ
        updateAnimationFrame(start, dist, prev, used, start);

        for (int step = 0; step < n; step++) {
            int u = -1;
            int best = Integer.MAX_VALUE;

            // 1) Одоогоор батлагдаагүй, dist хамгийн бага оройг олно
            for (int i = 0; i < n; i++) {
                if (!used[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }
            }

            if (u == -1) break; // үлдсэн хэсэгт хүрэх боломжгүй

            // Одоогийн сонгогдсон u оройг шарлаж харуулах
            updateAnimationFrame(start, dist, prev, used, u);
            sleep(700); // жаахан хоцролт -> шагнал шиг

            used[u] = true; // u орой батлагдлаа (ногоон болгоно)

            // u батлагдсаны дараах frame
            updateAnimationFrame(start, dist, prev, used, u);
            sleep(700);

            // 2) u-аас хүрэх хөршүүдийг шинэчилнэ
            for (int v = 0; v < n; v++) {
                if (graph[u][v] > 0) { // 0 бол холбоогүй
                    int newDist = dist[u] + graph[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u; // v-ийн өмнөх нь u гэдгийг тэмдэглэнэ

                        // Edge relax хийсэн бол бас нэг frame
                        updateAnimationFrame(start, dist, prev, used, v);
                        sleep(500);
                    }
                }
            }
        }

        // Эцсийн байдал
        updateAnimationFrame(start, dist, prev, used, -1);

        return new DijkstraResult(dist, prev);
    }

    // Animation frame update helper
    private void updateAnimationFrame(int start, int[] dist, int[] prev, boolean[] used, int current) {
        int[] dCopy = dist.clone();
        int[] pCopy = prev.clone();
        boolean[] uCopy = used.clone();

        SwingUtilities.invokeLater(() ->
                graphPanel.setAnimatedState(start, dCopy, pCopy, uCopy, current)
        );
    }

    // Thread.sleep-ийг try/catch-гүйгээр цэвэрхэн хэрэглэхын тулд
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    // dist + prev-ийг хамтад нь хадгалах дотоод класс
    private static class DijkstraResult {
        int[] dist;
        int[] prev;

        DijkstraResult(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }

    // === Графыг зурах дотоод класс ===
    private class GraphPanel extends JPanel {

        // Орой тус бүрийн координат (фиксэн байрлал)
        private final Point[] nodePos;

        private int[] dist;
        private int[] prev;
        private boolean[] used;
        private boolean hasResult = false;
        private int startNode = 0;
        private int currentNode = -1; // одоогоор шалгаж байгаа node

        public GraphPanel() {
            // 6 оройг зургаан өнцөгт маягаар байршуулав
            nodePos = new Point[graph.length];
            nodePos[0] = new Point(120, 120);
            nodePos[1] = new Point(280, 70);
            nodePos[2] = new Point(440, 120);
            nodePos[3] = new Point(440, 280);
            nodePos[4] = new Point(280, 340);
            nodePos[5] = new Point(120, 280);
        }

        public void setAnimatedState(int start, int[] dist, int[] prev, boolean[] used, int current) {
            this.startNode = start;
            this.dist = dist;
            this.prev = prev;
            this.used = used;
            this.currentNode = current;
            this.hasResult = true;
            repaint();
        }

        public void setResult(int start, int[] dist, int[] prev) {
            // эцсийн байдлаар дуудаж болно
            this.startNode = start;
            this.dist = dist;
            this.prev = prev;
            this.currentNode = -1;
            this.hasResult = true;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(550, 400);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Фон
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Эхлээд бүх ирмэгүүдийг (edge) зурах
            drawAllEdges(g2);

            // Хэрэв Dijkstra-н үр дүн байгаа бол shortest path tree-г highlight хийх
            if (hasResult && prev != null) {
                drawShortestTree(g2);
            }

            // Эцэст нь оройнуудыг зурна
            drawNodes(g2);

            g2.dispose();
        }

        private void drawAllEdges(Graphics2D g2) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(180, 180, 180));

            for (int i = 0; i < graph.length; i++) {
                for (int j = i + 1; j < graph.length; j++) {
                    if (graph[i][j] > 0) {
                        Point p1 = nodePos[i];
                        Point p2 = nodePos[j];

                        // Шугам
                        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));

                        // Жинг нь гол хэсэгт нь бичнэ
                        int mx = (p1.x + p2.x) / 2;
                        int my = (p1.y + p2.y) / 2;

                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                        g2.drawString(String.valueOf(graph[i][j]), mx - 5, my - 5);

                        g2.setColor(new Color(180, 180, 180)); // дараагийн шугамд буцааж тавина
                    }
                }
            }
        }

        private void drawShortestTree(Graphics2D g2) {
            g2.setStroke(new BasicStroke(4f));
            g2.setColor(new Color(255, 100, 100)); // тод улаан туяатай

            if (prev == null) return;

            for (int v = 0; v < prev.length; v++) {
                int u = prev[v];
                if (u != -1) {
                    Point p1 = nodePos[u];
                    Point p2 = nodePos[v];
                    g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                }
            }
        }

        private void drawNodes(Graphics2D g2) {
            int radius = 28;

            for (int i = 0; i < nodePos.length; i++) {
                Point p = nodePos[i];

                // Өнгөний логик:
                // current node -> шар, батлагдсан node (used) -> ногоон,
                // эхлэх орой -> цэнхэр туяатай, бусад -> цайвар
                if (i == currentNode) {
                    g2.setColor(new Color(255, 230, 150)); // одоо шалгаж буй node
                } else if (used != null && used.length > i && used[i]) {
                    g2.setColor(new Color(180, 255, 180)); // finalized node
                } else if (i == startNode) {
                    g2.setColor(new Color(150, 200, 255)); // эхлэх орой
                } else {
                    g2.setColor(new Color(230, 230, 250));
                }

                g2.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);

                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(p.x - radius, p.y - radius, radius * 2, radius * 2);

                // Оройн index
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                String label = String.valueOf(i);
                FontMetrics fm = g2.getFontMetrics();
                int tx = p.x - fm.stringWidth(label) / 2;
                int ty = p.y + fm.getAscent() / 2 - 3;
                g2.drawString(label, tx, ty);

                // dist байвал доор нь зайг нь бичнэ
                if (hasResult && dist != null && dist.length > i && dist[i] != Integer.MAX_VALUE) {
                    String dLabel = "d=" + dist[i];
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    int dx = p.x - g2.getFontMetrics().stringWidth(dLabel) / 2;
                    int dy = p.y + radius + 15;
                    g2.drawString(dLabel, dx, dy);
                }
            }
        }
    }

    // === Program эхлэх цэг ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphDijkstra frame = new GraphDijkstra();
            frame.setVisible(true);
        });
    }
}
