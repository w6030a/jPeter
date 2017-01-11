package warehouses;

import globalDefinition.RespondTimeDefinition;

import java.io.Serializable;
import java.util.HashMap;

public class JobRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int numOfConcurrentWorker;
	private int failCount;
	private double throughput;
	private HashMap<String, Double> executionTimes = new HashMap<String, Double>();
	
	public int getNumOfConcurrentWorker() {
		return numOfConcurrentWorker;
	}
	public JobRecord setNumOfConcurrentWorker(int numOfConcurrentWorker) {
		this.numOfConcurrentWorker = numOfConcurrentWorker;
		return this;
	}
	public int getFailCount() {
		return failCount;
	}
	public JobRecord setFailCount(int failCount) {
		this.failCount = failCount;
		return this;
	}
	public double getThroughput() {
		return throughput;
	}
	public JobRecord setThroughput(double throughput) {
		this.throughput = throughput;
		return this;
	}
	public HashMap<String, Double> getExecutionTimes() {
		return executionTimes;
	}
	public Double getExecutionTime(String key) {
		return executionTimes.get(key);
	}
	public void appendExecutionTime(String key, Double value) {
		this.executionTimes.put(key, value);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#ofWorker=").append(this.getNumOfConcurrentWorker()).append(", ");
		sb.append("#ofFail=").append(this.getFailCount()).append(", ");
		sb.append("Throughput=").append(this.getThroughput()).append(", ");
		
		for(String time : RespondTimeDefinition.RESPOND_TIMES) {
			sb.append(time + "Avg=").append(this.getExecutionTime(time + "Avg")).append(", ");
			sb.append(time + "SD=").append(this.getExecutionTime(time + "SD")).append(", ");
		}
		return sb.toString();
	}
}
