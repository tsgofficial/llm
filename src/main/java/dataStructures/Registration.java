package dataStructures;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;

/**
 * Гол бүртгэлийн класс:
 * - subjectList, majorList, studentList
 * - TXT файл унших (Subjects / Professions / Exams)
 * - Тайлан хэвлэх функцууд
 */
public class Registration {

    /* ===== Custom lists ===== */
    public ArrayLinearList studentList = new ArrayLinearList();  // Student
    public ArrayLinearList subjectList = new ArrayLinearList();  // Subject
    public ArrayLinearList majorList   = new ArrayLinearList();  // Major

    /* ===== Helper: strip BOM + trim ===== */
    private static String clean(String s) {
        if (s == null) return null;
        return s.replace("\uFEFF", "").trim();
    }

    /* ===== Inner models tied to Registration ===== */
    public static class Lesson {
        public Subject learned; // үзсэн хичээл
        public int score;       // 0..100

        public Lesson(Subject learned, int score) {
            this.learned = learned;
            this.score = score;
        }
        public double courseGpa() { return gpaFromScore(score); }
        public boolean isF() { return score < 60; }
        public String toString() { return learned.code + "(" + score + ")"; }
    }

    public static class Student {
        public String code;     // оюутны код
        public float GPA;       // голч
        public Chain lessons;   // Chain<Lesson>

        public Student(String code) {
            this.code = code;
            this.lessons = new Chain();
        }

        public String toString() {
            return code + " GPA=" + String.format(Locale.US, "%.2f", GPA);
        }
    }

    /* ===================== File loaders ===================== */

    // Subjects.txt: code/name/credit
    public void loadSubjects(String fileName) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = clean(line);
                if (line.isEmpty()) continue;

                String[] v = line.split("/");
                if (v.length < 3) continue;

                String code   = clean(v[0]);
                String name   = clean(v[1]);
                float credit  = Float.parseFloat(clean(v[2]));

                subjectList.add(subjectList.size(), new Subject(code, name, credit));
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Professions.txt: code/name
    public void loadMajors(String fileName) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            for (String line; (line = br.readLine()) != null; ) {
                line = clean(line);
                if (line.isEmpty()) continue;
                String[] v = line.split("/");
                if (v.length < 2) continue;
                majorList.add(majorList.size(), new Major(clean(v[0]), clean(v[1])));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName); System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Exams.txt: studentCode/subjectCode/point
    public void loadExams(String fileName) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            for (String line; (line = br.readLine()) != null; ) {
                line = clean(line);
                if (line.isEmpty()) continue;
                String[] v = line.split("/");
                if (v.length < 3) continue;

                String sCode   = clean(v[0]);
                String subjCod = clean(v[1]);
                int score      = Integer.parseInt(clean(v[2]));

                Subject subj = findSubjectByCode(subjCod);
                if (subj == null) { System.out.println("Todorhoigui hicheel: " + subjCod); continue; }

                Student s = findOrCreateStudent(sCode);

                // Chain төгсгөл рүү нэмэх (одоохондоо size тоолж index-ээр)
                int n = chainSize(s.lessons);
                s.lessons.add(n, new Lesson(subj, score));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName); System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Бүх оюутны GPA-г шинэчилнэ
        for (int i = 0; i < studentList.size(); i++) {
            Student s = (Student) studentList.get(i);
            s.GPA = (float) computeWeightedGpa(s);
        }
    }

    /* ===================== Reports ===================== */

    public void printAllSubjects() {
        System.out.println("=== Hicheeluud ===");
        for (int i = 0; i < subjectList.size(); i++)
            System.out.println("- " + subjectList.get(i));
    }

    public void printAllMajors() {
        System.out.println("=== Mergejluud ===");
        for (int i = 0; i < majorList.size(); i++)
            System.out.println("- " + majorList.get(i));
    }

    // Оюутны үзсэн хичээлийн дундаж оноо (0..100)
    private static double averageScore(Chain lessons) {
        final int[] sum = {0};
        final int[] n   = {0};
        iterateLessons(lessons, new LessonVisitor() {
            public void visit(Lesson L) { sum[0] += L.score; n[0]++; }
        });
        return n[0] == 0 ? 0.0 : (double) sum[0] / n[0];
    }

    // Оюутан бүрийн: хичээлийн тоо, дундаж оноо, GPA хэвлэнэ
    public void printPerStudentAverages() {
        if (studentList.size() == 0) { System.out.println("Oyutan alga"); return; }
        System.out.println("=== Oyutan buriin hicheeliin too, dundaj onoо, GPA ===");
        for (int i = 0; i < studentList.size(); i++) {
            Student s = (Student) studentList.get(i);
            int n = chainSize(s.lessons);
            double avg = averageScore(s.lessons);
            double gpa = s.GPA; // эсвэл computeWeightedGpa(s)

            System.out.printf(Locale.US,
                    "%-12s | hicheel=%2d | avg=%.2f | GPA=%.2f%n",
                    s.code, n, avg, gpa);
        }
    }

    public void printDropCandidatesF3OrMore() {
        if (studentList.size() == 0) { System.out.println("Oyutan alga"); return; }
        System.out.println("=== 3+ F-tei oyutnuud ===");
        for (int i = 0; i < studentList.size(); i++) {
            Student s = (Student) studentList.get(i);
            int f = countF(s.lessons);
            if (f >= 3) {
                System.out.printf("- %s (F=%d)%n", s.code, f);
            }
        }
    }

    public void printGradesBySubject() {
        System.out.println("=== Hicheel bureer dun ===");
        for (int j = 0; j < subjectList.size(); j++) {
            Subject subj = (Subject) subjectList.get(j);
            System.out.println(subj.code + ":");
            for (int i = 0; i < studentList.size(); i++) {
                Student stu = (Student) studentList.get(i);
                iterateLessons(stu.lessons, new LessonVisitor() {
                    public void visit(Lesson L) {
                        if (L.learned.code.equals(subj.code))
                            System.out.println("  " + stu.code + " -> " + L.score +
                                    " (" + letterFromScore(L.score) + ")");
                    }
                });
            }
        }
    }

    public void printGradesByMajor() {
        System.out.println("=== Mergejil bureer dun ===");
        for (int m = 0; m < majorList.size(); m++) {
            Major mj = (Major) majorList.get(m);
            System.out.println(mj.code + ": " + mj.name);
            for (int i = 0; i < studentList.size(); i++) {
                Student s = (Student) studentList.get(i);
                if (!s.code.startsWith(mj.code)) continue;

                StringBuilder sb = new StringBuilder();
                iterateLessons(s.lessons, new LessonVisitor() {
                    public void visit(Lesson L) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(L.learned.code).append(":")
                          .append(L.score).append("(")
                          .append(letterFromScore(L.score)).append(")");
                    }
                });
                System.out.println("  " + s.code + " | " + sb.toString() +
                        " | GPA=" + String.format(Locale.US, "%.2f", s.GPA));
            }
        }
    }

    /* ===================== Helpers ===================== */

    private Subject findSubjectByCode(String code) {
        for (int i = 0; i < subjectList.size(); i++) {
            Subject s = (Subject) subjectList.get(i);
            if (s.code.equals(code)) return s;
        }
        return null;
    }

    private Student findOrCreateStudent(String code) {
        for (int i = 0; i < studentList.size(); i++) {
            Student s = (Student) studentList.get(i);
            if (s.code.equals(code)) return s;
        }
        Student s = new Student(code);
        studentList.add(studentList.size(), s);
        return s;
    }

    private static double computeWeightedGpa(Student s) {
        final double[] g = {0.0};
        final double[] c = {0.0};
        iterateLessons(s.lessons, new LessonVisitor() {
            public void visit(Lesson L) {
                g[0] += L.courseGpa() * L.learned.credit;
                c[0] += L.learned.credit;
            }
        });
        return c[0] == 0 ? 0.0 : g[0] / c[0];
    }

    private static int countF(Chain lessons) {
        final int[] cnt = {0};
        iterateLessons(lessons, new LessonVisitor() {
            public void visit(Lesson L) { if (L.isF()) cnt[0]++; }
        });
        return cnt[0];
    }

    // Chain iterator-аар гүйх жижиг интерфэйс
    private interface LessonVisitor { void visit(Lesson l); }

    private static void iterateLessons(Chain lessons, LessonVisitor v) {
        @SuppressWarnings("unchecked")
        Iterator<Object> it = lessons.iterator();
        while (it.hasNext()) v.visit((Lesson) it.next());
    }

    private static int chainSize(Chain lessons) {
        int n = 0;
        @SuppressWarnings("unchecked")
        Iterator<Object> it = lessons.iterator();
        while (it.hasNext()) { it.next(); n++; }
        return n;
    }

    /* ===================== GPA rules ===================== */
    public static double gpaFromScore(int s) {
        if (s >= 90) return 4.0;
        if (s >= 80) return 3.0;
        if (s >= 70) return 2.0;
        if (s >= 60) return 1.0;
        return 0.0;
    }
    public static String letterFromScore(int s) {
        if (s >= 90) return "A";
        if (s >= 80) return "B";
        if (s >= 70) return "C";
        if (s >= 60) return "D";
        return "F";
    }
}
