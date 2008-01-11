/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
 * Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package rlVizLib.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import rlVizLib.visualization.interfaces.ValueFunctionDataProvider;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import rlglue.types.Observation;

/**
 *
 * @author btanner
 */
public class ValueFunctionVizComponent implements VizComponent, ChangeListener {

    long lastQueryTime = 0;
    Vector<Double> theValues = null;
    double bestV;
    double worstV;
    int VFRows;
    int VFCols;
    double rowGridSize;
    double colGridSize;
    double xQueryIncrement;
    double yQueryIncrement;
    ValueFunctionDataProvider dataProvider;
    double currentValueFunctionResolution;
    double newValueFunctionResolution;
    Vector<Observation> theQueryObservations = null;
    DynamicControlTarget theControlTarget = null;
    JSlider numColsOrRowsForValueFunction = null;

    public ValueFunctionVizComponent(ValueFunctionDataProvider theDataProvider, DynamicControlTarget theControlTarget) {
        super();
        currentValueFunctionResolution = 10.0;
        this.theControlTarget = theControlTarget;


        this.dataProvider = theDataProvider;

        bestV = Double.MIN_VALUE;
        worstV = Double.MAX_VALUE;

        theQueryObservations = null;

        //Setup the slider
        numColsOrRowsForValueFunction = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
        numColsOrRowsForValueFunction.setPreferredSize(new Dimension(150, 50));
        numColsOrRowsForValueFunction.setSize(new Dimension(150, 50));
     
        setValueFunctionResolution(numColsOrRowsForValueFunction.getValue());
        JPanel tinyPanel=new JPanel();
        tinyPanel.setPreferredSize(new Dimension(150, 50));
        tinyPanel.add(numColsOrRowsForValueFunction);

        numColsOrRowsForValueFunction.addChangeListener(this);

        if (theControlTarget != null) {
            Vector<Component> newComponents = new Vector<Component>();
            JLabel valueFunctionResolutionLabel = new JLabel("Resolution for Value Function (right is finer)");
            valueFunctionResolutionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            newComponents.add(valueFunctionResolutionLabel);
//            newComponents.add(numColsOrRowsForValueFunction);
            newComponents.add(tinyPanel);
            theControlTarget.addControls(newComponents);
        }
    }

    public int getIndexForRow(int row, int col) {
        return row + col * VFRows;
    }

    Vector<Observation> getQueryStates() {
        Vector<Observation> thePoints = new Vector<Observation>();

        int y = 0;
        int x = 0;

        for (y = 0; y < VFRows; y++) {
            for (x = 0; x < VFCols; x++) {

                //Query the value function in the agent
                double positionVal = dataProvider.getMinValueForDim(0) + x * xQueryIncrement;
                double velocityVal = dataProvider.getMinValueForDim(1) + y * yQueryIncrement;

                Observation thisState = new Observation(0, 2);
                thisState.doubleArray[0] = positionVal;
                thisState.doubleArray[1] = velocityVal;
                thePoints.add(thisState);
            }
        }
        return thePoints;
    }

    public void render(Graphics2D g) {
        double y = 0;
        double x = 0;

        double thisBest = Double.MIN_VALUE;
        double thisWorst = Double.MAX_VALUE;

        if (theValues == null) {
            return;
        }

        int linearIndex = 0;

        for (y = 0; y < VFRows; y++) {
            for (x = 0; x < VFCols; x++) {
                Rectangle2D valueRect = new Rectangle2D.Double(x * rowGridSize, y * colGridSize, rowGridSize, colGridSize);
                double V = 0.0f;

                V = theValues.get(linearIndex);


                if (Double.isInfinite(V) || Double.isNaN(V)) {
                    System.out.println("The value at linear index: " + linearIndex + " + is " + V + "+ (size is " + theValues.size());
                }

                if (V < thisWorst) {
                    thisWorst = V;
                }
                if (V > thisBest) {
                    thisBest = V;
                }
                float greenValue = (float) UtilityShop.normalizeValue(V, worstV,  bestV);

                if (greenValue < 0) {
                    greenValue = 0;
                }
                if (greenValue > 1) {
                    greenValue = 1;
                }
                Color theColor = new Color(0, greenValue, 0);

                g.setColor(theColor);
                g.fill(valueRect);

                linearIndex++;
            }
        }

        worstV = thisWorst;
        bestV = thisBest;
    }
//quick hack
    long lastUpdateTime = 0;

    public boolean update() {
        boolean changedThisTime = false;

        if (newValueFunctionResolution != currentValueFunctionResolution || theQueryObservations == null) {

            currentValueFunctionResolution = newValueFunctionResolution;

            VFRows = (int) currentValueFunctionResolution;
            VFCols = (int) currentValueFunctionResolution;

            //The range of the position and velocity
			
            double xRangeSize = dataProvider.getMaxValueForDim(0) - dataProvider.getMinValueForDim(0);
            double yRangeSize = dataProvider.getMaxValueForDim(1) - dataProvider.getMinValueForDim(1);
			System.out.println(xRangeSize);
			System.out.println(yRangeSize);
            //QueryIncrements are the number that the query variables will change from cell to cell
            xQueryIncrement = xRangeSize / VFCols;
            yQueryIncrement = yRangeSize / VFRows;

            rowGridSize = 1.0d / VFRows;
            colGridSize = 1.0d / VFCols;

            Vector<Observation> theQueryStates = getQueryStates();
            theQueryObservations = dataProvider.getQueryObservations(theQueryStates);
            changedThisTime = true;
        }

        long CurrentTime = System.currentTimeMillis();
        if (CurrentTime - lastUpdateTime > 100 || changedThisTime) {
            lastUpdateTime = CurrentTime;
            theValues = dataProvider.queryAgentValues(theQueryObservations);
            return true;
        }
        return false;
    }

    public double getValueFunctionResolution() {
        return currentValueFunctionResolution;
    }

    public void setValueFunctionResolution(int theValue) {
        newValueFunctionResolution = theValue;
    }

    public void stateChanged(ChangeEvent sliderChangeEvent) {
        JSlider source = (JSlider) sliderChangeEvent.getSource();
        int theValue = source.getValue();
        setValueFunctionResolution(theValue);
    }
}