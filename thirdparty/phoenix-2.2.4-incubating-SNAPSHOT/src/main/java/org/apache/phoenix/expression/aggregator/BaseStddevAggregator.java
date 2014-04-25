/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.expression.aggregator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import org.apache.hbase.index.util.ImmutableBytesPtr;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.schema.ColumnModifier;
import org.apache.phoenix.schema.PDataType;
import org.apache.phoenix.schema.tuple.Tuple;

/**
 * 
 * 
 * @since 1.2.1
 */
public abstract class BaseStddevAggregator extends DistinctValueWithCountClientAggregator {

    protected Expression stdDevColExp;
    private BigDecimal cachedResult = null;

    public BaseStddevAggregator(List<Expression> exps, ColumnModifier columnModifier) {
        super(columnModifier);
        this.stdDevColExp = exps.get(0);
    }

    @Override
    protected int getBufferLength() {
        return PDataType.DECIMAL.getByteSize();
    }

    @Override
    public boolean evaluate(Tuple tuple, ImmutableBytesWritable ptr) {
        if (cachedResult == null) {
            double ssd = sumSquaredDeviation();
            double result = Math.sqrt(ssd / getDataPointsCount());
            cachedResult = new BigDecimal(result);
        }
        if (buffer == null) {
            initBuffer();
        }
        buffer = PDataType.DECIMAL.toBytes(cachedResult);
        ptr.set(buffer);
        return true;
    }
    
    protected abstract long getDataPointsCount();
    
    private double sumSquaredDeviation() {
        double m = mean();
        double result = 0.0;
        for (Entry<ImmutableBytesPtr, Integer> entry : valueVsCount.entrySet()) {
            double colValue = (Double)PDataType.DOUBLE.toObject(entry.getKey(), this.stdDevColExp.getDataType());
            double delta = colValue - m;
            result += (delta * delta) * entry.getValue();
        }
        return result;
    }

    private double mean() {
        double sum = 0.0;
        for (Entry<ImmutableBytesPtr, Integer> entry : valueVsCount.entrySet()) {
            double colValue = (Double)PDataType.DOUBLE.toObject(entry.getKey(), this.stdDevColExp.getDataType());
            sum += colValue * entry.getValue();
        }
        return sum / totalCount;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.cachedResult = null;
    }
}
