package com.oneitthing.swingcontrollerizer.model;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.manager.JMSConsumeCoreManager;

/**
 * <p>[概 要] </p>
 * JMSサーバキューを購読する為の機能モデルクラスです。
 *
 * <p>[詳 細] </p>
 * JMSキューを使用してP2P通信を行う為のクラスです。<br/>
 * Queueコネクションをスタートしてキューへ送信されたメッセージを受信します。<br>
 * 購読を開始すると、明示的に購読中止を行わない限り、継続してメッセージを受信し続け、
 * 受信する毎にモデル処理成功イベント（ModelProcessEvent.SUCCESS）を発行します。<br>
 * <p>
 *
 * 必須設定メソッド
 * <ul>
 *   <li>{@link #setQueueName(String)} : メッセージ送信対象キュー名設定</li>
 * </ul>
 *
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * サーバでProduce中のキューメッセージを継続的に受信する。
 * <pre class="samplecode">
 *	package demo.serverpush.action;
 *
 *	import java.util.Date;
 *	import java.util.List;
 *	import java.util.Vector;
 *
 *	import javax.jms.MapMessage;
 *	import javax.swing.JScrollBar;
 *	import javax.swing.JScrollPane;
 *	import javax.swing.JTable;
 *	import javax.swing.JToggleButton;
 *	import javax.swing.table.DefaultTableModel;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.JMSConsumeCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *	public class ClientConsumeAction extends BaseAction {
 *
 *		&#064;Override
 *		protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *			// トグルボタンが押下中は受信中
 *			if((Boolean)getComponentValue("serverPushFrame.jtbClientConsume")) {
 *				((JToggleButton)getComponent("serverPushFrame.jtbClientConsume")).setText("受信中");
 *			}else{
 *				((JToggleButton)getComponent("serverPushFrame.jtbClientConsume")).setText("開　始");
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			models.add(JMSConsumeCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next)
 *				throws Exception {
 *
 *			if(index == 0) {
 *				((JMSConsumeCore)next).setQueueName("queue/testQueue");
 *				if((Boolean)getComponentValue("serverPushFrame.jtbClientConsume")) {
 *					// 受信開始
 *					((JMSConsumeCore)next).setIdentifier(String.valueOf(getOwnWindow().hashCode()));
 *					((JMSConsumeCore)next).setConsume(true);
 *				}else{
 *					// 受信停止
 *					((JMSConsumeCore)next).setIdentifier(String.valueOf(getOwnWindow().hashCode()));
 *					((JMSConsumeCore)next).setConsume(false);
 *				}
 *			}
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			if((Boolean)getComponentValue("serverPushFrame.jtbClientConsume")) {
 *				// 受信中は受信したMapMessageを画面上のJTableに表示
 *				JTable jtConsumeResult = (JTable)getComponent("serverPushFrame.jtConsumeResult");
 *				DefaultTableModel tableModel = (DefaultTableModel)jtConsumeResult.getModel();
 *
 *				MapMessage mapMessage = (MapMessage)result;
 *				String sendIndex = mapMessage.getString("index");
 *				String sendMessage = mapMessage.getString("message");
 *				Date consumeDate = new Date();
 *
 *				Vector<String> rowData = new Vector<String>();
 *				rowData.add(sendIndex);
 *				rowData.add(sendMessage);
 *				rowData.add(consumeDate.toString());
 *
 *				tableModel.addRow(rowData);
 *
 *				JScrollPane jspConsumeResult = (JScrollPane)getComponent("serverPushFrame.jspConsumeResult");
 *				JScrollBar nob = jspConsumeResult.getVerticalScrollBar();
 *				if(nob != null) {
 *					nob.setValue(nob.getMaximum());
 *				}
 *			}
 *		}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			System.out.println("受信失敗");
 *
 *			return e;
 *		}
 *	}
 * </pre>
 *


 *

 */
public class JMSConsumeCore extends BaseModel implements MessageListener {

	/** キューを受信中のJMSConsumeCoreを識別する為の識別子です。 */
	private String identifier;

	/** 開始、中止を判断するフラグです（true:購読開始、false:中止、デフォルト：true） */
	private boolean unconsume;

	/** JMS接続を行う為の接続先環境設定プロパティです。 */
	private Hashtable<String, String> environment;

	/** サーバとのキューコネクションです。 */
	private QueueConnection queueConnection;

	/** サーバとのキューセッションです。 */
	private QueueSession queueSession;

	/** 購読を行うキューです。 */
	private Queue queue;

	/** 購読開始、中止対象キュー名です。 */
	private String queueName;

	/** JMS Queueのメッセージを購読するオブジェクトです。 */
	private MessageConsumer consumer;

	/** JMSコネクションファクトリJNDI名です。（デフォルト：ConnectionFactory） */
	private String connectionFactoryName = "ConnectionFactory";

	/** QueueConnectionに与えるClientIdです。 */
	private String clientId;

	/** コンシューム開始をモデル処理成功と見做すかどうかのフラグです。 */
	private boolean fireSuccessEventImmediately;


	/**
	 * <p>[概 要] </p>
	 * キューを受信中のJMSConsumeCoreを識別する為の識別子を返却します。
	 *
	 * <p>[詳 細] </p>
	 * identifierフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return キューを受信中のJMSConsumeCoreを識別する為の識別子
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * <p>[概 要] </p>
	 * キューを受信中のJMSConsumeCoreを識別する為の識別子を設定します。
	 *
	 * <p>[詳 細] </p>
	 * identifierフィールドを引数identifierで設定します。
	 *
	 * <p>[備 考] </p>
	 * 同一Queueを購読するJMSConsumerCoreが複数存在する場合、このメソッドで
	 * ユニーク名を設定して下さい。
	 * 購読停止時はこのidentifierを指定することで任意のJMSConsumerCoreを
	 * 停止させることが出来ます。
	 *
	 * @param identifier キューを受信中のJMSConsumeCoreを識別する為の識別子
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * <p>[概 要] </p>
	 * consume中止を判断するフラグを返却します。
	 *
	 * <p>[詳 細] </p>
	 * unconsumeフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 開始、中止を判断するフラグ
	 */
	public boolean isUnconsume() {
		return unconsume;
	}

	/**
	 * <p>[概 要] </p>
	 * 開始、中止を判断するフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * unconsumeフィールドに引数unconsumeを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param unconsume 開始、中止を判断するフラグ
	 */
	public void setUnconsume(boolean unconsume) {
		this.unconsume = unconsume;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS接続を行う為の接続先環境設定プロパティを返却します。
	 *
	 * <p>[詳 細] </p>
	 * environmentフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMS接続を行う為の接続先環境設定プロパティ
	 */
	public Hashtable<String, String> getEnvironment() {
		if(environment == null) {
			environment = getController().getClientConfig().getDefaultJmsEnvironment();
		}

		return environment;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS接続を行う為の接続先環境設定プロパティを設定します。
	 *
	 * <p>[詳 細] </p>
	 * environmentフィールドを引数environmentで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param environment JMS接続を行う為の接続先環境設定プロパティ
	 */
	public void setEnvironment(Hashtable<String, String> environment) {
		this.environment = environment;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのキューコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * queueConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return サーバとのキューコネクション
	 */
	public QueueConnection getQueueConnection() {
		return queueConnection;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのキューコネクションを設定します。
	 *
	 * <p>[詳 細] </p>
	 * queueConnectionフィールドを引数queueConnectionで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queueConnection サーバとのキューコネクション
	 */
	public void setQueueConnection(QueueConnection queueConnection) {
		this.queueConnection = queueConnection;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのキューコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * queueConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return サーバとのキューコネクション
	 */
	public QueueSession getQueueSession() {
		return queueSession;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのキューコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * queueConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queueSession サーバとのキューコネクション
	 */
	public void setQueueSession(QueueSession queueSession) {
		this.queueSession = queueSession;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読を行うキューを返却します。
	 *
	 * <p>[詳 細] </p>
	 * queueフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 購読を行うキュー
	 */
	public Queue getQueue() {
		return queue;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読を行うキューを設定します。
	 *
	 * <p>[詳 細] </p>
	 * queueフィールドを引数queueで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queue 購読を行うキュー
	 */
	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読開始、中止対象キュー名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * queueNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 購読開始、中止対象キュー名
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読開始、中止対象キュー名を設定します。
	 *
	 * <p>[詳 細] </p>
	 * queueNameフィールドを引数queueNameで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queueName 購読開始、中止対象キュー名
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * <p>[概 要] </p>
	 * Queueを購読するオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * consumerフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return Queueを購読するオブジェクト
	 */
	public MessageConsumer getConsumer() {
		return consumer;
	}

	/**
	 * <p>[概 要] </p>
	 * Queueを購読するオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * consumerフィールドを引数consumerで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param consumer Queueを購読するオブジェクト
	 */
	public void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSコネクションファクトリJNDI名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * connectionFactoryNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMSコネクションファクトリJNDI名
	 */
	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSコネクションファクトリJNDI名を設定します。
	 *
	 * <p>[詳 細] </p>
	 * connectionFactoryNameフィールドを引数connectionFactoryNameで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param connectionFactoryName JMSコネクションファクトリJNDI名
	 */
	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	/**
	 * <p>[概 要] </p>
	 * QueuConnectionに与えるClientIdを取得します。
	 *
	 * <p>[詳 細] </p>
	 * clientIdフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return QueueConnectionに与えるClientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * <p>[概 要] </p>
	 * QueueConnectionに与えるClientIdを設定します。
	 *
	 * <p>[詳 細] </p>
	 * clientIdフィールドを引数clientIdで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param clientId QueueConnectionに与えるClientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * <p>[概 要] </p>
	 * コンシューム開始をモデル処理成功と見做すかどうかのフラグを返却します。
	 *
	 * <p>[詳 細] </p>
	 * fireSuccessEventImmediatelyフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return コンシューム開始をモデル処理成功と見做すかどうかのフラグ
	 */
	public boolean isFireSuccessEventImmediately() {
		return fireSuccessEventImmediately;
	}

	/**
	 * <p>[概 要] </p>
	 * コンシューム開始をモデル処理成功と見做すかどうかのフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * fireSuccessEventImmediatelyフィールドを引数fireSuccessEventImmediatelyで設定します。
	 * <p/>
	 *
	 * JMSConsumeCoreは、メッセージを受信するまでModelProcessEvent.SUCCESS
	 * イベントを発行しません。<br/>
	 * この為、次のような機能モデル予約がされている場合、
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	protected void reserveModels(List<Class<? extends Model>> models) {
	 *		models.add(JMSConsumeCore.class);
	 *		models.add(HTTPRequestCore.class);
	 *	}
	 * </pre>
	 *
	 * HTTPRequestCoreはJMSConsumeCoreが最初のメッセージを受信するまで実行されません。<br/>
	 * この挙動が期待するものでは無い場合、このメソッドを引数trueで呼び出して下さい。<br/>
	 * JMSSubscribeCore実行（サブスクライブ開始）後、ただちにModelProcessEvent.SUCCESSが発行され、
	 * HTTPRequestCoreがJMSメッセージ受信前に実行されるようになります。
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
	 *		switch(index == 0) {
	 *		case 0 :
	 *			((JMSConsumeCore)next).setFireSuccessEventImmediately(true);
	 *				:
	 *			break;
	 *		case 1 :
	 *			// JMSConsumeCoreのメッセージ受信を待たずに処理が到達
	 *			((HTTPRequestCore)next).setRequestUrl("http://foo.bar.com");
	 *				:
	 *			break;
	 *		}
	 * </pre>
	 * 但し、このSUCCESSイベントはメッセージを受信したイベントでは無い為、
	 * モデル処理結果resultはnullになります。
	 *
	 * <p>[備 考] </p>
	 * 同様の機構としてBaseAction#isRunModelsAndNoWait()をオーバーライドしてtrue返却する
	 * 方法が有りますが、この場合は予約されている全ての機能モデルが、前モデルのModelProcessEvent.SUCCESS
	 * を待たずに実行されるようになります。
	 *
	 * @param fireSuccessEventImmediately コンシューム開始をモデル処理成功と見做すかどうかのフラグ
	 */
	public void setFireSuccessEventImmediately(boolean fireSuccessEventImmediately) {
		this.fireSuccessEventImmediately = fireSuccessEventImmediately;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読の開始、停止をJMSConsumerManagerに委譲します。
	 *
	 * <p>[詳 細] </p>
	 * {@link JMSConsumeCoreManager#consume(String, JMSConsumeCore)}
	 * をgetQueueName()、thisを引数としてコールします。<br>
	 * mainprocを実行したJMSConsumeCoreは、getQueueName()を購読するモデルとして
	 * JMSConsumerManagerに管理されます。
	 * <p>
	 *
	 * isUnconsumeがtrueの場合、
	 * {@link JMSConsumeCoreManager#unconsume(String, String)}
	 * をgetQueueName()、getIdentifier()を引数としてコールします。<br>
	 * JMSConsumerManagerは管理しているJMSConsumeCoreの中からgetQueueName()を購読中
	 * のgetIdentifier()を持つJMSConsumeCoreを探し、購読を停止させます。
	 * getIdentifier()が指定されていない場合、getQueueName()を購読中の全JMSConsumeCore
	 * に購読を停止させます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws JMSException
	 * @throws NamingException
	 */
	@Override
	protected void mainproc() throws NamingException, JMSException  {
		if(!isUnconsume()) {
			JMSConsumeCoreManager.getInstance().consume(getQueueName(), this);
			// メッセージ受信時だけでなく、コンシューム開始時もモデル処理成功と見做す場合
			if(isFireSuccessEventImmediately()) {
				ModelProcessEvent evt = new ModelProcessEvent(this);
				evt.setResult(null);
				fireModelSuccess(evt);
			}
		}else{
			JMSConsumeCoreManager.getInstance().unconsume(getQueueName(), getIdentifier());

			// 購読中止の場合、この時点で例外が発生していなければモデル処理成功と見做す
			ModelProcessEvent evt = new ModelProcessEvent(this);
			// モデル処理結果オブジェクトは無し
			evt.setResult(null);
			// 発火
			fireModelSuccess(evt);

			fireModelFinished(new ModelProcessEvent(this));
		}
	}

	/**
	 * <p>[概 要] </p>
	 * キューメッセージ受信ハンドラです。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageListener#onMessage(Message)}を実装します。
	 * 受信したメッセージをsetResultメソッドでモデル処理結果とし、
	 * {@link #postConsume(Message)}メソッドをテンプレートコールして
	 * JMSConsumeCoreの継承モデルで受信メッセージを汎用的に加工可能にします。
	 * postConsumeの終了後、モデル処理成功イベントを発行します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param msg 受信したJMSメッセージ
	 */
	@Override
	public void onMessage(Message msg) {
		setResult(msg);

		postConsume(msg);

		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(msg);
		fireModelSuccess(successEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * consume結果受信用オーバーライドメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * 全ての受信メッセージに対して共通の処理を行う場合、
	 * このメソッドをオーバーライドした機能モデルを作成することで、
	 * メッセージ加工処理を局所化出来ます。
	 *
	 * @param msg consumeの結果受信したオブジェクト
	 */
	protected void postConsume(Message msg) {
	}

	/**
	 * <p>[概 要] </p>
	 * consume終了時にテンプレートコールされます。
	 *
	 * <p>[詳 細] </p>
	 * {@link #postUnconsume()}をコールしてModelProcessEvent.FINISHED
	 * イベントを発行します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public final void onUnconsume(){
		postUnconsume();

		ModelProcessEvent finishedEvent = new ModelProcessEvent(this);
		fireModelFinished(finishedEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * consume終了時にテンプレートコールされるオーバーライドメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * このメソッドをオーバーライドすることでUnconsume時の共通処理を記述出来ます。
	 *
	 */
	protected void postUnconsume() {
	}
}