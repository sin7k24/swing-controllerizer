package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;

import javax.swing.JList;


/**
 * <p>[概 要] </p>
 * JListコンポーネントが保持する値を取得するクラスです。
 *
 * <p>[詳 細] </p>
 * JListコンポーネントの値を取得する為の
 * {@link Parser#parse(Component)}を実装します。
 *
 * <p>[備 考] </p>
 * このクラスはComponentValueParserによってインスタンス化され、使用されます。<br>
 * 詳細は{@link ComponentValueParser#parse(Component)}を参照して下さい。
 *


 *

 */
public class JListValueParser implements Parser {

	/**
	 * <p>[概 要] </p>
	 * Protectedコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * JListコンポーネント値をパースするには
	 * {@link ComponentValueParser#parse(Component)}
	 * を使用して下さい。
	 */
	protected JListValueParser() {
	}

	/**
	 * <p>[概 要] </p>
	 * JListの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * JListコンポーネントで選択されている分の値を調べ、
	 * name属性値=選択数分の値でComponentValueオブジェクトを生成します。<br>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param list 値をパースするJListコンポーネント
	 */
	public ComponentValues parse(Component list) {
		ComponentValues ret = new ComponentValues();

		JList jList = (JList)list;
		Object[] values = jList.getSelectedValues();

		String name = jList.getName();

		for(int i=0; i<values.length; i++) {
			Object value = values[i];
			ComponentValue componentValue = new ComponentValue(name, value);
			ret.addComponentValue(componentValue);
		}

		return ret;
	}
}
