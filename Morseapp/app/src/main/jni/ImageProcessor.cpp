#include "ImageProcessor.hpp"

using namespace FW;
using namespace std;


bool inBounds(Vec2i pos, Vec2i size)
{
	return !(pos.x < 0 || pos.x >= size.x || pos.y < 0 || pos.y >= size.y);
}


void ImageProcessor::processImage(const char* imageData, const Vec2i& size_in)
{
	blinkers.clear();
	int downscalefactor = downscalefactor_slider;
	if (size_in != lastsize || lastdownscalefactor != downscalefactor)
	{
		lastsize = size_in;
		lastdownscalefactor = downscalefactor;
		sumImage.resize((lastsize.x / downscalefactor + 1) * (lastsize.y / downscalefactor + 1));
	}

	Vec2i size = lastsize / downscalefactor;

	//initialize histogram vectors
	memset(&sumImage[0], 0, sumImage.size() * sizeof(float));

	Vec2f maxvalue = .0f; //maximum value of each histogram

	//compute histogram from image rows, loop over all pixels and sum it to the proper vector
	float invsizex = 1.0f / lastsize.x;
	float invsizey = 1.0f / lastsize.y;
	float shadowWeight = 1.0f / (1.0f - shadowCutOff);
	for (int j = 0; j < lastsize.y; j++)
	{
		for (int i = 0; i < lastsize.x; i++)
		{
			float value = imageData[j * lastsize.x + i] / 255.0f;
			value = max(.0f, value - shadowCutOff) * shadowWeight;
			int x = i / downscalefactor;
			int y = j / downscalefactor;

			value = max(.0f, value - shadowCutOff) * shadowWeight;

			float cur_sum = value;
			if (inBounds(Vec2i(x - 1, y), size))
				cur_sum += sumImage[y * size.x + (x - 1)];
			if (inBounds(Vec2i(x, y - 1), size))
				cur_sum += sumImage[(y - 1) * size.x + x];
			if (inBounds(Vec2i(x - 1, y - 1), size))
				cur_sum -= sumImage[(y - 1) * size.x + (x - 1)];

			sumImage[y * size.x + x] = cur_sum;
		}
	}

	int sum_count = pointsearch_max;
	int sum_scale = pointsearch_min;
	std::vector<float> sumSquares(sum_count);
	std::vector<int> sumSizes(sum_count);
	int k = 0;
	for (int i = 0; i < sumSizes.size(); ++i) {
		k++;
		sumSizes[i] = sum_scale * k*k;
	}

	float merge_threshold = 1e5f;

	//loop over all pixels to find lights
	for (int i = 0; i < size.x; ++i) {
		for (int j = 0; j < size.y; ++j) {
			int x = i;
			int y = j;
			Vec2f pos = Vec2f(x, y) / Vec2f(size) * 2.0f - 1.0f;

			//first, compute all square sums centered on current pixel
			for (int k = 0; k < sum_count; ++k) {
				int x_right = i + sumSizes[k], x_left = i - sumSizes[k], y_top =
						j + sumSizes[k], y_bot = j - sumSizes[k];
				float cur_sum = .0f;
				if (!inBounds(Vec2i(x_right, y_bot), size) ||
					!inBounds(Vec2i(x_left, y_top), size)) {
					sumSquares[k] = .0f;
					continue;
				}
				cur_sum += sumImage[y_top * size.x + x_right];
				cur_sum -= sumImage[y_bot * size.x + x_right];
				cur_sum -= sumImage[y_top * size.x + x_left];
				cur_sum += sumImage[y_bot * size.x + x_left];
				sumSquares[k] = cur_sum;
			}

			//iterate over pairs of squares, getting the average brightness ratio of inner vs outer, and selecting the size with highest average brightness difference
			float max_diff = lightsearch_threshold;
			float max_inner = 0;
			int max_idx = 0;
			bool found = false;
			for (int k = 0; k < sum_count - 1; ++k) {
				float inner = sumSquares[k];
				float outer = sumSquares[k + 1];
				if (outer == .0f)
					continue;
				outer -= inner;

				int inner_area = (sumSizes[k] * 2 + 1) * (sumSizes[k] * 2 + 1);
				int outer_area = (sumSizes[k + 1] * 2 + 1) * (sumSizes[k + 1] * 2 + 1) - inner_area;
				float inner_average = inner / inner_area;
				float outer_average = outer / outer_area;
				float cur_diff = inner_average - outer_average;
				if (cur_diff > max_diff) {
					found = true;
					max_diff = cur_diff;
					max_idx = k;
					max_inner = inner_average;
				}
			}

			//if brightness below threshold, ignore
			if (!found)
				continue;
			blinkers.push_back(blinker(Vec2i(i,j)*downscalefactor, sumSizes[max_idx]*downscalefactor, int(max_diff * 255), lastBlinkerID++));
		}
	}
}