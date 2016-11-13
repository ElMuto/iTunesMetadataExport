package ElMuto.iTunesMetadataExport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException; 
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Main {
	public static void main(String[] args) {

		String inputFileName = "data/iTunes Music Library.xml";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		dbf.setNamespaceAware(true);
	    dbf.setValidating(true);
	    
	    DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(inputFileName));
			
			Node mainDict = doc.getChildNodes().item(1).getChildNodes().item(1);

			Node currNode = mainDict.getFirstChild();
			while (!currNode.getNodeName().equals("dict")) currNode = currNode.getNextSibling();
			Node titleDict = currNode;
						
			// iterate over titles
			NodeList titles = titleDict.getChildNodes();
			for (int i = 0; i < titles.getLength(); i++) {
				if (titles.item(i).getNodeName().equals("dict")) {
					Node title = titles.item(i);
					
					Map<String, String> attributeMap = new HashMap<>();
					
					// iterate over attributes
					NodeList attributes = title.getChildNodes();
					for (int j = 0; j < attributes.getLength(); j++) {
						Node attribute = attributes.item(j);
						if(attribute.getNodeName().equals("key")) {
							attributeMap.put(attribute.getTextContent().trim(), attribute.getNextSibling().getTextContent().trim());
						}
					}
					
					String[] interestingKeys = { "Artist", "Name", "Rating", "Rating Computed", "Album Rating", "Album Rating Computed" };
					
					String nullStr = "NULL";
					String sep = "\t";
					String row = attributeMap.containsKey(interestingKeys[0]) ? attributeMap.get(interestingKeys[0]) : nullStr;
					
					for (int j = 1; j < interestingKeys.length; j++) {
						row += sep;
						 String key = interestingKeys[j];
						if (key.equals("Rating Computed") || key.equals("Album Rating Computed")) {
							row += attributeMap.containsKey(key) ? "TRUE" : "FALSE";
						} else {
							row += attributeMap.containsKey(key) ? attributeMap.get(key) : nullStr;
						}
					}
					
					System.out.println(row);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
