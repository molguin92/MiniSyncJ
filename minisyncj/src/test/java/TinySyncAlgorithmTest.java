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

import se.kth.molguin.minisync.algorithm.TinySyncAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinySyncAlgorithmTest {

    private TinySyncAlgorithm algo;

    private static double FLOAT_DELTA = 0.001;
    private static double To = -1;
    private static double Tbr = 0;
    private static double Tbt = 1;
    private static double Tr = 2;

    @BeforeEach
    void setUp() {
        algo = new TinySyncAlgorithm();
    }

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


}