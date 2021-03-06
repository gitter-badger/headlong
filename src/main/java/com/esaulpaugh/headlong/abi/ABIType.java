package com.esaulpaugh.headlong.abi;

import com.esaulpaugh.headlong.abi.util.ClassNames;

import java.io.Serializable;
import java.nio.ByteBuffer;

import static com.esaulpaugh.headlong.abi.UnitType.UNIT_LENGTH_BYTES;

public abstract class ABIType<J> implements Serializable {

    static final int TYPE_CODE_BOOLEAN = 0;
    static final int TYPE_CODE_BYTE = 1;
    static final int TYPE_CODE_INT = 2;
    static final int TYPE_CODE_LONG = 3;
    static final int TYPE_CODE_BIG_INTEGER = 4;
    static final int TYPE_CODE_BIG_DECIMAL = 5;

    static final int TYPE_CODE_ARRAY = 6;
    static final int TYPE_CODE_TUPLE = 7;

    public static final ABIType<?>[] EMPTY_TYPE_ARRAY = new ABIType<?>[0];

    final String canonicalType;
    final Class<?> clazz;
    final boolean dynamic;

    private String name;

    ABIType(String canonicalType, Class<?> clazz, boolean dynamic) {
        this.canonicalType = canonicalType;
        this.clazz = clazz;
        this.dynamic = dynamic;
        this.name = null;
    }

    ABIType setName(String name) {
        this.name = name;
        return this;
    }

    public String getCanonicalType() {
        return canonicalType;
    }

    public String getName() {
        return name;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public Class<?> clazz() {
        return clazz;
    }

    abstract String arrayClassNameStub();

    abstract int typeCode();

    abstract int byteLength(Object value);

    abstract int byteLengthPacked(Object value);

    public abstract J parseArgument(String s);

    public abstract int validate(Object value);

    /**
     * Decodes the data at the buffer's current position according to this {@link ABIType}.
     *
     * @param buffer    the buffer containing the encoded data
     * @param unitBuffer a buffer of length {@link UnitType#UNIT_LENGTH_BYTES} in which to store intermediate values
     * @return  the decoded value
     */
    abstract J decode(ByteBuffer buffer, byte[] unitBuffer);

    void validateClass(Object value) {
        // may throw NPE
        if(clazz != value.getClass()) {
            // this pretty much only happens in the error case
            if(!clazz.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("class mismatch: "
                        + value.getClass().getName()
                        + " not assignable to "
                        + clazz.getName()
                        + " (" + ClassNames.toFriendly(value.getClass().getName()) + " not instanceof " + ClassNames.toFriendly(clazz.getName()) + "/" + canonicalType + ")");
            }
        }
    }

    static byte[] newUnitBuffer() {
        return new byte[UNIT_LENGTH_BYTES];
    }

    @Override
    public int hashCode() {
        return canonicalType.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return canonicalType.equals(((ABIType<?>) o).canonicalType);
    }

    @Override
    public String toString() {
        return canonicalType;
    }

    void toString(StringBuilder sb) {
        switch (typeCode()) {
        case TYPE_CODE_ARRAY:
            sb.append("[");
            ((ArrayType) this).elementType.toString(sb);
            sb.append("]");
            break;
        case TYPE_CODE_TUPLE:
            sb.append("(");
            for(ABIType<?> e : ((TupleType) this)) {
                e.toString(sb);
            }
            sb.append(")");
            break;
        default:
            sb.append(this);
        }
        sb.append(' ').append(getName()).append(',');
    }
}
