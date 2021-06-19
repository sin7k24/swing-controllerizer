package com.oneitthing.swingcontrollerizer.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>[概 要] </p>
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class ClassNode {

	/**  */
	public static final int CONTROLLER_NODE = 0;
	/**  */
	public static final int VIEW_NODE = 1;
	/**  */
	public static final int ACTION_NODE = 2;
	/**  */
	public static final int MODEL_NODE = 3;

	/**  */
	public double x;

	/**  */
	public double y;

	/**  */
	public double dx;

	/**  */
	public double dy;

	/**  */
	public boolean fixed;

	/**  */
	private ClassNode parent;

	/**  */
	private List<ClassNode> children = new ArrayList<ClassNode>();

	/**  */
	private int nodeType = CONTROLLER_NODE;

	/**  */
	private String packagePath;

	/**  */
	private String name;

	/**  */
	private double lineLength = 150d;


	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public ClassNode getParent() {
		return parent;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parent
	 */
	public void setParent(ClassNode parent) {
		this.parent = parent;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public List<ClassNode> getChildren() {
		return children;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param children
	 */
	public void setChildren(List<ClassNode> children) {
		this.children = children;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param child
	 */
	public void addChild(ClassNode child) {
		child.setParent(this);
		this.children.add(child);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public int getNodeType() {
		return nodeType;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param nodeType
	 */
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getPackagePath() {
		return packagePath;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param packagePath
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public double getLineLength() {
		return lineLength;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param lineLength
	 */
	public void setLineLength(double lineLength) {
		this.lineLength = lineLength;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param nodeType
	 * @param packagePath
	 * @param name
	 */
	public ClassNode(int nodeType,
					String packagePath,
					String name)
	{
		this.nodeType = nodeType;
		this.packagePath = packagePath;
		this.name = name;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public int getLength() {
		return length() + 1;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	private int length() {
		int cnt = getChildren().size();

		for(Iterator<ClassNode> it = getChildren().iterator(); it.hasNext();) {
			cnt += it.next().length();
		}

		return cnt;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public ClassNode[] toArray() {
		List<ClassNode> list = new ArrayList<ClassNode>();

		list.add(this);

		list = concat(list);

		return list.toArray(new ClassNode[0]);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param list
	 * @return
	 */
	private List<ClassNode> concat(List<ClassNode> list) {
		list.addAll(getChildren());

		for(Iterator<ClassNode> it = getChildren().iterator(); it.hasNext();) {
			list = it.next().concat(list);
		}

		return list;
	}
}
