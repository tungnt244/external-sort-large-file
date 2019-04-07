class DataSizeHelper {

    private static int STRING_HEADER = 40;
    private static int ARRAY_HEADER = 24;

    private static int OBJ_OVERHEAD = STRING_HEADER + ARRAY_HEADER;

    static long getMaxTmpFileSize(final long maxMemory) {
        return maxMemory / 2;
    }

    static long estimatedSizeOf(String s) {
        return s.getBytes().length + OBJ_OVERHEAD;
    }
}
