package dataStructures;

import java.util.EmptyStackException;
import java.util.Scanner;

public class MyStack implements Stack {
    private Object[] elements;
    private int top;
    private int capacity;

    public MyStack(int size) {
        capacity = (size <= 0) ? 16 : size;
        elements = new Object[capacity];
        top = -1;
    }

    @Override
    public boolean empty() { return top == -1; }

    @Override
    public Object peek() {
        if (empty()) throw new EmptyStackException();
        return elements[top];
    }

    @Override
    public void push(Object theObject) {
        if (top == capacity - 1) {
            int newCap = Math.max(1, capacity * 2);
            Object[] a = new Object[newCap];
            System.arraycopy(elements, 0, a, 0, capacity);
            elements = a;
            capacity = newCap;
        }
        elements[++top] = theObject;
    }

    @Override
    public Object pop() {
        if (empty()) throw new EmptyStackException();
        Object obj = elements[top];
        elements[top--] = null;
        return obj;
    }

    public int size() {
        MyStack temp = new MyStack(capacity);
        int count = 0;
        while (!empty()) { temp.push(pop()); count++; }
        while (!temp.empty()) { push(temp.pop()); }
        return count;
    }

    public void printStack() {
        if (empty()) {
            System.out.println("Stack empty.");
            return;
        }
        MyStack temp = new MyStack(capacity);
        while (!empty()) temp.push(pop());
        System.out.print("Stack (bottom→top): ");
        while (!temp.empty()) {
            Object x = temp.pop();
            System.out.print(x + " ");
            push(x);
        }
        System.out.println();
    }

    public MyStack splitStack() {
        int total = size();
        int half = total / 2;
        MyStack temp = new MyStack(capacity);
        MyStack second = new MyStack(capacity);
        while (!empty()) temp.push(pop());
        for (int i = 0; i < half && !temp.empty(); i++) push(temp.pop());
        while (!temp.empty()) second.push(temp.pop());
        System.out.println("After split:");
        System.out.print("Stack 1 → ");
        printStack();
        System.out.print("Stack 2 → ");
        second.printStack();
        return second;
    }

    public static MyStack combineStack(MyStack s1, MyStack s2) {
        int n1 = s1.size(), n2 = s2.size();
        MyStack t1 = new MyStack(n1), t2 = new MyStack(n2);
        MyStack res = new MyStack(n1 + n2);
        for (int i = 0; i < n1; i++) t1.push(s1.pop());
        for (int i = 0; i < n1; i++) { Object x = t1.pop(); s1.push(x); res.push(x); }
        for (int i = 0; i < n2; i++) t2.push(s2.pop());
        for (int i = 0; i < n2; i++) { Object x = t2.pop(); s2.push(x); res.push(x); }
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MyStack stack = new MyStack(100);
        MyStack second = null;

        while (true) {
            System.out.println("\n=== STACK MENU ===");
            System.out.println("1. Input stack");
            System.out.println("2. Print stack");
            System.out.println("3. Peek top element");
            System.out.println("4. Pop element");
            System.out.println("5. Split stack");
            System.out.println("6. Combine stacks (choose order)");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int ch = safeInt(sc);

            try {
                switch (ch) {
                    case 1 -> {
                        System.out.print("How many elements: ");
                        int n = safeInt(sc);
                        for (int i = 0; i < n; i++) {
                            System.out.print("Element " + (i + 1) + ": ");
                            stack.push(sc.next());
                        }
                    }
                    case 2 -> stack.printStack();
                    case 3 -> {
                        if (stack.empty()) System.out.println("Stack empty.");
                        else System.out.println("Top element: " + stack.peek());
                    }
                    case 4 -> {
                        if (stack.empty()) System.out.println("Stack empty.");
                        else System.out.println("Popped: " + stack.pop());
                    }
                    case 5 -> {
                        if (stack.empty()) { System.out.println("Stack empty."); break; }
                        second = stack.splitStack();
                    }
                    case 6 -> {
                        if (second == null) { System.out.println("Please split the stack first!"); break; }
                        System.out.println("Combine order:");
                        System.out.println("1) Stack1 + Stack2");
                        System.out.println("2) Stack2 + Stack1");
                        System.out.print("Select (1 or 2): ");
                        int order = safeInt(sc);
                        MyStack merged = (order == 2) ? combineStack(second, stack) : combineStack(stack, second);
                        stack = merged;
                        second = null;
                        System.out.println("Combined stack:");
                        stack.printStack();
                    }
                    case 0 -> System.exit(0);
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error: " + e.getMessage());
            }
        }
    }

    private static int safeInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a numeric value: ");
            sc.next();
        }
        return sc.nextInt();
    }
}
