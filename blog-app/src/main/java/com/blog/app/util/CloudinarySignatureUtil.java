package com.blog.app.util;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;

public class CloudinarySignatureUtil {
	public static String generateSignature(Map<String, String> paramsToSign, String apiSecret) {
	    // Alphabetical sort and concatenation
	    String data = paramsToSign.entrySet().stream()
	        .sorted(Map.Entry.comparingByKey())
	        .map(entry -> entry.getKey() + "=" + entry.getValue())
	        .collect(Collectors.joining("&"));

	    return DigestUtils.sha1Hex(data + apiSecret);  // Apache Commons Codec
	}
}
