import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        writeString(json, "data.json");

        String fileNameXML = "data.xml";
        List<Employee> listXML = parseXML(fileNameXML);

        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employees = null;

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            employees = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String jsonString, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileName);

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            employees = parseEmployeeNodes(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static List<Employee> parseEmployeeNodes(NodeList nodeList) {
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("employee")) {
                Element element = (Element) node;
                long id = Long.parseLong(getNodeValue(element, "id"));
                String firstName = getNodeValue(element, "firstName");
                String lastName = getNodeValue(element, "lastName");
                String country = getNodeValue(element, "country");
                int age = Integer.parseInt(getNodeValue(element, "age"));

                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        }

        return employees;
    }

    private static String getNodeValue(Element element, String nodeName) {
        return element.getElementsByTagName(nodeName).item(0).getTextContent();
    }

}