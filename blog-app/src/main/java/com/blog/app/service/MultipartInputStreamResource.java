package com.blog.app.service;

import org.springframework.core.io.ByteArrayResource;

public class MultipartInputStreamResource extends ByteArrayResource {
    private final String filename;

    public MultipartInputStreamResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return this.getByteArray().length;
    }
}