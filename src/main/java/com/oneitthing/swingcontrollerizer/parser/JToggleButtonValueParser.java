package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JToggleButton;


/**
 * <p>[概 要] </p>
 * JToggleButtonコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JToggleButtonコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JToggleButtonValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JToggleButtonコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JToggleButtonValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JToggleButtonの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JToggleButtonコンポーネントのtext属性値を調べ、
	 * name属性値=text属性値でComponentValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param toggleButton 値をパースするJToggleButtonコンポーネント
	 */
	public ComponentValues parse(Component toggleButton) {
		ComponentValues ret = new ComponentValues();


		String name = ((JToggleButton)toggleButton).getName();
		boolean value = ((JToggleButton)toggleButton).isSelected();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
