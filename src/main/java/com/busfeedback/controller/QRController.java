package com.busfeedback.controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class QRController {

    /**
     * GET /api/qr/{busId}
     * Returns a 300x300 PNG QR code pointing to the feedback form for that bus.
     *
     * Resolves the correct public-facing base URL even when the app is accessed
     * through a reverse proxy / tunnel such as ngrok, by honouring the standard
     * X-Forwarded-Proto and X-Forwarded-Host headers that ngrok sets.
     */
    @GetMapping("/{busId}")
    public void generateQR(@PathVariable String busId,
                           HttpServletRequest req,
                           HttpServletResponse res) throws IOException {

        String baseUrl = resolveBaseUrl(req);
        String feedbackUrl = baseUrl + "/feedback.html?busId=" + busId;

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix matrix = writer.encode(feedbackUrl, BarcodeFormat.QR_CODE, 300, 300, hints);

            res.setContentType("image/png");
            res.setHeader("Content-Disposition", "inline; filename=\"QR_" + busId + ".png\"");

            MatrixToImageWriter.writeToStream(matrix, "PNG", res.getOutputStream());

        } catch (WriterException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "QR generation failed");
        }
    }

    /**
     * Builds the externally-visible base URL (scheme + host [+ port]) for this request.
     * Falls back to the standard request scheme/host/port when no proxy headers are present
     * (e.g. when running purely on localhost without ngrok).
     */
    private String resolveBaseUrl(HttpServletRequest req) {
        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        String forwardedHost  = req.getHeader("X-Forwarded-Host");

        if (forwardedHost != null && !forwardedHost.isBlank()) {
            String scheme = (forwardedProto != null && !forwardedProto.isBlank())
                    ? forwardedProto
                    : req.getScheme();
            return scheme + "://" + forwardedHost;
        }

        // No proxy headers present — running directly (e.g. plain localhost)
        String scheme = req.getScheme();
        String host    = req.getServerName();
        int port       = req.getServerPort();

        boolean isDefaultPort = ("http".equals(scheme) && port == 80)
                              || ("https".equals(scheme) && port == 443);

        return isDefaultPort
                ? scheme + "://" + host
                : scheme + "://" + host + ":" + port;
    }
}
