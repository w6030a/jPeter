package warehouses;

import globalDefinition.RespondTimeDefinition;
import java.util.HashMap;

public class WorkRecord {

	int id = -1;
	long startTime = 0;
	long endTime = 0;
	
	HashMap<String, Long> respondTimes = new HashMap<String, Long>();
	
	public Long getRespondTime(String targetOperation) {
		return this.respondTimes.get(targetOperation);
	}
	
	public void setRespondTime(String targetOperation, Long respondTime) {
		this.respondTimes.put(targetOperation, respondTime);
	}
	
	boolean fail = false;
	String executionMessage = "";
	
	int numOfRequest = 0;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public boolean isFail() {
		return fail;
	}
	
	public void setFail(boolean fail) {
		this.fail = fail;
	}

	public void appendExecutionMessage(String executionMessage) {
		this.executionMessage += executionMessage;
	}
	
	public String getExecutionMessage() {
		return this.executionMessage;
	}
	
	public int getNumOfRequest() {
		return this.respondTimes.size();
	}
	
	public long getTotalRequestRespondTime() {
		if(this.respondTimes.size() <= 0)
			return 0;
		
		Long accumulator = 0L;
		for(String key : RespondTimeDefinition.RESPOND_TIMES) {
			Long temp = this.respondTimes.get(key);
			if(temp != null)
				accumulator += respondTimes.get(key);
		}
		
		return accumulator;
	}
}


