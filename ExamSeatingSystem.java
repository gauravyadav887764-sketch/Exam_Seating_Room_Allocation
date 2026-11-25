
import java.util.*;

public class ExamSeatingSystem {

    // Student class
    static class Student {
        int rollNo;
        String name;
        String department;

        Student(int rollNo, String name, String department) {
            this.rollNo = rollNo;
            this.name = name;
            this.department = department;
        }
    }

    // Room class
    static class Room {
        String roomName;
        int rows, cols;
        Student[][] seats;

        Room(String roomName, int rows, int cols) {
            this.roomName = roomName;
            this.rows = rows;
            this.cols = cols;
            seats = new Student[rows][cols];
        }
    }

    // Graph for department conflicts
    static class ConflictGraph {
        Map<Integer, Set<Integer>> graph = new HashMap<>();

        void addStudent(int rollNo) {
            graph.putIfAbsent(rollNo, new HashSet<>());
        }

        void addConflict(int roll1, int roll2) {
            graph.get(roll1).add(roll2);
            graph.get(roll2).add(roll1);
        }

        boolean hasConflict(int roll1, int roll2) {
            if (!graph.containsKey(roll1))
                return false;

            return graph.get(roll1).contains(roll2);
        }
    }

    static Queue<Student> studentQueue = new LinkedList<>();
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Room> rooms = new ArrayList<>();
    static ConflictGraph conflictGraph = new ConflictGraph();

    // Add student
    static void addStudent(int roll, String name, String dept) {
        Student s = new Student(roll, name, dept);
        students.add(s);
        studentQueue.add(s);
        conflictGraph.addStudent(roll);
    }

    // Create conflict graph
    static void createConflictGraph() {
        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) {

                Student s1 = students.get(i);
                Student s2 = students.get(j);

                if (s1.department.equalsIgnoreCase(s2.department)) {
                    conflictGraph.addConflict(s1.rollNo, s2.rollNo);
                }
            }
        }
    }

    // Check adjacent seat conflicts
    static boolean isSafe(Room room, int row, int col, Student student) {

        // Check left seat
        if (col > 0 && room.seats[row][col - 1] != null) {
            Student left = room.seats[row][col - 1];

            if (conflictGraph.hasConflict(student.rollNo, left.rollNo))
                return false;
        }

        // Check upper seat
        if (row > 0 && room.seats[row - 1][col] != null) {
            Student upper = room.seats[row - 1][col];

            if (conflictGraph.hasConflict(student.rollNo, upper.rollNo))
                return false;
        }

        return true;
    }

    // Allocate students
    static void allocateSeats() {

        while (!studentQueue.isEmpty()) {

            Student student = studentQueue.peek();
            boolean allocated = false;

            for (Room room : rooms) {

                for (int i = 0; i < room.rows; i++) {
                    for (int j = 0; j < room.cols; j++) {

                        if (room.seats[i][j] == null &&
                            isSafe(room, i, j, student)) {

                            room.seats[i][j] = student;
                            studentQueue.poll();
                            allocated = true;
                            break;
                        }
                    }

                    if (allocated)
                        break;
                }

                if (allocated)
                    break;
            }

            // If no safe seat is found, allow a seat
            // without the department restriction
            if (!allocated) {

                boolean found = false;

                for (Room room : rooms) {
                    for (int i = 0; i < room.rows; i++) {
                        for (int j = 0; j < room.cols; j++) {

                            if (room.seats[i][j] == null) {
                                room.seats[i][j] = student;
                                studentQueue.poll();
                                found = true;
                                break;
                            }
                        }

                        if (found)
                            break;
                    }

                    if (found)
                        break;
                }

                if (!found) {
                    System.out.println("No seats available for Roll No: "
                            + student.rollNo);
                    studentQueue.poll();
                }
            }
        }

        System.out.println("\nSeating Allocation Completed!");
    }

    // Display seating arrangement
    static void displayRooms() {

        for (Room room : rooms) {

            System.out.println("\n==================================");
            System.out.println("ROOM: " + room.roomName);
            System.out.println("==================================");

            for (int i = 0; i < room.rows; i++) {

                for (int j = 0; j < room.cols; j++) {

                    if (room.seats[i][j] == null) {
                        System.out.print("[ EMPTY ] ");
                    } else {
                        System.out.print("[ "
                                + room.seats[i][j].rollNo + " ] ");
                    }
                }

                System.out.println();
            }
        }
    }

    // Search student
    static void searchStudent(int rollNo) {

        for (Room room : rooms) {

            for (int i = 0; i < room.rows; i++) {
                for (int j = 0; j < room.cols; j++) {

                    Student s = room.seats[i][j];

                    if (s != null && s.rollNo == rollNo) {

                        System.out.println("\nStudent Found!");
                        System.out.println("Roll Number: " + s.rollNo);
                        System.out.println("Name: " + s.name);
                        System.out.println("Department: " + s.department);
                        System.out.println("Room: " + room.roomName);
                        System.out.println("Row: " + (i + 1));
                        System.out.println("Column: " + (j + 1));

                        return;
                    }
                }
            }
        }

        System.out.println("Student not found or not allocated.");
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Sample students
        addStudent(101, "Aman", "CSE");
        addStudent(102, "Rahul", "CSE");
        addStudent(103, "Priya", "ECE");
        addStudent(104, "Neha", "ECE");
        addStudent(105, "Rohit", "IT");
        addStudent(106, "Anjali", "IT");
        addStudent(107, "Karan", "CSE");
        addStudent(108, "Simran", "ECE");

        // Sample examination rooms
        rooms.add(new Room("R101", 2, 3));
        rooms.add(new Room("R102", 2, 3));

        // Build conflict graph
        createConflictGraph();

        int choice;

        do {

            System.out.println("\n==================================");
            System.out.println(" EXAM SEATING & ROOM ALLOCATION");
            System.out.println("==================================");
            System.out.println("1. Display Students");
            System.out.println("2. Allocate Seats");
            System.out.println("3. Display Seating Arrangement");
            System.out.println("4. Search Student Seat");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\nStudent List:");

                    for (Student s : students) {
                        System.out.println(s.rollNo + " - "
                                + s.name + " - " + s.department);
                    }
                    break;

                case 2:
                    allocateSeats();
                    break;

                case 3:
                    displayRooms();
                    break;

                case 4:
                    System.out.print("Enter Roll Number: ");
                    int roll = sc.nextInt();
                    searchStudent(roll);
                    break;

                case 5:
                    System.out.println("Exiting Program...");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

        } while (choice != 5);

        sc.close();
    }
}
