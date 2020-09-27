package com.webank.wecross.console;

import com.webank.wecross.console.common.ConsoleUtils;
import org.junit.Assert;
import org.junit.Test;

public class ConsoleUtilsTest {

    @Test
    public void testTokenizeCommand() throws Exception {
        String[] commands =
                ConsoleUtils.tokenizeCommand(
                        "  callByCNS [ ( ) ] HelloWorld.sol set \"{Hello}\" { \"a\":123, \"b\": 567 } \"  hello!  \"  ");

        Assert.assertEquals(commands.length, 7);
        Assert.assertArrayEquals(
                new String[] {
                    "callByCNS",
                    "[ ( ) ]",
                    "HelloWorld.sol",
                    "set",
                    "\"{Hello}\"",
                    "{ \"a\":123, \"b\": 567 }",
                    "\"  hello!  \"",
                },
                commands);
    }

    @Test
    public void isValidPathTest() {
        boolean isTrue;
        boolean isFalse;
        isTrue = ConsoleUtils.isValidPath("a.b.c");
        Assert.assertTrue(isTrue);

        isFalse = ConsoleUtils.isValidPath(".a.b.c");
        Assert.assertFalse(isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b.c.");
        Assert.assertFalse(isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b..c");
        Assert.assertFalse(isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b");
        Assert.assertFalse(isFalse);

        isFalse = ConsoleUtils.isValidPath("a.b.c.d");
        Assert.assertFalse(isFalse);
    }

    @Test
    public void parseRequestTest() throws Exception {
        String input = "r.status";
        String output = "r.status()";
        Assert.assertEquals(output, ConsoleUtils.parseCommand(ConsoleUtils.tokenizeCommand(input)));

        input = "r.detail";
        output = "r.detail()";
        Assert.assertEquals(output, ConsoleUtils.parseCommand(ConsoleUtils.tokenizeCommand(input)));

        input = "r.call Int getInt";
        output = "r.call \"Int\",\"getInt\"";
        Assert.assertEquals(output, ConsoleUtils.parseCommand(ConsoleUtils.tokenizeCommand(input)));

        input = "r.sendTransaction String getString \"hello world\"";
        output = "r.sendTransaction \"String\",\"getString\",\"hello world\"";
        Assert.assertEquals(output, ConsoleUtils.parseCommand(ConsoleUtils.tokenizeCommand(input)));

//        input = "r = WeCross.getResource a.b.c";
//        output = "r = WeCross.getResource \"a.b.c\"";
//        Assert.assertEquals(output, ConsoleUtils.parseCommand(ConsoleUtils.tokenizeCommand(input)));
    }
}
