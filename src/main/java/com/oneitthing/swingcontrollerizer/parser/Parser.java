package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

/**
 * <p>[概 要] </p>
 * Swingコンポーネントをパースする全てのパーサのインターフェースです。
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *


 *

 */
public interface Parser {

	/**
	 * <p>[概 要] </p>
	 * パーサオブジェクトが実装するパースメソッドです。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component 値をパースする各種エレメント
	 */
	public Object parse(Component component) throws Exception;
}
