
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := F12Test
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_CERTIFICATE := platform

LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.legacy_legacy-support-v13 \
    androidx.appcompat_appcompat \
    androidx.legacy_legacy-support-core-ui \
    androidx.legacy_legacy-support-v4
	
LOCAL_PRIVILEGED_MODULE := true
include $(BUILD_PACKAGE)