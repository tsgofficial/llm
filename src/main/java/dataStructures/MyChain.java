package dataStructures;
import java.util.*;

public class MyChain extends Chain {

    public MyChain() { super(); }
    public MyChain(final int initialCapacity) { super(initialCapacity); }

    public Object[] toArray() {
        final Object[] arr = new Object[size];
        ChainNode cur = firstNode;
        int i = 0;
        while (cur != null) { arr[i++] = cur.element; cur = cur.next; }
        return arr;
    }

    public void addRange(final Object[] elements) {
        if (elements == null)
            throw new IllegalArgumentException("elements must not be null");

        ChainNode tail = firstNode;
        if (tail != null) while (tail.next != null) tail = tail.next;

        for (final Object e : elements) {
            final ChainNode node = new ChainNode(e, null);
            if (firstNode == null) { firstNode = node; tail = node; }
            else { tail.next = node; tail = node; }
            size++;
        }
    }

    public MyChain union(final MyChain other) {
        if (other == null) throw new IllegalArgumentException("chain must not be null");
        final MyChain res = new MyChain();

        ChainNode c = this.firstNode;
        while (c != null) {
            if (!res.containsElement(c.element)) res.add(res.size(), c.element);
            c = c.next;
        }
        c = other.firstNode;
        while (c != null) {
            if (!res.containsElement(c.element)) res.add(res.size(), c.element);
            c = c.next;
        }
        return res;
    }

    public MyChain intersection(final MyChain other) {
        if (other == null) throw new IllegalArgumentException("chain must not be null");
        final MyChain res = new MyChain();

        ChainNode c = this.firstNode;
        while (c != null) {
            final Object e = c.element;
            if (other.containsElement(e) && !res.containsElement(e))
                res.add(res.size(), e);
            c = c.next;
        }
        return res;
    }

    private boolean containsElement(final Object e) {
        ChainNode cur = firstNode;
        while (cur != null) {
            if (e == null ? cur.element == null : e.equals(cur.element)) return true;
            cur = cur.next;
        }
        return false;
    }

    private static Object[] parseCsvLine(String line) {
        if (line == null) return new Object[0];
        line = line.trim();
        if (line.isEmpty()) return new Object[0];
        String[] parts = line.split(",");
        Object[] out = new Object[parts.length];
        for (int i = 0; i < parts.length; i++) out[i] = parseValue(parts[i].trim());
        return out;
    }

    private static Object parseValue(final String s) {
        try { return Integer.valueOf(s); } catch (Exception ignore) {}
        try { return Double.valueOf(s); } catch (Exception ignore) {}
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\""))
            return s.substring(1, s.length() - 1);
        return s;
    }

    public static void main(final String[] args) {
        final Scanner sc = new Scanner(System.in);
        final MyChain A = new MyChain();
        final MyChain B = new MyChain();
        MyChain cur = A;

        printBanner();

        while (true) {
            System.out.print("\n" + prompt(cur, A, B));
            final String line = sc.hasNextLine() ? sc.nextLine().trim() : "";
            if (line.isEmpty()) continue;

            final String[] parts = line.split("\\s+", 2);
            final String cmd = parts[0].toLowerCase(Locale.ROOT);
            final String rest = (parts.length > 1) ? parts[1] : "";

            try {
                switch (cmd) {
                    case "use": {
                        final String which = rest.trim().toUpperCase(Locale.ROOT);
                        if ("A".equals(which)) cur = A;
                        else if ("B".equals(which)) cur = B;
                        else { System.out.println("Usage: use A | use B"); break; }
                        System.out.println("Now current = " + which);
                        break;
                    }
                    case "range": {
                        if (rest.isEmpty()) { System.out.println("Usage: range x,y,..."); break; }
                        cur.addRange(parseCsvLine(rest));
                        System.out.println("Appended.");
                        break;
                    }
                    case "toarray": {
                        System.out.println(Arrays.toString(cur.toArray()));
                        break;
                    }
                    case "print": {
                        System.out.println("A = " + A);
                        System.out.println("B = " + B);
                        break;
                    }
                    case "union": {
                        System.out.println("Union(A,B) = " + A.union(B));
                        break;
                    }
                    case "intersect":
                    case "intersection": {
                        System.out.println("Intersection(A,B) = " + A.intersection(B));
                        break;
                    }
                    case "clear": {
                        while (cur.size() > 0) cur.remove(0);
                        System.out.println("Cleared.");
                        break;
                    }
                    case "help":
                    case "h": {
                        printBanner();
                        break;
                    }
                    case "exit":
                    case "quit":
                    case "0": {
                        return;
                    }
                    default: {
                        System.out.println("Unknown command. Type: help");
                    }
                }
            } catch (final Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static String prompt(MyChain cur, MyChain A, MyChain B) {
        return (cur == A ? "A" : "B") + "(size=" + cur.size() + ")> ";
    }

    private static void printBanner() {
        System.out.println("MyChain demo — minimal commands:");
        System.out.println("  use A|B             set current list");
        System.out.println("  range x,y,...       append comma-separated values to current");
        System.out.println("  toArray             print current list as array");
        System.out.println("  print               print A and B");
        System.out.println("  union               print unique Union(A,B)");
        System.out.println("  intersect           print unique Intersection(A,B)");
        System.out.println("  clear               clear current list");
        System.out.println("  help | h            show this help");
        System.out.println("  exit | quit | 0     quit");
    }
}
