package com.oneitthing.swingcontrollerizer.listener;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import com.oneitthing.swingcontrollerizer.controller.AbstractController;

/**
 * <p>[概 要] </p>
 * コンテナコンポーネントの、内容コンポーネントの追加削除を監視するクラスです。
 *
 * <p>[詳 細] </p>
 * コンテナに対するコンポーネント追加を監視して、EventBinderに紐付け予約されている
 * イベントリスナの付与をコントローラに委譲します。
 * <p>
 *
 * AbstractControllerがウィンドウのオープンを検知すると、オープンされたウィンドウ
 * の内容を走査してコンテナコンポーネントに対してこのリスナを追加します。<br>
 * componentAddedイベントハンドラ実装では、追加されたコンポーネント子孫を再帰的に
 * 走査し、登録するべきイベントが予約されていないか、EventBinderにイベントリスナ付与
 * を依頼します。
 *
 *
 * <p>[備 考] </p>
 *
 */
public class ContainerListenerImpl implements ContainerListener {

	/** コントローラです。 */
	private AbstractController controller;

	/**
	 * <p>[概 要] </p>
	 * コントローラを返却します。
	 *
	 * <p>[詳 細] </p>
	 * controlerフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コントローラ
	 */
	public AbstractController getController() {
		return controller;
	}

	/**
	 * <p>[概 要] </p>
	 * コントローラを設定します。
	 *
	 * <p>[詳 細] </p>
	 * controlerフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller コントローラ
	 */
	public void setController(AbstractController controller) {
		this.controller = controller;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数controllerをフィールドに設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller コントローラ
	 */
	public ContainerListenerImpl(AbstractController controller) {
		this.controller = controller;
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネント追加イベントハンドラ実装です。
	 *
	 * <p>[詳 細] </p>
	 * {@link ContainerListener#componentAdded(ContainerEvent)}を実装します。<br>
	 * コンポーネント追加イベントから追加されたコンポーネントを取り出し、<br>
	 * {@link #searchComponent(Component)}を呼び出して子孫コンポーネントを走査します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e コンポーネント追加イベント
	 */
	@Override
	public void componentAdded(ContainerEvent e) {
		// 追加されたコンポーネント
		Component addedComponent = e.getChild();
		// 追加されたコンポーネント内に含まれるコンポーネントを全検出
		searchComponent(addedComponent);
	}

	/**
	 * <p>[概 要] </p>
	 * 追加されたコンポーネントの子孫を走査します。
	 *
	 * <p>[詳 細] </p>
	 * 追加されたコンポーネントの中にコンテナコンポーネントが有った場合、
	 * 再帰的にContainerListenerImplを追加します。
	 * <p>
	 *
	 * 走査されたコンポーネント一つ一つに対して、
	 * {@link AbstractController#bindEvents(Component)}メソッドを呼び出し、
	 * イベントリスナ付与を委譲します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param addedComponent コンテナに追加されたコンポーネント
	 */
	private void searchComponent(Component addedComponent) {

		if(addedComponent instanceof Container) {
			Container container = ((Container)addedComponent);
			ContainerListener[] ls = container.getContainerListeners();

			// 既にContainerListenerImpleがリスナ登録されている場合は処理しない
			for(ContainerListener l : ls){
				if(l instanceof ContainerListenerImpl) {
					return;
				}
			}

			Component[] cs = container.getComponents();
			for(Component c : cs) {
				searchComponent(c);
			}

			container.addContainerListener(new ContainerListenerImpl(getController()));
		}

		getController().bindEvents(addedComponent);
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネント削除イベントハンドラ実装です。
	 *
	 * <p>[詳 細] </p>
	 * 処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e コンポーネント削除イベント
	 */
	@Override
	public void componentRemoved(ContainerEvent e) {
	}
}
