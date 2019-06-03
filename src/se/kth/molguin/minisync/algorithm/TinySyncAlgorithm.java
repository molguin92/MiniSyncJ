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
import se.kth.molguin.minisync.constraint.Line;
import se.kth.molguin.minisync.constraint.LowPoint;

import java.util.function.Predicate;

public class TinySyncAlgorithm extends BaseAlgorithm {
    @Override
    void cleanup() {

        this.low_points.removeIf(new Predicate<LowPoint>() {
            @Override
            public boolean test(LowPoint lp) {
                return TinySyncAlgorithm.this.current_high.lowPoint != lp &&
                        TinySyncAlgorithm.this.current_low.lowPoint != lp;
            }
        });

        this.high_points.removeIf(new Predicate<HighPoint>() {
            @Override
            public boolean test(HighPoint hp) {
                return TinySyncAlgorithm.this.current_high.highPoint != hp &&
                        TinySyncAlgorithm.this.current_low.highPoint != hp;
            }
        });

        this.high_constraints.removeIf(new Predicate<Line>() {
            @Override
            public boolean test(Line line) {
                return !(TinySyncAlgorithm.this.high_points.contains(line.highPoint) &&
                        TinySyncAlgorithm.this.low_points.contains(line.lowPoint));

            }
        });

        this.low_constraints.removeIf(new Predicate<Line>() {
            @Override
            public boolean test(Line line) {
                return !(TinySyncAlgorithm.this.high_points.contains(line.highPoint) &&
                        TinySyncAlgorithm.this.low_points.contains(line.lowPoint));

            }
        });

    }
}
