import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main.java
 * Console user interface for the Student Course Management System.
 *
 * Compile:  javac *.java
 * Run:      java Main
 */
public class Main {

    private static final String DEFAULT_FILE = "courses.csv";

    public static void main(String[] args) {
        CourseManager manager = new CourseManager();
        Scanner in = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("   STUDENT COURSE MANAGEMENT SYSTEM");
        System.out.println("   COS 201 - Programming I (MIVA Open University)");
        System.out.println("==========================================");

        runMenu(manager, in);   // starts the recursive menu

        in.close();
    }

    /**
     * RECURSION (menu): after handling one choice, this method calls itself
     * to show the menu again. The recursion stops (base case) when the user
     * chooses 7 (Exit) or the input ends.
     * Note: each menu round adds one stack frame, which is fine for a
     * lab-sized program but a while-loop would be safer for very long sessions.
     */
    private static void runMenu(CourseManager manager, Scanner in) {
        printMenu();
        String input = readLine(in, "Enter your choice (1-7): ");

        if (input == null) {                       // base case: input stream closed
            System.out.println("\nInput ended. Goodbye!");
            return;
        }

        int choice;
        try {
            choice = Integer.parseInt(input.trim());   // EXCEPTION: non-numeric menu input
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please enter a number from 1 to 7.");
            runMenu(manager, in);                       // recursive call
            return;
        }

        // Logical decision-making for menu selection
        switch (choice) {
            case 1: addCourse(manager, in);       break;
            case 2: manager.displayAll();         break;
            case 3: searchCourse(manager, in);    break;
            case 4: showTotal(manager);           break;
            case 5: saveCourses(manager, in);     break;
            case 6: loadCourses(manager, in);     break;
            case 7:
                System.out.println("Goodbye! Good luck with your semester.");
                return;                                 // base case: stop recursion
            default:
                System.out.println("Invalid choice. Please pick a number from 1 to 7.");
        }

        runMenu(manager, in);                           // recursive call: show menu again
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("-------------- MAIN MENU --------------");
        System.out.println(" 1. Add Course");
        System.out.println(" 2. View All Courses");
        System.out.println(" 3. Search Course by Code");
        System.out.println(" 4. Compute Total Units");
        System.out.println(" 5. Save to File");
        System.out.println(" 6. Load from File");
        System.out.println(" 7. Exit Program");
        System.out.println("---------------------------------------");
    }

    // ---------------------------------------------------------------
    // Menu actions
    // ---------------------------------------------------------------

    private static void addCourse(CourseManager manager, Scanner in) {
        System.out.println("\n--- Add Course ---");
        String code = readLine(in, "Course code (e.g. COS201): ");
        String title = readLine(in, "Course title (e.g. Programming I): ");
        String unitText = readLine(in, "Credit units (1-12): ");
        if (code == null || title == null || unitText == null) {
            return;
        }

        try {
            // EXCEPTION HANDLING: invalid number format
            int units = Integer.parseInt(unitText.trim());
            Course course = new Course(code, title, units); // validates + cleans strings
            manager.addCourse(course);                      // checks duplicates
            System.out.println("Added: " + course.getCode() + " - " + course.getTitle());
        } catch (NumberFormatException e) {
            System.out.println("Error: credit units must be a whole number (e.g. 3).");
        } catch (CourseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchCourse(CourseManager manager, Scanner in) {
        System.out.println("\n--- Search Course ---");
        String code = readLine(in, "Enter course code to search: ");
        if (code == null) {
            return;
        }
        if (code.trim().isEmpty()) {
            System.out.println("Error: please enter a course code.");
            return;
        }
        Course found = manager.findByCode(code);   // recursive search inside CourseManager
        if (found == null) {
            System.out.println("No course found with code '" + code.trim().toUpperCase() + "'.");
        } else {
            System.out.println("Course found:");
            System.out.println("  Code : " + found.getCode());
            System.out.println("  Title: " + found.getTitle());
            System.out.println("  Units: " + found.getUnits());
        }
    }

    private static void showTotal(CourseManager manager) {
        System.out.println("\n--- Total Credit Units ---");
        System.out.println("Courses recorded: " + manager.size());
        System.out.println("Total units     : " + manager.totalUnits()); // recursive sum
    }

    private static void saveCourses(CourseManager manager, Scanner in) {
        System.out.println("\n--- Save to File ---");
        String file = askFileName(in);
        if (file == null) {
            return;
        }
        try {
            manager.saveToFile(file);
            System.out.println("Saved " + manager.size() + " course(s) to '" + file + "'.");
        } catch (IOException e) {
            System.out.println("Error: could not save file. " + e.getMessage());
        }
    }

    private static void loadCourses(CourseManager manager, Scanner in) {
        System.out.println("\n--- Load from File ---");
        String file = askFileName(in);
        if (file == null) {
            return;
        }
        try {
            int count = manager.loadFromFile(file);
            System.out.println("Loaded " + count + " course(s) from '" + file + "'.");
        } catch (FileNotFoundException e) {
            // EXCEPTION HANDLING: file does not exist
            System.out.println("Error: file '" + file + "' was not found. "
                    + "Save a file first (option 5) or check the name.");
        } catch (IOException e) {
            System.out.println("Error: could not read file. " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Input helpers
    // ---------------------------------------------------------------

    /** Asks for a file name; pressing Enter uses the default. */
    private static String askFileName(Scanner in) {
        String name = readLine(in, "File name (press Enter for '" + DEFAULT_FILE + "'): ");
        if (name == null) {
            return null;
        }
        name = name.trim();                       // string processing: trim
        return name.isEmpty() ? DEFAULT_FILE : name;
    }

    /** Prints a prompt and reads one line; returns null if input has ended. */
    private static String readLine(Scanner in, String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {
            return null;
        }
        return in.nextLine();
    }
}
