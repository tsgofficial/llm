import dataStructures.ArrayLinearList;
import java.util.Scanner;

class MyArrayLinearList extends ArrayLinearList {
    public MyArrayLinearList() { super(); }

    public void insert(int index, int value) { add(index, Integer.valueOf(value)); }

    public void removeValue(int value) {
        int idx = indexOf(Integer.valueOf(value));
        if (idx != -1) remove(idx);
        else System.out.println("Value not found.");
    }

    public int max() {
        if (size == 0) throw new IllegalStateException("List is empty");
        int max = (Integer) element[0];
        for (int i = 1; i < size; i++) {
            int v = (Integer) element[i];
            if (v > max) max = v;
        }
        return max;
    }

    public int min() {
        if (size == 0) throw new IllegalStateException("List is empty");
        int min = (Integer) element[0];
        for (int i = 1; i < size; i++) {
            int v = (Integer) element[i];
            if (v < min) min = v;
        }
        return min;
    }

    public int sum() {
        int s = 0;
        for (int i = 0; i < size; i++) s += (Integer) element[i];
        return s;
    }

    public double average() {
        if (size == 0) throw new IllegalStateException("List is empty");
        return (double) sum() / size;
    }

    public void removeOdd() {
        for (int i = 0; i < size; ) {
            if (((Integer) element[i]) % 2 != 0) remove(i);
            else i++;
        }
    }

    public void sort() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if ((Integer) element[j] > (Integer) element[j + 1]) {
                    Object tmp = element[j];
                    element[j] = element[j + 1];
                    element[j + 1] = tmp;
                }
            }
        }
    }

    public static void main(String[] args) {
        MyArrayLinearList list = new MyArrayLinearList();
        Scanner sc = new Scanner(System.in);   // <-- хаахгүй, зөвхөн exit дээр хаана
        while (true) {
            System.out.println("\nChoose operation:");
            System.out.println("1. Insert value at index");
            System.out.println("2. Remove value");
            System.out.println("3. Max");
            System.out.println("4. Min");
            System.out.println("5. Sum");
            System.out.println("6. Average");
            System.out.println("7. Remove odd values");
            System.out.println("8. Sort");
            System.out.println("9. Print list");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) { System.out.println("Invalid input."); continue; }
                choice = Integer.parseInt(line);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Enter index: ");
                        int idx = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Enter value: ");
                        int val = Integer.parseInt(sc.nextLine().trim());
                        list.insert(idx, val);  // IndexOutOfBounds бол доорх catch-д орно
                        System.out.println("Inserted.");
                        break;
                    }
                    case 2: {
                        System.out.print("Enter value to remove: ");
                        int rem = Integer.parseInt(sc.nextLine().trim());
                        list.removeValue(rem);
                        break;
                    }
                    case 3: System.out.println("Max: " + list.max()); break;
                    case 4: System.out.println("Min: " + list.min()); break;
                    case 5: System.out.println("Sum: " + list.sum()); break;
                    case 6: System.out.println("Average: " + list.average()); break;
                    case 7: list.removeOdd(); System.out.println("Odd values removed."); break;
                    case 8: list.sort(); System.out.println("List sorted."); break;
                    case 9: System.out.println("List: " + list); break;
                    case 0:
                        System.out.println("Exiting.");
                        sc.close();            // <-- зөвхөн энд хаана
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                // АНХААР: энд Scanner-аа БҮҮ хаа
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
