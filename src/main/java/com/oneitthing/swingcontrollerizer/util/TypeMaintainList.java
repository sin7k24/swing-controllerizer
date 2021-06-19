package com.oneitthing.swingcontrollerizer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>[概 要] </p>
 * プリミティブの型情報を維持するリストクラスです。
 * 
 * <p>[詳 細] </p>
 * プリミティブ変数がCollectionに追加される際、オートボクシングによって
 * 失われる型情報を維持します。<br>
 * 
 * リストに追加される要素をTypeWrap内部クラスにラップして、値と型情報を保持します。<br>
 * 要素を取得する際、TypeWrapクラスのtype情報を参照することで、オートボクシング前の
 * 型情報を参照出来ます。<br>
 * 
 * <pre class="samplecode">
 * 		TypeMaintainList list = new TypeMaintainList();
 *		list.add("string");
 *		list.add(96);
 *		list.add((char)96);
 *		list.add((short)96);
 *		list.add((long)96);
 *		list.add((float)96);
 *		list.add((double)96); 
 *
 *	 	Class[] types = list.toTypeArray();
 *		for(Class t : types) {
 *			System.out.println(t);
 *		}
 * </pre>
 * 実行結果
 * <pre class="samplecode">
 * class java.lang.String
 * int
 * char
 * short
 * long
 * float
 * double
 * </pre>
 * 
 * <p>[備 考] </p>
 * 
 * 


 * 
  
 */
public class TypeMaintainList extends ArrayList<Object> {

	private static final long serialVersionUID = -1732988293481239710L;

	/**
	 * <p>[概 要] </p>
	 * リストにchar要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * char型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(char v) {
		TypeWrap wrap = new TypeWrap(v, char.class);
		return super.add(wrap);
	}

	/**
	 * <p>[概 要] </p>
	 * リストにshort要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * short型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(short v) {
		TypeWrap wrap = new TypeWrap(v, short.class);
		return super.add(wrap);
	}

	/**
	 * <p>[概 要] </p>
	 * リストにint要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * int型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(int v) {
		TypeWrap wrap = new TypeWrap(v, int.class);
		return super.add(wrap);
	}

	/**
	 * <p>[概 要] </p>
	 * リストにlong要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * long型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(long v) {
		TypeWrap wrap = new TypeWrap(v, long.class);
		return super.add(wrap);
	}

	/**
	 * <p>[概 要] </p>
	 * リストにfloat要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * float型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(float v) {
		TypeWrap wrap = new TypeWrap(v, float.class);
		return super.add(wrap);
	}
	
	/**
	 * <p>[概 要] </p>
	 * リストにdouble要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * double型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(double v) {
		TypeWrap wrap = new TypeWrap(v, double.class);
		return super.add(wrap);
	}
	
	/**
	 * <p>[概 要] </p>
	 * リストにboolean要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * boolean型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	public boolean add(boolean v) {
		TypeWrap wrap = new TypeWrap(v, boolean.class);
		return super.add(wrap);
	}
	
	/**
	 * <p>[概 要] </p>
	 * リストにObject要素を追加します。
	 * 
	 * <p>[詳 細] </p>
	 * Object型情報を保持してリストに追加します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param v 追加要素
	 * @return true : リスト追加成功、 false : リスト追加失敗
	 */
	@Override
	public boolean add(Object v) {
		TypeWrap wrap = new TypeWrap(v, v.getClass());
		return super.add(wrap);
	}
	
	/**
	 * <p>[概 要] </p>
	 * index番目の要素がプリミティブ変数であるかどうか調べます。
	 * 
	 * <p>[詳 細] </p>
	 * index番目のTypeWrap要素を取り出して、getType().isPrimitive()
	 * の返却値を返却します。
	 * 
	 * <p>[備 考] </p>
	 *  
	 * @param index 要素のインデックス
	 * @return true : プリミティブ、 false : オブジェクト
	 */
	public boolean isPrimitive(int index) {
		TypeWrap wrap = (TypeWrap)get(index);
		return wrap.getType().isPrimitive();
	}

	/**
	 * <p>[概 要] </p>
	 * 追加された要素群の型情報配列を返却します。
	 * 
	 * <p>[詳 細] </p>
	 * TypeWrap要素を取り出して、getType()メソッドの返却値を配列化、返却します。
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @return 要素群の型情報配列
	 */
	@SuppressWarnings("unchecked")
	public Class[] toTypeArray() {
		List<Class> typeList = new ArrayList<Class>();
		
		for(int i=0; i<size(); i++) {
			TypeWrap wrap = (TypeWrap)get(i);
			typeList.add(wrap.getType());
		}
		
		return typeList.toArray(new Class[0]);
	}
	
	/**
	 * <p>[概 要] </p>
	 * 追加された要素群の値配列を返却します。
	 * 
	 * <p>[詳 細] </p>
	 * TypeWrap要素を取り出して、getValue()メソッドの返却値を配列化、返却します。
	 * 
	 * <p>[備 考] </p>
	 * 要素値はオートボクシングされます。
	 * 
	 * @return 要素群の値配列
	 */
	public Object[] toValueArray() {
		List<Object> valueList = new ArrayList<Object>();
		
		for(int i=0; i<size(); i++) {
			TypeWrap wrap = (TypeWrap)get(i);
			valueList.add(wrap.getValue());
		}
		
		return valueList.toArray(new Object[0]);
	}

	/**
	 * <p>[概 要] </p>
	 * 値と型情報を保持する内部クラスです。
	 * 
	 * <p>[詳 細] </p>
	 * TypeMaintainListの要素になります。
	 * 
	 * <p>[備 考] </p>
	 *
	 */
	@SuppressWarnings("unchecked")
	private class TypeWrap{
		
		/** 要素の値です。 */
		private Object value;
		
		/** 要素の型情報です。 */
		private Class type;
		
		/**
		 * <p>[概 要] </p>
		 * 要素の値を取得します。
		 * 
		 * <p>[詳 細] </p>
		 * valueフィールドを返却します。
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @return 要素の値
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * <p>[概 要] </p>
		 * 要素の型情報を取得します。
		 * 
		 * <p>[詳 細] </p>
		 * typeフィールドを返却します。
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @return 要素の型情報
		 */
		public Class getType() {
			return type;
		}

		/**
		 * <p>[概 要] </p>
		 * コンストラクタです。
		 * 
		 * <p>[詳 細] </p>
		 * 値と型情報を保持するインスタンスを生成します。
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @param value 要素の値
		 * @param type 要素の型情報
		 */
		public TypeWrap(Object value, Class type) {
			this.value = value;
			this.type = type;
		}
	}
}


