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

package com.github.molguin92.minisync.constraint;

public class Line {
    public enum TYPE {
        LOW_TO_HIGH,
        HIGH_TO_LOW;
    }

    public final TYPE type;
    public final HighPoint highPoint;
    public final LowPoint lowPoint;

    public final double A;
    public final double B;

    public Line(LowPoint low, HighPoint high) {

        assert low.x != high.x; // TODO maybe not use assertions

        this.lowPoint = low;
        this.highPoint = high;

        this.A = (low.y - high.y) / (low.x - high.x);
        this.B = low.y - (this.A * low.x);

        if (low.x < high.x)
            this.type = TYPE.LOW_TO_HIGH;
        else
            this.type = TYPE.HIGH_TO_LOW;
    }

    public Line(HighPoint high, LowPoint low) {
        this(low, high);
    }

    public boolean equals(Line o) {
        return this.A == o.A && this.B == o.B;
    }

}
