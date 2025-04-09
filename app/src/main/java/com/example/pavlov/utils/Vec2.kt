package com.example.pavlov.utils

import kotlin.math.sqrt
import kotlin.random.Random

const val EPSILON = 0.001f
data class Vec2(
    val x: Float,
    val y: Float,
) {
    constructor(v: Float) : this(v, v)

    fun len2(): Float {
        return x * x + y * y
    }
    fun len(): Float {
        return sqrt(len2())
    }
    fun scale(s: Float): Vec2 {
        return Vec2(x*s, y*s)
    }
    fun add(other: Vec2): Vec2 {
        return Vec2(x+other.x, y+other.y)
    }
    fun sub(other: Vec2): Vec2 {
        return Vec2(x-other.x, y-other.y)
    }
    fun normalize0(): Vec2 {
        var v = this
        val l = len()
        if(l > EPSILON) {
            v = v.scale(1f / l)
        }
        return v
    }

    fun clamp(min: Vec2, max: Vec2): Vec2 {
        return Vec2(
            x = this.x.coerceIn(min.x, max.x),
            y = this.y.coerceIn(min.y, max.y)
        )
    }

    fun dist(other: Vec2): Float {
        return (other - this).len()
    }

    companion object {
        val Zero = Vec2(0f)
    }
}

/**
 * Generate a random point on the unit square centered on (0,0),
 * Then normalize the value to get a randomly oriented unit vector,
 */
fun getRandomUnitVec2(): Vec2 {
    var v = Vec2(
        Random.nextFloat(),
        Random.nextFloat(),
    )
    v = (v * 2f) - 1f
    return v.normalize0()
}

infix operator fun Vec2.times(f: Float): Vec2 {
    return this.scale(f)
}
infix operator fun Vec2.minus(v: Vec2): Vec2 {
    return this.sub(v)
}
infix operator fun Vec2.plus(v: Vec2): Vec2 {
    return this.add(v)
}
infix operator fun Vec2.minus(v: Float): Vec2 {
    return this.sub(Vec2(v))
}
infix operator fun Vec2.plus(v: Float): Vec2 {
    return this.add(Vec2(v))
}
