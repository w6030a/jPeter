package httpCommand;

import java.util.LinkedList;


public class HttpRequestManager {
	private LinkedList<HttpRequest> commandHistory;
	
	public HttpRequestManager() {
		commandHistory = new LinkedList<HttpRequest>();
	}
	
	public boolean execute(HttpRequest httpCommand) {
		boolean success = false;
		
		if(httpCommand.getMethod().equalsIgnoreCase("POST"))
			success = httpCommand.doPost();
		else if(httpCommand.getMethod().equalsIgnoreCase("GET"))
			success = httpCommand.doGet();
		else
			throw new RuntimeException("Unsupported Http Method!");
		
		if(success)
			commandHistory.add(httpCommand);
		
		return success;
	}
	
	public LinkedList<HttpRequest> getCommandHistory() {
		return this.commandHistory;
	}
}
