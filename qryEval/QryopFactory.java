
public class QryopFactory {

	public static Qryop getQryop(String str){
		 if(str.toLowerCase().equals("#and")){
			 return new QryopAnd();
		 }
		 else if(str.toLowerCase().equals("#or")){
			 return new QryopOr();
		 }
		 else if(str.toLowerCase().indexOf("#near/") != -1){
			 return new QryopNear(str);
		 }
		 else if(str.toLowerCase().indexOf("#uw/") != -1){
			 return new QryopUW(str);
		 }
		 else if(str.toLowerCase().indexOf("#sum") != -1){
			 return new QryopSum();
		 }
		 else if(str.toLowerCase().indexOf("#weight") != -1){
			 return new QryopWeight();
		 }
		 else{
			 return new QryopTerm(str);
		 }		
	}
	
	public static Qryop getQryop(String str, double weight){
		 if(str.toLowerCase().equals("#and")){
			 return new QryopAnd(weight);
		 }
		 else if(str.toLowerCase().equals("#or")){
			 return new QryopOr(weight);
		 }
		 else if(str.toLowerCase().indexOf("#near/") != -1){
			 return new QryopNear(str, weight);
		 }
		 else if(str.toLowerCase().indexOf("#uw/") != -1){
			 return new QryopUW(str, weight);
		 }
		 else if(str.toLowerCase().indexOf("#sum") != -1){
			 return new QryopSum(weight);
		 }
		 else if(str.toLowerCase().indexOf("#weight") != -1){
			 return new QryopWeight(weight);
		 }
		 else{
			 return new QryopTerm(str, weight);
		 }		
	}
	
	/* default operator can only be SUM, AND and OR */
	public static Qryop getDefaultop(String alogrithm){
		if(alogrithm.equals(EngineArguments.Rankedboolean)){
			return new QryopOr();
		}
		else if(alogrithm.equals(EngineArguments.Unrankedboolean)){
			return new QryopOr();
		}
		else if(alogrithm.equals(EngineArguments.Indri)){
			return new QryopAnd();
		}
		/* BM25 */
		else{
			return new QryopSum();
		}
	}
}
