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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;



public class Main {
	
	private static void printlnCommon(Node n) {
	    System.out.print(" nodeName=\"" + n.getNodeName() + "\"");

	    String val = n.getNamespaceURI();
	    if (val != null) {
	    	System.out.print(" uri=\"" + val + "\"");
	    }

	    val = n.getPrefix();

	    if (val != null) {
	    	System.out.print(" pre=\"" + val + "\"");
	    }

	    val = n.getLocalName();
	    if (val != null) {
	    	System.out.print(" local=\"" + val + "\"");
	    }

	    val = n.getNodeValue();
	    if (val != null) {
	    	System.out.print(" nodeValue=");
	        if (val.trim().equals("")) {
	            // Whitespace
	        	System.out.print("[WS]");
	        }
	        else {
	        	System.out.print("\"" + n.getNodeValue() + "\"");
	        }
	    }
	    System.out.println();
	}

	public static void main(String[] args) {

		String inputFileName = "data/iTunes Music Library.xml";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		dbf.setNamespaceAware(true);
	    dbf.setValidating(false);
	    
	    DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err,
					"UTF-8");
			Document doc = db.parse(new File(inputFileName));
			
			printlnCommon(doc.getFirstChild());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
