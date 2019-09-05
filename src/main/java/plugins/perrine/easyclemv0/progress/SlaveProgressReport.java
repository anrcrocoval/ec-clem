/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.progress;

public class SlaveProgressReport extends ProgressReport implements Cloneable {
    private int total;
    private int completed = 0;

    public SlaveProgressReport(int total) {
        this.total = total;
    }

    public SlaveProgressReport(int completed, int total) {
        this.completed = completed;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public void incrementCompleted() {
        completed++;
    }

    @Override
    public SlaveProgressReport clone() {
        SlaveProgressReport clone = null;
        try {
            clone = (SlaveProgressReport) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
