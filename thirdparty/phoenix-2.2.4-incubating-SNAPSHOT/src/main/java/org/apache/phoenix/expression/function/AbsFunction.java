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
package org.apache.phoenix.expression.function;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.parse.FunctionParseNode.Argument;
import org.apache.phoenix.parse.FunctionParseNode.BuiltInFunction;
import org.apache.phoenix.schema.PDataType;
import org.apache.phoenix.schema.tuple.Tuple;

import java.sql.SQLException;
import java.util.List;

/**
 * Function used to bucketize date/time values by rounding them to
 * an even increment.  Usage:
 * ROUND(<date/time col ref>,<'day'|'hour'|'minute'|'second'|'millisecond'>,<optional integer multiplier>)
 * The integer multiplier is optional and is used to do rollups to a partial time unit (i.e. 10 minute rollup)
 * The function returns a {@link org.apache.phoenix.schema.PDataType#DATE}
 *
 * @since 0.1
 */
@BuiltInFunction(name = AbsFunction.NAME, args = {
        @Argument(allowedTypes = {PDataType.INTEGER, PDataType.DOUBLE, PDataType.TINYINT, PDataType.SMALLINT, PDataType.LONG,PDataType.FLOAT})})
//        @Argument(allowedTypes = { PDataType.INTEGER})})
public class AbsFunction extends ScalarFunction {
    public static final String NAME = "ABS";

    public AbsFunction() {
    }

    public AbsFunction(List<Expression> children) throws SQLException {
        super(children);

    }

    private Expression getExpression() {
        return children.get(0);
    }

    @Override
    public boolean evaluate(Tuple tuple, ImmutableBytesWritable ptr) {

        Expression child = getExpression();
        if (!child.evaluate(tuple, ptr)) {
            return false;
        }
        int len = ptr.getLength();
        if (len == 0) {
            return false;
        }
        PDataType datum = child.getDataType();
        PDataType.PDataCodec codec = child.getDataType().getCodec();

        if (datum== PDataType.INTEGER) {
//			if (len != 4)
//				return false;
            int a = codec.decodeInt(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeInt(-a, ptr);
            return true;
        } else if (datum== PDataType.LONG) {
            long a = codec.decodeLong(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeLong(-a, ptr);
            return true;
        } else if (datum== PDataType.DOUBLE) {
            double a = codec.decodeDouble(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeDouble(-a, ptr);
            return true;
        } else if (datum== PDataType.FLOAT) {
            float a = codec.decodeFloat(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeFloat(-a, ptr);
            return true;
        }
        else if (datum == PDataType.SMALLINT) {
            short a = codec.decodeShort(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeShort((short)-a, ptr);
            return true;
        }
        else if (datum == PDataType.TINYINT) {
            byte a = codec.decodeByte(ptr, getColumnModifier());
            if (a < 0)
                codec.encodeByte((byte)-a, ptr);
            return true;
        }

        return false;
    }

	/*
     * @Override public int hashCode() { final int prime = 31; int result = 1;
	 * long roundUpAmount = this.getRoundUpAmount(); result = prime * result +
	 * (int)(divBy ^ (divBy >>> 32)); result = prime * result +
	 * (int)(roundUpAmount ^ (roundUpAmount >>> 32)); result = prime * result +
	 * children.get(0).hashCode(); return result; }
	 * 
	 * @Override public boolean equals(Object obj) { if (this == obj) return
	 * true; if (obj == null) return false; if (getClass() != obj.getClass())
	 * return false; AbsFunction other = (AbsFunction)obj; if (divBy !=
	 * other.divBy) return false; if (getRoundUpAmount() !=
	 * other.getRoundUpAmount()) return false; return
	 * children.get(0).equals(other.children.get(0)); }
	 * 
	 * @Override public void readFields(DataInput input) throws IOException {
	 * super.readFields(input); divBy = WritableUtils.readVLong(input); }
	 */

//	@Override
//	public void write(DataOutput output) throws IOException {
//		super.write(output);
//		WritableUtils.writeVLong(output, divBy);
//	}

    @Override
    public final PDataType getDataType() {
        return children.get(0).getDataType();
    }

//	@Override
//	public Integer getByteSize() {
//		return children.get(0).getByteSize();
//	}
//
//	@Override
//	public boolean isNullable() {
//		return children.get(0).isNullable() ;
//	}

//	@Override
//	public OrderPreserving preservesOrder() {
//		return OrderPreserving.YES;
//	}

//	@Override
//	public int getKeyFormationTraversalIndex() {
//		return 0;
//	}

    @Override
    public String getName() {
        return NAME;
    }
}