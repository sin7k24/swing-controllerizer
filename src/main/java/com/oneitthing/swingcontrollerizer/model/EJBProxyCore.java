package com.oneitthing.swingcontrollerizer.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.util.TypeMaintainList;

/**
 * <p>[概 要] </p>
 * サーバとEJB通信を行う機能モデルクラスです。
 *
 * <p>[詳 細] </p>
 * EJBリモートオブジェクトを取得してRPCを行います。
 * <p>
 *
 * 必須設定メソッド
 * <ul>
 *   <li>{@link #setLookupName(String)} : リモートEJBオブジェクトのJNDI名設定</li>
 *   <li>{@link #setMethodName(String)} : 呼び出すメソッド名の設定</li>
 * </ul>
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * EJBを使用してログイン認証を行う。
 * <pre class="samplecode">
 * 	package demo.login.action;
 *
 *	import java.util.EventListener;
 *	import java.util.List;
 *
 *	import javax.swing.JPasswordField;
 *	import javax.swing.JTextField;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.EJBProxyCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *	public class LoginAction extends BaseAction {
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			models.add(EJBProxyCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				// 使用するEJBオブジェクトのJNDI名を設定。
 *				((EJBProxyCore)next).setLookupName("SwingControllerizerFunctionalDemoForSwingServer/LoginBean/remote");
 *				// 呼び出すメソッド名を設定。
 *				((EJBProxyCore)next).setMethodName("login");
 *				// パラメータ設定
 *				((EJBProxyCore)next).addParameter(getComponentValueAsString("loginFrame.jtfUserId"));
 *				((EJBProxyCore)next).addParameter(String.valueOf((char[])getComponentValue("loginFrame.jpwPassword")));
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result)	throws Exception {
 *			showMessageDialog("ログインしました", "成功", MessageDialogUtil.INFORMATION_MESSAGE, MessageDialogUtil.DEFAULT_OPTION);			}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			showMessageDialog("ログインに失敗しました", "エラー", MessageDialogUtil.ERROR_MESSAGE, MessageDialogUtil.DEFAULT_OPTION);
 *			return null;
 *		}
 *	}
 * </pre>
 *


 *

 */
public class EJBProxyCore extends BaseModel {

	/** 接続先環境設定プロパティです。 */
	private Hashtable<String, String> environment;

	/** 使用するEJBコンポーネントのJNDI名です。 */
	private String lookupName;

	/** 呼び出すEJBメソッド名です。 */
	private String methodName;

	/** EJBメソッドパラメータです。 */
	private TypeMaintainList parameters;

	/**
	 * <p>[概 要] </p>
	 * 接続先環境設定プロパティを返却します。
	 *
	 * <p>[詳 細] </p>
	 * environmentフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 接続先環境設定プロパティ
	 */
	public Hashtable<String, String> getEnvironment() {
		if(environment == null) {
			environment = getController().getClientConfig().getDefaultEjbEnvironment();
		}
		return environment;
	}

	/**
	 * <p>[概 要] </p>
	 * 接続先環境設定プロパティを設定します。
	 *
	 * <p>[詳 細] </p>
	 * environmentフィールドに引数environmentを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param environment 接続先環境設定プロパティ
	 */
	public void setEnvironment(Hashtable<String, String> environment) {
		this.environment = environment;
	}

	/**
	 * <p>[概 要] </p>
	 * 使用するEJBコンポーネントのJNDI名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * lookupNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 使用するEJBコンポーネントのJNDI名
	 */
	public String getLookupName() {
		return lookupName;
	}

	/**
	 * <p>[概 要] </p>
	 * 使用するEJBコンポーネントのJNDI名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * lookupNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param lookupName 使用するEJBコンポーネントのJNDI名
	 */
	public void setLookupName(String lookupName) {
		this.lookupName = lookupName;
	}

	/**
	 * <p>[概 要] </p>
	 * 呼び出すEJBメソッド名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * methodNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 呼び出すEJBメソッド名
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * <p>[概 要] </p>
	 * 呼び出すEJBメソッド名を設定します。
	 *
	 * <p>[詳 細] </p>
	 * methodNameフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param methodName 呼び出すEJBメソッド名
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを返却します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return EJBメソッドパラメータ
	 */
	public TypeMaintainList getParameters() {
		return parameters;
	}

	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを設定します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameters EJBメソッドパラメータ
	 */
	public void setParameters(TypeMaintainList parameters) {
		this.parameters = parameters;
	}

	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(char parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(short parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(int parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(long parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(float parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(double parameter) {
		this.parameters.add(parameter);
	}
	/**
	 * <p>[概 要] </p>
	 * EJBメソッドパラメータを追加します。
	 *
	 * <p>[詳 細] </p>
	 * parametersフィールドに引数parameterを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter EJBメソッドパラメータ
	 */
	public void addParameter(boolean parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * フィールドの初期化を行います。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public EJBProxyCore() {
		this.parameters = new TypeMaintainList();
	}

	/**
	 * <p>[概 要] </p>
	 * EJBオブジェクトをルックアップしてサーバと通信を行います。
	 *
	 * <p>[詳 細] </p>
	 * 以下の処理を行います。
	 *
	 * <ol>
	 *   <li>environmentフィールド値を元にInitialContextを生成</li>
	 *   <li>lookupNameのルックアップ</li>
	 *   <li>呼び出すメソッドのリフレクション</li>
	 *   <li>EJBメソッド実行</li>
	 * </ol>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void mainproc() throws NamingException,
										SecurityException,
										NoSuchMethodException,
										IllegalArgumentException,
										IllegalAccessException,
										InvocationTargetException
	{
		InitialContext ctx = new InitialContext(getEnvironment());
		Object ejb = ctx.lookup(getLookupName());

		Class[] parameterTypes = getParameters().toTypeArray();
		Method method = ejb.getClass().getMethod(getMethodName(), parameterTypes);
		Object result = method.invoke(ejb, getParameters().toValueArray());

		setResult(result);
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理成功イベント、モデル処理終了イベントを発行します。
	 *
	 * <p>[詳 細] </p>
	 * fireModelSuccess、fireModelFinishedメソッドをコールします。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void postproc() throws Exception {
		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(getResult());
		fireModelSuccess(successEvent);

		fireModelFinished(new ModelProcessEvent(this));
	}
}

