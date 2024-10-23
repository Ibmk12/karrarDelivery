package com.karrardelivery.logging;

import java.io.PrintWriter;

/**
 * Extends PrintWriter to write to two PrintWriter instances simultaneously.
 * This is similar to the UNIX 'tee' command, but for PrintWriters. Useful for logging purposes.
 */
public class TeePrintWriter extends PrintWriter {

    private final PrintWriter branch;

    /**
     * Constructs a TeePrintWriter given two PrintWriter instances.
     *
     * @param main The main PrintWriter.
     * @param branch The secondary PrintWriter.
     */
    public TeePrintWriter(PrintWriter main, PrintWriter branch) {
        super(main, true);
        this.branch = branch;
    }

    @Override
    public void write(char[] buf, int off, int len) {
        writeToBoth(buf, off, len);
    }

    @Override
    public void write(String s, int off, int len) {
        writeToBoth(s, off, len);
    }

    @Override
    public void write(int c) {
        writeToBoth(c);
    }

    @Override
    public void flush() {
        super.flush();
        branch.flush();
    }

    private void writeToBoth(char[] buf, int off, int len) {
        super.write(buf, off, len);
        super.flush();
        branch.write(buf, off, len);
        branch.flush();
    }

    private void writeToBoth(String s, int off, int len) {
        super.write(s, off, len);
        super.flush();
        branch.write(s, off, len);
        branch.flush();
    }

    private void writeToBoth(int c) {
        super.write(c);
        super.flush();
        branch.write(c);
        branch.flush();
    }
}