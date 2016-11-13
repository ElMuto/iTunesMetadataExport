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
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Main {
	
	private static int indent = 0;
	private static String basicIndent ="\t";
	
	private class TitleMetadata {
		public TitleMetadata(String title, String artist, int titleRating, Optional<Boolean> titleRatingComputed,
				int albumRating, Optional<Boolean> albumRatingComputed) {
			super();
			this.title = title;
			this.artist = artist;
			this.titleRating = titleRating;
			this.titleRatingComputed = titleRatingComputed;
			this.albumRating = albumRating;
			this.albumRatingComputed = albumRatingComputed;
		}
		private String title;
		private String artist;
		private int titleRating;
		private Optional<Boolean> titleRatingComputed;
		private int albumRating;
		private Optional<Boolean>  albumRatingComputed;
		
		public void print() {
			System.out.println(artist + " - " + title + ": " + titleRating) ;
		}
	}
	


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
			
			NodeList titles = titleDict.getChildNodes();
			for (int i = 0; i < titles.getLength(); i++) {
				if (titles.item(i).getNodeName().equals("dict")) {
					Node title = titles.item(i);
					NodeList titleDataNodes = title.getChildNodes();
					for (int j = 0; j < titleDataNodes.getLength(); j++) {
						Node titleDataNode = titleDataNodes.item(j);
						
						String keyName;
						if(titleDataNode.getNodeName().equals("key")) {
							keyName = titleDataNode.getTextContent().trim();
							System.out.println(keyName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
