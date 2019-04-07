import java.util.Comparator;

public class StringComparator implements Comparator<String> {

    private StringComparator(){}

    private static StringComparator defaultComparator = new StringComparator();

    public int compare(String s, String t1) {
        return s.compareTo(t1);
    }

    static StringComparator getDefault() {
        return defaultComparator;
    }
}
