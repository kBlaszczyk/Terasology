/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.persistence.serializers;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;
import org.terasology.ModuleEnvironmentTest;
import org.terasology.naming.Name;
import org.terasology.persistence.ModuleContext;
import org.terasology.persistence.typeHandling.TypeHandlerLibrary;
import org.terasology.reflection.TypeInfo;
import org.terasology.testUtil.TeraAssert;

import java.io.IOException;

public class VectorTypeSerializerTest extends ModuleEnvironmentTest {

    static class TestObject{
        public Vector3f v1;
        public Vector2f v2;
        public Vector4f v3;
        public org.joml.Vector3f v11;
        public org.joml.Vector2f v22;
        public org.joml.Vector4f v33;
    }
    static class TestObject1 {
        public org.joml.Vector3f v1;
        public org.joml.Vector2f v2;
        public org.joml.Vector4f v3;
    }

    private TypeHandlerLibrary typeHandlerLibrary;
    private ProtobufSerializer protobufSerializer;
    private GsonSerializer gsonSerializer;

    @Override
    public void setup() {
        ModuleContext.setContext(moduleManager.getEnvironment().get(new Name("unittest")));

        typeHandlerLibrary = TypeHandlerLibrary.forModuleEnvironment(moduleManager, typeRegistry);

        protobufSerializer = new ProtobufSerializer(typeHandlerLibrary);
        gsonSerializer = new GsonSerializer(typeHandlerLibrary);
    }

    @Test
    public void testJsonSerializeRemapped() throws IOException {
        TestObject a = new TestObject();
        a.v1 = new Vector3f(11.5f, 13.15f, 3);
        a.v2 = new Vector2f(12, 13f);
        a.v3 = new Vector4f(12, 12.2f, 3f, 15.5f);
        a.v11 = new Vector3f(11.5f, 13.15f, 3);
        a.v22 = new Vector2f(12, 13f);
        a.v33 = new Vector4f(12, 12.2f, 3f, 15.5f);

        String data = gsonSerializer.toJson(a, new TypeInfo<TestObject>() {
        });

        TestObject1 o = gsonSerializer.fromJson(data, new TypeInfo<TestObject1>() {
        });

        TeraAssert.assertEquals(o.v1, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v2, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v3, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);

    }

    @Test
    public void testProtobufSerializeRemapped() throws IOException {
        TestObject a = new TestObject();
        a.v1 = new Vector3f(11.5f, 13.15f, 3);
        a.v2 = new Vector2f(12, 13f);
        a.v3 = new Vector4f(12, 12.2f, 3f, 15.5f);
        a.v11 = new Vector3f(11.5f, 13.15f, 3);
        a.v22 = new Vector2f(12, 13f);
        a.v33 = new Vector4f(12, 12.2f, 3f, 15.5f);

        byte[] data = protobufSerializer.toBytes(a, new TypeInfo<TestObject>() {
        });

        TestObject1 o = protobufSerializer.fromBytes(data, new TypeInfo<TestObject1>() {
        });

        TeraAssert.assertEquals(o.v1, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v2, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v3, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);

    }

    @Test
    public void testJsonSerialize() throws IOException {
        TestObject a = new TestObject();
        a.v1 = new Vector3f(11.5f, 13.15f, 3);
        a.v2 = new Vector2f(12, 13f);
        a.v3 = new Vector4f(12, 12.2f, 3f, 15.5f);
        a.v11 = new Vector3f(11.5f, 13.15f, 3);
        a.v22 = new Vector2f(12, 13f);
        a.v33 = new Vector4f(12, 12.2f, 3f, 15.5f);

        String data = gsonSerializer.toJson(a, new TypeInfo<TestObject>() {
        });

        TestObject o = gsonSerializer.fromJson(data, new TypeInfo<TestObject>() {
        });

        TeraAssert.assertEquals(o.v1, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v2, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v3, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);

        TeraAssert.assertEquals(o.v11, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v22, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v33, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);
    }

    @Test
    public void testProtobufSerialize() throws IOException {
        TestObject a = new TestObject();
        a.v1 = new Vector3f(11.5f, 13.15f, 3);
        a.v2 = new Vector2f(12, 13f);
        a.v3 = new Vector4f(12, 12.2f, 3f, 15.5f);
        a.v11 = new Vector3f(11.5f, 13.15f, 3);
        a.v22 = new Vector2f(12, 13f);
        a.v33 = new Vector4f(12, 12.2f, 3f, 15.5f);

        byte[] bytes = protobufSerializer.toBytes(a, new TypeInfo<TestObject>() {
        });

        TestObject o = protobufSerializer.fromBytes(bytes, new TypeInfo<TestObject>() {
        });

        TeraAssert.assertEquals(o.v1, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v2, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v3, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);

        TeraAssert.assertEquals(o.v11, new Vector3f(11.5f, 13.15f, 3), .00001f);
        TeraAssert.assertEquals(o.v22, new Vector2f(12f, 13f), .00001f);
        TeraAssert.assertEquals(o.v33, new Vector4f(12, 12.2f, 3f, 15.5f), .00001f);
    }

}
