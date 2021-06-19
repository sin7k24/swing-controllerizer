package com.oneitthing.swingcontrollerizer.event;

import java.util.EventObject;

import com.oneitthing.swingcontrollerizer.model.Model;

/**
 * <p>[概 要] </p>
 * モデル処理の成功、失敗情報を保持するイベントクラスです。
 *
 * <p>[詳 細] </p>
 * モデルレイヤ実装において、処理が成功した場合、失敗した場合にこのオブジェクトが
 * 生成され、コントローラ内のモデル処理結果イベントハンドラに譲渡されます。<br>
 * 上記リスナはモデルを起動したアクションのnextModel、successForward、failureFoward
 * に同オブジェクトを譲渡します。<br>
 *
 * <p>[備 考] </p>
 *
 *


 *
 * @see com.oneitthing.swingcontrollerizer.model.Model

 */
public class ModelProcessEvent extends EventObject {

	private static final long serialVersionUID = 1434883436721714559L;

	/** モデル処理成功結果です。 */
	private Object result;

	/** モデル処理失敗例外です。 */
	private Exception exception;


	/**
	 * <p>[概 要] </p>
	 * モデル処理成功結果を取得します。
	 *
	 * <p>[詳 細] </p>
	 * resultフィールドを返却します。
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデル処理成功結果
	 */
	public Object getResult() {
		return this.result;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理成功結果を設定します。
	 *
	 * <p>[詳 細] </p>
	 * resultフィールドを、引数resultで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param result モデル処理成功結果
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理失敗例外を取得します。
	 *
	 * <p>[詳 細] </p>
	 * exceptionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデル処理失敗例外
	 */
	public Exception getException() {
		return this.exception;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理失敗例外を設定します。
	 *
	 * <p>[詳 細] </p>
	 * exceptionフィールドを引数exceptionで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param exception モデル処理失敗例外
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数modelをイベントソースとして親コンストラクタをコールします。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param model イベントソース（イベントを発行したモデル）
	 */
	public ModelProcessEvent(Model model) {
		super(model);
	}
}
