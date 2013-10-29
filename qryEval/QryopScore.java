/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;

public class QryopScore extends Qryop {

  /**
   * The SCORE operator accepts just one argument.
   */
  public QryopScore(Qryop q) {
    this.args.add(q);
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {

    // Evaluate the query argument.
    QryResult result = args.get(0).evaluate();
    int ctf = result.invertedList.ctf;
    if(result.invertedList.df > 0 && EngineArguments.retrievalAlgorithm.equals(EngineArguments.Indri)){
    	result.docScores.defaultScore = getDefaultScore( args.get(0).field, result.invertedList.df, ctf);
    }
   // System.out.println("name " + args.get(0).name + " defalut score: " + result.docScores.defaultScore);
    // Each pass of the loop computes a score for one document. Note: If the evaluate operation
    // above returned a score list (which is very possible), this loop gets skipped.
    for (int i = 0; i < result.invertedList.df; i++) {     
      int tf = result.invertedList.postings.get(i).tf;	
  	  int docid = result.invertedList.postings.get(i).docid;
       // if Unranked Boolean. All matching documents get a score of 1.0.
      if(EngineArguments.retrievalAlgorithm.equals(EngineArguments.Unrankedboolean)) {
         result.docScores.add(docid, getUnrankedBooleanScore());
      }
      else if(EngineArguments.retrievalAlgorithm.equals(EngineArguments.Rankedboolean)){ 	
    	 result.docScores.add(docid, getRankedBooleanScore(tf));
      }
      else if(EngineArguments.retrievalAlgorithm.equals(EngineArguments.Indri)){
    	  
    	 result.docScores.add(docid, getIndriScore(docid, ctf, result.invertedList.df, tf, args.get(0).field));
      }
      /* BM25 without user weight, user weight will be done in the SUM operator*/	
      else{  
    	  result.docScores.add(docid
    			  , getBM25ScoreWithoutUserweight(docid, result.invertedList.df, tf, args.get(0).field));
      }
    }
    
    
    // The SCORE operator should not return a populated inverted list.
    // If there is one, replace it with an empty inverted list.
    if (result.invertedList.df > 0)
    	result.invertedList = new InvList();
   
    return result;
  }
  
  private float getUnrankedBooleanScore(){
	  return (float)1.0;
  }
  
  private float getRankedBooleanScore(int tf){
	  return (float)tf;
  }
  
  private float getBM25ScoreWithoutUserweight(int docid, int df, int tf, String field) throws IOException{
	  double RSJweight = getRSJweight(df);
	  double tfweight = getTFweight(docid, tf, field);
	  return (float)(RSJweight * tfweight);
  }
  
  private double getRSJweight(int df){
	  double logarithm = (EngineArguments.totalDoc - df + 0.5) / (df + 0.5);
	  return Math.log(logarithm);
  }
  
  private double getTFweight(int docid, int tf, String field) throws IOException{
	  long docLen = EngineArguments.docLengthStore.getDocLength(field, docid);
	  double avg_docLen = QryEval.READER.getSumTotalTermFreq(field) /
		      				(double) QryEval.READER.getDocCount (field);
	  double k1 = EngineArguments.K_1;
	  double b = EngineArguments.B;
	  double weight = k1*((1-b) + b*docLen/avg_docLen);
	  return  tf/(tf + weight);
  }
  
  
  private float getIndriScore(int docid, int ctf, int df, int tf, String field) throws IOException{
	  double mu = EngineArguments.Mu;
	  double lambda = EngineArguments.Lambda;
	  long docLen = EngineArguments.docLengthStore.getDocLength(field, docid);
	  long corpusLen = QryEval.READER.getSumTotalTermFreq(field);
	  double MLE;
	  if(EngineArguments.Smoothing.equals("df"))
		 MLE = ((double)df)/QryEval.READER.numDocs();
	  else{
		 MLE = ((double)ctf)/corpusLen;
	  }
	  double firstHalf = (lambda*((tf + mu*MLE)/(docLen + mu)));
	  double secondHalf = ((1-lambda)*MLE);
	  return (float)Math.log(firstHalf + secondHalf);
  }
  
  private float getDefaultScore(String field, int df, int ctf) throws IOException{
	  double mu = EngineArguments.Mu;
	  double lambda = EngineArguments.Lambda;
	  float lengthD = (float)(QryEval.READER.getSumTotalTermFreq(field))
		  		 	 /(float)QryEval.READER.getDocCount (field);
	  long corpusLen = QryEval.READER.getSumTotalTermFreq(field);
	  double MLE;
	  if(EngineArguments.Smoothing.equals("df"))
			// MLE = ((double)df)/QryEval.READER.numDocs());
		  MLE = ((double)df)/QryEval.READER.getDocCount(field);
		  else{
		  MLE = ((double)ctf)/corpusLen;
		  }
	  double firstHalf = (float)(lambda*((0 + mu*MLE)/(lengthD + mu)));
	  double secondHalf =  (float)((1-lambda)*MLE);
	  return (float)Math.log(firstHalf + secondHalf);
  }
}
