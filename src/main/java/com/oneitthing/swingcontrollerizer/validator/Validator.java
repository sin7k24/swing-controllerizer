package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;

import com.oneitthing.swingcontrollerizer.parser.ComponentValueParser;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;
import com.oneitthing.swingcontrollerizer.parser.Parser;

/**
 * <p>[概 要]</p>
 * 全バリデータの基底クラスです。
 *
 * <p>[詳 細]</p>
 * 各種バリデータは{@link #validate(ComponentValues)}をオーバーライドして
 * バリデーション処理を実装します。
 * <p>
 *
 * 新規バリデータを作成する場合は以下を行ってください。
 * <li>
 *   <ol>validateメソッドをオーバーライドしてバリデート処理実装</ol>
 *   <ol>registerErrorMessageメソッドをオーバーライドしてエラー文言定義</ol>
 * </li>
 *
 * <p>[備 考]</p>
 *

 *

 */
public abstract class Validator {

	/** バリデーションを行うコンポーネントです。 */
	private Component component;

	/** バリデーションエラーが発生した場合のエラーメッセージです。 */
	private String errorMessage;

	/** バリデーションエラーが発生した場合の見出し文字です。 */
	private String headWord;


	/**
	 * <p>[概 要]</p>
	 * バリデーションを行うコンポーネントを取得します。
	 *
	 * <p>[詳 細]</p>
	 * componentフィールドを返却します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return バリデーションを行うコンポーネント
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションを行うコンポーネントを設定します。
	 *
	 * <p>[詳 細]</p>
	 * componentフィールドを引数componentで設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param バリデーションを行うコンポーネント
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションエラーが発生した場合のエラーメッセージを取得します。
	 *
	 * <p>[詳 細]</p>
	 * errorMessageフィールドを返却します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return バリデーションエラーが発生した場合のエラーメッセージ
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションエラーが発生した場合のエラーメッセージを設定します。
	 *
	 * <p>[詳 細]</p>
	 * errorMesssageフィールドを引数errorMessageで設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param errorMessage バリデーションエラーが発生した場合のエラーメッセージ
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションエラーが発生した場合の見出し文字を取得します。
	 *
	 * <p>[詳 細]</p>
	 * headWordフィールドを返却します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return バリデーションエラーが発生した場合の見出し文字
	 */
	public String getHeadWord() {
		return this.headWord;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションエラーが発生した場合の見出し文字を設定します。
	 *
	 * <p>[詳 細]</p>
	 * headWordフィールドを引数headWordで設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param headWord バリデーションエラーが発生した場合の見出し文字
	 */
	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}


	/**
	 * <p>[概 要]</p>
	 * エラーメッセージ登録メソッドです。
	 *
	 * <p>[詳 細]</p>
	 *
	 * <p>[備 考]</p>
	 * 具象バリデータクラスは必ず実装する必要が有ります。
	 * バリデーションエラーになった場合のメッセージを返却するよう実装して下さい。
	 *
	 * <pre class="samplecode">
	 *	public class MoneyValidator extends Validator{
	 *		&#064;Override
	 *		protected String registerErrorMessage(){
	 *			return "金額が不足しています。";
	 *		}
	 * 			:
	 * 			:
	 * </pre>
	 */
	protected abstract String registerErrorMessage();

	/**
	 * <p>[概 要]</p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細]</p>
	 * 引数componentのフィールド保存、registerErrorMessageの戻り値を
	 * errorMessageフィールドに保存、
	 * エラー見出しの初期化（コンポーネント名を設定）を行います。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param component バリデーション対象コンポーネント
	 */
	public Validator(Component component) {
		super();

		setComponent(component);
		setErrorMessage(registerErrorMessage());
		setHeadWord(component.getName());
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーション対象コンポーネントの値を返却します。
	 *
	 * <p>[詳 細]</p>
	 * このクラスを継承した全バリデータはバリデーション対象コンポーネントの値を
	 * ComponentValues型として受け取ります。<BR>
	 * （各種バリデータはvalidateメソッドの引数としてこの戻り値を取得します）
	 *
	 * <p>[備 考]</p>
	 *
	 * @return バリデーション対象コンポーネントの値を持つオブジェクト
	 */
	protected ComponentValues getValue() throws Exception {

		// バリデーション対象のコンポーネントを取得
		Component component = getComponent();
		// コンポーネントの種類毎に値を取得
		Parser parser = new ComponentValueParser();
		ComponentValues values = null;
		values = (ComponentValues)parser.parse(component);

		return values;
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーションを実行します。
	 *
	 * <p>[詳 細]</p>
	 * validateメソッドをテンプレートコールします。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return エラー有無
	 * @throws Exception
	 */
	public boolean execute() throws Exception {
		ComponentValues values = getValue();

		return validate(values);
	}

	/**
	 * <p>[概 要]</p>
	 * バリデーション処理を実装します。
	 *
	 * <p>[詳 細]</p>
	 * 各種バリデータはこのメソッドをオーバーライドしてバリデーション処理を実装します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param values バリデーション対象コンポーネントの値を持つオブジェクト
	 * @return エラー有無
	 */
	protected abstract boolean validate(ComponentValues values);
}
