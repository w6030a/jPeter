package main;

import globalDefinition.OptionDefinition;
import options.Option;
import options.Options;
import server.SlaveServer;

public class Main {
	
	public static Options supportedOptions = new Options();
	static Integer port;
	
	static {
		/*
		 * Parameters for Slave
		 * 
		 * --p : port number
		 * --h : help menu
		 */
		supportedOptions
		.addOption(new Option(OptionDefinition.OPTION_KEY_HELP, 0, OptionDefinition.OPTION_DESC_HELP))
		.addOption(new Option(OptionDefinition.OPTION_KEY_PORT, 1, OptionDefinition.OPTION_DESC_PORT));
	}
	
	public static void main(String[] args) {
		// using TLS for sending https request
		System.setProperty("https.protocols", "TLSv1.1");
		
		supportedOptions.parse(args);
		port = Integer.parseInt(supportedOptions.getValue(OptionDefinition.OPTION_KEY_PORT));
		
		(new SlaveServer(port)).start();
	}
}
