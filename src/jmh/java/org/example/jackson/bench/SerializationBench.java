package org.example.jackson.bench;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@State(Scope.Benchmark)
public class SerializationBench {

    private Pojo pojo;

    private Writer writer;

    private OutputStream outputStream;

    private ObjectWriter defaultObjectWriter;

    private ObjectWriter fastObjectWriter;
    
    @Param({"true", "false"})
    public boolean fast;

    @Setup
    public void setUp() {
        this.pojo = Pojo.newRandomPojo(0L);
        this.writer = new NullWriter();
        this.outputStream = new NullOutputStream();
        this.defaultObjectWriter = new ObjectMapper()
                                        .writerFor(Pojo.class);
        this.fastObjectWriter = new ObjectMapper()
//                                        .enable(com.fasterxml.jackson.core.StreamWriteFeature.USE_FAST_DOUBLE_WRITER)
                                        .enable(com.fasterxml.jackson.core.JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)
                                        .writerFor(Pojo.class);
    }

    @Benchmark
    public Writer writer() throws IOException {
        ObjectWriter objectWriter = this.fast ? this.fastObjectWriter : this.defaultObjectWriter;
        objectWriter.writeValue(this.writer, this.pojo);
        return this.writer;
    }

    @Benchmark
    public OutputStream outputStream() throws IOException {
        ObjectWriter objectWriter = this.fast ? this.fastObjectWriter : this.defaultObjectWriter;
        objectWriter.writeValue(this.outputStream, this.pojo);
        return this.outputStream;
    }

}
