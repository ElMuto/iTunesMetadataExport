import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class iTunesMetadataExport {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		final boolean REMOVE_COMPUTED_RATINGS 			= true;
		final boolean PRINT_TITLES_WITH_REMOVED_TAGS 	= true;
		final boolean PRINT_INTERESINTG_TITLES 			= false;

		String inputFileName  = "iTunes Music Library.xml";
		String outputFileName = "iTunes Music Library.clean.xml";
		
		String defaultITunesDir = System.getenv("USERPROFILE") + "\\Music\\iTunes";		
		String inputFilePath = defaultITunesDir + "\\" + inputFileName;
		String outputFilePath = defaultITunesDir + "\\" + outputFileName;
		
		System.out.println("Using             '" + inputFilePath + "'        as input");
		
		
		String nullStr = "NULL";
		String sep = "\t";

		
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
			Document doc = db.parse(new File(inputFilePath));
			
			Node mainDict = doc.getChildNodes().item(1).getChildNodes().item(1);

			Node currNode = mainDict.getFirstChild();
			while (!currNode.getNodeName().equals("dict")) currNode = currNode.getNextSibling();
			Node titleDict = currNode;
			
			String header = interestingKeys[0];
			for (int j = 1; j < interestingKeys.length; j++) {
				header += sep + interestingKeys[j];
			}
			
			if (!PRINT_TITLES_WITH_REMOVED_TAGS && PRINT_INTERESINTG_TITLES) {
				System.out.println(header);
			}
			
			// iterate over titles
			NodeList titles = titleDict.getChildNodes();
			for (int i = 0; i < titles.getLength(); i++) {
				if (titles.item(i).getNodeName().equals("dict")) {
					Node title = titles.item(i);
					
					Map<String, AttribNodePair> attributeMap = new HashMap<>();
					
					// iterate over attributes
					NodeList attributes = title.getChildNodes();
					for (int j = 0; j < attributes.getLength(); j++) {
						Node attributeKeyNode = attributes.item(j);
						if(attributeKeyNode.getNodeName().equals("key")) {
							attributeMap.put(
									attributeKeyNode.getTextContent().trim(),
									new AttribNodePair(
											attributeKeyNode.getNextSibling().getTextContent().trim(),
											attributeKeyNode,
											attributeKeyNode.getNextSibling()
											)
									);
						}
					}
					
					boolean interestingArtist = false;
					for (int j = 0; j < interestingArtists.length && !interestingArtist; j++) {
						if (attributeMap.containsKey("Artist") && attributeMap.get("Artist").getValue().equals(interestingArtists[j])) {
							interestingArtist = true;
						}
					}
					
					boolean interestingTitle = false;
					for (int j = 0; j < interestingTitles.length && !interestingTitle; j++) {
						if (attributeMap.get("Name").getValue().equals(interestingTitles[j])) {
							interestingTitle = true;
						}
					}

					boolean ratingComputed 		= attributeMap.containsKey("Rating Computed") 		? true : false;
					boolean albumRatingComputed = attributeMap.containsKey("Album Rating Computed") ? true : false;
					
					if (REMOVE_COMPUTED_RATINGS && ratingComputed) {
						
						title.removeChild(attributeMap.get("Rating").getKeyNode());
						title.removeChild(attributeMap.get("Rating").getValueNode());
						
						if (PRINT_TITLES_WITH_REMOVED_TAGS) {
						System.out.println("Removed \"" + attributeMap.get("Name").getValue() + "\" by " + 
								(attributeMap.containsKey("Artist") ? ("\"" +  attributeMap.get("Artist").getValue() + "\"") : "unknown artist"));
						}
					}
					
					if (interestingArtist && interestingTitle && !PRINT_TITLES_WITH_REMOVED_TAGS && PRINT_INTERESINTG_TITLES) {
						printTitleInfoToConsole(nullStr, sep, interestingKeys, attributeMap, ratingComputed,
								albumRatingComputed);
					}
				}
			}
			Transformer tx = TransformerFactory.newInstance().newTransformer();
			tx.setOutputProperty(OutputKeys.INDENT, "no");
			
			System.out.println("Writing result to '" + outputFilePath + "'");
			tx.transform(new DOMSource( doc), new StreamResult(new FileOutputStream(outputFilePath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printTitleInfoToConsole(String nullStr, String sep, String[] interestingKeys,
			Map<String, AttribNodePair> attributeMap, boolean ratingComputed, boolean albumRatingComputed) {
		String row = attributeMap.containsKey(interestingKeys[0]) ? attributeMap.get(interestingKeys[0]).getValue() : nullStr;					
		for (int j = 1; j < interestingKeys.length; j++) {
			row += sep;
			String key = interestingKeys[j];
			
			if (key.equals("Rating Computed")) {
				row += String.valueOf(ratingComputed);
			} else	if (key.equals("Album Rating Computed")) {
				row += String.valueOf(albumRatingComputed);
			} else {
				if (attributeMap.containsKey(key)) {
					row += attributeMap.get(key).getValue();
				} else {
					row += nullStr;
				}
			}
		}
		System.out.println(row);
	}

	private static class AttribNodePair {
		
		private final String value;
		private final Node keyNode;
		private final Node valueNode;
		
		public AttribNodePair(String value, Node keyNode, Node valueNode) {
			super();
			this.value = value;
			this.keyNode = keyNode;
			this.valueNode = valueNode;
		}
		
		public String getValue() {
			return value;
		}
		
		public Node getKeyNode() {
			return keyNode;
		}
		
		public Node getValueNode() {
			return valueNode;
		}
	}
}
