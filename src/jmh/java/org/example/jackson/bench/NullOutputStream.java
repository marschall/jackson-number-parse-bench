package org.example.jackson.bench;

import java.io.OutputStream;

/**
 * {@link OutputStream} that does nothing.
 */
final class NullOutputStream extends OutputStream {

    NullOutputStream() {
        super();
    }

    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte[] b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }


}
