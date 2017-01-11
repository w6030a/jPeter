package warehouses;

import java.util.ArrayList;

public class WorkRecordWareHouse {
	private ArrayList<WorkRecord> workDiary = new ArrayList<WorkRecord>();

	public void appendRecord(WorkRecord record) {
		workDiary.add(record);
	}

	public ArrayList<WorkRecord> getDiary() {
		return workDiary;
	}
	
	public long getAvgMillTime(String targetOperation) {
		long accumulator = 0;
		long count = 0;
		for(WorkRecord wr : this.getDiary()) {
			Long temp = wr.getRespondTime(targetOperation);
			if(temp != null) {
				accumulator += temp;
				count++;
			}
		}
		return count == 0? 0 : accumulator/count;
	}
	
	public double getStandardDeviation(String targetOperation) {
		long avg = getAvgMillTime(targetOperation);
		long accumulator = 0;
		long count = 0;
		for(WorkRecord wr : this.getDiary()) {
			Long temp = wr.getRespondTime(targetOperation);
			if(temp != null) {
				accumulator += Math.pow((temp - avg), 2);
				count++;
			}
		}
		return count == 0? 0 : Math.sqrt(accumulator / count);
	}

	public int getFailCount() {
		int count = 0;
		for(WorkRecord wr : this.getDiary()) {
			if(wr.isFail())
				count++;
		}
		return count;
	}
	
	public int getSuccessCount() {
		int count = 0;
		for(WorkRecord wr : this.getDiary()) {
			if(!wr.isFail())
				count++;
		}
		return count;
	}
	
	public float getThroughput() {
		if(getTotalRespondTime() <= 0)
			return 0;
		else
			return (float) ((float) getTotalNumOfSucceedRequest() / ((float) getTotalRespondTime() / 1000.0));
	}
	
	public int getTotalNumOfSucceedRequest() {
		int accumulator = 0;
		for(WorkRecord wr : this.getDiary()) {
			if(!wr.isFail())
				accumulator += wr.getNumOfRequest();
		}
		return accumulator;
	}
	
	public long getTotalRespondTime() {
		long firstStart = Long.MAX_VALUE;
		long lastEnd = 0;
		
		for(WorkRecord wr : this.getDiary()) {
			if(!wr.isFail())
				if(wr.getStartTime() < firstStart)
					firstStart = wr.getStartTime();
				if(wr.getEndTime() > lastEnd)
					lastEnd = wr.getEndTime();
		}
		
		return lastEnd - firstStart;
	}
}
