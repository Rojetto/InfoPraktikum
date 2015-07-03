import java.io.*;

public class DateiSimulator {
    public static void main(String[] args) {
        File circuitFile = new File(args[0]);
        File eventFile = new File(args[1]);

        try {
            String content = readFileContent(circuitFile);
            CirFileParser.parse(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String content = "";

        String line = reader.readLine();
        while (line != null) {
            content += line + "\n";
            line = reader.readLine();
        }

        return content;
    }
}
