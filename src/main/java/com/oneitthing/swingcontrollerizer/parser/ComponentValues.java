package com.oneitthing.swingcontrollerizer.parser;

import java.util.Vector;

/**
 * <p>[概 要] </p>
 * コンポーネントの値を保持するComponentValueオブジェクトを集約するクラスです。
 *
 * <p>[詳 細] </p>
 * JTextFieldやJComboBoxは単一の値を保持しますが、マルチセレクタブルな
 * JListコンポーネントや、マトリクスデータを扱うtableコンポーネントは、複数の
 * 値を保持します。<BR>
 * その場合、一つの値（「name属性=値」）を持つComponentValueオブジェクトを
 * このComponentValuesが集約する形で保持します。
 *
 * <p>[備 考] </p>
 * ComponentValueはnameフィールドを持ち、大抵の場合、コンポーネントが複数の値を
 * 持つ場合でもnameフィールドの値は変わりません。
 * <P>
 *
 * 但し、tableコンポーネントの場合はnameフィールドが変わります。
 * <PRE>
 * 	&lt;table height="200px" id="table" name="dataTable" quickEdit="true"
 *		width="300px" x="40px" y="330px"&gt;
 *
 *		&lt;column&gt;
 *			&lt;header text="姓名"/&gt;
 *		&lt;/column&gt;
 *		&lt;column&gt;
 *			&lt;header text="年齢"/&gt;
 *		&lt;/column&gt;
 *		&lt;row&gt;
 *			&lt;cell name="name" editable="true" text="山田太郎"/&gt;
 *			&lt;cell name="age" editable="true" text="32"/&gt;
 *		&lt;/row&gt;
 *		&lt;row&gt;
 *			&lt;cell name="name" editable="true" text="鈴木一郎"/&gt;
 *			&lt;cell name="age" editable="true" text="25"/&gt;
 *		&lt;/row&gt;
 *	&lt;/table&gt;
 * </PRE>
 * 上記のテーブル定義の場合、
 *
 * <PRE>
 * Parser parser = new ComponentValueParser();
 * ComponentValues values = parser.parse(table);
 * </PRE>
 * 戻り値valuesは以下のような構造になり、４つの名前と値を持ちます。
 *
 * <PRE>
 * 	ComponentValues[
 * 		ComponentValue[
 * 			name = "dataTable[0].name"
 * 			value = "山田太郎"
 * 		]
 * 		ComponentValue[
 * 			name = "dataTable[0].age"
 * 			value = "32"
 * 		]
 * 		ComponentValue[
 * 			name = "dataTable[1].name"
 * 			value = "鈴木一郎"
 * 		]
 * 		ComponentValue[
 * 			name = "dataTable[1].age"
 * 			value = "25"
 * 		]
 * 	}
 * </PRE>
 *


 *

 */
public class ComponentValues {

	/** 単一の値を持つComponentValueオブジェクトの集約です。 */
	private Vector<ComponentValue> componentValues;

	/**
	 * <p>[概 要] </p>
	 * このオブジェクトが何個のComponentValueオブジェクトを保有しているか返却します。
	 *
	 * <p>[詳 細] </p>
	 * elementValuesフィールドのサイズを取得して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このオブジェクトが保持するComponentValueオブジェクトの数
	 */
	public int size() {
		return this.componentValues.size();
	}

	/**
	 * <p>[概 要] </p>
	 * 単一の値を持つComponentValueオブジェクトを追加します。
	 *
	 * <p>[詳 細] </p>
	 * elementValuesフィールドに引数elementValueを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param elementValue ComponentValueオブジェクト
	 */
	public void addComponentValue(ComponentValue elementValue) {
		this.componentValues.add(elementValue);
	}

	/**
	 * <p>[概 要] </p>
	 * 単一の値を持つComponentValueオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * elementValuesフィールドの引数index番目を取得して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param index 保持インデックス
	 * @return ComponentValueオブジェクト
	 */
	public ComponentValue getComponentValue(int index) {
		return componentValues.get(index);
	}

	/**
	 * <p>[概 要] </p>
	 * {@link ComponentValues#getComponentValue(int)}の0番目のインデックスの値を返却します。
	 *
	 * <p>[詳 細] </p>
	 * getComponentValue(0).getValue()と同義です。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コンポーネント値
	 */
	public Object getComponentValue() {
		ComponentValue componentValue = componentValues.get(0);
		Object value = componentValue.getValue();

		return value;
	}

	/**
	 * <p>[概 要] </p>
	 * デフォルトコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * ComponentValueオブジェクトを集約するベクタ領域を生成します。
	 *
	 * <p>[備 考] </p>
	 */
	public ComponentValues() {
		this.componentValues = new Vector<ComponentValue>();
	}
}
