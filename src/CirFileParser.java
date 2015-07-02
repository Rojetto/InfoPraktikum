import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CirFileParser {
    private static Map<Pattern, Statement> patterns = new HashMap<>();
    private Pattern statementPattern = Pattern.compile("\\s*([^;]+?)\\s*;");

    static {
        patterns.put(Pattern.compile("(Signal|Input|Output)\\s+([a-zA-Z0-9]+\\s*(?:\\s*,\\s*[a-zA-Z0-9]+)*)"), Statement.SIGNAL);
        patterns.put(Pattern.compile("Gate\\s+([a-zA-Z0-9]+)\\s+([A-Z]+)(\\d*)\\s+Delay\\s+(\\d+)"), Statement.GATE);
        patterns.put(Pattern.compile("([a-zA-Z0-9]+)\\s*\\.\\s*([a-zA-Z0-9]+)\\s*=\\s*([a-zA-Z0-9]+)"), Statement.CONNECTION);
    }

    private enum Statement {
        SIGNAL, GATE, CONNECTION
    }

    public Circuit parse(String content) {
        Circuit circuit = new Circuit();

        content = content.replaceAll("#.+", ""); // Remove all comments
        content = content.replaceAll("\\n", ""); // Remove linebreaks

        List<Statement> statements = new ArrayList<>();

        String statement = firstStatement(content);
        while (statement != null) {
            Statement s = identifyStatement(statement);
            System.out.println(s + ": " + statement);

            content = content.replaceFirst(statement, "");
            statement = firstStatement(content);
        }

        return circuit;
    }

    private String firstStatement(String s) {
        Matcher m = statementPattern.matcher(s);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private Statement identifyStatement(String s) {
        for (Pattern p : patterns.keySet()) {
            Matcher m = p.matcher(s);
            if (m.matches()) {
                return patterns.get(p);
            }
        }

        return null;
    }
}
