package com.oneitthing.swingcontrollerizer.controller;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.util.ComponentSearchUtil;

/**
 * <p>[概 要] </p>
 * イベント紐付け情報保持クラスです。
 *
 * <p>[詳 細] </p>
 * {@link BaseController#bind(EventBinder)}によるコンポーネント、イベントタイプ、アクションクラス
 * の紐付け情報を、bindInfoフィールドMapに保持します。<br>
 * bindInfoオブジェクトは以下のような構造を持ちます。
 * <pre>
 * [
 * コンポーネント名1 =
 *     イベントリスナクラス型 =
 *         [イベントタイプ1(イベントハンドラメソッド名) = アクションクラス,
 *          イベントタイプ2(イベントハンドラメソッド名) = アクションクラス],
 * コンポーネント名2 =
 *     イベントリスナクラス型 =
 *         [イベントタイプ1(イベントハンドラメソッド名) = アクションクラス]
 * ]
 * </pre>
 *
 * <pre class="samplecode">
 *		eventBinder.addEventBinding("chatFrame.jbEnterChannel", ActionListener.class, "actionPerformed", EnterChannelAction.class);
 *		eventBinder.addEventBinding("chatFrame.jbRemarkSend", ActionListener.class, "actionPerformed", RemarkSendAction.class);
 *		eventBinder.addEventBinding("chatFrame", WindowListener.class, "windowClosing", ChatFrameCloseAction.class);
 * </pre>
 *
 * 上記の場合、以下のようになります。
 * <pre>
 * [
 * "chatFrame.jbEnterChannel" =
 *     ActionListener.class =
 *         ["actionPerformed" = EnterChannelAction.class]
 * "chatFrame.jbRemarkSend" =
 *     ActionListener.class =
 *         ["actionPerformed" = RemarkSendAction.class]
 * ]
 *
 * </pre>
 *
 * <p>[備 考] </p>
 * アプリケーション動作中に紐付け情報を追加する場合、以下のようにEventBinderオブジェクト
 * を取得してaddEventBinding、又はaddEventBindingImmediatelyをコールします。
 *
 * <pre class="samplecode">
 * 	getController().getEventBinder().addEventBinding("inputFormFrame.jtfUserId", FocusListener.class, "focusLost", UserIdFocusLostAction.class);
 * </pre>
 *


 *

 */
public class EventBinder {

	/** コンポーネント名、リスナタイプ、eventタイプ、Actionクラスの紐付け情報を保持するオブジェクトです。 */
	private Map<String, Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>>> bindInfo;

	/** コントローラオブジェクトです。 */
	private AbstractController controller;

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報保持オブジェクトを取得します。
	 *
	 * <p>[詳 細] </p>
	 * bindInfoフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return イベント紐付け情報
	 */
	public Map<String, Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>>> getBindInfo() {
		return bindInfo;
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報保持オブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * bindInfoフィールドを、引数bindInfoで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param bindInfo イベント紐付け情報
	 */
	public void setBindInfo(
			Map<String, Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>>> bindInfo) {
		this.bindInfo = bindInfo;
	}

	/**
	 * <p>[概 要] </p>
	 * コントローラオブジェクトを取得します。
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
	 * controllerフィールドを引数controllerで設定します。
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
	 * デフォルトコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * bindInfoフィールドインスタンスを生成します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public EventBinder(AbstractController controller) {
		this.controller = controller;
		this.bindInfo = new HashMap<String, Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>>>();
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報追加メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 引数componentNameが既に登録されている場合、componentName用のMapを取り出し、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * componentNameが未登録の場合、componentName用のMapを新規に生成して、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * 登録された情報はイベント紐付け情報保持オブジェクト（bindInfoフィールド）に保持され、
	 * コントローラのコンポーネント挿抜ハンドラによって参照されます。
	 *
	 * <p>[備 考] </p>
	 * 登録された紐付け情報が反映されるのは、componentNameをname属性値として持つ
	 * コンポーネントが画面追加されたタイミングです。
	 *
	 * @param componentName
	 * @param listenerFqcn
	 * @param eventType
	 * @param actionFqcn
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void addEventBinding(String componentName, String listenerFqcn, String eventType, String actionFqcn) throws Exception {
		Class<? extends EventListener> listenerType = (Class<? extends EventListener>)Class.forName(listenerFqcn);

		addEventBinding(componentName, listenerType, eventType, actionFqcn);
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報追加メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 引数componentNameが既に登録されている場合、componentName用のMapを取り出し、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * componentNameが未登録の場合、componentName用のMapを新規に生成して、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * 登録された情報はイベント紐付け情報保持オブジェクト（bindInfoフィールド）に保持され、
	 * コントローラのコンポーネント挿抜ハンドラによって参照されます。
	 *
	 * <p>[備 考] </p>
	 * 登録された紐付け情報が反映されるのは、componentNameをname属性値として持つ
	 * コンポーネントが画面追加されたタイミングです。
	 *
	 * @param componentName
	 * @param listenerType
	 * @param eventType
	 * @param actionFqcn
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void addEventBinding(String componentName, Class<? extends EventListener> listenerType, String eventType, String actionFqcn) throws Exception {
		Class<? extends AbstractAction> actionClass = (Class<? extends AbstractAction>)Class.forName(actionFqcn);

		addEventBinding(componentName, listenerType, eventType, actionClass);
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報追加メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 引数componentNameが既に登録されている場合、componentName用のMapを取り出し、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * componentNameが未登録の場合、componentName用のMapを新規に生成して、
	 * イベントタイプをキーにしてアクションを登録します。<br>
	 * 登録された情報はイベント紐付け情報保持オブジェクト（bindInfoフィールド）に保持され、
	 * コントローラのコンポーネント挿抜ハンドラによって参照されます。
	 *
	 * <p>[備 考] </p>
	 * 登録された紐付け情報が反映されるのは、componentNameをname属性値として持つ
	 * コンポーネントが画面追加されたタイミングです。
	 *
	 * @param componentName
	 * @param listenerType
	 * @param eventType
	 * @param actionClass
	 */
	public void addEventBinding(String componentName, Class<? extends EventListener> listenerType, String eventType, Class<? extends AbstractAction> actionClass) {

		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap = null;

		if(this.bindInfo.containsKey(componentName)) {
			listenerTypeMap = this.bindInfo.get(componentName);
		} else {
			listenerTypeMap = new HashMap<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>>();
			this.bindInfo.put(componentName, listenerTypeMap);
		}

		Map<String, Class<? extends AbstractAction>> eventTypeMap = null;

		if(listenerTypeMap.containsKey(listenerType)) {
			eventTypeMap = listenerTypeMap.get(listenerType);
		} else {
			eventTypeMap = new HashMap<String, Class<? extends AbstractAction>>();
			listenerTypeMap.put(listenerType, eventTypeMap);
		}

		eventTypeMap.put(eventType, actionClass);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentNameに紐付いているイベントリスナタイプマップを返却します。
	 *
	 * <p>[詳 細] </p>
	 * 下記構造のMapを返却します。
	 * <pre>
	 * [
	 *   イベントリスナ型 = [イベントハンドラメソッド名 = アクションクラス],
	 *   イベントリスナ型 = [イベントハンドラメソッド名 = アクションクラス],
	 *   イベントリスナ型 = [イベントハンドラメソッド名 = アクションクラス],
	 * ]
	 * </pre>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName イベントリスナタイプマップを取得するコンポーネント名
	 * @return イベントリスナタイプマップ
	 */
	public Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> getListenerTypeMap(String componentName) {
		return this.bindInfo.get(componentName);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentNameに紐付いているイベントリスナのリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentNameに対して以下のリスナタイプマップが紐付けられている場合、
	 * <pre>
	 * [
	 *   イベントリスナ型1 = [イベントハンドラメソッド名 = アクションクラス],
	 *   イベントリスナ型2 = [イベントハンドラメソッド名 = アクションクラス],
	 *   イベントリスナ型3 = [イベントハンドラメソッド名 = アクションクラス],
	 * ]
	 * </pre>
	 *
	 * 以下のリストを返却します。
	 * <pre>
	 * [イベントリスナ型1、イベントリスナ型2、イベントリスナ型3]
	 * </pre>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName イベントリスナリストを取得するコンポーネント名
	 * @return イベントリスナリスト
	 */
	public List<Class<? extends EventListener>> getListenerTypes(String componentName) {
		List<Class<? extends EventListener>> ret = new ArrayList<Class<? extends EventListener>>();

		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap =
			this.bindInfo.get(componentName);

		if(listenerTypeMap != null) {
			Set<Class<? extends EventListener>> keys = listenerTypeMap.keySet();
			for(Iterator<Class<? extends EventListener>> it = keys.iterator(); it.hasNext();) {
				Class<? extends EventListener> listener = it.next();
				ret.add(listener);
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentName、listenerTypeに紐付いているイベントタイプリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentNameに対して以下のリスナタイプマップが紐付けられている場合、
	 * <pre>
	 * [
	 *   イベントリスナ型1 = [イベントハンドラメソッド名1 = アクションクラス,
	 *                       イベントハンドラメソッド名2 = アクションクラス],
	 *   イベントリスナ型2 = [イベントハンドラメソッド名2 = アクションクラス],
	 *   イベントリスナ型3 = [イベントハンドラメソッド名3 = アクションクラス],
	 * ]
	 * </pre>
	 *
	 * イベントリスナ型1に対して以下のリストを返却します。
	 * <pre>
	 * [イベントハンドラメソッド名1、イベントハンドラメソッド名2]
	 * </pre>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName イベントタイプリストを取得するコンポーネント名
	 * @param listenerType イベントタイプリストを取得するイベントリスナタイプ
	 * @return イベントタイプリスト
	 */
	public List<String> getEventTypes(String componentName, Class<? extends EventListener> listenerType) {
		List<String> ret = new ArrayList<String>();

		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap =
			this.bindInfo.get(componentName);

		if(listenerTypeMap != null) {
			Map<String, Class<? extends AbstractAction>> eventTypeMap = listenerTypeMap.get(listenerType);
			if(eventTypeMap != null) {
				Set<String> keys = eventTypeMap.keySet();
				for(Iterator<String> it = keys.iterator(); it.hasNext();) {
					String eventType = it.next();
					ret.add(eventType);
				}
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentName、listenerType、eventTypeに紐付いているイベントタイプリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentNameに対して以下のリスナタイプマップが紐付けられている場合、
	 * <pre>
	 * [
	 *   イベントリスナ型1 = [イベントハンドラメソッド名1 = アクションクラス1,
	 *                       イベントハンドラメソッド名2 = アクションクラス2],
	 *   イベントリスナ型2 = [イベントハンドラメソッド名2 = アクションクラス3],
	 *   イベントリスナ型3 = [イベントハンドラメソッド名3 = アクションクラス4],
	 * ]
	 * </pre>
	 *
	 * イベントリスナ型1、イベントハンドラメソッド名1に対してアクションクラス1を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName アクションクラスを取得するコンポーネント名
	 * @param listenerType アクションクラスを取得するイベントリスナタイプ
	 * @param eventType アクションクラスを取得するイベントタイプ
	 * @return アクションクラス
	 */
	public Class<? extends AbstractAction> getActionClass(String componentName,
															Class<? extends EventListener> listenerType,
															String eventType)
	{
		Class<? extends AbstractAction> ret = null;

		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap =
			this.bindInfo.get(componentName);

		if(listenerTypeMap != null) {
			Map<String, Class<? extends AbstractAction>> eventTypeMap = listenerTypeMap.get(listenerType);
			if(eventTypeMap != null) {
				ret = eventTypeMap.get(eventType);
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentNameに対してイベント紐付けが行われているかどうか調べます。
	 *
	 * <p>[詳 細] </p>
	 * bindInfoフィールドにcomponentNameがキーとして含まれているかどうか調べます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName イベント紐付けが行われているかどうか調べるコンポーネント名
	 * @return true : 紐付け有り、false : 紐付け無し
	 */
	public boolean isEventBinding(String componentName) {
		return this.bindInfo.containsKey(componentName);
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報追加メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #addEventBindingImmediately(Component, Class, String, Class)}メソッドに
	 * 処理委譲します。
	 * 引数componentNameを名前として持つコンポーネントがメモリ上に存在しない場合、
	 * 処理は行われません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName
	 * @param listenerType
	 * @param eventType
	 * @param actionClass
	 */
	public void addEventBindingImmediately(String componentName, Class<? extends EventListener> listenerType,
											String eventType,
											Class<? extends AbstractAction> actionClass)
	{
		Component c = ComponentSearchUtil.searchComponentByNameFromAllWindow(componentName);
		if(c == null) {
			return;
		}

		addEventBindingImmediately(c, listenerType, eventType, actionClass);
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報追加メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #addEventBinding(String, Class, String, Class)}オーバーロードメソッドを
	 * 呼び出して、イベント紐付け情報保持オブジェクト（bindInfo）に追加します。<br>
	 * 追加後、ui Documentを取得して、追加された紐付け情報を即座に反映します。
	 *
	 * <p>[備 考] </p>
	 * {@link #addEventBinding(String, Class, String, Class)}と違い、
	 * 登録された情報が画面コンポーネントにイベントリスナ追加されるのはメソッド呼び出し直後です。
	 *
	 * @param component
	 * @param listenerType
	 * @param eventType
	 * @param actionClass
	 */
	public void addEventBindingImmediately(Component component,
											Class<? extends EventListener> listenerType,
											String eventType,
											Class<? extends AbstractAction> actionClass)
	{
		addEventBinding(component.getName(), listenerType, eventType, actionClass);

		List<String> eventTypes = new ArrayList<String>();
		eventTypes.add(eventType);
		this.controller.addListener(component, listenerType, eventTypes);
	}

	/**
	 * <p>[概 要]</p>
	 * イベント紐付け情報削除メソッドです.
	 *
	 * <p>[詳 細]</p>
	 * 保持されているコンポーネン紐付け情報から、引数componentName
	 * に紐付いている全リスナを削除します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param componentName
	 */
	public void removeEventBinding(String componentName) {
		this.bindInfo.remove(componentName);
	}

	/**
	 * <p>[概 要]</p>
	 * イベント紐付け情報削除メソッドです.
	 *
	 * <p>[詳 細]</p>
	 * 保持されているコンポーネン紐付け情報から、引数componentName
	 * に紐付いている引数listenerTypeを削除します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param componentName
	 * @param listenerType
	 */
	public void removeEventBinding(String componentName, Class<? extends EventListener> listenerType) {
		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap =
			this.bindInfo.get(componentName);

		listenerTypeMap.remove(listenerType);
	}

	/**
	 * <p>[概 要]</p>
	 * イベント紐付け情報削除メソッドです.
	 *
	 * <p>[詳 細]</p>
	 * 保持されているコンポーネン紐付け情報から、引数componentNameの
	 * 引数listenerTypeに紐付いているeventTypeを削除します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param componentName
	 * @param listenerType
	 * @param eventType
	 */
	public void removeEventBinding(String componentName,
									Class<? extends EventListener> listenerType,
									String eventType)
	{
		Map<Class<? extends EventListener>, Map<String, Class<? extends AbstractAction>>> listenerTypeMap =
			this.bindInfo.get(componentName);

		Map<String, Class<? extends AbstractAction>> eventTypeMap = listenerTypeMap.get(listenerType);
		eventTypeMap.remove(eventType);
	}

	/**
	 * <p>[概 要] </p>
	 * イベント紐付け情報削除メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #removeEventBinding(String, Class, String)}メソッドを
	 * 呼び出して、イベント紐付け情報保持オブジェクト（bindInfo）から
	 * 引数情報を元に削除を行います。<br>
	 * 削除後、引数componentから引数listenerを即座に削除反映します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component
	 * @param listenerType
	 * @param eventType
	 * @param listener
	 */
	public void removeEventBindingImmediately(Component component,
												Class listenerType,
												String eventType,
												EventListener listener)
		throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		String componentName = component.getName();
		removeEventBinding(componentName, listenerType, eventType);

		Field listenerListField = JComponent.class.getDeclaredField("listenerList");
		listenerListField.setAccessible(true);

		EventListenerList listenerList = (EventListenerList)listenerListField.get(component);

		listenerList.remove(listenerType, listener);
	}
}
