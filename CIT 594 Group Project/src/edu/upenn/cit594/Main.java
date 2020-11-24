package edu.upenn.cit594;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.ui.CommandLineUserInterface;

public class Main {
	
	public static void main(String[ ] args) {
		

		verifyArguments(args);
		
		String parkingFileType = args[0];
		
		String parkingFileName = args[1];
		
		String propertyFileName = args[2];
		
		String populationFilename = args[3];
		
		String logFileName = args[4];
		
		Logger.setFileName(logFileName);
		
		Logger logger = Logger.getInstance();
		
		logger.log(String.valueOf(System.currentTimeMillis()) + " "
				+ parkingFileType + " "
				+ parkingFileName + " "
				+ propertyFileName + " "
				+ populationFilename + " "
				+ logFileName + "\n");
		
		CommandLineUserInterface userInterface = new CommandLineUserInterface(parkingFileType, parkingFileName, propertyFileName, populationFilename, logger);
		
		userInterface.respondToUserInput();
		
		logger.close();
		
	}
	
	public static void verifyArguments(String[ ] args) {
		
		//TODO
		
	}
	
	
	
	

}
