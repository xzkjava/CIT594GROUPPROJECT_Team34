package edu.upenn.cit594;

import java.io.File;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.processor.ProcessorFactory;
import edu.upenn.cit594.ui.CommandLineUserInterface;

public class Main {
	
	public static void main(String[ ] args) {
		

		verifyArguments(args);
		
		String parkingFileType = args[0];
		
		String parkingFileName = args[1];
		
		String propertyFileName = args[2];
		
		String populationFileName = args[3];
		
		String logFileName = args[4];
        
		Logger.setFileName(logFileName);
		
		Logger logger = Logger.getInstance();
		
		logger.log(String.valueOf(System.currentTimeMillis()) + " "
				+ parkingFileType + " "
				+ parkingFileName + " "
				+ propertyFileName + " "
				+ populationFileName + " "
				+ logFileName + "\n");
			
		Processor processor = ProcessorFactory.createProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName);
					
		CommandLineUserInterface userInterface = new CommandLineUserInterface(processor);
		
		userInterface.respondToUserInput();
		
		logger.close();
		
	}
	
	public static void verifyArguments(String[ ] args) {
		
		
		if(args == null ||args.length != 5) {
			System.out.println("ERROR: The number of arguments passed to the main ( ) should be 5.");
			System.exit(1);
		}
		
		String type = args[0];
		
		String parkingFileName = args[1];
		
		String propertyFileName = args[2];
		
		String populationFileName = args[3];
		
		String logFileName = args[4];
		
		if(!type.equals("csv") && !type.equals("json")) {
			System.out.println("ERROR: The first argument should be 'csv' or 'json'.");
			System.exit(1);
		}
		else if(parkingFileName != null && !parkingFileName.isEmpty()) {
			File parkingFile = new File(parkingFileName);
			if(!parkingFile.exists() || !parkingFile.canRead()) {
				System.out.println("ERROR: the parking violation file doesn't exist or can't be opened to read.");
				System.exit(1);
			}
		}
		else if(propertyFileName != null && !propertyFileName.isEmpty()) {
			File propertyFile = new File(propertyFileName);
			if(!propertyFile.exists() || !propertyFile.canRead()) {
				System.out.println("ERROR: the property file doesn't exist or can't be opened to read.");
				System.exit(1);
			}
		}
		else if(populationFileName != null && !populationFileName.isEmpty()) {
			File populationFile = new File(populationFileName);
			if(!populationFile.exists() ||!populationFile.canRead()) {
				System.out.println("ERROR: the population file doesn't exist or can't be opened to read.");
				System.exit(1);
			}
		}
		else if(logFileName != null && !logFileName.isEmpty()) {
			System.out.println("ERROR: the log file provided shouldn't be null or empty.");
			System.exit(1);
			
		}
		
	}
	
	
	
	

}
