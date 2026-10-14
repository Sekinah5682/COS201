import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseManager.java
 * Holds the list of courses and performs all operations on it.
 *
 * COLLECTIONS: courses are stored in an ArrayList<Course>.
 * RECURSION:   findRecursive() and totalRecursive() call themselves.
 * FILE I/O:    saveToFile() and loadFromFile() use java.io classes.
 */
public class CourseManager {

    private static final String HEADER = "CODE,UNITS,TITLE";

    private final List<Course> courses = new ArrayList<Course>();

    public int size() {
        return courses.size();
    }

    // ---------------------------------------------------------------
    // 1. Add
    // ---------------------------------------------------------------

    /** Adds a course; rejects duplicates (same code). */
    public void addCourse(Course course) throws CourseException {
        if (findByCode(course.getCode()) != null) {
            throw new CourseException("A course with code " + course.getCode()
                    + " already exists.");
        }
        courses.add(course);
    }

    // ---------------------------------------------------------------
    // 2. View all
    // ---------------------------------------------------------------

    /** Prints all courses in an aligned table (iteration with a for-each loop). */
    public void displayAll() {
        if (courses.isEmpty()) {
            System.out.println("No courses recorded yet.");
            return;
        }
        String line = repeat('-', 58);
        System.out.println(line);
        System.out.println(String.format("%-10s %-40s %5s", "CODE", "TITLE", "UNITS"));
        System.out.println(line);
        for (Course c : courses) {
            System.out.println(c);
        }
        System.out.println(line);
        System.out.println("Total courses: " + courses.size());
    }

    // ---------------------------------------------------------------
    // 3. Search (RECURSIVE)
    // ---------------------------------------------------------------

    /** Public entry point: cleans the code, then starts recursion at index 0. */
    public Course findByCode(String code) {
        if (code == null) {
            return null;
        }
        String cleaned = code.trim().replaceAll("\\s+", "").toUpperCase(); // string processing
        return findRecursive(cleaned, 0);
    }

    /**
     * RECURSION (search): checks the course at 'index'; if it is not a match,
     * the method calls itself with index + 1.
     * Base case 1: index reaches the end of the list -> not found (null).
     * Base case 2: course at index matches -> return it.
     */
    private Course findRecursive(String code, int index) {
        if (index >= courses.size()) {
            return null;
        }
        if (courses.get(index).getCode().equals(code)) {
            return courses.get(index);
        }
        return findRecursive(code, index + 1);
    }

    // ---------------------------------------------------------------
    // 4. Total units (RECURSIVE)
    // ---------------------------------------------------------------

    public int totalUnits() {
        return totalRecursive(0);
    }

    /**
     * RECURSION (sum): units of the course at 'index' + total of the rest.
     * Base case: index past the end of the list -> 0.
     */
    private int totalRecursive(int index) {
        if (index >= courses.size()) {
            return 0;
        }
        return courses.get(index).getUnits() + totalRecursive(index + 1);
    }

    // ---------------------------------------------------------------
    // 5. Save
    // ---------------------------------------------------------------

    /**
     * Writes all courses to a CSV file.
     * try-with-resources closes the writer automatically, even if an error occurs.
     * The IOException is passed up to Main, which shows a friendly message.
     */
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(HEADER);
            for (Course c : courses) {
                out.println(c.toCsvLine());
            }
            if (out.checkError()) {
                throw new IOException("Could not write to " + filename);
            }
        }
    }

    // ---------------------------------------------------------------
    // 6. Load
    // ---------------------------------------------------------------

    /**
     * Reads courses from a CSV file, replacing the current list.
     * A missing file throws FileNotFoundException (a kind of IOException),
     * which Main catches. Bad lines are skipped with a warning
     * instead of crashing the whole load.
     *
     * @return number of courses loaded successfully
     */
    public int loadFromFile(String filename) throws IOException {
        List<Course> loaded = new ArrayList<Course>();
        int lineNo = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {      // loop until end of file
                lineNo++;
                line = line.trim();
                if (line.isEmpty() || line.equalsIgnoreCase(HEADER)) {
                    continue;                              // skip blanks and header
                }
                try {
                    Course c = Course.fromCsvLine(line);
                    if (loaded.contains(c)) {              // uses Course.equals (by code)
                        System.out.println("  Skipped line " + lineNo
                                + ": duplicate code " + c.getCode());
                    } else {
                        loaded.add(c);
                    }
                } catch (CourseException e) {
                    System.out.println("  Skipped line " + lineNo + ": " + e.getMessage());
                }
            }
        }
        courses.clear();
        courses.addAll(loaded);
        return loaded.size();
    }

    // ---------------------------------------------------------------
    // Helper (String.repeat does not exist in Java 8)
    // ---------------------------------------------------------------
    private static String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
