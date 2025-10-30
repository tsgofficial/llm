package dataStructures;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainApp {
    private static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1) Niit hicheeluud");
        System.out.println("2) Niit mergejluud");
        System.out.println("3) Oyutan buriin dundaj (score) & GPA");
        System.out.println("4) 3+ F-tei oyutnuud");
        System.out.println("5) Hicheel bureer dun");
        System.out.println("6) Mergejil bureer dun");
        System.out.println("0) Garah");
        System.out.print("Songolt: ");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java dataStructures.MainApp <Subjects.txt> <Professions.txt> <Exams.txt>");
            System.exit(1);
        }

        Registration reg = new Registration();
        reg.loadSubjects(args[0]);
        reg.loadMajors(args[1]);
        reg.loadExams(args[2]);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            printMenu();
            String line = in.readLine();
            if (line == null) break;
            switch (line.trim()) {
                case "1": reg.printAllSubjects(); break;
                case "2": reg.printAllMajors(); break;
                case "3": reg.printPerStudentAverages(); break;
                case "4": reg.printDropCandidatesF3OrMore(); break;
                case "5": reg.printGradesBySubject(); break;
                case "6": reg.printGradesByMajor(); break;
                case "0": System.out.println("Bye!"); return;
                default:  System.out.println("Buruu songolt");
            }
        }
    }
}
