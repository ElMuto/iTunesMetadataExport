package ElMuto.iTunesMetadataExport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Main {
	public static void main(String[] args) {
		String nullStr = "NULL";
		String sep = "\t";

		String inputFileName = "data/iTunes Music Library.xml";		
		String[] interestingKeys = { "Artist", "Album", "Name", "Rating", "Rating Computed", "Album Rating", "Album Rating Computed" };

		String[] interestingArtists = { "Solange", "Polica", "A$AP Rocky", "The Mars Volta", "The Acid", "Thundercat",
				"Ginger Baker's Air Force", "Amy Winehouse"};
		String[] interestingTitles  = { "Rise", "Weary", "Violent Games", "Lord Pretty Flacko Jodye 2 (Lpfj2)",
				"Veda", "Vedamalady", "Walkin'", "Da Da Man", "Early In The Morning", "Wake Up Alone"};
		
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
			
			String header = interestingKeys[0];
			for (int j = 1; j < interestingKeys.length; j++) {
				header += sep + interestingKeys[j];
			}
			
			System.out.println(header);
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
					
					boolean interestingArtist = false;
					for (int j = 0; j < interestingArtists.length && !interestingArtist; j++) {
						if (attributeMap.containsKey("Artist") && attributeMap.get("Artist").equals(interestingArtists[j])) {
							interestingArtist = true;
						}
					}
					
					boolean interestingTitle = false;
					for (int j = 0; j < interestingTitles.length && !interestingTitle; j++) {
						if (attributeMap.get("Name").equals(interestingTitles[j])) {
							interestingTitle = true;
						}
					}

					boolean ratingComputed 		= attributeMap.containsKey("Rating Computed") 		? true : false;
					boolean albumRatingComputed = attributeMap.containsKey("Album Rating Computed") ? true : false;

//					if (ratingComputed) {
					if (interestingArtist && interestingTitle) {
						String row = attributeMap.containsKey(interestingKeys[0]) ? attributeMap.get(interestingKeys[0]) : nullStr;					
						for (int j = 1; j < interestingKeys.length; j++) {
							row += sep;
							String key = interestingKeys[j];
							
							if (key.equals("Rating Computed")) {
								row += String.valueOf(ratingComputed);
							} else	if (key.equals("Album Rating Computed")) {
								row += String.valueOf(albumRatingComputed);
							} else {
								if (attributeMap.containsKey(key)) {
									row += attributeMap.get(key);
								} else {
									row += nullStr;
								}
							}
						}
						System.out.println(row);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
