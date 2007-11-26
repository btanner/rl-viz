/* RLViz Application, a visualizer and dynamic loader for C++ and Java RL-Glue agents/environments
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
package proving;

import btViz.frames.MacOSAboutHandler;
import btViz.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;





public class ProvingVizFrame extends JFrame implements ActionListener {

    //Components
    
    JPanel activityPanel=new JPanel();

    RLGlueLogic theGlueConnection = null;
    
    static String programName = "RLVizApp";
    private static final long serialVersionUID = 1L;


    public ProvingVizFrame() {
        super();


        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));


        activityPanel = new JPanel();
        activityPanel.add(new JLabel("Proving Activity Panel"));

        //
        //Setup the border for the Environment Visualizer
        //NOTE: WE DO THIS AGAIN in VisualizerPanel.notifyOfVisualizerLoaded()
        //
        TitledBorder titled = null;
        Border loweredetched = null;
        loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        titled = BorderFactory.createTitledBorder(loweredetched, "Environment Visualizer");
        activityPanel.setBorder(titled);


//        JPanel controlPanel = new RLControlPanel(theGlueConnection);
//        controlPanel.setMaximumSize(new Dimension(500, 1000));
//
        getContentPane().add(activityPanel);
//        getContentPane().add(controlPanel);



        createMenus();


        setSize(1000, 600);
        setVisible(true);

        //Make sure we exit if they close the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("RLViz App 1.0");
    }

    public static void showAboutBox() {
        //default title and icon
        String theMessage=programName+" was created by Brian Tanner and the RLAI group at the University of Alberta.";
        theMessage+="\nYou're probably using as part of the RL Competition.  Good luck!  http://rl-competition.org";
        theMessage+="\nCopyright 2007.";
        theMessage+="\nhttp://brian.tannerpages.com  email: brian@tannerpages.com";
        JOptionPane.showMessageDialog(null,theMessage, "About "+programName, JOptionPane.INFORMATION_MESSAGE);
    }


    JMenuItem aboutButton=null;
    JMenuItem quitButton=null;
    protected void createMenus() {

        //Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu;
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Check if we're on a mac
        if (System.getProperty("mrj.version") == null) {
            //Linux
        menu = new JMenu(programName);
        menuBar.add(menu);

        aboutButton = new JMenuItem("About " + programName);
        menu.add(aboutButton);
  
        aboutButton.addActionListener(this);
        quitButton = new JMenuItem("Quit " + programName);
        menu.add(quitButton);
        quitButton.addActionListener(this);

        setJMenuBar(menuBar);
        } else {
            // the Mac specific code will go here
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            new MacOSAboutHandler();

        }
    }

    public void actionPerformed(ActionEvent theEvent) {
        if(theEvent.getSource()==aboutButton)
           showAboutBox();
       if(theEvent.getSource()==quitButton)
           System.exit(0);
    }
}