/**
 * CourseException.java
 * Custom (checked) exception used for all course-related problems:
 * invalid input, duplicate codes, and malformed file lines.
 *
 * EXCEPTION HANDLING: using our own exception type lets Main catch
 * course problems separately from file (IOException) problems.
 */
public class CourseException extends Exception {

    private static final long serialVersionUID = 1L;

    public CourseException(String message) {
        super(message);
    }
}
