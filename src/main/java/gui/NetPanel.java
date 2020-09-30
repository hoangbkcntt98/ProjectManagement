package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import javax.swing.border.BevelBorder;

public class NetPanel extends JPanel {
	Graph graph;
	AbstractLayout layout;
	PluggableRenderer pr;
	VisualizationViewer vv;
	;
	DefaultSettableVertexLocationFunction vertexLocations;
	EditingModalGraphMouse graphMouse;
	String instructions = "<html>" +

			"<h3>Picking Mode:</h3>" + "<ul>" + "<li>Mouse1 on a Vertex selects the vertex"
			+ "<li>Mouse1 elsewhere unselects all Vertices"
			+ "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"
			+ "<li>Mouse1+drag on a Vertex moves all selected Vertices"
			+ "<li>Mouse1+drag elsewhere selects Vertices in a region"
			+ "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"
			+ "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it" + "</ul>"
			+ "<h3>Transforming Mode:</h3>" + "<ul>" + "<li>Mouse1+drag pans the graph"
			+ "<li>Mouse1+Shift+drag rotates the graph" + "<li>Mouse1+CTRL(or Command)+drag shears the graph" + "</ul>"
			+ "</html>";
	public void drawGraph() {
		generateGraph();
		generateVisualizationView();
		drawToolsBar();

	}
	public void generateGraph() {
		graph = new SparseGraph();
		Vertex[] v = createVertices();
		createEdges(v);
	}
	public void generateVisualizationView() {
		pr = new PluggableRenderer();
		this.layout = new FRLayout(graph);
		
		vertexLocations = new DefaultSettableVertexLocationFunction();
		setLocationVertex();
		
		layout.initialize(new Dimension(600, 600));
		vv = new VisualizationViewer(layout, pr);
		vv.setBackground(Color.white);
		vv.setPickSupport(new ShapePickSupport());
		
		pr.setEdgeShapeFunction(new EdgeShape.Line());
		pr.setVertexLabelCentering(true);
		setVertexPaintFunction();
		setEdgePaintFunction();
		setVertexLable();
		generateVertexShape(pr);
		vv.setToolTipFunction(new DefaultToolTipFunction());
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		add(panel);
		
	}
	public void setLocationVertex() {
		
	}
	public void setVertexPaintFunction() {
		
	}
	public void setEdgePaintFunction() {
		
	}
	
	public void generateVertexShape(PluggableRenderer pr) {
		// change size of vertex
		pr.setVertexShapeFunction(new AbstractVertexShapeFunction(new ConstantVertexSizeFunction(40),
				new ConstantVertexAspectRatioFunction(1.0f)) {
			public Shape getShape(Vertex v) {
				// TODO Auto-generated method stub
				return factory.getEllipse(v);
			}
		});
	}
	

	public void drawToolsBar() {
		
		graphMouse = new EditingModal();
		
		graphMouse.setVertexLocations(vertexLocations);
		vv.setGraphMouse(graphMouse);
		
		setMousePlugin();
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		final ScalingControl scaler = new CrossoverScalingControl();
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(vv, instructions);
			}
		});
		JButton back = new JButton("Back");
		back.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JPanel controls = new JPanel();
		controls.add(plus);
		controls.add(minus);
		JComboBox modeBox = graphMouse.getModeComboBox();
		controls.add(modeBox);
		controls.add(help);
		controls.add(back);
		add(controls, BorderLayout.SOUTH);
	}
	public void setMousePlugin() {
		graphMouse.add(new PopupGraphMousePlugin());
	}
	public void createMainMenu() {
		JMenu menu = new JMenu("File");
		menu.add(new AbstractAction("Make Image") {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showSaveDialog(NetPanel.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					writeJPEGImage(file);
				}
			}
		});

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		add(menuBar);
	}
	
	public void setVertexLable() {
		pr.setVertexStringer(new VertexStringer() {
			public String getLabel(ArchetypeVertex v) {
				return v.toString();
			}
		});
	}
	/**
	 * create some vertices
	 * 
	 * @param count how many to create
	 * @return the Vertices in an array
	 */
	public Vertex[] createVertices() {
		int count = 6;
		Vertex[] v = new Vertex[count];
		for (int i = 0; i < count; i++) {
			v[i] = graph.addVertex(new SparseVertex());
		}
		return v;
	}

	/**
	 * create edges for this demo graph
	 * 
	 * @param v an array of Vertices to connect
	 */
	public void createEdges(Vertex[] v) {
		graph.addEdge(new DirectedSparseEdge(v[0], v[1]));
		graph.addEdge(new DirectedSparseEdge(v[0], v[2]));
		graph.addEdge(new DirectedSparseEdge(v[2], v[3]));
		graph.addEdge(new DirectedSparseEdge(v[1], v[3]));
		graph.addEdge(new DirectedSparseEdge(v[1], v[4]));
		graph.addEdge(new UndirectedSparseEdge(v[4], v[5]));
		graph.addEdge(new UndirectedSparseEdge(v[4], v[3]));
	}

	/**
	 * copy the visible part of the graph to a file as a jpeg image
	 * 
	 * @param file
	 */
	public void writeJPEGImage(File file) {
		int width = vv.getWidth();
		int height = vv.getHeight();

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		vv.paint(graphics);
		graphics.dispose();

		try {
			ImageIO.write(bi, "jpeg", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex)
			throws java.awt.print.PrinterException {
		if (pageIndex > 0) {
			return (Printable.NO_SUCH_PAGE);
		} else {
			java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
			vv.setDoubleBuffered(false);
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

			vv.paint(g2d);
			vv.setDoubleBuffered(true);

			return (Printable.PAGE_EXISTS);
		}
	}

	/**
	 * Create the panel.
	 */
	public NetPanel() {
		
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(new BorderLayout(0, 0));
		

	}
	

}
