package com.oneitthing.swingcontrollerizer.model;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

/**
 * <p>[概 要] </p>
 * JMSサーバトピックにメッセージを発行する為の機能モデルです。
 *
 * <p>[詳 細] </p>
 * JMSトピックを通じてPub/Sub通信を行う為のクラスです。<br/>
 * Topicコネクションをスタートして設定されたメッセージをトピックに送信します。
 * <p>
 *
 * 必須設定メソッド
 * <ul>
 *   <li>{@link #setTopicName(String)} : メッセージ送信対象トピック名設定</li>
 *   <li>{@link #createSession()} : TopicSession作成</li>
 *   <li>{@link #setMessage(Message)} | {@link #setTextMessage(String)} : 送信メッセージ設定</li>
 * </ul>
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * チャットトピックへメッセージを送信する。
 * <pre class="samplecode">
 * package demo.chat.action;
 *
 *	import java.util.List;
 *
 *	import javax.swing.JTextField;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.JMSPublishCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *	public class RemarkSendAction extends BaseAction {
 *
 *		&#064;Override
 *		protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *			// 送信メッセージ入力欄が空の場合は処理を行いません。
 *			if("".equals(getComponentValueAsString("chatFrame.jtfRemark").trim())) {
 *				return false;
 *			}else{
 *				return true;
 *			}
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			models.add(JMSPublishCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				String remark = getComponentValueAsString("chatFrame.jtfRemark");
 *				String userName = getComponentValueAsString("chatFrame.jtfUserName");
 *
 *				// メッセージを送信するTopicのJNDI名を設定します。
 *				((JMSPublishCore)next).setTopicName("topic/chatTopic");
 *				// JMSコネクションを確立してJMSセッションを作成します。
 *				((JMSPublishCore)next).createSession();
 *				// 送信するメッセージを設定します。
 *				((JMSPublishCore)next).setTextMessage("<" + userName + "> " + remark);
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result)	throws Exception {
 *			// メッセージ正常送信後、送信メッセージ入力欄を空にします。
 *			((JTextField)getComponent("chatFrame.jtfRemark")).setText("");
 *		}
 *	}
 * </pre>
 *


 *

 */
public class JMSPublishCore extends BaseModel {

	/** JMS接続を行う為の接続先環境設定プロパティです。 */
	private Hashtable<String, String> environment;

	/** JMSコネクションファクトリJNDI名です。（デフォルト：ConnectionFactory） */
	private String connectionFactoryName = "ConnectionFactory";

	/** サーバとのトピックコネクションです。 */
	private TopicConnection topicConnection;

	/** サーバとのトピックセッションです。 */
	private TopicSession topicSession;

	/** 購読を行うトピックです。 */
	private Topic topic;

	/** メッセージ送信対象トピック名です。 */
	private String topicName;

	/** JMS Topicにメッセージを発行するオブジェクトです。 */
	private TopicPublisher publisher;

	/** 送信するテキストメッセージです。 */
	private String textMessage;

	/** JMS Topicに発行するメッセージオブジェクト */
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
	 * サーバとのトピックコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * topicConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return サーバとのトピックコネクション
	 */
	public TopicConnection getTopicConnection() {
		return topicConnection;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのトピックコネクションを設定します。
	 *
	 * <p>[詳 細] </p>
	 * topicConnectionフィールドを引数topicConnectionで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topicConnection サーバとのトピックコネクション
	 */
	public void setTopicConnection(TopicConnection topicConnection) {
		this.topicConnection = topicConnection;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのトピックコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * topicConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return サーバとのトピックコネクション
	 */
	public TopicSession getTopicSession() {
		return topicSession;
	}

	/**
	 * <p>[概 要] </p>
	 * サーバとのトピックコネクションを返却します。
	 *
	 * <p>[詳 細] </p>
	 * topicConnectionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topicSession サーバとのトピックコネクション
	 */
	public void setTopicSession(TopicSession topicSession) {
		this.topicSession = topicSession;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読を行うトピックを返却します。
	 *
	 * <p>[詳 細] </p>
	 * topicフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 購読を行うトピック
	 */
	public Topic getTopic() {
		return topic;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読を行うトピックを設定します。
	 *
	 * <p>[詳 細] </p>
	 * topicフィールドを引数topicで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topic 購読を行うトピック
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読開始、中止対象トピック名を返却します。
	 *
	 * <p>[詳 細] </p>
	 * topicNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 購読開始、中止対象トピック名
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読開始、中止対象トピック名を設定します。
	 *
	 * <p>[詳 細] </p>
	 * topicNameフィールドを引数topicNameで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topicName 購読開始、中止対象トピック名
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSトピックにメッセージを発行するオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * publisherフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMSトピックにメッセージを発行するオブジェクト
	 */
	public TopicPublisher getPublisher() {
		return publisher;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSトピックにメッセージを発行するオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * publisherフィールドを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param publisher JMSトピックにメッセージを発行するオブジェクト
	 */
	public void setPublisher(TopicPublisher publisher) {
		this.publisher = publisher;
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
	 * Textメッセージ情報はTopicSessionが無い状態でも生成出来る為、
	 * 簡易メッセージ設定メソッドとして用意されています。
	 *
	 * @param textMessage 送信するTextメッセージ
	 */
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Topicに発行するメッセージオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMS Topicに発行するメッセージオブジェクト
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Topicに発行するメッセージオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * messageフィールドを引数messageで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param message JMS Topicに発行するメッセージオブジェクト
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * <p>[概 要] </p>
	 * JMS TopicSessionを生成します。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <ul>
	 *   <li>TopicConnectionFactoryのルックアップ</li>
	 *   <li>TopicConnectionの生成</li>
	 *   <li>Topicのルックアップ</li>
	 *   <li>TopicConnectionからTopicSessionの生成</li>
	 * </ul>
	 *
	 * を行います。
	 * <p/>
	 *
	 * <p>[備 考] </p>
	 * JMSメッセージオブジェクトはTopicSessionから作成します。<br/>
	 * このメソッドはJMSSubscribeCore機能モデルがrunされる前に
	 * 呼び出しておく必要が有ります。
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
	 *		if(index == 0) {
	 *			String remark = getComponentAsString("chatFrame.jtfRemark");
	 *			String userName = getComponentAsString("chatFrame.jtfUserName");
	 *
	 *			((JMSPublishCore)next).setTopicName("topic/chatTopic");
	 *			// 発行メッセージを設定する前に要createSessionメソッドコール
	 *			((JMSPublishCore)next).createSession();
	 *			((JMSPublishCore)next).setTextMessage("<" + userName + "> " + remark);
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
		TopicConnectionFactory tcf =
			(TopicConnectionFactory)context.lookup(getConnectionFactoryName());

		setTopicConnection(tcf.createTopicConnection());
		setTopic((Topic)context.lookup(getTopicName()));
		setTopicSession(getTopicConnection().createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE));
	}

	/**
	 * <p>[概 要] </p>
	 * JMS Topicコネクションを開始してメッセージを発行します。
	 *
	 * <p>[詳 細] </p>
	 * TopicConnectionをstartさせ、getTopic()に対応するPublisherを生成します。<br>
	 * PublisherはgetMessage()をメッセージとして、JMS Topicに発行を行います。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void mainproc() throws JMSException {

		getTopicConnection().start();

		setPublisher(getTopicSession().createPublisher(getTopic()));

		// textMessageがセットされている場合は送信メッセージとして使用する
		if(getTextMessage() != null) {
			setMessage(getTopicSession().createTextMessage(getTextMessage()));
		}
		getPublisher().publish(getMessage());
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
	 * JMS Topicコネクションを切断します。
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
	 * JMS Topicコネクションを切断します。
	 *
	 * <p>[詳 細] </p>
	 * <ul>
	 *   <li>Publisherのclose</li>
	 *   <li>TopicConnectionのstop</li>
	 *   <li>TopicSessionのclose</li>
	 *   <li>TopicConnectionのclose</li>
	 * </ul>
	 * を行います。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws JMSException
	 */
	public void stop() throws JMSException {
		getPublisher().close();
		getTopicConnection().stop();
		getTopicSession().close();
		getTopicConnection().close();
	}

	/**
	 * <p>[概 要] </p>
	 * TextMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているTopicSessionにTextMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return TextMessageオブジェクト
	 * @throws JMSException
	 */
	public TextMessage createTextMessage() throws JMSException {
		TextMessage msg = getTopicSession().createTextMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * ObjectMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているTopicSessionにObjectMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return ObjectMessageオブジェクト
	 * @throws JMSException
	 */
	public ObjectMessage createObjectMessage() throws JMSException {
		ObjectMessage msg = getTopicSession().createObjectMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * MapMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているTopicSessionにMapMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return MapMessageオブジェクト
	 * @throws JMSException
	 */
	public MapMessage createMapMessage() throws JMSException {
		MapMessage msg = getTopicSession().createMapMessage();

		return msg;
	}

	/**
	 * <p>[概 要] </p>
	 * StreamMessageオブジェクトを生成します。
	 *
	 * <p>[詳 細] </p>
	 * 確立されているTopicSessionにStreamMessageオブジェクトの生成を依頼します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return StreamMessageオブジェクト
	 * @throws JMSException
	 */
	public StreamMessage createStreamMessage() throws JMSException {
		StreamMessage msg = getTopicSession().createStreamMessage();

		return msg;
	}
}

