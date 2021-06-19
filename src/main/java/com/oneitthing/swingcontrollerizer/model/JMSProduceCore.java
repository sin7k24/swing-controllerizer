package com.oneitthing.swingcontrollerizer.model;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

/**
 * <p>[概 要] </p>
 * JMSサーバキューにメッセージを発行する為の機能モデルです。
 *
 * <p>[詳 細] </p>
 * JMSキューを使用してP2P通信を行う為のクラスです。<br/>
 * Queueコネクションをスタートして設定されたメッセージをキューに送信します。
 * <p>
 *
 * 必須設定メソッド
 * <ul>
 *   <li>{@link #setQueueName(String)} : メッセージ送信対象キュー名設定</li>
 *   <li>{@link #createSession()} : QueueSession作成</li>
 *   <li>{@link #setMessage(Message)} | {@link #setTextMessage(String)} : 送信メッセージ設定</li>
 * </ul>
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * サーバ上のキューにMapMessageを送信する
 *
 * <pre class="samplecode">
 *	package demo.serverpush.action;
 *
 *	import java.util.List;
 *
 *	import javax.jms.MapMessage;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.JMSProduceCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *	public class ClientProduceAction extends BaseAction {
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			models.add(JMSProduceCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next)	throws Exception {
 *			if(index == 0) {
 *				((JMSProduceCore)next).setQueueName("queue/testQueue");
 *				((JMSProduceCore)next).createSession();
 *				MapMessage mapMessage = ((JMSProduceCore)next).createMapMessage();
 *				mapMessage.setString("index", "");
 *				mapMessage.setString("message", "クライアント");
 *				((JMSProduceCore)next).setMessage(mapMessage);
 *			}
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			System.out.println("success");
 *		}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			System.out.println("failure");
 *
 *			return e;
 *		}
 *	}
 *
 * </pre>
 *


 *

 */
public class JMSProduceCore extends BaseModel {

	/** JMS接続を行う為の接続先環境設定プロパティです。 */
	private Hashtable<String, String> environment;

	/** JMSコネクションファクトリJNDI名です。（デフォルト：ConnectionFactory） */
	private String connectionFactoryName = "ConnectionFactory";

	/** サーバとのキューコネクションです。 */
	private QueueConnection queueConnection;

	/** サーバとのキューセッションです。 */
	private QueueSession queueSession;

	/** 購読を行うキューです。 */
	private Queue queue;

	private String queueName;

	/** JMS Queueにメッセージを発行するオブジェクトです。 */
	private MessageProducer producer;

	/** 送信するテキストメッセージです。 */
	private String textMessage;

	/** JMS Queueに発行するメッセージオブジェクト */
	private Message message;

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




	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}



	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public MessageProducer getProducer() {
		return producer;
	}

	public void setProducer(MessageProducer producer) {
		this.producer = producer;
	}

	/**
	 * <p>[概 要] </p>
	 * Textメッセージを取得する簡易メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * textMessageフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 送信するTextメッセージ
	 */
	public String getTextMessage() {
		return textMessage;
	}

	/**
	 * <p>[概 要] </p>
	 * Textメッセージを設定する簡易メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * textMessageフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 * Textメッセージ情報はQueueSessionが無い状態でも生成出来る為、
	 * 簡易メッセージ設定メソッドとして用意されています。
	 *
	 * @param textMessage 送信するTextメッセージ
	 */
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Queueに発行するメッセージオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMS Queueに発行するメッセージオブジェクト
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Queueに発行するメッセージオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを引数messageで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message JMS Queueに発行するメッセージオブジェクト
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS QueueSessionを生成します。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <ul>
	 *   <li>QueueConnectionFactoryのルックアップ</li>
	 *   <li>QueueConnectionの生成</li>
	 *   <li>Queueのルックアップ</li>
	 *   <li>QueueConnectionからQueueSessionの生成</li>
	 * </ul>
	 *
	 * を行います。
	 * <p/>
	 *
	 * <p>[備 考] </p>
	 * JMSメッセージオブジェクトはQueueSessionから作成します。<br/>
	 * このメソッドはJMSConsumeCore機能モデルがrunされる前に
	 * 呼び出しておく必要が有ります。
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
	 *
	 *		if(index == 0) {
	 *			((JMSProduceCore)next).setQueueName("queue/testQueue");
	 *			// 要コール
	 *			((JMSProduceCore)next).createSession();
	 *			MapMessage mapMessage = ((JMSProduceCore)next).createMapMessage();
	 *			mapMessage.setString("index", "");
	 *			mapMessage.setString("message", "クライアント");
	 *			((JMSProduceCore)next).setMessage(mapMessage);
	 *		}
	 *		return true;
	 *	}
	 * </pre>
	 *
	 * @throws NamingException
	 * @throws JMSException
	 */
	public void createSession() throws NamingException, JMSException {
		InitialContext context = new InitialContext(getEnvironment());
		QueueConnectionFactory qcf =
			(QueueConnectionFactory)context.lookup(getConnectionFactoryName());

		setQueueConnection(qcf.createQueueConnection());
		setQueue((Queue)context.lookup(getQueueName()));
		setQueueSession(getQueueConnection().createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE));
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Queueコネクションを開始してメッセージを発行します。
	 *
	 * <p>[詳 細] </p>
	 * QueueConnectionをstartさせ、getQueue()に対応するPublisherを生成します。<br>
	 * PublisherはgetMessage()をメッセージとして、JMS Queueに発行を行います。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void mainproc() throws JMSException {

		getQueueConnection().start();

		setProducer(getQueueSession().createProducer(getQueue()));

		// textMessageがセットされている場合は送信メッセージとして使用する
		if(getTextMessage() != null) {
			setMessage(getQueueSession().createTextMessage(getTextMessage()));
		}
		getProducer().send(getMessage());
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理成功イベント、モデル処理終了イベントを発行します。
	 *
	 * <p>[詳 細] </p>
	 * fireModelSuccess、fireModelFinishedメソッドをコールします。
	 *
	 * <p>[備 考] </p>
	 */
	@Override
	protected void postproc() throws Exception {
		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(getResult());
		fireModelSuccess(successEvent);

		fireModelFinished(new ModelProcessEvent(this));
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Queueコネクションを切断します。
	 *
	 * <p>[詳 細] </p>
	 * stopメソッドに処理委譲します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	public void done() throws Exception{
		stop();
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Queueコネクションを切断します。
	 *
	 * <p>[詳 細] </p>
	 * <ul>
	 *   <li>Publisherのclose</li>
	 *   <li>QueueConnectionのstop</li>
	 *   <li>QueueSessionのclose</li>
	 *   <li>QueueConnectionのclose</li>
	 * </ul>
	 * を行います。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws JMSException
	 */
	public void stop() throws JMSException {
		getProducer().close();
		getQueueConnection().stop();
		getQueueSession().close();
		getQueueConnection().close();
	}

	/**
	 * <p>[概 要] </p>
	 * TextMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているQueueSessionにTextMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return TextMessageオブジェクト
	 * @throws JMSException
	 */
	public TextMessage createTextMessage() throws JMSException {
		TextMessage msg = getQueueSession().createTextMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * ObjectMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているQueueSessionにObjectMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return ObjectMessageオブジェクト
	 * @throws JMSException
	 */
	public ObjectMessage createObjectMessage() throws JMSException {
		ObjectMessage msg = getQueueSession().createObjectMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * MapMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているQueueSessionにMapMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return MapMessageオブジェクト
	 * @throws JMSException
	 */
	public MapMessage createMapMessage() throws JMSException {
		MapMessage msg = getQueueSession().createMapMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * StreamMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているQueueSessionにStreamMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return StreamMessageオブジェクト
	 * @throws JMSException
	 */
	public StreamMessage createStreamMessage() throws JMSException {
		StreamMessage msg = getQueueSession().createStreamMessage();

		return msg;
	}
}

