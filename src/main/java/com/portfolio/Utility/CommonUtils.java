package com.portfolio.Utility;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.portfolio.DAO.ProjectDao;

import jakarta.validation.ValidationException;

public class CommonUtils {

	private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);
	
	public static void checkProjectByTitle(ProjectDao portfolioDao, String title, String message) {
		if (portfolioDao.existsByTitle(title)) {
			throw new ValidationException(message);
		}
	}

	public static void logMethodEntry(Object caller) {
		String className = caller.getClass().getSimpleName();
		log.info("Inside {}.{}", className, getCallingMethodName());
	}

	public static void logMethodEntry(Object caller, String message) {
		String className = caller.getClass().getSimpleName();
		log.info("Inside {}.{}() → {}", className, getCallingMethodName(), message);
	}

	private static String getCallingMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public static HashMap<String, Object> prepareResponse(HashMap<String, Object> response, String message,
			boolean success) {
		response.put("status", success ? "0" : "1");
		response.put("message", message);
		return response;
	}

	public static void logError(Exception ex) {
		StackTraceElement origin = ex.getStackTrace()[0];
		log.error("Exception in {}.{}() → {}", origin.getClassName(), origin.getMethodName(), ex.getMessage(), ex);
	}

	public static ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message,
			Object errors) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		if (errors != null)
			body.put("details", errors);

		return new ResponseEntity<>(body, status);
	}

}
