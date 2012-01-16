package jeliot.adapt;


public class ModelEvent {

	String activity;
	Integer[] programmingConcepts;
	String result;
	
	public ModelEvent (String activity, Integer[] programmingConcepts, String result){
		this.activity = activity;
		
		//TODO check that prog concept is in the list of concepts from the adapt resources
		this.programmingConcepts = programmingConcepts;
		
		this.result = result;
	}
	
	public String getActivity(){
		return activity;
	}
	public Integer[] getProgrammingConcepts(){
		return programmingConcepts;
	}
	
	public String getResult(){
		return result;
	}
}
