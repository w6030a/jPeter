package utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
	
	public static ArrayList<String> extractValue(String jsonString, String targetKey) {
		ArrayList<String> result = new ArrayList<String>();
		
		if(jsonString.startsWith("[")) {
			JSONArray jsonArray = new JSONArray(jsonString);
			for(Object jsonObject : jsonArray) {
				result.addAll(extractValue(jsonObject.toString(), targetKey));
			}
		} else {
			try {
				JSONObject jsonObj = new JSONObject(jsonString);
				if(jsonObj.has(targetKey))
					result.add(jsonObj.get(targetKey).toString());
			} catch(JSONException e) {
				e.printStackTrace();
				System.out.println("The faulty json string is: " + jsonString);
			}
		}
		
		return result;
	}
	
	public static void closeQuietly(Closeable resource) {
		try {
			if(resource != null)
				resource.close();
		} catch(IOException e) {
			// intentionally suppressed
		}
	}

	public static String takeOutSpaceChar(String s) {
		return s.replace(" ", "");
	}
}
