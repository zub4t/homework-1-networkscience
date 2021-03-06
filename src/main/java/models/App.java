package models;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.*;
import nodes.Node;
import bfs.BFS;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class App {
    static double prob = Integer.MAX_VALUE;
    static int maxNumberOfConnections = 2;
    final static DecimalFormat df = new DecimalFormat("0.0000");
    final static Map<Integer, Integer> currentMap = new TreeMap<>();
    static int m = 0;
    static int m0 = 0;

    public static void main(String[] args) {
        final Canvas canvas = new Canvas();
        canvas.setSize(1000, 800);
        final JFrame frame = new JFrame("Modelo Erdős–Rényi");
        // ---
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem1;
        JMenuItem menuItem2;
        JMenuItem menuItem12;

        JMenuItem menuItem3;
        JMenuItem menuItem4;
        JMenuItem menuItem34;
        JMenuItem menuItem43;
        // Create the menu bar.
        menuBar = new JMenuBar();

        // Build the first menu.
        menu = new JMenu("Erdős–Rényi");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);
        // a group of JMenuItems
        menuItem1 = new JMenuItem("Generating n = 2000, p = 0.0001",
                KeyEvent.VK_T);
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                FileOutputStream fos;
                try {

                    fos = new FileOutputStream("random1.txt", false);
                    List<Node> list = erdosrenyi(2000, 0.0001);
                    String out = writeConnections(2000, list);
                    JOptionPane.showMessageDialog(null,
                            "The Giant Component size of the generated graph is " + getGiantComponentList(list).size(),
                            "GiantComponent", JOptionPane.PLAIN_MESSAGE);
                    fos.write(out.getBytes()); // writes bytes into file
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        menu.add(menuItem1);

        menuItem2 = new JMenuItem("Generating n = 2000, p = 0.005",
                KeyEvent.VK_T);
        menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {

                FileOutputStream fos;
                try {

                    fos = new FileOutputStream("random2.txt", false);
                    List<Node> list = erdosrenyi(2000, 0.005);
                    String out = writeConnections(2000, list);
                    JOptionPane.showMessageDialog(null,
                            "The Giant Component size of the generated graph is " + getGiantComponentList(list).size(),
                            "GiantComponent", JOptionPane.PLAIN_MESSAGE);
                    fos.write(out.getBytes()); // writes bytes into file
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        menu.add(menuItem2);

        menuItem12 = new JMenuItem("Plot giant component",
                KeyEvent.VK_T);
        menuItem12.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                final Map<Double, Integer> map = getGiantComponentPoints();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        SimplePlotPanel.barabasi = false;
                        SimplePlotPanel.size = 10;

                        SimplePlotMain.createAndShowGUI(new Function() {
                            @Override
                            public double compute(double argument) {
                                double d = Double.parseDouble(df.format(argument).replaceAll(",", "."));
                                // //System.out.println(d);

                                Integer value = map.get(d);

                                if (value != null) {

                                    return value;
                                }

                                return -100;

                            }
                        }, 0.0001, 0.005, 0.0, 2000.0);
                    }
                });
            }
        });
        menu.add(menuItem12);

        // --

        // Build second menu in the menu bar.
        menu = new JMenu("Barabási-Albert");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);
        // a group of JMenuItems
        menuItem3 = new JMenuItem("Generating n = 2000, m0 = 3, m = 1",
                KeyEvent.VK_T);
        menuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream("ba1.txt", false);
                    fos.write(writeConnections(2000, barabasi(2000, 3, 1)).getBytes()); // writes bytes into file
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        menu.add(menuItem3);

        menuItem4 = new JMenuItem("Generating n = 2000, m0 = 5, m = 2 ",
                KeyEvent.VK_T);
        menuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                FileOutputStream fos;
                try {
                    List<Node> list = barabasi(2000, 5, 2);
                    fos = new FileOutputStream("ba2.txt", false);

                    fos.write(writeConnections(2000, list).getBytes()); // writes bytes into file
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        menu.add(menuItem4);

        menuItem34 = new JMenuItem("Plot the degree distribution n=2000, m0=3, m=1",
                KeyEvent.VK_T);

        menuItem34.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        List<Node> list = barabasi(SimplePlotPanel.n, 3, 1);
                        final Map<Integer, Integer> map = getCumulativeDistrib(list);
                        App.currentMap.clear();
                        App.currentMap.putAll(map);
                        App.m = 1;
                        App.m0 = 3;
                        SimplePlotPanel.barabasi = true;
                        SimplePlotPanel.size = 10;
                        SimplePlotMain.createAndShowGUI(new Function() {
                            @Override
                            public double compute(double argument) {
                                int key = (int) Math.pow(10, (argument));
                                Integer value = map.get(key);
                                if (value != null) {

                                    return Math.log10(value);
                                }
                                return 0;

                            }
                        }, 0, Math.log10(map.size()), 0, Math.log10(list.size() + 1));

                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                JOptionPane.showMessageDialog(null,
                                        "The Alpha accordingly with the slide Maximum likelihood fitting is "
                                                + SimplePlotPanel.trueAlpha,
                                        "Maximum likelihood fitting – best", JOptionPane.PLAIN_MESSAGE);
                            }
                        }.start();

                    }
                });
            }
        });
        menu.add(menuItem34);

        menuItem43 = new JMenuItem("Plot the degree distribution n=2000, m0=5, m=2",
                KeyEvent.VK_T);

        menuItem43.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        List<Node> list = barabasi(SimplePlotPanel.n, 5, 2);

                        final Map<Integer, Integer> map = getCumulativeDistrib(list);
                        currentMap.clear();
                        currentMap.putAll(map);
                        App.m = 1;
                        App.m0 = 3;
                        SimplePlotPanel.barabasi = true;
                        SimplePlotPanel.size = 10;

                        SimplePlotMain.createAndShowGUI(new Function() {
                            @Override
                            public double compute(double argument) {
                                int key = (int) Math.pow(10, (argument));
                                Integer value = map.get(key);
                                if (value != null) {

                                    return Math.log10(value);
                                }

                                return 0;

                            }
                        }, 0, Math.log10(map.size()), 0, Math.log10(list.size() + 1));
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                JOptionPane.showMessageDialog(null,
                                        "The Alpha accordingly with the slide Maximum likelihood fitting is "
                                                + SimplePlotPanel.trueAlpha,
                                        "Maximum likelihood fitting – best", JOptionPane.PLAIN_MESSAGE);
                            }
                        }.start();
                    }
                });
            }
        });
        menu.add(menuItem43);

        // --

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 850);
        frame.setJMenuBar(menuBar);
        final JPanel panel1 = new JPanel();
        final JLabel labelNodes = new JLabel("number of nodes");
        final JLabel nodesSliderValue = new JLabel("10");

        final JSlider nodesSlider = new JSlider(JSlider.HORIZONTAL, 0, 2000, 10);
        nodesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                nodesSliderValue.setText(nodesSlider.getValue() + "");

            }

        });
        panel1.add(labelNodes);
        panel1.add(nodesSlider);

        final JLabel labelProbability = new JLabel("probability to connect");
        final JLabel probabilitySliderValue = new JLabel("0%");
        final JSlider probabilitySlider = new JSlider(JSlider.HORIZONTAL, 0, Integer.MAX_VALUE, 0);
        probabilitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                probabilitySliderValue.setText((probabilitySlider.getValue() / App.prob) + "%");

            }

        });
        final JPanel panel2 = new JPanel();

        panel2.add(labelProbability);
        panel2.add(probabilitySlider);

        final JPanel panel = new JPanel();

        final JPanel panel11 = new JPanel();
        panel11.add(BorderLayout.NORTH, panel1);
        panel11.add(BorderLayout.CENTER, nodesSliderValue);

        final JPanel panel22 = new JPanel();
        panel11.add(BorderLayout.NORTH, panel2);
        panel11.add(BorderLayout.CENTER, probabilitySliderValue);

        panel.add(panel11);
        panel.add(panel22);

        JButton drawErdosrenyi = new JButton("draw erdosrenyi");
        drawErdosrenyi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                erdosrenyi(canvas, nodesSlider, probabilitySlider);
            }
        });

        JButton drawBarabasi = new JButton("draw barabasi");
        drawBarabasi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                barabasi(canvas, 100, 3, 2);
            }
        });
        // Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, canvas);

        final JPanel panelModel = new JPanel();
        panelModel.add(drawErdosrenyi);
        panelModel.add(drawBarabasi);
        frame.getContentPane().add(BorderLayout.SOUTH, panelModel);

        frame.setVisible(true);

    }

    public static String writeConnections(int n, List<Node> list) {
        Set<String> map = new TreeSet<>();
        String out = list.size() + "\n";
        for (Node node : list) {
            for (Node connected : node.getConnectedTo()) {
                if (!map.contains(connected.getID() + "-" + node.getID())) {
                    out += node.getID() + " " + connected.getID() + "\n";
                    map.add(connected.getID() + "-" + node.getID());
                    map.add(node.getID() + "-" + connected.getID());
                }

            }
        }

        return out;
    }

    public static Set<Node> getGiantComponentList(List<Node> list) {
        List<Set<Node>> setList = BFS.doBFS(list);
        Set<Node> giantComponent = null;
        int max = -1;
        for (Set<Node> set : setList) {
            if (max < set.size()) {
                giantComponent = set;
                max = set.size();
            }
        }

        return giantComponent;
    }

    public static void erdosrenyi(Canvas canvas, JSlider nodesSlider, JSlider probabilitySlider) {

        Graphics g = canvas.getGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodesSlider.getValue(); i++) {
            list.add(Node.builder()
                    .x((int) Math.round(Math.random() * 1000))
                    .y((int) Math.round(Math.random() * 800))
                    .ID(i)
                    .connectedTo(new TreeSet<Node>()).build());

        }
        double probSliderValue = probabilitySlider.getValue() / App.prob;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                Random r = new java.util.Random();
                double prob = r.nextDouble();
                if (prob <= probSliderValue) {

                    list.get(i).getConnectedTo().add(list.get(j));
                    list.get(j).getConnectedTo().add(list.get(i));

                }

            }

        }

        for (Node node : list) {
            g.setColor(new Color(
                    (int) Math.random() * 255,
                    (int) Math.random() * 100,
                    (int) Math.random() * 100));
            g.drawArc(
                    node.getX(),
                    node.getY(),
                    5, 5, 0, 360);

            g.drawString(node.getID() + "", node.getX(), node.getY());
            for (Node connectedToNode : node.getConnectedTo()) {
                g.drawLine(node.getX(),
                        node.getY(),
                        connectedToNode.getX(),
                        connectedToNode.getY());
            }
        }
        for (Node node : list) {
            g.setColor(new Color(
                    (int) Math.random() * 255,
                    (int) Math.random() * 100,
                    (int) Math.random() * 100));
            g.drawArc(
                    node.getX(),
                    node.getY(),
                    20, 20, 0, 360);

            g.drawString(node.getID() + "", node.getX(), node.getY());
            for (Node connectedToNode : node.getConnectedTo()) {
                g.drawLine(node.getX(),
                        node.getY(),
                        connectedToNode.getX(),
                        connectedToNode.getY());
            }
        }
        Set<Node> giantComponent = getGiantComponentList(list);

        if (giantComponent.size() > 0) {

            Iterator<Node> itr = giantComponent.iterator();
            while (itr.hasNext()) {
                Node nnn = itr.next();
                g.setColor(new Color(255, 0, 0));
                g.fillArc(
                        nnn.getX(),
                        nnn.getY(),
                        20, 20, 0, 360);
            }

        }
    }

    public static List<Node> erdosrenyi(int n, double p) {
        // //System.out.println("end erdosrenyi:" + n);
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(Node.builder()
                    .ID(i)
                    .connectedTo(new TreeSet<Node>()).build());

        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Random r = new java.util.Random();
                double prob = r.nextDouble();
                if (prob <= p) {
                    list.get(i).getConnectedTo().add(list.get(j));
                    list.get(j).getConnectedTo().add(list.get(i));
                }
            }
        }
        // //System.out.println("end erdosrenyi:" + n);
        return list;
    }

    public static Map<Double, Integer> getGiantComponentPoints() {
        Map<Double, Integer> pointsMap = new TreeMap<>();
        for (double p = 0.0001; p <= 0.005; p += 0.0001) {
            double d = Double.parseDouble(df.format(p).replaceAll(",", "."));
            List<Node> list = erdosrenyi(2000, p);
            double sum = 0;
            for (Node nn : list) {
                sum += nn.getConnectedTo().size() / 1999.0;
            }
            Set<Node> giant = getGiantComponentList(list);
            System.out.println("at prob: " + d + " -> average dregree " + sum + " -> " + giant.size());
            pointsMap.put(d,giant.size());
        }
        return pointsMap;
    }

    public static Map<Integer, Integer> getCumulativeDistrib(List<Node> list) {
        Map<Integer, Integer> pointsMap = new TreeMap<>();
        int maxDegree = 0;

        for (Node node : list) {

            if (node.getConnectedTo().size() > maxDegree) {
                maxDegree = node.getConnectedTo().size();

            }

        }

        for (int i = maxDegree; i > 0; i--) {
            int count = 0;
            for (Node node : list) {
                if (node.getConnectedTo().size() >= i) {
                    count++;
                }
            }
            pointsMap.put(i, count);

        }

        return pointsMap;
    }

    public static void barabasi(Canvas canvas, final int n, final int m0, final int m) {

        final Graphics g = canvas.getGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        final List<Node> list = new ArrayList<>();
        for (int i = 0; i < m0; i++) {
            list.add(Node.builder()
                    .x((int) Math.round(Math.random() * 1000))
                    .y((int) Math.round(Math.random() * 800))
                    .ID(i)
                    .connectedTo(new TreeSet<Node>()).build());

        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (i != j) {
                    list.get(i).getConnectedTo().add(list.get(j));
                    list.get(j).getConnectedTo().add(list.get(i));
                }
            }
        }

        for (Node node : list) {
            g.setColor(new Color(
                    (int) Math.random() * 255,
                    (int) Math.random() * 100,
                    (int) Math.random() * 100));
            g.drawArc(
                    node.getX(),
                    node.getY(),
                    5, 5, 0, 360);

            g.drawString(node.getID() + "", node.getX(), node.getY());
            for (Node connectedToNode : node.getConnectedTo()) {
                g.drawLine(node.getX(),
                        node.getY(),
                        connectedToNode.getX(),
                        connectedToNode.getY());
            }
        }

        new Thread() {
            public void run() {
                while (list.size() < n) {
                    Node currentNode = Node.builder()
                            .x((int) Math.round(Math.random() * 1000))
                            .y((int) Math.round(Math.random() * 800))
                            .ID(list.size())
                            .connectedTo(new TreeSet<Node>()).build();

                    while (currentNode.getConnectedTo().size() < m) {
                        int current = (int) Math.round(Math.random() * (list.size() - 1));
                        Node nn = list.get(current);
                        if (Math.random() <= (double) nn.getConnectedTo().size() / list.size() * 2) {
                            currentNode.getConnectedTo().add(list.get(nn.getID()));
                            nn.getConnectedTo().add(currentNode);
                        }
                    }

                    list.add(currentNode);
                    for (Node node : list) {
                        g.setColor(new Color(
                                (int) Math.random() * 255,
                                (int) Math.random() * 100,
                                (int) Math.random() * 100));
                        g.drawArc(
                                node.getX(),
                                node.getY(),
                                5, 5, 0, 360);

                        g.drawString(node.getID() + "", node.getX(), node.getY());
                        for (Node connectedToNode : node.getConnectedTo()) {
                            g.drawLine(node.getX(),
                                    node.getY(),
                                    connectedToNode.getX(),
                                    connectedToNode.getY());
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

    public static List<Node> barabasi(int n, int m0, int m) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < m0; i++) {
            list.add(Node.builder()
                    .ID(i)
                    .connectedTo(new TreeSet<Node>()).build());
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (i != j) {
                    list.get(i).getConnectedTo().add(list.get(j));
                    list.get(j).getConnectedTo().add(list.get(i));
                }
            }
        }

        while (list.size() < n) {
            Node currentNode = Node.builder()
                    .ID(list.size())
                    .connectedTo(new TreeSet<Node>()).build();

            while (currentNode.getConnectedTo().size() < m) {
                int current = (int) Math.round(Math.random() * (list.size() - 1));
                Node nn = list.get(current);
                double p = Math.random();

                double threshold = (double) nn.getConnectedTo().size()
                        / (((list.size() - m0) * m) + (m0 * (m0 - 1)));
                if (p <= threshold) {
                    currentNode.getConnectedTo().add(list.get(nn.getID()));
                    nn.getConnectedTo().add(currentNode);
                }

            }

            list.add(currentNode);

        }

        return list;
    }

}
