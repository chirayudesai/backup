LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := permissions_com.stevesoltys.backup.xml
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := whitelist_com.stevesoltys.backup.xml
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/sysconfig
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	BIP39:../../../libs/BIP39-2019.01.27.jar \
	commons-io:../../../libs/commons-io-2.6.jar \
	constraintlayout:../../../libs/constraintlayout-1.1.3.aar \
	core-ktx:../../../libs/core-ktx-1.0.2.aar \
	lifecycle-extensions:../../../libs/lifecycle-extensions-2.0.0.aar \
	material:../../../libs/material-1.0.0.aar \
	preference-ktx:../../../libs/preference-ktx-1.0.0.aar

include $(BUILD_MULTI_PREBUILT)