package edu.upenn.cit594.logging;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	private static Logger instance;
	
	private FileWriter out;
	
	private static String fileName;
	
	private Logger() {
		try {
			out = new FileWriter(fileName, true);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static Logger getInstance() {
		
		if(instance == null) {
			return new Logger();
		}
		
		return instance;
		
	}
	
	public static void setFileName(String fileN) {
		
		fileName = fileN;
	
	}
	
	public void log(String message) {
		
		try {
			out.write(message);
			out.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
