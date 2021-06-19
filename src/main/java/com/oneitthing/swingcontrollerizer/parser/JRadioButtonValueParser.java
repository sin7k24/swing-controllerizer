package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JRadioButton;


/**
 * <p>[概 要] </p>
 * JRadioButtonコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JRadioButtonコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JRadioButtonValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JCheckBoxコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JRadioButtonValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JRadioButtonの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JRadioButtonコンポーネントのselected属性値を調べ、
	 * name属性値=selected属性値でComponentValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param checkBox 値をパースするJCheckBoxコンポーネント
	 */
	public ComponentValues parse(Component radioButton) {
		ComponentValues ret = new ComponentValues();


		String name = ((JRadioButton)radioButton).getName();
		boolean value = ((JRadioButton)radioButton).isSelected();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
