package com.github.kaiwinter.dbjson;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.meta.Database;

public final class Main {

    public static void main(String[] args) throws IOException {
        CommandLineArgs commandLineArgs = parseCommandLineArgs(args);
        if (commandLineArgs != null) {
            start(commandLineArgs);
        }
    }

    private static CommandLineArgs parseCommandLineArgs(String[] args) {
        CommandLineArgs commandLineArgs = new CommandLineArgs();
        CmdLineParser commandLineArgsParser = new CmdLineParser(commandLineArgs);
        try {
            commandLineArgsParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            commandLineArgsParser.printUsage(System.err);
            return null;
        }

        return commandLineArgs;
    }

    private static void start(CommandLineArgs commandLineArgs) throws IOException {
        File file = new File(commandLineArgs.config);
        if (!file.exists()) {
            throw new IllegalArgumentException("Config cannot be opened: " + file.getAbsolutePath());
        }

        Config config = Config.fromFile(file);
        Database metadata = new Database(config);

        if (isQuerySetInConfig(config)) {
            exportAllTables(metadata, commandLineArgs.outfile);
        } else {
            exportQueryResult(metadata, commandLineArgs.outfile);
        }
    }

    private static boolean isQuerySetInConfig(Config config) {
        return config.query != null && !config.query.isEmpty();
    }

    private static void exportAllTables(Database metadata, String outfile) throws IOException {
        try (OutputStream stream = (outfile == null) ? System.out : new PrintStream(outfile)) {
            metadata.exportAllTables(stream);
        }
    }

    private static void exportQueryResult(Database metadata, String outfile) throws IOException {
        try (OutputStream stream = (outfile == null) ? System.out : new PrintStream(outfile)) {
            metadata.exportQueryResult(stream);
        }
    }

    private static class CommandLineArgs {
        @Option(name = "-config", usage = "the json configuration", required = true)
        public String config;

        @Option(name = "-outfile", usage = "file to write the output to. If not set result is written to standart out", required = false)
        public String outfile;
    }
}
