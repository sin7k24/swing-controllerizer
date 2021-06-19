package com.oneitthing.swingcontrollerizer.common.exception;

/**
 * <p>[概 要]</p>
 * 例外規定インターフェース
 * <p>[詳 細]</p>
 * <p>[備 考]</p>
 *

 */
public interface CoreExceptionIF {
	/**
	 * <p>[概 要]</p>
	 * エラー番号取得
	 * <p>[詳 細]</p>
	 * 設定されているエラー番号を取得します。
	 * <p>[備 考]</p>
	 *
	 * @return エラー番号
	 */
	public String getId();

	/**
	 * <p>[概 要]</p>
	 * エラーメッセージ取得
	 * <p>[詳 細]</p>
	 * 設定されているエラーメッセージを取得します。
	 * <p>[備 考]</p>
	 *
	 * @return エラーメッセージ
	 */
	public String getMessage();

	/**
	 * <p>[概 要]</p>
	 * クライアント通知フラグ取得
	 * <p>[詳 細]</p>
	 * エラー内容クライアント通知フラグを取得します。
	 * <p>[備 考]</p>
	 *
	 * @return 通知する場合はtrue、それ以外はfalse
	 */
	public boolean isNotifyToUser();

	/**
	 * <p>[概 要]</p>
	 * クライアント通知フラグ設定
	 * <p>[詳 細]</p>
	 * エラー内容クライアント通知フラグを設定します。
	 * <p>[備 考]</p>
	 *
	 * @param notifyToUser 通知する場合はtrue、それ以外はfalse
	 */
	public void setNotifyToUser(boolean notifyToUser);
}
