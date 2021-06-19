package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;


/**
 * <p>[概 要] </p>
 * １バリデーションエラーの情報を保持するクラスです。
 *
 * <p>[詳 細] </p>
 * <ul>
 *     <li>バリデーションエラーを起こしたコンポーネントのインスタンス</li>
 *     <li>バリデーションエラーメッセージ</li>
 *     <li>エラー見出し文字（設定されない場合はコンポーネントのname属性値）</li>
 * </ul>
 * を保持します。<br>
 *
 * <p>[備 考] </p>
 *

 *

 */
public class ValidateError {

	/** バリデーションエラーを起こしたコンポーネントです。 */
	private Component component;

	/** バリデータから譲渡されたエラーメッセージです。 */
	private String message;

	/** バリデータから譲渡されたエラー見出し文字です。（デフォルトはname属性値） */
	private String headWord;


	/**
	 * <p>[概 要] </p>
	 * バリデーションエラーを起こしたコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return バリデーションエラーを起こしたコンポーネント
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデーションエラーを起こしたコンポーネントを設定します。
	 *
	 * <p>[詳 細] </p>
	 * componentフィールドを引数componentで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component バリデーションエラーを起こしたコンポーネント
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデータから譲渡されたエラーメッセージを返却します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return バリデータから譲渡されたエラーメッセージ
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデータから譲渡されたエラーメッセージを設定します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを引数messageで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message バリデータから譲渡されたエラーメッセージ
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデータから譲渡されたエラー見出し文字を返却します。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return バリデータから譲渡されたエラー見出し文字
	 */
	public String getHeadWord() {
		return headWord;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデータから譲渡されたエラー見出し文字を設定します。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param headWord バリデータから譲渡されたエラー見出し文字
	 */
	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * componentフィールドに引数componentを、messageフィールドに引数messageを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component バリデーションエラーを起こしたコンポーネント
	 * @param message バリデータから譲渡されたエラーメッセージ
	 */
	public ValidateError(Component component, String message){
		this.component = component;
		this.message = message;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #ValidateError(Component, String)}オーバーロードコンストラクタをコール後、<br>
	 * headWordフィールドを引数headWordで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component バリデーションエラーを起こしたコンポーネント
	 * @param message バリデータから譲渡されたエラーメッセージ
	 * @param headWord バリデータから譲渡されたエラー見出し文字
	 */
	public ValidateError(Component component, String message, String headWord){
		this(component, message);
		this.headWord = headWord;
	}
}
