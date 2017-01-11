package utils;

public class WorkerRatioParser {
	static String[] numerators;
	static int denominator;
	static int numOfFixedNumOfWorkers;
	
	private WorkerRatioParser(String workerRatio) {
		WorkerRatioParser.numerators = parseNumerators(workerRatio);
		WorkerRatioParser.denominator = calculateDenominator();
		WorkerRatioParser.numOfFixedNumOfWorkers = calculateNumOfFixedNumOfWorkers();
	}
	
	public static void parse(String workerRatio) {
		new WorkerRatioParser(workerRatio);
	}
	
	public static String[] getNumerators() {
		return numerators;
	}

	public static int getDenominator() {
		return denominator;
	}
	
	public static int getNumOfFixedNumOfWorkers() {
		return numOfFixedNumOfWorkers;
	}
	
	private String[] parseNumerators(String workerRatio) {
		return workerRatio.split(":");
	}
	
	private int calculateDenominator() {
		int denominator = 0;
		for(String s : getNumerators()) {
			if(Integer.parseInt(s) > 0)
				denominator += Integer.parseInt(s);
		}
		return denominator;
	}
	
	private int calculateNumOfFixedNumOfWorkers() {
		int numOfFixedNumOfWorkers = 0;
		for(String s : getNumerators()) {
			if(Integer.parseInt(s) < 0)
				numOfFixedNumOfWorkers += Math.abs(Integer.parseInt(s));
		}
		return numOfFixedNumOfWorkers;
	}
}
