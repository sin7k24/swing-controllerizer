package com.oneitthing.swingcontrollerizer.event;

import java.util.EventObject;

import com.oneitthing.swingcontrollerizer.util.MessageDialog;

/**
 * <p>[概 要] </p>
 * モードレスメッセージダイアログのユーザインタラクション結果が格納されるイベントクラスです。
 *
 * <p>[詳 細] </p>
 * MessageDialogUtilによるモードレスメッセージ表示後、メッセージダイアログが
 * 閉じる契機で発生するイベントです。<br>
 * どのボタンが押されたか、ユーザの選択がreturnValueフィールドに格納されます。
 *
 * <p>[備 考] </p>
 *
 *


 *
 * @see com.oneitthing.swingcontrollerizer.listener.MessageDialogListener

 */
public class MessageDialogEvent extends EventObject {

	private static final long serialVersionUID = -4413338257819059035L;

	/** 押下ボタン識別値です。 */
	private int returnValue;

	/**
	 * <p>[概 要] </p>
	 * 押下ボタン識別値を返却します。
	 *
	 * <p>[詳 細] </p>
	 * returnValueフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 押下ボタン識別値
	 */
	public int getReturnValue() {
		return returnValue;
	}

	/**
	 * <p>[概 要] </p>
	 * 押下ボタン識別値を設定します。
	 *
	 * <p>[詳 細] </p>
	 * returnValueフィールド値を設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param returnValue 押下ボタン識別値
	 */
	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * イベントソースとボタン押下値をフィールドに保存します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param source イベントを発生させたメッセージダイアログ
	 * @param returnValue 押下ボタン識別値
	 */
	public MessageDialogEvent(MessageDialog source, int returnValue) {
		super(source);

		setReturnValue(returnValue);
	}
}
