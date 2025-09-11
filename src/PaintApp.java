import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

abstract class MyShape {
	Color color;

	abstract void draw(Graphics g);

	abstract boolean contains(int x, int y);

	abstract void move(int dx, int dy);
}

class MyBrushStroke extends MyShape {
	ArrayList<Point> points = new ArrayList<>();
	int strokeWidth = 3; // default brush size

	public MyBrushStroke(Color color, int strokeWidth) {
		this.color = color;
		this.strokeWidth = strokeWidth;
	}

	public void addPoint(int x, int y) {
		points.add(new Point(x, y));
	}

	@Override
	void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int i = 1; i < points.size(); i++) {
			Point p1 = points.get(i - 1);
			Point p2 = points.get(i);
			g2.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

	@Override
	boolean contains(int x, int y) {
		for (Point p : points) {
			if (Math.abs(p.x - x) <= strokeWidth && Math.abs(p.y - y) <= strokeWidth) {
				return true;
			}
		}
		return false;
	}

	@Override
	void move(int dx, int dy) {
		for (Point p : points) {
			p.translate(dx, dy);
		}
	}
}

class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener {
	ArrayList<MyShape> shapes = new ArrayList<>();
	MyBrushStroke currentStroke = null;
	MyShape selectedShape = null;
	int lastX, lastY;
	Color currentColor = Color.BLACK;
	String mode = "draw";
	boolean dragging = false;

	int brushSize = 3; // default

	public void increaseBrushSize() {
		brushSize += 2;
	}

	public void decreaseBrushSize() {
		brushSize = Math.max(1, brushSize - 2);
	}

	public CanvasPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (MyShape s : shapes)
			s.draw(g);
		if (currentStroke != null && mode.equals("draw"))
			currentStroke.draw(g);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX(), y = e.getY();
		lastX = x;
		lastY = y;

		if (mode.equals("draw")) {
			currentStroke = new MyBrushStroke(currentColor, brushSize);
			currentStroke.addPoint(x, y);
		} else {
			for (int i = shapes.size() - 1; i >= 0; i--) {
				if (shapes.get(i).contains(x, y)) {
					selectedShape = shapes.get(i);
					dragging = true;
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mode.equals("draw") && currentStroke != null) {
			shapes.add(currentStroke);
			currentStroke = null;
			repaint();
		}
		dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX(), y = e.getY();

		if (mode.equals("draw")) {
			currentStroke.addPoint(x, y);
			repaint();
		} else if (dragging && selectedShape != null) {
			int dx = x - lastX;
			int dy = y - lastY;
			selectedShape.move(dx, dy);
			lastX = x;
			lastY = y;
			repaint();
		}
	}

	public void deleteShapeAt(int x, int y) {
		for (int i = shapes.size() - 1; i >= 0; i--) {
			if (shapes.get(i).contains(x, y)) {
				shapes.remove(i);
				repaint();
				return;
			}
		}
	}

	public void setMode(String m) {
		this.mode = m;
		this.selectedShape = null;
		this.dragging = false;
	}

	public void setColor(Color c) {
		this.currentColor = c;
	}

	public void mouseClicked(MouseEvent e) {
		if (mode.equals("delete")) {
			deleteShapeAt(e.getX(), e.getY());
		}
	}

	// Unused methods
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}

public class PaintApp extends JFrame {
	CanvasPanel canvas;

	public PaintApp() {

		setTitle("Brush Paint App with CRUD");
		setSize(900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas = new CanvasPanel();

		JButton biggerBtn = new JButton("Brush +");
		biggerBtn.addActionListener(_ -> canvas.increaseBrushSize());

		JButton smallerBtn = new JButton("Brush -");
		smallerBtn.addActionListener(_ -> canvas.decreaseBrushSize());

		JButton drawBtn = new JButton("Draw");
		drawBtn.addActionListener(_ -> canvas.setMode("draw"));

		JButton moveBtn = new JButton("Move");
		moveBtn.addActionListener(_ -> canvas.setMode("move"));

		JButton deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(_ -> canvas.setMode("delete"));

		JButton colorBtn = new JButton("Color");
		colorBtn.addActionListener(_ -> {
			Color chosen = JColorChooser.showDialog(null, "Pick Color", Color.BLACK);
			if (chosen != null)
				canvas.setColor(chosen);
		});

		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('+'), "increaseBrush");
		canvas.getActionMap().put("increaseBrush", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				canvas.increaseBrushSize();
			}
		});
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('-'), "decreaseBrush");
		canvas.getActionMap().put("decreaseBrush", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				canvas.decreaseBrushSize();
			}
		});

		JPanel controls = new JPanel();
		controls.add(biggerBtn);
		controls.add(smallerBtn);
		controls.add(drawBtn);
		controls.add(moveBtn);
		controls.add(deleteBtn);
		controls.add(colorBtn);

		add(controls, BorderLayout.NORTH);
		add(canvas, BorderLayout.CENTER);
		setVisible(true);
	}

	public static void main(String[] args) {
		new PaintApp();
	}
}
