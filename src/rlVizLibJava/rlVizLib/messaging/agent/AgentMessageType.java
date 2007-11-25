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
package rlVizLib.messaging.agent;

//These numberings aren't in order and there are holes, wanted to make similar ids match between env and agent asmap

public enum AgentMessageType {

    kAgentResponse(0),
    kAgentQueryValuesForObs(1),
    kAgentCustom(2),
    kAgentQuerySupportedVersion(4),
    kAgentQueryVisualizerName(6);
    private final int id;

    AgentMessageType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static String name(int id) {
        if (id == kAgentResponse.id()) {
            return "kAgentResponse";
        }
        if (id == kAgentQueryValuesForObs.id()) {
            return "kAgentQueryValuesForObs";
        }
        if (id == kAgentCustom.id()) {
            return "kAgentCustom";
        }
        if (id == kAgentQuerySupportedVersion.id()) {
            return "kAgentQuerySupportedVersion";
        }
        if (id == kAgentQueryVisualizerName.id()) {
            return "kAgentQueryVisualizerName";
        }
        return "Type: " + id + " is unknown AgentMessageType";
    }

    public static String name(AgentMessageType mType) {
        return name(mType.id());
    }
}