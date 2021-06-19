package com.oneitthing.swingcontrollerizer.model;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.manager.JMSSubscribeCoreManager;

/**
 * <p>[概 要] </p>
 * JMSサーバトピックを購読する為の機能モデルクラスです。
 *
 * <p>[詳 細] </p>
 * JMSトピックを通じてPub/Sub通信を行う為のクラスです。<br/>
 * Topicコネクションをスタートしてトピックへ送信されたメッセージを受信します。<br/>
 * 購読を開始すると、明示的に購読中止を行わない限り、継続してメッセージを受信し続け、
 * 受信する毎にモデル処理成功イベント（ModelProcessEvent.SUCCESS）を発行します。<br>
 * <p>
 *
 * 必須設定メソッド
 * <ul>
 *   <li>{@link #setTopicName(String)} : メッセージ送信対象トピック名設定</li>
 * </ul>
 *
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * チャットトピックのメッセージを購読する。
 * <pre class="samplecode">
 *    package demo.chat.action;
 *
 *    import java.util.List;
 *
 *    import javax.jms.TextMessage;
 *    import javax.swing.JTextArea;
 *
 *    import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *    import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *    import com.oneitthing.swingcontrollerizer.model.JMSSubscribeCore;
 *    import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *    public class EnterChannelAction extends BaseAction {
 *
 *        &#064;Override
 *        protected void reserveModels(List<Class<? extends Model>> models) {
 *            models.add(JMSSubscribeCore.class);
 *        }
 *
 *        &#064;Override
 *        public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *            if(index == 0) {
 *                ((JMSSubscribeCore)next).setTopicName("topic/chatTopic");
 *            }
 *            return true;
 *        }
 *
 *        &#064;Override
 *        public void successForward(int index, Model model, Object result)    throws Exception {
 *            String remark = ((TextMessage)result).getText();
 *
 *            JTextArea jtaLog = (JTextArea)getComponent("chatFrame.jtaLog");
 *            jtaLog.append(remark + System.getProperty("line.separator"));
 *        }
 *    }
 * </pre>
 *
 * 任意のトピックメッセージを3回受信したら購読を中止する。
 * <pre class="samplecode">
 *    package demo.chat.action;
 *
 *    import java.util.List;
 *
 *    import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *    import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *    import com.oneitthing.swingcontrollerizer.manager.JMSSubscribeCoreManager;
 *    import com.oneitthing.swingcontrollerizer.model.JMSSubscribeCore;
 *    import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *    public class SpecificTimeSubscribeAction extends BaseAction {
 *
 *        &#064;Override
 *        protected void reserveModels(List<Class<? extends Model>> models) {
 *            models.add(JMSSubscribeCore.class);
 *        }
 *
 *        &#064;Override
 *        public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *            if(index == 0) {
 *                ((JMSSubscribeCore)next).setTopicName("topic/dataTopic");
 *                ((JMSSubscribeCore)next).setIdentifier(String.valueOf(hashCode()));
 *            }
 *            return true;
 *        }
 *
 *        &#064;Override
 *        public void successForward(int index, Model model, Object result) throws Exception {
 *            System.out.println("受信したトピックメッセージ : " + result);
 *
 *            if(((JMSSubscribeCore)model).getSuccessCount() == 3) {
 *                JMSSubscribeCoreManager.getInstance().unsubscribe("topic/dataTopic", String.valueOf(hashCode()));
 *            }
 *        }
 *
 *        &#064;Override
 *        public void complete(ParameterMapping parameterMapping) throws Exception {
 *            System.out.println("受信終了");
 *        }
 *    }
 * </pre>
 *


 *

 */
public class JMSSubscribeCore extends BaseModel implements MessageListener {

	/** トピックを受信中のJMSSubscribeCoreを識別する為の識別子です。 */
	private String identifier;

	/** 購読中止を判断するフラグです。 */
	private boolean unsubscribe;

	/** JMS接続を行う為の接続先環境設定プロパティです。 */
	private Hashtable<String, String> environment;

	/** サーバとのトピックコネクションです。 */
	private TopicConnection topicConnection;

	/** サーバとのトピックセッションです。 */
	private TopicSession topicSession;

	/** 購読を行うトピックです。 */
	private Topic topic;

	/** 購読開始、中止対象トピック名です。 */
	private String topicName;

	/** JMS Topicのメッセージを購読するオブジェクトです。 */
	private TopicSubscriber subscriber;

	/** JMSコネクションファクトリJNDI名です。（デフォルト：ConnectionFactory） */
	private String connectionFactoryName = "ConnectionFactory";

	/** TopicConnectionに与えるClientIdです。 */
	private String clientId;

	/** TopicSubscriberをdurableモードで作るかどうかのフラグです。 */
	private boolean durable;

	/** durableモードで作られたTopicSubscriberの名前です。 */
	private String durableSubscriberName;

	/** Subscribe開始をモデル処理成功と見做すかどうかのフラグです。 */
	private boolean fireSuccessEventImmediately;


	/**
	 * <p>[概 要] </p>
	 * トピックを受信中のJMSSubscribeCoreを識別する為の識別子を返却します。
	 *
	 * <p>[詳 細] </p>
	 * identifierフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return トピックを受信中のJMSSubscribeCoreを識別する為の識別子
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * <p>[概 要] </p>
	 * トピックを受信中のJMSSubscribeCoreを識別する為の識別子を設定します。
	 *
	 * <p>[詳 細] </p>
	 * identifierフィールドを引数identifierで設定します。
	 *
	 * <p>[備 考] </p>
	 * 同一Topicを購読するJMSSubscriberCoreが複数存在する場合、このメソッドで
	 * ユニーク名を設定して下さい。
	 * 購読停止時はこのidentifierを指定することで任意のJMSSubscriberCoreを
	 * 停止させることが出来ます。
	 *
	 * @param identifier トピックを受信中のJMSSubscribeCoreを識別する為の識別子
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * <p>[概 要] </p>
	 * 開始、中止を判断するフラグを返却します。
	 *
	 * <p>[詳 細] </p>
	 * unsubscribeフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 開始、中止を判断するフラグ
	 */
	public boolean isUnsubscribe() {
		return unsubscribe;
	}

	/**
	 * <p>[概 要] </p>
	 * 購読中止を判断するフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * unsubscribeフィールドに引数unsubscribeを設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param unsubscribe 購読中止を判断するフラグ
	 */
	public void setUnsubscribe(boolean unsubscribe) {
		this.unsubscribe = unsubscribe;
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
	 * Topicを購読するオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * subscriberフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return Topicを購読するオブジェクト
	 */
	public TopicSubscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * <p>[概 要] </p>
	 * Topicを購読するオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * subscriberフィールドを引数subscriberで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param subscriber Topicを購読するオブジェクト
	 */
	public void setSubscriber(TopicSubscriber subscriber) {
		this.subscriber = subscriber;
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
	 * TopicConnectionに与えるClientIdを取得します。
	 *
	 * <p>[詳 細] </p>
	 * clientIdフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return TopicConnectionに与えるClientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * <p>[概 要] </p>
	 * TopicConnectionに与えるClientIdを設定します。
	 *
	 * <p>[詳 細] </p>
	 * clientIdフィールドを引数clientIdで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param clientId TopicConnectionに与えるClientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * <p>[概 要] </p>
	 * TopicSubscriberをdurableモードで作るかどうかのフラグを取得します。
	 *
	 * <p>[詳 細] </p>
	 * durableフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 * デフォルトはfalseです。
	 *
	 * @return TopicSubscriberをdurableモードで作るかどうかのフラグ
	 */
	public boolean isDurable() {
		return durable;
	}

	/**
	 * <p>[概 要] </p>
	 * TopicSubscriberをdurableモードで作るかどうかのフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * durableフィールドを引数durableで設定します。
	 *
	 * <p>[備 考] </p>
	 * デフォルトはfalseです。
	 *
	 * @param durable TopicSubscriberをdurableモードで作るかどうかのフラグ
	 */
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	/**
	 * <p>[概 要] </p>
	 * durableモードで作られたTopicSubscriberの名前を取得します。
	 *
	 * <p>[詳 細] </p>
	 * durableSubscriberNameフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return durableモードで作られたTopicSubscriberの名前
	 */
	public String getDurableSubscriberName() {
		return durableSubscriberName;
	}

	/**
	 * <p>[概 要] </p>
	 * durableモードで作られたTopicSubscriberの名前を設定します。
	 *
	 * <p>[詳 細] </p>
	 * durableSubscriberNameフィールドを引数durableSubscriberNameで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param durableSubscriberName durableモードで作られたTopicSubscriberの名前
	 */
	public void setDurableSubscriberName(String durableSubscriberName) {
		this.durableSubscriberName = durableSubscriberName;
	}

	/**
	 * <p>[概 要] </p>
	 * Subscribe開始をモデル処理成功と見做すかどうかのフラグを返却します。
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
	 * Subscribe開始をモデル処理成功と見做すかどうかのフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * fireSuccessEventImmediatelyフィールドを引数fireSuccessEventImmediatelyで設定します。
	 * <p/>
	 *
	 * JMSSubscribeCoreは、メッセージを受信するまでModelProcessEvent.SUCCESS
	 * イベントを発行しません。<br/>
	 * この為、次のような機能モデル予約がされている場合、
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	protected void reserveModels(List<Class<? extends Model>> models) {
	 *		models.add(JMSSubscribeCore.class);
	 *		models.add(HTTPRequestCore.class);
	 *	}
	 * </pre>
	 *
	 * HTTPRequestCoreはJMSSubscribeCoreが最初のメッセージを受信するまで実行されません。<br/>
	 * この挙動が期待するものでは無い場合、このメソッドを引数trueで呼び出して下さい。<br/>
	 * JMSSubscribeCore実行（サブスクライブ開始）後、ただちにModelProcessEvent.SUCCESSが発行され、
	 * HTTPRequestCoreがJMSメッセージ受信前に実行されるようになります。
	 *
	 * <pre class="samplecode">
	 *	&#064;Override
	 *	public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
	 *		switch(index == 0) {
	 *		case 0 :
	 *			((JMSSubscribeCore)next).setFireSuccessEventImmediately(true);
	 *				:
	 *			break;
	 *		case 1 :
	 *			// JMSSubscribeCoreのメッセージ受信を待たずに処理が到達
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
	 * 購読の開始、停止をJMSSubscriberManagerに委譲します。
	 *
	 * <p>[詳 細] </p>
	 * {@link JMSSubscribCoreManager#subscribe(String, JMSSubscribeCore)}
	 * をgetTopicName()、thisを引数としてコールします。<br>
	 * mainprocを実行したJMSSubscribeCoreは、getTopicName()を購読するモデルとして
	 * JMSSubscriberManagerに管理されます。
	 * <p>
	 *
	 * isUnsubscribeがtrueの場合、
	 * {@link JMSSubscribCoreManager#unsubscribe(String, String)}
	 * をgetTopicName()、getIdentifier()を引数としてコールします。<br>
	 * JMSSubscriberManagerは管理しているJMSSubscribeCoreの中からgetTopicName()を購読中
	 * のgetIdentifier()を持つJMSSubscribeCoreを探し、購読を停止させます。
	 * getIdentifier()が指定されていない場合、getTopicName()を購読中の全JMSSubscribeCore
	 * に購読を停止させます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws JMSException
	 * @throws NamingException
	 */
	@Override
	protected void mainproc() throws NamingException, JMSException  {
		if(!isUnsubscribe()) {
			JMSSubscribeCoreManager.getInstance().subscribe(getTopicName(), this);
			// メッセージ受信時だけでなく、Subscribe開始時もモデル処理成功と見做す場合
			if(isFireSuccessEventImmediately()) {
				ModelProcessEvent evt = new ModelProcessEvent(this);
				evt.setResult(null);
				fireModelSuccess(evt);
			}
		}else{
			JMSSubscribeCoreManager.getInstance().unsubscribe(getTopicName(), getIdentifier());
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
	 * トピックメッセージ受信ハンドラです。
	 *
	 * <p>[詳 細] </p>
	 * {@link MessageListener#onMessage(Message)}を実装します。
	 * 受信したメッセージをsetResultメソッドでモデル処理結果とし、
	 * {@link #postSubscribe(Message)}メソッドをテンプレートコールして
	 * JMSSubscribeCoreの継承モデルで受信メッセージを汎用的に加工可能にします。
	 * postSubscribeの終了後、モデル処理成功イベントを発行します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param msg 受信したJMSメッセージ
	 */
	@Override
	public final void onMessage(Message msg) {
		setResult(msg);

		postSubscribe(msg);

		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(msg);
		fireModelSuccess(successEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * subscribe結果受信用オーバーライドメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * 全ての受信メッセージに対して共通の処理を行う場合、
	 * このメソッドをオーバーライドした機能モデルを作成することで、
	 * メッセージ加工処理を局所化出来ます。
	 *
	 * @param msg subscribeの結果受信したオブジェクト
	 */
	protected void postSubscribe(Message msg) {
	}

	/**
	 * <p>[概 要] </p>
	 * subscribe終了時にテンプレートコールされます。
	 *
	 * <p>[詳 細] </p>
	 * {@link #postUnsubscribe()}をコールしてModelProcessEvent.FINISHED
	 * イベントを発行します。
	 *
	 * <p>[備 考] </p>
	 */
	public final void onUnsubscribe() {
		postUnsubscribe();

		ModelProcessEvent finishedEvent = new ModelProcessEvent(this);
		fireModelFinished(finishedEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * subscribe終了時にテンプレートコールされるオーバーライドメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * このメソッドをオーバーライドすることでUnsubscribe時の共通処理を記述出来ます。
	 *
	 */
	protected void postUnsubscribe() {
	}
}