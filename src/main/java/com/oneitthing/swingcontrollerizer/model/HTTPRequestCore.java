package com.oneitthing.swingcontrollerizer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.oneitthing.swingcontrollerizer.controller.ClientConfig;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

/**
 * <p>[概 要] </p>
 * サーバとHTTP通信を行う機能モデルクラスです。
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 * BASIC認証、DIGEST認証、Cookieに関する設定は{@link AbstractHTTPCore}を参照して下さい。
 *
 *


 *

 */
public class HTTPRequestCore extends AbstractHTTPCore {

	/** HTTPプレフィックスを示す定数です。 */
	public static final String HTTP_URL_PREFIX = "http.url.prefix";

	/** HTTPレスポンスをデシリアライズすることを示す定数です。 */
	public static final String OBJECT = "OBJECT";

	/** HTTPレスポンスがバイナリであることを示す定数です。 */
	public static final String BINARY = "BINARY";

	/** HTTPレスポンスがプレーンテキストであることを示す定数です。 */
	public static final String PLAIN = "PLAIN";


	/** HTTPリクエストを行うURLです。 */
	private String requestUrl;

	/** HTTPメソッドです。デフォルトはPOSTです。 */
	private String requestMethod = "POST";

	/** HTTPレスポンスをどのように扱うかを識別します。 */
	private String responseType = OBJECT;

	/** HTTP URLパラメータです。 */
	private Map<String, String> urlParameters;


	/**  */
	private int responseCode;


	/**
	 * <p>[概 要] </p>
	 * HTTPリクエストを行うURLを返却します。
	 *
	 * <p>[詳 細] </p>
	 * requestUrlフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getRequestUrl() {
		if(!this.requestUrl.startsWith("http") &&
		   !this.requestUrl.startsWith("https"))
		{
			ClientConfig config = getController().getClientConfig();
			String prefix = config.getDefaultHttpEnvironment().get(HTTP_URL_PREFIX);
			return prefix + this.requestUrl;
		}else{
			return this.requestUrl;
		}
	}

	/**
	 * <p>[概 要] </p>
	 * HTTPリクエストを行うURLを設定します。
	 *
	 * <p>[詳 細] </p>
	 * requestUrlフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param requestUrl HTTPリクエストを行うURL
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTPメソッドを返却します。
	 *
	 * <p>[詳 細] </p>
	 * requestMethodフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return HTTPメソッド
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTPメソッドを設定します。
	 *
	 * <p>[詳 細] </p>
	 * requestMethodフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param requestMethod HTTPメソッド
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTPレスポンスをどのように扱うかを返却します。
	 *
	 * <p>[詳 細] </p>
	 * responseTypeフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return HTTPレスポンスの扱い
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTPレスポンスをどのように扱うかを設定します。
	 *
	 * <p>[詳 細] </p>
	 * responseTypeフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param responseType HTTPレスポンスの扱い
	 */
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTP URLパラメータを返却します。
	 *
	 * <p>[詳 細] </p>
	 * urlParametersフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return HTTP URLパラメータ
	 */
	public Map<String, String> getUrlParameters() {
		return urlParameters;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTP URLパラメータを設定します。
	 *
	 * <p>[詳 細] </p>
	 * urlParametersフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param urlParameters HTTP URLパラメータ
	 */
	public void setUrlParameters(Map<String, String> urlParameters) {
		this.urlParameters = urlParameters;
	}

	/**
	 * <p>[概 要] </p>
	 * HTTP URLパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * urlParametersフィールドにkey=valueの形式で追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param key URLパラメータキー
	 * @param value URLパラメータ値
	 */
	public void addUrlParamteter(String key, String value) {
		this.urlParameters.put(key, value);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param responseCode
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * フィールドを初期化します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public HTTPRequestCore() {
		setUrlParameters(new HashMap<String, String>());
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws IOException
	 */
	@Override
	protected void mainproc() throws IOException {
		URLConnection httpConn = createConnection();
		((HttpURLConnection)httpConn).setRequestMethod(getRequestMethod());

		if(isSendCookie()) {
			String cookies = createCookieString();
			httpConn.setRequestProperty("Cookie", cookies.toString());
		}

//		setResponseCode(httpConn.getResponseCode());

		Object result = null;
		if(OBJECT.equals(getResponseType())) {
			createPostParameter(httpConn);
			result = createObjectResult(httpConn);
		}else if(BINARY.equals(getResponseType())) {

		}else if(PLAIN.equals(getResponseType())) {
			result = createPlainResult(httpConn);
		}

		setResult(result);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 * @throws IOException
	 */
	protected URLConnection createConnection() throws IOException  {
		URLConnection ret = null;
		URL url = new URL(getRequestUrl());
		ret = (URLConnection) url.openConnection();

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param httpConn
	 * @throws IOException
	 */
	protected void createPostParameter(URLConnection httpConn) throws IOException {
		httpConn.setDoOutput(true);
		PrintWriter writer = null;
		BufferedReader reader = null;

		writer = new PrintWriter(httpConn.getOutputStream());

		Map<String, String> urlParameters = getUrlParameters();
		Set<String> keys = urlParameters.keySet();
		Iterator<String> it = keys.iterator();

		StringBuilder sb = new StringBuilder("");
		while(it.hasNext()) {
			String key = it.next();
			String value = urlParameters.get(key);
			value = URLEncoder.encode(value, "UTF-8");
			sb.append(key + "=" + value + "&");
		}
		writer.print(sb.toString());
		writer.close();
	}



	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param httpConn
	 * @return
	 * @throws IOException
	 */
	protected Object createObjectResult(URLConnection httpConn) throws IOException {
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(httpConn.getInputStream());

		Object result = null;
		try {
			result = ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param httpConn
	 * @return
	 * @throws IOException
	 */
	protected String createPlainResult(URLConnection httpConn) throws IOException {
		InputStream is = httpConn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		StringBuilder sb = new StringBuilder("");
		String line = null;
		while((line = br.readLine()) != null) {
			sb = sb.append(line);
		}

		return sb.toString();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void postproc() throws Exception {
		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(getResult());
		fireModelSuccess(successEvent);

		fireModelFinished(new ModelProcessEvent(this));
	}
}