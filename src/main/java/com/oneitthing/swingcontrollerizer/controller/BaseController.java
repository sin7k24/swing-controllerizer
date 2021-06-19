package com.oneitthing.swingcontrollerizer.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.action.Action;
import com.oneitthing.swingcontrollerizer.action.BaseAction;
import com.oneitthing.swingcontrollerizer.common.exception.CoreExceptionIF;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.listener.ModelProcessListener;
import com.oneitthing.swingcontrollerizer.model.BaseModel;
import com.oneitthing.swingcontrollerizer.model.DefaultModel;
import com.oneitthing.swingcontrollerizer.model.Model;
import com.oneitthing.swingcontrollerizer.util.MessageDialogUtil;

/**
 * <p>[概 要] </p>
 * アクションとモデルを制御する基底コントローラクラスです。
 *
 * <p>[詳 細] </p>
 * イベント発生時、BaseControllerの{@link Controller#invoke(Class, ParameterMapping)}
 * 実装は、以下の処理フロー形成を行います。
 *
 * <p>
 * 	<table width="100%" border="1" style="border-collapse:collapse;">
 * 		<tr>
 * 			<td>実行順序</td>
 * 			<td>BaseAction</td>
 * 			<td>BaseController</td>
 * 			<td>BaseModel</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">1</td>
 * 			<td>　</td>
 * 			<td>{@link #invoke(Class, ParameterMapping)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">2</td>
 * 			<td>　</td>
 * 			<td>　{@link #runAction(Class, ParameterMapping)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">3</td>
 * 			<td>{@link BaseAction#run(ParameterMapping) run(ParameterMapping)}</td>
 * 			<td>　</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">4</td>
 * 			<td>　</td>
 * 			<td>　{@link #runModels(List, ParameterMapping, int, ModelProcessEvent)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">4´</td>
 * 			<td>　</td>
 * 			<td>　{@link #runModelsAndNoWait(List, ParameterMapping)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">5</td>
 * 			<td>　</td>
 * 			<td>　</td>
 * 			<td>{@link BaseModel#run() run()}</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">6</td>
 * 			<td>　</td>
 * 			<td>　モデル処理管理リスナ（成功）</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">7</td>
 * 			<td>{@link BaseAction#successForward(int, Model, Object) successForward(int, Model, Object)}</td>
 * 			<td>　</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">6´</td>
 * 			<td>　</td>
 * 			<td>　モデル処理管理リスナ（失敗）</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">7´</td>
 * 			<td>{@link BaseAction#failureForward(int, Model, Exception) failureForward(int, Model, Exception)}</td>
 * 			<td>　</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">8</td>
 * 			<td>　</td>
 * 			<td>　モデル処理管理リスナ（完了）</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">9</td>
 * 			<td>{@link BaseAction#complete() complete()}</td>
 * 			<td>　</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">10</td>
 * 			<td>　</td>
 * 			<td>　{@link #trap(Throwable)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 		<tr>
 * 			<td align="center">11</td>
 * 			<td>　</td>
 * 			<td>　{@link #invokeFinalize(ParameterMapping)}</td>
 * 			<td>　</td>
 * 		</tr>
 * 	</table>
 * この過程で呼ばれるアクション（run）とモデル（run）の中では別途処理フローが作られ、
 * 機能実装者にフックポイントを提供します。
 *
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * ・典型的な具象コントローラ（BaseController継承クラス）
 * <pre class="samplecode">
 *	package demo.controller;
 *
 *	import java.awt.event.ActionListener;
 *	import java.awt.event.FocusListener;
 *	import java.awt.event.ItemListener;
 *	import java.awt.event.WindowListener;
 *	import java.util.Hashtable;
 *
 *	import javax.naming.Context;
 *	import javax.swing.event.InternalFrameListener;
 *	import javax.swing.event.PopupMenuListener;
 *
 *	import com.oneitthing.swingcontrollerizer.controller.BaseController;
 *	import com.oneitthing.swingcontrollerizer.controller.ClientConfig;
 *	import com.oneitthing.swingcontrollerizer.controller.EventBinder;
 *	import com.oneitthing.swingcontrollerizer.model.DatabaseCore;
 *	import com.oneitthing.swingcontrollerizer.model.HTTPRequestCore;
 *	import demo.chat.action.ChatFrameCloseAction;
 *	import demo.chat.action.EnterChannelAction;
 *	import demo.chat.action.RemarkSendAction;
 *	import demo.communication.action.AllCommunicateAction;
 *	import demo.communication.action.ClearResultAction;
 *	import demo.communication.action.EjbCommunicateAction;
 *	import demo.communication.action.HttpCommunicateAction;
 *	import demo.communication.action.SoapCommunicateAction;
 *	import demo.componentsearch.action.ComponentLocateAction;
 *	import demo.componentsearch.action.SearchAction;
 *	import demo.componentsearch.action.ShowSourceAction;
 *	import demo.componentsearch.action.WindowNamesFetchAction;
 *	import demo.correlation.action.CorrelationImageFrameInitAction;
 *	import demo.dbaccess.action.UsersFetchAction;
 *	import demo.form.action.AddressFocusLostAction;
 *	import demo.form.action.EmailFocusLostAction;
 *	import demo.form.action.FullNameFocusLostAction;
 *	import demo.form.action.InputFormInitAction;
 *	import demo.form.action.OpenPostalSearchAction;
 *	import demo.form.action.PasswordFocusLostAction;
 *	import demo.form.action.PostalFirstFocusLostAction;
 *	import demo.form.action.PostalLastFocusLostAction;
 *	import demo.form.action.UserIdFocusLostAction;
 *	import demo.form.action.UserRegistAction;
 *	import demo.functionlauncher.action.OpenChatAction;
 *	import demo.functionlauncher.action.OpenCommunicationAction;
 *	import demo.functionlauncher.action.OpenComponentSearchAction;
 *	import demo.functionlauncher.action.OpenCorrelationAction;
 *	import demo.functionlauncher.action.OpenDbAccessAction;
 *	import demo.functionlauncher.action.OpenInputFormAction;
 *	import demo.functionlauncher.action.OpenJFreeChartAction;
 *	import demo.functionlauncher.action.OpenMapViewerAction;
 *	import demo.functionlauncher.action.OpenWebServiceAction;
 *	import demo.jfreechart.action.OpenLineChartAction;
 *	import demo.jfreechart.action.OpenPieChartAction;
 *	import demo.jfreechart.action.PieChartInitAction;
 *	import demo.jfreechart.action.TimeSeriesChartCloseAction;
 *	import demo.jfreechart.action.TimeSeriesChartInitAction;
 *	import demo.login.action.LoginAction;
 *	import demo.postal.action.DecideAction;
 *	import demo.postal.action.NextPageAction;
 *	import demo.postal.action.NumPerPageChangeAction;
 *	import demo.postal.action.PostalSearchFacadeAction;
 *	import demo.postal.action.PrevPageAction;
 *	import demo.postal.action.WardsFetchAction;
 *	import demo.webservice.action.MtomSendAction;
 *
 *	public class DemoController extends BaseController {
 *
 *		&#064;Override
 *		protected void initialize(ClientConfig config) {
 *			// デフォルトJMS接続環境を設定
 *			Hashtable<String, String> jmsEnvironment = new Hashtable<String, String>();
 *			jmsEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
 *			jmsEnvironment.put(Context.PROVIDER_URL, "localhost:1099");
 *			jmsEnvironment.put("java.naming.rmi.security.manager", "yes");
 *			jmsEnvironment.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");
 *			config.setDefaultJmsEnvironment(jmsEnvironment);
 *
 *			// デフォルトEJB接続環境を設定
 *			Hashtable<String, String> ejbEnvironment = new Hashtable<String, String>();
 *			ejbEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.NamingContextFactory");
 *			ejbEnvironment.put(Context.PROVIDER_URL, "jnp://localhost:1099");
 *			config.setDefaultEjbEnvironment(ejbEnvironment);
 *
 *			// デフォルトDatabase接続環境を設定
 *			Hashtable<String, String> dbEnvironment = new Hashtable<String, String>();
 *			dbEnvironment.put(DatabaseCore.DB_DRIVER_FQCN, "org.postgresql.Driver");
 *			dbEnvironment.put(DatabaseCore.DB_URL, "jdbc:postgresql://localhost:5432/RFD");
 *			dbEnvironment.put(DatabaseCore.DB_USER, "nakanishi");
 *			dbEnvironment.put(DatabaseCore.DB_PASSWORD, "shingon");
 *			dbEnvironment.put(DatabaseCore.DB_AUTO_COMMIT, "true");
 *			config.setDefaultDatabaseEnvironment(dbEnvironment);
 *
 *			Hashtable<String, String> httpEnvironment = new Hashtable<String, String>();
 *			httpEnvironment.put(HTTPRequestCore.HTTP_URL_PREFIX, "http://localhost:8080/RFDforSwingWeb/");
 *			config.setDefaultHttpEnvironment(httpEnvironment);
 *		}
 *
 *		&#064;Override
 *		protected void bind(EventBinder eventBinder) {
 *			// ログイン画面アクション
 *			eventBinder.addEventBinding("loginFrame.jbLogin", ActionListener.class, "actionPerformed", LoginAction.class);
 *
 *			// 起動画面アクション
 *			eventBinder.addEventBinding("functionlauncher.jbOpenChat", ActionListener.class, "actionPerformed", OpenChatAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenComponentSearch", ActionListener.class, "actionPerformed", OpenComponentSearchAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenDesktop", ActionListener.class, "actionPerformed", OpenJFreeChartAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenInputForm", ActionListener.class, "actionPerformed", OpenInputFormAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenCommunication", ActionListener.class, "actionPerformed", OpenCommunicationAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenDbAccess", ActionListener.class, "actionPerformed", OpenDbAccessAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenCorrelation", ActionListener.class, "actionPerformed", OpenCorrelationAction.class);
 *			eventBinder.addEventBinding("functionLauncher.jbOpenMapViewer", ActionListener.class, "actionPerformed", OpenMapViewerAction.class);
 *			eventBinder.addEventBinding("functionlauncher.jbOpenWebService", ActionListener.class, "actionPerformed", OpenWebServiceAction.class);
 *
 *
 *			// チャットデモ画面アクション
 *			eventBinder.addEventBinding("chatFrame.jbEnterChannel", ActionListener.class, "actionPerformed", EnterChannelAction.class);
 *			eventBinder.addEventBinding("chatFrame.jbRemarkSend", ActionListener.class, "actionPerformed", RemarkSendAction.class);
 *			eventBinder.addEventBinding("chatFrame", WindowListener.class, "windowClosing", ChatFrameCloseAction.class);
 *
 *			// コンポーネント検索画面アクション
 *			eventBinder.addEventBinding("componentSearchFrame.jbSearch", ActionListener.class, "actionPerformed", SearchAction.class);
 *			eventBinder.addEventBinding("componentSearchFrame.jcbWindowName", PopupMenuListener.class, "popupMenuWillBecomeVisible", WindowNamesFetchAction.class);
 *			eventBinder.addEventBinding("componentSearchFrame.jbLocate", ActionListener.class, "actionPerformed", ComponentLocateAction.class);
 *			eventBinder.addEventBinding("componentSearchFrame.jbShowSource", ActionListener.class, "actionPerformed", ShowSourceAction.class);
 *
 *			// JFreeChartデモ画面アクション
 *			eventBinder.addEventBinding("desktopFrame.jmiPieChart", ActionListener.class, "actionPerformed", OpenPieChartAction.class);
 *			eventBinder.addEventBinding("desktopFrame.jmiLineChart", ActionListener.class, "actionPerformed", OpenLineChartAction.class);
 *			eventBinder.addEventBinding("jfreechart.jifPieChart", InternalFrameListener.class, "internalFrameOpened", PieChartInitAction.class);
 *			eventBinder.addEventBinding("jfreechart.jifTimeSeriesChart", InternalFrameListener.class, "internalFrameOpened", TimeSeriesChartInitAction.class);
 *			eventBinder.addEventBinding("jfreechart.jifTimeSeriesChart", InternalFrameListener.class, "internalFrameClosing", TimeSeriesChartCloseAction.class);
 *
 *			// 入力フォームデモ画面アクション
 *			eventBinder.addEventBinding("inputFormFrame", WindowListener.class, "windowOpened", InputFormInitAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jbOpenPostalSearch", ActionListener.class, "actionPerformed", OpenPostalSearchAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfFullName", FocusListener.class, "focusLost", FullNameFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfPostalFirst", FocusListener.class, "focusLost", PostalFirstFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfPostalLast", FocusListener.class, "focusLost", PostalLastFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfAddress", FocusListener.class, "focusLost", AddressFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfEmail", FocusListener.class, "focusLost", EmailFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jtfUserId", FocusListener.class, "focusLost", UserIdFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jpwPassword", FocusListener.class, "focusLost", PasswordFocusLostAction.class);
 *			eventBinder.addEventBinding("inputFormFrame.jbRegist", ActionListener.class, "actionPerformed", UserRegistAction.class);
 *
 *			// 郵便番号検索画面アクション
 *			eventBinder.addEventBinding("postalSearchDialog", WindowListener.class, "windowOpened", WardsFetchAction.class);
 *			eventBinder.addEventBinding("postalSearchDialog.jbSearch", ActionListener.class, "actionPerformed", PostalSearchFacadeAction.class);
 *			eventBinder.addEventBinding("postalSearchDialog.jbDecide", ActionListener.class, "actionPerformed", DecideAction.class);
 *			eventBinder.addEventBinding("postalSearchDialog.jcbNumPerPage", ItemListener.class, "itemStateChanged", NumPerPageChangeAction.class);
 *			eventBinder.addEventBinding("postalSearchDialog.jbNextPage", ActionListener.class, "actionPerformed", NextPageAction.class);
 *			eventBinder.addEventBinding("postalSearchDialog.jbPrevPage", ActionListener.class, "actionPerformed", PrevPageAction.class);
 *
 *			// 通信デモ画面アクション
 *			eventBinder.addEventBinding("communicationFrame.jbEjb", ActionListener.class, "actionPerformed", EjbCommunicateAction.class);
 *			eventBinder.addEventBinding("communicationFrame.jbHttp", ActionListener.class, "actionPerformed", HttpCommunicateAction.class);
 *			eventBinder.addEventBinding("communicationFrame.jbSoap", ActionListener.class, "actionPerformed", SoapCommunicateAction.class);
 *			eventBinder.addEventBinding("communicationFrame.jbAll", ActionListener.class, "actionPerformed", AllCommunicateAction.class);
 *			eventBinder.addEventBinding("communicationFrame.jbClear", ActionListener.class, "actionPerformed", ClearResultAction.class);
 *
 *			eventBinder.addEventBinding("webServiceFrame.jbMtomSend", ActionListener.class, "actionPerformed", MtomSendAction.class);
 *
 *			// データベース直接アクセス画面アクション
 *			eventBinder.addEventBinding("dbaccessFrame.jbLoad", ActionListener.class, "actionPerformed", UsersFetchAction.class);
 *
 *			// コンポーネントイメージ相関画面アクション
 *			eventBinder.addEventBinding("correlationImageFrame", WindowListener.class, "windowOpened", CorrelationImageFrameInitAction.class);
 *
 *
 *		}
 *	}
 * </pre>
 *
 * ・具象コントローラの登録方法
 * <pre class="samplecode">
 *	package demo;
 *
 *	import javax.swing.SwingUtilities;
 *	import javax.swing.UIManager;
 *
 *	import demo.controller.DemoController;
 *	import demo.login.LoginFrame;
 *
 *	public class Main {
 *		public static void main(String[] args) {
 *			try{
 *				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 *			}catch(Exception e) {
 *				e.printStackTrace();
 *			}
 *
 *	        SwingUtilities.invokeLater(new Runnable() {
 *	            public void run() {
 *	        		DemoController controller = new DemoController();
 *	        		LoginFrame loginFrame = new LoginFrame();
 *	        		loginFrame.setVisible(true);
 *	            }
 *	        });
 *		}
 *	}
 * </pre>
 *
 *
 */
public class BaseController extends AbstractController {

	/**
	 * <p>[概 要]</p>
	 * コントローラの主幹メソッドです。
	 *
	 * <p>[詳 細]</p>
	 * コントローラ処理フローの幹を形成します。
	 * このメソッドのtryスコープで以下が行われます。
	 * 	<ol>
	 * 		<li>{@link #createParameterMapping()} MVCレイヤを巡回するParameterMappingオブジェクトの生成</li>
	 * 		<li>{@link #runAction(Class, ParameterMapping)} アクションの実行</li>
	 * 		<li>{@link #runModels(List, ParameterMapping, int, ModelProcessEvent)} or <br>
	 *          {@link #runModelsAndNoWait(List, ParameterMapping)} アクションで予約されたモデル群の実行
	 *      </li>
	 * 	</ol>
	 *
	 * 上記の処理中に例外が発生した場合、trapメソッドがテンプレートコールされます。
	 * 最終的にfinallyスコープに入るとhanderFinalizeメソッドがテンプレートコールされます。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param actionClass
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	@Override
	public Object invoke(Class<? extends Action> actionClass, ParameterMapping parameterMapping) {
	    System.out.println("invoke");
		Object ret = null;

		try {

			// Action実行
			parameterMapping = runAction(actionClass, parameterMapping);
			if (parameterMapping == null) {
				return null;
			}

			// Actionで予約されたモデルクラス群を取得
			List<Class<? extends Model>> modelClasses = parameterMapping.getModelClasses();
			if (modelClasses.size() == 0) {
				modelClasses.add(DefaultModel.class);
			}
			parameterMapping.setModelReservedNum(modelClasses.size());
			parameterMapping.setModelFinishedNum(0);
			// Model群実行
			if (parameterMapping.isRunModelsAndNoWait()) {
				// 待たずに次Model実行
				runModelsAndNoWait(modelClasses, parameterMapping);
			} else {
				// Modelの戻り値を待って次Model実行
				runModels(modelClasses, parameterMapping, 0, null);
			}
		} catch (Throwable e) {
			// 共通例外処理
			trap(e);
		} finally {
			// 共通終了処理
			invokeFinalize(parameterMapping);
		}
		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * イベントに対応するアクションを実行します。
	 *
	 * <p>[詳 細] </p>
	 * 引数で指定されたアクションクラス型をインスタンス化、
	 * 実行（{@link BaseAction#run(ParameterMapping)}）します。<br>
	 * execute実行後、結果戻り値であるParameterMappingを返却します。
	 *
	 * <p>[備 考] </p>
	 * この結果がnullだった場合はコントローラの以降の処理は中止されます。
	 * {@link BaseAction#run(ParameterMapping)}がnullを返却するのは、
	 * prepareがfalseを返却、もしくはvalidate結果がエラーだった場合があります。
	 *
	 * @param actionClass 実行するアクションクラスの型
	 * @param parameterMapping MVCを巡回するパラメータマッピング
	 * @return アクション実行後のParameterMappingオブジェクト
	 * @throws Exception
	 */
	public ParameterMapping runAction(Class<? extends Action> actionClass, ParameterMapping parameterMapping) throws Exception {
		AbstractAction action = (AbstractAction) actionClass.newInstance();;
		action.setController(this);
		parameterMapping = action.run(parameterMapping);

		if (parameterMapping == null) {
			return null;
		}

		parameterMapping.setActionInstance(action);

		return parameterMapping;
	}


	/**
	 * <p>[概 要] </p>
	 * アクションで予約されたモデル郡をインスタンス化して連続実行します。
	 *
	 * <p>[詳 細] </p>
	 * {@link BaseAction#reserveModels(List)}で予約されたBaseModel実装モデル群を実行します。<br>
	 * 登録モデルが複数有る場合、前回モデルの実行終了を待ってから次回モデルが実行されます。<p>
	 *
	 * モデル実行直前に{@link BaseAction#nextModel(int, ModelProcessEvent, Model)}がコールバックされます。<br>
	 * 実行モデルへのパラメータ設定を上記メソッドで行うことが出来ます。<p>
	 *
	 * モデル実行後、成功時は{@link BaseAction#successForward(int, Model, Object)}が、
	 * 失敗時は{@link BaseAction#failureForward(int, Model, Exception)}がコールバックされます。<br>
	 * モデルの実行結果は上記メソッドで取得することが出来ます。
	 *
	 * <p>[備 考] </p>
	 * {@link BaseAction#isRunModelsAndNoWait()}
	 * がfalseの場合、同期モードで実行されます。<br>
	 * 同メソッドはデフォルトでfalseを返却します。
	 *
	 * @param modelClasses {@link BaseAction#reserveModels(List)} で予約されたモデルクラス群
	 * @param parameterMapping MVC各レイヤを伝播するパラメータ
	 * @param executeIndex モデル実行順序インデックス
	 * @param modelProcessEvent 直前に実行したモデルの処理結果イベントオブジェクト
	 * @throws Exception モデル内で発生し得る例外
	 */
	public void runModels(final List<Class<? extends Model>> modelClasses,
			ParameterMapping parameterMapping, int executeIndex,
			ModelProcessEvent modelProcessEvent) throws Exception {

		// コールバック用に事前実行されたActionインスタンスを取得
		BaseAction action = (BaseAction) parameterMapping.getActionInstance();

		if (modelClasses.size() > 0) {
			Class<? extends Model> modelClass = modelClasses.remove(0);
			// モデルクラスからインスタンス生成
			BaseModel model = (BaseModel) modelClass.newInstance();

			// パラメータマッピングをモデルに設定
			model.setParameterMapping(parameterMapping);
			// コントローラインスタンスをモデルに設定
			model.setController(this);
			// モデル実行順序をモデルに設定
			model.setExecuteIndex(executeIndex);
			// モデル処理監視リスナをモデルに追加
			model.addModelProcessListener(new ModelProcessListener() {
				// モデル処理成功
				public void modelSuccess(ModelProcessEvent evt) {
					BaseModel model = (BaseModel) evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction) parameterMapping.getActionInstance();
					int executeIndex = model.getExecuteIndex();
					try {
						// BaseAction#successForwardをコールバック。
						// モデル結果をActionレイヤで取得可能にする。
						action.successForward(executeIndex, model, evt.getResult());

						if (model.getSuccessCount() == 1) {
							runModels(modelClasses, parameterMapping, ++executeIndex, evt);
						}
					} catch (Exception e) {
						trap(e);
					}
				}

				// モデル処理失敗
				public void modelFailure(ModelProcessEvent evt) {
					BaseModel model = (BaseModel) evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction)parameterMapping.getActionInstance();
					try {
						model.done();
						modelsDone(parameterMapping);
						// BaseAction#failureForwardをコールバック。
						// モデル失敗例外をActionレイヤで取得可能にする。
						Exception e = action.failureForward(model.getExecuteIndex(), model, evt.getException());
						// BaseAction#failureForwardがnullを返却した場合、
						// コントローラによる例外処理は行わない。
						if (e != null) {
							trap(e);
						}
					} catch (Exception e) {
						trap(e);
					}
				}

				// モデル終了処理
				public void modelFinished(ModelProcessEvent evt) {
					BaseModel model = (BaseModel)evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction)parameterMapping.getActionInstance();
					try {
						int finishedNum = parameterMapping.getModelFinishedNum();
						parameterMapping.setModelFinishedNum(finishedNum + 1);
						if (parameterMapping.getModelReservedNum() == parameterMapping.getModelFinishedNum()) {
							// モデル終了処理テンプレートコール
							model.done();
							modelsDone(parameterMapping);
							// アクションの全モデル終了通知メソッドをテンプレートコール
							action.complete(parameterMapping);
							// モデルの完了カウントをクリア
							parameterMapping.setModelFinishedNum(0);

						}
					} catch (Exception e) {
						trap(e);
					}
				}
			});
			// モデル初期化
			model.init();
			// モデル実行直前にActionのメソッドをコールバック。モデルインスタンス設定フックタイミングを作る。
			boolean isProceed = action.nextModel(executeIndex, modelProcessEvent, model);
			if (!isProceed) {
				// モデルの前処理でfalseが返されてしまうとイベントが発行されないため、
				// 処理終了前に終了イベントを発行
				model.fireModelFinished(new ModelProcessEvent(model));
				return;
			}
			if (model.isSkip()) {
				// モデルがSkipされてしまうとイベントが発行されないため、
				// 次のモデル実行前に終了イベントを発行
				model.fireModelFinished(new ModelProcessEvent(model));
				// 次のモデルへ
				runModels(modelClasses, parameterMapping, ++executeIndex, null);
			} else {
				// nextModelでsetSkip(true)されていなければモデル実行
				ExecutorService executor = Executors.newCachedThreadPool();
				Future<Object> future = executor.submit(model);
				if(!model.isAsync()) {
					try{
						future.get();
					}catch(InterruptedException e) {
						if(!parameterMapping.isAllowInteruptedExceptionOnSyncModel()) {
							throw e;
						}
					}
				}
			}
		} else {
			action.nextModel(executeIndex, modelProcessEvent, null);
		}
	}

	/**
	 * <p>[概 要] </p>
	 * アクションで予約されたモデル郡をインスタンス化して連続実行します。
	 *
	 * <p>[詳 細] </p>
	 * {@link BaseAction#reserveModels(List)}で予約されたBaseModel実装モデル群を実行します。<br>
	 * 登録モデルが複数有る場合、前回モデルの実行終了を待たずに次回モデルが実行されます。<br>
	 *
	 * モデル実行直前に{@link BaseAction#nextModel(int, ModelProcessEvent, Model)}がコールバックされます。<br>
	 * 実行モデルへのパラメータ設定を上記メソッドで行うことが出来ます。<br>
	 * 非同期モードでモデル実行した場合は前回モデルの結果を待たずに次回モデルを実行する為、
	 * 第二引数prev:ModelProcessEventが常時nullになります。<br>
	 * 前回モデルの結果を判断して、次回モデルのパラメータ設定をすることは出来ません。<p>
	 *
	 * モデル実行後、成功時は{@link BaseAction#successForward(int, Model, Object)}が、
	 * 失敗時は{@link BaseAction#failureForward(int, Model, Exception)}がコールバックされます。<br>
	 * モデルの実行結果は上記メソッドで取得することが出来ます。
	 *
	 * <p>[備 考] </p>
	 * {@link BaseAction#isRunModelsAndNoWait()}
	 * がtrueの場合、非同期モードで実行されます。<br>
	 * 非同期モードで実行する場合は、{@link BaseAction#isRunModelsAndNoWait()}を
	 * オーバーライドしてtrueを返却して下さい。<br>
	 *
	 * <pre class="samplecode">
	 *	  &#064;Override
	 *    protected boolean isRunModelsAndNoWait() {
	 *       return true;
	 *    }
	 * </pre>
	 *
	 * @param modelClasses {@link BaseAction#reserveModels(List)} で予約されたモデルクラス群
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 * @throws Exception モデル内で発生し得る例外
	 */
	public void runModelsAndNoWait(List<Class<? extends Model>> modelClasses,
			ParameterMapping parameterMapping) throws Exception {

		// コールバック用に事前実行されたActionインスタンスを取得
		BaseAction action = (BaseAction) parameterMapping.getActionInstance();

		// コントローラが実行するモデルの実行順位
		int executeIndex = 0;
		// 全モデルクラス群を実行

		for (; executeIndex < modelClasses.size(); executeIndex++) {
			Class<? extends Model> modelClass = modelClasses.get(executeIndex);
			// モデルクラスからインスタンス生成
			BaseModel model = (BaseModel) modelClass.newInstance();
			// パラメータマッピングをモデルに設定
			model.setParameterMapping(parameterMapping);
			// コントローラインスタンスをモデルに設定
			model.setController(this);
			// モデル実行順序をモデルに設定
			model.setExecuteIndex(executeIndex);
			// モデル処理監視リスナをモデルに追加
			model.addModelProcessListener(new ModelProcessListener() {
				// モデル処理成功
				public void modelSuccess(final ModelProcessEvent evt) {
					BaseModel model = (BaseModel) evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction)parameterMapping.getActionInstance();
					try {
						// BaseAction#successForwardをコールバック。
						// モデル結果をActionレイヤで取得可能にする。
						action.successForward(model.getExecuteIndex(), model, evt.getResult());
					} catch (Exception e) {
						trap(e);
					}
				}

				// モデル処理失敗
				public void modelFailure(ModelProcessEvent evt) {
					BaseModel model = (BaseModel) evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction)parameterMapping.getActionInstance();
					try {
						model.done();
						modelsDone(parameterMapping);

						// BaseAction#failureForwardをコールバック。
						// モデル失敗例外をActionレイヤで取得可能にする。
						Exception e = action.failureForward(model
								.getExecuteIndex(), model, evt.getException());
						// BaseAction#failureForwardがnullを返却した場合、
						// コントローラによる例外処理は行わない。
						if (e != null) {
							trap(e);
						}
					} catch (Exception e) {
						trap(e);
					}
				}

				// モデル終了処理
				public void modelFinished(ModelProcessEvent evt) {
					BaseModel model = (BaseModel)evt.getSource();
					ParameterMapping parameterMapping = model.getParameterMapping();
					BaseAction action = (BaseAction)parameterMapping.getActionInstance();
					try {
						int finishedNum = parameterMapping.getModelFinishedNum();
						parameterMapping.setModelFinishedNum(finishedNum + 1);
						if (parameterMapping.getModelReservedNum() == parameterMapping.getModelFinishedNum()) {
							// モデル終了処理をテンプレートコール
							model.done();
							modelsDone(parameterMapping);
							// アクションの全モデル終了通知メソッドをテンプレートコール
							action.complete(parameterMapping);
							// モデルの完了カウントをクリア
							parameterMapping.setModelFinishedNum(0);
						}
					} catch (Exception e) {
						trap(e);
					}
				}
			});
			// モデル初期化
			model.init();
			// モデル実行直前にActionのメソッドをコールバック。モデルインスタンス設定フックタイミングを作る。
			boolean isProceed = action.nextModel(executeIndex, null, model);
			if (!isProceed) {
				// モデルの前処理でfalseが返されてしまうとイベントが発行されないため、
				// 処理終了前に終了イベントを発行
				model.fireModelFinished(new ModelProcessEvent(model));
				return;
			}
			if (model.isSkip()) {
				// モデルがSkipされてしまうとイベントが発行されないため、
				// 次のモデル実行前に終了イベントを発行
				model.fireModelFinished(new ModelProcessEvent(model));
			} else {
				// nextModelでsetSkip(true)されていなければモデル実行
				ExecutorService executor = Executors.newCachedThreadPool();
				Future<Object> future = executor.submit(model);
				if(!model.isAsync()) {
					try{
						future.get();
					}catch(InterruptedException e) {
						if(!parameterMapping.isAllowInteruptedExceptionOnSyncModel()) {
							throw e;
						}
					}
				}
			}
		}
		action.nextModel(executeIndex, null, null);
	}

	/**
	 * <p>[概 要] </p>
	 * MVC各レイヤで発生した例外が最終的にハンドリングされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * {@link ClientConfig#isShowErrorDialogOnExceptionTrap()}
	 * がtrueの場合、キャッチした例外をダイアログ表示します。
	 *
	 *
	 * <p>[備 考] </p>
	 * 自動ダイアログ表示を行いたくない場合、
	 * {@link ClientConfig#isShowErrorDialogOnExceptionTrap()}
	 * がfalse返却するよう設定してください。
	 *
	 * <pre class="samplecode">
	 * 	public class DemoController extends BaseController {
	 *
	 *		&#064;Override
	 *		protected void initialize(ClientConfig config) {
	 *			config.setShowErrorDialogOnExceptionTrap(false);
	 *		}
	 * </pre>
	 *
	 * @param e MVC各レイヤで発生したスロー可能オブジェクト
	 */
	protected void trap(Throwable e){


		if (e instanceof CoreExceptionIF) {
			// 想定例外時はエラーコードとメッセージでダイアログを出力
			if (((CoreExceptionIF) e).isNotifyToUser()) {
				String id = ((CoreExceptionIF) e).getId();
				String message = ((CoreExceptionIF) e).getMessage();
				if(getClientConfig().isShowErrorDialogOnExceptionTrap()) {
					MessageDialogUtil.showMessageDialog(null, message, id, MessageDialogUtil.ERROR_MESSAGE);
				}
			}
		} else {
			// 予期せぬエラー時はコンフィグ設定されたタイトルとメッセージでダイアログを出力
			if(getClientConfig().isShowErrorDialogOnExceptionTrap()) {
				MessageDialogUtil.showMessageDialog(null,
						getClientConfig().getUnexpectedErrorDialogMessage(),
						getClientConfig().getUnexpectedErrorDialogTitle(),
						MessageDialogUtil.ERROR_MESSAGE);
			}
		}

		if(getClientConfig().isPrintStackTraceOnExceptionTrap()) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 各ユーザ定義イベントハンドリングの最後にテンプレートコールされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * このメソッドを具象コントローラでオーバーライドすると、全イベントアクションの
	 * 共通最終処理を実装出来ます。
	 *
	 * @param mapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	protected void invokeFinalize(ParameterMapping mapping) {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param mapping
	 */
	protected void modelsDone(ParameterMapping mapping) throws Exception {
		BaseAction action = (BaseAction)mapping.getActionInstance();
		action.done(mapping);
	}
}
