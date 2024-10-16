package edu.internet2.middleware.grouperClientExt.com.fasterxml.jackson.core.exc;

import edu.internet2.middleware.grouperClientExt.com.fasterxml.jackson.core.*;

/**
 * Intermediate base class for all read-side streaming processing problems, including
 * parsing and input value coercion problems.
 *<p>
 * Added in 2.13 to eventually replace {@link edu.internet2.middleware.grouperClientExt.com.fasterxml.jackson.core.JsonGenerationException}.
 *
 * @since 2.13
 */
public abstract class StreamWriteException
    extends JsonProcessingException
{
    private final static long serialVersionUID = 2L;

    protected transient JsonGenerator _processor;

    protected StreamWriteException(Throwable rootCause, JsonGenerator g) {
        super(rootCause);
        _processor = g;
    }

    protected StreamWriteException(String msg, JsonGenerator g) {
        super(msg, (JsonLocation) null);
        _processor = g;
    }

    protected StreamWriteException(String msg, Throwable rootCause, JsonGenerator g) {
        super(msg, null, rootCause);
        _processor = g;
    }

    /**
     * Fluent method that may be used to assign originating {@link JsonGenerator},
     * to be accessed using {@link #getProcessor()}.
     *
     * @param g Generator to assign
     *
     * @return This exception instance (to allow call chaining)
     */
    public abstract StreamWriteException withGenerator(JsonGenerator g);

    @Override
    public JsonGenerator getProcessor() { return _processor; }
}
