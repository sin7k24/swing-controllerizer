package com.oneitthing.swingcontrollerizer.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import com.oneitthing.swingcontrollerizer.common.exception.CoreLogicException;
import com.oneitthing.swingcontrollerizer.controller.AbstractController;
import com.oneitthing.swingcontrollerizer.controller.BaseController;
import com.oneitthing.swingcontrollerizer.controller.ClientConfig;
import com.oneitthing.swingcontrollerizer.controller.EventBinder;
import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
import com.oneitthing.swingcontrollerizer.listener.MessageDialogListener;
import com.oneitthing.swingcontrollerizer.manager.WindowManager;
import com.oneitthing.swingcontrollerizer.parser.ComponentValue;
import com.oneitthing.swingcontrollerizer.parser.ComponentValueParser;
import com.oneitthing.swingcontrollerizer.parser.ComponentValues;
import com.oneitthing.swingcontrollerizer.parser.Parser;
import com.oneitthing.swingcontrollerizer.util.ComponentSearchUtil;
import com.oneitthing.swingcontrollerizer.util.MessageDialogUtil;
import com.oneitthing.swingcontrollerizer.validator.ValidateError;
import com.oneitthing.swingcontrollerizer.validator.ValidateErrors;
import com.oneitthing.swingcontrollerizer.validator.Validator;

/**
 * <p>[概 要] </p>
 * 全アクションクラスの抽象基底アクションクラスです。
 *
 * <p>[詳 細] </p>
 * アクションクラスとして動作する為の必要最低限機能と、
 * ウィンドウ操作やコンポーネント取得の為のAPIを提供します。
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public abstract class AbstractAction implements Action {

	/** MVC各レイヤを伝播するパラメータオブジェクトです。 */
	private ParameterMapping parameterMapping;

	/** このアクションを起動したコントローラです。 */
	private AbstractController controller;

	/**
	 * <p>[概 要] </p>
	 * MVC各レイヤを伝播するパラメータオブジェクトを取得します。
	 *
	 * <p>[詳 細] </p>
	 * parameterMappingフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return MVC各レイヤを伝播するパラメータオブジェクト
	 */
	public ParameterMapping getParameterMapping() {
		return this.parameterMapping;
	}

	/**
	 * <p>[概 要] </p>
	 * MVC各レイヤを伝播するパラメータオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * parameterMappingフィールドを引数parameterMappingで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameterMapping
	 */
	public void setParameterMapping(ParameterMapping parameterMapping) {
		this.parameterMapping = parameterMapping;
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
	 * コントローラにコールされるアクションの主幹メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 引数parameterMappingをフィールドに保存します。
	 *
	 * <p>[備 考] </p>
	 * このメソッドをオーバーライドして新たなアクション基底クラスを作成する場合、
	 * super.run(parameterMapping);を記述する必要が有ります。
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	public ParameterMapping run(ParameterMapping parameterMapping) throws Exception{
		this.parameterMapping = parameterMapping;

		return parameterMapping;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデーションを行うメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * validatorsメソッドで登録されたValidatorの数分、バリデーションを行います。
	 * バリデーションエラーが発生した場合、エラー情報がValidateErrorオブジェクトに設定され、
	 * 戻り値であるValidateErrorsオブジェクトに追加されます。<br>
	 *
	 * <p>[備 考] </p>
	 * ValidateErrors返却後、{@link BaseAction#run(ClientEvent, ParameterMapping)}は
	 * {@link BaseAction#validationFault(ValidateErrors)}メソッドをテンプレートコールします。<br>
	 * ValidateErrorをハンドリングする場合は、validationFaultメソッドをオーバーライドして下さい。
	 *
	 * @param validators validatorsメソッドで設定されたバリデータオブジェクト群
	 * @return バリデーションエラー保持リストオブジェクト
	 * @throws Exception
	 */
	protected ValidateErrors validate(List<Validator> validators)
			throws Exception {
		ValidateErrors validateErrors = new ValidateErrors();

		try {
			for (Validator validator : validators) {
				Component element = validator.getComponent();
				returnElementStatusBeforeError(element);
			}

			for (Validator validator : validators) {
				// バリデート実行
				if (!validator.execute()) {
					validateErrors.addError(new ValidateError(validator
							.getComponent(), validator.getErrorMessage(),
							validator.getHeadWord()));
				}
			}
		} catch (Exception e) {
			throw new CoreLogicException("EFC0009", e);
		}

		return validateErrors;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデーションエラーが発生した時にコールされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 一つでもバリデーションエラーが発生した場合に呼び出されます。<br>
	 * ClientConfigオブジェクトの設定によってデフォルト処理が異なります。<p>
	 *
	 * 【ClientConfig#isEnableValidationFaultProcessing()がtrue】<br>
	 * デフォルトエラーハンドリング処理を行います。<p>
	 *
	 * 【ClientConfig#isComponentColorAndTipChangeOnValidationFault()がtrue】<br>
	 * ・エラーコンポーネントの背景色をcomponent.color.on.validation.faultの値で変更します。<br>
	 * ・エラーコンポーネントのツールチップをエラーメッセージに変更します。<p>
	 *
	 * 【ClientConfig#isDisplayDialogOnValidationFault()がtrue】<br>
	 * 全エラーメッセージをダイアログ表示します。
	 *
	 * <p>[備 考] </p>
	 * デフォルトエラーハンドリング処理を行わない場合、
	 * ClientConfig#setEnableValidationFaultProcessing(false)を実行して下さい。
	 *
	 * @see AbstractController#initialize()
	 * @param errors {@link #validate(List)}で生成されたエラーリストオブジェクト
	 */
	public void validationFault(ValidateErrors errors) {
		ClientConfig config = getController().getClientConfig();
		if (config.isEnableValidationFaultProcessing()) {
			// バリデーションエラーが一個でも有った場合
			if (errors.hasError()) {
				// コンフィグで設定したエラー表示数分またはエラー数分ループ
				for (int i = 0; i < errors.size(); i++) {
					ValidateError error = errors.getError(i);
					Component errorComponent = error.getComponent();
					String headWord = error.getHeadWord();
					String message = error.getMessage();
					if(errorComponent instanceof JComponent) {

						Map<Integer, Map<String, Object>> snapshot =
							getController().getErrorComponentSnapshot().get(getOwnWindow().hashCode());
						Map<String, Object> errorInfo = new HashMap<String, Object>();

						if (config.isComponentColorChangeOnValidationFault()) {
							errorInfo.put("color", ((JComponent)errorComponent).getBackground());
							((JComponent)errorComponent).setBackground(config.getComponentColorOnValidationFault());
						}
						if (config.isComponentTipChangeOnValidationFault()) {
							errorInfo.put("tip", ((JComponent)errorComponent).getToolTipText());
							((JComponent)errorComponent).setToolTipText(message);
						}
						snapshot.put(errorComponent.hashCode(), errorInfo);
					}
				}
			}
		}
	}

	/**
	 * <p>[概 要] </p>
	 * コンポーネントの背景色とチップをバリデーションエラー発生前の状態に戻します。
	 *
	 * <p>[詳 細] </p>
	 * AbstractControllerに保存されているerrorComponentSnapshotオブジェクトから
	 * 引数で指定されたコンポーネントのエラー前状態を取得します。
	 * エラー前状態（backgroundColor、tooltip）属性を取り出し、現在の
	 * コンポーネントにセットします。
	 * errorComponentSnapshotオブジェクトに引数指定されたコンポーネントが存在しなかった場合は、
	 * 処理を行わずにfalseを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component 状態を元に戻すエレメント
	 * @return true : 状態復帰
	 */
	protected boolean returnElementStatusBeforeError(Component component) {
		boolean ret = false;

		// バリデーションエラー前のコンポーネントを取得
		Map<String, Object> errorInfo = getController()
				.getErrorComponentSnapshot().get(getOwnWindow().hashCode()).get(component.hashCode());

		// バリデーションエラー前のコンポーネントが有った場合
		if (errorInfo != null) {
			// 背景色を元に戻す
			Color color = (Color)errorInfo.get("color");
			if (color != null) {
				component.setBackground(color);
			}
			// ツールチップを元に戻す
			String tip = (String)errorInfo.get("tip");
			if (tip != null) {
				((JComponent)component).setToolTipText(tip);
			}
			// 状態復帰処理成功
			ret = true;
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * アプリ起動～終了まで存在するデータ保存領域を取得します。
	 *
	 * <p>[詳 細] </p>
	 * コントローラに保持されているデータ保持領域Mapインスタンスを取得します。
	 *
	 * <p>[備 考] </p>
	 * このデータ保持領域は、コントローラの初期化時に生成され、
	 * アプリケーション終了時まで保持されます。
	 *
	 * @return アプリ起動～終了まで存在するデータ保存領域
	 */
	public Map<Object, Object> getPermanent() {
		return getController().getPermanent();
	}

	/**
	 * <p>[概 要] </p>
	 * アプリ起動～終了まで存在するデータ保存領域から引数keyに対応する値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * コントローラに保持されているデータ保持領域Mapから、
	 * 引数keyをキーにして値を取得、返却します。
	 *
	 * <p>[備 考] </p>
	 * このデータ保持領域は、コントローラの初期化時に生成され、
	 * アプリケーション終了時まで保持されます。
	 *
	 * @param key データ保存領域Map内のキー
	 * @return 引数keyに対する値
	 */
	public Object getPermanent(Object key) {
		return getController().getPermanent().get(key);
	}

	/**
	 * <p>[概 要] </p>
	 * アプリ起動～終了まで存在するデータ保存領域にkey=valueの形式で値を追加します。
	 *
	 * <p>[詳 細] </p>
	 * コントローラに保持されているデータ保持領域Mapを取得、
	 * 引数keyをキーにして引数valueを値として追加します。
	 *
	 * <p>[備 考] </p>
	 * このデータ保持領域は、コントローラの初期化時に生成され、
	 * アプリケーション終了時まで保持されます。
	 *
	 * @param key データ保存領域Map内のキー
	 * @param value 引数keyに対する値
	 */
	public void addPermanent(Object key, Object value) {
		getController().getPermanent().put(key, value);
	}

	/**
	 * <p>[概 要] </p>
	 * アプリ起動～終了まで存在するデータ保存領域から引数keyに対応する値を削除します。
	 *
	 * <p>[詳 細] </p>
	 * コントローラに保持されているデータ保持領域Mapから、
	 * 引数keyをキーにして値を削除します。
	 *
	 * <p>[備 考] </p>
	 * このデータ保持領域は、コントローラの初期化時に生成され、
	 * アプリケーション終了時まで保持されます。
	 *
	 * @param key データ保存領域Map内のキー
	 * @return 削除されたキーに対するValue
	 */
	public Object removePermanent(Object key) {
		return getController().getPermanent().remove(key);
	}

	/**
	 * <p>[概 要] </p>
	 * Actionを発生させたイベントソースコンポーネントが属する、ウィンドウレベルコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * このアクションを発生させたエレメントを取得し、
	 * {@link Component#getParent()}がWindow継承クラスオブジェクトをlを返却するまで親を辿ります。
	 * このメソッドの戻り値が、このアクションを発生させたコンポーネントが属するウィンドウであると見做されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return Actionを発生させたソースコンポーネントが所属するウィンドウレベルコンポーネント
	 */
	protected Window getOwnWindow() {
		Window ret = null;

//		Component eventSource = (Component)getParameterMapping().getEventSource();
		Component eventSource = (Component)getParameterMapping().getListenerAddedComponent();
		// イベントディスパッチスレッド切り替えの結果、getParent()が参照を失うケースが有る為、
		// eventSourceからgetParent()はしない。
		Component eventSourceParent = (Component)getParameterMapping().getEventSourceParent();

		ret = ComponentSearchUtil.searchWindowLevelObject(eventSource, eventSourceParent);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * メモリ上に存在するウィンドウの中から、引数windowNameをnameとして持つWindowインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * {@link WindowManager#getWindowByName(String)}の戻り値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowName 取得するウィンドウレベルコンポーネントの名前
	 * @return メモリ上に存在する、windowNameをnameとして持つWindowインスタンス
	 */
	protected Window getWindow(String windowName) {
		Window ret = null;

		ret = WindowManager.getInstance().getWindowByName(windowName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * メモリ上に存在する全ウィンドウを返却します。
	 *
	 * <p>[詳 細] </p>
	 * {@link WindowManager#getWindowList()}の戻り値を配列変換して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return メモリ上に存在する全ウィンドウ
	 */
	protected Window[] getWindows() {
		Window[] ret = null;

		ret = WindowManager.getInstance().getWindowList().toArray(new Window[1]);

		return ret;
	}

	/**
	 *
	 * @param windowName
	 * @return
	 */
	protected Window[] getWindows(String windowName) {
		Window[] ret = null;

		ret = WindowManager.getInstance().getWindowsByName(windowName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * このActionを発生させたコンポーネントが属するウィンドウレベルコンポーネントから、
	 * 引数componentNameをnameとして持つコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 自ウィンドウコンポーネントを求め、
	 * {@link ComponentSearchUtil#searchComponentByName(Component, String)}
	 * メソッドを呼び出します。<br>
	 * 引数componentNameをnameとして持つコンポーネントが自ウィンドウ内に無かった場合はnullを返却します。
	 *
	 * <p>[備 考] </p>
	 * 同じWindow、同じname属性のコンポーネントが複数有る場合でも、
	 * このメソッドを使用することでユニークに取得出来ます。
	 *
	 * @param componentName 取得するコンポーネントの名前
	 * @return 自ウィンドウ内で引数componentNameをnameとして持つコンポーネント
	 */
	protected Component getComponent(String componentName) {
		Component ret = null;

		Window window = getOwnWindow();

		ret = getComponent(window, componentName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowNameで指定されたウィンドウレベルコンポーネントから、
	 * 引数nameをnameとして持つコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 第一引数コンポーネントの子孫コンポーネントの中から、引数nameをname属性として持つ
	 * コンポーネントを返却します。
	 *
	 * <p>[備 考] </p>
	 * Aウィンドウで発生したActionの中からBウィンドウのコンポーネント値を参照する場合等に使用します。
	 *
	 * @param windowName 取得したいコンポーネントが存在するウィンドウレベルコンポーネントの名前
	 * @param componentName 取得するコンポーネントのname属性値
	 * @return 他ウィンドウ内で引数nameをnameとして持つコンポーネント
	 */
	protected Component getComponent(String windowName, String componentName) {
		Component ret = null;

		Window window = getWindow(windowName);

		ret = getComponent(window, componentName);

		return ret;
	}


	/**
	 * <p>[概 要] </p>
	 * 引数windowで指定されたウィンドウレベルコンポーネントから、
	 * 引数nameをnameとして持つコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 第一引数コンポーネントの子孫コンポーネントの中から、引数nameをname属性として持つ
	 * コンポーネントを返却します。
	 *
	 * <p>[備 考] </p>
	 * Aウィンドウで発生したActionの中からBウィンドウのコンポーネント値を参照する場合等に使用します。
	 *
	 * @param window 取得したいコンポーネントが存在するウィンドウレベルコンポーネントの名前
	 * @param componentName 取得するコンポーネントのname属性値
	 * @return 他ウィンドウ内で引数nameをnameとして持つコンポーネント
	 */
//	protected Component getComponent(Window window, String componentName) {
	protected Component getComponent(Component from, String componentName) {
		Component ret = null;

//		ret = ComponentSearchUtil.searchComponentByName(window, componentName);
		ret = ComponentSearchUtil.searchComponentByName(from, componentName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * このActionを発生させたコンポーネントが属するウィンドウレベルコンポーネントから、
	 * 引数componentNameをnameとして持つ全てのコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 自ウィンドウレベルコンポーネントの子孫コンポーネントの中から、
	 * 引数nameをname属性として持つコンポーネントを全て返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName 取得するコンポーネントの名前
	 * @return 自ウィンドウ内でcomponentNameをnameとして持つ全てのコンポーネント
	 */
	protected Component[] getComponents(String componentName) {
		Component[] ret = null;

		Window window = getOwnWindow();
		ret = getComponents(window, componentName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowNameで指定されたウィンドウレベルコンポーネントから、
	 * 引数nameをnameとして持つ全てのコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 第一引数コンポーネントの子孫コンポーネントの中から、引数nameをname属性として持つ
	 * コンポーネントを全て返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowName 取得したいコンポーネントが存在するウィンドウレベルコンポーネントの名前
	 * @param componentName 取得するコンポーネントのname属性値
	 * @return 他ウィンドウ内で引数nameをnameとして持つ全てのコンポーネント
	 */
	protected Component[] getComponents(String windowName, String componentName) {
		Component[] ret = null;

		Window window = getWindow(windowName);
		ret = getComponents(window, componentName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowで指定されたウィンドウレベルコンポーネントから、
	 * 引数nameをnameとして持つ全てのコンポーネントを取得します。
	 *
	 * <p>[詳 細] </p>
	 * 第一引数コンポーネントの子孫コンポーネントの中から、引数nameをname属性として持つ
	 * コンポーネントを全て返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 取得したいコンポーネントが存在するウィンドウレベルコンポーネントの名前
	 * @param componentName 取得するコンポーネントのname属性値
	 * @return 他ウィンドウ内で引数nameをnameとして持つ全てのコンポーネント
	 */
	protected Component[] getComponents(Window window, String componentName) {
		Component[] ret = null;

		List<Component> componentList = new ArrayList<Component>();
		ComponentSearchUtil.searchComponentsByName(componentList, window, componentName);

		ret = componentList.toArray(new Component[0]);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentNameをnameとして持つコンポーネントの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * 自ウィンドウレベルコンポーネント({@link #getOwnWindow()})から、
	 * 引数nameをnameとして持つコンポーネントを取得、コンポーネントの保持する値を
	 * 汎用的な値格納オブジェクトに格納して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName 値を取得するコンポーネントの名前
	 * @return コンポーネント値の汎用格納オブジェクト
	 * @throws Exception
	 */
	protected ComponentValues getComponentValues(String componentName) throws Exception {
		ComponentValues ret = null;

		Window window = getOwnWindow();

		ret = getComponentValues(window, componentName);

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentNameをnameとして持つエレメントの値を取得します。
	 *
	 * <p>[詳 細] </p>
	 * 引数windowNameで指定されたウィンドウレベルコンポーネントから、
	 * 引数nameをname属性値として持つコンポーネントを取得、コンポーネントの保持する値を
	 * 汎用的な値格納オブジェクトに格納して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param windowName 引数nameをname属性値として持つエレメントが存在するウィンドウレベルコンポーネントの名前
	 * @param componentName 値を取得するコンポーネントの名前
	 * @return コンポーネント値の汎用格納オブジェクト
	 * @throws Exception
	 */
	protected ComponentValues getComponentValues(String windowName, String componentName) throws Exception {
		ComponentValues ret = null;

		Window window = getWindow(windowName);

		ret = getComponentValues(window, componentName);

		return ret;
	}

	/**
	 *
	 * @param window
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected ComponentValues getComponentValues(Window window, String componentName) throws Exception {
		ComponentValues ret = null;

		Parser parser = new ComponentValueParser();
		ret = (ComponentValues)parser.parse(getComponent(window, componentName));

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * {@link ComponentValues#getComponentValue(int)}の0番目のインデックスの値を返却します。
	 *
	 * <p>[詳 細] </p>
	 * getComponentValues(0).getValue()と同義です。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName
	 * @return コンポーネント値
	 * @throws Exception
	 */
	protected Object getComponentValue(String componentName) throws Exception {
		Object ret = null;

		Window window = getOwnWindow();

		ret = getComponentValue(window, componentName);

		return ret;
	}

	/**
	 *
	 * @param windowName
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected Object getComponentValue(String windowName, String componentName) throws Exception {
		Object ret = null;

		Window window = getWindow(windowName);

		ret = getComponentValue(window, componentName);

		return ret;
	}

	/**
	 *
	 * @param window
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected Object getComponentValue(Window window, String componentName) throws Exception {
		Object ret = null;

		ComponentValues componentValues = getComponentValues(window, componentName);
		ComponentValue componentValue = componentValues.getComponentValue(0);

		ret = componentValue.getValue();

		return ret;

	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected String getComponentValueAsString(String componentName) throws Exception {
		String ret = null;

		Window window = getOwnWindow();
		ret = getComponentValueAsString(window, componentName);

		return ret;
	}

	/**
	 *
	 * @param windowName
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected String getComponentValueAsString(String windowName, String componentName) throws Exception {
		String ret = null;

		Window window = getWindow(windowName);
		ret = getComponentValueAsString(window, componentName);

		return ret;
	}

	/**
	 *
	 * @param window
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	protected String getComponentValueAsString(Window window, String componentName) throws Exception {
		String ret = null;

		ComponentValues values = getComponentValues(window, componentName);
		Object value = values.getComponentValue();
		ret = value.toString();

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowを表示します。
	 *
	 * <p>[詳 細] </p>
	 * ウィンドウの多重起動を制御します。
	 * 引数duplicateがfalseの場合、既にwindowと同じ名前を持つウィンドウが
	 * メモリ上に存在している場合、新規に表示は行わず、既存windowにフォーカスをあてます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 表示するウィンドウインスタンス
	 * @param duplicate true：多重起動可、false：多重起動不可
	 */
	protected void showWindow(Window window, boolean duplicate) {
		WindowManager.getInstance().showWindow(window, duplicate);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数windowを引数baseWindowNameに隣接して表示します。
	 *
	 * <p>[詳 細] </p>
	 * ウィンドウの多重起動を制御します。
	 * 引数duplicateがfalseの場合、既にwindowと同じ名前を持つウィンドウが
	 * メモリ上に存在している場合、新規に表示は行わず、既存windowにフォーカスをあてます。
	 *
	 * 表示されるウィンドウ位置は引数positionで以下のように決定されます。
	 * baseWindowNameをnameとして持つウィンドウの、
	 * <OL>
	 *   <LI>直上：WindowManager.BASE</LI>
	 *   <LI>右隣：WindowManager.RIGHT</LI>
	 *   <LI>下隣：WindowManager.BOTTOM</LI>
	 * </OL>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param window 表示するウィンドウインスタンス
	 * @param duplicate duplicate true：多重起動可、false：多重起動不可
	 * @param baseWindowName 隣接するウィンドウの名前
	 * @param position 隣接位置定数
	 */
	protected void showWindow(Window window, boolean duplicate, String baseWindowName, int position) {
		WindowManager.getInstance().showWindow(window, duplicate, baseWindowName, position);
	}

	/**
	 * <p>[概 要] </p>
	 * モーダルメッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showMessageDialog(Component, String, String, int)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * アイコンタイプはMessageDialogUtil.INFORMATION_MESSAGEです。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 */
	protected int showMessageDialog(String message) {
		return MessageDialogUtil.showMessageDialog(getOwnWindow(), message, null, MessageDialogUtil.INFORMATION_MESSAGE);
	}

	/**
	 * <p>[概 要] </p>
	 * モーダルメッセージダイアログをタイトル付きで表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showMessageDialog(Component, String, String, int)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * タイトルは第2引数title、
	 * アイコンタイプはMessageDialogUtil.INFORMATION_MESSAGEです。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 */
	protected int showMessageDialog(String message, String title) {
		return MessageDialogUtil.showMessageDialog(getOwnWindow(), message, title, MessageDialogUtil.INFORMATION_MESSAGE);
	}

	/**
	 * <p>[概 要] </p>
	 * モーダルメッセージダイアログをタイトル付きで表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showMessageDialog(Component, String, String, int)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * タイトルは第2引数title、
	 * アイコンタイプは第3引数messageTypeです。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 * @param messageType メッセージアイコンタイプ
	 */
	protected int showMessageDialog(String message, String title, int messageType) {
		return MessageDialogUtil.showMessageDialog(getOwnWindow(), message, title, messageType);
	}

	/**
	 * <p>[概 要] </p>
	 * モーダルメッセージダイアログをタイトル付きで表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showMessageDialog(Component, String, String, int)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * タイトルは第2引数title、
	 * アイコンタイプは第3引数messageType、
	 * ボタン配置は第4引数buttonOptionです。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 * @param messageType メッセージアイコンタイプ
	 * @param buttonOption ボタン配置
	 */
	protected int showMessageDialog(String message, String title, int messageType, int buttonOption) {
		return MessageDialogUtil.showMessageDialog(getOwnWindow(), message, title, messageType, buttonOption);
	}

	/**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログを表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showModelessMessageDialog(Component, String, String, MessageDialogListener)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * アイコンタイプはMessageDialogUtil.INFORMATION_MESSAGEです。
	 *
	 * イベントディスパッチスレッドと平行に表示されているモードレスダイアログの
	 * ユーザインタラクションを取得する為に、第2引数listenerを使用します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 * @param listener ダイアログ結果取得リスナインスタンス
	 */
	protected void showModelessMessageDialog(String message, MessageDialogListener listener) {
		MessageDialogUtil.showModelessMessageDialog(getOwnWindow(), message, null, listener);
	}

	/**
	 * <p>[概 要] </p>
	 * モードレスメッセージダイアログをタイトル付きで表示します。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageDialogUtil#showModelessMessageDialog(Component, String, String, MessageDialogListener)}
	 * を呼び出します。
	 * 表示基底コンポーネントは{@link #getOwnWindow()}、
	 * タイトルは第2引数title、
	 * アイコンタイプはMessageDialogUtil.INFORMATION_MESSAGEです。
	 *
	 * イベントディスパッチスレッドと平行に表示されているモードレスダイアログの
	 * ユーザインタラクションを取得する為に、第2引数listenerを使用します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message 表示するメッセージ
	 * @param title ダイアログタイトル
	 * @param listener ダイアログ結果取得リスナインスタンス
	 */
	protected void showModelessMessageDialog(String message, String title, MessageDialogListener listener) {
		MessageDialogUtil.showModelessMessageDialog(getOwnWindow(), message, title, listener);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentに追加されているイベントリスナを削除します。
	 *
	 * <p>[詳 細] </p>
	 *
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component
	 * @param listenerType
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	protected void removeListener(JComponent component,
									Class listenerType,
									EventListener listener) throws Exception
	{
		Field listenerListField = JComponent.class.getDeclaredField("listenerList");
		listenerListField.setAccessible(true);

		EventListenerList listenerList = (EventListenerList)listenerListField.get(component);
		listenerList.remove(listenerType, listener);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentへイベント追加後、EventBinderへイベント紐付けを追加します。
	 *
	 * <p>[詳 細] </p>
	 * {@link EventBinder#addEventBindingImmediately(Component, Class, String, Class)}
	 * を呼び出します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component イベント追加するコンポーネント
	 * @param listenerType イベントリスナクラスタイプ
	 * @param eventType イベントハンドルメソッド名
	 * @param actionClass 実行するAbstractAction継承アクションクラス
	 */
	protected void addEventBindingImmediately(JComponent component,
			Class<? extends EventListener> listenerType,
			String eventType,
			Class<? extends AbstractAction> actionClass)
	{
		BaseController controller = ((BaseController)getController());
		EventBinder eventBinder = controller.getEventBinder();

		eventBinder.addEventBindingImmediately(component, listenerType, eventType, actionClass);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentのイベント削除後、EventBinderへイベント紐付けを消去します。
	 *
	 * <p>[詳 細] </p>
	 * {@link EventBinder#removeEventBindingImmediately(Component, Class, String, EventListener)}
	 * を呼び出します。
	 *
	 * <p>[備 考] </p>
	 *
	 *
	 * <b>使用例) </b><br/>
	 * ・ログインボタンを押下後、ログアウトボタンに変更する
	 * <pre class="samplecode">
	 *  // ログインボタンインスタンスを取得
	 *  JButton jbLogin = ((JButton)getComponentByName("loginFrame.jbLogin"));
	 *  // 文言変更
	 *  jbLogin.setText("ログアウト");
	 *  // ParameterMappingからイベントリスナタイプ取得
	 *  Class<? extends EventListener> listenerType = getParameterMapping().getEventListenerType();
	 *  // ParameterMappingからイベントリスナインスタンス取得
	 *  EventListener listener = getParameterMapping().getEventListener();
	 *  // イベント削除してEventBinderも紐付け予約を消去
	 *  removeEventBindingImmediately(jb, listenerType, "actionPerformed", listener);
	 * </pre>
	 *
	 * @param component イベント削除するコンポーネント
	 * @param listenerType イベントリスナクラスタイプ
	 * @param eventType イベントハンドルメソッド名
	 * @param listener 削除するリスナインスタンス
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unchecked")
	public void removeEventBindingImmediately(JComponent component,
			Class listenerType,
			String eventType,
			EventListener listener)
		throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
	{
		BaseController controller = ((BaseController)getController());
		EventBinder eventBinder = controller.getEventBinder();

		eventBinder.removeEventBindingImmediately(component, listenerType, eventType, listener);
	}
}
