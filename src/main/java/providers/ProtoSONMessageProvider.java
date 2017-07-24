package providers;

import com.google.common.collect.ImmutableSet;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import static providers.ProtoSONMessageProvider.APPLICATION_PROTOBUF;

/**
 * A MessageBody[Reader|Writer] capable of serializing Protocol Buffer messages to/from JSON and binary Protocol
 * Buffer streams.
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON, APPLICATION_PROTOBUF})
@Produces({MediaType.APPLICATION_JSON, APPLICATION_PROTOBUF})
public class ProtoSONMessageProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message> {
    public static final String APPLICATION_PROTOBUF = "application/x-protobuf";
    private static final MediaType APPLICATION_PROTOBUF_TYPE = new MediaType("application", "x-protobuf");
    private static final Set<MediaType> SUPPORTED_CONTENT_TYPES = ImmutableSet.of(
            MediaType.APPLICATION_JSON_TYPE, APPLICATION_PROTOBUF_TYPE);

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type) && SUPPORTED_CONTENT_TYPES.contains(mediaType);
    }

    @Override
    public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException {
        if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            Message.Builder builder = getBuilderForType(type);
            JsonFormat.parser().ignoringUnknownFields().merge(new InputStreamReader(entityStream), builder);
            return builder.build();
        } else if (mediaType.equals(APPLICATION_PROTOBUF_TYPE)) {
            return getBuilderForType(type).mergeFrom(entityStream).build();
        } else {
            throw new IllegalArgumentException("ProtoSONMessageProvider can't read " + mediaType.toString());
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type) && SUPPORTED_CONTENT_TYPES.contains(mediaType);
    }

    @Override
    public long getSize(Message message, Class<?> type, Type genericType, Annotation[] annotations, MediaType
            mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Message message, Class<?> type, Type genericType, Annotation[] annotations, MediaType
            mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {
        if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            final OutputStreamWriter output = new OutputStreamWriter(entityStream);
            JsonFormat.printer().appendTo(message, output);
            output.flush();
        } else if (mediaType.equals(APPLICATION_PROTOBUF_TYPE)) {
            message.writeTo(entityStream);
        } else {
            throw new IllegalArgumentException("ProtoSONMessageProvider can't write " + mediaType.toString());
        }
    }

    private Message.Builder getBuilderForType(Class<Message> type) {
        try {
            Method newBuilder = type.getMethod("newBuilder");
            return (Message.Builder) newBuilder.invoke(type);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Oops", e);
        }
    }
}
