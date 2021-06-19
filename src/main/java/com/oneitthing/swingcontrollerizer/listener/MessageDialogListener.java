package com.oneitthing.swingcontrollerizer.listener;

import java.util.EventListener;

import com.oneitthing.swingcontrollerizer.event.MessageDialogEvent;

/**
 * <p>[概 要] </p>
 * モードレスメッセージダイアログのユーザインタラクション結果を監視するリスナクラスです。
 *
 * <p>[詳 細] </p>
 * MessageDialogUtilが生成するモードレスダイアログに対して、押下されたボタン値を
 * 取得する為に使用します。
 *
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public interface MessageDialogListener extends EventListener {

	/**
	 * <p>[概 要]</p>
	 * モードレスダイアログのクローズをハンドリングします。
	 *
	 * <p>[詳 細]</p>
	 * 引数evtに押下されたボタンの値が格納されています。
	 *
	 * <pre>
	 * int ret = evt.getReturnValue();
	 * </pre>
	 * <p>[備 考]</p>
	 *
	 * @param evt ダイアログがクローズされたイベント
	 */
	public void dialogClosed(MessageDialogEvent evt);
}
