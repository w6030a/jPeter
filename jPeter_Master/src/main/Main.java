package main;

import globalDefinition.OptionDefinition;
import options.Option;
import options.Options;
import client.MasterClient;

public class Main {
	static String atkTarget;
	static String[] workerSlaves;
	static Integer numOfRerun;
	static Integer numOfWorkerToEnd;
	static String ratioOfWorkers;
	static Integer runInterval;
	public static Options supportedOptions = new Options();
	
	static {
		supportedOptions
		.addOption(new Option(OptionDefinition.OPTION_KEY_TARGET, 1, OptionDefinition.OPTION_DESC_TARGET))
		.addOption(new Option(OptionDefinition.OPTION_KEY_SLAVE, 1, OptionDefinition.OPTION_DESC_SLAVE))
		.addOption(new Option(OptionDefinition.OPTION_KEY_RERUN, 1, OptionDefinition.OPTION_DESC_RERUN))
		.addOption(new Option(OptionDefinition.OPTION_KEY_END, 1, OptionDefinition.OPTION_DESC_END))
		.addOption(new Option(OptionDefinition.OPTION_KEY_WORKER_RATIO, 1, OptionDefinition.OPTION_DESC_WORKER_RATIO))
		.addOption(new Option(OptionDefinition.OPTION_KEY_INTERVAL, 1, OptionDefinition.OPTION_DESC_INTERVAL))
		.addOption(new Option(OptionDefinition.OPTION_KEY_HELP, 0, OptionDefinition.OPTION_DESC_HELP));
	}
	
	public static void main(String[] args) {
		// using TLS for sending https request
		System.setProperty("https.protocols", "TLSv1.1");
		
		/*
		 * Parameters for Master
		 * 
		 * --t : atkTarget, an IP or a name
		 * --s : slave's IP and Port separated by column, appended with comma
		 * --r : number of reruns
		 * --e : number of workers to end
		 * --w : ratio of workers of customized types
		 * --i : interval of each run in seconds
		 * --h : show help menu
		 */
		supportedOptions.parse(args);
		atkTarget = supportedOptions.getValue(OptionDefinition.OPTION_KEY_TARGET);
		workerSlaves = supportedOptions.getValue(OptionDefinition.OPTION_KEY_SLAVE).split(",");
		numOfRerun = Integer.parseInt(supportedOptions.getValue(OptionDefinition.OPTION_KEY_RERUN));
		numOfWorkerToEnd = Integer.parseInt(supportedOptions.getValue(OptionDefinition.OPTION_KEY_END));
		ratioOfWorkers = supportedOptions.getValue(OptionDefinition.OPTION_KEY_WORKER_RATIO);
		runInterval = Integer.parseInt(supportedOptions.getValue(OptionDefinition.OPTION_KEY_INTERVAL));
		
		(new MasterClient(atkTarget, workerSlaves, numOfRerun, numOfWorkerToEnd, ratioOfWorkers, runInterval)).run();
	}
}
