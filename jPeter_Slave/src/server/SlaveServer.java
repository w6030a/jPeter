package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import client.Job;

import globalDefinition.RespondTimeDefinition;
import utils.IOUtils;
import utils.WorkerRatioParser;
import warehouses.JobRecord;
import warehouses.WorkRecord;
import warehouses.WorkRecordWareHouse;
import worker.CountDownWorkerBase;

public class SlaveServer extends Thread {
	private ServerSocket listener;
	private int slavePort;
	File errorLogDir;
	File errorLogSubDir;
	File errorLog;
	int badGatewayCount, timeoutCount, loginFailCount;
	
	{
		errorLogDir = new File("./jPeter_ErrorLog");
		errorLogDir.mkdir();
		errorLogSubDir = new File(errorLogDir.getAbsoluteFile() + "/Error_Log" + "_" + Calendar.getInstance().getTime().toString().substring(0, 10));
		errorLogSubDir.mkdir();
		badGatewayCount = timeoutCount = loginFailCount = 0;
	}
	
	public SlaveServer(Integer port) {
		this.slavePort = (port == null || port < 0)? 8888 : port ;
		
		try {
			listener = new ServerSocket(slavePort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		errorLog = new File(errorLogSubDir.getAbsoluteFile() + "/Error_Log" + "_p" + slavePort + "_t" + Calendar.getInstance().getTime());
		System.out.println("Zombie slave at port: " + slavePort + " is up waiting for Dr. Isaacs' command...");
		Socket socket = null;
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		try {
			while(!this.isInterrupted()) {
				System.out.println("Waiting for next connection");
				
				synchronized(listener) {
					socket = listener.accept();
				}

				// it's a magic number
				socket.setSoTimeout(600000);
				//not sure if this two should be new at every run
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
				
				System.out.println("Connected, geting command from : " + socket.getInetAddress());
				Job temp = null;
				do {
					temp = (Job) in.readObject();
				} while(temp == null);
				
				System.out.println("Command received: " + temp.toString());
				
				Job command = temp;
				String atkTarget = command.getAtkTarget();
				int numOfConcurrentWorker = command.getNumOfWorkerToCreate();
				String workerRatio = command.getWorkerRatio();
				int serialPrefix = command.getWorkerSerialPrefix();
				int numOfReRun = command.getNumOfRerun();
				boolean isSelfDestruct = command.isLastRun();
				
				WorkerRatioParser.parse(workerRatio);
				WorkRecordWareHouse recordWareHouse= new WorkRecordWareHouse();
				
				
				// re-run for the same number of concurrent workers for better accuracy
				for(int j = 1; j <= numOfReRun; j++ ) {
					CountDownLatch begin = new CountDownLatch(2);
					CountDownLatch end = new CountDownLatch(numOfConcurrentWorker);

					WorkerFactory zf = new WorkerFactory(numOfConcurrentWorker);
					ArrayList<CountDownWorkerBase> zombies = zf.spawnWorkers(atkTarget, serialPrefix, begin, end, recordWareHouse);
					for(CountDownWorkerBase zombie : zombies) {
						zombie.start();
					}
					
					System.out.println("*********************** Start of Round " + j + " for "+ numOfConcurrentWorker + " Zombies ************************");
					// unleash the zombies once all of them are created and ready
					begin.countDown();
					try {
						end.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					for(WorkRecord wr : recordWareHouse.getDiary()) {
						if(wr.isFail()) {
							//System.out.println(wr.getExecutionMessage());
							writeToErrorLog(wr.getExecutionMessage(), numOfConcurrentWorker);
						}
					}
					logIncapabilityError(numOfConcurrentWorker);
				}
				
				// collect work record and do some calculation, wrap the numbers into a job record,
				JobRecord jb = generateJobRecord(numOfConcurrentWorker, recordWareHouse);
				// send report back to Dr. Isaacs
				out.writeObject(jb);
				out.flush();
				out.close();
				in.close();
				
				System.out.println("************************ End for " + numOfConcurrentWorker + " Zombies *************************\n");
				
				if(isSelfDestruct)
					this.interrupt();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(socket);
			IOUtils.closeQuietly(listener);
			System.exit(0);
		}
	}

	private void logIncapabilityError(int numOfConcurrentWorker) {
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(errorLog, true)));
			
			pw.write("Among " + numOfConcurrentWorker + " workers:\n");
			pw.write("# of login fail: " + loginFailCount + "\n");
			pw.write("# of time out: " + timeoutCount + "\n");
			pw.write("# of bad gateway: " + badGatewayCount + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.flush();
			IOUtils.closeQuietly(pw);
		}
		
		badGatewayCount = timeoutCount = loginFailCount = 0;
	}

	private void writeToErrorLog(String errorMessage, int numOfConcurrentWorker) {
		PrintWriter pw = null;
		
		// count 502 bad gateway, 504 timeout, 0 login fail and log afterward 
		if(errorMessage.contains("Code: 502")) {
			badGatewayCount++;
			//return;
		}
		if(errorMessage.contains("Code: 504")) {
			timeoutCount++;
			//return;
		}
		if(errorMessage.contains("Login Fail")) {
			loginFailCount++;
			//return;
		}
		
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(errorLog, true)));
			pw.write("Among " + numOfConcurrentWorker + " workers, " + errorMessage + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.flush();
			IOUtils.closeQuietly(pw);
		}
	}

	private JobRecord generateJobRecord(int numOfConcurrentWorker, WorkRecordWareHouse recordWareHouse) {
		JobRecord jb = new JobRecord();
		
		jb.setNumOfConcurrentWorker(numOfConcurrentWorker)
			.setFailCount(recordWareHouse.getFailCount())
			.setThroughput(recordWareHouse.getThroughput());
		
		for(String key : RespondTimeDefinition.RESPOND_TIMES) {
			jb.appendExecutionTime(key + "Avg", recordWareHouse.getAvgMillTime(key)/1000.0);
			jb.appendExecutionTime(key + "SD", recordWareHouse.getStandardDeviation(key)/1000.0);
		}
		
		return jb; 
	}
}
