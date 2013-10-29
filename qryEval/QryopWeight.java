import java.io.IOException;
import java.util.List;


public class QryopWeight extends Qryop{

	public double totalWeight;
	public QryopWeight(){
		  this.name = "#Weight";
		  this.weight = 1;
		  this.field = "body";
	  }
	  
	  public QryopWeight(double weight){
		  this.name = "#Weight";
		  this.weight = weight;
		  this.field = "body";
	  }

	@Override
	public QryResult evaluate() throws IOException {
		
		  if(args.size() == 0) return new QryResult();
		    totalWeight = getTotalWeight(args);
		    Qryop impliedQryOp = new QryopScore(args.get(0));
		    QryResult result = impliedQryOp.evaluate();
		    reCalculateScore(result, args.get(0).weight, args.size());    
		    result.docScores.defaultScore *= (double)args.get(0).weight/totalWeight;
		    
		    for (int i = 1; i < args.size(); i++) {

		      impliedQryOp = new QryopScore(args.get(i));
		      QryResult iResult = impliedQryOp.evaluate();
		      reCalculateScore(iResult, args.get(i).weight, args.size());   
		      iResult.docScores.defaultScore *= (double)args.get(i).weight/totalWeight;
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
		        	  //System.out.println("rDoc " + rDoc + " iDoc " +  iDoc + " rSocre " + rScore + " iScore " + iScore);
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
	
	private double getTotalWeight(List<Qryop> args){
		double weight = 0;
		for(Qryop op : args){
			weight += op.weight;
		}
		return weight;
	}
	
	private void reCalculateScore(QryResult result, double weight, int size){
		  for(ScoreList.ScoreListEntry scoreEntry : result.docScores.scores){
			  scoreEntry.setScore((float)scoreEntry.getScore()*(float)weight/(float)totalWeight);
		  }
	  }

}
