package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JCheckBox;


/**
 * <p>[概 要] </p>
 * JCheckBoxコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JCheckBoxコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JCheckBoxValueParser implements Parser {

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
	protected JCheckBoxValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JCheckBoxの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JCheckBoxコンポーネントのselected属性値を調べ、
	 * name属性値=selected属性値でComponentValueオブジェクトを生成します。<br>
	 * 生成されたComponentValueオブジェクトはComponentValuesオブジェクトの
	 * インデックス0として返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param checkBox 値をパースするJCheckBoxコンポーネント
	 */
	public ComponentValues parse(Component checkBox) {
		ComponentValues ret = new ComponentValues();


		String name = ((JCheckBox)checkBox).getName();
		boolean value = ((JCheckBox)checkBox).isSelected();

		ComponentValue componentValue = new ComponentValue(name, value);
		ret.addComponentValue(componentValue);

		return ret;
	}
}
