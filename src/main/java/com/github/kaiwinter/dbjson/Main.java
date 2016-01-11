package com.github.kaiwinter.dbjson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.github.kaiwinter.dbjson.config.Config;
import com.github.kaiwinter.dbjson.meta.Database;
import com.github.kaiwinter.dbjson.meta.Table;
import com.google.gson.stream.JsonWriter;

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

        JsonWriter writer = null;
        try (FileReader fileReader = new FileReader(file)) {

            OutputStream outputStream;
            if (commandLineArgs.outfile == null) {
                outputStream = System.out;
            } else {
                outputStream = new FileOutputStream(commandLineArgs.outfile);
            }

            writer = new JsonWriter(new PrintWriter(outputStream));

            Config config = Config.fromFile(fileReader);
            Database metadata = new Database(config);
            writer.beginArray();
            for (Table table : metadata.getTables()) {
                table.export(writer, true);
            }
            writer.endArray();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static class CommandLineArgs {
        @Option(name = "-config", usage = "the json configuration", required = true)
        public String config;

        @Option(name = "-outfile", usage = "file to write the output to. If not set result is written to standart out", required = false)
        public String outfile;
    }
}
