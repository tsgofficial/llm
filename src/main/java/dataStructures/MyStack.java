package dataStructures;
import java.util.*;

public class MyStack implements Stack {
    private Object[] elements;
    private int top;
    private int capacity;

    // ----- Constructor -----
    public MyStack(int size) {
        capacity = size;
        elements = new Object[capacity];
        top = -1;
    }

    // ----- 1. Stack интерфейсийн аргууд -----
    @Override
    public boolean empty() {
        return top == -1;
    }

    @Override
    public Object peek() {
        if (empty()) throw new EmptyStackException();
        return elements[top];
    }

    @Override
    public void push(Object theObject) {
        if (top == capacity - 1) throw new StackOverflowError("stack full!");
        elements[++top] = theObject;
    }

    @Override
    public Object pop() {
        if (empty()) throw new EmptyStackException();
        Object obj = elements[top];
        elements[top--] = null;
        return obj;
    }

    // ----- 2. Өргөтгөсөн аргууд -----

    // (a) size() – стекийн хэмжээ
    public int size() {
        return top + 1;
    }

    // (b) inputStack() – хэрэглэгчээс элементүүдийг оруулах
    public void inputStack() {
        Scanner sc = new Scanner(System.in);
        System.out.print("How many elements do you want to enter? ");
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.print("Enter element " + (i + 1) + ": ");
            push(sc.next());
        }
    }

    // (c) printStack() – элементүүдийг хэвлэх
    public void printStack() {
        if (empty()) {
            System.out.println("Stack is empty.");
            return;
        }
        System.out.print("Stack elements (from bottom to top): ");
        for (int i = 0; i <= top; i++) {
            System.out.print(elements[i] + " ");
        }
        System.out.println();
    }

    // (d) splitStack() – Стекийг хоёр болгож хуваах
    public MyStack splitStack() {
        int half = size() / 2;
        MyStack second = new MyStack(capacity);
        for (int i = half; i < size(); i++) {
            second.push(elements[i]);
        }
        top = half - 1; // эхний стекийн дээд хязгаарыг багасгана
        return second;
    }

    // (e) combineStack() – хоёр стекийн элементүүдийг нэгтгэх
    public static MyStack combineStack(MyStack s1, MyStack s2) {
        MyStack result = new MyStack(s1.size() + s2.size());
        // эхний стек
        for (int i = 0; i <= s1.top; i++) result.push(s1.elements[i]);
        // дараагийн стек
        for (int i = 0; i <= s2.top; i++) result.push(s2.elements[i]);
        return result;
    }

    // ----- Турших үндсэн функц -----
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MyStack stack = new MyStack(20);

        while (true) {
            System.out.println("\n=== STACK MENU ===");
            System.out.println("1. Enter elements");
            System.out.println("2. Print stack");
            System.out.println("3. Peek last element");
            System.out.println("4. Pop element");
            System.out.println("5. Split stack");
            System.out.println("6. Combine example");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt();

            try {
                switch (ch) {
                    case 1 -> stack.inputStack();
                    case 2 -> stack.printStack();
                    case 3 -> System.out.println("Top element: " + stack.peek());
                    case 4 -> System.out.println("Popped element: " + stack.pop());
                    case 5 -> {
                        MyStack second = stack.splitStack();
                        System.out.println("Stack 1:");
                        stack.printStack();
                        System.out.println("Stack 2:");
                        second.printStack();
                    }
                    case 6 -> {
                        MyStack s1 = new MyStack(5);
                        s1.push("A"); s1.push("B");
                        MyStack s2 = new MyStack(5);
                        s2.push("C"); s2.push("D");
                        MyStack merged = combineStack(s1, s2);
                        System.out.println("Combined stack:");
                        merged.printStack();
                    }
                    case 0 -> System.exit(0);
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error: " + e.getMessage());
            }
        }
    }
}
