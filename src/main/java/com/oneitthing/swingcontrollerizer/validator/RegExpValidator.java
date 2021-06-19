package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oneitthing.swingcontrollerizer.common.util.ResourceUtil;
import com.oneitthing.swingcontrollerizer.parser.ComponentValue;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;

/**
 * <p>[概 要] </p>
 * 正規表現バリデータクラスです。
 *
 * <p>[詳 細] </p>
 * コンポーネントの文字列値をpatternフィールドに設定された
 * 正規表現パターンでバリデーションします。
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * BaseAction継承クラス#validatorsメソッドで以下のように使用します。
 * <pre class="samplecode">
 *	&#064;Override
 *	protected void validators(List<Validator> validators) {
 *
 *		// バリデーション対象コンポーネントを引数にしてインスタンス生成
 *		Component jtfEmail = getComponent("inputFormFrame.jtfEmail");
 *		RegExpValidator regexp = new RegExpValidator(jtfEmail);
 *
 *		// Eメールパターンをバリデーションパターンに設定
 *		regexp.setPattern("[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+");
 *
 *		// 引数validatorsに追加
 *		validators.add(regexp);
 *	}
 * </pre>
 *

 */
public class RegExpValidator extends Validator {

	/** エラーメッセージ取得ソースです。 */
	private final String MESSAGE_RESOURCE = "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";

	/** バリデーションに使用する正規表現パターンです。 */
	private String pattern;


	/**
	 * <p>[概 要] </p>
	 * バリデーションに使用する正規表現パターンを返却します。
	 *
	 * <p>[詳 細] </p>
	 * patternフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return バリデーションに使用する正規表現パターン
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデーションに使用する正規表現パターンを設定します。
	 *
	 * <p>[詳 細] </p>
	 * patternフィールドに引数patternを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param pattern バリデーションに使用する正規表現パターン
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

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
	public RegExpValidator(Component component) {
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
	protected String registerErrorMessage() {
		Properties messages = ResourceUtil.instance.asProperties(MESSAGE_RESOURCE);
		return messages.getProperty("EFC2004");
	}

	/**
	 * <p>[概 要] </p>
	 * 正規表現バリデーションを行います。
	 *
	 * <p>[詳 細] </p>
	 * コンポーネントの文字列値をpatternフィールド値で正規表現マッチングします。
	 * 入力値がpatternにマッチしなかった場合、falseが返却されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param value コンポーネント値
	 * @return エラー有：false、エラー無：true
	 */
	@Override
	protected boolean validate(ComponentValues values) {
		boolean ret = true;

		for (int i = 0; i < values.size(); i++) {
			ComponentValue componentValue = values.getComponentValue(i);
			String val = componentValue.getValue().toString();

			Pattern ptn = Pattern.compile(getPattern());
			Matcher mc = ptn.matcher(val);
			if(mc.matches()) {
				ret = ret & true;
			} else{
				ret = ret & false;
			}
		}

		return ret;
	}
}
