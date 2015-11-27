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

#include "Math.hpp"

using namespace FW;

//------------------------------------------------------------------------

Vec4f Vec4f::fromABGR(U32 abgr)
{
    return Vec4f(
        (F32)(abgr & 0xFF) * (1.0f / 255.0f),
        (F32)((abgr >> 8) & 0xFF) * (1.0f / 255.0f),
        (F32)((abgr >> 16) & 0xFF) * (1.0f / 255.0f),
        (F32)(abgr >> 24) * (1.0f / 255.0f));
}

//------------------------------------------------------------------------

U32 Vec4f::toABGR(void) const
{
    return
        ((((U32)(((U64)(FW::clamp(x, 0.0f, 1.0f) * exp2(56)) * 255) >> 55) + 1) >> 1) << 0) |
        ((((U32)(((U64)(FW::clamp(y, 0.0f, 1.0f) * exp2(56)) * 255) >> 55) + 1) >> 1) << 8) |
        ((((U32)(((U64)(FW::clamp(z, 0.0f, 1.0f) * exp2(56)) * 255) >> 55) + 1) >> 1) << 16) |
        ((((U32)(((U64)(FW::clamp(w, 0.0f, 1.0f) * exp2(56)) * 255) >> 55) + 1) >> 1) << 24);
}

//------------------------------------------------------------------------
