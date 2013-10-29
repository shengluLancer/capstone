/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;


public class QryopAnd extends Qryop {

  public QryopAnd(){
	  this.name = "#AND";
	  this.weight = 1;
	  this.field = "body";
  }
  
  public QryopAnd(double weight){
	  this.name = "#AND";
	  this.weight = weight;
	  this.field = "body";
  }
  
  /**
   * It is convenient for the constructor to accept a variable number of arguments. Thus new
   * qryopAnd (arg1, arg2, arg3, ...).
   */
  public QryopAnd(Qryop... q) {
    for (int i = 0; i < q.length; i++)
      this.args.add(q[i]);
  }

  @Override
  public QryResult evaluate() throws IOException {
	  	if(EngineArguments.retrievalAlgorithm.equals(EngineArguments.Indri)){
	  		return evaluateIndri();
	  	}
	  	else return evaluateBoolean();
  }
  
  /**
   * Evaluate the query operator.
   */
  public QryResult evaluateBoolean() throws IOException {
	  if(args.size() == 0) return new QryResult();

	    // Seed the result list by evaluating the first query argument. The result could be docScores or
	    // invList, depending on the query operator. Wrap a SCORE query operator around it to force it
	    // to be a docScores list. There are more efficient ways to do this. This approach is just easy
	    // to see and understand.
	    Qryop impliedQryOp = new QryopScore(args.get(0));
	    QryResult result = impliedQryOp.evaluate();

	    // Each pass of the loop evaluates one query argument.
	    for (int i = 1; i < args.size(); i++) {

	      impliedQryOp = new QryopScore(args.get(i));
	      QryResult iResult = impliedQryOp.evaluate();

	      // Use the results of the i'th argument to incrementally compute the query operator.
	      // Intersection-style query operators iterate over the incremental results, not the results of
	      // the i'th query argument.
	      int rDoc = 0; /* Index of a document in result. */
	      int iDoc = 0; /* Index of a document in iResult. */

	      while (rDoc < result.docScores.scores.size()) {

	        // Unranked Boolean AND. Remove from the incremental result any documents that weren't 
	        // returned by the i'th query argument.

	        // Ignore documents matched only by the i'th query arg.
	        while ((iDoc < iResult.docScores.scores.size())
	            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {
	          iDoc++;
	        }

	        // If the rDoc document appears in both lists, keep it, otherwise discard it.
	        if ((iDoc < iResult.docScores.scores.size())
	            && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc))) {
	          
	          // for ranked boolean, Use the MIN function to combine the scores from the query arguments.
	          if(EngineArguments.retrievalAlgorithm.equals(EngineArguments.Rankedboolean)){
	        	  float rScore = result.docScores.getDocidScore(rDoc);
	        	  float iScore = iResult.docScores.getDocidScore(iDoc);
	        	  result.docScores.setDocidScore(rDoc, Math.min(rScore, iScore));
	          }
	          rDoc++;
	          iDoc++;
	        } else {
	          result.docScores.scores.remove(rDoc);
	        }
	      }
	    }
	    return result;
  }
  
  
  public QryResult evaluateIndri() throws IOException{
	  if(args.size() == 0) return new QryResult();

	    Qryop impliedQryOp = new QryopScore(args.get(0));
	    QryResult result = impliedQryOp.evaluate();
	    reCalculateScore(result, args.size());   
	    result.docScores.defaultScore /= args.size();
	    
	    for (int i = 1; i < args.size(); i++) {
	      impliedQryOp = new QryopScore(args.get(i));
	      QryResult iResult = impliedQryOp.evaluate();
	      reCalculateScore(iResult, args.size());   	
	      iResult.docScores.defaultScore /= args.size();
	      int rDoc = 0; /* Index of a document in result. */
	      int iDoc = 0; /* Index of a document in iResult. */

	      while (rDoc < result.docScores.scores.size()) {
	        while ((iDoc < iResult.docScores.scores.size())
	            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {
	      	  float iScore = iResult.docScores.getDocidScore(iDoc);
	          result.docScores.add(rDoc, iResult.docScores.getDocid(iDoc), result.docScores.defaultScore + iScore);
	          rDoc ++;
	          iDoc ++;
	        }

	        // If the rDoc document appears in both lists, keep it, otherwise discard it.
	        if ((iDoc < iResult.docScores.scores.size())
	            && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc))) {
	        	
	        	  float rScore = result.docScores.getDocidScore(rDoc);
	        	  float iScore = iResult.docScores.getDocidScore(iDoc);
	        	 // System.out.println("rDoc " + rDoc + " iDoc " +  iDoc + " rSocre " + rScore + " iScore " + iScore);
	        	  result.docScores.setDocidScore(rDoc, rScore + iScore);	     
	        	  rDoc++;
	        	  iDoc++;
	        } else {         
	        	  float rScore = result.docScores.getDocidScore(rDoc);
	        	  result.docScores.setDocidScore(rDoc, rScore + iResult.docScores.defaultScore);
	        	  rDoc ++;
	        }
	      }
	      result.docScores.defaultScore += iResult.docScores.defaultScore;
	    }
	    return result;
  }
  
  private void reCalculateScore(QryResult result, int size){
	  for(ScoreList.ScoreListEntry scoreEntry : result.docScores.scores){
		  scoreEntry.setScore((float)(scoreEntry.getScore()/(double)size));
	  }
  }
}
