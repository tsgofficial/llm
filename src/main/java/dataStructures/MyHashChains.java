package dataStructures;

import java.util.Scanner;

public class MyHashChains extends HashChains {

    public MyHashChains(int theDivisor) {
        // HashChains доторх хүснэгтийн хэмжээг (divisor) тохируулж өгнө
        super(theDivisor);
    }

    /**
     * Өгөгдсөн key-тэй бичлэг байвал зөвхөн element (value)-ийг нь шинэчилнэ.
     * Жишээ: [key][oldValue] → [key][newValue]
     * Олдсон бол хуучин element-ээ буцаана, олдохгүй бол null.
     */
    public Object updateElement(Object theKey, Object theElement) {
        // Тухайн key байгаа эсэхийг шалгана
        Object old = this.get(theKey);
        if (old == null) {
            // ийм key-тай бичлэг алга
            return null;
        }

        // Байгаа бичлэгийн element (value)-ийг шинэчилнэ
        // put() нь тухайн key өмнө нь байвал value-г нь overwrite хийнэ
        this.put(theKey, theElement);

        // Хуучин value-г нь буцаана
        return old;
    }

    /**
     * Хуучин key (theKey)-ийг шинэ key (theNewKey)-ээр солино.
     * Жишээ:
     *   [oldKey][element] → [newKey][same element]
     *
     * Олдсон бол element-ийг буцаана, олдохгүй бол null.
     */
    public Object updateKey(Object theKey, Object theNewKey) {
        // 1. Хуучин key-аар нь element-ийг нь олно
        Object element = this.get(theKey);
        if (element == null) {
            // ийм key-тай бичлэг олдсонгүй
            return null;
        }

        // 2. Хуучин key-тай бичлэгийг устгана (key + element хоёул хамт)
        this.remove(theKey);

        // 3. Шинэ key + хуучин element хосоор нь дахин хадгална
        this.put(theNewKey, element);

        // 4. Олдсон element-ээ буцаана
        return element;
    }

    /**
     * Түлхүүрээр нь хайгаад тухайн бичлэгийг бүтнээр нь устгана.
     * [key][element] хоёул алга болно.
     * Олдохгүй бол юу ч хийхгүй.
     */
    public void delete(Object theKey) {
        // remove() нь key байвал устгаад, олдсон element-ээ буцаана,
        // байхгүй бол null буцаагаад л зогсоно.
        this.remove(theKey);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // ---- Hash table-ийн хэмжээг авах ----
        System.out.print("Hash table size (divisor): ");
        int m = in.nextInt();

        MyHashChains h = new MyHashChains(m);

        // ---- Эхний өгөгдлүүд (optional) ----
        System.out.print("Initial (key, element) pairs count: ");
        int n = in.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- " + (i + 1) + "-th entry ---");
            System.out.print("key = ");
            int key = in.nextInt();

            System.out.print("element = ");
            int element = in.nextInt();

            h.put(key, element);
        }

        System.out.println("\n=== Initial table ===");
        h.output();

        // ---- Меню loop ----
        while (true) {
            System.out.println("\n================ MENU ================");
            System.out.println("1. Insert (put) new (key, element)");
            System.out.println("2. Update element by key");
            System.out.println("3. Update key (move element to new key)");
            System.out.println("4. Delete by key");
            System.out.println("5. Print hash table");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int choice = in.nextInt();
            System.out.println();

            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            }

            switch (choice) {
                case 1: {
                    // Insert
                    System.out.print("key = ");
                    int key = in.nextInt();
                    System.out.print("element = ");
                    int element = in.nextInt();

                    h.put(key, element);
                    System.out.println("Inserted: [" + key + "][" + element + "]");
                    h.output();
                    break;
                }

                case 2: {
                    // Update element
                    System.out.print("Key to update element: ");
                    int key = in.nextInt();
                    System.out.print("New element: ");
                    int newElement = in.nextInt();

                    Object old = h.updateElement(key, newElement);
                    if (old == null) {
                        System.out.println("No entry with key = " + key);
                    } else {
                        System.out.println("Element updated. Old value = " + old);
                    }
                    h.output();
                    break;
                }

                case 3: {
                    // Update key
                    System.out.print("Old key: ");
                    int oldKey = in.nextInt();
                    System.out.print("New key: ");
                    int newKey = in.nextInt();

                    Object moved = h.updateKey(oldKey, newKey);
                    if (moved == null) {
                        System.out.println("No entry with key = " + oldKey);
                    } else {
                        System.out.println("Key updated. Element = " + moved);
                    }
                    h.output();
                    break;
                }

                case 4: {
                    // Delete by key
                    System.out.print("Key to delete: ");
                    int deleteKey = in.nextInt();

                    // Олдсон эсэхийг нь харахын тулд урьдчилж шалгана
                    Object exists = h.get(deleteKey);
                    if (exists == null) {
                        System.out.println("No entry with key = " + deleteKey);
                    } else {
                        h.delete(deleteKey);
                        System.out.println("Deleted entry with key = " + deleteKey + " (element was " + exists + ")");
                    }
                    h.output();
                    break;
                }

                case 5: {
                    // Print table
                    System.out.println("=== Hash table ===");
                    h.output();
                    break;
                }

                default: {
                    System.out.println("Invalid choice. Try again.");
                }
            }
        }

        in.close();
    }
}
