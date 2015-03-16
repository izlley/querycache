package com.skplanet.querycache.shell;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * Created by nazgul33 on 15. 3. 12.
 */
public class TSVWriter {
    private String recordDelimiter = "\n";
    private Appendable output;
    public TSVWriter(Appendable output) {
        this.output = output;
    }

    public TSVWriter(Appendable output, String recordDelimiter) {
        this.output = output;
        this.recordDelimiter = recordDelimiter;
    }

    public void printRecord(Iterable<String> record) throws IOException {
        Iterator<String> iterator = record.iterator();
        String col;
        while (iterator.hasNext()) {
            col = iterator.next();
            output.append(col);
            if (iterator.hasNext()) {
                output.append('\t');
            }
            else {
                output.append(recordDelimiter);
            }
        }
    }
}
