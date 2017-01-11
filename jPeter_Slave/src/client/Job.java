package client;

import java.io.Serializable;

public class Job implements Serializable{
	private static final long serialVersionUID = 2L;
	
	private String attackTarget;
	private int numOfWorkerToCreate;
	private String workerRatio;
	private int workerSerialPrefix;
	private int numOfRerun;
	private boolean isLastRound;

	public String getAtkTarget() {
		return attackTarget;
	}
	public Job setAtkTarget(String attackTarget) {
		this.attackTarget = attackTarget;
		return this;
	}
	public int getNumOfWorkerToCreate() {
		return numOfWorkerToCreate;
	}
	public Job setNumOfWorkerToCreate(int numOfWorkerToCreate) {
		this.numOfWorkerToCreate = numOfWorkerToCreate;
		return this;
	}
	public String getWorkerRatio() {
		return workerRatio;
	}
	public Job setWorkerRatio(String workerRatio) {
		this.workerRatio = workerRatio;
		return this;
	}
	public int getWorkerSerialPrefix() {
		return workerSerialPrefix;
	}
	public Job setWorkerSerialPrefix(int workerSerialPrefix) {
		this.workerSerialPrefix = workerSerialPrefix;
		return this;
	}
	public int getNumOfRerun() {
		return numOfRerun;
	}
	public Job setNumOfRerun(int numOfRerun) {
		this.numOfRerun = numOfRerun;
		return this;
	}
	public boolean isLastRun() {
		return isLastRound;
	}
	public Job setIsLastRun(boolean isLastRound) {
		this.isLastRound = isLastRound;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append("attackTarget=").append(this.attackTarget).append(", ")
			.append("numOfWorker=").append(this.numOfWorkerToCreate).append(", ")
			.append("workerRatio=").append(this.workerRatio).append(", ")
			.append("workerSerialPrefix=").append(this.workerSerialPrefix).append(", ")
			.append("numOfRerun=").append(this.numOfRerun).append(", ")
			.append("isLastRound=").append(this.isLastRound);
		
		return temp.toString();
	}
}
