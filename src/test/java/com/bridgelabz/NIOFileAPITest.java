package com.bridgelabz;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class NIOFileAPITest {
    public static final String HOME = System.getProperty("user.home");
    public static final String PLAY_WITH_NIO = "TempPlayground";

    @Test
    public void givenPath_whenChecked_thenConfirm() throws IOException {
        //check file Exists
        Path homePath = Paths.get(HOME);
        Assert.assertTrue(Files.exists(homePath));

        //Delete file and check file not exist
        Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        if (Files.exists(playPath))
            FileUtils.deleteFiles(playPath.toFile());
        Assert.assertTrue(Files.notExists(playPath));

        //Create Directory
        Files.createDirectory(playPath);
        Assert.assertTrue(Files.exists(playPath));

        //create file
        IntStream.range(1, 10).forEach(counter -> {
            Path tempFile = Paths.get(playPath + "/temp" + counter);
            Assert.assertTrue(Files.notExists(tempFile));
            try {
                Files.createFile(tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Assert.assertTrue(Files.exists(tempFile));
        });

        Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
        Files.newDirectoryStream(playPath).forEach(System.out::println);
        Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp")).forEach(System.out::println);

    }
}