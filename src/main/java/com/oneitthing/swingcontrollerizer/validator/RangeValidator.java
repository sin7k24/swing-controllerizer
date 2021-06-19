package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;
import java.util.Properties;

import com.oneitthing.swingcontrollerizer.common.util.ResourceUtil;
import com.oneitthing.swingcontrollerizer.parser.ComponentValue;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;

/**
 * <p>[概 要] </p>
 * 文字列長範囲バリデータクラスです。
 *
 * <p>[詳 細] </p>
 * コンポーネントの文字列値を桁数判定して範囲内であるかどうか調べます。
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * BaseAction継承クラス#validatorsメソッドで以下のように使用します。
 * <pre class="samplecode">
 *	&#064;Override
 *	protected void validators(List<CustomValidator> validators) {
 *		// バリデーション対象コンポーネントを引数にしてインスタンス生成
 *		RangeValidator range_channelName =
 *			new RangeValidator(getComponent("channelName"));
 *		// 独自のエラーメッセージを使う場合
 *		range_channelName.setErrorMessage("チャンネル名は1～20文字で指定して下さい");
 *		// 文字数範囲（1文字～20文字）を設定
 *		range_channelName.setRange(1,20);
 *
 *		// 引数validatorsに追加
 *		validators.add(range_channelName);
 *	}
 * </pre>
 *

 *

 */
public class RangeValidator extends Validator {

	/** エラーメッセージ取得ソースです。 */
	private final String MESSAGE_RESOURCE = "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";

	/** 最大文字長です。 */
	private int max;

	/** 最小文字長です。 */
	private int min;

	/**
	 * <p>[概 要] </p>
	 * 許容範囲の文字列長を設定します。
	 *
	 * <p>[詳 細] </p>
	 * minフィールドに引数minを、maxフィールドに引数maxを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param min 最小文字長数
	 * @param max 最大文字長数
	 */
	public void setRange(int min, int max) {
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
	 * @param component
	 */
	public RangeValidator(Component component) {
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
		return messages.getProperty("EFC2003");
	}

	/**
	 * <p>[概 要] </p>
	 * 桁数バリデーションを行います。
	 *
	 * <p>[詳 細] </p>
	 * コンポーネントの文字列値を桁数判定して範囲内でなければfalseを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param values コンポーネント値
	 * @return エラー有：false、エラー無：true
	 */
	@Override
	protected boolean validate(ComponentValues values) {

		int max = this.max;
		int min = this.min;
		for (int i = 0; i < values.size(); i++) {
			ComponentValue elementValue = values.getComponentValue(i);
			//TODO trim() をオプションでできるように
			String v = elementValue.getValue().toString();
			if (v.length() < min || v.length() > max) {
				return false;
			}
		}
		return true;
	}
}
