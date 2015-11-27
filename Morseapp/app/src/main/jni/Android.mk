LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := jniprocess
LOCAL_SRC_FILES := jniprocess.cpp Math.cpp Math.hpp ImageProcessor.cpp ImageProcessor.hpp Defs.hpp
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)