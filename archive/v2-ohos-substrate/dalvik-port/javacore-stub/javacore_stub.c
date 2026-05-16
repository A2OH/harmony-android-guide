#include <jni.h>
#include <unistd.h>
#include <string.h>
#include <sys/time.h>
#include <stddef.h>

static void dprintln(JNIEnv* e, jstring s) {
    if(!s){write(1,"null\n",5);return;}
    const char* u=(*e)->GetStringUTFChars(e,s,NULL);
    if(u){write(1,u,strlen(u));write(1,"\n",1);(*e)->ReleaseStringUTFChars(e,s,u);}
}

/* System */
JNIEXPORT jobject JNICALL Java_java_lang_System_initSystemProperties(JNIEnv* e,jclass c,jobject p){
    if(!p) return p;
    jclass pc=(*e)->GetObjectClass(e,p);
    jmethodID set=(*e)->GetMethodID(e,pc,"setProperty","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
    if(!set){(*e)->ExceptionClear(e); return p;}
    #define P(k,v) (*e)->CallObjectMethod(e,p,set,(*e)->NewStringUTF(e,k),(*e)->NewStringUTF(e,v))
    P("java.vm.name","Dalvik"); P("file.separator","/"); P("line.separator","\n");
    P("path.separator",":"); P("file.encoding","UTF-8"); P("user.language","en");
    P("user.dir","/"); P("user.home","/"); P("user.name","root");
    P("os.arch","x86_64"); P("os.name","Linux"); P("os.version","5.10");
    P("java.io.tmpdir","/tmp"); P("java.library.path","/data/a2oh");
    #undef P
    if((*e)->ExceptionCheck(e)) (*e)->ExceptionClear(e);
    return p;
}
JNIEXPORT jlong JNICALL Java_java_lang_System_currentTimeMillis(JNIEnv* e,jclass c){struct timeval tv;gettimeofday(&tv,NULL);return(jlong)tv.tv_sec*1000+tv.tv_usec/1000;}
JNIEXPORT jlong JNICALL Java_java_lang_System_nanoTime(JNIEnv* e,jclass c){struct timeval tv;gettimeofday(&tv,NULL);return(jlong)tv.tv_sec*1000000000LL+(jlong)tv.tv_usec*1000;}
JNIEXPORT jstring JNICALL Java_java_lang_System_mapLibraryName(JNIEnv* e,jclass c,jstring n){return n;}
JNIEXPORT jobjectArray JNICALL Java_java_lang_System_specialProperties(JNIEnv* e,jclass c){return(*e)->NewObjectArray(e,0,(*e)->FindClass(e,"java/lang/String"),NULL);}

/* ICU */
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getDefaultLocale(JNIEnv* e,jclass c){return(*e)->NewStringUTF(e,"en_US");}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getIcuVersion(JNIEnv* e,jclass c){return(*e)->NewStringUTF(e,"58");}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getUnicodeVersion(JNIEnv* e,jclass c){return(*e)->NewStringUTF(e,"9");}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getCldrVersion(JNIEnv* e,jclass c){return(*e)->NewStringUTF(e,"30");}

/* Runtime */
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_freeMemory(JNIEnv* e,jobject s){return 64*1024*1024;}
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_totalMemory(JNIEnv* e,jobject s){return 64*1024*1024;}
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_maxMemory(JNIEnv* e,jobject s){return 256*1024*1024;}

/* DirectPrintStream */
JNIEXPORT void JNICALL Java_java_io_DirectPrintStream_nativeWrite(JNIEnv* e,jclass c,jint fd,jbyteArray arr,jint off,jint len){
    jbyte* data=(*e)->GetByteArrayElements(e,arr,NULL);
    if(data){write(fd,data+off,len);(*e)->ReleaseByteArrayElements(e,arr,data,0);}
}
JNIEXPORT void JNICALL Java_java_io_DirectPrintStream_nativeWriteByte(JNIEnv* e,jclass c,jint fd,jint b){
    char ch=(char)b; write(fd,&ch,1);
}

/* NativePrint helpers */
JNIEXPORT void JNICALL Java_NativePrint_println(JNIEnv* e,jclass c,jstring s){dprintln(e,s);}
JNIEXPORT void JNICALL Java_NativePrint_print(JNIEnv* e,jclass c,jstring s){
    if(!s){write(1,"null",4);return;}const char* u=(*e)->GetStringUTFChars(e,s,NULL);
    if(u){write(1,u,strlen(u));(*e)->ReleaseStringUTFChars(e,s,u);}}
JNIEXPORT void JNICALL Java_PrintTest_nprintln(JNIEnv* e,jclass c,jstring s){dprintln(e,s);}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm,void* reserved){return JNI_VERSION_1_6;}


/* Native println — writes directly to stdout fd 1 */
JNIEXPORT void JNICALL Java_HelloNative_nativePrintln(JNIEnv* e, jclass c, jstring msg) {
    dprintln(e, msg);
}

JNIEXPORT void JNICALL Java_HelloOHOS_nativePrintln(JNIEnv* e, jclass c, jstring msg) {
    dprintln(e, msg);
}
