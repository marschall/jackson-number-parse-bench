package org.example.jackson.bench;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@State(Scope.Benchmark)
public class SerializationBench {

    private Pojo[] pojos;

    private Writer writer;

    private OutputStream outputStream;

    private ObjectWriter defaultObjectWriter;

    private ObjectWriter fastObjectWriter;
    
    @Param({"true", "false"})
    public boolean useFastWriter;

    @Setup
    public void setUp() {
        this.pojos = generatePojos(100);
        this.writer = new NullWriter();
        this.outputStream = new NullOutputStream();
        this.defaultObjectWriter = new ObjectMapper().writerFor(Pojo[].class);
        this.fastObjectWriter = this.defaultObjectWriter.with(StreamWriteFeature.USE_FAST_DOUBLE_WRITER);
    }
    
    private static Pojo[] generatePojos(int size) {
        List<Pojo> pojos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pojos.add(Pojo.newRandomPojo(i));
        }
        return pojos.toArray(new Pojo[0]);
    }

    @Benchmark
    public Writer writer() throws IOException {
        ObjectWriter objectWriter = this.useFastWriter ? this.fastObjectWriter : this.defaultObjectWriter;
        objectWriter.writeValue(this.writer, this.pojos);
        return this.writer;
    }

    @Benchmark
    public OutputStream outputStream() throws IOException {
        ObjectWriter objectWriter = this.useFastWriter ? this.fastObjectWriter : this.defaultObjectWriter;
        objectWriter.writeValue(this.outputStream, this.pojos);
        return this.outputStream;
    }

}
