package com.skplanet.querycache.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by nazgul33 on 15. 3. 13.
 */
public class PrettyWriter {
    public static final int MAX_QUEUE = 30;
    private String recordDelimiter = "\n";
    private Appendable output;
    private ArrayList<Collection<String>> recordList = new ArrayList<Collection<String>>(MAX_QUEUE);
    private Collection<String> header = null;

    public PrettyWriter(Appendable output) {
        this.output = output;
    }

    public PrettyWriter(Appendable output, String recordDelimiter) {
        this.output = output;
        this.recordDelimiter = recordDelimiter;
    }

    private boolean queueRecord(Collection<String> record) {
        if ( recordList.size() < MAX_QUEUE ) {
            ArrayList<String> newR = new ArrayList<String>();
            newR.addAll(record);
            recordList.add(newR);
            return true;
        }

        return false;
    }

    private int maxColumnWidth[] = null;
    public void flushRecords() throws IOException {
        if (recordList.size() == 0)
            return;

        // calc max column width
        for (Iterable<String> record: recordList) {
            Iterator<String> iterator = record.iterator();
            int col = 0;
            while (iterator.hasNext()) {
                String colStr = iterator.next();
                if (colStr == null) {
                    colStr = "null";
                } else {
                    colStr = colStr.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "  ");
                }
                int lenCol = ConsoleWidthUtils.getConsoleWidth(colStr);
                if (lenCol > maxColumnWidth[col]) {
                    maxColumnWidth[col] = lenCol;
                }
                col++;
            }
        }

        // build table
        // header
        StringBuilder rowBuilder = new StringBuilder(1024);
        for (int i = 0; i < maxColumnWidth.length; i++) {
            rowBuilder.append('+');
            for (int j = 0; j < maxColumnWidth[i] + 2; j++) rowBuilder.append('-');
        }
        rowBuilder.append('+');
        String hr = rowBuilder.toString();

        output.append(hr); output.append(this.recordDelimiter);

        // header
        rowBuilder = new StringBuilder(1024);
        Iterator<String> iterator = header.iterator();
        int col = 0;
        while(iterator.hasNext()) {
            String colStr = iterator.next();
            rowBuilder.append("| ");
            rowBuilder.append(colStr);
            for (int j = ConsoleWidthUtils.getConsoleWidth(colStr); j < maxColumnWidth[col] + 1; j++) rowBuilder.append(' ');
            col++;
        }
        rowBuilder.append('|');
        output.append(rowBuilder.toString()); output.append(this.recordDelimiter);

        output.append(hr); output.append(this.recordDelimiter);

        // records
        for (Iterable<String> record: recordList) {
            rowBuilder = new StringBuilder(1024);
            iterator = record.iterator();
            col = 0;
            while (iterator.hasNext()) {
                String colStr = iterator.next();
                if (colStr == null) colStr = "null";
                rowBuilder.append("| ");
                rowBuilder.append(colStr);
                for (int j = ConsoleWidthUtils.getConsoleWidth(colStr); j < maxColumnWidth[col] + 1; j++) rowBuilder.append(' ');
                col++;
            }
            rowBuilder.append('|');
            output.append(rowBuilder.toString()); output.append(this.recordDelimiter);
        }
        output.append(hr); output.append(this.recordDelimiter);
        output.append(this.recordDelimiter);

        recordList.clear();
    }

    public void setHeader(Collection<String> header) {
        this.header = new ArrayList<String>();
        this.header.addAll(header);
        // count
        int count = 0;
        Iterator<String> iterator = header.iterator();
        while(iterator.hasNext()) {
            iterator.next();
            count++;
        }

        maxColumnWidth = new int[count];
        iterator = header.iterator();
        int col = 0;
        while(iterator.hasNext()) {
            String colStr = iterator.next();
            if (colStr == null) colStr = "null";
            maxColumnWidth[col] = ConsoleWidthUtils.getConsoleWidth(colStr);
            col++;
        }
    }

    public void printRecord(Collection<String> record) throws IOException {
        boolean queued = queueRecord(record);
        if (!queued) {
            flushRecords();
            queueRecord(record);
        }
    }
}
