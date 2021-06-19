package com.oneitthing.swingcontrollerizer.controller;

import java.awt.Component;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.oneitthing.swingcontrollerizer.action.Action;
import com.oneitthing.swingcontrollerizer.model.Model;

/**
 * <p>[概 要] </p>
 * MVC各レイヤを伝播するパラメータオブジェクトです。
 *
 * <p>[詳 細] </p>
 * JVMから提供されるイベント情報、フレームワークが動作する為の情報、ビジネスロジックから
 * 参照が必要な情報が格納されます。<br>
 * イベント発生時にコントローラによってインスタンス生成され、
 * アクション→コントローラ→モデル→コントローラ→アクション→コントローラ<br>
 * と伝播して、イベントハンドリングが終了した後に破棄されます。
 * <p>
 *
 * アクションの中で発生したイベント情報を取得したい場合には、
 * <pre class="samplecode">
 * EventObject evt = parameterMapping.getEventObject();
 * </pre>
 * 等で取得することが出来ます。
 * <p>
 *
 * 又、{@link #put(Object, Object)}を使用することで、
 * ビジネスロジック固有のパラメータを格納することが出来ます。<br>
 * MVCの各フックポイントでこのクラスの同一インスタンスを参照することで、
 * 「アクションレイヤで設定したパラメータをモデルレイヤで参照する」
 * といった処理を、広いスコープの変数を用意することなく実装出来ます。
 *
 * <p>[備 考] </p>
 * SwingControllerizerによって自動的にset系メソッドがコールされ、情報が設定されます。
 *
 *


 *

 */
public class ParameterMapping {

	/** 具象イベントハンドラ名マッピングのキー定数です。 */
	public static final String ACTION_CLASS_NAME = "ACTION_CLASS_NAME";

	/** Swingクライアントイベントマッピングのキー定数です。 */
	public static final String EVENT_OBJECT = "EVENT_OBJECT";

	/** イベントリスナプロキシインスタンスのキー定数です。 */
	public static final String EVENT_LISTENER = "EVENT_LISTENER";

	/** イベントリスナの型を示すキー定数です。 */
	public static final String EVENT_LISTENER_TYPE = "EVENT_LISTENER_TYPE";

	/** イベントリスナプロキシがハンドリングした、イベントリスナのハンドラメソッド名を示すキー定数です。 */
	public static final String EVENT_TYPE = "EVENT_TYPE";

	/** イベントを起こしたコンポーネントマッピングのキー定数です。 */
	public static final String EVENT_SOURCE_OBJECT = "EVENT_SOURCE_OBJECT";

	/** イベントを起こしたコンポーネントの親コンポーネントを示すキー定数です。 */
	public static final String EVENT_SOURCE_OBJECT_PARENT = "EVENT_SOURCE_OBJECT_PARENT";

	/** コントローラによって実行されるモデルクラス群のキー定数です。 */
	public static final String MODEL_CLASSES = "MODEL_CLASSES";

	/** モデルの登録数を示す定数です。 */
	public static final String MODEL_RESERVED_NUM = "MODEL_RESERVED_NUM";

	/** モデルの終了数を示す定数です。 */
	public static final String MODEL_FINISHED_NUM = "MODEL_FINISHED_NUM";

	/** 実行されるバリデータ群のキー定数です。 */
	public static final String VALIDATORS = "VALIDATORS";

	/** 実行されるアクションのインスタンスを示すキー定数です。 */
	public static final String ACTION_INSTANCE = "ACTION_INSTANCE";

	/** イベントを発生させたエレメントが所属するウィンドウレベルエレメントを示すキー定数です。 */
	public static final String WAIT_MODELS_DONE = "WAIT_MODELS_DONE";

	/** モデル連続実行時、一モデルの終了を待つかどうかのフラグを示すキー定数です。 */
	public static final String RUN_MODELS_AND_NO_WAIT = "RUN_MODELS_AND_NO_WAIT";

	/** モデル同期実行時に発生したInterruptedExceptionをエラーと見做すかどうかのフラグを表すキー定数です。 */
	public static final String ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL = "ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL";

	public static final String LISTENER_ADDED_COMPONENT = "LISTENER_ADDED_COMPONENT";

	/** このクラスが保持するデータが全て入ったMapオブジェクトです。 */
	private Map<Object, Object> parameters;


	/**
	 * <p>[概 要] </p>
	 * デフォルトコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドMap領域を初期化します。
	 *
	 * <p>[備 考] </p>
	 */
	public ParameterMapping(){
		this.parameters = new HashMap<Object, Object>();

		setEventObject(null);
		setEventSource(null);
		setEventSourceParent(null);
		setEventListener(null);
		setEventListenerType(null);
		setEventType(null);
		setModelClasses(null);
		setModelReservedNum(0);
		setModelFinishedNum(0);
		setActionInstance(null);
		setRunModelsAndNoWait(false);
		setAllowInteruptedExceptionOnSyncModel(false);
	}

	/**
	 * <p>[概 要] </p>
	 * クライアントMVCレイヤ内で追加されたパラメータを取得します。
	 *
	 * <p>[詳 細] </p>
	 * アクション、モデル内で伝播させる情報を取得します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param key マッピングキー
	 * @return マッピング値
	 */
	public Object get(Object key){
		return this.parameters.get(key);
	}

	/**
	 * <p>[概 要] </p>
	 * クライアントMVCレイヤ内で自由に使用出来るパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * ParameterMappingオブジェクトに対して、key=valueの形式で値を登録します。
	 *
	 * <p>[備 考] </p>
	 * 一イベントハンドリング間のビジネスロジックに必要な、任意のパラメータ
	 * を格納する為に使用します。
	 *
	 * @param key マッピングキー
	 * @param value マッピング値
	 */
	public void put(Object key, Object value){
		this.parameters.put(key, value);
	}

	/**
	 * <p>[概 要]</p>
	 * Swingコンポーネントが発行したイベントオブジェクトを返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"EVENT_OBJECT"キーで値を取り出します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return Swingコンポーネントが発行したイベントオブジェクト
	 */
	public EventObject getEventObject() {
		return (EventObject)this.parameters.get(EVENT_OBJECT);
	}

	/**
	 * <p>[概 要]</p>
	 * Swingコンポーネントが発行したイベントオブジェクトを返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_OBJECT"キーで引数eventObjectを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param eventObject Swingコンポーネントが発行したイベントオブジェクト
	 */
	public void setEventObject(EventObject eventObject) {
		this.parameters.put(EVENT_OBJECT, eventObject);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントを起こしたオブジェクトを返却します。
	 *
	 * <p>[詳 細]</p>
	 * paramtersフィールドから"EVENT_SOURCE_OBJECT"キーで値を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return イベントを起こしたオブジェクト
	 */
	public Object getEventSource() {
		return this.parameters.get(EVENT_SOURCE_OBJECT);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントを起こしたオブジェクトを設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_SOURCE_OBJECT"キーで引数eventSourceObjectを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param eventSourceComponent イベントを起こしたオブジェクト
	 */
	public void setEventSource(Object eventSourceObject) {
		this.parameters.put(EVENT_SOURCE_OBJECT, eventSourceObject);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントを起こしたオブジェクトの親を返却します。
	 *
	 * <p>[詳 細]</p>
	 * paramtersフィールドから"EVENT_SOURCE_OBJECT_PARENT"キーで値を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return イベントを起こしたオブジェクトの親
	 */
	public Object getEventSourceParent() {
		return this.parameters.get(EVENT_SOURCE_OBJECT_PARENT);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントを起こしたオブジェクトの親を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_SOURCE_OBJECT_PARENT"キーで引数eventSourceObjectParentを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param eventSourceComponentParent イベントを起こしたオブジェクトの親
	 */
	public void setEventSourceParent(Object eventSourceObjectParent) {
		this.parameters.put(EVENT_SOURCE_OBJECT_PARENT, eventSourceObjectParent);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントリスナプロキシインスタンスを返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"EVENT_LISTENER"キーで値を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return イベントリスナプロキシインスタンス
	 */
	public EventListener getEventListener() {
		return (EventListener)this.parameters.get(EVENT_LISTENER);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントリスナプロキシインスタンスを設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_LISTENRE"キーで引数eventListenerを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param eventListener イベントリスナプロキシインスタンス
	 */
	public void setEventListener(EventListener eventListener) {
		this.parameters.put(EVENT_LISTENER, eventListener);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントリスナの型を返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"EVENT_LISTENER_TYPE"キーで値を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return イベントリスナの型
	 */
	public Class<? extends EventListener> getEventListenerType() {
		return (Class<? extends EventListener>)this.parameters.get(EVENT_LISTENER_TYPE);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントリスナの型を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_LISTENER_TYPE"キーで引数listenerTypeを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param listenerType イベントリスナの型
	 */
	public void setEventListenerType(Class<? extends EventListener> listenerType) {
		this.parameters.put(EVENT_LISTENER_TYPE, listenerType);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントハンドラメソッド名を返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"EVENT_TYPE"キーで値を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return イベントハンドラメソッド名
	 */
	public String getEventType() {
		return (String)this.parameters.get(EVENT_TYPE);
	}

	/**
	 * <p>[概 要]</p>
	 * イベントハンドラメソッド名を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"EVENT_TYPE"キーで引数eventTypeを設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param eventType イベントハンドラメソッド名
	 */
	public void setEventType(String eventType) {
		this.parameters.put(EVENT_TYPE, eventType);
	}

	/**
	 * <p>[概 要]</p>
	 * モデルクラス一覧を取得します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"MODES_CLASSES"キーで値を取得します。
	 * 登録されているモデルクラス一覧を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return 登録されているモデルクラス一覧
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Model>> getModelClasses() {
		return (List<Class<? extends Model>>)this.parameters.get(MODEL_CLASSES);
	}

	/**
	 * <p>[概 要]</p>
	 * モデルクラス一覧を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"MODEL_CLASSES"キーで引数modelClassesを設定します。
	 * 実行するモデルクラスを登録します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param modelClasses 実行するモデルクラス
	 */
	public void setModelClasses(List<Class<? extends Model>> modelClasses) {
		this.parameters.put(MODEL_CLASSES, modelClasses);
	}

	/**
	 * <p>[概 要]</p>
	 * 登録モデル数を返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"MODEL_RESERVED_NUM"キーで値を取得します。
	 * 登録したモデル数を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return 登録したモデル数
	 */
	public int getModelReservedNum() {
		return ((Integer)this.parameters.get(MODEL_RESERVED_NUM)).intValue();
	}

	/**
	 * <p>[概 要]</p>
	 * 登録モデル数を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"MODEL_RESERVED_NUM"キーで引数modelReservedNumを設定します。
	 * 登録したモデル数を設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param modelReservedNum 登録するモデル数
	 */
	public void setModelReservedNum(int modelReservedNum) {
		this.parameters.put(MODEL_RESERVED_NUM, modelReservedNum);
	}

	/**
	 * <p>[概 要]</p>
	 * 終了モデル数を返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"MODEL_FINISHED_NUM"キーで値を取得します。
	 * 終了したモデル数を取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @return 終了したモデル数
	 */
	public int getModelFinishedNum() {
		return ((Integer)this.parameters.get(MODEL_FINISHED_NUM)).intValue();
	}

	/**
	 * <p>[概 要]</p>
	 * 終了モデル数を設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"MODEL_FINISHED_NUM"キーで引数modelFinishedNumを設定します。
	 * 終了したモデル数を設定します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param modelFinishedNum 終了したモデル数
	 */
	public void setModelFinishedNum(int modelFinishedNum) {
		this.parameters.put(MODEL_FINISHED_NUM, modelFinishedNum);
	}

	/**
	 * <p>[概 要]</p>
	 * アクションインスタンスを返却します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドから"ACTION_INSTANCE"キーで値を取得します。
	 * 登録されているアクションを取得します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param 登録されているアクション
	 */
	public Action getActionInstance() {
		return (Action)this.parameters.get(ACTION_INSTANCE);
	}

	/**
	 * <p>[概 要]</p>
	 * アクションインスタンスを設定します。
	 *
	 * <p>[詳 細]</p>
	 * parametersフィールドに"ACTION_INSTANCE"キーで引数actionを設定します。
	 * 実行するアクションを登録します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param action 実行するアクション
	 */
	public void setActionInstance(Action action) {
		this.parameters.put(ACTION_INSTANCE, action);
	}

	/**
	 * <p>[概 要] </p>
	 * 複数予約されたモデルの実行動作を取得します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドから"RUN_MODELS_AND_NO_WAIT"キーで値を取得します。<br>
	 * モデル群がコントローラに連続実行される際、モデルの処理結果を待たずに
	 * 次のモデルを実行するかどうかのフラグを取得します。
	 *
	 * <p>[備 考] </p>
	 * モデルをシーケンシャルに実行しない場合、モデルは登録された順序で実行されますが
	 * レスポンスを待たずに次のモデルが実行されます。
	 *
	 * @return シーケンシャルに実行しない場合はtrue、それ以外はfalse
	 */
	public boolean isRunModelsAndNoWait(){
		return ((Boolean)this.parameters.get(RUN_MODELS_AND_NO_WAIT)).booleanValue();
	}

	/**
	 * <p>[概 要] </p>
	 * 複数予約されたモデルの実行動作を設定します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに"RUN_MODELS_AND_NO_WAIT"キーで引数runModelsAndNoWaitを設定します。<br>
	 * モデル群がコントローラに実行される際、モデルの処理結果を待たずに
	 * 次モデルを実行するかどうかのフラグを設定します。
	 *
	 * <p>[備 考] </p>
	 * モデルをシーケンシャルに実行しない場合、モデルは登録された順序で実行されますが
	 * レスポンスを待たずに次のモデルが実行されます。
	 *
	 * @param runModelsAndNoWait シーケンシャルに実行しない場合はtrue、それ以外はfalse
	 */
	public void setRunModelsAndNoWait(boolean runModelsAndNoWait) {
		this.parameters.put(RUN_MODELS_AND_NO_WAIT, runModelsAndNoWait);
	}

	/**
	 * <p>[概 要] </p>
	 * モデル同期実行時に発生したInterruptedExceptionをエラーと見做すかどうかのフラグを返却します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドから"ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL"キーで値を取得します。<br>
	 * 実行モデルの処理終了をコントローラが待っている際にInterruptedExceptionが発生した場合に
	 * エラーと見做すか見做さないかを返却します。<br/>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデル同期実行時に発生したInterruptedExceptionをエラーと見做すかどうかのフラグ
	 */
	public boolean isAllowInteruptedExceptionOnSyncModel() {
		return ((Boolean)this.parameters.get(ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL)).booleanValue();
	}

	/**
	 * <p>[概 要] </p>
	 * モデル同期実行時に発生したInterruptedExceptionをエラーと見做すかどうかのフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに"ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL"キーで値を取得します。<br>
	 * 実行モデルの処理終了をコントローラが待っている際にInterruptedExceptionが発生した場合に
	 * エラーと見做すか見做さないかを設定します。<br/>
	 *
	 * <p>[備 考] </p>
	 * TimerProcessCoreがActionを実行中で、Actionに同期実行モデルがreserveされている場合、
	 * TimerProcessCoreをstopするとInterruptedExceptionがスローされます。<br>
	 * これをエラーではなく、中止と見做す為にTimerProcessCoreManagerが内部的にtrueに設定します。
	 *
	 * @param allowInteruptedExceptionOnSyncModel モデル同期実行時に発生したInterruptedExceptionをエラーと見做すかどうかのフラグ
	 */
	public void setAllowInteruptedExceptionOnSyncModel(boolean allowInteruptedExceptionOnSyncModel) {
		this.parameters.put(ALLOW_INTERUPTED_EXCEPTION_ON_SYNC_MODEL, allowInteruptedExceptionOnSyncModel);
	}

	/**
	 * <p>[概 要] </p>
	 * イベントリスナが付与されたComponentを返却します。
	 *
	 * <p>[詳 細] </p>
	 * {@link EventObject#getSource()}で返却されるオブジェクトではなく、
	 * 実際にイベントリスナが付与されたComponentを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return イベントリスナが付与されたComponent
	 */
	public Component getListenerAddedComponent() {
		return (Component)this.parameters.get(LISTENER_ADDED_COMPONENT);
	}

	/**
	 * <p>[概 要] </p>
	 * イベントリスナが付与されたComponentを設定します。
	 *
	 * <p>[詳 細] </p>
	 * {@link EventObject#getSource()}で返却されるオブジェクトではなく、
	 * 実際にイベントリスナが付与されたComponentを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listenerAddedComponent イベントリスナが付与されたComponent
	 */
	public void setListenerAddedComponent(Component listenerAddedComponent) {
		this.parameters.put(LISTENER_ADDED_COMPONENT, listenerAddedComponent);
	}

	/**
	 * <p>[概 要] </p>
	 * このクラスオブジェクトの複製を返却します。
	 *
	 * <p>[詳 細] </p>
	 * ParameterMappingのシャローコピーを行います。<br>
	 * ParameterMappingオブジェクト、及び保持するプロパティは完全なコピーを作りますが、
	 * parametersプロパティ内で保持する定数キー要素値のアドレス参照はコピーしません。
	 * <br />
	 * parameterMapping#parametersと、<br>
	 * clonedParameterMapping#parameters<br>
	 * は異なるアドレスで保持されますが、
	 * <br />
	 * parameterMapping#getEventSourceと、<br>
	 * clonedParameterMapping#getEventSource<br>
	 * は同じアドレスを返却します。
	 *
	 * <p>[備 考] </p>
	 * このメソッドはアクションからコントローラに別アクション実行を委譲する場合等に使用します。<br>
	 * <pre class="samplecode">
     *    protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
     *        this.getController().invoke(OtherAction.class, parameterMapping.clone());
     *        return true;
     *    }
     * </pre>
     * コントローラはコールバック（successForwardやfailureForward）先アクションアドレスを
     * ParameterMapping#getActionInstanceで判別しますが、invokeが呼ばれると左記のアドレスを指定
     * された新規アクションのアドレスで上書きします。<br>
     * 上記のようにinvokeを呼び出すことで、コントローラが認識するアクションアドレスの上書き
     * を抑止することが出来ます。
	 *
	 * @return 複製されたParameterMappingオブジェクト
	 */
	public ParameterMapping clone() {
		ParameterMapping pm = new ParameterMapping();
		for (Iterator<Object> itr = this.parameters.keySet().iterator(); itr.hasNext(); ) {
			Object key = itr.next();
			pm.put(key, this.parameters.get(key));
		}
		return pm;
	}
}
