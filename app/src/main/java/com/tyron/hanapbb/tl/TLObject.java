package com.tyron.hanapbb.tl;

public class TLObject {

    public boolean disableFree = false;
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new ThreadLocal<NativeByteBuffer>() {
        @Override
        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    };

    public TLObject() {

    }

    public void readParams(AbstractSerializedData stream, boolean exception) {

    }

    public void serializeToStream(AbstractSerializedData stream) {

    }

    public TLObject deserializeResponse(AbstractSerializedData stream, int constructor,
                                        boolean exception) {
        return null;
    }

    public void freeResources() {

    }

    public int getObjectSize() {
        NativeByteBuffer byteBuffer = sizeCalculator.get();
        byteBuffer.rewind();
        serializeToStream(sizeCalculator.get());
        return byteBuffer.length();
    }
}
