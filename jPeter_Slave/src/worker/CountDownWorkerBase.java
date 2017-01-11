package worker;

import java.util.concurrent.CountDownLatch;
import httpCommand.HttpRequest;
import httpCommand.HttpRequestManager;
import warehouses.WorkRecord;
import warehouses.WorkRecordWareHouse;

public abstract class CountDownWorkerBase extends Thread {
	CountDownLatch begin;
	CountDownLatch end;

	HttpRequestManager httpRequestManager;
	WorkRecordWareHouse recordWareHouse;
	WorkRecord workRecord;
	
	int id;
	String atkTarget;
	
	boolean fail = false;
	String failMessage = "";

	public CountDownWorkerBase(int id, String atkTarget, CountDownLatch begin, CountDownLatch end, WorkRecordWareHouse recordWareHouse) {
		this.id = id;
		this.atkTarget = atkTarget;

		this.begin = begin;
		this.end = end;
		this.httpRequestManager = new HttpRequestManager();
		this.recordWareHouse = recordWareHouse;
		
		this.workRecord = new WorkRecord();
		workRecord.setId(id);
		workRecord.appendExecutionMessage("Worker " + id + " from machine ");
	}
	
	protected abstract void doBefore();
	protected abstract void doSetUp();
	protected abstract void doOperation();
	protected abstract void doRecord();
	protected abstract void doAfter();
	
	@Override
	public void run() {
		doBefore();
		doSetUp();
		doOperation();
		doRecord();
		doAfter();
	}
	
	final protected boolean execute(HttpRequest request) {
		if(!httpRequestManager.execute(request) || request.getResponseCode() > 399) {
			fail = true;
			failMessage += String.format("%s%s\n%s\nCode: %s\n%s\n",
					request.toString(),
					request.getMethod().toUpperCase(),
					request.getResourcePath(),
					request.getResponseCode(),
					request.getResponseMessage());
			return false;
		}
		return true;
	}
	
	final protected void storeRecord() {
		recordWareHouse.appendRecord(workRecord);
	}
	
	final protected void waitForConcurrentStart() {
		try {
			begin.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	final protected void notifyConcurrentStart() {
		begin.countDown();
	}
	
	final protected void notifyComplettion() {
		end.countDown();
	}
}
