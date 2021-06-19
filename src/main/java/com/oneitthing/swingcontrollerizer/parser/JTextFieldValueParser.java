package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JTextField;


/**
 * <p>[概 要] </p>
 * JTextFieldコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JTextFieldコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JTextFieldValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JTextFieldコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JTextFieldValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JTextFieldの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JTextFieldコンポーネントのtext属性値を調べ、
	 * name属性値=text属性値でComponentValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param textField 値をパースするJTextFieldコンポーネント
	 */
	public ComponentValues parse(Component textField) {
		ComponentValues ret = new ComponentValues();


		String name = ((JTextField)textField).getName();
		String value = ((JTextField)textField).getText();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
