package client;

import globalDefinition.RespondTimeDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import utils.IOUtils;
import utils.WorkerRatioParser;
import warehouses.JobRecord;
import warehouses.JobRecordWareHouse;

public class MasterClient {
	int numOfSlaves;
	String atkTarget;
	int numOfRerun;
	int numOfWorkerToStartWith;
	int numOfWorkerToEnd;
	int numOfWorkerToIncrement;
	String ratioOfWorkers;
	int runInterval;
	static final String FORMATER_FLOAT_5_DIGIT = "%.5f";
	File resultDir;
	File resultFile;
	String[] zombieSlaves;
	
	// initializer
	{
		resultDir = new File("./jPeter_Result");
		resultDir.mkdir();
	}
	
	public MasterClient(String atkTarget, String[] zombieSlaves, int numOfRerun, int numOfWorkerToEnd, String ratioOfWorkers, int runInterval) {
		this.zombieSlaves = zombieSlaves;
		this.numOfSlaves = zombieSlaves.length;
		this.atkTarget = atkTarget;
		this.numOfRerun = numOfRerun;
		this.ratioOfWorkers = ratioOfWorkers;
		this.runInterval = runInterval;
		WorkerRatioParser.parse(ratioOfWorkers);
		this.numOfWorkerToStartWith = WorkerRatioParser.getNumOfFixedNumOfWorkers();
		this.numOfWorkerToEnd = numOfWorkerToEnd;
		this.numOfWorkerToIncrement = WorkerRatioParser.getDenominator();

		resultFile = new File(resultDir.getAbsoluteFile() + "/Work_Diary" + "_ns" + numOfSlaves + "_nzs" + numOfWorkerToStartWith + "_nze" + numOfWorkerToEnd + "_t" + Calendar.getInstance().getTime());
	}
	
	public void run() {
		writeReportHeader();
		
		for(int numOfWorkerForTheRun = numOfWorkerToStartWith; numOfWorkerForTheRun <= numOfWorkerToEnd; numOfWorkerForTheRun += numOfWorkerToIncrement) {
			CountDownLatch jobStart = new CountDownLatch(numOfSlaves);
			CountDownLatch jobDone = new CountDownLatch(numOfSlaves);
			
			ArrayList<JobHandler> jobHandlers = new ArrayList<JobHandler>();
			for(String s : zombieSlaves) {
				String[] slaveIPandPort = s.split(":");
				jobHandlers.add(new JobHandler(slaveIPandPort[0], slaveIPandPort[1]));
			}
			
			//assign # of workers to server handler
			int quotient = numOfWorkerForTheRun / numOfSlaves;
			int remainder = numOfWorkerForTheRun % numOfSlaves;
			for(int i = 0; i < numOfSlaves; i++) {
				int numOfWorkerToCreate = quotient;
				
				if(remainder > 0) {
					numOfWorkerToCreate++;
					remainder--;
				}
				
				jobHandlers.get(i).setStartLatch(jobStart);
				jobHandlers.get(i).setEndLatch(jobDone);
				jobHandlers.get(i).assign(
						new Job().setAtkTarget(atkTarget)
						.setNumOfWorkerToCreate(numOfWorkerToCreate)
						.setWorkerRatio(ratioOfWorkers)
						.setWorkerSerialPrefix(i+1)
						.setNumOfRerun(numOfRerun)
						.setIsLastRun(numOfWorkerForTheRun + numOfWorkerToIncrement > numOfWorkerToEnd));
			}
			
			for(JobHandler jh : jobHandlers)
				jh.connect();

			for(JobHandler jh : jobHandlers)
				jh.start();

			// wait for all slaves finish their work
			try {
				jobDone.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i = 0; i < numOfSlaves; i++) {
				if(jobHandlers.get(i).getJobRecord() != null)
					System.out.println("Machine: " + i + " has result: " + jobHandlers.get(i).getJobRecord().toString());
			}

			JobRecordWareHouse.push(mergeJobRecord(jobHandlers));
			writeReportRecord();
			
			// wait for seconds before next run
			try {
				Thread.sleep(runInterval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String mergeJobRecord(ArrayList<JobHandler> jobHandlers) {
		StringBuilder sb = new StringBuilder();
		int workerCount = 0;
		int failCount = 0;
		double throughput = 0;
		
		for(JobHandler jh : jobHandlers) {
			if(jh.getJobRecord() == null)
				break;
			JobRecord record = jh.getJobRecord();
			workerCount += record.getNumOfConcurrentWorker();
			failCount += record.getFailCount();
			throughput += record.getThroughput();
		}
		sb.append(workerCount).append(", ");
		sb.append(failCount).append(", ");
		sb.append(throughput).append(", ");
		
		for(String time : RespondTimeDefinition.RESPOND_TIMES) {
			double tempAvgAccumulator = 0;
			double tempSDAccumulator = 0;
			for(JobHandler jh : jobHandlers) {
				if(jh.getJobRecord() == null)
					break;
				JobRecord record = jh.getJobRecord();
				tempAvgAccumulator += record.getExecutionTime(time + "Avg") * record.getNumOfConcurrentWorker();
				tempSDAccumulator += record.getExecutionTime(time + "SD") * record.getNumOfConcurrentWorker();;
			}
			sb.append(String.format(FORMATER_FLOAT_5_DIGIT, tempAvgAccumulator / workerCount)).append(", ");
			sb.append(String.format(FORMATER_FLOAT_5_DIGIT, tempSDAccumulator / workerCount)).append(", ");
		}
		
		String mergedResult = sb.toString();
		System.out.println("Job merged: " + mergedResult + "\n");
		return mergedResult.substring(0, mergedResult.length()-1);
	}
	
	private void writeReportHeader() {
		PrintWriter pw = null;
		try {
			StringBuilder header = new StringBuilder();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(resultFile, true)));
			header.append("NumOfWorker").append(", ");
			header.append("Fails").append(", ");
			header.append("Throughput").append(", ");
			for(String time : RespondTimeDefinition.RESPOND_TIMES) {
				header.append("Avg " + time).append(", ");
				header.append("SD of " + time).append(", ");
			}
			header.deleteCharAt(header.length() - 2).append("\n");
			pw.write(header.toString());
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(pw);
		}
	}
	
	private void writeReportRecord() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(resultFile, true)));
			pw.write(JobRecordWareHouse.pop() + "\n");
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(pw);
		}
	}
}
