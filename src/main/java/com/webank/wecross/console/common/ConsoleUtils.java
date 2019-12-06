package com.webank.wecross.console.common;

import com.webank.wecross.console.exception.ConsoleException;
import com.webank.wecross.console.exception.Status;
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

    public static void checkServer(String server) throws ConsoleException {
        String errorMessage = "Illegal ip:port: " + server;

        if (server == null
                || server.length() == 0
                || server.charAt(0) == '.'
                || server.endsWith(".")) {
            throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
        }

        String ipUnits[] = server.split("\\.");
        if (ipUnits.length != 4) {
            throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
        }

        for (int i = 0; i < 3; ++i) {
            try {
                int ipUnit = Integer.parseInt(ipUnits[i]);
                if (ipUnit < 0 || ipUnit > 255) {
                    throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
                }
            } catch (NumberFormatException e) {
                throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
            }
        }

        String ipAndPort[] = ipUnits[3].split(":");
        if (ipAndPort.length != 2) {
            throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
        }

        try {
            int ipUnit = Integer.parseInt(ipAndPort[0]);
            if (ipUnit < 0 || ipUnit > 255) {
                throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
        }

        try {
            int port = Integer.parseInt(ipAndPort[1]);
            if (port < 0 || port > 65535) {
                throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new ConsoleException(Status.ILLEGAL_SERVER, errorMessage);
        }
    }

    public static boolean isValidPath(String path) {
        if (path == null || path.length() == 0 || path.charAt(0) == '.' || path.endsWith(".")) {
            return false;
        }

        String unit[] = path.split("\\.");
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

    public static String[] parseRetTypes(String retTypes) {
        String[] types = retTypes.split(",");
        String[] result = new String[types.length];
        int i = 0;
        for (String type : types) {
            result[i++] = type.trim();
        }
        return result;
    }

    public static boolean isValidPathVar(String path, Map<String, String> pathMaps) {
        if (pathMaps.containsKey(path)) {
            return true;
        }
        return false;
    }

    // parse variables and save path variables
    public static Boolean parseVars(
            String params[],
            Set<String> resourceVars,
            Set<String> pathVars,
            Map<String, String> pathMaps) {
        int length = params.length;
        if (length < 3 || params[0].contains("\"") || params[0].contains("\'")) {
            return false;
        }

        if (params[1].equals("=")) {
            if (params[2].equals("WeCross.getResource")) {
                if (length != 4) {
                    if (length > 5 || !params[4].equals(" ")) {
                        return false;
                    }
                }
                if (pathMaps.keySet().contains(params[3])) {
                    resourceVars.add(params[0]);
                    return true;
                }

                String out = parseString(params[3]);
                if (ConsoleUtils.isValidPath(out)) {
                    resourceVars.add(params[0]);
                    return true;
                }
            } else {
                if (length != 3) {
                    if (length > 4 || !params[3].equals(" ")) {
                        return false;
                    }
                }
                String out = parseString(params[2]);
                if (ConsoleUtils.isValidPath(out)) {
                    pathVars.add(params[0]);
                    pathMaps.put(params[0], out);
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

    // parse args as string or int
    public static Object[] parseArgs(String params[], int start) throws ConsoleException {
        int length = params.length;
        Object ret[] = new Object[length - start];
        int i = 0, j = start;
        for (; j < length; ++j) {
            int len = params[j].length();
            if (params[j].charAt(0) == '\"' && params[j].charAt(len - 1) == '\"'
                    || params[j].charAt(0) == '\'' && params[j].charAt(len - 1) == '\'') {
                // as string
                ret[i++] = params[j].substring(1, len - 1);
            } else {
                // as int
                try {
                    ret[i++] = Integer.parseInt(params[j]);
                } catch (Exception e) {
                    String errorMessage =
                            "Cannot convert "
                                    + params[j]
                                    + " to int\nAllowed: -2147483648 to 2147483647\n";
                    throw new ConsoleException(Status.ILLEGAL_PARAM, errorMessage);
                }
            }
        }
        return ret;
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
            sb.append("    ");
        }
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

    private static Boolean isGrooveyCommand(String command) {
        List<String> grovvyCommands =
                Arrays.asList(
                        ".callInt",
                        ".callIntArray",
                        ".callString",
                        ".callStringArray",
                        ".sendTransactionInt",
                        ".sendTransactionIntArray",
                        ".sendTransactionString",
                        ".sendTransactionStringArray");
        for (String grooveyCommand : grovvyCommands) {
            if (command.endsWith(grooveyCommand)) {
                return true;
            }
        }
        return false;
    }

    public static String parseRequest(String[] params) throws ConsoleException {

        String result = "";
        Boolean isArgs = false;
        int length = params.length;
        int start = 0;
        int startArgs = 1;
        if (length != 0) {
            if (params[0].endsWith(".call") || params[0].endsWith(".sendTransaction")) {
                isArgs = true;
                if (length < 2) {
                    throw new ConsoleException(Status.TYPES_MISSING, "Types is missing");
                }
                if (length < 3) {
                    throw new ConsoleException(Status.METHOD_MISSING, "Method is missing");
                }

                params[1] = "\"" + parseString(params[1]) + "\"";
                params[2] = "\"" + parseString(params[2]) + "\"";
                start = 1;
                startArgs = 3;
                result = params[0] + " ";
            } else if (isGrooveyCommand(params[0])) {
                isArgs = true;
                if (length < 2) {
                    throw new ConsoleException(Status.METHOD_MISSING, "Method is missing");
                }

                params[1] = "\"" + parseString(params[1]) + "\"";
                start = 1;
                startArgs = 2;
                result = params[0] + " ";
            } else if (params[0].endsWith(".getData") || params[0].endsWith(".setData")) {
                isArgs = true;
                start = 1;
                startArgs = 1;
                result = params[0] + " ";
            } else if (params[0].endsWith(".exists")) {
                if (length != 1) {
                    throw new ConsoleException(Status.INTERNAL_ERROR, "Redundant parameters");
                }
                result = params[0] + "()";
                return result;
            }

            for (; start < length; ++start) {
                String temp = parseString(params[start]);
                if (!isArgs && ConsoleUtils.isValidPath(temp)) {
                    result += ("\"" + temp + "\"" + " ");
                } else if (isArgs) {
                    // args1 args2 ...
                    if (start >= startArgs) {
                        if (temp.equals(params[start])) {
                            // as int
                            try {
                                Integer.parseInt(temp);
                            } catch (Exception e) {
                                String errorMessage =
                                        "Cannot convert "
                                                + temp
                                                + " to int\nAllowed: -2147483648 to 2147483647\n";
                                throw new ConsoleException(Status.ILLEGAL_PARAM, errorMessage);
                            }
                        }
                    }
                    result += (params[start] + ",");
                } else {
                    result += (params[start] + " ");
                }
            }

            result = result.substring(0, result.length() - 1);
        }
        //        System.out.println(result);
        return result;
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
