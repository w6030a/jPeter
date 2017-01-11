package options;

import globalDefinition.OptionDefinition;

import java.util.ArrayList;

public class Options {
	ArrayList<Option> options = new ArrayList<Option>();
	
	public ArrayList<Option> getOptions() {
		return this.options;
	}
	
	public Options addOption(Option option) {
		this.options.add(option);
		return this;
	}
	
	public Options addOptions(Options options) {
		this.options.addAll(options.getOptions());
		return this;
	}
	
	public Option getOption(String key) {
		Option temp = null;
		for(Option option : options)
			if(option.getKey().equals(key))
				temp = option;
		
		return temp;
	}
	
	public String getValue(String key) {
		String temp = null;
		for(Option option : options)
			if(option.getKey().equals(key))
				temp = option.getValue();
		
		return temp;
	}
	
	public void parse(String[] args) {
		try {
			for(String arg : args) {
				String[] keyValuePair = arg.split("=");
				String key = keyValuePair[0];
				String value = keyValuePair[1];
				getOption(key).setValue(value);
			}
			
			if(getValue(OptionDefinition.OPTION_KEY_TARGET) == null)
				throw new RuntimeException("Invalid argument for target");
			if(getValue(OptionDefinition.OPTION_KEY_SLAVE) == null)
				throw new RuntimeException("Invalid argument for slave");
			if(getValue(OptionDefinition.OPTION_KEY_RERUN) == null)
				throw new RuntimeException("Invalid argument for re-run");
			if(getValue(OptionDefinition.OPTION_KEY_END) == null)
				throw new RuntimeException("Invalid argument for number of worker to end with");
			if(getValue(OptionDefinition.OPTION_KEY_WORKER_RATIO) == null)
				throw new RuntimeException("Invalid argument for worker ratio");
			if(getValue(OptionDefinition.OPTION_KEY_INTERVAL) == null)
				throw new RuntimeException("Invalid argument for attack interval");
		} catch (Exception e) {
			showHelpMenu();
			throw new RuntimeException(e);
		}
	}

	private void showHelpMenu() {
		for(Option op : this.options) {
			String helpKey = op.getKey();
			String helpDesc = op.getDescription();
			System.out.println(String.format("%1$-8s : %2$-40s", helpKey, helpDesc));
		}
	}
}
