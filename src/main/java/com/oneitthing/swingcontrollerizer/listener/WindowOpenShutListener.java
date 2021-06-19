package com.oneitthing.swingcontrollerizer.listener;

import java.awt.AWTEvent;
import java.util.EventListener;

/**
 * <p>[概 要] </p>
 * ウィンドウ開閉リスナI/Fです。
 * 
 * <p>[詳 細] </p>
 * WindowManagerに対してこのI/F実装クラスをaddWindowOpenShutListenerすることで、
 * ウィンドウの開閉イベントをハンドリングします。
 * 
 * <p>[備 考] </p>
 * AbstractControllerはこのI/Fを実装しており、ウィンドウのオープン時、
 * 包含コンポーネントに対してイベント追加を行います。
 *   
 * 


 * 

 */
public interface WindowOpenShutListener extends EventListener {

	/**
	 * <p>[概 要] </p>
	 * ウィンドウのオープンイベントをハンドリングします。
	 * 
	 * <p>[詳 細] </p>
	 * WindowManaggerにコールバックされます。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param evt ウィンドウオープンイベントオブジェクト
	 */
	public void windowOpen(AWTEvent evt);
	
	/**
	 * <p>[概 要] </p>
	 * ウィンドウのクローズイベントをハンドリングします。
	 * 
	 * <p>[詳 細] </p>
	 * WindowManagerにコールバックされます。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param evt ウィンドウクローズイベントオブジェクト
	 */
	public void windowShut(AWTEvent evt);
}
