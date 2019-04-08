import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestSortFile {

    private static final String[] EXPECTED_SORT_RESULTS = {"", "a", "a", "b", "b", "e", "f",
            "i", "m", "o", "u", "u", "x", "y", "z"
    };

    private static final String SAMPLE_FILE = "/home/tungnt/learning/largefilesort/src/test/java/sample-file.txt";
    private static final String ASCII_FILE = "/home/tungnt/learning/largefilesort/src/test/java/ascii-file.txt";
    private static final String ASCII_FILE_OUTPUT = "/home/tungnt/learning/largefilesort/src/test/java/ascii-file-output.txt";
    private static final String ASCII_FILE_EXPECT = "/home/tungnt/learning/largefilesort/src/test/java/ascii-file-expect.txt";
    private static final String UNICODE_FILE = "/home/tungnt/learning/largefilesort/src/test/java/unicode-file.txt";
    private static final String UNICODE_FILE_OUTPUT = "/home/tungnt/learning/largefilesort/src/test/java/unicode-file-output.txt";
    private static final String UNICODE_FILE_EXPECT = "/home/tungnt/learning/largefilesort/src/test/java/unicode-file-expect.txt";
    private File sampleFile;
    private File asciiFile;
    private File asciiFileOutput;
    private File asciiFileExpect;
    private File unicodeFileExpect;

    @Before
    public void setUp() {
        this.asciiFileOutput = new File(ASCII_FILE_OUTPUT);
        this.sampleFile = new File(SAMPLE_FILE);
        this.asciiFile = new File(ASCII_FILE);
        this.asciiFileExpect = new File(ASCII_FILE_EXPECT);
        this.unicodeFileExpect = new File(UNICODE_FILE_EXPECT);
    }

    @Test
    public void testSortUnicodeFile() throws IOException {
        testSortLargeFile(UNICODE_FILE, UNICODE_FILE_OUTPUT, unicodeFileExpect);
    }

    @Test
    public void testSortAsciiFile() throws IOException {
        testSortLargeFile(ASCII_FILE, ASCII_FILE_OUTPUT, asciiFileExpect);
    }

    private void testSortLargeFile(String inputFile, String outputFile, File largeExpectedFile) throws IOException {
        SortFile.doIt(inputFile, outputFile);

        String line;
        List<String> outputArray = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile)))) {
            while ((line = bf.readLine()) != null) {
                outputArray.add(line);
            }
        }

        List<String> expectedArray = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(largeExpectedFile)))) {
            while ((line = bf.readLine()) != null) {
                expectedArray.add(line);
            }
        }

        assertArrayEquals(expectedArray.toArray(), outputArray.toArray());
    }

    @Test
    public void testMergeSortFile() throws IOException {
        List<File> sortedFiles = SortFile.sortBatch(asciiFile);
        List<BufferedReader> bufferedReaderList = SortFile.getListTempToRead(sortedFiles);
        List<CachedBufferedReader> cachedBufferedReaders = new ArrayList<>();
        for (BufferedReader bfReader : bufferedReaderList) {
            cachedBufferedReaders.add(new CachedBufferedReader(bfReader));
        }
        SortFile.mergeSortedFiles(cachedBufferedReaders, asciiFileOutput);
        for (File f : sortedFiles) {
            f.delete();
        }
        String line;
        List<String> outputArray = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(asciiFileOutput)))) {
            while ((line = bf.readLine()) != null) {
                outputArray.add(line);
            }
        }

        List<String> expectedArray = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(asciiFileExpect)))) {
            while ((line = bf.readLine()) != null) {
                expectedArray.add(line);
            }
        }

        assertArrayEquals(expectedArray.toArray(), outputArray.toArray());
    }

    @Test
    public void testSortLargeFileToList() throws IOException {
        List<File> resultFiles = SortFile.sortBatch(asciiFile); // 1mbfile with 512kb ram
        assertNotNull(resultFiles);
        for (File file : resultFiles) {
            assertTrue(file.exists());
        }
        assertEquals(1, resultFiles.size());
    }

    @Test
    public void testEstimateSize() {
        String expted = "âaâ";
        long length = DataSizeHelper.estimatedSizeOf(expted);
        assertEquals(expted.length()*2 + 64, length);
    }

    @Test
    public void testSortFile() throws IOException {
        List<String> unsortedString = fileToList(sampleFile);
        File resultFile = SortFile.sortIntoFile(unsortedString);
        assertNotNull(resultFile);
        assertTrue(resultFile.exists());
        assertTrue(resultFile.length() > 0);
        List<String> fileToArray = new ArrayList<>();
        String line;
        try (BufferedReader bf = new BufferedReader(new FileReader(resultFile))) {
            while ((line = bf.readLine()) != null) {
                fileToArray.add(line);
            }
        }
        assertArrayEquals(EXPECTED_SORT_RESULTS, fileToArray.toArray());
    }

    @Test
    public void testEmptyFiles() throws Exception {
        File f1 = File.createTempFile("tmp", "unit");
        f1.deleteOnExit();
        File sortedFile = SortFile.sortIntoFile(fileToList(f1));
        if (sortedFile.length() != 0) throw new RuntimeException("empty files should end up emtpy");
    }

    private static List<String> fileToList(File file) {
        List<String> unsortedListString = new ArrayList<>();
        try (BufferedReader fbReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = fbReader.readLine()) != null) {
                unsortedListString.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unsortedListString;
    }
}
