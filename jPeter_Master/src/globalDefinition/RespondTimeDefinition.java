package globalDefinition;

public class RespondTimeDefinition {
	/*
	 * Respond times desired to record.
	 * They are used as keys.
	 * Define them yourself.
	 * Both the Master and the Slave have to have the same copy of it.
	*/
	
	/*
	 * Example
	 */
	public static final String LOGIN_TIME = "loginTime";
	public static final String REGISTER_DEVICE_TIME = "registerDeviceTime";
	public static final String GET_SERVICE_TIME = "getServiceTime";
	public static final String CHANGE_ACCOUNT_EMAIL_TIME = "changeAccountEmailTime";
	public static final String LOCK_DEVICE_TIME = "lockDeviceTime";
	public static final String UNLOCK_DEVICE_TIME = "unlockDeviceTime";
	
	// Pack them together for ez enumeration
	public static final String[] RESPOND_TIMES = {
		LOGIN_TIME, 
		REGISTER_DEVICE_TIME, 
		GET_SERVICE_TIME,
		CHANGE_ACCOUNT_EMAIL_TIME, 
		LOCK_DEVICE_TIME, 
		UNLOCK_DEVICE_TIME
	};
}
