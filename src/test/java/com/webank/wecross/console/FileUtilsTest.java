package com.webank.wecross.console;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.TransactionInfo;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {
    @Test
    public void readFileContentTest() throws Exception {
        String fileName = "test";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Hello World");
        } catch (IOException e) {
            System.out.println(e);
        }

        String readFileContent = FileUtils.readFileContent(fileName);
        Assert.assertEquals("Hello World", readFileContent);
        Assert.assertThrows(IOException.class, () -> FileUtils.readFileContent(".."));
        FileUtils.cleanFile(System.getProperty("user.dir"), fileName);
    }

    @Test
    public void getTomlTest() throws Exception {
        String fileName = "test.toml";
        TransactionInfo transactionInfo = new TransactionInfo("123", Arrays.asList("123", "456"));
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(transactionInfo, new File(fileName));
        } catch (IOException e) {
            System.out.println(e);
        }

        Toml toml = FileUtils.getToml(fileName);
        String txID = FileUtils.getTransactionID(toml);
        List<String> paths = FileUtils.getTransactionPath(toml);

        Assert.assertEquals("123", txID);
        Assert.assertEquals("123", paths.get(0));
        Assert.assertEquals("456", paths.get(1));
        Assert.assertThrows(
                WeCrossConsoleException.class,
                () -> FileUtils.getToml("classpath:" + fileName + "1"));
        Assert.assertThrows(RuntimeException.class, () -> FileUtils.getToml(fileName + "1"));

        FileUtils.cleanFile(System.getProperty("user.dir"), fileName);
    }

    @Test
    public void getTransactionInfoMapTest() throws Exception {
        String fileName1 = "test-123.toml";
        String fileName2 = "test-234.toml";
        TransactionInfo transactionInfo1 = new TransactionInfo("123", Arrays.asList("123", "456"));
        TransactionInfo transactionInfo2 = new TransactionInfo("234", Arrays.asList("234", "345"));
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(transactionInfo1, new File(fileName1));
            writer.write(transactionInfo2, new File(fileName2));
        } catch (IOException e) {
            System.out.println(e);
        }
        File dir = new File(System.getProperty("user.dir"));
        FilenameFilter filter =
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith("test-");
                    }
                };
        String[] children = dir.list(filter);
        Assert.assertNotNull(children);
        Assert.assertEquals(2, children.length);
        Arrays.sort(children);
        List<String> txIDs = new ArrayList<>();
        List<List<String>> path = new ArrayList<>();
        for (String child : children) {
            Toml toml = FileUtils.getToml(child);
            String txID = FileUtils.getTransactionID(toml);
            List<String> paths = FileUtils.getTransactionPath(toml);
            txIDs.add(txID);
            path.add(paths);
        }

        Assert.assertEquals("123", txIDs.get(0));
        Assert.assertEquals("123", path.get(0).get(0));
        Assert.assertEquals("456", path.get(0).get(1));

        Assert.assertEquals("234", txIDs.get(1));
        Assert.assertEquals("234", path.get(1).get(0));
        Assert.assertEquals("345", path.get(1).get(1));

        FileUtils.cleanFile(System.getProperty("user.dir"), fileName1);
        FileUtils.cleanFile(System.getProperty("user.dir"), fileName2);
    }
}
