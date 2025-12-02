package com.agropulse.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class to extract the real client IP address from HTTP requests.
 * Handles proxies, load balancers, and X-Forwarded-For headers.
 */
public class IpAddressUtil {

    /**
     * Extracts the real client IP address from the HTTP request.
     * Checks X-Forwarded-For header first (for proxies/load balancers),
     * then X-Real-IP, and finally falls back to getRemoteAddr().
     * 
     * @param request The HTTP servlet request
     * @return The client IP address, or "unknown" if unable to determine
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = null;

        // Check X-Forwarded-For header (most common for proxies/load balancers)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For can contain multiple IPs, the first one is the original client
            ipAddress = xForwardedFor.split(",")[0].trim();
        }

        // If not found, check X-Real-IP header (used by some proxies)
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }

        // If still not found, check X-Forwarded header (alternative)
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Forwarded");
        }

        // Fall back to getRemoteAddr() (direct connection)
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Handle IPv6 localhost (various formats)
        if (ipAddress != null) {
            String normalized = ipAddress.toLowerCase().trim();
            if (normalized.equals("0:0:0:0:0:0:0:1") || 
                normalized.equals("::1") || 
                normalized.equals("[::1]") ||
                normalized.startsWith("0:0:0:0:0:0:0:1") ||
                normalized.startsWith("::1")) {
                ipAddress = "127.0.0.1";
            }
        }

        // Final fallback
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "unknown";
        }

        return ipAddress;
    }
}

