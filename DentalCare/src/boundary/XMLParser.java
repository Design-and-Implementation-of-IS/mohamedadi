package boundary;

import entity.Item;
import entity.Supplier;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    public List<Item> parse(String xmlFile) {
        List<Item> items = new ArrayList<>();

        try {
            File file = new File(xmlFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    int id = Integer.parseInt(elem.getElementsByTagName("serialNum").item(0).getTextContent());
                    String name = elem.getElementsByTagName("itemName").item(0).getTextContent();
                    String description = elem.getElementsByTagName("description").item(0).getTextContent();
                    int quantity = Integer.parseInt(elem.getElementsByTagName("quantityInStock").item(0).getTextContent());
                    int supplierId = Integer.parseInt(elem.getElementsByTagName("supplierId").item(0).getTextContent());
                    LocalDate expDate = LocalDate.parse(elem.getElementsByTagName("expDate").item(0).getTextContent());

                    Supplier supplier = new Supplier(String.valueOf(supplierId), "Imported", "", "", "");
                    Item item = new Item(id, name, description, null, quantity, expDate, supplier);

                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
