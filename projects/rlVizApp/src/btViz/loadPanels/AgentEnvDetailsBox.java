/*
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package btViz.loadPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import rlVizLib.general.ParameterHolder;
import rlVizLib.rlVizCore;

/**
 *
 * @author Brian Tanner
 */
public class AgentEnvDetailsBox implements ActionListener{
ParameterHolder theParamHolder;

    public AgentEnvDetailsBox(ParameterHolder theParamHolder){
        this.theParamHolder=theParamHolder;
        
    }
    public void actionPerformed(ActionEvent e) {
        createAboutBox();
    }

    private void createAboutBox() {
        String name="Details not specified.";
        String description="";
        String authors="";
        String url="";
        String loadName="unknown";
        String loadSource="unknown";

                        
        if (theParamHolder.isParamSet("###name")) {
            name = theParamHolder.getStringParam("###name");
        }

        if (theParamHolder.isParamSet("###description")) {
            description = theParamHolder.getStringParam("###description");
        }
        if (theParamHolder.isParamSet("###authors")) {
            authors = theParamHolder.getStringParam("###authors");
        }

        if (theParamHolder.isParamSet("###url")) {
            url = theParamHolder.getStringParam("###url");
        }
        if (theParamHolder.isParamSet("###loadname")) {
            loadName = theParamHolder.getStringParam("###loadname");
        }
        if (theParamHolder.isParamSet("###loadsource")) {
            loadSource = theParamHolder.getStringParam("###loadsource");
        }
        
        //default title and icon
        String theMessage =name;
        theMessage += "\n----------------------";
        theMessage += "\n"+description;
        theMessage += "\n\nCreated by: "+authors;
        theMessage += "\n"+url;
        theMessage += "\n\nTechnical Details\n----------------------";
        
        theMessage += "\nFull Qualified: "+loadName;
        theMessage += "\nLoaded From: "+loadSource;
        
        

        JOptionPane.showMessageDialog(null, theMessage, "About "+name, JOptionPane.INFORMATION_MESSAGE);
    }

    
}
