package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import algo.ModelChecker;
import error.ModelCheckingException;

@SuppressWarnings("serial")
public class Interface extends JFrame {

	private JPanel contentPane;
	private File f;
	private String path;

	public Interface() {

		setSize(600, 400);
		setResizable(false);
		setTitle("JPF-CTL");

		setLocationRelativeTo(null);

		// center the frame
		// setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// ImageIcon img = new ImageIcon("src/main/java/gui/images/jpf-ctl-logo.png");
		// stage.setIconImage(img.getImage()); //super blurry

		// manager = new JPanel();
		// root = new BoxLayout(manager, BoxLayout.Y_AXIS);

		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		// contentPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lbl_title = new JLabel("JPF-CTL Java Model Checker");
		lbl_title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		lbl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
		// lbl_title.setSize(600, 50);

		// enter formula sub component
		JPanel formulaPane = new JPanel();
		formulaPane.setLayout(new GridLayout(4, 2, 10, 10));
		formulaPane.setPreferredSize(new Dimension(this.getWidth(), 200));

		JLabel lbl_formula = new JLabel("Enter Formula:");
		lbl_formula.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		lbl_formula.setAlignmentX(Component.CENTER_ALIGNMENT);
		// lbl_formula.setPreferredSize(new Dimension(300, 50));

		JTextArea ta_formula = new JTextArea();
		// ta_formula.setSize(new Dimension(300,50));
		ta_formula.setBorder(BorderFactory.createLineBorder(Color.black));
		ta_formula.setLineWrap(true);

		JButton btn_choose = new JButton("Choose File");
		btn_choose.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		btn_choose.setAlignmentX(Component.CENTER_ALIGNMENT);

		JCheckBox chk_randomness = new JCheckBox("Consider Randomness");
		chk_randomness.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		chk_randomness.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lbl_file = new JLabel("No File Chosen");
		// lbl_file.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lbl_file.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

		JLabel lbl_cmd = new JLabel("Command Line Input:");
		// lbl_cmd.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lbl_cmd.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

		// run function for choose file button
		btn_choose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Java class files (*.class)";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else
							return f.getName().toLowerCase().endsWith(".class");
					}
				});

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					f = fc.getSelectedFile();
					path = f.toString();
					lbl_file.setText(path);
				}
			}
		});
		
		formulaPane.add(lbl_formula);
		formulaPane.add(btn_choose);
		formulaPane.add(ta_formula);
		formulaPane.add(chk_randomness);

		formulaPane.add(lbl_file);
		formulaPane.add(new JLabel());
		formulaPane.add(lbl_cmd);
		formulaPane.add(new JLabel());

		// left text aligned lbl pane

		JTextArea ta_cmd = new JTextArea();
		// ta_formula.setSize(new Dimension(300,50));
		ta_cmd.setBorder(BorderFactory.createLineBorder(Color.black));
		ta_cmd.setLineWrap(true);

		JPanel runPanel = new JPanel();
		runPanel.setLayout(new GridLayout(1, 2));

		JButton btn_run = new JButton("Run");
		btn_run.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		btn_run.setBackground(new Color(0, 153, 0));

		// run function for run button
		btn_run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ta_formula.getText().trim().isEmpty()) {
					// joption pane for error
				} else if (f == null) {
					// joptionpane for error
				} else {
					// get the values and do the run code
					String checked = chk_randomness.isSelected() ? "true" : "false";
					String formula = ta_formula.getText().trim();
					String cmd = ta_cmd.getText().trim();

					JFrame frame = new JFrame();
					try {
						boolean result = ModelChecker.validate(formula, path, checked);

						System.out.println("Result: " + result);

						if (result) { // TODO display valid
							JOptionPane.showMessageDialog(frame, "Valid");
						} else { // Display invalid + counter example
							JOptionPane.showMessageDialog(frame, "Invalid");
						}

					} catch (ModelCheckingException e1) {
						JOptionPane.showMessageDialog(frame, "There was an error model checking:\n" + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}

		});

		runPanel.add(new Label());
		runPanel.add(btn_run);

		contentPane.add(lbl_title);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(formulaPane);
		// contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(ta_cmd);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(runPanel);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.add(contentPane);

		setVisible(true);
		// add(contentPane);
	}

	public static void main(String[] args) {
		new Interface();
	}

}
