/*
*  Copyright (c) 2009-2011, NVIDIA Corporation
*  All rights reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions are met:
*      * Redistributions of source code must retain the above copyright
*        notice, this list of conditions and the following disclaimer.
*      * Redistributions in binary form must reproduce the above copyright
*        notice, this list of conditions and the following disclaimer in the
*        documentation and/or other materials provided with the distribution.
*      * Neither the name of NVIDIA Corporation nor the
*        names of its contributors may be used to endorse or promote products
*        derived from this software without specific prior written permission.
*
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
*  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
*  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
*  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
*  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
*  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
*  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
*  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#pragma once
//#include "base/DLLImports.hpp"
#include "Defs.hpp"

#include <math.h>

namespace FW {

	typedef enum {
		CUDA_SUCCESS = 0
	} CUresult;
	typedef struct {
		FW::S32 x, y;
	} int2;
	typedef struct {
		FW::S32 x, y, z;
	} int3;
	typedef struct {
		FW::S32 x, y, z, w;
	} int4;
	typedef struct {
		FW::F32 x, y;
	} float2;
	typedef struct {
		FW::F32 x, y, z;
	} float3;
	typedef struct {
		FW::F32 x, y, z, w;
	} float4;
	typedef struct {
		FW::F64 x, y;
	} double2;
	typedef struct {
		FW::F64 x, y, z;
	} double3;
	typedef struct {
		FW::F64 x, y, z, w;
	} double4;
	//------------------------------------------------------------------------

	__inline F32 sqrt(F32 a) { return ::sqrtf(a); }

	__inline F64 sqrt(F64 a) { return ::sqrt(a); }

	__inline S32 abs(S32 a) { return (a >= 0) ? a : -a; }

	__inline S64 abs(S64 a) { return (a >= 0) ? a : -a; }

	__inline F32 abs(F32 a) { return ::fabsf(a); }

	__inline F64 pow(F64 a, F64 b) { return ::pow(a, b); }

	__inline F64 exp(F64 a) { return ::exp(a); }

	__inline F64 log(F64 a) { return ::log(a); }

	__inline F64 sin(F64 a) { return ::sin(a); }

	__inline F64 cos(F64 a) { return ::cos(a); }

	__inline F64 tan(F64 a) { return ::tan(a); }

	__inline F32 asin(F32 a) { return ::asinf(a); }

	__inline F64 asin(F64 a) { return ::asin(a); }

	__inline F32 acos(F32 a) { return ::acosf(a); }

	__inline F64 acos(F64 a) { return ::acos(a); }

	__inline F32 atan(F32 a) { return ::atanf(a); }

	__inline F64 atan(F64 a) { return ::atan(a); }

	__inline F64 atan2(F64 y, F64 x) { return ::atan2(y, x); }

	__inline F32 atan2(F32 y, F32 x) { return ::atan2f(y, x); }

	__inline F32 floor(F32 a) { return ::floorf(a); }

	__inline F64 floor(F64 a) { return ::floor(a); }

	__inline F32 ceil(F32 a) { return ::ceilf(a); }

	__inline F64 ceil(F64 a) { return ::ceil(a); }

	__inline U64 doubleToBits(F64 a) { return *(U64 *) &a; }

	__inline F64 bitsToDouble(U64 a) { return *(F64 *) &a; }

#if FW_CUDA
	__inline F32    pow(F32 a, F32 b)  { return ::__powf(a, b); }
	__inline F32    exp(F32 a)         { return ::__expf(a); }
	__inline F32    exp2(F32 a)         { return ::exp2f(a); }
	__inline F64    exp2(F64 a)         { return ::exp2(a); }
	__inline F32    log(F32 a)         { return ::__logf(a); }
	__inline F32    log2(F32 a)         { return ::__log2f(a); }
	__inline F64    log2(F64 a)         { return ::log2(a); }
	__inline F32    sin(F32 a)         { return ::__sinf(a); }
	__inline F32    cos(F32 a)         { return ::__cosf(a); }
	__inline F32    tan(F32 a)         { return ::__tanf(a); }
	__inline U32    floatToBits(F32 a)         { return ::__float_as_int(a); }
	__inline F32    bitsToFloat(U32 a)         { return ::__int_as_float(a); }
	__inline F32    exp2(int a)         { return ::exp2f((F32)a); }
	__inline F32    fastMin(F32 a, F32 b)  { return ::fminf(a, b); }
	__inline F32    fastMax(F32 a, F32 b)  { return ::fmaxf(a, b); }
	__inline F64    fastMin(F64 a, F64 b)  { return ::fmin(a, b); }
	__inline F64    fastMax(F64 a, F64 b)  { return ::fmax(a, b); }
#else

	inline F32 pow(F32 a, F32 b) { return ::powf(a, b); }

	inline F32 exp(F32 a) { return ::expf(a); }

	inline F32 exp2(F32 a) { return ::powf(2.0f, a); }

	inline F64 exp2(F64 a) { return ::pow(2.0, a); }

	inline F32 log(F32 a) { return ::logf(a); }

	inline F32 log2(F32 a) { return ::logf(a) / ::logf(2.0f); }

	inline F64 log2(F64 a) { return ::log(a) / ::log(2.0); }

	inline F32 sin(F32 a) { return ::sinf(a); }

	inline F32 cos(F32 a) { return ::cosf(a); }

	inline F32 tan(F32 a) { return ::tanf(a); }

	inline U32 floatToBits(F32 a) { return *(U32 *) &a; }

	inline F32 bitsToFloat(U32 a) { return *(F32 *) &a; }

	inline F32 exp2(int a) { return bitsToFloat(clamp(a + 127, 1, 254) << 23); }

#endif

	__inline bool isFinite(F32 a) { return ((~floatToBits(a) & 0x7F800000) != 0); }

	__inline F32 scale(F32 a, int b) { return a * exp2(b); }

	template<class T>
	__inline T sqr(const T &a) { return a * a; }

	template<class T>
	__inline T rcp(const T &a) { return (a) ? (T) 1 / a : (T) 0; }

	template<class A, class B>
	__inline A lerp(const A &a, const A &b, const B &t) { return (A) (a * ((B) 1 - t) + b * t); }

	//------------------------------------------------------------------------

	template<class T, int L>
	class Vector;

	template<class T, int L, class S>
	class VectorBase {
	public:
		__inline VectorBase(void) { }

		__inline const T *getPtr(void) const { return ((S *) this)->getPtr(); }

		__inline T *getPtr(void) { return ((S *) this)->getPtr(); }

		__inline const T &get(int idx) const {
			FW_ASSERT(idx >= 0 && idx < L);
			return getPtr()[idx];
		}

		__inline T &get(int idx) {
			FW_ASSERT(idx >= 0 && idx < L);
			return getPtr()[idx];
		}

		__inline T set(int idx, const T &a) {
			T &slot = get(idx);
			T old = slot;
			slot = a;
			return old;
		}

		__inline void set(const T &a) {
			T *tp = getPtr();
			for (int i = 0; i < L; i++) tp[i] = a;
		}

		__inline void set(const T *ptr) {
			FW_ASSERT(ptr);
			T *tp = getPtr();
			for (int i = 0; i < L; i++) tp[i] = ptr[i];
		}

		__inline void setZero(void) { set((T) 0); }

#if !FW_CUDA

#endif

		__inline bool isZero(void) const {
			const T *tp = getPtr();
			for (int i = 0; i < L; i++) if (tp[i] != (T) 0) return false;
			return true;
		}

		__inline T lenSqr(void) const {
			const T *tp = getPtr();
			T r = (T) 0;
			for (int i = 0; i < L; i++) r += sqr(tp[i]);
			return r;
		}

		__inline T length(void) const { return sqrt(lenSqr()); }

		__inline S normalized(T len = (T) 1) const { return operator*(len * rcp(length())); }

		__inline void normalize(T len = (T) 1) { set(normalized(len)); }

		__inline T min(void) const {
			const T *tp = getPtr();
			T r = tp[0];
			for (int i = 1; i < L; i++) r = FW::min(r, tp[i]);
			return r;
		}

		__inline T max(void) const {
			const T *tp = getPtr();
			T r = tp[0];
			for (int i = 1; i < L; i++) r = FW::max(r, tp[i]);
			return r;
		}

		__inline T sum(void) const {
			const T *tp = getPtr();
			T r = tp[0];
			for (int i = 1; i < L; i++) r += tp[i];
			return r;
		}

		__inline S abs(void) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = FW::abs(tp[i]);
			return r;
		}


		__inline const T &operator[](int idx) const { return get(idx); }

		__inline T &operator[](int idx) { return get(idx); }

		__inline S &operator=(const T &a) {
			set(a);
			return *(S *) this;
		}

		__inline S &operator+=(const T &a) {
			set(operator+(a));
			return *(S *) this;
		}

		__inline S &operator-=(const T &a) {
			set(operator-(a));
			return *(S *) this;
		}

		__inline S &operator*=(const T &a) {
			set(operator*(a));
			return *(S *) this;
		}

		__inline S &operator/=(const T &a) {
			set(operator/(a));
			return *(S *) this;
		}

		__inline S &operator%=(const T &a) {
			set(operator%(a));
			return *(S *) this;
		}

		__inline S &operator&=(const T &a) {
			set(operator&(a));
			return *(S *) this;
		}

		__inline S &operator|=(const T &a) {
			set(operator|(a));
			return *(S *) this;
		}

		__inline S &operator^=(const T &a) {
			set(operator^(a));
			return *(S *) this;
		}

		__inline S &operator<<=(const T &a) {
			set(operator<<(a));
			return *(S *) this;
		}

		__inline S &operator>>=(const T &a) {
			set(operator>>(a));
			return *(S *) this;
		}

		__inline S               operator+(void) const { return *this; }

		__inline S               operator-(void) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = -tp[i];
			return r;
		}

		__inline S               operator~(void) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = ~tp[i];
			return r;
		}

		__inline S               operator+(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] + a;
			return r;
		}

		__inline S               operator-(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] - a;
			return r;
		}

		__inline S               operator*(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] * a;
			return r;
		}

		__inline S               operator/(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] / a;
			return r;
		}

		__inline S               operator%(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] % a;
			return r;
		}

		__inline S               operator&(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] & a;
			return r;
		}

		__inline S               operator|(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] | a;
			return r;
		}

		__inline S               operator^(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] ^ a;
			return r;
		}

		__inline S               operator<<(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] << a;
			return r;
		}

		__inline S               operator>>(const T &a) const {
			const T *tp = getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] >> a;
			return r;
		}

		template<class V>
		__inline void set(const VectorBase<T, L, V> &v) { set(v.getPtr()); }

		template<class V>
		__inline T dot(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			T r = (T) 0;
			for (int i = 0; i < L; i++) r += tp[i] * vp[i];
			return r;
		}

		template<class V>
		__inline S min(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = FW::min(tp[i], vp[i]);
			return r;
		}

		template<class V>
		__inline S max(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = FW::max(tp[i], vp[i]);
			return r;
		}

		template<class V, class W>
		__inline S clamp(const VectorBase<T, L, V> &lo, const VectorBase<T, L, W> &hi) const {
			const T *tp = getPtr();
			const T *lop = lo.getPtr();
			const T *hip = hi.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = FW::clamp(tp[i], lop[i], hip[i]);
			return r;
		}

		template<class V>
		__inline S &operator=(const VectorBase<T, L, V> &v) {
			set(v);
			return *(S *) this;
		}

		template<class V>
		__inline S &operator+=(const VectorBase<T, L, V> &v) {
			set(operator+(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator-=(const VectorBase<T, L, V> &v) {
			set(operator-(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator*=(const VectorBase<T, L, V> &v) {
			set(operator*(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator/=(const VectorBase<T, L, V> &v) {
			set(operator/(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator%=(const VectorBase<T, L, V> &v) {
			set(operator%(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator&=(const VectorBase<T, L, V> &v) {
			set(operator&(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator|=(const VectorBase<T, L, V> &v) {
			set(operator|(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator^=(const VectorBase<T, L, V> &v) {
			set(operator^(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator<<=(const VectorBase<T, L, V> &v) {
			set(operator<<(v));
			return *(S *) this;
		}

		template<class V>
		__inline S &operator>>=(const VectorBase<T, L, V> &v) {
			set(operator>>(v));
			return *(S *) this;
		}

		template<class V>
		__inline S       operator+(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] + vp[i];
			return r;
		}

		template<class V>
		__inline S       operator-(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] - vp[i];
			return r;
		}

		template<class V>
		__inline S       operator*(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] * vp[i];
			return r;
		}

		template<class V>
		__inline S       operator/(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] / vp[i];
			return r;
		}

		template<class V>
		__inline S       operator%(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] % vp[i];
			return r;
		}

		template<class V>
		__inline S       operator&(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] & vp[i];
			return r;
		}

		template<class V>
		__inline S       operator|(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] | vp[i];
			return r;
		}

		template<class V>
		__inline S       operator^(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] ^ vp[i];
			return r;
		}

		template<class V>
		__inline S       operator<<(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] << vp[i];
			return r;
		}

		template<class V>
		__inline S       operator>>(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			S r;
			T *rp = r.getPtr();
			for (int i = 0; i < L; i++) rp[i] = tp[i] >> vp[i];
			return r;
		}

		template<class V>
		__inline bool    operator==(const VectorBase<T, L, V> &v) const {
			const T *tp = getPtr();
			const T *vp = v.getPtr();
			for (int i = 0; i < L; i++) if (tp[i] != vp[i]) return false;
			return true;
		}

		template<class V>
		__inline bool    operator!=(const VectorBase<T, L, V> &v) const { return (!operator==(v)); }
	};

	//------------------------------------------------------------------------

	class Vec2i : public VectorBase<S32, 2, Vec2i>, public int2 {
	public:
		__inline Vec2i(void) { setZero(); }

		__inline Vec2i(S32 a) { set(a); }

		__inline Vec2i(S32 xx, S32 yy) {
			x = xx;
			y = yy;
		}

		__inline Vec2i(const int2 &v) {
			x = v.x;
			y = v.y;
		}

		__inline const S32 *getPtr(void) const { return &x; }

		__inline S32 *getPtr(void) { return &x; }

		static __inline Vec2i fromPtr(const S32 *ptr) { return Vec2i(ptr[0], ptr[1]); }

		__inline Vec2i perpendicular(void) const { return Vec2i(-y, x); }

		template<class V>
		__inline Vec2i(const VectorBase<S32, 2, V> &v) { set(v); }

		template<class V>
		__inline Vec2i &operator=(const VectorBase<S32, 2, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec3i : public VectorBase<S32, 3, Vec3i>, public int3 {
	public:
		__inline Vec3i(void) { setZero(); }

		__inline Vec3i(S32 a) { set(a); }

		__inline Vec3i(S32 xx, S32 yy, S32 zz) {
			x = xx;
			y = yy;
			z = zz;
		}

		__inline Vec3i(const Vec2i &xy, S32 zz) {
			x = xy.x;
			y = xy.y;
			z = zz;
		}

		__inline Vec3i(const int3 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		__inline const S32 *getPtr(void) const { return &x; }

		__inline S32 *getPtr(void) { return &x; }

		static __inline Vec3i fromPtr(const S32 *ptr) { return Vec3i(ptr[0], ptr[1], ptr[2]); }

		__inline Vec2i getXY(void) const { return Vec2i(x, y); }

		template<class V>
		__inline Vec3i(const VectorBase<S32, 3, V> &v) { set(v); }

		template<class V>
		__inline Vec3i &operator=(const VectorBase<S32, 3, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec4i : public VectorBase<S32, 4, Vec4i>, public int4 {
	public:
		__inline Vec4i(void) { setZero(); }

		__inline Vec4i(S32 a) { set(a); }

		__inline Vec4i(S32 xx, S32 yy, S32 zz, S32 ww) {
			x = xx;
			y = yy;
			z = zz;
			w = ww;
		}

		__inline Vec4i(const Vec2i &xy, S32 zz, S32 ww) {
			x = xy.x;
			y = xy.y;
			z = zz;
			w = ww;
		}

		__inline Vec4i(const Vec3i &xyz, S32 ww) {
			x = xyz.x;
			y = xyz.y;
			z = xyz.z;
			w = ww;
		}

		__inline Vec4i(const Vec2i &xy, const Vec2i &zw) {
			x = xy.x;
			y = xy.y;
			z = zw.x;
			w = zw.y;
		}

		__inline Vec4i(const int4 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
			w = v.w;
		}

		__inline const S32 *getPtr(void) const { return &x; }

		__inline S32 *getPtr(void) { return &x; }

		static __inline Vec4i fromPtr(const S32 *ptr) {
			return Vec4i(ptr[0], ptr[1], ptr[2], ptr[3]);
		}

		__inline Vec2i getXY(void) const { return Vec2i(x, y); }

		__inline Vec3i getXYZ(void) const { return Vec3i(x, y, z); }

		__inline Vec3i getXYW(void) const { return Vec3i(x, y, w); }

		template<class V>
		__inline Vec4i(const VectorBase<S32, 4, V> &v) { set(v); }

		template<class V>
		__inline Vec4i &operator=(const VectorBase<S32, 4, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec2f : public VectorBase<F32, 2, Vec2f>, public float2 {
	public:
		__inline Vec2f(void) { setZero(); }

		__inline Vec2f(F32 a) { set(a); }

		__inline Vec2f(F32 xx, F32 yy) {
			x = xx;
			y = yy;
		}

		__inline Vec2f(const Vec2i &v) {
			x = (F32) v.x;
			y = (F32) v.y;
		}

		__inline Vec2f(const float2 &v) {
			x = v.x;
			y = v.y;
		}

		__inline const F32 *getPtr(void) const { return &x; }

		__inline F32 *getPtr(void) { return &x; }

		static __inline Vec2f fromPtr(const F32 *ptr) { return Vec2f(ptr[0], ptr[1]); }

		__inline    operator Vec2i(void) const { return Vec2i((S32) x, (S32) y); }

		__inline Vec2f perpendicular(void) const { return Vec2f(-y, x); }

		__inline F32 cross(const Vec2f &v) const { return x * v.y - y * v.x; }

		template<class V>
		__inline Vec2f(const VectorBase<F32, 2, V> &v) { set(v); }

		template<class V>
		__inline Vec2f &operator=(const VectorBase<F32, 2, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec3f : public VectorBase<F32, 3, Vec3f>, public float3 {
	public:
		__inline Vec3f(void) { setZero(); }

		__inline Vec3f(F32 a) { set(a); }

		__inline Vec3f(F32 xx, F32 yy, F32 zz) {
			x = xx;
			y = yy;
			z = zz;
		}

		__inline Vec3f(const Vec2f &xy, F32 zz) {
			x = xy.x;
			y = xy.y;
			z = zz;
		}

		__inline Vec3f(const Vec3i &v) {
			x = (F32) v.x;
			y = (F32) v.y;
			z = (F32) v.z;
		}

		__inline Vec3f(const float3 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		__inline const F32 *getPtr(void) const { return &x; }

		__inline F32 *getPtr(void) { return &x; }

		static __inline Vec3f fromPtr(const F32 *ptr) { return Vec3f(ptr[0], ptr[1], ptr[2]); }

		__inline    operator Vec3i(void) const { return Vec3i((S32) x, (S32) y, (S32) z); }

		__inline Vec2f getXY(void) const { return Vec2f(x, y); }

		__inline Vec3f cross(const Vec3f &v) const {
			return Vec3f(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		}

		template<class V>
		__inline Vec3f(const VectorBase<F32, 3, V> &v) { set(v); }

		template<class V>
		__inline Vec3f &operator=(const VectorBase<F32, 3, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec4f : public VectorBase<F32, 4, Vec4f>, public float4 {
	public:
		__inline Vec4f(void) { setZero(); }

		__inline Vec4f(F32 a) { set(a); }

		__inline Vec4f(F32 xx, F32 yy, F32 zz, F32 ww) {
			x = xx;
			y = yy;
			z = zz;
			w = ww;
		}

		__inline Vec4f(const Vec2f &xy, F32 zz, F32 ww) {
			x = xy.x;
			y = xy.y;
			z = zz;
			w = ww;
		}

		__inline Vec4f(const Vec3f &xyz, F32 ww) {
			x = xyz.x;
			y = xyz.y;
			z = xyz.z;
			w = ww;
		}

		__inline Vec4f(const Vec2f &xy, const Vec2f &zw) {
			x = xy.x;
			y = xy.y;
			z = zw.x;
			w = zw.y;
		}

		__inline Vec4f(const Vec4i &v) {
			x = (F32) v.x;
			y = (F32) v.y;
			z = (F32) v.z;
			w = (F32) v.w;
		}

		__inline Vec4f(const float4 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
			w = v.w;
		}

		__inline const F32 *getPtr(void) const { return &x; }

		__inline F32 *getPtr(void) { return &x; }

		static __inline Vec4f fromPtr(const F32 *ptr) {
			return Vec4f(ptr[0], ptr[1], ptr[2], ptr[3]);
		}

		__inline    operator Vec4i(void) const { return Vec4i((S32) x, (S32) y, (S32) z, (S32) w); }

		__inline Vec2f getXY(void) const { return Vec2f(x, y); }

		__inline Vec3f getXYZ(void) const { return Vec3f(x, y, z); }

		__inline Vec3f getXYW(void) const { return Vec3f(x, y, w); }

#if !FW_CUDA

		static Vec4f fromABGR(U32 abgr);

		U32 toABGR(void) const;

#endif

		template<class V>
		__inline Vec4f(const VectorBase<F32, 4, V> &v) { set(v); }

		template<class V>
		__inline Vec4f &operator=(const VectorBase<F32, 4, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec2d : public VectorBase<F64, 2, Vec2d>, public double2 {
	public:
		__inline Vec2d(void) { setZero(); }

		__inline Vec2d(F64 a) { set(a); }

		__inline Vec2d(F64 xx, F64 yy) {
			x = xx;
			y = yy;
		}

		__inline Vec2d(const Vec2i &v) {
			x = (F64) v.x;
			y = (F64) v.y;
		}

		__inline Vec2d(const Vec2f &v) {
			x = v.x;
			y = v.y;
		}

		__inline Vec2d(const double2 &v) {
			x = v.x;
			y = v.y;
		}

		__inline const F64 *getPtr(void) const { return &x; }

		__inline F64 *getPtr(void) { return &x; }

		static __inline Vec2d fromPtr(const F64 *ptr) { return Vec2d(ptr[0], ptr[1]); }

		__inline    operator Vec2i(void) const { return Vec2i((S32) x, (S32) y); }

		__inline    operator Vec2f(void) const { return Vec2f((F32) x, (F32) y); }

		__inline Vec2d perpendicular(void) const { return Vec2d(-y, x); }

		__inline F64 cross(const Vec2d &v) const { return x * v.y - y * v.x; }

		template<class V>
		__inline Vec2d(const VectorBase<F64, 2, V> &v) { set(v); }

		template<class V>
		__inline Vec2d &operator=(const VectorBase<F64, 2, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec3d : public VectorBase<F64, 3, Vec3d>, public double3 {
	public:
		__inline Vec3d(void) { setZero(); }

		__inline Vec3d(F64 a) { set(a); }

		__inline Vec3d(F64 xx, F64 yy, F64 zz) {
			x = xx;
			y = yy;
			z = zz;
		}

		__inline Vec3d(const Vec2d &xy, F64 zz) {
			x = xy.x;
			y = xy.y;
			z = zz;
		}

		__inline Vec3d(const Vec3i &v) {
			x = (F64) v.x;
			y = (F64) v.y;
			z = (F64) v.z;
		}

		__inline Vec3d(const Vec3f &v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		__inline Vec3d(const double3 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		__inline const F64 *getPtr(void) const { return &x; }

		__inline F64 *getPtr(void) { return &x; }

		static __inline Vec3d fromPtr(const F64 *ptr) { return Vec3d(ptr[0], ptr[1], ptr[2]); }

		__inline    operator Vec3i(void) const { return Vec3i((S32) x, (S32) y, (S32) z); }

		__inline    operator Vec3f(void) const { return Vec3f((F32) x, (F32) y, (F32) z); }

		__inline Vec2d getXY(void) const { return Vec2d(x, y); }

		__inline Vec3d cross(const Vec3d &v) const {
			return Vec3d(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		}

		template<class V>
		__inline Vec3d(const VectorBase<F64, 3, V> &v) { set(v); }

		template<class V>
		__inline Vec3d &operator=(const VectorBase<F64, 3, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	class Vec4d : public VectorBase<F64, 4, Vec4d>, public double4 {
	public:
		__inline Vec4d(void) { setZero(); }

		__inline Vec4d(F64 a) { set(a); }

		__inline Vec4d(F64 xx, F64 yy, F64 zz, F64 ww) {
			x = xx;
			y = yy;
			z = zz;
			w = ww;
		}

		__inline Vec4d(const Vec2d &xy, F64 zz, F64 ww) {
			x = xy.x;
			y = xy.y;
			z = zz;
			w = ww;
		}

		__inline Vec4d(const Vec3d &xyz, F64 ww) {
			x = xyz.x;
			y = xyz.y;
			z = xyz.z;
			w = ww;
		}

		__inline Vec4d(const Vec2d &xy, const Vec2d &zw) {
			x = xy.x;
			y = xy.y;
			z = zw.x;
			w = zw.y;
		}

		__inline Vec4d(const Vec4i &v) {
			x = (F64) v.x;
			y = (F64) v.y;
			z = (F64) v.z;
			w = (F64) v.w;
		}

		__inline Vec4d(const Vec4f &v) {
			x = v.x;
			y = v.y;
			z = v.z;
			w = v.w;
		}

		__inline Vec4d(const double4 &v) {
			x = v.x;
			y = v.y;
			z = v.z;
			w = v.w;
		}

		__inline const F64 *getPtr(void) const { return &x; }

		__inline F64 *getPtr(void) { return &x; }

		static __inline Vec4d fromPtr(const F64 *ptr) {
			return Vec4d(ptr[0], ptr[1], ptr[2], ptr[3]);
		}

		__inline    operator Vec4i(void) const { return Vec4i((S32) x, (S32) y, (S32) z, (S32) w); }

		__inline    operator Vec4f(void) const { return Vec4f((F32) x, (F32) y, (F32) z, (F32) w); }

		__inline Vec2d getXY(void) const { return Vec2d(x, y); }

		__inline Vec3d getXYZ(void) const { return Vec3d(x, y, z); }

		__inline Vec3d getXYW(void) const { return Vec3d(x, y, w); }

		template<class V>
		__inline Vec4d(const VectorBase<F64, 4, V> &v) { set(v); }

		template<class V>
		__inline Vec4d &operator=(const VectorBase<F64, 4, V> &v) {
			set(v);
			return *this;
		}
	};

	//------------------------------------------------------------------------

	template<class T, int L, class S>
	__inline T lenSqr(const VectorBase<T, L, S> &v) { return v.lenSqr(); }

	template<class T, int L, class S>
	__inline T length(const VectorBase<T, L, S> &v) { return v.length(); }

	template<class T, int L, class S>
	__inline S normalize(const VectorBase<T, L, S> &v, T len = (T) 1) { return v.normalized(len); }

	template<class T, int L, class S>
	__inline T min(const VectorBase<T, L, S> &v) { return v.min(); }

	template<class T, int L, class S>
	__inline T max(const VectorBase<T, L, S> &v) { return v.max(); }

	template<class T, int L, class S>
	__inline T sum(const VectorBase<T, L, S> &v) { return v.sum(); }

	template<class T, int L, class S>
	__inline S abs(const VectorBase<T, L, S> &v) { return v.abs(); }

	template<class T, int L, class S>
	__inline S operator+(const T &a, const VectorBase<T, L, S> &b) { return b + a; }

	template<class T, int L, class S>
	__inline S operator-(const T &a, const VectorBase<T, L, S> &b) { return -b + a; }

	template<class T, int L, class S>
	__inline S operator*(const T &a, const VectorBase<T, L, S> &b) { return b * a; }

	template<class T, int L, class S>
	__inline S operator/(const T &a, const VectorBase<T, L, S> &b) {
		const T *bp = b.getPtr();
		S r;
		T *rp = r.getPtr();
		for (int i = 0; i < L; i++) rp[i] = a / bp[i];
		return r;
	}

	template<class T, int L, class S>
	__inline S operator%(const T &a, const VectorBase<T, L, S> &b) {
		const T *bp = b.getPtr();
		S r;
		T *rp = r.getPtr();
		for (int i = 0; i < L; i++) rp[i] = a % bp[i];
		return r;
	}

	template<class T, int L, class S>
	__inline S operator&(const T &a, const VectorBase<T, L, S> &b) { return b & a; }

	template<class T, int L, class S>
	__inline S operator|(const T &a, const VectorBase<T, L, S> &b) { return b | a; }

	template<class T, int L, class S>
	__inline S operator^(const T &a, const VectorBase<T, L, S> &b) { return b ^ a; }

	template<class T, int L, class S>
	__inline S operator<<(const T &a, const VectorBase<T, L, S> &b) {
		const T *bp = b.getPtr();
		S r;
		T *rp = r.getPtr();
		for (int i = 0; i < L; i++) rp[i] = a << bp[i];
		return r;
	}

	template<class T, int L, class S>
	__inline S operator>>(const T &a, const VectorBase<T, L, S> &b) {
		const T *bp = b.getPtr();
		S r;
		T *rp = r.getPtr();
		for (int i = 0; i < L; i++) rp[i] = a >> bp[i];
		return r;
	}

	template<class T, int L, class S, class V>
	__inline T dot(const VectorBase<T, L, S> &a, const VectorBase<T, L, V> &b) { return a.dot(b); }

	__inline Vec2f perpendicular(const Vec2f &v) { return v.perpendicular(); }

	__inline Vec2d perpendicular(const Vec2d &v) { return v.perpendicular(); }

	__inline F32 cross(const Vec2f &a, const Vec2f &b) { return a.cross(b); }

	__inline F64 cross(const Vec2d &a, const Vec2d &b) { return a.cross(b); }

	__inline Vec3f cross(const Vec3f &a, const Vec3f &b) { return a.cross(b); }

	__inline Vec3d cross(const Vec3d &a, const Vec3d &b) { return a.cross(b); }

#define MINMAX(T) \
    __inline T min(const T& a, const T& b)                          { return a.min(b); } \
    __inline T min(T& a, T& b)                                      { return a.min(b); } \
    __inline T max(const T& a, const T& b)                          { return a.max(b); } \
    __inline T max(T& a, T& b)                                      { return a.max(b); } \
    __inline T min(const T& a, const T& b, const T& c)              { return a.min(b).min(c); } \
    __inline T min(T& a, T& b, T& c)                                { return a.min(b).min(c); } \
    __inline T max(const T& a, const T& b, const T& c)              { return a.max(b).max(c); } \
    __inline T max(T& a, T& b, T& c)                                { return a.max(b).max(c); } \
    __inline T min(const T& a, const T& b, const T& c, const T& d)  { return a.min(b).min(c).min(d); } \
    __inline T min(T& a, T& b, T& c, T& d)                          { return a.min(b).min(c).min(d); } \
    __inline T max(const T& a, const T& b, const T& c, const T& d)  { return a.max(b).max(c).max(d); } \
    __inline T max(T& a, T& b, T& c, T& d)                          { return a.max(b).max(c).max(d); } \
    __inline T clamp(const T& v, const T& lo, const T& hi)          { return v.clamp(lo, hi); } \
    __inline T clamp(T& v, T& lo, T& hi)                            { return v.clamp(lo, hi); }

	MINMAX(Vec2i)

	MINMAX(Vec3i)

	MINMAX(Vec4i)

	MINMAX(Vec2f)

	MINMAX(Vec3f)

	MINMAX(Vec4f)

	MINMAX(Vec2d)

	MINMAX(Vec3d)

	MINMAX(Vec4d)

#undef MINMAX

	//------------------------------------------------------------------------
}
