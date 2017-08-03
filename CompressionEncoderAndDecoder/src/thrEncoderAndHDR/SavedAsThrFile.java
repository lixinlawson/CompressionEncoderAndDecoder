package thrEncoderAndHDR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SavedAsThrFile extends JFrame {

	public SavedAsThrFile(String fileName, double compRatio) {
		super("Compressed To .thr File");
		setLayout(new BorderLayout());
		add(createNameAndRatioPanel(fileName, compRatio), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createNameAndRatioPanel(String fileName, double compRatio) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBackground(Color.WHITE);
		JLabel name = new JLabel("Compressed File is : " + fileName);
		name.setFont(new Font("Serif", Font.BOLD, 20));
		JLabel ratio = new JLabel(String.format("Compression Ratio is: %.7f",
				compRatio));
		ratio.setFont(new Font("Serif", Font.BOLD, 20));

		panel.add(name);
		panel.add(ratio);
		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(createOKButton(this));
		return panel;
	}

	private JButton createOKButton(final SavedAsThrFile saved) {
		JButton ok = new JButton(" OK ");
		ok.setForeground(Color.BLUE);
		Dimension prefSize = ok.getPreferredSize();
		Dimension newSize = new Dimension(Integer.MAX_VALUE,
				(int) prefSize.getHeight());
		ok.setMaximumSize(newSize);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saved.dispose();
			}
		});
		return ok;
	}

}
