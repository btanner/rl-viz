/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rlVizLib.dynamicLoading;

public enum EnvOrAgentType {

    kEnv(0),
    kAgent(1),
    kBoth(2),
    kViz(3);
    private final int id;

    EnvOrAgentType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}