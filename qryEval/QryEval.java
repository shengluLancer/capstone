/*
 *  This software illustrates the architecture for the portion of a
 *  search engine that evaluates queries.  It is a template for class
 *  homework assignments, so it emphasizes simplicity over efficiency.
 *  It implements an unranked Boolean retrieval model, however it is
 *  easily extended to other retrieval models.  For more information,
 *  see the ReadMe.txt file.
 *
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class QryEval {

  static String usage = "Usage:  java " + System.getProperty("sun.java.command")
      + " paramFile\n\n";

  /**
   * The index file reader is accessible via a global variable. This isn't great programming style,
   * but the alternative is for every query operator to store or pass this value, which creates its
   * own headaches.
   */
  public static IndexReader READER;
 

  public static EnglishAnalyzerConfigurable analyzer =  new EnglishAnalyzerConfigurable (Version.LUCENE_43);
  static {
    analyzer.setLowercase(true);
    analyzer.setStopwordRemoval(true);
    analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
  }

  /**
   * 
   * @param args The only argument should be one file name, which indicates the parameter file.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
	 // args = new String[1];
	 // args[0] = "/Users/rhua/Downloads/try1.txt";
    // must supply parameter file
    if (args.length < 1) {
      System.err.println(usage);
      System.exit(1);
    }

    // read in the parameter file; one parameter per line in format of key=value
    Map<String, String> params = new HashMap<String, String>();
    Scanner scan = new Scanner(new File(args[0]));
    String line = null;
    do {
      line = scan.nextLine();
      String[] pair = line.split("=");
      params.put(pair[0].trim(), pair[1].trim());
    } while (scan.hasNext());
    
    // parameters required for this example to run
    if (!params.containsKey("indexPath") || 
    	!params.containsKey("queryFilePath") || !params.containsKey("retrievalAlgorithm")) {
      System.err.println("Error: Parameters were missing.");
      System.exit(1);
    }

    READER = DirectoryReader.open(FSDirectory.open(new File(params.get("indexPath"))));
    EngineArguments.docLengthStore = new DocLengthStore(READER);
    EngineArguments.retrievalAlgorithm = params.get("retrievalAlgorithm").toLowerCase();
    EngineArguments.B = Double.parseDouble(params.get("BM25:b"));
    EngineArguments.K_1 = Double.parseDouble(params.get("BM25:k_1"));
    EngineArguments.K_3 = Double.parseDouble(params.get("BM25:k_3"));
    EngineArguments.Mu = Double.parseDouble(params.get("Indri:mu"));
    EngineArguments.Lambda = Double.parseDouble(params.get("Indri:lambda"));
    EngineArguments.Smoothing = params.get("Indri:smoothing");
    EngineArguments.totalDoc = QryEval.READER.numDocs();
    List<String> queries = getQuery(params.get("queryFilePath"));
   // System.out.println("retrievalAlgorithm")
    
    if (READER == null) {
      System.err.println(usage);
      System.exit(1);
    }
    /*
     long before = System.currentTimeMillis();
     showResult(queries);
     long after = System.currentTimeMillis();
     System.out.println(after - before);
     */
     showResult(queries);
  } 
  
   static void showResult(List<String> queries) throws IOException {
	   for(String str : queries){
		    int startIndex = str.indexOf(":");	
		    int queryID = Integer.valueOf(str.substring(0, startIndex).trim());
		    String query = str.substring(startIndex + 1, str.length());
	    	QryTree qryT = new QryTree(query);
	    	//System.out.println(qryT);
	    	// get the score list of the tree root
	    	QryResult result = (new QryopScore(qryT.root)).evaluate();	    	
	    	int rank = 1;
	    	List<ScoreList.ScoreListEntry> finalscores = result.docScores.getFinalScoreList();
	    	int size = finalscores.size() >= 100 ? 100 : finalscores.size();
	    	if(size == 0){
	    		System.out.println(queryID + " Q0 dummy 1 0 run-1");
	    		continue;
	    	}
	    	List<ScoreList.ScoreListEntry> scores = finalscores.subList(0, size);
	    	for(ScoreList.ScoreListEntry entry : scores){
	    	    System.out.println(queryID + " Q0 " + getExternalDocid(entry.getDocId())
	    	    		+ " " + rank ++ + " " + entry.getScore() + " run-1" );
	    	}
	    }
   }
   static List<String> getQuery(String path) throws FileNotFoundException {
	   List<String> result = new ArrayList<String>();
	   Scanner sc = new Scanner(new File(path));
	   while(sc.hasNextLine()){
		   result.add(sc.nextLine());
	   }
	   return result;
   }

  /**
   *  Get the external document id for a document specified by an
   *  internal document id.  Ordinarily this would be a simple call to
   *  the Lucene index reader, but when the index was built, the
   *  indexer added "_0" to the end of each external document id.  The
   *  correct solution would be to fix the index, but it's too late
   *  for that now, so it is fixed here before the id is returned.
   * 
   * @param iid The internal document id of the document.
   * @throws IOException 
   */
  static String getExternalDocid (int iid) throws IOException {
    Document d = QryEval.READER.document (iid);
    String eid = d.get ("externalId");

    if ((eid != null) && eid.endsWith ("_0"))
      eid = eid.substring (0, eid.length()-2);

    return (eid);
  }

  /**
   * Prints the query results. 
   * 
   * THIS IS NOT THE CORRECT OUTPUT FORMAT.
   * YOU MUST CHANGE THIS METHOD SO THAT IT OUTPUTS IN THE FORMAT SPECIFIED IN THE HOMEWORK PAGE, 
   * WHICH IS: 
   * 
   * QueryID Q0 DocID Rank Score RunID
   * 
   * @param queryName Original query.
   * @param result Result object generated by {@link Qryop#evaluate()}.
   * @throws IOException 
   */
  static void printResults(String queryName, QryResult result) throws IOException {

    System.out.println(queryName + ":  ");
    if (result.docScores.scores.size() < 1) {
      System.out.println("\tNo results.");
    } else {
      for (int i = 0; i < result.docScores.scores.size(); i++) {
        System.out.println("\t" + i + ":  "
			   + getExternalDocid (result.docScores.getDocid(i))
			   + ", "
			   + result.docScores.getDocidScore(i));
      }
    }
  }

  /**
   * Given a query string, returns the terms one at a time with stopwords
   * removed and the terms stemmed using the Krovetz stemmer. 
   * 
   * Use this method to process raw query terms. 
   * 
   * @param query String containing query
   * @return Array of query tokens
   * @throws IOException
   */
  static String[] tokenizeQuery(String query) throws IOException {
    
    TokenStreamComponents comp = analyzer.createComponents("dummy", new StringReader(query));
    TokenStream tokenStream = comp.getTokenStream();

    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    
    List<String> tokens = new ArrayList<String>();
    while (tokenStream.incrementToken()) {
      String term = charTermAttribute.toString();
      tokens.add(term);
    }
    return tokens.toArray(new String[tokens.size()]);
  }
}
