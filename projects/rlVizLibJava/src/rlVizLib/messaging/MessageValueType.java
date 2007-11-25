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
package rlVizLib.messaging;

public enum MessageValueType {

    kStringList(0),
    kString(1),
    kBoolean(2),
    kNone(3);
    private final int id;

    MessageValueType(int id) {
        this.id = id;
    }

    public final int id() {
        return id;
    }

    public static String name(int id) {
        if (id == kStringList.id()) {
            return "kStringList";
        }
        if (id == kString.id()) {
            return "kString";
        }
        if (id == kBoolean.id()) {
            return "kBoolean";
        }
        if (id == kNone.id()) {
            return "kNone";
        }
        return "Type: " + id + " is unknown MessageValueType";
    }

    public static String name(MessageValueType valueType) {
        return name(valueType.id());
    }
}
