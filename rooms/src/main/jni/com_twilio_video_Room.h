#include <jni.h>

#ifndef _Included_com_twilio_conversations_Room
#define _Included_com_twilio_conversations_Room

#ifdef __cplusplus
extern "C" {
#endif

struct RoomDataContext {

};

JNIEXPORT jstring JNICALL Java_com_twilio_video_Room_nativeGetName
        (JNIEnv *, jobject, jlong);

JNIEXPORT jstring JNICALL Java_com_twilio_video_Room_nativeGetSid
        (JNIEnv *, jobject, jlong);

JNIEXPORT void JNICALL Java_com_twilio_video_Room_nativeDisconnect
        (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
