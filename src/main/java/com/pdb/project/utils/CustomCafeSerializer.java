package com.pdb.project.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pdb.project.model.Cafe;

import java.io.IOException;
import java.util.List;

public class CustomCafeSerializer extends StdSerializer<List<Cafe>> {
    public CustomCafeSerializer() {
        this(null);
    }

    public CustomCafeSerializer(Class<List<Cafe>> t) {
        super(t);
    }

    @Override
    public void serialize(List<Cafe> cafes, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        //long[] ids = cafes.stream().map(Cafe::getId).mapToLong(x -> x).toArray();
        gen.writeStartArray();
        for (Cafe cafe: cafes) {
            gen.writeStartObject();
            gen.writeStringField("id", cafe.getId().toString());
            gen.writeStringField("name", cafe.getName());
            gen.writeStringField("address", cafe.getAddress());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}