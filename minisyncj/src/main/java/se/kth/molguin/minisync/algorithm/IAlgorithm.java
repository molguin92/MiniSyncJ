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

public interface IAlgorithm {

    /**
     * Adds a data point to the algorithm and recalculates the estimates.
     *
     * @param To Outgoing beacon timestamp.
     * @param Tb Reply timestamp on the remote peer.
     * @param Tr Reply reception timestamp.
     */
    void addDataPoint(double To, double Tb, double Tr);

    /**
     * Get the current estimated relative clock drift.
     * If the number of samples provided to the algorithm so far is less than 2, returns 1.0.
     *
     * @return The relative clock drift.
     */
    double getDrift();

    /**
     * Get the current estimated relative clock drift error.
     * If the number of samples provided to the algorithm so far is less than 2, returns o.0.
     *
     * @return The relative clock drift error.
     */
    double getDriftError();

    /**
     * Get the current estimated relative clock offset.
     * If the number of samples provided to the algorithm so far is less than 2, returns 0.0.
     *
     * @return The relative clock offset.
     */
    double getOffset();

    /**
     * Get the current estimated relative clock offset error.
     * If the number of samples provided to the algorithm so far is less than 2, returns 0.0.
     *
     * @return The relative clock offset error.
     */
    double getOffsetError();


    /**
     * Get the number of processed data points so far. Note that this does not equal the number of data points currently
     * stored in the algorithm, but rather the total number of data points seen so far.
     *
     * @return The number of processed data points so far.
     */
    int numDataPoints();
}
