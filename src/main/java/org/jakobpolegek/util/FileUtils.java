package org.jakobpolegek.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {
    private FileUtils() {}

    public static String readFileAsString(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    public static void writeStringToFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content);
    }
}