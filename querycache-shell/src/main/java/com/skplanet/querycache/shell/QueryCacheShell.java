package com.skplanet.querycache.shell;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nazgul33 on 15. 3. 6.
 */
public class QueryCacheShell {
    public static final Logger LOG = LoggerFactory.getLogger("qcshell");

    public static class ParsedOptions {
        public String url = null;
        public String user = null;
        public String passwd = null;
        public String query = null;
        public String file = null;
        public List<String> query_list;
        public String outputFile = null;
        public int outputType = QueryExec.OUTPUT_PRETTY;
    }

    public static Options initOptions() {
        Options options = new Options();
        options.addOption("u", "url", true, "JDBC url to data backend");
        options.addOption("n", "username", true, "username to connect to data backend");
        options.addOption("p", "password", true, "password for specified username");
        options.addOption("e", "query", true, "query string to run : non-interactive session");
        options.addOption("f", "file", true, "sql file to run : non-interactive session. Lines starting with # are ignored. (e.g. comments)");
        options.addOption("t", "output-type", true, "one of { csv, tsv, default }");
        options.addOption("o", "output-file", true, "path to a file where query result should be stored");
        return options;
    }

    public static String removeComments(String str) {
        StringBuilder strBuilder = new StringBuilder();
        for (String subStr: str.split("\\r?\\n|\\r")) {
            String trimmed = subStr.trim();
            if (!InteractiveShell.isComment(trimmed)) {
                if (subStr.length()>0) {
                    strBuilder.append(subStr);
                    strBuilder.append(' ');
                }
            }
        }
        return strBuilder.toString();
    }

    public static List<String> queryTokenizer(String str) {
        ArrayList<String> tokens = new ArrayList<>();
        int len = str.length();
        int start=0;
        int end=-1;
        char currentQuot = '\0';
        for (int i=0; i<len; i++) {
            char curChar = str.charAt(i);
            if (currentQuot == '\0') {
                if (curChar == ';') {
                    tokens.add(str.substring(start, i).trim());
                    start=i+1;
                    continue;
                }

                if (curChar == '`' || curChar == '\'' || curChar == '\"') {
                    // start of quotation block
                    currentQuot = curChar;
                    continue;
                }
            }
            else {
                if (curChar == currentQuot) {
                    // filter backslash-escaped quot
                    if (str.charAt(i-1) != '\\') {
                        // clear currentQuot
                        currentQuot='\0';
                        continue;
                    }
                }
            }
        }
        if (start<str.length()) {
            String q = str.substring(start).trim();
            if (q.length()>0) {
                tokens.add(q);
            }
        }
        return tokens;
    }

    public static void main(String argv[]) {
        ParsedOptions opts = new ParsedOptions();
        Options o = initOptions();
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        boolean interactive = false;

        try {
            String oVal;
            line = parser.parse(o, argv);

            opts.url = line.getOptionValue("url");
            opts.user = line.getOptionValue("username");
            opts.passwd = line.getOptionValue("password");
            opts.query = line.getOptionValue("query");
            opts.file = line.getOptionValue("file");
            opts.outputFile = line.getOptionValue("output-file");

            oVal = line.getOptionValue("output-type");
            if (oVal != null) {
                opts.outputType = QueryExec.getOutputTypeFromString(oVal);
            }
        } catch (ParseException e) {
            LOG.error("parsing command line options", e);
        } finally {
            boolean needHelp = false;
            if (opts.query == null && opts.file == null) {
                interactive = true;
            }

            if (opts.url == null || opts.user == null) {
                LOG.error("-u and -n options are mandatory");
                needHelp = true;
            } else if (opts.query != null && opts.file != null) {
                LOG.error("-e and -f are mutually exclusive options.");
                needHelp = true;
            } else if (opts.outputType > QueryExec.OUTPUT_MAX || opts.outputType < 0) {
                LOG.error("-t : specify one of {tsv, csv, default}");
                needHelp = true;
            }

            if (needHelp) {
                HelpFormatter fmt = new HelpFormatter();
                fmt.printHelp("qcshell", o);
                System.exit(1);
            }
        }

        // load jdbc driver
        try {
            String className = "com.skplanet.querycache.jdbc.QCDriver";
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOG.error("JDBC driver not found", e);
            System.exit(1);
        }

        // prepare output stream
        OutputStreamWriter writer = null;
        if ( opts.outputFile != null ) {
            try {
                FileOutputStream ofs = new FileOutputStream(opts.outputFile, false);
                writer = new OutputStreamWriter(ofs, "UTF-8");
            } catch (IOException e) {
                LOG.error("opening file {}", opts.outputFile, e);
                System.exit(1);
            }
        } else {
            writer = new OutputStreamWriter(System.out);
        }

        if (interactive) {
            InteractiveShell iShell = null;
            try {
                iShell = new InteractiveShell(opts, writer);
            } catch (Throwable t) {
                LOG.error("Exception :", t);
            }

            if (iShell != null) {
                iShell.execute();
                try {
                    writer.append('\n');
                    writer.flush();
                } catch (IOException e) {
                    // ignore
                }
            }
        } else {
            QueryExec exec = null;
            // prepare query string
            if (opts.file != null) {
                InputStreamReader isr = null;
                try {
                    StringBuilder sb = new StringBuilder();
                    isr = new InputStreamReader(new FileInputStream(opts.file), "UTF-8");
                    char buf[] = new char[1024];
                    while ( isr.read(buf) > 0 ) {
                        sb.append(buf);
                    }
                    isr.close();
                    opts.query = removeComments(sb.toString());
                } catch (Exception e) {
                    LOG.error("Reading sql file", e);
                    System.exit(1);
                }
            }
            if (opts.query != null) {
                opts.query_list = QueryCacheShell.queryTokenizer(opts.query);
            }

            try {
                exec = new QueryExec(opts, writer);
                exec.exec();
            } catch (SQLException e) {
                LOG.error("SQLException {} - {}", e.getSQLState(), e.getMessage(), e);
            } catch (IOException e) {
                LOG.error("IOException", e);
            } finally {
                if (exec != null) {
                    exec.close();
                }
            }
        }

        if ( opts.outputFile != null ) {
            try {
                writer.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
