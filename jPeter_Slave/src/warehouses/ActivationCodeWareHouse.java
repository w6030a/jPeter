package warehouses;

import java.util.List;
import java.util.Stack;

public class ActivationCodeWareHouse {
	private static Stack<String> activationCodeWareHouse = new Stack<String>();
	
	public static void store(List<String> activationCode) {
		activationCodeWareHouse.addAll(activationCode);
	}
	
	public static String next() {
		return activationCodeWareHouse.pop();
	}
}
