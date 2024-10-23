package com.karrardelivery.logging;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Wrapper around HttpServletResponse that captures the output stream,
 * allowing it to be read multiple times. Useful for logging purposes.
 */
@Slf4j
public class ResponseLoggingWrapper extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream capturedBodyStream = new ByteArrayOutputStream();
	private final PrintWriter writer = new PrintWriter(capturedBodyStream);
	private long id;

	/**
	 * Constructs a new logging response wrapper.
	 *
	 * @param requestId An identifier to bind the response with its request.
	 * @param response The original HttpServletResponse.
	 */
	public ResponseLoggingWrapper(Long requestId, HttpServletResponse response) {
		super(response);
		this.id = requestId;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		final ServletOutputStream originalOutputStream = super.getOutputStream();

		return new ServletOutputStream() {
			private final TeeOutputStream teeStream = new TeeOutputStream(originalOutputStream, capturedBodyStream);

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
				// No implementation needed
			}

			@Override
			public void write(int b) throws IOException {
				teeStream.write(b);
			}
		};
	}

	/**
	 * Logs the captured response data.
	 */
	public void logResponse() {
		byte[] toLog = toByteArray();
		if (toLog.length > 0) {
			log.info("{}: http response {}", id, new String(toLog));
		}
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new TeePrintWriter(super.getWriter(), writer);
	}

	/**
	 * Returns the captured bytes from the response's output stream and then resets the buffer.
	 *
	 * @return The captured bytes.
	 */
	public byte[] toByteArray() {
		byte[] capturedBytes = capturedBodyStream.toByteArray();
		capturedBodyStream.reset();
		return capturedBytes;
	}

	/**
	 * Gets the ID of the response.
	 *
	 * @return The ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the response.
	 *
	 * @param id The ID to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
}