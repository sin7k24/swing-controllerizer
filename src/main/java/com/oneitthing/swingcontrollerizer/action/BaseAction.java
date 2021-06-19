package com.oneitthing.swingcontrollerizer.action;

import java.util.ArrayList;
import java.util.List;

import com.oneitthing.swingcontrollerizer.controller.BaseController;
import com.oneitthing.swingcontrollerizer.controller.EventBinder;
import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.model.Model;
import com.oneitthing.swingcontrollerizer.validator.ValidateErrors;
import com.oneitthing.swingcontrollerizer.validator.Validator;

/**
 * <p>[概 要] </p>
 * イベント発生時、コントローラによって起動されるActionの基底クラスです。
 *
 * <p>[詳 細] </p>
 * {@link com.oneitthing.swingcontrollerizer.controller.AbstractController#bind(EventBinder)}
 * によって登録したイベントに対応してこのクラスの継承アクションクラスが起動されます。<br>
 * 実行される具象アクションクラスには、テンプレートコールされる以下の処理を実装します。<br>
 *
 * <ul>
 *     <li>前処理、クライアント完結処理 : {@link #prepare(ParameterMapping)}</li>
 *     <li>画面入力値のチェック : {@link #validators(List)},　{@link #validationFault(ValidateErrors)}</li>
 *     <li>実行モデルの予約、設定 : {@link #reserveModels(List)},　{@link #nextModel(int, ModelProcessEvent, Model)}</li>
 *     <li>実行モデルの成功ハンドリング : {@link #successForward(int, Model, Object)}</li>
 *     <li>実行モデルの失敗ハンドリング : {@link #failureForward(int, Model, Exception)}</li>
 * </ul>
 * これらは全て必須では無く、任意での実装になります。<br>
 * これらのメソッドはBaseActionのrunメソッド内コールフロー、
 * 及びBaseControllerからコールバックされる為、任意での呼び出しは不要です。<br>
 *
 * <p>[備 考] </p>
 * 上記のようにこの基底クラスは、処理フローを実装して開発手順の型決めを行っています。<br>
 * 全てのモデル処理結果をsucessForward、failureForwardでハンドリングするのでは無い場合は{@link DispatchAction}を、<br>
 * reserveModel、nextModelによるモデル実行制御を行わない場合は、{@link FlexibleAction}を基底クラスとして選択して下さい。
 * <p>
 *
 * <b>使用例）</b><br>
 * 典型的なBaseAction継承アクション
 *
 * <pre class="samplecode">
 *    package demo.client.form.action;
 *
 *    import java.util.List;
 *
 *    import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *    import com.oneitthing.swingcontrollerizer.model.HTTPRequestCore;
 *    import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *    public class OpenPostalWindowAction extends BaseAction {
 *
 *        // ①前準備処理を実装します
 *        &#064;Override
 *        protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *            System.out.println("サーブレットと通信してユーザ、パスワードを登録します。");
 *            return true;
 *        }
 *
 *        // ②機能モデルを登録します
 *        &#064;Override
 *        protected void reserveModels(List<Class<? extends Model>> models) {
 *            models.add(HTTPRequestCore.class);
 *        }
 *
 *        // ③コントローラによってインスタンス化された機能モデルの設定を行います
 *        &#064;Override
 *        public void nextModel(int index, Model prev, Model next) throws Exception{
 *            switch(index){
 *                case 0:
 *                    // 画面からユーザ名とパスワードを取得
 *                    String user = getComponentValueAsString("userRegistFrame.jtfUser");
 *                    char[] pass = (char[])getComopnentValue("userRegistFrame.jpwPass");
 *
 *                    // サーバロジックは任意
 *                    ((HTTPRequestCore)next).setRequestUrl("UserRegist.do");
 *                    ((HTTPRequestCore)next).addUrlParameters("user", user);
 *                    ((HTTPRequestCore)next).addUrlParameters("pass", String.valueOf(pass));
 *                    break;
 *            }
 *        }
 *
 *        // ④機能モデルの正常終了処理結果をハンドリングします
 *        &#064;Override
 *        public void successForward(int index, Model model, Object result) throws Exception {
 *            System.out.println("ユーザ登録正常終了");
 *        }
 *
 *        // ④´機能モデルの異常終了処理結果をハンドリングします
 *        &#064;Override
 *        public Exception failureForward(int index, Model model, Exception e) {
 *            System.out.println("ユーザ登録異常終了");
 *        }
 *    }
 *
 * </pre>
 *
 * 一回目の通信結果に応じて二回目の通信を振り分ける
 * <pre class="samplecode">
 *    package demo.client.distribute.action;
 *
 *    import java.util.List;
 *
 *    import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *    import com.oneitthing.swingcontrollerizer.model.HTTPRequestCore;
 *    import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *    import demo.share.dao.DataDao;
 *
 *    public class DistributeLogicAction extends BaseAction {
 *
 *        &#064;Override
 *        protected void reserveModels(List<Class<? extends Model>> models) {
 *            models.add(HTTPRequestCore.class);
 *            models.add(HTTPRequestCore.class);
 *        }
 *
 *        &#064;Override
 *        public void nextModel(int index, Model prev, Model next) throws Exception{
 *            // サーバロジックは任意
 *            ((HTTPRequestCore)next).setRequestUrl("webcontroller");
 *
 *            switch(index){
 *                case 0:
 *                    ((HTTPRequestCore)next).addUrlParameters("model.fqcn", "demo.server.model.FetchDataModel");
 *                    break;
 *                case 1:
 *                    DataDao dao = (DataDao)prev.getResult();
 *                    int div = dao.getDivision();
 *                    // 一回目の通信結果を判断して実行するサーバロジックを切り替える
 *                    if(div == 0) {
 *                        ((HTTPRequestCore)next).addUrlParameters("model.fqcn", "demo.server.model.Div0Model");
 *                    }else {
 *                        ((HTTPRequestCore)next).addUrlParameters("model.fqcn", "demo.server.model.Div1Model");
 *                    }
 *            }
 *        }
 *    }
 * </pre>
 *
 * 登録した機能モデルを並列実行する
 * <pre class="samplecode">
 *	package demo.communication.action;
 *
 *	import java.util.List;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.EJBProxyCore;
 *	import com.oneitthing.swingcontrollerizer.model.HTTPRequestCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *	import com.oneitthing.swingcontrollerizer.model.WebServiceCore;
 *	import demo.server.ws.heavyprocess.HeavyProcess;
 *	import demo.server.ws.heavyprocess.HeavyProcessService;
 *
 *	public class ModelExecuteByParallelAction extends BaseAction {
 *
 *		&#064;Override
 *		protected boolean isRunModelsAndNoWait() throws Exception {
 *			// ①このメソッドをオーバーライドしてtrueを返却することで、
 *			// コントローラが機能モデルを実行する際、前のモデルの終了を待たなくなります。
 *			return true;
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			models.add(EJBProxyCore.class);
 *			models.add(WebServiceCore.class);
 *			models.add(HTTPRequestCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			switch(index) {
 *			case 0:
 *				System.out.println("EJBProxyCore : nextModel \n");
 *
 *				// ②trueを指定することで非同期スレッドでEJB通信が行われます。
 *				((EJBProxyCore)next).setAsync(true);
 *				((EJBProxyCore)next).setLookupName("FunctionalDemoForSwingServer/HeavyProcessBean/remote");
 *				((EJBProxyCore)next).setMethodName("heavyProcess");
 *				((EJBProxyCore)next).addParameter(5000L);
 *				break;
 *			case 1:
 *				System.out.println("WebServiceCore : nextModel \n");
 *
 *				// ③trueを指定することで非同期スレッドでWebService通信が行われます。
 *				((WebServiceCore)next).setAsync(true);
 *				((WebServiceCore)next).setServiceClass(HeavyProcessService.class);
 *				((WebServiceCore)next).setPortClass(HeavyProcess.class);
 *				((WebServiceCore)next).setMethodName("heavyProcess");
 *				((WebServiceCore)next).addParameter(5000L);
 *				break;
 *			case 2:
 *				System.out.println("HTTPRequestCore : nextModel \n");
 *
 *				// ④trueを指定することで非同期スレッドでHTTP通信が行われます。
 *				((HTTPRequestCore)next).setAsync(true);
 *				((HTTPRequestCore)next).setRequestUrl("webcontroller");
 *				((HTTPRequestCore)next).addUrlParamteter("model.fqcn", "demo.server.model.HeavyProcessModel");
 *				break;
 *			}
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result)	throws Exception {
 *			// ⑤モデル実行開始順（index順）では無く、処理終了順にsuccessForwardがコールバックされます。
 *			switch(index) {
 *			case 0:
 *				System.out.println("EJB終了");
 *				break;
 *			case 1:
 *				System.out.println("WebService終了");
 *				break;
 *			case 2:
 *				System.out.println("HTTPリクエスト終了");
 *				break;
 *			}
 *		}
 *
 *		&#064;Override
 *		public void complete(ParameterMapping parameterMapping) {
 *			// ⑥全ての機能モデルが正常終了するとコールバックされます。
 *			System.out.println("全通信モデル終了");
 *		}
 *	}
 * </pre>
 *


 *

 */
public class BaseAction extends AbstractAction {


	@Override
	public BaseController getController() {
		return (BaseController)super.getController();
	}

	/**
	 * <p>[概 要]</p>
	 * コントローラにコールされるアクションの主幹メソッドです。
	 *
	 * <p>[詳 細]</p>
	 * 継承クラスから情報を収集してコントローラに返却します。
	 * 以下の順にテンプレートメソッドがコールされます。
	 * 	<ol>
	 * 		<li>{@link #prepare(ParameterMapping)}　：　前準備の実装、クライアント完結コードの実装</li>
	 * 		<li>{@link #validators(List)}　：　バリデータオブジェクトの登録</li>
	 * 		<li>{@link #validate(List)}　：　バリデーションの実行</li>
	 * 		<li>{@link #validationFault(ValidateErrors)}　：　バリデーションエラーハンドリング</li>
	 * 		<li>{@link #reserveModels(List)}　：　実行モデルの登録</li>
	 * 	</ol>
	 * <p>
	 * 上記のコールフロー終了後、ParameterMappingオブジェクトがコントローラに返却されます。
	 * <p>
	 * prepare実装メソッドでfalseを返却した場合、又はvalidateメソッドが一つでも
	 * ValidateErrorオブジェクトを返却した場合、<br>
	 * コントローラに返却されるParameterMappingオブジェクトはnullになります。<br>
	 * nullのParameterMappingを受け取ったコントローラは以降の処理を中止します。
	 *
	 * <p>[備 考]</p>
	 * このメソッドを実装する必要は有りません。
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 * @return 継承Actionから収集した、コントローラに返却される制御情報
	 */
	public ParameterMapping run(ParameterMapping parameterMapping) throws Exception{
		try{
			super.run(parameterMapping);

			// 前準備メソッドをテンプレートコール
			// falseが返却された場合はAction、Controller共に処理中止
			if(!prepare(parameterMapping)) return null;

			// validatorsテンプレートメソッドから予約されたValidatorインスタンスリストを取得
			List<Validator> validators =
				new ArrayList<Validator>();
			validators(validators);

			// validatorsテンプレートメソッドで予約されたバリデーションを実行
			ValidateErrors validateErrors = validate(validators);
			if(validateErrors.hasError()){
				// エラーが一つでも有ればvalidationFaultをテンプレートコール
				validationFault(validateErrors);
				// Controllerの処理中止
				return null;
			}

			// reserveModelsテンプレートメソッドで予約されたModelクラスリストを取得
			List<Class<? extends Model>> modelClasses =
				new ArrayList<Class<? extends Model>>();
			reserveModels(modelClasses);
			parameterMapping.setModelClasses(modelClasses);

			// 予約モデル群の非同期実行フラグを取得（デフォルト：false）
			boolean runModelsAndNoWait = isRunModelsAndNoWait();
			parameterMapping.setRunModelsAndNoWait(runModelsAndNoWait);

		}catch(Exception e){
			e = trap(e);
			if(e != null){
				throw e;
			}
		}
		return parameterMapping;	}

	/**
	 * <p>[概 要] </p>
	 * BaseAction内で最初にテンプレートコールされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 * オーバーライド先でfalseを返却すると、それ以降のアクション処理は行われません。
	 *
	 * <p>[備 考] </p>
	 * 単純な処理等、クライアント内で完結するようなイベント処理や、
	 * 条件を判断して以降の処理を実行しない、等の処理を実装する場合は、
	 * このメソッドをオーバーライドして処理を記述して下さい。
	 * <p>
	 * <b>使用例) </b>
	 * <p>
	 *	<pre class="samplecode">
	 *    &#064;Override
	 *    protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
	 *         // 発言欄が空の状態で送信ボタンが押下された場合は送信しない
	 *        String chatRemark = getComponentValueAsString("chatFrame.jtfChatRemark")
	 *
	 *        if(chatRemark.length() == 0){
	 *            return false;
	 *        }else{
	 *            return true;
	 *        }
	 *    }
	 * </pre>
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 * @return 以降の処理を継続するかどうかのフラグ
	 */
	protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
		return true;
	}

	/**
	 * <p>[概 要] </p>
	 * Action処理終了後、コントローラに実行させるモデルクラス群の予約を行います。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * Model I/Fを継承して自作モデルクラスを作成し、登録することが出来ますが、
	 * HTTP、EJB、SOAPリクエスト、Pub/Subといった使用頻度の高い機能モデルについては
	 * F/Wでも提供しています。詳しくはmodelパッケージを参照して下さい。
	 *
	 * @param models 実行するモデルクラスを格納するリストオブジェクト
	 */
	protected void reserveModels(List<Class<? extends Model>> models){

	}

	/**
	 * <p>[概 要] </p>
	 * 予約モデルがコントローラによって実行される直前にコールバックされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * reserveModelsで登録したモデル数＋１回、コントローラによって呼び出されます。<br>
	 * 引数prevには前回モデルの処理結果（初回null）が、
	 * 引数nextには次回モデルインスタンス（最終回null）が渡されます。<p>
	 *
	 * {@link BaseAction#isRunModelsAndNoWait()}がfalseの場合、次回モデルの実行が
	 * 前回モデルの終了後であることが確約される為、prevの結果を元にnextの動作設定をすることが出来ます。<p>
	 *
	 * <p>[備 考] </p>
	 * {@link BaseAction#isRunModelsAndNoWait()}がtrueの場合、コントローラはモデルの実行を
	 * シーケンシャルには行いません。<br>
	 * この場合、引数prevにはnullが渡されます。
	 *
	 * @param index 実行インデックス（0 ～ reserveModelsによるモデル登録数）
	 * @param prev 前インデックスで実行されたモデル処理結果イベント
	 * @param next 次インデックスで実行される予定のモデルインスタンス
	 * @return 次のモデルを実行するかどうかのフラグ
	 * @throws Exception オーバーライド先で発生する可能性の有る例外
	 */
	public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
		return true;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデータ群を登録するメソッドです。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param validators Validator継承クラスを追加するリストオブジェクト
	 */
	protected void validators(List<Validator> validators) {

	}

	/**
	 * <p>[概 要] </p>
	 * モデル群実行を非シーケンシャルに行うかどうかを設定するメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルトではfalseを返却します。<br>
	 * {@link BaseAction#reserveModels(List)}によって予約されたモデル群を
	 * コントローラが実行する時、1モデルの結果取得を待ってから次モデルを
	 * 実行するかどうかの設定値として解釈されます。<br>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return true : モデルの処理結果取得を待たずに次モデル実行
	 */
	protected boolean isRunModelsAndNoWait() throws Exception {
		return false;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理が正常終了した契機でコントローラにコールバックされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * コントローラが実行した機能モデルの数分コールバックされます。<br>
	 * どのモデルの処理結果か、を判断するには第一引数indexを判定するか、
	 * 第二引数modelのインスタンスを判定します。<p>
	 *
	 * モデルの処理結果は第三引数resultから取得しますが、実行したモデルの種類によって、
	 * resultの実体（型）が異なります。<br>
	 * フレームワークが提供する以下の代表的なモデルは、次のようにresultを返却します。
	 * <p>
	 *
	 * <b>HTTPRequestCore</b>
	 * <ul>
	 *     <li>応答がxalだった場合：Document</li>
	 *     <li>応答がシリアライズオブジェクトだった場合：デシリアライズされたObject</li>
	 *     <li>サーバがコンテントを返却しなかった場合：null</li>
	 * </ul>
	 * <p>
	 *
	 * <b>SubscribeCore</b>
	 * <ul>
	 *     <li>購読オブジェクトがxalだった場合：Document</li>
	 *     <li>購読オブジェクトがシリアライズオブジェクトだった場合：デシリアライズされたObject</li>
	 *     <li>購読オブジェクトが文字列だった場合：String</li>
	 * </ul>
	 *
	 * <p>[備 考] </p>
	 * 機能モデルによって変換される前の処理結果を取得する場合は、以下のように取得可能です。<br>
	 * Object planeResult = model.getResult();<br>
	 *
	 *
	 * @param index 結果を返却したモデルの実行インデックス
	 * @param model 結果を返却したモデルインスタンス
	 * @param result モデル処理結果オブジェクト
	 */
	public void successForward(int index, Model model, Object result) throws Exception {
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理が異常終了した契機でコントローラにコールバックされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * コントローラに実行されたモデルが例外を発生させた場合、コールバックされます。<br>
	 * 第一引数index、第二引数modelを判定して、エラーハンドリング実装を行います。<p>
	 *
	 * 戻り値Exceptionは最終的にコントローラにキャッチ
	 * （{@link com.oneitthing.swingcontrollerizer.controller.BaseController#trap(Throwable)}）されます。<br>
	 * nullを返却すると、上記のコントローラによるキャッチは発生しません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param index 結果を返却したモデルの実行インデックス
	 * @param model 結果を返却したモデルインスタンス
	 * @param e モデル異常終了時の例外オブジェクト
	 * @return 引数e、若しくはオーバーライドメソッドで変換された例外
	 */
	public Exception failureForward(int index, Model model, Exception e){
		return e;
	}

	/**
	 * <p>[概 要]</p>
	 * reserveModelsで登録されたモデル群が全て正常終了した契機でコールされます。
	 *
	 * <p>[詳 細]</p>
	 * reserveModelsで登録された全モデルがfireModelFinishedを発行
	 * した契機でコントローラによってコールバックされます。
	 *
	 * <p>[備 考]</p>
	 *
	 */
	public void complete(ParameterMapping parameterMapping) throws Exception{
	}

	/**
	 * <p>[概 要]</p>
	 * reserveModelsで登録されたモデル群が全て終了、又は失敗した契機でコールされます。
	 *
	 * <p>[詳 細]</p>
	 * モデル実行の成功、失敗に関わらずコールバックされます。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	public void done(ParameterMapping parameterMapping) throws Exception{
	}

	/**
	 * <p>[概 要] </p>
	 * Action内で発生した例外をハンドリングします。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e アクション内で発生した例外
	 * @return 引数e、若しくはオーバーライド先で生成した例外
	 */
	protected Exception trap(Exception e) {
		return e;
	}

}
