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

    // gyo
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

    // (хаалттай) INFIX хэлбэрээр хэвлэх
    public void printInfix() {
        printInfix(root);
        System.out.println();
    }

    private void printInfix(BinaryTreeNode t) {
        if (t == null) return;

        
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

    // Хуучин хувилбар — ганцаараа дуудахад хэрэглэж болно
    public int evaluate() {
        Scanner in = new Scanner(System.in);
        Map<String, Integer> vars = new HashMap<>();
        return eval(root, in, vars);
    }

    // ШИНЭ хувилбар — гаднаас Scanner, vars авч ажиллана
    // Ингэснээр олон Expression хооронд хувьсагчийн утгаа share хийнэ
    public int evaluate(Scanner in, Map<String, Integer> vars) {
        return eval(root, in, vars);
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
        try {
            // тоон literal байвал шууд parse
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // хувьсагч бол map-с хайна, байхгүй бол асууна
            if (vars.containsKey(val)) {
                return vars.get(val);
            }
            System.out.print("tomyoond baigaa " + val + " huvisagchiin utgiig oruulna uu: ");
            int x = in.nextInt();
            vars.put(val, x);
            return x;
        }
    }

    public static void main(String[] args) {
        // (a + b) * (c - d) илэрхийллийн 3 хэлбэр
        String postfixExpr = "a b + c d - *";
        String prefixExpr  = "* + a b - c d";
        String infixExpr   = "( ( a + b ) * ( c - d ) )";

        // 1) Постфиксоос мод үүсгэх
        Expression ePost = new Expression();
        ePost.buildFromPostfix(postfixExpr);

        // 2) Префиксоос мод үүсгэх
        Expression ePre = new Expression();
        ePre.buildFromPrefix(prefixExpr);

        // 3) Инфиксоос (бүрэн хаалттай) мод үүсгэх
        Expression eInf = new Expression();
        eInf.buildFromInfix(infixExpr);

        System.out.println("=== ILERHIILELIIN 3 helber ===");
        System.out.print("Postfix ilerhiilel: ");
        System.out.println(postfixExpr);
        System.out.print("Prefix  ilerhiilel: ");
        System.out.println(prefixExpr);
        System.out.print("Infix   ilerhiilel: ");
        System.out.println(infixExpr);

        // --------------------------
        // Odoo bodoh kheseg
        // --------------------------
        Scanner in = new Scanner(System.in);
        Map<String, Integer> sharedVars = new HashMap<>();

        System.out.println();
        System.out.println("=== a, b, c, d (huvisagchdiin utgiig neg udaа oruulna) ===");

        // ehleed postfix huvilbaryg bodood a,b,c,d asuuj avna
        int vPost = ePost.evaluate(in, sharedVars);

        // daraagii 2 ni map-soo utgaa shuud avna, dahin asuuhgui
        int vPre  = ePre.evaluate(in, sharedVars);
        int vInf  = eInf.evaluate(in, sharedVars);

        System.out.println();
        System.out.println("=== UR DUNGUUD ===");
        System.out.println("Postfix result = " + vPost);
        System.out.println("Prefix result = " + vPre);
        System.out.println("Infix result = " + vInf);

        System.out.println();
        System.out.println("=== TEST ===");
        if (vPost == vPre && vPre == vInf) {
            System.out.println("3 uun helber ni hariu adil tul zuw baina");
        } else {
            System.out.println("3 helberiin ali neg ni buruu baina. Tomyoonuudiig haritsuulaar daxin shalga.");
        }
    }
}
