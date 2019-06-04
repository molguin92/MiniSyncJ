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

package se.kth.molguin.minisync.algorithm;

import se.kth.molguin.minisync.constraint.HighPoint;
import se.kth.molguin.minisync.constraint.LowPoint;

import java.util.HashMap;
import java.util.Map;

public class MiniSyncAlgorithm extends BaseAlgorithm {

    private Map<LowPoint, Map<LowPoint, Double>> low_slopes;
    private Map<HighPoint, Map<HighPoint, Double>> high_slopes;

    public MiniSyncAlgorithm() {
        super();
        this.low_slopes = new HashMap<>();
        this.high_slopes = new HashMap<>();
    }

    @Override
    void cleanup() {
    }

    @Override
    protected LowPoint addLowPoint(double Tb, double To) {
        LowPoint lp = super.addLowPoint(Tb, To);
        // calculate low slopes
        return lp;
    }

    @Override
    protected HighPoint addHighPoint(double Tb, double Tr) {
        HighPoint hp = new HighPoint(Tb, Tr);
        // calculate new constraints
        for (LowPoint lp : this.low_points)
            this.addConstraint(lp, hp);

        this.high_points.add(hp);
        return hp;
    }
}

