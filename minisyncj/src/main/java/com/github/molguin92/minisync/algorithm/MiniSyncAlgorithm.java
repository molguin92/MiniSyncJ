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
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Implementation of the improved MiniSync algorithm described in
 * <p>
 * [1] S. Yoon, C. Veerarittiphan, and M. L. Sichitiu. 2007. Tiny-sync: Tight time synchronization for wireless sensor networks. ACM Trans. Sen. Netw. 3, 2, Article 8 (June 2007). DOI: 10.1145/1240226.1240228
 * <p>
 * [2] M. L. Sichitiu and C. Veerarittiphan, "Simple, accurate time synchronization for wireless sensor networks," 2003 IEEE Wireless Communications and Networking, 2003. WCNC 2003., New Orleans, LA, USA, 2003, pp. 1266-1273 vol.2. DOI: 10.1109/WCNC.2003.1200555. URL: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=1200555&isnumber=27029
 */
public class MiniSyncAlgorithm extends BaseAlgorithm {

    private MultiKeyMap<LowPoint, Double> low_slopes;
    private MultiKeyMap<HighPoint, Double> high_slopes;

    public MiniSyncAlgorithm() {
        super();
        this.low_slopes = new MultiKeyMap<>();
        this.high_slopes = new MultiKeyMap<>();
    }

    @Override
    void cleanup() {

        // clean up low points
        Set<LowPoint> low_points_tbr = new HashSet<>();
        this.low_points.forEach(new Consumer<LowPoint>() {
            @Override
            public void accept(LowPoint Aj) {
                if (MiniSyncAlgorithm.this.current_high.lowPoint != Aj &&
                        MiniSyncAlgorithm.this.current_low.lowPoint != Aj) {
                    // point is not in the current constraints
                    // now we compare the slopes with each other point to see if we store it for future use or not
                    // if current point is Aj, compare each M(Ai, Aj) with M(Aj, Ak)
                    // store point Aj only iff there exists
                    // M(Ai, Aj) > M(Aj, Ak) for 0 <= i < j < k <= total number of points.

                    for (LowPoint Ai = MiniSyncAlgorithm.this.low_points.lower(Aj);
                         Ai != null;
                         Ai = MiniSyncAlgorithm.this.low_points.lower(Ai)) {
                        for (LowPoint Ak = MiniSyncAlgorithm.this.low_points.higher(Aj);
                             Ak != null;
                             Ak = MiniSyncAlgorithm.this.low_points.higher(Ak)) {

                            if (MiniSyncAlgorithm.this.low_slopes.get(Ai, Aj) > MiniSyncAlgorithm.this.low_slopes.get(Aj, Ak))
                                return;

                        }
                    }
                    // if we get here it's because we didn't find the necessary condition for storing the point
                    low_points_tbr.add(Aj); // add to delete list
                }
            }
        });

        // clean up high points
        Set<HighPoint> high_points_tbr = new HashSet<>();
        this.high_points.forEach(new Consumer<HighPoint>() {
            @Override
            public void accept(HighPoint Aj) {
                if (MiniSyncAlgorithm.this.current_high.highPoint != Aj &&
                        MiniSyncAlgorithm.this.current_low.highPoint != Aj) {
                    // point is not in the current constraints
                    // now we compare the slopes with each other point to see if we store it for future use or not
                    // if current point is Aj, compare each M(Ai, Aj) with M(Aj, Ak)
                    // store point Aj only iff there exists
                    // M(Ai, Aj) > M(Aj, Ak) for 0 <= i < j < k <= total number of points.

                    for (HighPoint Ai = MiniSyncAlgorithm.this.high_points.lower(Aj);
                         Ai != null;
                         Ai = MiniSyncAlgorithm.this.high_points.lower(Ai)) {
                        for (HighPoint Ak = MiniSyncAlgorithm.this.high_points.higher(Aj);
                             Ak != null;
                             Ak = MiniSyncAlgorithm.this.high_points.higher(Ak)) {

                            if (MiniSyncAlgorithm.this.high_slopes.get(Ai, Aj) > MiniSyncAlgorithm.this.high_slopes.get(Aj, Ak))
                                return;

                        }
                    }
                    // if we get here it's because we didn't find the necessary condition for storing the point
                    high_points_tbr.add(Aj); // add to delete list
                }
            }
        });

        // actually remove points
        this.low_points.removeAll(low_points_tbr);
        this.high_points.removeAll(high_points_tbr);

        // constraints
        this.high_constraints.removeIf(new Predicate<Line>() {
            @Override
            public boolean test(Line line) {
                return low_points_tbr.contains(line.lowPoint) || high_points_tbr.contains(line.highPoint);
            }
        });

        this.low_constraints.removeIf(new Predicate<Line>() {
            @Override
            public boolean test(Line line) {
                return low_points_tbr.contains(line.lowPoint) || high_points_tbr.contains(line.highPoint);
            }
        });

        // slopes
        low_points_tbr.forEach(new Consumer<LowPoint>() {
            @Override
            public void accept(LowPoint r_lp) {
                MiniSyncAlgorithm.this.low_points.forEach(new Consumer<LowPoint>() {
                    @Override
                    public void accept(LowPoint lp) {
                        MiniSyncAlgorithm.this.low_slopes.removeMultiKey(r_lp, lp);
                        MiniSyncAlgorithm.this.low_slopes.removeMultiKey(lp, r_lp);
                    }
                });
            }
        });

        high_points_tbr.forEach(new Consumer<HighPoint>() {
            @Override
            public void accept(HighPoint r_hp) {
                MiniSyncAlgorithm.this.high_points.forEach(new Consumer<HighPoint>() {
                    @Override
                    public void accept(HighPoint hp) {
                        MiniSyncAlgorithm.this.high_slopes.removeMultiKey(r_hp, hp);
                        MiniSyncAlgorithm.this.high_slopes.removeMultiKey(hp, r_hp);
                    }
                });
            }
        });
    }

    @Override
    protected LowPoint addLowPoint(double Tb, double To) {
        LowPoint lp = super.addLowPoint(Tb, To);
        // calculate new slopes including this point
        // the new point has the largest x value, so it should always go to the right in the tuple
        for (LowPoint olp : this.low_points) {
            if (lp.x == olp.x) continue;
            double slope = (lp.y - olp.y) / (lp.x - olp.x);
            this.low_slopes.put(olp, lp, slope);
        }
        return lp;
    }

    @Override
    protected HighPoint addHighPoint(double Tb, double Tr) {
        HighPoint hp = super.addHighPoint(Tb, Tr);
        // calculate new slopes including this point
        // the new point has the largest x value, so it should always go to the right in the tuple
        for (HighPoint ohp : this.high_points) {
            if (hp.x == ohp.x) continue;
            double slope = (hp.y - ohp.y) / (hp.x - ohp.x);
            this.high_slopes.put(ohp, hp, slope);
        }
        return hp;
    }
}

