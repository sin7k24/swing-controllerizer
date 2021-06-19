package com.oneitthing.swingcontrollerizer.action;

import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;

/**
 * <p>[概 要] </p>
 * アクションの処理フローを実装する、アクション基底クラスです。
 *
 * <p>[詳 細] </p>
 * 継承クラスは{@link #execute(ParameterMapping)}メソッドを実装して処理を実装します。<br>
 * AbstractActionが提供する基本的な機能＋機能モデルをコントローラ経由で呼び出す為のメソッドを持ちます。<br>
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * ・xal取得、サーバロジック実行、ClientでのSubscribe開始等を任意のタイミングで実行する
 *
 * <pre class="samplecode">
 *	public class TestAction extends FlexibleAction {
 *
 *		&#064;Override
 *		public void execute(ParameterMapping parameterMapping) throws Exception{
 *
 *			// xalを同期要求
 *			HTTPRequestCore xalRequester = new HTTPRequestCore();
 *			xalRequester.setRequestUrl("webcontroller");
 *			xalRequester.addUrlParameters("forward.page", "/pages/serverpush.xal");
 *			xalRequester.setRenderResponse(true);
 *			runModel(xalRequester);
 *
 *			// サーバロジック（パブリッシュ開始）を非同期要求
 *			HTTPRequestCore publishRequester = new HTTPRequestCore();
 *			publishRequester.setRequestUrl("webcontroller");
 *			publishRequester.addUrlParameters("model.fqcn", "demo.server.model.ServerPushStartModel");
 *			publishRequester.addUrlParameters("publishSpan", "2000");
 *			publishRequester.setAsync(true);
 *			runModel(publishRequester);
 *
 *			// サブスクライブ開始を非同期実行
 *			SubscribeCore subscriber = new SubscribeCore();
 *			subscriber.setTopic("serverPushDemo");
 *			subscriber.setSubscribe(true);
 *			runModelWithProcessListener(subscriber, new ModelProcessListener(){
 *				&#064;Override
 *				public void modelSuccess(ModelProcessEvent evt) {
 *					ObjectDataSource odc = getObjectDataSourceById("hostDataSource");
 *					odc.setSource(evt.getResult());
 *				}
 *
 *				&#064;Override
 *				public void modelFailure(ModelProcessEvent evt) {
 *					evt.getExeption().printStackTrace();
 *				}
 *			});
 *
 *			// 取得したxalを操作
 *			Element serverPushWindow =
 *				getWindowsByCommunicateId(xalRequester.getCommunicateId()).get(0);
 *			Element lblStatus =
 *				getElementByNameFromWindow(serverPushWindow, "serverPush.lblStatus");
 *			lblStatus.setAttribute("text", "serverでpublish、clientでsubscribeが開始されました。");
 *
 *			// イベントソースコンポーネントを無効化
 *			Element eventSourceElement = parameterMapping.getEventSourceElement();
 *			eventSourceElement.setAttribute("disabled", "true");
 *		}
 *	}
 * </pre>
 *


 *

 */
public class FlexibleAction extends AbstractAction {

	/**
	 * <p>[概 要] </p>
	 * コントローラから呼び出されるアクションの基幹メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * アクション処理を実装する為の唯一のメソッド{@link #execute(ParameterMapping)}
	 * をテンプレートコールします。
	 * <p>
	 * FlexibleActionにおけるrunメソッドは必ずnullを返却します。nullを返却されたコントローラは、
	 * 以降の処理を行いません。<br>
	 * FlexibleAction継承アクションでは、{@link #execute(ParameterMapping)}
	 * の処理でイベント処理が完結します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	@Override
	public ParameterMapping run(ParameterMapping parameterMapping) throws Exception{
		super.run(parameterMapping);

		// 処理実装メソッドをテンプレートコール
		execute(parameterMapping);

		// コントローラに以降の制御は行わせない
		return null;
	}

	/**
	 * <p>[概 要] </p>
	 * アクション処理を実装するメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * FlexibleAction継承アクションは、このメソッドをオーバーライドして、
	 * バリデーション、モデル実行、エレメント編集等の処理を任意に実装します。<br>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	protected void execute(ParameterMapping parameterMapping) throws Exception {

	}

//	/**
//	 * <p>[概 要] </p>
//	 * 機能モデルをコントローラ経由で呼び出します。
//	 *
//	 * <p>[詳 細] </p>
//	 * コントローラのモデル実行機能に引数modelの実行を委譲します。<br>
//	 * このメソッドで戻り値を返却出来るのは、モデル処理が同期的なものである場合です。<br>
//	 * 非同期的に結果を受けるモデルであった場合、戻り値はnullになります。
//	 *
//	 * <p>[備 考] </p>
//	 * 非同期モデルを実行する場合は{@link #runModelWithProcessListener(Model, ModelProcessListener)}
//	 * を使用して下さい。
//	 *
//	 * @param model 実行する機能モデルインスタンス
//	 * @return 実行された機能モデルインスタンスの汎用戻り値
//	 * @throws Exception
//	 */
//	protected Object runModel(Model model) throws Exception {
//		Object result = getController().runModel(model, getParameterMapping());
//
//		return result;
//	}
//
//	/**
//	 * <p>[概 要] </p>
//	 * 機能モデルをコントローラ経由で呼び出します。
//	 *
//	 * <p>[詳 細] </p>
//	 * コントローラのモデル実行機能に引数modelの実行を委譲します。<br>
//	 * モデルの処理内容の同期、非同期に関わらず正常、異常結果がリスナに通知されます。
//	 *
//	 * <p>[備 考] </p>
//	 *
//	 *
//	 * @param model 実行する機能モデルインスタンス
//	 * @param listener 実行された機能モデルの正常、異常結果を取得する為のリスナインスタンス
//	 * @throws Exception
//	 */
//	protected void runModelWithProcessListener(Model model, ModelProcessListener listener) throws Exception{
//		getController().runModelWithProcessListener(model, listener, getParameterMapping());
//	}
}
