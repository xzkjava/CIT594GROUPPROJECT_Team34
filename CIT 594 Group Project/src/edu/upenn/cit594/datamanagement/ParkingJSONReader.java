package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.logging.Logger;

public class ParkingJSONReader extends ParkingViolationReader{

	public ParkingJSONReader(String fileName, Logger logger) {
		super(fileName, logger);
		
	}

	@Override
	public HashMap<String, List<ParkingViolation>> parseParkingViolations() {

		HashMap<String, List<ParkingViolation>> ret = new HashMap<>();
		
		JSONParser parser = new JSONParser();
		
		try {
			
			JSONArray violations = (JSONArray)parser.parse(fileReader);
			
			Iterator itr = violations.iterator();
		   
			while (itr.hasNext()) {
		    	
				JSONObject violation = (JSONObject) itr.next();
		    	 
		    	int fine = 0;
		    	 
		    	 try{
		    		 Integer.parseInt(violation.get("fine").toString());
		    	 }
		    	 catch(NumberFormatException e) {
		    		 e.printStackTrace();
		    	 }
		    	
		    	 String  state = violation.get("state").toString();
		    	 
		    	 String zipcode = violation.get("zip_code").toString();
		    	 
		    	 ParkingViolation violationToAdd = new ParkingViolation(fine, state, zipcode);
		    	
		   
		    	 if (!ret.containsKey(zipcode)) {
		    		 
		    		 ArrayList<ParkingViolation> violationList = new ArrayList<>();
		    		 violationList.add(violationToAdd);
		    		 ret.put(zipcode, violationList);
		    	 }
		    	 else {
		    		 ret.get(zipcode).add(violationToAdd);
		    	 }
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return ret;
	
	
	}

}
