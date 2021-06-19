package com.oneitthing.swingcontrollerizer.util;

import static com.oneitthing.swingcontrollerizer.util.MessageDialogUtil.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.oneitthing.swingcontrollerizer.event.MessageDialogEvent;
import com.oneitthing.swingcontrollerizer.listener.MessageDialogListener;

/**
 * <p>[概 要] </p>
 * メッセージダイアログ画面クラスです。
 *
 * <p>[詳 細] </p>
 * MessageDialogUtilの各種メソッドが生成する画面を構築します。
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class MessageDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = 3623505668973825850L;

	/** デフォルトボタン文言です。 */
    public static String defaultButtonText = "了解";

	/** yesボタン文言です。 */
    public static String yesButtonText = "はい";

	/** noボタン文言です。 */
    public static String noButtonText = "いいえ";

	/** cancelボタン文言です。 */
    public static String cancelButtonText = "キャンセル";

	/** okボタン文言です。 */
    public static String okButtonText = "OK";


	/** 押下ボタン識別値です。 */
	private int returnValue;

	/** メッセージダイアログの処理終了を監視するリスナリストです。 */
	private List<MessageDialogListener> listenerList;

	/** 表示メッセージです。 */
	private String message;

	/** ダイアログタイトルです。 */
	private String title;

	/** メッセージのタイプです。 */
	private int messageType;

	/** ボタン構成を識別します。 */
	private int buttonOption;


	/**
	 * <p>[概 要] </p>
	 * 押下ボタン識別値を返却します。
	 *
	 * <p>[詳 細] </p>
	 * returnValueフィールドを返却します。
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
	 * returnValueフィールドを設定します。
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
	 * ユーザインタラクション取得の為のリスナを追加します。
	 *
	 * <p>[詳 細] </p>
	 * 引数listenerをlistenerListフィールドに追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener 押下ボタン取得用リスナ
	 */
	public void addMessageDialogListener(MessageDialogListener listener) {
		if(listener != null) {
			this.listenerList.add(listener);
		}
	}

	/**
	 * <p>[概 要] </p>
	 * ユーザインタラクション取得の為のリスナを削除します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドから引数listenerを削除します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener 押下ボタン取得用リスナ
	 */
	public void removeMessageDialogListener(MessageDialogListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * フィールドの初期化、引数のフィールド保存を行った後、initUIメソッドをコールします。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param owner メッセージダイアログの親ウィンドウ
	 * @param modalType Dialog.ModalityType.APPLICATION_MODAL or Dialog.ModalityType.MODELESS
	 * @param message 表示メッセージ
	 * @param title ダイアログタイトル
	 * @param messageType メッセージタイプ
	 * @param buttonOption ボタン構成
	 */
	protected MessageDialog(Window owner,
						Dialog.ModalityType modalType,
						String message,
						String title,
						int messageType,
						int buttonOption) {
		super(owner, modalType);

		this.listenerList = new ArrayList<MessageDialogListener>();
		this.message = message;
		this.title = title;
		this.messageType = messageType;
		this.buttonOption = buttonOption;

		initUI();
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 * ownerフィールドがnullでない場合、ownerウィンドウの中央にダイアログを表示します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モーダル表示時のボタン押下値
	 */
	protected int showDialog() {
		if(getOwner() != null) {
			setLocationRelativeTo(getOwner());
		}

		setVisible(true);

		return this.returnValue;
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログを構築します。
	 *
	 * <p>[詳 細] </p>
	 * タイトル、アイコン、ボタンをダイアログに構築します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	protected void initUI() {
		setTitle(this.title);
		setResizable(false);

		Container contentPane = getContentPane();

		JPanel jpNorth = new JPanel();
		jpNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));

		Icon icon = createMessageTypeIcon(this.messageType);
		JLabel jlIcon = new JLabel(icon);
		jpNorth.add(jlIcon);

		JLabel jlMessage = new JLabel();
		jlMessage.setText(this.message);
		jpNorth.add(jlMessage);

		JPanel jpSouth = new JPanel();
		jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton[] buttons = createButtons(this.buttonOption);
		for(JButton button : buttons) {
			jpSouth.add(button);
		}

		contentPane.add(jpNorth, BorderLayout.NORTH);
//		contentPane.add(jpCenter, BorderLayout.CENTER);
		contentPane.add(jpSouth, BorderLayout.SOUTH);

		addWindowListener(this);
		pack();
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージタイプに沿ったアイコンを返却します。
	 *
	 * <p>[詳 細] </p>
	 * messageTypeを判別して、
	 * <li>
	 *   <ol>ERROR_MESSAGE</ol>
	 *   <ol>INFORMATION_MESSAGE</ol>
	 *   <ol>WARNING_MESSAGE</ol>
	 *   <ol>QUESTION_MESSAGE</ol>
	 * </li>
	 * に沿ったアイコンの生成をUIManager.getIconメソッドに依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param messageType メッセージタイプ
	 * @return 生成されたアイコンオブジェクト
	 */
	protected Icon createMessageTypeIcon(int messageType) {
		Icon icon = null;

		switch(messageType) {
		case ERROR_MESSAGE :
			icon = UIManager.getIcon("OptionPane.errorIcon");
			break;
		case INFORMATION_MESSAGE :
			icon = UIManager.getIcon("OptionPane.informationIcon");
			break;
		case WARNING_MESSAGE :
			icon = UIManager.getIcon("OptionPane.warningIcon");
			break;
		case QUESTION_MESSAGE :
			icon = UIManager.getIcon("OptionPane.questionIcon");
			break;

		}

		return icon;
	}

	/**
	 * <p>[概 要] </p>
	 * ボタン構成に沿ったJButton配列を返却します。
	 *
	 * <p>[詳 細] </p>
	 * buttonOptionを判定して、
	 *
	 * <li>
	 *   <ol>DEFAULT_OPTION : デフォルトボタン（-1）</ol>
	 *   <ol>YES_NO_OPTION : yesボタン (0)、noボタン (1)</ol>
	 *   <ol>YES_NO_CANCEL_OPTION : yesボタン (0)、noボタン (1)、cancelボタン (2)</ol>
	 *   <ol>OK_CANCEL_OPTION : okボタン (0)、cancelボタン (2)</ol>
	 * </li>
	 * 上記ボタン構成のJButton配列を返却します。（括弧内は押下ボタン識別値）
	 *
	 * <p>[備 考] </p>
	 *
	 * @param buttonOption ボタン構成
	 * @return 生成されたJButton配列
	 */
	protected JButton[] createButtons(int buttonOption) {
		JButton[] buttons = null;

		List<JButton> buttonList = new ArrayList<JButton>();

		switch(buttonOption) {
		case DEFAULT_OPTION :
			buttonList.add(new JButton(defaultButtonText));
			buttonList.get(0).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(DEFAULT_OPTION);
					setVisible(false);
					dispose();
				}
			});
			break;
		case YES_NO_OPTION:
			buttonList.add(new JButton(yesButtonText));
			buttonList.get(0).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(YES_OPTION);
					setVisible(false);
					dispose();
				}
			});
			buttonList.add(new JButton(noButtonText));
			buttonList.get(1).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(NO_OPTION);
					setVisible(false);
					dispose();
				}
			});

			break;
		case YES_NO_CANCEL_OPTION:
			buttonList.add(new JButton(yesButtonText));
			buttonList.get(0).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(YES_OPTION);
					setVisible(false);
					dispose();
				}
			});
			buttonList.add(new JButton(noButtonText));
			buttonList.get(1).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(NO_OPTION);
					setVisible(false);
					dispose();
				}
			});
			buttonList.add(new JButton(cancelButtonText));
			buttonList.get(2).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(CANCEL_OPTION);
					setVisible(false);
					dispose();
				}
			});
			break;
		case OK_CANCEL_OPTION:
			buttonList.add(new JButton(okButtonText));
			buttonList.get(0).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(OK_OPTION);
					setVisible(false);
					dispose();
				}
			});
			buttonList.add(new JButton(cancelButtonText));
			buttonList.get(1).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					setReturnValue(CANCEL_OPTION);
					setVisible(false);
					dispose();
				}
			});

			break;
		}

		double maxHeight = 0;
		double maxWidth = 0;
		for(JButton b : buttonList) {
			Dimension d = b.getPreferredSize();
			maxHeight = d.getHeight() > maxHeight ? d.getHeight() : maxHeight;
			maxWidth = d.getWidth() > maxWidth ? d.getWidth() : maxWidth;
		}

		Dimension buttonSize = new Dimension();
		buttonSize.setSize(maxWidth, maxHeight);
		for(JButton b : buttonList) {
			b.setPreferredSize(buttonSize);
		}

		buttons = buttonList.toArray(new JButton[0]);
		return buttons;
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのActivatedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのClosedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドに格納されているMessageDialogListener分、
	 * {@link MessageDialogListener#dialogClosed(MessageDialogEvent)}
	 * をコールします。
	 * dialogClosedの引数MessageDialogEventには押下されたボタンの識別値が格納されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		for(MessageDialogListener listener : listenerList) {
			MessageDialogEvent event = new MessageDialogEvent(this, getReturnValue());
			listener.dialogClosed(event);
		}

	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのActivatedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowClosing(WindowEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのDeactivatedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのDeiconifiedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのIconifiedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 * メッセージダイアログのOpenedイベントをハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowOpened(WindowEvent e) {
	}
}
