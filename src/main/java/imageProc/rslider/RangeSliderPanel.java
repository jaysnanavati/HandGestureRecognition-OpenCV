
// RangeSliderPanel.ava
// Andrew Davison, ad@fivedots.coe.psu.ac.th, March 2011


package imageProc.rslider;


import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class RangeSliderPanel extends JPanel
{
  private RangeSlider rangeSlider;
  private JTextField lowerValTF, upperValTF;


  public RangeSliderPanel()
  {  this("Range Slider", 0, 10, 3, 7); }


  public RangeSliderPanel(String title, int min, int max, int lower, int upper)
  {
    setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    setLayout(new BorderLayout());

    Border lowerEtch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    TitledBorder titleBorder = BorderFactory.createTitledBorder(lowerEtch, title);
    titleBorder.setTitleJustification(TitledBorder.RIGHT);
    setBorder(titleBorder);

    // check min-max, lower-upper order
    if (min > max) { // swap values
      int temp = min;
      min = max;
      max = temp;
    }
    if (lower > upper) { // swap values
      int temp = lower;
      lower = upper;
      upper = temp;
    }

    if ((lower < min) || (lower > max)) // check lower is in range
      lower = min;
    if ((upper < min) || (upper > max)) // check upper is in range
      upper = max;

    // System.out.println(title + " min: " + min + ": max: " + max);
    // System.out.println("      lower: " + lower + ": upper: " + upper);

    // initialize slider
    rangeSlider = new RangeSlider(min, max, lower, upper);
    add(rangeSlider, BorderLayout.CENTER);

    int maxCols = Math.round(("" + max).length() * 1.3f);      
                              // convert digit to (approx) column width
    JPanel reportPanel = initReportPanel(lower, upper, maxCols);
    add(reportPanel, BorderLayout.SOUTH);
  }  // end of RangeSliderPanel()


  private JPanel initReportPanel(int lower, int upper, int numCols)
  {
    // initialize lower and upper report labels
    lowerValTF = new JTextField(numCols);
    lowerValTF.setEditable(false);
    lowerValTF.setText("" + lower);

    upperValTF = new JTextField(numCols);
    upperValTF.setEditable(false);
    upperValTF.setText("" + upper);

    // position report components
    JPanel lowerPanel = new JPanel();
    lowerPanel.add(new JLabel("Lower value: "));
    lowerPanel.add(lowerValTF);

    JPanel upperPanel = new JPanel();
    upperPanel.add(new JLabel("Upper value: "));
    upperPanel.add(upperValTF);

    JPanel reportPanel = new JPanel();
    reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.X_AXIS));  
    reportPanel.add(lowerPanel);
    reportPanel.add(upperPanel);

    return reportPanel;
  }  // end of initReportPanel()
    

  public void updateLabels(int lower, int upper)
  { lowerValTF.setText("" + lower);
    upperValTF.setText("" + upper);
  }  // end of updateLabels()
    

  public RangeSlider getRangeSlider()
  {  return rangeSlider;  }

}  // end of RangeSliderPanel class
