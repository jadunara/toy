package kr.faz.app.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	public static String uploadTransfer(String url, File file, String content) {
		CloseableHttpClient httpClient =  HttpClients.createDefault();
		CloseableHttpClient httpclient = httpClient ;
		String result = null;
		try {
			HttpPost httppost = new HttpPost(url);

			FileBody fileBody = new FileBody(file);
			StringBody stringBody = new StringBody(content, ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", fileBody)
					.addPart("stringBody", stringBody).build();

			 ///MultipartEntityBuilder mb = MultipartEntityBuilder.create();

			httppost.setEntity(reqEntity);

			logger.debug("executing upload request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				logger.debug(response.getStatusLine().toString());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity,  "UTF-8");
					logger.debug("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error("[HttpClientUtil][upload][url=" + url + "]" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("[HttpClientUtil][upload][url=" + url + "]" + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("[HttpClientUtil][upload][url=" + url + "]" + e.getMessage(), e);
		} finally {
			try {
				if ( httpclient != null)
					httpclient.close();
			} catch (IOException e) {
				logger.error("[HttpClientUtil][upload][url=" + url + "]" + e.getMessage(), e);
			}
		}
		return result;
	}

}
