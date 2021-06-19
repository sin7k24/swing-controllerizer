package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;
import java.util.Properties;

import com.oneitthing.swingcontrollerizer.common.util.ResourceUtil;
import com.oneitthing.swingcontrollerizer.parser.ComponentValue;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;


/**
 * <p>[概 要] </p>
 * バイト長範囲バリデータクラスです。
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * BaseAction継承クラス#validatorsメソッドで以下のように使用します。
 * <pre class="samplecode">
 *	&#064;Override
 *	protected void validators(List<Validator> validators) {
 *		// バリデーション対象コンポーネントを引数にしてインスタンス生成
 *		ByteLengthValidator validator =
 *			new ByteLengthValidator(getComponent("target"));
 *		// 独自のエラーメッセージを使う場合
 *		validator.setErrorMessage("29bytesまでの制限があります。");
 *		// バイト長範囲（0bytes～29bytes）を設定
 *		validator.setBytesRange(0,29);
 *
 *		// 引数validatorsに追加
 *		validators.add(validator);
 *	}
 * </pre>
 *
 */
public class ByteLengthValidator extends Validator{

	/** エラーメッセージ取得ソースです。 */
	private final String MESSAGE_RESOURCE = "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";

	/** 最大バイト長です。 */
	private int max;

	/** 最小バイト長です。 */
	private int min;

	/**
	 * <p>[概 要] </p>
	 * 許容範囲のバイト長を設定します。
	 *
	 * <p>[詳 細] </p>
	 * minフィールドに引数minを、maxフィールドに引数maxを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param min 最小文字長数
	 * @param max 最大文字長数
	 */
	public void setBytesRange(int min, int max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数element付きでsuper()を呼び出します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param element
	 */
	public ByteLengthValidator(Component element) {
		super(element);
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
		return messages.getProperty("EFC2004");
	}

	/**
	 * <p>[概 要] </p>
	 * バイト長バリデーションを行います。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param values コンポーネント値
	 * @return エラー有：false、エラー無：true
	 */
	@Override
	public boolean validate(ComponentValues values) {

		int max = this.max;
		int min = this.min;
		for (int i = 0; i < values.size(); i++) {
			ComponentValue elementValue = values.getComponentValue(i);
			String v = elementValue.getValue().toString();
			if (v.getBytes().length < min || v.getBytes().length > max) {
				return false;
			}
		}
		return true;
	}
}
