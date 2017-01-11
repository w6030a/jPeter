package utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

	public static void closeQuietly(Closeable resource) {
		if(resource != null) {
			try {
				resource.close();
			} catch(IOException ie) {
				// intentionally ignoring the exception
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
