import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


public class QryopOr extends Qryop {
	
	  public QryopOr(){
		  this.name = "#OR";
		  this.weight = 1;
		  this.field = "body";
	  }
	  
	  public QryopOr(double weight){
		  this.name = "#OR";
		  this.weight = weight;
		  this.field = "body";
	  }
	  
	  /**
	   * It is convenient for the constructor to accept a variable number of arguments. Thus new
	   * qryopAnd (arg1, arg2, arg3, ...).
	   */
	  public QryopOr(Qryop... q) {
	    for (int i = 0; i < q.length; i++)
	      this.args.add(q[i]);
	  }
	  
	@Override
	public QryResult evaluate() throws IOException {
		// use docid as key, and score as value
		Map<Integer, Float> resultMap = new TreeMap<Integer, Float>();
		for (int i = 0; i < args.size(); i ++){
			 Qryop impliedQryOp = new QryopScore(args.get(i));
			 QryResult iResult = impliedQryOp.evaluate();
			 int iDoc = 0;
			 while(iDoc < iResult.docScores.scores.size()){
				int docid = iResult.docScores.getDocid(iDoc);
				float score = iResult.docScores.getDocidScore(iDoc);
				if(!resultMap.containsKey(docid)){
					resultMap.put(docid, score);
				}
				else{
					float newScore = Math.max(resultMap.get(docid), score);
					resultMap.put(docid, newScore);
				}
				iDoc ++;
			 }
		}
		
		QryResult result = new QryResult();
        for(Map.Entry<Integer, Float> entry : resultMap.entrySet()){
        	result.docScores.add(entry.getKey(), entry.getValue());
        }
	    return result;
	  }
}
