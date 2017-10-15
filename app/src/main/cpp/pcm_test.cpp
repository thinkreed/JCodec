//
// Created by thinkreed on 2017/10/12.
//

#include "pcm_test.h"
#include <iostream>
#include <malloc.h>

int pcms16le_split(char *url) {
    FILE *pcmSourceFile = fopen(url, "rb+");
    FILE *output_left = fopen("output_left.pcm", "wb+");
    FILE *output_right = fopen("output_right.pcm", "wb+");

    unsigned char *sample = (unsigned char *) malloc(4);

    while (!feof(pcmSourceFile)) {
        fread(sample, 1, 4, pcmSourceFile);
        fwrite(sample, 1, 2, output_left);
        fwrite(sample, 1, 2, output_right);
    }

    free(sample);
    fclose(pcmSourceFile);
    fclose(output_left);
    fclose(output_right);
    return 0;
}