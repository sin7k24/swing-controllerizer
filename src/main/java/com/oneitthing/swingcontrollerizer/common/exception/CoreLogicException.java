package com.oneitthing.swingcontrollerizer.common.exception;

/**
 * <p>[概 要]</p>
 * フレームワーク内共通例外クラスです。
 * <p>[詳 細]</p>
 * corelogicフレームワーク内で発生した、想定される例外は
 * 全てこのクラスにラッピングされてスローされます。
 * <p>[備 考]</p>
 *

 */
public class CoreLogicException extends AbstractCoreException {
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 6285526206463903933L;

	/**
	 * エラーメッセージリソースファイルを返却します。
	 *
	 * @return エラーメッセージリソースファイル名前空間
	 */
	protected String getMessageSource(){
		return "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";
	}

	public CoreLogicException(){

	}

	/**
	 * メッセージIDを指定するコンストラクタです。
	 * 処理は基底クラスであるAbstractCoreExceptionに委譲します。
	 *
	 * @param id メッセージID
	 */
	public CoreLogicException(String id) {
		super(id);
	}

	/**
	 * メッセージIDと原因例外を引数に取るコンストラクタです。
	 * 処理は基底クラスであるAbstractCoreExceptionに委譲します。
	 *
	 * @param id メッセージID
	 * @param e cause
	 */
	public CoreLogicException(String id, Throwable e) {
		super(id, e);
	}
}
