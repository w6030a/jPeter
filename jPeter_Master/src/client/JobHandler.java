package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import utils.IOUtils;
import warehouses.JobRecord;

public class JobHandler extends Thread {
	String slaveAddr;
	int slavePort;
	Job job;
	Socket masterClient = null;
	CountDownLatch jobStart;
	CountDownLatch jobDone;
	JobRecord jobRecord;
	
	public JobHandler(String slaveIP, String slavePort) {
		this.slaveAddr = slaveIP;
		this.slavePort = Integer.parseInt(slavePort);
	}
	
	public void run() {
		ObjectInputStream  in = null;
		ObjectOutputStream out = null;
		
		if(masterClient != null) {
			try {
				out = new ObjectOutputStream(masterClient.getOutputStream());
				out.writeObject(job);
				out.flush();
				
				in = new ObjectInputStream(masterClient.getInputStream());
				
				JobRecord temp = null;
				do {
					temp = (JobRecord) in.readObject();
				} while(temp == null);
				jobRecord = temp;
				
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Communication with " + slaveAddr + ":" + slavePort + " failed..");
				e.printStackTrace();
				//System.exit(1);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(masterClient);
				masterClient = null;
			}
		}
		
		jobDone.countDown();
	}
	
	public void connect() {
		if(job.getNumOfWorkerToCreate() == 0)
			return;
		
		if(masterClient == null) {
			masterClient = new Socket();
			InetSocketAddress isa = new InetSocketAddress(slaveAddr, slavePort);
			try {
				masterClient.connect(isa, 15000);
			} catch (IOException e) {
				e.printStackTrace();
				masterClient = null;
				throw new RuntimeException("Connecting to " + slaveAddr + ":" + slavePort + " failed..");
			}
		}
	}

	public void assign(Job job) {
		this.job= job;
	}
	
	public void setStartLatch(CountDownLatch jobStart) {
		this.jobStart = jobStart;
	}
	
	public void setEndLatch(CountDownLatch jobDone) {
		this.jobDone = jobDone;
	}
	
	public JobRecord getJobRecord() {
		return this.jobRecord;
	}
}
