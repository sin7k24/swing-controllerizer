package com.oneitthing.swingcontrollerizer.common.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * <p>[概 要]</p>
 * リソースファイルユーティリティクラス
 * <p>[詳 細]</p>
 * <p>[備 考]</p>
 *

 */
public enum ResourceUtil {
	/** インスタンス */
	instance;
	/** リソースマップ */
	private Map<String, Properties> _resourceMap = new Hashtable<String, Properties>();

	/**
	 * <p>[概 要]</p>
	 * プロパティファイル読み込み
	 * <p>[詳 細]</p>
	 * 指定されたパスのプロパティファイルを読み込みます。
	 * <p>[備 考]</p>
	 *
	 * @param resourcePath　リソースパス
	 * @return プロパティ
	 */
	public Properties asProperties(String resourcePath) {
		if (this._resourceMap.containsKey(resourcePath)) {
			return this._resourceMap.get(resourcePath);
		}
		ResourceBundle bundle = ResourceBundle.getBundle(resourcePath);
		Properties messages = new Properties();
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			messages.put(key, value);
		}
		this._resourceMap.put(resourcePath, messages);
		return messages;
	}
}
