package com.webank.wecross.console;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.exception.ConsoleException;
import org.junit.Assert;
import org.junit.Test;

public class ConsoleUtilsTest {

    @Test
    public void checkServerTest() throws ConsoleException {
        ConsoleUtils.checkServer("255.255.255.255:65535");
        ConsoleUtils.checkServer("0.0.0.0:0");
        String invalidServer = "255.255.255.255:65536";
        String errorMessage = "Illegal ip:port: " + invalidServer;
        try {
            ConsoleUtils.checkServer(invalidServer);
            Assert.fail();
        } catch (ConsoleException e) {
            Assert.assertEquals(errorMessage, e.getMessage());
        }

        invalidServer = "255.255.255.255:65535.";
        errorMessage = "Illegal ip:port: " + invalidServer;
        try {
            ConsoleUtils.checkServer(invalidServer);
            Assert.fail();
        } catch (ConsoleException e) {
            Assert.assertEquals(errorMessage, e.getMessage());
        }

        invalidServer = "-1.255.255.255:0";
        errorMessage = "Illegal ip:port: " + invalidServer;
        try {
            ConsoleUtils.checkServer(invalidServer);
            Assert.fail();
        } catch (ConsoleException e) {
            Assert.assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void isValidPathTest() {
        boolean isTrue;
        boolean isFalse;
        isTrue = ConsoleUtils.isValidPath("a.b.c");
        Assert.assertTrue(isTrue);

        isFalse = ConsoleUtils.isValidPath(".a.b.c");
        Assert.assertTrue(!isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b.c.");
        Assert.assertTrue(!isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b..c");
        Assert.assertTrue(!isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b");
        Assert.assertTrue(!isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b.c.d");
        Assert.assertTrue(!isFalse);
    }

    @Test
    public void parseRequestTest() throws Exception {
        String input = "r.exists";
        String output = "r.exists()";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.call Int getInt 1";
        output = "r.call \"Int\",\"getInt\",1";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.callInt getInt 1";
        output = "r.callInt \"getInt\",1";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.sendTransaction String getString \"hello world\"";
        output = "r.sendTransaction \"String\",\"getString\",\"hello world\"";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.sendTransaction String,Int setAndget \"hello world\" 1";
        output = "r.sendTransaction \"String,Int\",\"setAndget\",\"hello world\",1";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.getData \"hello world\"";
        output = "r.getData \"hello world\"";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));

        input = "r.setData \"hello world\" \"xi xi\"";
        output = "r.setData \"hello world\",\"xi xi\"";
        Assert.assertEquals(output, ConsoleUtils.parseRequest(ConsoleUtils.tokenizeCommand(input)));
    }
}
