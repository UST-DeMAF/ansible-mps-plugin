package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public interface Module {
    String getName();
    HashSet<String> getVars();
}
