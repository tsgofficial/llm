package dataStructures;

import java.io.*;

// ---------- Car ----------
class Car {
    String plate;
    public Car(String plate) { this.plate = plate; }
    public String toString() { return plate; }
}

// ---------- Main ----------
public class CarParking {

    private static final int CAPACITY = 10;
    private static MyStack garage = new MyStack(CAPACITY); // <-- uses the separate MyStack.java
    private static MyStack temp    = new MyStack(CAPACITY);

    public static void main(String[] args) {
        try {
            String file = (args.length > 0) ? args[0] : "src/main/data/cars.txt";
            inputProcessOutput(file);
        } catch (Exception e) {
            System.out.println("⚠ Error: " + e.getMessage());
        }
    }

    public static void inputProcessOutput(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(" ");
            char action = parts[0].charAt(0);
            String plate = parts[1];
            Car car = new Car(plate);
            if (action == 'A') arrival(car);
            else if (action == 'D') departure(car);
        }
        br.close();
    }

    private static void arrival(Car car) {
        if (garage.size() == CAPACITY)
            System.out.println("Arrival " + car + " -> Garage full, this car cannot enter.");
        else {
            garage.push(car);
            System.out.println("Arrival " + car + " -> There is room.");
        }
    }

    private static void departure(Car target) {
        int moved = 0; boolean found = false;
        if (garage.empty()) { System.out.println("Departure " + target + " -> Garage empty."); return; }
        while (!garage.empty()) {
            Car top = (Car) garage.pop();
            if (top.plate.equals(target.plate)) { found = true; break; }
            temp.push(top); moved++;
        }
        if (!found) {
            while (!temp.empty()) garage.push(temp.pop());
            System.out.println("Departure " + target + " -> This car not in the garage.");
            return;
        }
        while (!temp.empty()) garage.push(temp.pop());
        System.out.println("Departure " + target + " -> " + moved + " cars moved out.");
    }
}
