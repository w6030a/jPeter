package options;

public class Option {
	String key;
	String value;
	String description;
	int numOfParameters;
	
	public Option (String key, int numOfParameters, String description) {
		this.key = key;
		this.numOfParameters = numOfParameters;
		this.description = description;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean hasParameter() {
		return this.numOfParameters > 0;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public int getNumOfParameters() {
		return this.numOfParameters;
	}
}
