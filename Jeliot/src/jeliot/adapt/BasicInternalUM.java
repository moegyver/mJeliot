package jeliot.adapt;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import java.util.HashMap;
import java.util.Iterator;
public class BasicInternalUM extends UMInteraction{

	//Right now properties are just variables
	// TODO: To be changed to something more OO
	
	
	UserProperties internalUM = null;
	//HashMap internalUM = new HashMap();
	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	public void userLogin(String userName, String password) {
		internalUM = ResourceBundles.getUserModelConceptsProperties();
	}

	public void userLogout(String userName) {
		
		internalUM.save();

	}

	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		Integer[] entries = event.getProgrammingConcepts();
		int result = Integer.parseInt(event.getResult());
		int value = result;
		String activity = event.getActivity();
		for (int i=0; i < entries.length; i++){
			String key = entries[i].toString() +"."  + activity;
			if (internalUM.containsKey(key)){
				value = result + internalUM.getIntegerProperty(key);

			} else {
				value = result;
			}
			System.out.println(key + "="+value);
			internalUM.setIntegerProperty(key, value);
		}
				
	}

	public boolean isConceptKnown(int concept){
		String conceptID = Integer.toString(concept);
		int rightAnswers = (internalUM.containsKey(conceptID+".questions.correct"))?
				internalUM.getIntegerProperty(conceptID+".questions.correct"):0;
		int wrongAnswers = (internalUM.containsKey(conceptID+".questions.wrong"))?
				internalUM.getIntegerProperty(conceptID+".questions.wrong"):0;
		
		return (rightAnswers > 2) && (rightAnswers - wrongAnswers > 2);
				
	
	}
	public double getConceptKnowledge(String concept, String activity) {
		String property = concept + ".questions.correct";
		double result = Double.valueOf(internalUM.getStringProperty(property)).doubleValue();
		return result;
	}
	
	/**
	 * Updates the internal User Model UserProperties. It overwrites previous values stored!!
	 * 
	 * @param properties Set of properties to replace internalUM with
	 */
	public void updateInternalUM(HashMap properties){
		
		Iterator it = properties.keySet().iterator();
		while (it.hasNext()){
			String key= (String) it.next();
			internalUM.setStringProperty(key,  
					(String) properties.get(key));
		}
	}
}
