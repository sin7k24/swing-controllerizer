package com.oneitthing.swingcontrollerizer.controller;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.listener.ContainerListenerImpl;
import com.oneitthing.swingcontrollerizer.listener.WindowOpenShutListener;
import com.oneitthing.swingcontrollerizer.manager.WindowManager;

/**
 * <p>[概 要] </p>
 * アプリケーションを制御する抽象コントローラです。
 *
 * <p>[詳 細] </p>
 * JVM上のウィンドウレベルコンポーネントの開閉を監視して、
 * <ul>
 *   <li>コンポーネントに対するイベントリスナ追加</li>
 *   <li>イベント発生時のハンドラ集約</li>
 * </ul>
 * を行います。
 * <p>
 *
 * <b>コンポーネントに対するイベントリスナ追加</b>
 * <p>
 *
 * EventBinderフィールドがコンポーネントとアクションをイベントタイプ毎に紐付け管理します。<br>
 * 実装することで蓄積されます。<br>
 * ウィンドウコンポーネントの画面表示イベントを監視して、包含するコンポーネントを走査、
 * EventBinderに登録されているコンポーネント名であれば
 * 「{@link #handlerFacade(EventObject, Class, String, EventListener)}
 * をコールするイベントリスナプロキシ」をそのコンポーネントに対して設定します。
 * <p>
 *
 * <b>イベント発生時のハンドラ集約</b>
 * <p>
 *
 * 上記イベントリスナの自動追加により、SwingControllerizer使用アプリケーションのイベントハンドラは全て、
 * このクラスのhandlerFacadeメソッドになります。<br>
 * Swingコーディングにおけるイベントハンドラ追加の常套手段である、
 * <pre>
 * 	jbutton.addActionListener(new ActionListener(){
 * 		public void actionPerformed(ActionEvent evt) {
 * 			....
 *		}
 * 	});
 * </pre>
 * や、
 *
 * <pre>
 * 	JButton button = new JButton(new LoginAction());
 * </pre>
 * といった画面クラスで行うイベント追加と、イベントリスナ処理の実装は不要です。
 * <p>
 *
 * <p>[備 考] </p>
 *
 *
 */
public abstract class AbstractController implements Controller, WindowOpenShutListener {

    /** イベント紐付けオブジェクトです。 */
    private EventBinder eventBinder;

    /** アプリ起動～終了まで存在するデータ保存領域です。 */
    private Map<Object, Object> permanent;

    /** フレームワーク挙動情報を保持するオブジェクトです。 */
    private ClientConfig clientConfig;

    /** JVM上のウィンドウコンポーネントを管理するオブジェクトです。 */
    private WindowManager windowManager;

    /** バリデーションエラーを起こしているコンポーネントの情報を管理するオブジェクトです。 */
    private Map<Integer, Map<Integer, Map<String, Object>>> errorComponentSnapshot;

    /** 起動中イベントディスパッチスレッド識別子のリストです */
    private volatile Vector<String> invokeThreadIds = new Vector<String>();

    /**
     * <p>[概 要] </p>
     * イベント紐付けオブジェクトを取得します。
     *
     * <p>[詳 細] </p>
     * eventBinderフィールドオブジェクトを返却します。
     *
     * <p>[備 考] </p>
     *
     * @return イベント紐付けオブジェクト
     */
    public EventBinder getEventBinder() {
        return eventBinder;
    }

    /**
     * <p>[概 要] </p>
     * イベント紐付けオブジェクトを設定します。
     *
     * <p>[詳 細] </p>
     * eventBinderフィールドオブジェクトを設定します。
     *
     * <p>[備 考] </p>
     *
     * @param eventBinder イベント紐付けオブジェクト
     */
    public void setEventBinder(EventBinder eventBinder) {
        this.eventBinder = eventBinder;
    }

    /**
     * <p>[概 要] </p>
     * アプリ起動～終了まで存在するデータ保存領域を取得します。
     *
     * <p>[詳 細] </p>
     * permanentフィールドオブジェトを返却します。
     *
     * <p>[備 考] </p>
     *
     * @return アプリ起動～終了まで存在するデータ保存領域
     */
    public Map<Object, Object> getPermanent() {
        return this.permanent;
    }

    /**
     * <p>[概 要] </p>
     * アプリ起動～終了まで存在するデータ保存領域を設定します。
     *
     * <p>[詳 細] </p>
     * permanentフィールドオブジェクトを設定します。
     *
     * <p>[備 考] </p>
     *
     * @param permanent アプリ起動～終了まで存在するデータ保存領域
     */
    public void setPermanent(Map<Object, Object> Permanent) {
        this.permanent = Permanent;
    }

    /**
     * <p>[概 要] </p>
     * フレームワーク挙動情報を保持するオブジェクトを取得します。
     *
     * <p>[詳 細] </p>
     * clientConfigフィールドを返却します。
     *
     * <p>[備 考] </p>
     *
     * @return フレームワーク挙動情報を保持するオブジェクト
     */
    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    /**
     * <p>[概 要] </p>
     * フレームワーク挙動情報を保持するオブジェクトを設定します。
     *
     * <p>[詳 細] </p>
     * clientConfigフィールドを設定します。
     *
     * <p>[備 考] </p>
     *
     * @param clientConfig フレームワーク挙動情報を保持するオブジェクト
     */
    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * <p>[概 要] </p>
     * JVM上のウィンドウコンポーネントを管理するオブジェクトを返却します。
     *
     * <p>[詳 細] </p>
     * windowManagerフィールドを返却します。
     *
     * <p>[備 考] </p>
     *
     * @return JVM上のウィンドウコンポーネントを管理するオブジェクト
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * <p>[概 要] </p>
     * JVM上のウィンドウコンポーネントを管理するオブジェクトを設定します。
     *
     * <p>[詳 細] </p>
     * windowManagerフィールドを設定します。
     *
     * <p>[備 考] </p>
     *
     * @param windowManager JVM上のウィンドウコンポーネントを管理するオブジェクト
     */
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * <p>[概 要] </p>
     * バリデーションエラーを起こしているコンポーネントの情報を管理するオブジェクトを返却します。
     *
     * <p>[詳 細] </p>
     * errorComponentSnapshotフィールドを返却します。
     *
     * <p>[備 考] </p>
     *
     * @return バリデーションエラーを起こしているコンポーネントの情報を管理するオブジェクト
     */
    public Map<Integer, Map<Integer, Map<String, Object>>> getErrorComponentSnapshot() {
        return errorComponentSnapshot;
    }

    /**
     * <p>[概 要] </p>
     * バリデーションエラーを起こしているコンポーネントの情報を管理するオブジェクトを設定します。
     *
     * <p>[詳 細] </p>
     * errorComponentSnapshotフィールドを設定します。
     *
     * <p>[備 考] </p>
     *
     * @param errorComponentSnapshot バリデーションエラーを起こしているコンポーネントの情報を管理するオブジェクト
     */
    public void setErrorComponentSnapshot(
            Map<Integer, Map<Integer, Map<String, Object>>> errorComponentSnapshot) {
        this.errorComponentSnapshot = errorComponentSnapshot;
    }

    /**
     * <p>[概 要] </p>
     * デフォルトコンストラクタです。
     *
     * <p>[詳 細] </p>
     * コントローラの初期化を行います。
     * <p>
     * 	<UL>
     * 		<LI>イベント紐付け情報オブジェクトの生成</LI>
     * 		<LI>アプリ起動～終了まで存在するデータ保存領域の生成</LI>
     * 		<LI>SwingControllerizer設定情報保持領域の生成</LI>
     * 		<LI>エレメントのエラー前クローン保存領域</LI>
     * 	</UL>
     * <p>
     * を行った後、イベント紐付け登録読込みの為、{@link #bind(EventBinder)}メソッドを
     * テンプレートコールします。
     *
     * <p>
     * イベント紐付け情報が正常に読み込まれた後、アプリケーション初期化の為のinitialize
     * がコールされます。
     *
     * <p>[備 考] </p>
     *
     */
    public AbstractController() {
        setEventBinder(new EventBinder(this));
        setClientConfig(new ClientConfig());
        setPermanent(new HashMap<Object, Object>());
        WindowManager.getInstance().addWindowOpenShutListener(this);
        setWindowManager(WindowManager.getInstance());
        setErrorComponentSnapshot(new HashMap<Integer, Map<Integer, Map<String, Object>>>());

        bind(getEventBinder());

        initialize(getClientConfig());
        postInitialize(getClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });
    }

    /**
     * <p>[概 要] </p>
     * 初期化処理が記述可能なメソッドです。
     *
     * <p>[詳 細] </p>
     * Document、ClientSessionが生成されるタイミングでテンプレートコールされます。
     * デフォルトの処理は有りません。
     *
     * <p>[備 考] </p>
     * 業務固有の初期化処理が必要な場合は、具象コントローラ内でこのメソッドを
     * オーバーライドして下さい。<br>
     * 又、SwingControllerizerの挙動設定をこのタイミングで設定することが出来ます。<p>
     *
     * <b>使用例）</b><br>
     * <pre class="samplecode">
     *	protected void initialize(ClientConfig config){
     *		// デフォルトJMS接続環境を設定
     *		Hashtable<String, String> jmsEnvironment = new Hashtable<String, String>();
     *		jmsEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
     *		jmsEnvironment.put(Context.PROVIDER_URL, "localhost:1099");
     *		jmsEnvironment.put("java.naming.rmi.security.manager", "yes");
     *		jmsEnvironment.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");
     *		config.setDefaultJmsEnvironment(jmsEnvironment);
     *
     *		// デフォルトEJB接続環境を設定
     *		Hashtable<String, String> ejbEnvironment = new Hashtable<String, String>();
     *		ejbEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.NamingContextFactory");
     *		ejbEnvironment.put(Context.PROVIDER_URL, "jnp://localhost:1099");
     *		config.setDefaultEjbEnvironment(ejbEnvironment);
     *
     *		// デフォルトDatabase接続環境を設定
     *		Hashtable<String, String> dbEnvironment = new Hashtable<String, String>();
     *		dbEnvironment.put(DatabaseCore.DB_DRIVER_FQCN, "org.postgresql.Driver");
     *		dbEnvironment.put(DatabaseCore.DB_URL, "jdbc:postgresql://localhost:5432/RFD");
     *		dbEnvironment.put(DatabaseCore.DB_USER, "nakanishi");
     *		dbEnvironment.put(DatabaseCore.DB_PASSWORD, "shingon");
     *		dbEnvironment.put(DatabaseCore.DB_AUTO_COMMIT, "true");
     *		config.setDefaultDatabaseEnvironment(dbEnvironment);
     *
     *		Hashtable<String, String> httpEnvironment = new Hashtable<String, String>();
     *		httpEnvironment.put(HTTPRequestCore.HTTP_URL_PREFIX, "http://localhost:8080/RFDforSwingWeb/");
     *		config.setDefaultHttpEnvironment(httpEnvironment);
     *	}
     * </pre>
     *
     * @param config フレームワーク挙動情報を保持するオブジェクト
     */
    protected void initialize(ClientConfig config) {
    }

    /**
     * <p>[概 要] </p>
     * ClientConfigの値に依存するクライアント初期化設定を行います。
     *
     * <p>[詳 細] </p>
     *
     * <p>[備 考] </p>
     *
     * @param config フレームワーク挙動情報を保持するオブジェクト
     */
    protected void postInitialize(ClientConfig config) {
    }

    /**
     * <p>[概 要] </p>
     * 追加されたコンポーネントに対して予約されているリスナ追加をaddListenerメソッドに委譲します。
     *
     * <p>[詳 細] </p>
     * ContainerListenerImplが検知したコンポーネント追加イベント時にコールされます。<br>
     * 追加されたコンポーネントの名前がEventBinderに紐付け予約されているものである場合、<br>
     * 追加すべきイベントリスナ分addListenerメソッドを呼び出します。
     *
     * <p>[備 考] </p>
     *
     * @param addedComponent 画面に追加されたコンポーネント
     */
    public void bindEvents(Component addedComponent) {
        String componentName = addedComponent.getName();
        if (componentName != null && !"".equals(componentName)) {
            List<Class<? extends EventListener>> listenerTypes = this.eventBinder
                    .getListenerTypes(addedComponent.getName());

            for (Class<? extends EventListener> listenerType : listenerTypes) {
                List<String> eventTypes = this.eventBinder.getEventTypes(componentName, listenerType);
                addListener(addedComponent, listenerType, eventTypes);
            }
        }
    }

    /**
     * <p>[概 要] </p>
     * 追加されたコンポーネントに対してイベントリスナを追加します。
     *
     * <p>[詳 細] </p>
     * 引数listenerTypeのプロキシオブジェクトを生成して、
     * 引数componentにイベントリスナとして追加します。
     * <p>
     *
     * Swingコンポーネントはイベントタイプによってイベント追加メソッド名が異なります。<br>
     * 生成されたプロキシはJavaの命名規則「"add" + listenerTypeクラス名」に沿って
     * 追加メソッド名を以下のように動的判別します。
     *
     * <pre>
     * 		String addMethodName = "add" + listenerType.getSimpleName();
     *		Method addMethod = component.getClass().getMethod(addMethodName, listenerType);
     *		addMethod.invoke(component, eventListener);
     * </pre>
     *
     * <p>[備 考] </p>
     * プロキシ内のハンドラでは発生したイベントタイプを引数eventTypesと比較、
     * eventTypesに含まれるハンドラメソッドが呼ばれる場合は
     * {@link #handlerFacade(EventObject, Class, String, EventListener)}
     * をコールします。
     *
     * @param component イベントリスナプロキシを追加するコンポーネント
     * @param listenerType プロキシの原型
     * @param eventTypes イベントハンドラメソッド名リスト
     */
    public void addListener(Component component,
            Class<? extends EventListener> listenerType,
            List<String> eventTypes) {
        try {
            InvocationHandler handler = new EventInvocationHandler(this, listenerType, eventTypes, component);
            EventListener eventListener = (EventListener) Proxy.newProxyInstance(
                    listenerType.getClassLoader(),
                    new Class[] { listenerType },
                    handler);

            String addMethodName = "add" + listenerType.getSimpleName();
            Method addMethod = component.getClass().getMethod(addMethodName, listenerType);
            addMethod.invoke(component, eventListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * <p>[概 要] </p>
     * 全ユーザ定義イベントをハンドルする入り口になるメソッドです。
     *
     * <p>[詳 細] </p>
     * 発生したイベントタイプ、イベントを起こしたコンポーネント名を元に、<br>
     * イベント紐付けオブジェクト（EventBinder）から対応するアクションクラス型を取得します。<br>
     * 取得したアクションクラス型を引数にして{@link #invoke(Class, ParameterMapping)}に処理委譲します。
     *
     * <p>[備 考] </p>
     *
     * @param e
     * @param listenerType
     * @param eventType
     * @param eventListenerProxy
     */
    public void handlerFacade(final EventObject e,
            Class<? extends EventListener> listenerType,
            String eventType,
            EventListener eventListenerProxy,
            Component listenerAddedComponent) {
        final Class<? extends AbstractAction> actionClass = getEventBinder()
                .getActionClass(listenerAddedComponent.getName(), listenerType, eventType);

        final ParameterMapping parameterMapping = createParameterMapping(e, listenerType, eventType, eventListenerProxy,
                listenerAddedComponent);

        if (getClientConfig().isDuplicateActionInvoke()) {
            invoke(actionClass, parameterMapping);
        } else {
            final String eventSourceId = String.valueOf(e.getSource().hashCode());
            synchronized (this.invokeThreadIds) {
                if (!this.invokeThreadIds.contains(eventSourceId)) {
                    this.invokeThreadIds.add(eventSourceId);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                invoke(actionClass, parameterMapping);
                            } finally {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        invokeThreadIds.remove(eventSourceId);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * <p>[概 要] </p>
     * MVC各レイヤを巡回するParameteraMappingオブジェクトを生成、初期化します。
     *
     * <p>[詳 細] </p>
     * イベント発生の際、Controller、Action、Modelを流れるデータマップを作成します。<br>
     * このメソッドによって、以下の情報がParameterMappingオブジェクトに設定されます。<p>
     * <ul>
     * 		<li>発生したイベントオブジェクト</li>
     * 		<li>イベントソースコンポーネント</li>
     * 		<li>イベントリスナクラス型</li>
     * 		<li>イベントリスナインスタンス</li>
     * 		<li>イベントタイプ（イベントハンドラメソッド名）</li>
     * </ul>
     *
     * <p>[備 考] </p>
     *
     * @param e イベントオブジェクト
     * @param listenerType イベントリスナクラス型
     * @param eventType イベントタイプ（イベントハンドラメソッド名）
     * @param eventListenerProxy イベントリスナインスタンス
     * @return MVC各レイヤを巡回するパラメータオブジェクト
     */
    protected ParameterMapping createParameterMapping(EventObject e,
            Class<? extends EventListener> listenerType,
            String eventType,
            EventListener eventListenerProxy,
            Component listenerAddedComponent) {
        ParameterMapping ret = new ParameterMapping();

        ret.setEventObject(e);
        ret.setEventSource(e.getSource());
        if (e.getSource() instanceof Component) {
            ret.setEventSourceParent(((Component) e.getSource()).getParent());
        }
        ret.setEventListenerType(listenerType);
        ret.setEventListener(eventListenerProxy);
        ret.setEventType(eventType);
        ret.setListenerAddedComponent(listenerAddedComponent);

        return ret;
    }

    /**
     * <p>[概 要]</p>
     * UIコンポーネントとアクションを紐付ける、EventBinderオブジェクトを作成するメソッドです。
     *
     * <p>[詳 細]</p>
     * コントローラ実装クラスでこのメソッドをオーバーライドして、<br>
     * eventBinder.addEventBinding("コンポーネント名", "イベントリスナ型", "イベントハンドラメソッド名", BaseAction継承クラス);<br>
     * のように紐付け処理を列挙して下さい。<br>
     *
     * ex.)eventBinder.addEventBinding("loginFrame.jbLogin", ActionListener.class, "actionPerformed", LoginAction.class);
     *
     * <p>[備 考]</p>
     *
     * @param eventBinder イベント紐付けオブジェクト
     */
    protected void bind(EventBinder eventBinder) {
    }

    /**
     * <p>[概 要] </p>
     * ウィンドウコンポーネント表示時にコールされるイベントハンドラです。
     *
     * <p>[詳 細] </p>
     * {@link WindowOpenShutListener#windowOpen(AWTEvent)}を実装します。<br>
     * 表示された画面が包含するコンテナへのContainerListenerImple追加と、
     * 走査されたコンポーネントにイベントリスナを追加する為のメソッドをコールします。
     *
     * <p>[備 考] </p>
     *
     * @param evt ウィンドウオープンイベントオブジェクト
     */
    @Override
    public void windowOpen(AWTEvent evt) {
        registWindow((Window) evt.getSource());

        getErrorComponentSnapshot().put(evt.getSource().hashCode(), new HashMap<Integer, Map<String, Object>>());
    }

    /**
     * <p>[概 要] </p>
     * ウィンドウコンポーネントクローズ時にコールされるイベントハンドラです。
     *
     * <p>[詳 細] </p>
     * {@link WindowOpenShutListener#windowShut(AWTEvent)}を実装します。<br>
     * {@link ClientConfig#isAutoWindowDispose()}がtrueの場合、
     * 閉じたウィンドウコンポーネントのdisposeを行います。
     *
     * <p>[備 考] </p>
     *
     * @param evt ウィンドウクローズイベントオブジェクト
     */
    @Override
    public void windowShut(AWTEvent evt) {
        if (getClientConfig().isAutoWindowDispose()) {
            ((Window) evt.getSource()).dispose();
        }

        getErrorComponentSnapshot().remove(evt.getSource());
    }

    /**
     * <p>[概 要] </p>
     * ウィンドウコンポーネントをSwingControllerizer管理画面にします。
     *
     * <p>[詳 細] </p>
     * {@link #searchComponent(Component)}に処理委譲します。
     *
     * <p>[備 考] </p>
     *
     * @param window SwingControllerizer管理下に置くウィンドウコンポーネント
     */
    public void registWindow(Window window) {
        searchComponent(window);
    }

    /**
     * <p>[概 要] </p>
     * ウィンドウコンポーネントクローズ時にコールされるイベントハンドラです。
     *
     * <p>[詳 細] </p>
     * 引数addedComponentが包含するコンポーネントを再帰的に走査します。<br>
     * 走査されたコンポーネントはbindEventsメソッドにイベントリスナ追加を委譲します。<br>
     * コンポーネントがコンテナコンポーネントであった場合は、ContainerListenerImplを追加します。
     *
     * <p>[備 考] </p>
     *
     * @param addedComponent 追加されたコンポーネント
     */
    private void searchComponent(Component addedComponent) {
        if(addedComponent instanceof JDesktopPane)
        System.out.println(addedComponent);
        bindEvents(addedComponent);
        if (addedComponent instanceof Container) {

            Component[] cs = ((Container) addedComponent).getComponents();
            for (Component c : cs) {
                searchComponent(c);
            }

            if(addedComponent instanceof JMenu) {
                JMenu menu = (JMenu)addedComponent;
                for(int i=0; i<menu.getMenuComponentCount(); i++) {
                    searchComponent(menu.getMenuComponent(i));
                }
            }
            ((Container) addedComponent).addContainerListener(new ContainerListenerImpl(this));
        }
    }

    /**
     * <p>[概 要] </p>
     * JVMのシャットダウン時にコールされるフックハンドラです。
     *
     * <p>[詳 細] </p>
     * 具象コントローラでこのメソッドをオーバーライドして、
     * アプリケーションの終末処理を記述します。
     *
     * <p>[備 考] </p>
     *
     */
    protected void shutdown() {
    }
}
