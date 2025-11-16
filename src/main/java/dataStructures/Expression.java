package dataStructures;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

/**
 * Expression мод
 *  - LinkedBinaryTree-ээс удамшина
 *  - илэрхийллийг prefix / infix / postfix хэлбэрээр:
 *      * мод үүсгэх
 *      * хэвлэх
 *      * бодох
 */
public class Expression extends LinkedBinaryTree {

    public Expression() {
        super();
    }

    /* =========================
       Туслах функцууд
       ========================= */

    // оператор эсэхийг шалгах (энд зөвхөн + - * / гэж үзье)
    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    /* =========================
       1. POSTFIX-ээс мод үүсгэх
       ж: "a b c * + d -"
       ========================= */

    public void buildFromPostfix(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            root = null;
            return;
        }

        Stack<BinaryTreeNode> st = new Stack<>();
        String[] tokens = expr.trim().split("\\s+");

        for (String tok : tokens) {
            if (tok.isEmpty()) continue;

            if (!isOperator(tok)) {
                // operand → ганц навч зангилаа
                st.push(new BinaryTreeNode(tok, null, null));
            } else {
                // operator → хоёр хүүхэдтэй зангилаа (зүүн, баруун)
                BinaryTreeNode right = st.pop();
                BinaryTreeNode left = st.pop();
                st.push(new BinaryTreeNode(tok, left, right));
            }
        }

        root = st.isEmpty() ? null : st.pop();
    }

    /* =========================
       2. PREFIX-ээс мод үүсгэх
       ж: "- + a * b c d"
       ========================= */

    public void buildFromPrefix(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            root = null;
            return;
        }

        String[] tokens = expr.trim().split("\\s+");
        int[] idx = {0};               // current index
        root = buildFromPrefix(tokens, idx);
    }

    // рекурсив туслах
    private BinaryTreeNode buildFromPrefix(String[] tokens, int[] idx) {
        if (idx[0] >= tokens.length) return null;

        String tok = tokens[idx[0]++];
        if (!isOperator(tok)) {
            // operand
            return new BinaryTreeNode(tok, null, null);
        } else {
            // operator → эхлээд зүүн, дараа нь баруун дэд мод
            BinaryTreeNode left = buildFromPrefix(tokens, idx);
            BinaryTreeNode right = buildFromPrefix(tokens, idx);
            return new BinaryTreeNode(tok, left, right);
        }
    }

    /* =========================
       3. INFIX (FULLY PARENTHESIZED)
       илэрхийллээс мод үүсгэх
       ж: "( ( a + b ) * ( c - d ) )"
       ========================= */

    public void buildFromInfix(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            root = null;
            return;
        }

        String[] tokens = expr.trim().split("\\s+");
        int[] idx = {0};
        root = buildFromInfix(tokens, idx);
    }

    // бүрэн хаалттай инфикс гэж үзнэ: ( left op right )
    private BinaryTreeNode buildFromInfix(String[] tokens, int[] idx) {
        String tok = tokens[idx[0]++];

        if ("(".equals(tok)) {
            // ( left op right )
            BinaryTreeNode left = buildFromInfix(tokens, idx);
            String op = tokens[idx[0]++];
            BinaryTreeNode right = buildFromInfix(tokens, idx);

            // хаалт ) -ыг алгасна
            String close = tokens[idx[0]++];
            // close нь ")" байна гэж үзнэ

            return new BinaryTreeNode(op, left, right);
        } else {
            // operand (хаалт биш)
            return new BinaryTreeNode(tok, null, null);
        }
    }

    /* =========================
       4. Хэвлэлүүд
       ========================= */

    // (хаалттай) INFIX хэлбэрээр хэвлэх
    public void printInfix() {
        printInfix(root);
        System.out.println();
    }

    private void printInfix(BinaryTreeNode t) {
        if (t == null) return;

        // навч бол шууд хэвлэнэ
        if (t.leftChild == null && t.rightChild == null) {
            System.out.print(t.element);
            return;
        }

        // бусад үед ( left op right ) хэвлэнэ
        System.out.print("(");
        printInfix(t.leftChild);
        System.out.print(" " + t.element + " ");
        printInfix(t.rightChild);
        System.out.print(")");
    }

    // PREFIX хэлбэрээр хэвлэх
    public void printPrefix() {
        printPrefix(root);
        System.out.println();
    }

    private void printPrefix(BinaryTreeNode t) {
        if (t == null) return;
        System.out.print(t.element + " ");
        printPrefix(t.leftChild);
        printPrefix(t.rightChild);
    }

    // POSTFIX хэлбэрээр хэвлэх
    public void printPostfix() {
        printPostfix(root);
        System.out.println();
    }

    private void printPostfix(BinaryTreeNode t) {
        if (t == null) return;
        printPostfix(t.leftChild);
        printPostfix(t.rightChild);
        System.out.print(t.element + " ");
    }

    /* =========================
       5. Модыг ашиглаж илэрхийллийг бодох
       ========================= */

    public int evaluate() {
        Scanner in = new Scanner(System.in);
        Map<String, Integer> vars = new HashMap<>();
        int result = eval(root, in, vars);
        // in-ийг хааж болно, гэхдээ ихэвчлэн main гарна гэж үзье
        return result;
    }

    // рекурсив үнэлгээ
    private int eval(BinaryTreeNode t, Scanner in, Map<String, Integer> vars) {
        if (t == null) return 0;

        String val = t.element.toString();

        // оператор бол зүүн, баруун дэд модоо бодно
        if (isOperator(val)) {
            int left = eval(t.leftChild, in, vars);
            int right = eval(t.rightChild, in, vars);

            return switch (val) {
                case "+" -> left + right;
                case "-" -> left - right;
                case "*" -> left * right;
                case "/" -> right != 0 ? left / right : 0; // 0-д хуваах хамгаалалт
                default -> 0;
            };
        }

        // operand → тоо эсвэл хувьсагч
        // 1) integer literal эсэхийг шалгана
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // 2) хувьсагч гэж үзээд хэрэглэгчээс асууна
            if (vars.containsKey(val)) {
                return vars.get(val);
            }
            System.out.print("tomyond baigaa " + val + " huwisagciin utgiig oruulna uu: ");
            int x = in.nextInt();
            vars.put(val, x);
            return x;
        }
    }

    /* =========================
       6. Жижиг DEМО main
       ========================= */

    public static void main(String[] args) {
        Expression e = new Expression();

        // Жишээ: postfix-ээс мод үүсгэе
        // (a + b) * (c - d) гэсэн илэрхийллийн postfix хэлбэр:
        // a b + c d - *
        e.buildFromPostfix("a b + c d - *");

        System.out.print("Infix  : ");
        e.printInfix();      // ((a + b) * (c - d))

        System.out.print("Prefix : ");
        e.printPrefix();     // * + a b - c d

        System.out.print("Postfix: ");
        e.printPostfix();    // a b + c d - *

        int result = e.evaluate();
        System.out.println("Result = " + result);

        // Жишээ: prefix-ээс мод үүсгэх
        Expression e2 = new Expression();
        e2.buildFromPrefix("* + a b - c d");

        System.out.print("\n[e2] Infix  : ");
        e2.printInfix();
        System.out.print("[e2] Prefix : ");
        e2.printPrefix();
        System.out.print("[e2] Postfix: ");
        e2.printPostfix();

        // Жишээ: fully parenthesized infix-ээс
        Expression e3 = new Expression();
        e3.buildFromInfix("( ( a + b ) * ( c - d ) )");
        System.out.print("\n[e3] Infix  : ");
        e3.printInfix();
        System.out.print("[e3] Prefix : ");
        e3.printPrefix();
        System.out.print("[e3] Postfix: ");
        e3.printPostfix();
    }
}
