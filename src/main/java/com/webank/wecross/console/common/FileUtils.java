package com.webank.wecross.console.common;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {

    public static String readFileContent(String fileName) throws Exception {
        try {
            Path path;

            if (fileName.indexOf("classpath:") != 0) {
                path = Paths.get(fileName);
            } else {
                // Start with "classpath:"
                PathMatchingResourcePatternResolver resolver =
                        new PathMatchingResourcePatternResolver();
                path = Paths.get(resolver.getResource(fileName).getURI());
            }

            String content = new String(Files.readAllBytes(path));
            return content;
        } catch (Exception e) {
            throw new Exception("Read file error: " + e);
        }
    }

    public static String readFileToBytesString(String filePath) throws Exception {
        String content = readFileContent(filePath);
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }
}
