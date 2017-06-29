
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <net/if.h>
#include <linux/if_tun.h>
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
  (JNIEnv * env, jobject jobj, jstring string, jint descriptor) {
  printf("start ioctl...\n");
  const char* dev = (*env)->GetStringUTFChars(env, string, 0);
  
  struct ifreq ifr;
  memset(&ifr, 0, sizeof(ifr));
  
  ifr.ifr_flags = IFF_TUN;
  strncpy(ifr.ifr_name, dev, IFNAMSIZ);
  int err;

  if ( (err = ioctl(descriptor, TUNSETIFF, (void *) &ifr)) == -1 ) {
      printf("Error in ioctl");
      exit(1);
  }
  printf("Established interface\n\n");
  return;
}

// This socket class could be a potential way to write back establishing the connection
JNIEXPORT jint JNICALL Java_TestingJNI_socket
  (JNIEnv * env, jobject jobj, jstring string) {
  printf("start socket...\n");
  
  int sock_fd;
  if ( (sock_fd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
    printf("Error establishing socket");
    exit(1);
  }
  
  struct sockaddr_in remote;
  const char* remote_ip = (*env)->GetStringUTFChars(env, string, 0);
  unsigned short int port = PORT;
  
  memset(&remote, 0, sizeof(remote));
  remote.sin_family = AF_INET;
  remote.sin_addr.s_addr = inet_addr(remote_ip);
  remote.sin_port = htons(port);
  
  printf("connecting to %s...\n", remote_ip);
  if (connect(sock_fd, (struct sockaddr*) &remote, sizeof(remote)) < 0) {
    perror("connect()");
    exit(1);
  }
  
  printf("Established socket on %d\n\n", sock_fd);
  return sock_fd;
}




