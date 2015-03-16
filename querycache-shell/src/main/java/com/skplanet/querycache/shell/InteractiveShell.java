package com.skplanet.querycache.shell;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import jline.console.history.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by nazgul33 on 15. 3. 13.
 */
public class InteractiveShell {
    public static final Logger LOG = LoggerFactory.getLogger("qcshell");
    private static final String historyFile = System.getenv("HOME") + "/.qcshell_history";
    private static final int ERRNO_OK = 0;
    private static final int ERRNO_OTHER = 1;

    private ConsoleReader consoleReader;
    private boolean exit = false;
    QueryCacheShell.ParsedOptions options;
    private String prompt = "qcshell> ";

    private QueryExec queryExec;

    public InteractiveShell(QueryCacheShell.ParsedOptions options, OutputStreamWriter output) throws SQLException, IOException, Exception {
        this.options = options;
        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);
        // setup history
        ByteArrayOutputStream hist = null;
        if (new File(historyFile).isFile()) {
            try {
                // save the current contents of the history buffer. This gets
                // around a bug in JLine where setting the output before the
                // input will clobber the history input, but setting the
                // input before the output will cause the previous commands
                // to not be saved to the buffer.
                FileInputStream historyIn = new FileInputStream(historyFile);
                hist = new ByteArrayOutputStream();
                int n;
                while ((n = historyIn.read()) != -1) {
                    hist.write(n);
                }
                historyIn.close();
            } catch (Exception e) {
                LOG.error("reading history", e);
                // TODO: what?
            }
        }
        try {
            // now set the output for the history
            consoleReader.setHistory(new FileHistory(new File(historyFile)));
        } catch (Exception e) {
            LOG.error("setting up history", e);
            // TODO: what?
        }
        try {
            // now load in the previous history
            if (hist != null) {
                History h = consoleReader.getHistory();
                if (h instanceof FileHistory) {
                    ((FileHistory) consoleReader.getHistory()).load(new ByteArrayInputStream(hist
                            .toByteArray()));
                } else {
                    consoleReader.getHistory().add(hist.toString());
                }
            }
        } catch (Exception e) {
            LOG.error("loading history", e);
            // TODO: what?
        }

        // add shutdown hook to flush the history to history file
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                History h = consoleReader.getHistory();
                if (h instanceof FileHistory) {
                    try {
                        ((FileHistory) h).flush();
                        System.out.println();
                        System.err.println();
                    } catch (IOException e) {
                        LOG.error("flushing history", e);
                        // TODO: what?
                    }
                }
            }
        }));

        queryExec = new QueryExec(options, output);
        prompt = getPrompt(options.url);
    }

    private static String getPrompt(String url) {
        String prompt = "qcshell> ";
        if (url == null || url.length() == 0) {
            return prompt;
        }
        if (url.toUpperCase().startsWith("JDBC:")) {
            int idxProtocolSep = url.indexOf("://");
            return url.substring(5, idxProtocolSep) + "> ";
        }
        return prompt;
    }

    public static boolean isComment(String lineTrimmed) {
        // SQL92 comment prefix is "--"
        // beeline also supports shell-style "#" prefix
        return lineTrimmed.startsWith("#") || lineTrimmed.startsWith("--");
    }

    /**
     * Test whether a line requires a continuation.
     *
     * @param line
     *          the line to be tested
     *
     * @return true if continuation required
     */
    private boolean needsContinuation(String line) {
        String trimmed = line.trim();

        if (trimmed.length() == 0) {
            return false;
        }

        if (isComment(trimmed)) {
            return true;
        }

        return !trimmed.endsWith(";");
    }

    String getPrompt() {
        return prompt;
    }

    StringBuilder sqlBuilder = null;
    private boolean dispatch(String line) {
        if (line == null) {
            // exit
            exit = true;
            return true;
        }

        if (line.trim().length() == 0) {
            return true;
        }

        if (isComment(line)) {
            return true;
        }

        line = line.trim();

        // build multiline sql
        String multilineSql;
        if (!line.endsWith(";")) {
            if (sqlBuilder == null) {
                sqlBuilder = new StringBuilder();
            }
            sqlBuilder.append(line); sqlBuilder.append('\n');
            LOG.debug("buffered sql : {}", sqlBuilder.toString());
            return true;
        }

        if (sqlBuilder == null) {
            multilineSql = line;
        } else {
            sqlBuilder.append(line); sqlBuilder.append('\n');
            multilineSql = sqlBuilder.toString();
            LOG.debug("buffered sql : {}", multilineSql);
            sqlBuilder = null;
        }

        multilineSql = QueryCacheShell.removeComments(multilineSql);
        options.query_list = QueryCacheShell.queryTokenizer(multilineSql);

        return queryExec.exec();
    }

    public int execute() {
        while (!exit) {
            try {
                // Execute one instruction; terminate on executing a script if there is an error
                dispatch(consoleReader.readLine(getPrompt()));
            } catch (Throwable t) {
                LOG.error("Exception while executing query", t);
                return ERRNO_OTHER;
            }
        }
        return ERRNO_OK;
    }
}
