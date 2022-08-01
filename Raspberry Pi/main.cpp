#include <stdio.h>
#include <stdlib.h>
#include "es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper.h"

#include "libproject1.h"

static bool initialized = false;

/*
 * Class:     es_upm_dte_iot_devices_RPiDevicesWrapper
 * Method:    initResources
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_initResources
(JNIEnv *env, jobject caller, jint pinLEDR, jint pinLEDG, jint pinLEDB, jint pinBUTTON)
{

    initResources(pinLEDR, pinLEDG, pinLEDB, pinBUTTON);
    initialized = true;

}

/*
 * Class:     es_upm_dte_iot_devices_RPiDevicesWrapper
 * Method:    readSi7021
 * Signature: ()Les/upm/dte/iot/devices/TempHumObservation;
 */
JNIEXPORT jobject JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_readSi7021
  (JNIEnv *env, jobject caller){
      float temp, hum;
    jclass exceptionClass;
    jclass obsClass;
    jobject res;
    jmethodID constructor;

    exceptionClass = env->FindClass( "java/lang/Exception" );

    if ( exceptionClass == 0 )
    {
        perror("readSi7021 (native): no access to exceptionClass.");
        return env->NewGlobalRef(NULL);
    }

    if ( !initialized ){
        perror("readSi7021 (native): not initialized.");
        return env->NewGlobalRef(NULL);
    }

    obsClass = env->FindClass("es/upm/dte/iot/devices/TempHumObservation");
    if ( obsClass == 0 )
    {
        perror("readSi7021 (native): no access to obsClass.");
        return env->NewGlobalRef(NULL);
    }

    constructor = env->GetMethodID(obsClass, "<init>", "(FF)V");
    if ( constructor == 0 )
    {
        perror("readSi7021 (native): no constructor.");
        return env->NewGlobalRef(NULL);
    }

    readSi7021(temp, hum);


#ifdef _DEBUG_
    printf("readSi7021 (native) : Temp: %f. Humedad: %f\n", temp, hum);
#endif

    res = env->NewObject(obsClass,constructor, temp, hum);
    if ( res == 0 )
    {
        perror("readSi7021 (native): no object instantiated.");
        return env->NewGlobalRef(NULL);
    }

    return env->NewGlobalRef(res);
}

/*
 * Class:     es_upm_dte_iot_devices_RPiDevicesWrapper
 * Method:    readFakeTempRH
 * Signature: ()Les/upm/dte/iot/devices/TempHumObservation;
 */
JNIEXPORT jobject JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_readFakeTempRH
(JNIEnv *env, jobject caller)
{
    float temp, hum;
    jclass exceptionClass;
    jclass obsClass;
    jobject res;
    jmethodID constructor;

    exceptionClass = env->FindClass( "java/lang/Exception" );

    if ( exceptionClass == 0 )
    {
        perror("readFakeTempRH (native): no access to exceptionClass.");
        return env->NewGlobalRef(NULL);
    }

    if ( !initialized ){
        perror("readFakeTempRH (native): not initialized.");
        return env->NewGlobalRef(NULL);
    }

    obsClass = env->FindClass("es/upm/dte/iot/devices/TempHumObservation");
    if ( obsClass == 0 )
    {
        perror("readFakeTempRH (native): no access to obsClass.");
        return env->NewGlobalRef(NULL);
    }

    constructor = env->GetMethodID(obsClass, "<init>", "(FF)V");
    if ( constructor == 0 )
    {
        perror("readFakeTempRH (native): no constructor.");
        return env->NewGlobalRef(NULL);
    }

    //readFakeTempRH(temp, hum);

#ifdef _DEBUG_
    printf("readFakeTempRH (native) : Temp: %f. Humedad: %f\n", temp, hum);
#endif

    res = env->NewObject(obsClass,constructor, temp, hum);
    if ( res == 0 )
    {
        perror("readFakeTempRH (native): no object instantiated.");
        return env->NewGlobalRef(NULL);
    }

    return env->NewGlobalRef(res);
}

/*
 * Class:     es_upm_dte_iot_devices_RPiDevicesWrapper
 * Method:    setLCDBackLight
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_setLCDBackLight
(JNIEnv *env, jobject caller, jshort LEDR, jshort LEDG, jshort LEDB)
{
    setLCDBackLight( (unsigned char)LEDR, (unsigned char)LEDG, (unsigned char)LEDB);
}

/*
 * Class:     es_upm_dte_iot_devices_RPiDevicesWrapper
 * Method:    setLCDText
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_setLCDText
(JNIEnv *env, jobject caller, jstring text)
{

#define MAX_COL 16

    const char *message = env->GetStringUTFChars(text,NULL);
    setLCDText((char *) message);
    env->ReleaseStringUTFChars(text,message);

}

/*
 * Class:     es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper
 * Method:    getPrecipitacion
 * Signature: ()Les/upm/dte/iot/devices/LluviaObservation;
 */
JNIEXPORT jobject JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_getPrecipitacion
  (JNIEnv *env, jobject)
{
        int lluvia;
    jclass exceptionClass;
    jclass obsClass;
    jobject res;
    jmethodID constructor;

    exceptionClass = env->FindClass ( "java/lang/Exception" );


    if ( exceptionClass == 0 )
    {
        perror("getPrecipitacion (native): no access to exceptionClass.");
        return env->NewGlobalRef(NULL);
    }

    if ( !initialized ){
        perror("getPrecipitacion (native): not initialized.");
        return env->NewGlobalRef(NULL);
    }

    obsClass = env->FindClass("es/upm/dte/iot/devices/LluviaObservation");
    if ( obsClass == 0 )
    {
        perror("getPrecipitacion (native): no access to obsClass.");
        return env->NewGlobalRef(NULL);
    }

    constructor = env->GetMethodID(obsClass, "<init>", "(I)V");
    if ( constructor == 0 )
    {
        perror("getPrecipitacion (native): no constructor.");
        return env->NewGlobalRef(NULL);
    }

    getPrecipitacion(lluvia);

#ifdef _DEBUG_
    printf("getPrecipitacion (native) : lluvia: %d\n", lluvia);
#endif

    res = env->NewObject(obsClass,constructor, lluvia);
    if ( res == 0 )
    {
        perror("getPrecipitacion (native): no object instantiated.");
        return env->NewGlobalRef(NULL);
    }

    return env->NewGlobalRef(res);

  } // End Method getPrecipitacion

/*
 * Class:     es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper
 * Method:    readColor
 * Signature: ()Les/upm/dte/iot/devices/ColorObservation;
 */
JNIEXPORT jobject JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_readColor
  (JNIEnv *env, jobject caller){
  int clear, red, green, blue;
    jclass exceptionClass;
    jclass obsClass;
    jobject res;
    jmethodID constructor;

    exceptionClass = env->FindClass( "java/lang/Exception" );

    if ( exceptionClass == 0 )
    {
        perror("readColor (native): no access to exceptionClass.");
        return env->NewGlobalRef(NULL);
    }

    if ( !initialized ){
        perror("readColor (native): not initialized.");
        return env->NewGlobalRef(NULL);
    }

    obsClass = env->FindClass("es/upm/dte/iot/devices/ColorObservation");
    if ( obsClass == 0 )
    {
        perror("readColor (native): no access to obsClass.");
        return env->NewGlobalRef(NULL);
    }

    constructor = env->GetMethodID(obsClass, "<init>", "(IIII)V");
    if ( constructor == 0 )
    {
        perror("readColor (native): no constructor.");
        return env->NewGlobalRef(NULL);
    }

    readColor(clear, red, green, blue);

#ifdef _DEBUG_
    printf("readColor (native) : clear: %d. \n", clear);
#endif

    res = env->NewObject(obsClass,constructor, clear, red, green, blue);
    if ( res == 0 )
    {
        perror("readColor (native): no object instantiated.");
        return env->NewGlobalRef(NULL);
    }

    return env->NewGlobalRef(res);
  }
/*
 * Class:     es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper
 * Method:    setRGB
 * Signature: (SSS)V
 */
JNIEXPORT void JNICALL Java_es_upm_dte_iot_devices_raspberry_RPiDevicesWrapper_setRGB
  (JNIEnv *env, jobject caller, jshort red, jshort green, jshort blue){
  setRGB( (unsigned char)red, (unsigned char)green, (unsigned char)blue);
  }
