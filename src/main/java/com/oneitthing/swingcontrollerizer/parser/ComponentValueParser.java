package com.oneitthing.swingcontrollerizer.parser;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import com.oneitthing.swingcontrollerizer.common.exception.CoreLogicException;

/**
 * <p>[概 要] </p>
 * 各種コンポーネントが持つ値を包括的に取得する為のパーサです。
 *
 * <p>[詳 細] </p>
 * 各種コンポーネントは値の保持の仕方が異なります。<br>
 * （textFieldコンポーネントはtext属性値を値として保持し、listBoxコンポーネントは
 * selected属性がついているlistItem子コンポーネントのtext属性値を値として保持する等）<p>
 *
 * このクラスを使用してパースすることで、統一的な値へのアクセスと、
 * 共通のオブジェクトによる値の保持が行われます。
 *
 * <p>[備 考] </p>
 *
 * このクラスのperseメソッドはElementの種類を意識せず、
 * 単一の方法で値を取る為の手法を提供します。
 * <P>
 *
 * <B>NexawebコンポーネントとSwingControllerizerが提供するValuePaserの対応表</B>
 * 	<TABLE border="1">
 * 		<TR>
 * 			<TD>コンポーネント種</TD>
 * 			<TD>対応する値取得パーサクラス</TD>
 * 			<TD>値取得の対象</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>button</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.ButtonValueParser</TD>
 * 			<TD>text属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>label</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.LabelValueParser</TD>
 * 			<TD>text属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>passwordField</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.PasswordFieldValueParser</TD>
 * 			<TD>text属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>textField</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.TextFieldValueParser</TD>
 * 			<TD>text属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>checkBox</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.CheckBoxValueParser</TD>
 * 			<TD>selected="true"属性値があれば"true"、なければ"false"</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>radioButton</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.RadioButtonValueParser</TD>
 * 			<TD>selected="true"属性値があれば"true"、なければ"false"</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>comboBox</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.ComboBoxValueParser</TD>
 * 			<TD>selected="true"属性値がついたlistBox/listItemコンポーネントのvalue属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>listBox</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.ListBoxValueParser</TD>
 * 			<TD>selected="true"属性値がついたlistItemコンポーネントのvalue属性値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>horizontalSlider</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.HorizontalSliderValueParser</TD>
 * 			<TD>position属性値、なければ"0"</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>verticalSlider</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.VerticalSliderValueParser</TD>
 * 			<TD>position属性値、なければ"0"</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>textArea</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.TextAreaValueParser</TD>
 * 			<TD>テキストノード値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>textView</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.TextViewValueParser</TD>
 * 			<TD>テキストノード値</TD>
 * 		</TR>
 * 		<TR>
 * 			<TD>table</TD>
 * 			<TD>com.oneitthing.swingcontrollerizer.client.nexaweb.parser.TableValueParser</TD>
 * 			<TD>row/cellコンポーネントのtext属性値</TD>
 * 		</TR>
 * 	</TABLE>
 *


 *

 */
public class ComponentValueParser implements Parser{

	/** コンポーネント種類毎に用意されたパーサ群をマッピングします。 */
	public static final Map<Class<? extends Component>, Class<? extends Parser>> PARSERS;

	static{
		// PARSERS static領域を初期化。フレームワークが提供するValuePaser群を登録。
		PARSERS = new HashMap<Class<? extends Component>, Class<? extends Parser>>();
		PARSERS.put(JButton.class, JButtonValueParser.class);
		PARSERS.put(JCheckBox.class, JCheckBoxValueParser.class);
		PARSERS.put(JComboBox.class, JComboBoxValueParser.class);
		PARSERS.put(JList.class, JListValueParser.class);
		PARSERS.put(JLabel.class, JLabelValueParser.class);
		PARSERS.put(JPasswordField.class, JPasswordFieldValueParser.class);
		PARSERS.put(JSlider.class, JSliderValueParser.class);
		PARSERS.put(JSpinner.class, JSpinnerValueParser.class);
		PARSERS.put(JTextArea.class, JTextAreaValueParser.class);
		PARSERS.put(JTextField.class, JTextFieldValueParser.class);
		PARSERS.put(JTextPane.class, JTextPaneValueParser.class);
		PARSERS.put(JToggleButton.class, JToggleButtonValueParser.class);
		PARSERS.put(JRadioButton.class, JRadioButtonValueParser.class);
	}

	/**
	 * <p>[概 要] </p>
	 * Swingコンポーネントの値解析を行うメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 引数elementのローカル名を元にPARSERSからParserクラスを取り出し
	 * インスタンス化、parseメソッドを引数elementで呼び出します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component 解析対象コンポーネント
	 */
	public ComponentValues parse(Component component) throws Exception{
		ComponentValues ret = null;
		try{
			Class<? extends Component> type = component.getClass();
			Parser parser = null;
			if(PARSERS.containsKey(type)){
				parser = (Parser)((Class<? extends Parser>)PARSERS.get(type)).newInstance();
			}else{
				return null;
			}

			ret = (ComponentValues)parser.parse(component);
		}catch(IllegalAccessException e){
			throw new CoreLogicException("EFC0011");
		}catch(InstantiationException e){
			throw new CoreLogicException("EFC0012");
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 任意のParserを追加します。
	 *
	 * <p>[詳 細] </p>
	 * 各コンポーネント名に対応する、値パーサマップに新規、上書き追加します。<br>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param elementName 解析対象コンポーネント名
	 * @param parser 解析オブジェクト
	 */
	public static void addParser(Class<? extends Component> elementName,
									Class<? extends Parser> parser)
	{
		PARSERS.put(elementName, parser);
	}
}
