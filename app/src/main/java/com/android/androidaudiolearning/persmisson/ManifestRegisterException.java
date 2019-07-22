package com.android.androidaudiolearning.persmisson;

/**
 * Manifest中未注册会抛出异常
 */
final class ManifestRegisterException extends RuntimeException {
    ManifestRegisterException(String permission) {
        super(permission == null ?
                "No permissions are registered in the manifest file" :
                (permission + ": Permissions are not registered in the manifest file"));
    }
}
