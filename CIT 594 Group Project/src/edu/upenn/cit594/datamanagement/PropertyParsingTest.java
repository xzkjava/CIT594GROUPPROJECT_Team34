package edu.upenn.cit594.datamanagement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.logging.Logger;

class PropertyParsingTest {

	@Test
	void testParsingWithMissingFields() {
		
		//arrange and act
		Logger.setFileName("test1Log.txt");
		Logger log = Logger.getInstance();
		PropertyReader reader = new PropertyReader("test1.txt");
		Map<String, List<Property>> test = reader.parseProperties(); 
		
		//act and assert
		assertTrue("test1 should have 5 key-value pairs", test.keySet().size() == 5);		
		assertTrue("key 11111 should have 2 properties", test.get("11111").size() == 2);
	    assertTrue("key 22222 should have 2 properties", test.get("22222").size() == 2);
	    assertTrue("key 33333 should have 1 property", test.get("33333").size() == 1);
	    assertTrue("key 44444 should have 1 properties", test.get("44444").size() == 1);
	    assertTrue("key 55555 should have 2 properties", test.get("55555").size() == 2);
	    assertTrue("key 55555 should not have marketValue", test.get("55555").get(0).getMarketValue().isEmpty());
	    assertTrue("key 55555 should not have area", test.get("55555").get(1).getTotalLivableArea().isEmpty());
	    assertTrue("key 5555 should not have marketValue", test.get("55555").get(1).getTotalLivableArea().isEmpty());
	    assertTrue("key 11111 1 should have area 1000", test.get("11111").get(0).getTotalLivableArea().equals("1000"));
	    assertTrue("key 22222 1 should have area 2000", test.get("22222").get(1).getTotalLivableArea().equals("2000"));
	    assertTrue("key 22222 0 should have area 2000", test.get("22222").get(0).getTotalLivableArea().equals("2000"));
	    //assertTrue("key 33333 0 should have no area", test.get("33333"))
	}
	
	@Test
	void testRemoveCommaInQuote() {
		//arrange
		String test1 = "this is testing ,\" this is, testing, this is testing\"";
		
		Logger.setFileName("test1Log.txt");
		Logger log = Logger.getInstance();
		PropertyReader reader = new PropertyReader("test1.txt");
		
		//act and assert
		
		//test1 = reader.removeCommaInsideQuote(test1);
		
		//assertTrue("String 1 test failed", test1.equals("this is testing ,\" this is  testing  this is testing\""));
		
		
		
	}

}
