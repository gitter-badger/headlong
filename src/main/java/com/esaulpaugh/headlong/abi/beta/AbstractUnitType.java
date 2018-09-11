package com.esaulpaugh.headlong.abi.beta;

import com.esaulpaugh.headlong.rlp.util.BizarroIntegers;
import com.esaulpaugh.headlong.rlp.util.RLPIntegers;

import java.math.BigInteger;

abstract class AbstractUnitType<V> extends StackableType<V> { // instance of V should be instanceof Number or Boolean

    static final int UNIT_LENGTH_BYTES = 32;
    static final int LOG_2_UNIT_LENGTH_BYTES = 31 - Integer.numberOfLeadingZeros(UNIT_LENGTH_BYTES);

    final int bitLength;
    final boolean unsigned;

    AbstractUnitType(String canonicalType, int bitLength, boolean unsigned) {
        super(canonicalType, false);
        this.bitLength = bitLength;
        this.unsigned = unsigned;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bitLength + ")";
    }

    @Override
    int byteLength(Object value) {
        return UNIT_LENGTH_BYTES;
    }

    // don't do unsigned check for array element
    void validatePrimitiveElement(long longVal) {
        final int bitLen = longVal >= 0 ? RLPIntegers.bitLen(longVal) : BizarroIntegers.bitLen(longVal);
        if(bitLen > bitLength) {
            throw new IllegalArgumentException("exceeds bit limit: " + bitLen + " > " + bitLength);
        }
    }

    // don't do unsigned check for array element
    void validateBigIntElement(final BigInteger bigIntVal) {
        if(bigIntVal.bitLength() > bitLength) {
            throw new IllegalArgumentException("exceeds bit limit: " + bigIntVal.bitLength() + " > " + bitLength);
        }
    }

    // --------------------------------

    void validateLongBitLen(long longVal) {
        final int bitLen = longVal >= 0 ? RLPIntegers.bitLen(longVal) : BizarroIntegers.bitLen(longVal);
        if(bitLen > bitLength) {
            throw new IllegalArgumentException("exceeds bit limit: " + bitLen + " > " + bitLength);
        }
        if(unsigned && longVal < 0) {
            throw new IllegalArgumentException("negative value for unsigned type");
        }
    }

    void validateBigIntBitLen(final BigInteger bigIntVal) {
        if(bigIntVal.bitLength() > bitLength) {
            throw new IllegalArgumentException("exceeds bit limit: " + bigIntVal.bitLength() + " > " + bitLength);
        }
        if(unsigned && bigIntVal.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("negative value for unsigned type");
        }
    }
}
