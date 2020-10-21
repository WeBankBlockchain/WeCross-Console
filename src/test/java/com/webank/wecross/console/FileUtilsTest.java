package com.webank.wecross.console;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.TransactionInfo;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {
    @Test
    public void readFileContentTest() throws Exception {
        String fileName = "test";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Hello World");
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
}
