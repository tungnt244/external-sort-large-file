class DataSizeHelper {

    private static int STRING_HEADER = 40;
    private static int ARRAY_HEADER = 24;

    private static int OBJ_OVERHEAD = STRING_HEADER + ARRAY_HEADER;

    static long getMaxTmpFileSize() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        long usedMem = r.totalMemory() - r.freeMemory();
        long freeMem = r.maxMemory() - usedMem;
        return freeMem/2;
    }

    static long estimatedSizeOf(String s) {
        return s.length() * 2 + OBJ_OVERHEAD;
    }
}
