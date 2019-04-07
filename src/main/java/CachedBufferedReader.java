import java.io.BufferedReader;
import java.io.IOException;

class CachedBufferedReader {
    private BufferedReader bufferedReader;
    String cache;

    CachedBufferedReader(BufferedReader reader) throws IOException {
        this.bufferedReader = reader;
        cache = this.bufferedReader.readLine();
    }

    String pop() throws IOException {
        String oldCache = cache.toString();
        cache = this.bufferedReader.readLine();
        return oldCache;
    }

    boolean isEmpty() {
        return cache == null;
    }

    void close() throws IOException {
        this.bufferedReader.close();
    }
}
