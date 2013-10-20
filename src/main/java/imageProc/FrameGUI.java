package imageProc;

import imageProc.rslider.RangeSliderPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.googlecode.javacv.CanvasFrame;

public class FrameGUI extends JFrame {

	private CanvasFrame rawFrame, thresholdFrame;
	private RangeSliderPanel huePanel, saturationPanel, valuePanel;
	private HSVSettings hsvSettings;

	public FrameGUI(HSVSettings hsvSettings) {
		super("Range Slider Demo");
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		this.hsvSettings = hsvSettings;
		
		huePanel = new RangeSliderPanel("Hue", 0, hsvSettings.getMAX_HUE(), hsvSettings.getHueLower(),
				hsvSettings.getHueUpper());
		saturationPanel = new RangeSliderPanel("Saturation", 0,
				hsvSettings.getMAX_SATURATION(), hsvSettings.getSaturationLower(),
				 hsvSettings.getSaturationUpper());
		valuePanel = new RangeSliderPanel("Value", 0,
				hsvSettings.getMAX_VALUE(), hsvSettings.getValueLower(), hsvSettings.getValueUpper());

		c.add(huePanel, BorderLayout.WEST);
		c.add(saturationPanel, BorderLayout.CENTER);
		c.add(valuePanel, BorderLayout.EAST);

		huePanel.getRangeSlider().addChangeListener(
				new HueChangeListener(huePanel));
		saturationPanel.getRangeSlider().addChangeListener(
				new SaturationChangeListener(saturationPanel));
		valuePanel.getRangeSlider().addChangeListener(
				new ValueChangeListener(valuePanel));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setResizable(false);
		setVisible(true);

		this.rawFrame = new CanvasFrame("Raw");
		this.thresholdFrame = new CanvasFrame("Threshold");

		JButton frameClose = new JButton("Exit");
		frameClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// stopCamera();
				System.exit(1);

			}
		});
		rawFrame.add(frameClose, BorderLayout.SOUTH);

		rawFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				rawFrame.dispose();
				// stopCamera();
			}
		});

	}

	private class HueChangeListener implements ChangeListener {

		private RangeSliderPanel panel;

		HueChangeListener(RangeSliderPanel panel) {
			this.panel = panel;
		}

		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			int lower = panel.getRangeSlider().getValue();
			int upper = panel.getRangeSlider().getUpperValue();
			panel.updateLabels(lower, upper);
			hsvSettings.setHueRange(lower, upper);
		}

	}

	private class SaturationChangeListener implements ChangeListener {

		private RangeSliderPanel panel;

		SaturationChangeListener(RangeSliderPanel panel) {
			this.panel = panel;
		}

		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			int lower = panel.getRangeSlider().getValue();
			int upper = panel.getRangeSlider().getUpperValue();
			panel.updateLabels(lower, upper);
			hsvSettings.setSaturationRange(lower, upper);
		}


	}

	private class ValueChangeListener implements ChangeListener {

		private RangeSliderPanel panel;

		ValueChangeListener(RangeSliderPanel panel) {
			this.panel = panel;
		}

		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			int lower = panel.getRangeSlider().getValue();
			int upper = panel.getRangeSlider().getUpperValue();
			panel.updateLabels(lower, upper);
			hsvSettings.setValueRange(lower, upper);
		}
		
	}

	public CanvasFrame getRawFrame() {
		return rawFrame;
	}

	public void setRawFrame(CanvasFrame rawFrame) {
		this.rawFrame = rawFrame;
	}

	public CanvasFrame getThresholdFrame() {
		return thresholdFrame;
	}

	public void setThresholdFrame(CanvasFrame thresholdFrame) {
		this.thresholdFrame = thresholdFrame;
	}

	public HSVSettings getHsvSettings() {
		return hsvSettings;
	}

	public void setHsvSettings(HSVSettings hsvSettings) {
		this.hsvSettings = hsvSettings;
	}

}
