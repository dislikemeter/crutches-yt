package pw.crutchtools.hisau.component.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public final class SimpleHttpClient {

	public static String getRequest(String url) throws MalformedURLException, IOException {
		URL requestUrl = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(requestUrl.openStream()));
		String response;
		StringBuilder sb = new StringBuilder();
		while ((response = in.readLine()) != null)
			sb.append(response);
		in.close();
		return sb.toString();
	}

}
