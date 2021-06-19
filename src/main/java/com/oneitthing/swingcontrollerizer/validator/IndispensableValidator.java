package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;
import java.util.Properties;

import com.oneitthing.swingcontrollerizer.common.util.ResourceUtil;
import com.oneitthing.swingcontrollerizer.parser.ComponentValue;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;

/**
 * <p>[概 要] </p>
 * 必須バリデータクラスです。
 *
 * <p>[詳 細] </p>
 * コンポーネントの文字列入力値が空でないかどうか調べます。
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * BaseAction継承クラス#validatorsメソッドで以下のように使用します。
 * <pre class="samplecode">
 * 	&#064;Override
 * 	protected void validators(List<Validator> validators) {
 *		Component jtfFullName = getComponent("inputFormFrame.jtfFullName");
 *
 *		IndispensableValidator indispensable = new IndispensableValidator(jtfFullName);
 *		validators.add(indispensable);
 *	}
 * </pre>
 *

 */
public class IndispensableValidator extends Validator {

	/** エラーメッセージ取得ソースです。 */
	private final String MESSAGE_RESOURCE = "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数component付きでsuper()を呼び出します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component
	 */
	public IndispensableValidator(Component component) {
		super(component);
	}

	/**
	 * <p>[概 要] </p>
	 * エラー文言登録メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * com.oneitthing.swingcontrollerizer.shared.exception.corelogic_message.properties
	 * から対応するエラーメッセージを取得して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return エラー時メッセージ
	 */
	@Override
	public String registerErrorMessage() {
		Properties messages = ResourceUtil.instance.asProperties(MESSAGE_RESOURCE);
		return messages.getProperty("EFC2002");
	}

	/**
	 * <p>[概 要] </p>
	 * 必須バリデーションを行います。
	 *
	 * <p>[詳 細] </p>
	 * コンポーネントの文字列入力値が空であった場合、falseを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param values コンポーネント値
	 * @return エラー有：false、エラー無：true
	 */
	@Override
	protected boolean validate(ComponentValues values) {

		for (int i = 0; i < values.size(); i++) {
			ComponentValue componentValue = values.getComponentValue(i);
			String val = componentValue.getValue().toString();
			if (val == null || val.length() == 0) {
				return false;
			}
		}

		return true;
	}
}
