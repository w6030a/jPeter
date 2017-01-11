package worker;

import globalDefinition.HttpDefinition;
import globalDefinition.RespondTimeDefinition;
import httpCommand.HttpRequest;
import java.util.concurrent.CountDownLatch;
import utils.HttpUtils;
import warehouses.WorkRecordWareHouse;

public class ExampleCountDownWorker extends CountDownWorkerBase {
	int serialPrefix;
	String accessToken = "JWT" + " ";
	
	public ExampleCountDownWorker(int id, int serialPrefix, String atkTarget, CountDownLatch begin, CountDownLatch end, WorkRecordWareHouse recordWareHouse) {
		super(id, atkTarget, begin, end, recordWareHouse);
		this.serialPrefix = serialPrefix;
		workRecord.appendExecutionMessage(serialPrefix + " ");
	}

	@Override
	protected void doBefore() {
		waitForConcurrentStart();
		workRecord.setStartTime(System.currentTimeMillis());
	}

	@Override
	protected void doSetUp() {
		login();
		postRegisterDevice();
	}
	
	@Override
	protected void doOperation() {
		getServiceInfo();
		postChangeUserAccount();
		workRecord.setEndTime(System.currentTimeMillis());
	}

	@Override
	protected void doRecord() {
		if (fail) {
			workRecord.setFail(true);
			workRecord.appendExecutionMessage(failMessage);
		}
		else
			workRecord.appendExecutionMessage("completes all tasks successfully");
	}
	
	@Override
	protected void doAfter() {
		storeRecord();
		notifyComplettion();
	}
	
	/*
	 * Example operations
	 */
	public void login() {
		if (fail)
			return;

		StringBuilder userLoginInfo = new StringBuilder();
		HttpRequest loginRequest = new HttpRequest();
		
		userLoginInfo
		.append("{\"backend\":\"google-oauth2\"")
		.append(",\"code\":\"helloWorld" + serialPrefix + "_" + id + "@gmail.com\"}");
		
		loginRequest
		.setForceHttps(true)
		.setServerName(atkTarget)
		.setMethod("post")
		.setResourcePath("/api/some_API/?format=json")
		.setHeader(HttpDefinition.CONTENT_TYPE_JSON)
		.setData(userLoginInfo.toString());

		execute(loginRequest);
		if(fail)
			return;
		
		workRecord.setRespondTime(RespondTimeDefinition.LOGIN_TIME, loginRequest.getElapsedTime());
		accessToken += (HttpUtils.extractValue(loginRequest.getResponseMessage(), "token")).get(0);
	}
	
	public void postRegisterDevice() {
	}
	
	public void getServiceInfo() {
	}
	
	public void postChangeUserAccount() {
	}
}