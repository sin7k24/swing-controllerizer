package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JTextPane;


/**
 * <p>[概 要] </p>
 * JTextPaneコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JTextPaneコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JTextPaneValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JTextPaneコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JTextPaneValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JTextPaneの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JTextPaneコンポーネントのtext属性値を調べ、
	 * name属性値=text属性値でComponentValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param textPane 値をパースするJTextPaneコンポーネント
	 */
	public ComponentValues parse(Component textPane) {
		ComponentValues ret = new ComponentValues();

		String name = ((JTextPane)textPane).getName();
		String value = ((JTextPane)textPane).getText();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
