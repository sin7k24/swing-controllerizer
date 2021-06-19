package com.oneitthing.swingcontrollerizer.model;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.PasswordAuthentication;
import java.util.List;

/**
 * <p>[概 要] </p>
 * HTTP通信を行う機能モデルクラスの基底クラスです。
 *
 * <p>[詳 細] </p>
 * BASIC認証、DIGEST認証が掛かったサーバと通信する際に使用される
 * Authenticatorを設定します。</p>
 *
 *
 * <p>[備 考] </p>
 * このクラスは内部的にAuthenticator#setDefault(Authenticator)を実行します。<br>
 * この為、以降のHTTP通信ではAuthenticatorが有効になったままです。<br>
 * 別途Authenticatorが必要なサーバと通信する際は再度
 * {@link #setAuthentication(String, char[])}を実行して下さい。
 *


 *

 */
public abstract class AbstractHTTPCore extends BaseModel {

	/** 認証ユーザ名です。 */
	private String authUser;

	/** 認証パスワードです。 */
	private char[] authPassword;

	/** クッキー送受信制御オブジェクトです。 */
	private static CookieManager cookieManager;
	static {
		cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

		CookieHandler.setDefault(cookieManager);
	}

	/** クッキー送信するかどうかのフラグです。デフォルトはtrueです。 */
	private boolean sendCookie = true;


	/**
	 * <p>[概 要] </p>
	 * 認証ユーザ名を取得します。
	 *
	 * <p>[詳 細] </p>
	 * authUserフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 認証ユーザ名
	 */
	public String getAuthUser() {
		return authUser;
	}

	/**
	 * <p>[概 要] </p>
	 * 認証パスワードを取得します。
	 *
	 * <p>[詳 細] </p>
	 * authPasswordフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 認証パスワード
	 */
	public char[] getAuthPassword() {
		return authPassword;
	}

	/**
	 * <p>[概 要] </p>
	 * クッキー送信するかどうかを返却します。
	 *
	 * <p>[詳 細] </p>
	 * sendCookieフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return クッキー送信有無
	 */
	public boolean isSendCookie() {
		return sendCookie;
	}

	/**
	 * <p>[概 要] </p>
	 * クッキー送信するかどうかを設定します。
	 *
	 * <p>[詳 細] </p>
	 * sendCookieフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param sendCookie クッキー送信有無
	 */
	public void setSendCookie(boolean sendCookie) {
		this.sendCookie = sendCookie;
	}

	/**
	 * <p>[概 要] </p>
	 * Authenticatorを設定します。
	 *
	 * <p>[詳 細] </p>
	 * user、passwordはフィールドに保存されます。<br>
	 * 指定されたuser、passwordでPasswordAuthenticationインスタンスを
	 * 生成、返却するAuthenticatorを作成して、Authenticatorに対して
	 * setDefaultで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param user 認証ユーザ
	 * @param password 認証パスワード
	 */
	public void setAuthentication(String user, char[] password) {
		this.authUser = user;
		this.authPassword = password;

		Authenticator.setDefault(new Authenticator(){
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = null;
				try{
					pa = new PasswordAuthentication(
							authUser,
							authPassword);
				}catch(Exception e){
					e.printStackTrace();
				}
				return pa;
			}
		});
	}

	/**
	 * <p>[概 要] </p>
	 * 設定されているAuthenticatorを削除します。
	 *
	 * <p>[詳 細] </p>
	 * authUserフィールド、authPasswordフィールドにnullを設定して、
	 * Authenticator.setDefault(null)を実行します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void removeAuthentication() {
		this.authUser = null;
		this.authPassword = null;
		Authenticator.setDefault(null);
	}

	/**
	 * <p>[概 要] </p>
	 * Cookie文字列を作成します。
	 *
	 * <p>[詳 細] </p>
	 * メモリ上のCookieManagerが保持しているクッキーの値を、
	 * 「キー名=値;キー名=値;...」のフォーマットで連結します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return Cookie文字列
	 */
	protected String createCookieString() {
		StringBuilder ret = new StringBuilder("");

		List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
		for (HttpCookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			ret.append(name + "=" + value + ";");
		}

		return ret.toString();
	}
}
