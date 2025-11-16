package dataStructures;

public class MyHashChains extends HashChains {

    public MyHashChains(int theDivisor) {
        super(theDivisor);
    }

    /**
     * Хэрвээ тухайн түлхүүртэй (theKey) элемент олдвол
     * түүний утгыг theElement-р сольж, хуучин утгыг буцаана.
     * Олдохгүй бол null буцаана.
     */
    public Object updateElement(Object theKey, Object theElement) {
        // Эхлээд байгаа эсэхийг шалгана
        Object old = this.get(theKey);
        if (old == null) {
            // ийм key байхгүй тул юу ч өөрчлөхгүй
            return null;
        }

        // HashChains.put нь тухайн key байсан бол value-г нь overwrite хийнэ сольж өгнө
        this.put(theKey, theElement);

        // утгаа буцаана
        return old;
    }

    /**
     * Хуучин түлхүүр (theKey)-ийг шинэ түлхүүр (theNewKey)-ээр сольж,
     * элементийг дахин байршуулна.
     * Олдвол элементийг буцаана, олдохгүй бол null.
     */
    public Object updateKey(Object theKey, Object theNewKey) {
        // 1. Хуучин key-аар байгаа элементийг олно
        Object element = this.get(theKey);
        if (element == null) {
            // ийм key байхгүй
            return null;
        }

        // 2. Хуучин key-г устгана
        this.remove(theKey);

        // 3. Шинэ key-ээр дахин хадгална
        this.put(theNewKey, element);

        // 4. element-ээ буцаана
        return element;
    }

    /**
     * Түлхүүрээр нь хайгаад, бүр мөсөн устгана.
     * Олдохгүй бол юу ч хийхгүй.
     */
    public void delete(Object theKey) {
        this.remove(theKey); // remove() өөрөө олдохгүй бол null буцаагаад л дуусна
    }

    public static void main(String[] args) {
        MyHashChains h = new MyHashChains(11);

        // Эхний өгөгдлүүд
        h.put(80, 80);
        h.put(40, 40);
        h.put(65, 65);
        h.put(58, 58);
        h.put(24, 24);
        h.put(2, 2);
        h.put(13, 13);
        h.put(46, 46);
        h.put(16, 16);
        h.put(7, 7);
        h.put(21, 21);

        System.out.println("=== main table ===");
        h.output();

        // updateElement example
        System.out.println("\nupdateElement(7 -> 29) called:");
        Object old7 = h.updateElement(7, 29);
        System.out.println("Old value = " + old7);
        h.output();

        // updateKey example
        System.out.println("\nupdateKey(21 -> 99) called:");
        Object moved = h.updateKey(21, 99);
        System.out.println("Element that was at 21 = " + moved);
        h.output();

        // delete example
        System.out.println("\ndelete(40) called:");
        h.delete(40);
        h.output();
    }
}
