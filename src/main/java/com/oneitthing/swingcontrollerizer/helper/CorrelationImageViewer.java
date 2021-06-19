package com.oneitthing.swingcontrollerizer.helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;

import com.oneitthing.swingcontrollerizer.controller.BaseController;


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
public class CorrelationImageViewer extends Panel implements Runnable, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -911530005188004749L;

	public ClassNode[] classNodes;

	Thread relaxer;
	boolean stress;

	int numMouseButtonsDown = 0;

	public BaseController controller;


	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public CorrelationImageViewer() {
		addMouseListener(this);

		ClassNode controller =
			new ClassNode(ClassNode.CONTROLLER_NODE, "dummy", "DummyController");
		controller.fixed = true;


		this.classNodes = controller.toArray();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public void buildNodes(BaseController controller) throws SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
		ClassNode controllerNode = CorrelationBuilder.build(controller);
		this.classNodes = controllerNode.toArray();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void run() {
		Thread me = Thread.currentThread();
		while (relaxer == me) {
			relax();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	synchronized void relax() {
		for (int i = 1; i < classNodes.length; i++) {
			ClassNode classNode = classNodes[i];
			double vx = classNode.getParent().x - classNode.x;
			double vy = classNode.getParent().y - classNode.y;
			double len = Math.sqrt(vx * vx + vy * vy);
			len = (len == 0) ? .0001 : len;
			double f = (classNode.getLineLength() - len) / (len * 3);
			double dx = f * vx;
			double dy = f * vy;

			classNode.getParent().x = classNode.getParent().x + dx;
			classNode.getParent().y = classNode.getParent().y + dy;
			classNode.x = classNode.x - dx;
			classNode.y = classNode.y - dy;
		}

		for (int i = 1; i < classNodes.length; i++) {
			ClassNode n1 = classNodes[i];
			double dx = 0;
			double dy = 0;

			for (int j = 0; j < classNodes.length; j++) {
				if (i == j) {
					continue;
				}
				ClassNode n2 = classNodes[j];
				double vx = n1.x - n2.x;
				double vy = n1.y - n2.y;
				double len = vx * vx + vy * vy;
				if (len == 0) {
					dx += Math.random();
					dy += Math.random();
				} else if (len < 100 * 100) {
					dx += vx / len;
					dy += vy / len;
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0) {
				dlen = Math.sqrt(dlen) / 2;
				n1.x = n1.x + (dx / dlen);
				n1.y = n1.y + (dy / dlen);
			}
		}

		Dimension d = getSize();
		for (int i = 1; i < classNodes.length; i++) {
			ClassNode n = classNodes[i];
			if (!n.fixed) {
				n.x += Math.max(-5, Math.min(5, n.dx));
				n.y += Math.max(-5, Math.min(5, n.dy));
			}
			if (n.x < 0) {
				n.x = 0;
			} else if (n.x > d.width) {
				n.x = d.width;
			}
			if (n.y < 0) {
				n.y = 0;
			} else if (n.y > d.height) {
				n.y = d.height;
			}
			n.dx /= 2;
			n.dy /= 2;
		}
		repaint();
	}

	ClassNode pick;
	boolean pickfixed;
	Image offscreen;
	Dimension offscreensize;
	Graphics offgraphics;

	final Color controllerNodeColor = new Color(255, 100, 100);
	final Color viewNodeColor = new Color(230, 230, 230);
	final Color actionNodeColor = new Color(230, 200, 125);
	final Color modelNodeColor = new Color(125, 230, 125);

	final Color fixedColor = new Color(255, 100, 100);
	final Color selectColor = Color.pink;
	final Color nodeColor = new Color(250, 220, 100);
	final Color stressColor = Color.darkGray;
	final Color arcColor1 = Color.black;
	final Color arcColor2 = Color.pink;
	final Color arcColor3 = Color.red;

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public synchronized void update(Graphics g) {
		Dimension d = getSize();
		if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
			offscreen = createImage(d.width, d.height);
			offscreensize = d;
			if (offgraphics != null) {
				offgraphics.dispose();
			}
			offgraphics = offscreen.getGraphics();
			offgraphics.setFont(getFont());
		}

		offgraphics.setColor(getBackground());
		offgraphics.fillRect(0, 0, d.width, d.height);
		for (int i = 1; i < classNodes.length; i++) {
			ClassNode classNode = classNodes[i];
			int x1 = (int) classNode.x;
			int y1 = (int) classNode.y;
			int x2 = (int) classNode.getParent().x;
			int y2 = (int) classNode.getParent().y;
			int len = (int) Math.abs(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) - classNode.getLineLength());
			offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3));
			offgraphics.drawLine(x1, y1, x2, y2);
			if (stress) {
//				String lbl = String.valueOf(len);
				String lbl = "actionPerformed";
				offgraphics.setColor(stressColor);
				offgraphics.drawString(lbl, x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
			}
		}

		FontMetrics fm = offgraphics.getFontMetrics();

		for (int i = 0; i < classNodes.length; i++) {
			ClassNode classNode = classNodes[i];
			if(!firstRendering) {
				classNode.x = d.width / 2;
				classNode.y = d.height / 2;
			}
			paintNode(offgraphics, classNodes[i], fm);
		}
		g.drawImage(offscreen, 0, 0, null);
		firstRendering = true;

	}

	boolean firstRendering = false;

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param g
	 * @param n
	 * @param fm
	 */
	public void paintNode(Graphics g, ClassNode n, FontMetrics fm) {
		int x = (int) n.x;
		int y = (int) n.y;
		g.setColor((n == pick) ? selectColor : (n.getParent() == null ? fixedColor : nodeColor));

		if(n.getNodeType() == ClassNode.CONTROLLER_NODE) {
			g.setColor(controllerNodeColor);
			Dimension d = getSize();
			n.x = d.getWidth() / 2;
			n.y = d.getHeight() / 2;

		}else if(n.getNodeType() == ClassNode.VIEW_NODE) {
			g.setColor(viewNodeColor);
		}else if(n.getNodeType() == ClassNode.ACTION_NODE) {
			g.setColor(actionNodeColor);
		}else if(n.getNodeType() == ClassNode.MODEL_NODE) {
			g.setColor(modelNodeColor);
		}
		int w = fm.stringWidth(n.getName()) + 10;
		int h = fm.getHeight() + 4;
		g.fillRect(x - w / 2, y - h / 2, w, h);
		g.setColor(Color.black);
		g.drawRect(x - w / 2, y - h / 2, w - 1, h - 1);
		g.drawString(n.getName(), x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());
	}


	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mousePressed(MouseEvent e) {
		numMouseButtonsDown++;
		addMouseMotionListener(this);
		double bestdist = Double.MAX_VALUE;

		int x = e.getX();
		int y = e.getY();
		for (int i = 0; i < classNodes.length; i++) {
			ClassNode n = classNodes[i];
			double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
			if (dist < bestdist) {
				pick = n;
				bestdist = dist;
			}
		}
		pickfixed = pick.fixed;
		pick.fixed = true;
		pick.x = x;
		pick.y = y;

		repaint();
		e.consume();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseReleased(MouseEvent e) {
		numMouseButtonsDown--;
		removeMouseMotionListener(this);

		pick.fixed = pickfixed;
		pick.x = e.getX();
		pick.y = e.getY();
		if (numMouseButtonsDown == 0) {
			pick = null;
		}

		repaint();
		e.consume();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseDragged(MouseEvent e) {
		pick.x = e.getX();
		pick.y = e.getY();
		repaint();
		e.consume();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void start() {
		relaxer = new Thread(this);
		relaxer.start();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public void stop() {
		relaxer = null;
	}
}
