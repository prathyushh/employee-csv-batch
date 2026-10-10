package com.example.batch.exception;

public class InvalidCsvFileException extends RuntimeException {
	public InvalidCsvFileException(String message) {
		super(message);
	}
}
