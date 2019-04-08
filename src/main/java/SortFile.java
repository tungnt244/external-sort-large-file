import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

class SortFile {

    public static void main(String[] args) throws IOException {
        Date startDate = new Date();
        System.out.println(startDate);
        String inputFileName = args[0];
        String outputFileName = args[1];
        SortFile.doIt(inputFileName, outputFileName);
        Date endDate = new Date();
        System.out.println((endDate.getTime() - startDate.getTime()) / 1000);
    }

    static void doIt(String inputFile, String outputFile) throws IOException {
        List<File> sortedFileList = sortBatch(new File(inputFile));
        List<CachedBufferedReader> cachedBufferedReaders = new ArrayList<>();
        for (BufferedReader br : getListTempToRead(sortedFileList)) {
            cachedBufferedReaders.add(new CachedBufferedReader(br));
        }
        mergeSortedFiles(cachedBufferedReaders, new File(outputFile));
        for (File f : sortedFileList) {
            f.delete();
        }
    }

    static void mergeSortedFiles(List<CachedBufferedReader> listInputReader, File outputFile) throws IOException {

        PriorityQueue<CachedBufferedReader> priorityQueue = new PriorityQueue<>(10, (cachedBufferedReader, t1) -> StringComparator.getDefault().compare(cachedBufferedReader.cache, t1.cache));
        for (CachedBufferedReader br : listInputReader) {
            if (!br.isEmpty()) {
                priorityQueue.add(br);
            }
        }
        try (BufferedWriter outputFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), Charset.defaultCharset()))) {
            String line;
            while (priorityQueue.size() > 0) {
                CachedBufferedReader cachedFBReader = priorityQueue.poll();
                line = cachedFBReader.pop();
                outputFileWriter.write(line);
                outputFileWriter.newLine();
                if (cachedFBReader.isEmpty()) {
                    cachedFBReader.close();
                } else {
                    priorityQueue.add(cachedFBReader);
                }
            }
            outputFileWriter.flush();
        } finally {
            for (CachedBufferedReader pq : priorityQueue) {
                pq.close();
            }
        }
    }

    static List<BufferedReader> getListTempToRead(List<File> sortedFile) throws FileNotFoundException {
        List<BufferedReader> listBufferedReader = new ArrayList<>();
        for (File file : sortedFile) {
            InputStream in = new FileInputStream(file);
            listBufferedReader.add(new BufferedReader(new InputStreamReader(in)));
        }
        return listBufferedReader;
    }

    static List<File> sortBatch(File largeFile) throws IOException {

        List<File> sortedFiles = new ArrayList<>();
        long maxTmpFileSize = DataSizeHelper.getMaxTmpFileSize();
        String line = "";
        List<String> unsortedListString = new ArrayList<>();

        try (BufferedReader fbReader = new BufferedReader(new InputStreamReader(new FileInputStream(largeFile)))) {
            while (line != null) {
                long currentTmpSize = 0;
                while ((currentTmpSize < maxTmpFileSize) && (line = fbReader.readLine()) != null) {
                    unsortedListString.add(line);
                    currentTmpSize += DataSizeHelper.estimatedSizeOf(line);
                }
                sortedFiles.add(sortIntoFile(unsortedListString));
                unsortedListString.clear();
            }
        } catch (EOFException e) {
            if (unsortedListString.size() > 0) {
                sortedFiles.add(sortIntoFile(unsortedListString));
                unsortedListString.clear();
            }
            e.printStackTrace();
        }
        return sortedFiles;
    }

    static File sortIntoFile(List<String> stringList) throws IOException {
        stringList = stringList.parallelStream().sorted(StringComparator.getDefault()).collect(Collectors.toCollection(ArrayList::new));
        File tmpFile = File.createTempFile("sortedFile", "daf", null);
        tmpFile.deleteOnExit();
        OutputStream out = new FileOutputStream(tmpFile);
        try (BufferedWriter fbwriter = new BufferedWriter(new OutputStreamWriter(out))) {
            for (String s : stringList) {
                fbwriter.write(s);
                fbwriter.newLine();
            }
            fbwriter.flush();
            fbwriter.close();
            return tmpFile;
        }
    }
}
