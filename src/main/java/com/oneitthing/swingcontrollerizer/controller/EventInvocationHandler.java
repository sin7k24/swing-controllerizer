package com.oneitthing.swingcontrollerizer.controller;

import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * <p>[概 要] </p>
 * 汎用イベントハンドラクラスです。
 *
 * <p>[詳 細] </p>
 * EventBinder#addEventBindingメソッドで登録されたリスナタイプの動的Proxyオブジェクトです。
 *
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class EventInvocationHandler implements InvocationHandler {

	/** コントローラオブジェクトです。 */
	private AbstractController controller;

	/** このプロキシがハンドリングするイベントリスナの型です。 */
	private Class<? extends EventListener> listenerType;

	/** AbstractController#handlerFacadeに処理委譲するイベントタイプのリストです。 */
	private List<String> eventTypes;

	/** リスナが実際に付与されているコンポーネントです。 */
	private Component listenerAddedComponent;

	private static Method hashCodeMethod;
	private static Method equalsMethod;
	private static Method toStringMethod;

	static {
		try {
			hashCodeMethod = Object.class.getMethod("hashCode", null);
			equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
			toStringMethod = Object.class.getMethod("toString", null);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}


	/**
	 * <p>[概 要] </p>
	 * コントローラオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * controllerフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コントローラオブジェクト
	 */
	public AbstractController getController() {
		return controller;
	}

	/**
	 * <p>[概 要] </p>
	 * コントローラオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * controllerフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller コントローラオブジェクト
	 */
	public void setController(AbstractController controller) {
		this.controller = controller;
	}

	/**
	 * <p>[概 要] </p>
	 * このプロキシがハンドリングするイベントリスナの型を返却します。
	 *
	 * <p>[詳 細] </p>
	 * listenerTypeフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このプロキシがハンドリングするイベントリスナの型
	 */
	public Class<? extends EventListener> getListenerType() {
		return listenerType;
	}

	/**
	 * <p>[概 要] </p>
	 * このプロキシがハンドリングするイベントリスナの型を設定します。
	 *
	 * <p>[詳 細] </p>
	 * listenerTypeフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listenerType このプロキシがハンドリングするイベントリスナの型
	 */
	public void setListenerType(Class<? extends EventListener> listenerType) {
		this.listenerType = listenerType;
	}

	/**
	 * <p>[概 要] </p>
	 * handlerFacadeに処理委譲するイベントタイプメソッド名のリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * eventTypesフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return handlerFacadeに処理委譲するイベントタイプメソッド名のリスト
	 */
	public List<String> getEventTypes() {
		return eventTypes;
	}

	/**
	 * <p>[概 要] </p>
	 * handlerFacadeに処理委譲するイベントタイプメソッドのリストを設定します。
	 *
	 * <p>[詳 細] </p>
	 * eventTypesフィールドに引数eventTypesを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param eventTypes handlerFacadeに処理委譲するイベントタイプメソッド名のリスト
	 */
	public void setEventTypes(List<String> eventTypes) {
		this.eventTypes = eventTypes;
	}

	/**
	 * <p>[概 要] </p>
	 * リスナが実際に付与されているコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * listenerAddedComponentフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return リスナが実際に付与されているコンポーネント
	 */
	public Component getListenerAddedComponent() {
		return listenerAddedComponent;
	}

	/**
	 * <p>[概 要] </p>
	 * リスナが実際に付与されているコンポーネントを設定します。
	 *
	 * <p>[詳 細] </p>
	 * listenerAddedComponentフィールドに引数listenerAddedComponentを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listenerAddedComponent リスナが実際に付与されているコンポーネント
	 */
	public void setListenerAddedComponent(Component listenerAddedComponent) {
		this.listenerAddedComponent = listenerAddedComponent;
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * 引数情報をフィールドに保存します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller
	 * @param listenerType
	 * @param eventTypes
	 * @param listenerAddedComponent
	 */
	public EventInvocationHandler(AbstractController controller,
								   Class<? extends EventListener> listenerType,
								   List<String> eventTypes,
								   Component listenerAddedComonent)
	{
		this.controller = controller;
		this.listenerType = listenerType;
		this.eventTypes = eventTypes;
		this.listenerAddedComponent = listenerAddedComonent;
	}

	/**
	 * <p>[概 要] </p>
	 * イベント発生時のハンドリングを行います。
	 *
	 * <p>[詳 細] </p>
	 * 呼び出されるメソッドが、eventTypesリストに含まれるメソッド名である場合、
	 * {@link AbstractController#handlerFacade(EventObject, Class, String, EventListener)}
	 * を呼び出します。
	 *
	 * handlerFacadeでは、譲渡された引数情報を元に、実行するべきアクションを決定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param eventListenerProxy イベントを受け取ったリスナプロキシ
	 * @param method 呼び出されるイベントハンドリングメソッド
	 * @param args イベントハンドリングメソッドの引数
	 */
	public Object invoke(Object eventListenerProxy, Method method, Object[] args) throws Throwable {
		Object result = null;

		Class declaringClass = method.getDeclaringClass();

		if (declaringClass == Object.class) {
			if (method.equals(hashCodeMethod)) {
				return proxyHashCode(eventListenerProxy);
			} else if (method.equals(equalsMethod)) {
				return proxyEquals(eventListenerProxy, args[0]);
			} else if (method.equals(toStringMethod)) {
				return proxyToString(eventListenerProxy);
			} else {
				throw new InternalError("unexpected Object method dispatched: " + method);
			}
		} else {
			if(this.eventTypes.contains(method.getName())) {
				this.controller.handlerFacade((EventObject)args[0],
												this.listenerType,
												method.getName(),
												(EventListener)eventListenerProxy,
												this.listenerAddedComponent);
			}
		}

		return result;
	}

	/**
	 * <p>[概 要] </p>
	 * プロキシクラスのhashCodeメソッド実装です。
	 *
	 * <p>[詳 細] </p>
	 * プロキシクラスがhashCodeメソッド実行要請を受けた時の処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param proxy hashCodeメソッド実行要請を受けるプロキシ
	 * @return proxyのハッシュ値
	 */
	protected Integer proxyHashCode(Object proxy) {
		return new Integer(System.identityHashCode(proxy));
	}

	/**
	 * <p>[概 要] </p>
	 * equalsメソッド実装です。
	 *
	 * <p>[詳 細] </p>
	 * プロキシクラスがequalsメソッド実行要請を受けた時の処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param proxy equalsメソッド実行要請を受けるプロキシ
	 * @param other proxyと比較するオブジェクト
	 * @return true : 同じオブジェクト、false : 違うオブジェクト
	 */
	protected Boolean proxyEquals(Object proxy, Object other) {
		return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * <p>[概 要] </p>
	 * toStringメソッド実装です。
	 *
	 * <p>[詳 細] </p>
	 * プロキシクラスがtoStringメソッド実行要請を受けた時の処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param proxy toStringメソッド実行要請を受けるプロキシ
	 * @return proxyの文字列表現
	 */
	protected String proxyToString(Object proxy) {
		return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
	}
}
