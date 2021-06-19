package com.oneitthing.swingcontrollerizer.parser;

/**
 * <p>[概 要] </p>
 * コンポーネントの値を保持するクラスです。
 *
 * <p>[詳 細] </p>
 * &lt;textField id="ageTextField" name="age" text="32"/&gt;<BR>
 * 上記のコンポーネントはname=age、value=32でこのクラスオブジェクトが生成されます。
 *
 * <p>[備 考] </p>
 *


 *

 */
public class ComponentValue {

	/** コンポーネントのname属性です。 */
	private String name;

	/** コンポーネントの値です。 */
	private Object value;

	/**
	 * <p>[概 要] </p>
	 * nameとvalueを引数に取るコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数nameをnameフィールドに、引数valueをvalueフィールドに設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param name コンポーネントのname属性
	 * @param value コンポーネントの値
	 */
	public ComponentValue(String name, Object value) {
		this.name = name;
		this.value = value;
		if(this.value == null){
			this.value = "";
		}
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネントのname属性を取得します。
	 *
	 * <p>[詳 細] </p>
	 * nameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コンポーネントのname属性
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネントのname属性を設定します。
	 *
	 * <p>[詳 細] </p>
	 * nameフィールドを引数nameで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param name コンポーネントのname属性
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネントの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * valueフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コンポーネントの値
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネントの値を設定します。
	 *
	 * <p>[詳 細] </p>
	 * valueフィールドを引数valueで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param value コンポーネントの値
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
