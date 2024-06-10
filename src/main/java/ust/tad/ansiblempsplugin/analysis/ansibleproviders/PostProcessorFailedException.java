package ust.tad.ansiblempsplugin.analysis.ansibleproviders;

public class PostProcessorFailedException extends Exception {
    public PostProcessorFailedException(String errorMessage) {
        super(errorMessage);
    }
}
