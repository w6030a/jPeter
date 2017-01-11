package globalDefinition;

public class OptionDefinition {

	public static final String OPTION_KEY_TARGET = "-t";
	public static final String OPTION_KEY_SLAVE = "-s";
	public static final String OPTION_KEY_RERUN = "-r";
	public static final String OPTION_KEY_END = "-e";
	public static final String OPTION_KEY_WORKER_RATIO = "-w";
	public static final String OPTION_KEY_INTERVAL = "-i";
	public static final String OPTION_KEY_HELP = "-h";
	
	public static final String OPTION_DESC_TARGET = "AtkTarget, an IP or a name. The server which would be attack by this program";
	public static final String OPTION_DESC_SLAVE = "Slave's IP and Port separated by column, appended with comma."
			+ "For instance, if two slaves are used for attack, the input would look like \"--s=127.0.0.1:8888,127.0.0.2:8889\"";
	public static final String OPTION_DESC_RERUN = "Number of reruns (Int). Each round of attack will be repeated for this number fo times, for better accuracy for attacks with small number of workers";
	public static final String OPTION_DESC_END = "Number of workers to end (Int). The program ends when this number of workers is reached"
			+ " or when the number of workers for next run exceeds this number";
	public static final String OPTION_DESC_WORKER_RATIO = "Ratio of workers of customized types."
			+ " Negative number for creating EXACT number of that type of worker"
			+ " Positive number for creating that type workers according to given ratio"
			+ " For instance, \"--w=-1:3:1\" would cause the program to create exact 1 worker of the first type, and workers of the second and third type splits the remaining quota."
			+ " ** This parameter determines the initial number of workers and the number of workers to increment at each round.**"
			+ " The initial number of workers would be the sum of all negative numbers, and the number of workers to increment will be the sum of all positive numbers**";
	public static final String OPTION_DESC_INTERVAL = "Interval of each run in seconds";
	public static final String OPTION_DESC_HELP = "Show help menu";
}
