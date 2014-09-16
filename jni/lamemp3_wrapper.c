/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_mp3encodedemo_JNIMp3Encode */

#ifndef _Included_com_example_soundtouchdemo_JNIMp3Encode
#define _Included_com_example_soundtouchdemo_JNIMp3Encode
#ifdef __cplusplus
extern "C" {
#endif

#include "libmp3lame/lame.h"
#define BUFFER_SIZE 4096

lame_t lame;

/*
 * Class:     com_example_mp3encodedemo_JNIMp3Encode
 * Method:    init
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNIMp3Encode_init
  (JNIEnv *env, jobject obj, jint channel, jint sampleRate, jint brate)
{
	lame = lame_init();
	lame_set_num_channels(lame, channel);
	lame_set_in_samplerate(lame, sampleRate);
	lame_set_brate(lame, brate);
	lame_set_mode(lame, 1);
	lame_set_quality(lame, 2);
	lame_init_params(lame);
}

/*
 * Class:     com_example_mp3encodedemo_JNIMp3Encode
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNIMp3Encode_destroy
  (JNIEnv *env, jobject obj)
{
	lame_close(lame);
}

/*
 * Class:     com_example_mp3encodedemo_JNIMp3Encode
 * Method:    encode
 * Signature: ([SI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_example_soundtouchdemo_JNIMp3Encode_encode
  (JNIEnv *env, jobject obj, jshortArray buffer, jint len)
{
	int nb_write = 0;
	char output[BUFFER_SIZE];

	// 转换为本地数组
	jshort *input = (*env)->GetShortArrayElements(env, buffer, NULL);

	// 压缩mp3
	nb_write = lame_encode_buffer(lame, input, input, len, output, BUFFER_SIZE);

	// 局部引用，创建一个byte数组
	jbyteArray result = (*env)->NewByteArray(env, nb_write);

	// 给byte数组设置值
	(*env)->SetByteArrayRegion(env, result, 0, nb_write, (jbyte *)output);

	// 释放本地数组(避免内存泄露)
	(*env)->ReleaseShortArrayElements(env, buffer, input, 0);

	return result;
}

#ifdef __cplusplus
}
#endif
#endif
