package com.twilio.video;

import com.twilio.video.internal.Logger;

import org.webrtc.CameraEnumerationAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CameraCapturerFormatProvider {
    private static final Logger logger = Logger.getLogger(CameraCapturerFormatProvider.class);

    private final Map<CameraCapturer.CameraSource, List<CaptureFormat>> supportedFormatsMap =
            new HashMap<>();

    static int getCameraId(CameraCapturer.CameraSource cameraSource) {
        String deviceName;
        int cameraId = -1;

        if(cameraSource == CameraCapturer.CameraSource.CAMERA_SOURCE_BACK_CAMERA) {
            deviceName = CameraEnumerationAndroid.getNameOfBackFacingDevice();
        } else {
            deviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
        }
        if(deviceName == null) {
            cameraId = -1;
        } else {
            String[] deviceNames = CameraEnumerationAndroid.getDeviceNames();
            for(int i = 0; i < deviceNames.length; i++) {
                if(deviceName.equals(deviceNames[i])) {
                    cameraId = i;
                    break;
                }
            }
        }

        return cameraId;
    }

    List<CaptureFormat> getSupportedFormats(CameraCapturer.CameraSource cameraSource) {
        List<CaptureFormat> supportedFormats = supportedFormatsMap.get(cameraSource);

        if (supportedFormats == null) {
            supportedFormats = getSupportedFormats(getCameraId(cameraSource));
            supportedFormatsMap.put(cameraSource, supportedFormats);
        }

        return supportedFormats;
    }

    private List<CaptureFormat> getSupportedFormats(int cameraId) {
        final android.hardware.Camera.Parameters parameters;
        android.hardware.Camera camera = null;
        try {
            camera = android.hardware.Camera.open(cameraId);
            parameters = camera.getParameters();
        } catch (RuntimeException e) {
            return new ArrayList<>();
        } finally {
            if (camera != null) {
                camera.release();
            }
        }

        final List<CaptureFormat> formatList = new ArrayList<>();
        try {
            int minFps = 0;
            int maxFps = 0;
            final List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
            if (listFpsRange != null) {
                // getSupportedPreviewFpsRange() returns a sorted list. Take the fps range
                // corresponding to the highest fps.
                final int[] range = listFpsRange.get(listFpsRange.size() - 1);
                minFps = range[android.hardware.Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
                maxFps = range[android.hardware.Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            }
            for (android.hardware.Camera.Size size : parameters.getSupportedPreviewSizes()) {
                formatList.add(new CaptureFormat(size.width,
                        size.height,
                        minFps,
                        maxFps,
                        CapturePixelFormat.NV21));
            }
        } catch (Exception e) {
            logger.e("getSupportedFormats() failed on camera index " + cameraId, e);
        }

        return formatList;
    }
}
