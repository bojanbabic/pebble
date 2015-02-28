package org.pebble.core.decoding.iterators.longs;

/**
 *  Copyright 2015 Groupon
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import it.unimi.dsi.io.InputBitStream;

import java.io.IOException;

/**
 * Implements the iterator of an strictly incremental list encoded using delta encoding. See
 * {@link org.pebble.core.encoding.OutputSuccinctStream#writeDelta(it.unimi.dsi.fastutil.longs.LongList, int) writeDelta}
 * for details regarding the compressed representation.
 */
class DeltaIterator extends CompressionIterator {

    /**
     * @param valueBitSize fixed number of bits used to represent value in list to be encoded. It can be any value
     *                     between 1bit and 63 bits.
     * @param inputBitStream input bit stream used to read the compressed list representation.
     * @throws java.io.IOException when there is an exception reading from <code>inputBitStream</code>.
     */
    public DeltaIterator(final int valueBitSize, final InputBitStream inputBitStream) throws IOException {
        super(inputBitStream);
        if (remainingElements > 0) {
            currentValue = inputBitStream.readLong(valueBitSize);
            remainingElements--;
        } else {
            currentValue = -1L;
        }
        recordOffset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long next() throws IOException {
        value = currentValue;
        if (remainingElements > 0) {
            seek();
            currentValue = inputBitStream.readDelta() + value + 1L;
            remainingElements--;
            recordOffset();
        } else {
            currentValue = -1;
        }
        return value;
    }

}