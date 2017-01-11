package utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

	public static void closeQuietly(Closeable resource) {
		try {
			if(resource != null)
				resource.close();
		} catch(IOException e) {
			// purposefully ignoring the exception
		}
	}
}
