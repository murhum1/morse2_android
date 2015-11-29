#pragma once

#include "jni.h"
#include "Math.hpp"
#include <vector>

#ifdef _cplusplus
extern "C"
{
#endif

namespace FW {

	struct blinker //class that describes a detected light, whether it's a sender we're following or just a streetlight
	{
		Vec2i pos; //position of light on screen
		int size;
		int ID;
		int brightness;

		blinker(const Vec2i pos, int size, int brightness, int ID) : pos(pos), size(size),
																	 brightness(brightness),
																	 ID(ID) { }

	};

	class ImageProcessor {

	public:
		int lastBlinkerID;

		void processImage(const char *imageData, const Vec2i &size);
		std::vector <blinker> blinkers;

		bool stabilize_image;

		int downscalefactor_slider;
		int pointsearch_min;
		int pointsearch_max;

		float lightsearch_threshold;
		float shadowCutOff;
		std::vector <float> sumImage;

		ImageProcessor() {

			lastBlinkerID = 0;
			stabilize_image = false;
			downscalefactor_slider = 1;
			pointsearch_min = 1;
			pointsearch_max = 4;

			lightsearch_threshold = .06f;
			shadowCutOff = .6f;
		}

		~ImageProcessor(void) { }

	private:
		ImageProcessor(const ImageProcessor &); //forbid copy
		ImageProcessor operator=(const ImageProcessor &); //forbid assignment

	private:
		std::vector <float> prevHistoX;
		std::vector <float> prevHistoY;
		std::vector <float> histoX;
		std::vector <float> histoY;

		Vec2i lastsize;
		Vec2i lastoffset;
		int lastdownscalefactor;
	};

}

#ifdef _cplusplus
}
#endif