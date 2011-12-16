package com.porpoise.common.functions;

public class InvalidKeyFunctionException extends RuntimeException {

	private final Object	objectInput;

	public InvalidKeyFunctionException(final Object objectInput, final Throwable arg1) {
		super(arg1);
		this.objectInput = objectInput;
	}

	public Object getObjectInput() {
		return this.objectInput;
	}

}
