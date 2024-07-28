package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public class Host {
    private String hostName;
    private HashSet<Variable> vars;


    public Host(String hostName, HashSet<Variable> vars) {
        this.hostName = hostName;
        this.vars = vars;
    }

    // Getters and Setters
    public String getHostName(){
        return this.hostName;
    }
    public void setHostName(String hostName){
        this.hostName = hostName;
    }
    public HashSet<Variable> getVars(){
        return this.vars;
    }
    public void setVars(HashSet<Variable> vars){
        this.vars = vars;
    }

}
