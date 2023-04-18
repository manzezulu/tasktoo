package sdp.task2;

import java.io.File;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class App {

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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            
            // Extract the field values from the XML document
            Element record = (Element) doc.getElementsByTagName("record").item(0);
            String name = getNodeValue(record, "name", fieldNumbers);
            String postalZip = getNodeValue(record, "postalZip", fieldNumbers);
            String region = getNodeValue(record, "region", fieldNumbers);
            String country = getNodeValue(record, "country", fieldNumbers);
            String address = getNodeValue(record, "address", fieldNumbers);
            String list = getNodeValue(record, "list", fieldNumbers);
            
            // Output the selected fields in JSON format
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (name != null) sb.append("\"Name\": \"" + name + "\", ");
            if (postalZip != null) sb.append("\"Postal ZIP code\": \"" + postalZip + "\", ");
            if (region != null) sb.append("\"Region\": \"" + region + "\", ");
            if (country != null) sb.append("\"Country\": \"" + country + "\", ");
            if (address != null) sb.append("\"Address\": \"" + address + "\", ");
            if (list != null) sb.append("\"List\": \"" + list + "\", ");
            sb.delete(sb.length() - 2, sb.length()); // Remove the last ", " from the output
            sb.append("}");
            
            System.out.println(sb.toString());
            
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
    
    private static String getNodeValue(Element element, String tagName, String[] selectedFields) {
    // Return null if the current tag is not selected
    boolean isSelected = false;
    for (String fieldNumber : selectedFields) {
        if (fieldNumber.trim().equals(tagName)) {
            isSelected = true;
            break;
        }
    }
    if (!isSelected) {
        return null;
    }

    // Extract the text value of the specified tag
    NodeList nodeList = element.getElementsByTagName(tagName);
    if (nodeList.getLength() == 0) {
        return null;
    }
    Element subElement = (Element) nodeList.item(0);
    if (subElement == null || subElement.getTextContent() == null) {
        return null;
    }
    return subElement.getTextContent();
}
}