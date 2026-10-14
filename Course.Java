/**
 * Course.java
 * Represents a single course (code, title, credit units).
 *
 * OOP: Encapsulation - all fields are private and accessed via getters/setters.
 * STRING MANIPULATION: the setters trim whitespace, fix casing, and validate.
 * EXCEPTION HANDLING: invalid data throws CourseException (custom checked exception).
 */
public class Course {

    private static final int MIN_UNITS = 1;
    private static final int MAX_UNITS = 12;

    private String code;
    private String title;
    private int units;

    /** Constructor: delegates to the setters so validation is always applied. */
    public Course(String code, String title, int units) throws CourseException {
        setCode(code);
        setTitle(title);
        setUnits(units);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------
    public String getCode()  { return code; }
    public String getTitle() { return title; }
    public int getUnits()    { return units; }

    // ---------------------------------------------------------------
    // Setters (with validation and string processing)
    // ---------------------------------------------------------------

    /**
     * STRING MANIPULATION: trim(), remove inner spaces, toUpperCase(), regex validation.
     * "  cos 201 " becomes "COS201".
     */
    public void setCode(String code) throws CourseException {
        if (code == null || code.trim().isEmpty()) {
            throw new CourseException("Course code cannot be empty.");
        }
        String cleaned = code.trim().replaceAll("\\s+", "").toUpperCase();
        // 2-4 letters followed by 3-4 digits, e.g. COS201, MTH101, GST1101
        if (!cleaned.matches("[A-Z]{2,4}[0-9]{3,4}")) {
            throw new CourseException("Invalid course code '" + code.trim()
                    + "'. Use 2-4 letters followed by 3-4 digits (e.g. COS201).");
        }
        this.code = cleaned;
    }

    /**
     * STRING MANIPULATION: trim(), collapse repeated spaces, convert to Title Case.
     * "  programming   i " becomes "Programming I".
     */
    public void setTitle(String title) throws CourseException {
        if (title == null || title.trim().isEmpty()) {
            throw new CourseException("Course title cannot be empty.");
        }
        String[] words = title.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) {
                sb.append(w.substring(1).toLowerCase());
            }
            if (i < words.length - 1) {
                sb.append(' ');
            }
        }
        this.title = sb.toString();
    }

    public void setUnits(int units) throws CourseException {
        if (units < MIN_UNITS || units > MAX_UNITS) {
            throw new CourseException("Credit units must be between "
                    + MIN_UNITS + " and " + MAX_UNITS + ".");
        }
        this.units = units;
    }

    // ---------------------------------------------------------------
    // File support (CSV line format:  CODE,UNITS,TITLE)
    // The title is last so that commas inside a title cannot break parsing.
    // ---------------------------------------------------------------

    /** Converts this course into one CSV line for saving. */
    public String toCsvLine() {
        return code + "," + units + "," + title;
    }

    /**
     * Builds a Course from a CSV line.
     * EXCEPTION HANDLING: NumberFormatException from Integer.parseInt is caught
     * and re-thrown as a CourseException with a helpful message.
     */
    public static Course fromCsvLine(String line) throws CourseException {
        if (line == null) {
            throw new CourseException("Empty line.");
        }
        String[] parts = line.split(",", 3); // limit 3: title may contain commas
        if (parts.length < 3) {
            throw new CourseException("Expected 'CODE,UNITS,TITLE' but found: " + line);
        }
        try {
            int u = Integer.parseInt(parts[1].trim());
            return new Course(parts[0], parts[2], u);
        } catch (NumberFormatException e) {
            throw new CourseException("Units is not a whole number in line: " + line);
        }
    }

    // ---------------------------------------------------------------
    // Object methods
    // ---------------------------------------------------------------

    /** Formatted row used by the "View All Courses" table. */
    @Override
    public String toString() {
        return String.format("%-10s %-40s %5d", code, title, units);
    }

    /** Two courses are the same if their codes match. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return code.equals(((Course) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
