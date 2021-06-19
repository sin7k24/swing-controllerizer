package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JButton;


/**
 * <p>[概 要] </p>
 * JButtonコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JButtonコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JButtonValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JButtonコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JButtonValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JButtonの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JButtonエレメントのtext属性値を調べ、
	 * name属性値=text属性値でComponenttValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param button 値をパースするJButtonコンポーネント
	 */
	public ComponentValues parse(Component button) {
		ComponentValues ret = new ComponentValues();


		String name = ((JButton)button).getName();
		String value = ((JButton)button).getText();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
