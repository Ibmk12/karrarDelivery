package com.karrardelivery.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.TeeInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Wrapper around HttpServletRequest that captures the input stream
 * allowing it to be read multiple times. Useful for logging purposes.
 */
@Slf4j
public class RequestLoggingWrapper extends HttpServletRequestWrapper {

	private final ByteArrayOutputStream capturedBodyStream = new ByteArrayOutputStream();
	private long id;

	/**
	 * Constructs a new logging request wrapper.
	 *
	 * @param requestId An identifier to bind the request with its response.
	 * @param request The original HttpServletRequest.
	 */
	public RequestLoggingWrapper(Long requestId, HttpServletRequest request) {
		super(request);
		this.id = requestId;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ServletInputStream originalInputStream = super.getInputStream();

		return new ServletInputStream() {
			private final TeeInputStream teeStream = new TeeInputStream(originalInputStream, capturedBodyStream);

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// No implementation needed
			}

			@Override
			public int read() throws IOException {
				return teeStream.read();
			}
		};
	}

	/**
	 * Returns the captured bytes from the request's input stream.
	 *
	 * @return The captured bytes.
	 */
	public byte[] toByteArray() {
		return capturedBodyStream.toByteArray();
	}

	/**
	 * Gets the ID of the request.
	 *
	 * @return The ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the request.
	 *
	 * @param id The ID to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
}