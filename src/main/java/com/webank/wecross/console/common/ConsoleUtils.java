package com.webank.wecross.console.common;

import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class ConsoleUtils {

    public static boolean isValidPath(String path) {
        if (path == null || path.length() == 0 || path.charAt(0) == '.' || path.endsWith(".")) {
            return false;
        }
        String[] unit = path.split("\\.");
        if (unit.length == 3) {
            String templateUrl = "http://127.0.0.1:8080/" + path.replace('.', '/');
            try {
                new URL(templateUrl);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isValidPathVar(String path, Map<String, String> pathMaps) {
        return pathMaps.containsKey(path);
    }
    // parse variables and save path variables
    public static Boolean parseVars(
            String[] params,
            Set<String> resourceVars,
            Set<String> pathVars,
            Map<String, String> pathMaps) {
        int length = params.length;
        if (length < 3 || params[0].contains("\"") || params[0].contains("'")) {
            return false;
        }
        if (params[1].equals("=")) {
            if (params[2].equals("WeCross.getResource")) {
                if (length != 5) {
                    return false;
                }
                if (pathMaps.keySet().contains(params[3])) {
                    resourceVars.add(params[0]);
                    return true;
                }
                String path = parseString(params[3]);
                if (ConsoleUtils.isValidPath(path)) {
                    pathVars.add(path);
                    resourceVars.add(params[0]);
                    return true;
                }
            } else {
                if (length != 3) {
                    return false;
                }
                String path = parseString(params[2]);
                if (ConsoleUtils.isValidPath(path)) {
                    pathVars.add(params[0]);
                    pathMaps.put(params[0], path);
                    return true;
                }
            }
        }
        return false;
    }

    // remove "" or '' of string
    public static String parseString(String input) {
        int len = input.length();
        if (len < 2) {
            return input;
        }
        if (input.charAt(0) == '\"' && input.charAt(len - 1) == '\"'
                || input.charAt(0) == '\'' && input.charAt(len - 1) == '\'') {
            return input.substring(1, len - 1);
        }
        return input;
    }

    public static String[] parseAgrs(String[] args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = parseString(args[i]);
        }
        return result;
    }

    public static void printJson(String jsonStr) {
        System.out.println(formatJson(jsonStr));
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        jsonStr = jsonStr.replace("\\n", "");
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                case ' ':
                    if (',' != jsonStr.charAt(i - 1)) {
                        sb.append(current);
                    }
                    break;
                case '\\':
                    break;
                default:
                    if (!(current == " ".charAt(0))) sb.append(current);
            }
        }
        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
    }

    public static String parsePath(String[] params, Map<String, String> pathMaps) {
        String path = parseString(params[1]);
        if (!isValidPath(path)) {
            if (!isValidPathVar(params[1], pathMaps)) {
                System.out.println("Please provide a valid path");
                HelpInfo.statusHelp();
                return null;
            }
            path = pathMaps.get(params[1]);
        }
        return path;
    }

    private static class CommandTokenizer extends StreamTokenizer {
        public CommandTokenizer(Reader r) {
            super(r);
            resetSyntax();
            // Invisible ASCII characters.
            whitespaceChars(0x00, 0x20);
            // All visible ASCII characters.
            wordChars(0x21, 0x7E);
            // Other UTF8 characters.
            wordChars(0xA0, 0xFF);
            // Uncomment this to allow comments in the command.
            // commentChar('/');
            // Allow both types of quoted strings, e.g. 'abc' and "abc".
            quoteChar('\'');
            quoteChar('"');
        }

        public void parseNumbers() {}
    }

    public static String[] tokenizeCommand(String command) throws Exception {
        // example: callByCNS HelloWorld.sol set"Hello" parse [callByCNS, HelloWorld.sol,
        // set"Hello"]
        List<String> tokens1 = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        while (stringTokenizer.hasMoreTokens()) {
            tokens1.add(stringTokenizer.nextToken());
        }
        // example: callByCNS HelloWorld.sol set"Hello" parse [callByCNS, HelloWorld.sol, set,
        // "Hello"]
        List<String> tokens2 = new ArrayList<>();
        StreamTokenizer tokenizer = new CommandTokenizer(new StringReader(command));
        int token = tokenizer.nextToken();
        while (token != StreamTokenizer.TT_EOF) {
            switch (token) {
                case StreamTokenizer.TT_EOL:
                    // Ignore \n character.
                    break;
                case StreamTokenizer.TT_WORD:
                    tokens2.add(tokenizer.sval);
                    break;
                case '\'':
                    // If the tailing ' is missing, it will add a tailing ' to it.
                    // E.g. 'abc -> 'abc'
                    tokens2.add(String.format("'%s'", tokenizer.sval));
                    break;
                case '"':
                    // If the tailing " is missing, it will add a tailing ' to it.
                    // E.g. "abc -> "abc"
                    tokens2.add(String.format("\"%s\"", tokenizer.sval));
                    break;
                default:
                    // Ignore all other unknown characters.
                    throw new RuntimeException("unexpected input tokens " + token);
            }
            token = tokenizer.nextToken();
        }
        return tokens1.size() <= tokens2.size()
                ? tokens1.toArray(new String[tokens1.size()])
                : tokens2.toArray(new String[tokens2.size()]);
    }

    public static String parseCommand(String[] params) throws WeCrossConsoleException {
        StringBuilder result = new StringBuilder();
        boolean isArgs = false;
        int length = params.length;
        int start = 0;
        if (length != 0) {
            if (params[0].endsWith(".call") || params[0].endsWith(".sendTransaction")) {
                isArgs = true;
                if (length < 2) {
                    throw new WeCrossConsoleException(
                            ErrorCode.METHOD_MISSING, "Method is missing");
                }
                start = 1;
                result = new StringBuilder(params[0] + " ");
            } else if (params[0].endsWith(".status") || params[0].endsWith(".detail")) {
                if (length != 1) {
                    throw new WeCrossConsoleException(
                            ErrorCode.ILLEGAL_PARAM, "Redundant parameters");
                }
                result = new StringBuilder(params[0] + "()");
                return result.toString();
            } else if (length > 3 && params[2].equals("WeCross.getResource")) {
                if (length != 5) {
                    throw new WeCrossConsoleException(
                            ErrorCode.ILLEGAL_PARAM, "Parameter:q error: [path] [accountName]");
                }
                result = new StringBuilder(params[0] + " " + params[1] + " " + params[2] + " ");
                String path = params[3];
                if (ConsoleUtils.isValidPath(parseString(params[3]))) {
                    path = "\"" + path + "\"";
                }
                result.append(path).append(",").append("\"").append(params[4]).append("\"");
                return result.toString();
            }
            for (; start < length; ++start) {
                String temp = parseString(params[start]);
                if (!isArgs && ConsoleUtils.isValidPath(temp)) {
                    result.append("\"").append(temp).append("\"").append(" ");
                } else if (isArgs) {
                    result.append("\"").append(parseString(params[start])).append("\"").append(",");
                } else {
                    result.append(params[start]).append(" ");
                }
            }
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        // System.out.println(result);
        return result.toString();
    }

    public static void printTransactionResponse(TransactionResponse response, boolean isCall) {
        if (response == null) {
            System.out.println("Response: null");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            printJson(response.toString());
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            printJson(response.getReceipt().toString());
        } else {
            if (!isCall) {
                System.out.println("Txhash  : " + response.getReceipt().getHash());
                System.out.println("BlockNum: " + response.getReceipt().getBlockNumber());
                System.out.println(
                        "Result  : " + Arrays.toString(response.getReceipt().getResult()));
            } else {
                System.out.println("Result: " + Arrays.toString(response.getReceipt().getResult()));
            }
        }
    }

    public static void singleLine() {
        System.out.println(
                "---------------------------------------------------------------------------------------------");
    }

    public static void doubleLine() {
        System.out.println(
                "=============================================================================================");
    }
}
