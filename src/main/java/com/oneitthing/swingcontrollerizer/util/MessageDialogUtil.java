package com.oneitthing.swingcontrollerizer.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;

import com.oneitthing.swingcontrollerizer.listener.MessageDialogListener;

/**
 * <p>[概 要] </p>
 * メッセージダイアログを表示するユーティリティクラスです。
 *
 * <p>[詳 細] </p>
 * JOptionPaneが保持する機能の他、モードレスでのダイアログ表示をサポートします。
 *
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * <pre>
 * 	MessageDialogUtil.showModelessMessageDialog(c,
 * 		"アカウントが有りません",
 * 		"エラー",
 * 		MessageDialogUtil.ERROR_MESSAGE,
 * 		new MessageDialogListener()
 *	{
 * 		&#064;Override
 *		public void dialogClosed(MessageDialogEvent e) {
 *			System.out.println("モードレスダイアログの結果 = " + e.getReturnValue());
 *		}
 *	});
 * </pre>
 *


 *

 */
public class MessageDialogUtil {

	/** 「はい」が押されたことを示す定数です。 */
	public static final int YES_OPTION = 0;

	/** 「いいえ」が押されたことを示す定数です。 */
	public static final int NO_OPTION = 1;

	/** 「キャンセル」が押されたことを示す定数です。 */
	public static final int CANCEL_OPTION = 2;

	/** 「OK」が押されたことを示す定数です。 */
	public static final int OK_OPTION = 0;

	/** 「×」が押されたことを示す定数です。 */
	public static final int CLOSED_OPTION = -1;


	/** エラーメッセージダイアログであることを示す定数です。 */
	public static final int ERROR_MESSAGE = 0;

	/** 情報メッセージダイアログであることを示す定数です。 */
	public static final int INFORMATION_MESSAGE = 1;

	/** 警告メッセージダイアログであることを示す定数です。 */
	public static final int WARNING_MESSAGE = 2;

	/** 質問メッセージダイアログであることを示す定数です。 */
	public static final int QUESTION_MESSAGE = 3;

	/** 通常メッセージダイアログであることを示す定数です。 */
	public static final int PLAIN_MESSAGE = -1;


	/** 通常ボタン構成であることを示す定数です。 */
	public static final int DEFAULT_OPTION = -1;

	/** 「はい」、「いいえ」ボタン構成であることを示す定数です。 */
	public static final int YES_NO_OPTION = 0;

	/** 「はい」、「いいえ」、「キャンセル」ボタン構成であることを示す定数です。 */
	public static final int YES_NO_CANCEL_OPTION = 1;

	/** 「OK」、「キャンセル」ボタン構成であることを示す定数です。 */
	public static final int OK_CANCEL_OPTION = 2;


	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 * messageType : INFORMATION_MESSAGE
	 * buttonOption : DEFAULT_OPTION
	 * で
	 * {@link #showMessageDialog(Component, String, String, int, int)}
	 * メソッドをコールします。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parent 表示基底になる親コンポーネント
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 * @return 押下されたボタンの識別子
	 */
    public static int showMessageDialog(Component parent, String message, String title) {
    	return showMessageDialog(parent, message, title, INFORMATION_MESSAGE, DEFAULT_OPTION);
    }

    /**
	 * <p>[概 要] </p>
	 * メッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
     *
	 * @param parent 表示基底になる親コンポーネント
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
     * @param messageType
     * @return 押下されたボタンの識別子
     */
	public static int showMessageDialog(Component parent, String message, String title, int messageType) {
    	return showMessageDialog(parent, message, title, messageType, DEFAULT_OPTION);
    }

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parent 表示基底になる親コンポーネント
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 * @param messageType
	 * @param buttonOption
	 * @return 押下されたボタンの識別子
	 */
	public static int showMessageDialog(Component parent,
											String message,
											String title,
											int messageType,
											int buttonOption)
    {
		MessageDialog dialog =
			createDialog((Window)parent, message, title, messageType, buttonOption);
		return dialog.showDialog();
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログを生成します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @param owner
	 * @param message
	 * @param title
	 * @param messageType
	 * @param buttonOption
	 * @return 生成されたモーダルメッセージダイアログ
	 */
	protected static MessageDialog createDialog(Window owner,
											String message,
											String title,
											int messageType,
											int buttonOption)
	{
		MessageDialog dialog =
			new MessageDialog(owner,
					Dialog.ModalityType.APPLICATION_MODAL,
					message,
					title,
					messageType,
					buttonOption);

		return dialog;
	}

	/**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parent
	 * @param message
	 * @param title
	 * @param listener
	 */
    public static void showModelessMessageDialog(Component parent,
    											String message,
    											String title,
    											MessageDialogListener listener)
    {
    	showModelessMessageDialog(parent, message, title, INFORMATION_MESSAGE, DEFAULT_OPTION, listener);
    }

    /**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
     *
     * @param parent
     * @param message
     * @param title
     * @param messageType
     * @param listener
     */
    public static void showModelessMessageDialog(Component parent,
    											String message,
    											String title,
    											int messageType,
    											MessageDialogListener listener)
    {
    	showModelessMessageDialog(parent, message, title, messageType, DEFAULT_OPTION, listener);
    }

    /**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
     *
     * @param parent
     * @param message
     * @param title
     * @param messageType
     * @param buttonOption
     * @param listener
     */
	public static void showModelessMessageDialog(Component parent,
											String message,
											String title,
											int messageType,
											int buttonOption,
											MessageDialogListener listener)
	{
		MessageDialog dialog =
			createModelessDialog((Window)parent, message, title, messageType, buttonOption, listener);
		dialog.showDialog();
	}

	/**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログを生成します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @param owner
	 * @param message
	 * @param title
	 * @param messageType
	 * @param buttonOption
	 * @param listener
	 * @return 生成されたモードレスダイアログ
	 */
	protected static MessageDialog createModelessDialog(Window owner,
											String message,
											String title,
											int messageType,
											int buttonOption,
											MessageDialogListener listener)
	{
		MessageDialog dialog =
			new MessageDialog(owner,
					Dialog.ModalityType.MODELESS,
					message,
					title,
					messageType,
					buttonOption);

		dialog.addMessageDialogListener(listener);

		return dialog;
	}
}
