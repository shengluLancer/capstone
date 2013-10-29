import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/* #SUM operator, only for BM25 */
public class QryopSum extends Qryop {
	
	  public QryopSum(){
		  this.name = "#SUM";
		  this.weight = 1;
		  this.field = "body";
	  }
	  
	  public QryopSum(double weight){
		  this.name = "#SUM";
		  this.weight = weight;
		  this.field = "body";
	  }
	  
	@Override
	public QryResult evaluate() throws IOException {
		// use docid as key, and score as value
		HashMap<String, Integer> qfMap = new HashMap<String, Integer>();
		for(int i = 0; i < args.size(); i ++){
			String termName = args.get(i).name;
			if(termName.indexOf("#") != -1) continue;
			if(qfMap.containsKey(termName)){
				qfMap.put(termName, qfMap.get(termName) + 1);
			}
			else qfMap.put(termName, 1);
		}
		Map<Integer, Float> resultMap = new TreeMap<Integer, Float>();
		for (int i = 0; i < args.size(); i ++){
			 Qryop impliedQryOp = new QryopScore(args.get(i));
			 QryResult iResult = impliedQryOp.evaluate();
			 int iDoc = 0;
			 while(iDoc < iResult.docScores.scores.size()){
				int docid = iResult.docScores.getDocid(iDoc);
				float score = iResult.docScores.getDocidScore(iDoc);
				if(!resultMap.containsKey(docid)){
					if(qfMap.get(args.get(i).name) != null)
						resultMap.put(docid, adduserWeight(score, qfMap.get(args.get(i).name)));
					else
						resultMap.put(docid, adduserWeight(score, 1));
				}
				else{
					float newScore;
					if(qfMap.get(args.get(i).name) != null)
					   newScore = resultMap.get(docid) + adduserWeight(score,qfMap.get(args.get(i).name));
					else
					   newScore = resultMap.get(docid) + adduserWeight(score,1);
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
	
	private float adduserWeight(float score, int qtf){
		double up = (EngineArguments.K_3 + 1) * qtf;
		double down = EngineArguments.K_3 + qtf;
		return (float)(up/down) * score;
	}
}
