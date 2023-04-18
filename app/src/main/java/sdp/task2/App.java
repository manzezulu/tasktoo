package sdp.task2;

import java.io.File;
import java.util.Scanner;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class App {

    private static final String[] FIELD_NAMES = {"name", "postalZip", "region", "country", "address", "list"};

    public static void main(String[] args) {
        // Prompt the user to select which fields to output
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which fields would you like to output?");
        System.out.println("1. Name");
        System.out.println("2. Postal ZIP code");
        System.out.println("3. Region");
        System.out.println("4. Country");
        System.out.println("5. Address");
        System.out.println("6. List");
        System.out.print("Enter the numbers of the fields separated by commas: ");
        String[] fieldNumbers = scanner.nextLine().split(",");
        
        // Validate the user's input
        for (String fieldNumber : fieldNumbers) {
            if (!isValidFieldNumber(fieldNumber)) {
                System.err.println("Invalid field number: " + fieldNumber);
                System.exit(1);
            }
        }
        
        // Load the XML file
        try {
            File file = new File("data.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            RecordHandler handler = new RecordHandler(fieldNumbers);
            saxParser.parse(file, handler);
            System.out.println(handler.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isValidFieldNumber(String fieldNumber) {
        try {
            int number = Integer.parseInt(fieldNumber.trim());
            return number >= 1 && number <= 6;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static class RecordHandler extends DefaultHandler {
        private final String[] selectedFields;
        private final StringBuilder sb;
        private String currentField;
        private boolean inRecord;

        public RecordHandler(String[] selectedFields) {
            this.selectedFields = selectedFields;
            this.sb = new StringBuilder("{");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("record")) {
                inRecord = true;
            } else if (inRecord) {
                for (String fieldName : FIELD_NAMES) {
                    if (qName.equals(fieldName)) {
                        currentField = fieldName;
                        break;
                    }
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (inRecord && currentField != null) {
                String value = new String(ch, start, length).trim();
                for (String selectedField : selectedFields) {
                    if (selectedField.trim().equals(currentField)) {
                        sb.append("\"").append(currentField).append("\": \"").append(value).append("\", ");
                        break;
                    }
                }
                currentField = null;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("record")) {
                inRecord = false;
            }
        }

        public String toJson() {
            sb.delete(sb.length() - 2, sb.length()); // Remove the last ", "
            sb.append("}");
            return sb.toString();
        }
    }
}
