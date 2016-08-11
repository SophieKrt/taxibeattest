package skritikou.taxibeattest.Models;

/**
 * Created by sofi on 09/08/16.
 */

public class LabeledDisplay {
    private String label = "";
    private double labeledLat = 0;
    private double labeledLng = 0;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLabeledLat() {
        return labeledLat;
    }

    public void setLabeledLat(double labeledLat) {
        this.labeledLat = labeledLat;
    }

    public double getLabeledLng() {
        return labeledLng;
    }

    public void setLabeledLng(double labeledLng) {
        this.labeledLng = labeledLng;
    }
}
