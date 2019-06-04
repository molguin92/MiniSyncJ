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

import org.junit.jupiter.api.Test;
import com.github.molguin92.minisync.algorithm.IAlgorithm;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseAlgorithmTest {
    IAlgorithm algo;

    private static double NUM_LOOPS = 50;
    private static double FLOAT_DELTA = 0.001;

    private static double To = -1;
    private static double Tbr = 0;
    private static double Tbt = 1;
    private static double Tr = 2;

    @Test
    void base() {
        assertEquals(1.0, algo.getDrift(), FLOAT_DELTA);
        assertEquals(0.0, algo.getDriftError(), FLOAT_DELTA);
        assertEquals(0.0, algo.getOffset(), FLOAT_DELTA);
        assertEquals(0.0, algo.getOffsetError(), FLOAT_DELTA);
    }

    @Test
    void addDataPoints() {

        // initial offset and drift
        // initial coordinates are on x = 0, so max_offset and min_offset should simply be the y coordinates
        double high_drift = (To - Tr) / (Tbt - Tbr);
        double low_drift = (Tr - To) / (Tbt - Tbr);
        double expected_drift = (high_drift + low_drift) / 2.0;
        double expected_drift_error = (low_drift - high_drift) / 2.0;

        double expected_offset = (To + Tr) / 2.0;
        double expected_offset_error = (Tr - To) / 2.0;

        algo.addDataPoint(To, Tbr, Tr);
        // first point does not trigger update
        base();

        algo.addDataPoint(To, Tbt, Tr);
        assertEquals(expected_drift, algo.getDrift(), FLOAT_DELTA);
        assertEquals(expected_drift_error, algo.getDriftError(), FLOAT_DELTA);
        assertEquals(expected_offset, algo.getOffset(), FLOAT_DELTA);
        assertEquals(expected_offset_error, algo.getOffsetError(), FLOAT_DELTA);
    }

    static double currentTimeMicroSeconds() {
        return System.nanoTime() / 1000.0d;
    }

    @Test
    void increasingAccuracy() {
        // errors should always decrease
        double T0 = currentTimeMicroSeconds();
        double current_drift_error = Double.MAX_VALUE;
        double current_offset_error = Double.MAX_VALUE;
        Random r = new Random(System.nanoTime());
        for (int i = 0; i < NUM_LOOPS; ++i) {
            try {
                Thread.sleep(r.nextInt(10));
                double To = currentTimeMicroSeconds() - T0;
                Thread.sleep(r.nextInt(10));
                double Tb = currentTimeMicroSeconds() - T0;
                Thread.sleep(r.nextInt(10));
                double Tr = currentTimeMicroSeconds() - T0;

                algo.addDataPoint(To, Tb, Tr);

                if (i >= 1) { // algorithm needs at least two points
                    assertTrue(algo.getDriftError() <= current_drift_error);
                    assertTrue(algo.getOffsetError() <= current_offset_error);

                    current_drift_error = algo.getDriftError();
                    current_offset_error = algo.getOffsetError();
                }
            } catch (InterruptedException ignored) {
            }

        }
    }
}
