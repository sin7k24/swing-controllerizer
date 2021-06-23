package com.oneitthing.swingcontrollerizer.manager;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import com.oneitthing.swingcontrollerizer.listener.WindowOpenShutListener;

/**
 * <p>[概 要] </p>
 * メモリ上のウィンドウコンポーネントを管理するクラスです。
 *
 * <p>[詳 細] </p>
 * AWTEventListenerを使用してアプリケーション全体のウィンドウ開閉を監視します。<br>
 * オープンされたウィンドウはwindowListフィールドでインスタンス管理され、
 * クローズされたウィンドウはwindoListからインスタンスが削除されます。
 * <p>
 *
 * windowListフィールドが持つウィンドウインスタンスを基に、
 * ウィンドウの多重起動制御、アクティブウィンドウの取得、指定された名前を持つ
 * ウィンドウの取得等を行います。
 *
 * <p>[備 考] </p>
 *
 * <b>使い方</b></p>
 * <pre class="samplecode">
 * 	WindowManager.getInstance().addWindowOpenShutListener(new WindowOpenShutListener(){
 * 		public void windowOpen(AWTEvent evt){
 * 			System.out.println((Window)evt.getSource() + "is opend");
 * 		}
 *
 *		public void windowShut(AWTEvent evt){
 * 			System.out.println((Window)evt.getSource() + "is closed");
 *		}
 * 	});
 * </pre>
 *
 */
public class WindowManager {

	/** showWindow時、基底ウィンドウの直上に表示する定数です。 */
	public static final int BASE = 0;

	/** showWindow時、基底ウィンドウの右隣に表示する定数です。 */
	public static final int RIGHT = 1;

	/** showWindow時、基底ウィンドウの下隣に表示する定数です。 */
	public static final int BOTTOM = 2;

	/** シングルトンインスタンスです。 */
	private static WindowManager windowManager;

	/** ウィンドウ開閉リスナのリストです。 */
	private List<WindowOpenShutListener> listenerList;

	/** 管理しているWindowインスタンスのリストです。 */
	private List<Window> windowList;


	/**
	 * <p>[概 要] </p>
	 * ウィンドウ開閉リスナのリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return ウィンドウ開閉リスナのリスト
	 */
	public List<WindowOpenShutListener> getListenerList() {
		return listenerList;
	}

	/**
	 * <p>[概 要] </p>
	 * ウィンドウ開閉リスナのリストを設定します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listenerList ウィンドウ開閉リスナのリスト
	 */
	public void setListenerList(List<WindowOpenShutListener> listenerList) {
		this.listenerList = listenerList;
	}

	/**
	 * <p>[概 要] </p>
	 * 管理しているWindowインスタンスのリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * windowListフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 管理しているWindowインスタンスのリスト
	 */
	public List<Window> getWindowList() {
		return windowList;
	}

	/**
	 * <p>[概 要] </p>
	 * 管理しているWindowインスタンスのリストを設定します。
	 *
	 * <p>[詳 細] </p>
	 * windowListフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowList 管理しているWindowインスタンスのリスト
	 */
	public void setWindowList(List<Window> windowList) {
		this.windowList = windowList;
	}

	/**
	 * <p>[概 要] </p>
	 * プライベートデフォルトコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールド、windowListフィールドを初期化後、
	 * AWTEventListenerを使用してウィンドウの開閉監視を開始します。
	 *
	 * 開閉イベントはWindowOpenShutListenerでリッスン中のオブジェクトに通知されます。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	private WindowManager() {
		this.listenerList = new ArrayList<WindowOpenShutListener>();
		this.windowList = new ArrayList<Window>();

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){
			public void eventDispatched(AWTEvent evt) {
				if(evt.getID() == WindowEvent.WINDOW_OPENED) {
					for(WindowOpenShutListener l : listenerList) {
						windowList.add((Window)evt.getSource());
						l.windowOpen(evt);
					}
				}
				if(evt.getID() == WindowEvent.WINDOW_CLOSING) {
					for(WindowOpenShutListener l : listenerList) {
						windowList.remove(evt.getSource());
						l.windowShut(evt);
					}
				}
			}
		}, AWTEvent.WINDOW_EVENT_MASK);
	}

	/**
	 * <p>[概 要] </p>
	 * シングルトンインスタンス取得メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * windowManagerフィールドに保存されているインスタンスを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return WindowManagerインスタンス
	 */
	public static WindowManager getInstance() {
		if(windowManager == null) {
			windowManager = new WindowManager();
		}

		return windowManager;
	}

	/**
	 * <p>[概 要] </p>
	 * ウィンドウ開閉リスナを追加します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドに引数listenerを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener ウィンドウ開閉リスナ
	 */
	public void addWindowOpenShutListener(WindowOpenShutListener listener) {
		this.listenerList.add(listener);
	}

	/**
	 * <p>[概 要] </p>
	 * ウィンドウ開閉リスナを削除します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドから引数listenerを削除します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener ウィンドウ開閉リスナ
	 */
	public void removeWindowOpenShutListener(WindowOpenShutListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 * <p>[概 要] </p>
	 * ウィンドウを表示します。
	 *
	 * <p>[詳 細] </p>
	 * 引数windowをsetVisible(true)します。<br>
	 * 引数duplicateがfalse指定されており、既に同名のウィンドウがwindowListで
	 * 管理されていた場合、既存ウィンドウに対してsetVisible(true)を行います。<br>
	 * その際、引数windowはdispose()されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 表示するウィンドウ
	 * @param duplicate true：多重起動可、false：多重起動不可
	 */
	public void showWindow(Window window, boolean duplicate) {
		Window target = null;

		// 多重起動可の場合は常に新規ウィンドウが表示対象
		if(duplicate) {
			target = window;
		}else{
			Window w = null;
			// 既に同名のウィンドウが管理されている場合
			// 新規ウィンドウは破棄、既存ウィンドウが表示対象
			if((w = getWindowByName(window.getName())) != null) {
				window.dispose();
				target = w;
			}else {
				target = window;
			}
		}

		target.setVisible(true);
	}

	/**
	 * <p>[概 要] </p>
	 * 特定のウィンドウに隣接してウィンドウを表示します。
	 *
	 * <p>[詳 細] </p>
	 * 引数baseWindowNameを持つウィンドウの、
	 * <OL>
	 *   <LI>直上：WindowManager.BASE</LI>
	 *   <LI>右隣：WindowManager.RIGHT</LI>
	 *   <LI>下隣：WindowManager.BOTTOM</LI>
	 * </OL>
	 * に引数windowを表示します。
	 * baseWindowNameを持つウィンドウが存在しない場合は通常表示します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 表示するウィンドウ
	 * @param duplicate true：多重起動可、false：多重起動不可
	 * @param baseWindowName 隣接させるウィンドウの名前
	 * @param position 隣接位置
	 */
	public void showWindow(Window window, boolean duplicate, String baseWindowName, int position) {

		Window baseWindow = getWindowByName(baseWindowName);
		showWindow(window, duplicate, baseWindow, position);
	}

	/**
	 * <p>[概 要] </p>
	 * 特定のウィンドウに隣接してウィンドウを表示します。
	 *
	 * <p>[詳 細] </p>
	 * 引数baseWindowNameを持つウィンドウの、
	 * <ol>
	 *   <li>直上：WindowManager.BASE</li>
	 *   <li>右隣：WindowManager.RIGHT</li>
	 *   <li>下隣：WindowManager.BOTTOM</li>
	 * </ol>
	 * に引数windowを表示します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 表示するウィンドウ
	 * @param duplicate true：多重起動可、false：多重起動不可
	 * @param baseWindow 隣接させるウィンドウ
	 * @param position 隣接位置
	 */
	public void showWindow(Window window, boolean duplicate, Window baseWindow, int position) {
		if(baseWindow != null) {
			Point movePoint = new Point();

			Point p = baseWindow.getLocation();
			Dimension d = baseWindow.getSize();

			switch(position) {
			case BASE :
				movePoint.x = p.x + 10;
				movePoint.y = p.y + 10;
				break;

			case RIGHT :
				movePoint.x = p.x + d.width;
				movePoint.y = p.y;
				break;

			case BOTTOM :
				movePoint.x = p.x;
				movePoint.y = p.y + d.height;

			default :

			}
			window.setLocation(movePoint);
		}

		showWindow(window, duplicate);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowNameを持つウィンドウインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * 管理ウィンドウリストwindowListフィールドから、windowNameが
	 * setNameされているウィンドウを探し、返却します。<br>
	 * 見つからなかった場合はnullを返却します。
	 *
	 * <p>[備 考] </p>
	 * 同名のウィンドウが複数存在する場合、
	 * 最初に検出されたウィンドウが返却されます。
	 *
	 * @param windowName 取得するウィンドウの名前
	 * @return windowNameを名前として持つウィンドウ
	 */
	public Window getWindowByName(String windowName) {
		Window ret = null;

		for(Window w : this.windowList) {
			if(windowName.equals(w.getName())) {
				ret = w;
				break;
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowNameを持つ全てのウィンドウインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * 管理ウィンドウリストwindowListフィールドから、windowNameが
	 * setNameされている全てのウィンドウを探し、返却します。<br>
	 * 見つからなかった場合は空のWindow配列を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowName 取得するウィンドウの名前
	 * @return windowNameを名前として持つ全てのウィンドウの配列
	 */
	public Window[] getWindowsByName(String windowName) {
		List<Window> ret = new ArrayList<Window>();

		for(Window w : this.windowList) {
			if(windowName.equals(w.getName())) {
				ret.add(w);
			}
		}

		return ret.toArray(new Window[1]);
	}

	/**
	 * <p>[概 要] </p>
	 * 同名のWindowが管理されているかどうか調べます。
	 *
	 * <p>[詳 細] </p>
	 * 引数windowと同じ名前がsetNameされているウィンドウがあるか、
	 * windowListフィールドを調べます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 調査対象ウィンドウ
	 * @return true：既管理、false：未管理
	 */
	public boolean existSameNameWindow(Window window) {
		return existSameNameWindow(window.getName());
	}

	/**
	 * <p>[概 要] </p>
	 * 同名のWindowが管理されているかどうか調べます。
	 *
	 * <p>[詳 細] </p>
	 * 引数windowNameと同じ名前がsetNameされているウィンドウがあるか、
	 * windowListフィールドを調べます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowName 調査対象ウィンドウの名前
	 * @return true：既管理、false：未管理
	 */
	public boolean existSameNameWindow(String windowName) {
		boolean ret = false;

		for(Window w : this.windowList) {
			if(windowName.equals(w.getName())) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 管理中ウィンドウの中から、現在アクティブであるウィンドウを返却します。
	 *
	 * <p>[詳 細] </p>
	 * windowListフィールドの中から、isActiveメソッドがtrueであるウィンドウを
	 * 探して返却します。<br>
	 * アクティブなウィンドウが存在しない場合はnullを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return アクティブなウィンドウ
	 */
	public Window searchActiveWindow() {
		Window ret = null;

		List<Window> windows = getWindowList();
		for(Window window : windows) {
			if(window.isActive()) {
				ret = window;
				break;
			}
		}

		return ret;
	}
}
