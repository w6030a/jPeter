package warehouses;

import java.util.ArrayList;

public class JobRecordWareHouse {
	private static ArrayList<String> jobDiary = new ArrayList<String>();
	
	public static void push(String record) {
		jobDiary.add(record);
	}
	
	public static String pop() {
		return jobDiary.remove(0);
	}
	
	public static ArrayList<String> getDiary() {
		return jobDiary;
	}
}
