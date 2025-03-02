package com.fixmycar.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to handle text file operations for the FixMyCar application.
 */
public class TextFileOfTextView {
    
    private static final Logger LOGGER = Logger.getLogger(TextFileOfTextView.class.getName());
    
    private final File textFile;
    private final List<String> lineFile;
    
    /**
     * Constructor initializes the file path and reads the file contents.
     * 
     * @param filePath Path to the text file
     */
    public TextFileOfTextView(String filePath) {
        this.textFile = new File(filePath);
        this.lineFile = new ArrayList<>();
        
        if (isExistsFile()) {
            readAllText();
            parseLine();
        } else {
            LOGGER.log(Level.WARNING, "File does not exist: {0}", filePath);
        }
    }
    
    /**
     * Checks if the specified text file exists.
     * 
     * @return true if file exists, false otherwise
     */
    public boolean isExistsFile() {
        Path path = Paths.get(textFile.getAbsolutePath());
        return Files.exists(path);
    }
    
    /**
     * Reads all lines from the text file and stores them in lineFile list.
     */
    private void readAllText() {
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineFile.add(line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Writes a line to the text file.
     * 
     * @param line The line to write
     * @param append If true, appends to existing file; if false, overwrites
     * @return true if writing was successful, false otherwise
     */
    public boolean writeLine(String line, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, append))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Writes multiple lines to the text file.
     * 
     * @param lines List of lines to write
     * @param append If true, appends to existing file; if false, overwrites
     * @return true if writing was successful, false otherwise
     */
    public boolean writeLines(List<String> lines, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, append))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Parses the lines from the text file, focusing on lines that are either empty
     * or contain "FMC" substring.
     */
    private void parseLine() {
        List<String> parsedLines = new ArrayList<>();
        
        for (String line : lineFile) {
            if (line.trim().isEmpty() || line.contains("FMC")) {
                parsedLines.add(line);
            }
        }
        
        lineFile.clear();
        lineFile.addAll(parsedLines);
    }
    
    /**
     * Gets a copy of the line list.
     * 
     * @return A new list containing all lines
     */
    public List<String> getLines() {
        return new ArrayList<>(lineFile);
    }
    
    /**
     * Gets the text file.
     * 
     * @return The File object
     */
    public File getTextFile() {
        return textFile;
    }
    
    /**
     * Returns a string representation of the text file's contents.
     * 
     * @return String representation of file contents
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String line : lineFile) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
