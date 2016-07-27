package com.taxeo.sbus.srxp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import com.taxeo.tips.services.InvoiceContent;
import com.taxeo.tips.services.UploadInvoicesRequest;

public class Client {

	public static final String ACCESS_KEY = "be7c706dbce2ccc1b2a5db62e18f0ffd695148b2";

	public static final String TIPS_BETA_URL = "http://beta-tips.taxeo.com:8080/TIPS/services/exposedRestServices/uploadInvoice";

	public static final String TIPS_PROD_URL = "https://tips.taxeo.com/TIPS/services/exposedRestServices/uploadInvoice";
	
	public static final String TIPS_PROD_AUTHORIZATION = "Basic dGlwc1N5c3RlbTp0aXBzU3lzdGVt";
	
	public static final String TIPS_BETA_AUTHORIZATION = "Basic dGlwc1N5c3RlbTp0aXBzU3lzdGVt";
	
	public String doGet(String urlStr, String accessKey) {
		StringBuffer response = new StringBuffer();
		BufferedReader in = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			if (accessKey != null) {
				con.setRequestProperty("Authorization", "SRXPAPI accesskey=" + accessKey);
			}
			con.setRequestProperty("Content-Type", "application/json");

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response.toString();
	}

	public byte[] doGetBytes(String urlStr) {
		byte[] result = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");

			result = inputStreamToByte(con.getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private byte[] inputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[4096];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }
            return output.toByteArray();
        } finally {
            output.close();
        }
	}

	public int tipsPost(UploadInvoicesRequest request) {
		int responseCode = 0;
		BufferedReader in = null;
		StringBuffer response = new StringBuffer();
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		String json = null;
		try {
			URL url = new URL(TIPS_BETA_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", TIPS_PROD_AUTHORIZATION);
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { UploadInvoicesRequest.class });
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("eclipselink.media-type", "application/json");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(request, System.out);
			m.marshal(request, out);
			json = new String(out.toByteArray(), "UTF-8");
			wr.writeBytes(json);
			wr.flush();
			wr.close();

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			responseCode = con.getResponseCode();
			
			if (responseCode != 200) {
				String errorReport = response+"\n"+json;
				FileUtils.writeStringToFile(new File("error/"+System.currentTimeMillis()+".json"), errorReport, Charset.forName("UTF-8"));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		
		return responseCode;
	}

}
