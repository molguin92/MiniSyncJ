/**********************************************************************************************************************
 * Copyright (c) 2019 Manuel Olguín Muñoz <molguin@kth.se>                                                            *
 *                                                                                                                    *
 * This file is part of MiniSyncJ.                                                                                    *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License");                                                    *
 * you may not use this file except in compliance with the License.                                                   *
 * You may obtain a copy of the License at                                                                            *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * See also the LICENSE file at the root directory of this repository.                                                *
 * Unless required by applicable law or agreed to in writing, software                                                *
 * distributed under the License is distributed on an "AS IS" BASIS,                                                  *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.                                           *
 * See the License for the specific language governing permissions and                                                *
 * limitations under the License.                                                                                     *
 **********************************************************************************************************************/

package com.github.molguin92.minisync.algorithm;

import com.github.molguin92.minisync.constraint.HighPoint;
import com.github.molguin92.minisync.constraint.Line;
import com.github.molguin92.minisync.constraint.LowPoint;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

abstract class BaseAlgorithm implements IAlgorithm {

    protected final Set<Line> low_constraints;
    protected final Set<Line> high_constraints;
    protected final TreeSet<HighPoint> high_points;
    protected final TreeSet<LowPoint> low_points;

    protected Line current_high;
    protected Line current_low;

    private Value offset;
    private Value drift;

    private double diff_factor;
    private int processed_datapoints;
    private double minimum_local_delay; // (for instance, one way through the network stack)
    private double minimum_remote_delay;

    protected BaseAlgorithm() {
        this.low_constraints = new HashSet<>();
        this.high_constraints = new HashSet<>();
        this.low_points = new TreeSet<>();
        this.high_points = new TreeSet<>();

        this.offset = new Value();
        this.drift = new Value(1.0, 0.0);

        this.diff_factor = Double.MAX_VALUE;
        this.processed_datapoints = 0;
        this.minimum_local_delay = 0.0d;
        this.minimum_remote_delay = 0.0d;
    }

    @Override
    public void addDataPoint(double To, double Tb, double Tr) {
        // adjust delays
        To += this.minimum_local_delay;
        Tb -= this.minimum_remote_delay;
        Tr -= this.minimum_local_delay;

        // add points to algorithm
        this.addLowPoint(Tb, To);
        this.addHighPoint(Tb, Tr);
        ++this.processed_datapoints;

        if (this.processed_datapoints > 1)
            this.recalculateEstimates();
    }

    private void recalculateEstimates() {

        double tmp_diff;
        for (Line low : low_constraints) {
            for (Line high : high_constraints) {
                tmp_diff = (low.A - high.A) * (high.B - low.B);
                if (tmp_diff < this.diff_factor) {
                    this.diff_factor = tmp_diff;
                    this.current_low = low;
                    this.current_high = high;
                }
            }
        }

        this.cleanup();

        this.drift.value = (current_low.A + current_high.A) / 2;
        this.offset.value = (current_low.B + current_high.B) / 2;
        this.drift.error = (current_low.A - current_high.A) / 2;
        this.offset.error = (current_high.B - current_low.B) / 2;

        assert this.drift.value >= 0;
    }

    protected LowPoint addLowPoint(double Tb, double To) {
        LowPoint lp = new LowPoint(Tb, To);
        // calculate new constraints
        for (HighPoint hp : this.high_points)
            this.addConstraint(lp, hp);

        this.low_points.add(lp);
        return lp;
    }

    protected HighPoint addHighPoint(double Tb, double Tr) {
        HighPoint hp = new HighPoint(Tb, Tr);
        // calculate new constraints
        for (LowPoint lp : this.low_points)
            this.addConstraint(lp, hp);

        this.high_points.add(hp);
        return hp;
    }

    protected boolean addConstraint(LowPoint lp, HighPoint hp) {
        if (lp.x == hp.x) return false;

        Line constraint = new Line(lp, hp);
        switch (constraint.type) {
            case LOW_TO_HIGH: {
                this.low_constraints.add(constraint);
                break;
            }
            case HIGH_TO_LOW: {
                this.high_constraints.add(constraint);
                break;
            }
        }

        return true;
    }

    abstract void cleanup();

    private class Value {
        double value;
        double error;

        Value(double v, double e) {
            this.value = v;
            this.error = e;
        }

        Value() {
            this(0, 0);
        }
    }

    @Override
    public double getDrift() {
        return this.drift.value;
    }

    @Override
    public double getDriftError() {
        return this.drift.error;
    }

    @Override
    public double getOffset() {
        return this.offset.value;
    }

    @Override
    public double getOffsetError() {
        return this.offset.error;
    }

    @Override
    public int numDataPoints() {
        return this.processed_datapoints;
    }

    @Override
    public void setMinimumDelay(double d) {
        this.minimum_remote_delay = d;
        this.minimum_local_delay = d;
    }

    @Override
    public void setMinimumLocalDelay(double d) {
        this.minimum_local_delay = d;
    }

    @Override
    public void setMinimumRemoteDelay(double d) {
        this.minimum_remote_delay = d;
    }
}
