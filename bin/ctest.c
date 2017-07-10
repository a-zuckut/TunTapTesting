
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <net/if.h>
#include <linux/if_tun.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <arpa/inet.h> 
#include <sys/select.h>
#include <sys/time.h>
#include <errno.h>
#include <stdarg.h>
#include "TestingJNI.h"

#define PORT 55555

JNIEXPORT void JNICALL Java_TestingJNI_ioctl
  (JNIEnv * env, jobject obj, jstring string, jint descriptor) {
  printf("start ioctl...\n");
  const char* dev = (*env)->GetStringUTFChars(env, string, 0);
  char cloneDev[16] = "/dev/net/tun";
  
  struct ifreq ifr;
  memset(&ifr, 0, sizeof(ifr));
  
  ifr.ifr_flags = IFF_TUN;
  strncpy(ifr.ifr_name, dev, IFNAMSIZ);
  int err;

  // Turn on Tun Interface
  if ( (err = ioctl(descriptor, TUNSETIFF, (void *) &ifr)) == -1 ) {
      printf("Error ioctl()");
      exit(1);
  }
  printf("Established interface\n\n");
  return;
}





