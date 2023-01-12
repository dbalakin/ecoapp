package com.example.myapplication.data.geometry

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.math.max
import kotlin.math.min

object ClickHelper {

    fun check(polygon: List<Point>, q: Point): Boolean {
        val edges = mutableListOf<Pair<Point, Point>>()
        for (i in 1 until polygon.size) {
            edges.add(polygon[i] to polygon[i - 1])
        }
        edges.add(polygon.last() to polygon.first())
        var count = 0
        for ((a, b) in edges) {
            if (isOnSegment(a, b, q)) {
                return true
            } else if (a.longitude == b.longitude || q.longitude == min(a.longitude, b.longitude)) {
                continue
            } else if (q.longitude == max(a.longitude, b.longitude) && q.latitude <= min(a.latitude, b.latitude)) {
                count++
            } else if (q.longitude in a.longitude..b.longitude && isLeft(a, b, q)) {
                count++
            }
        }
        return count % 2 != 0
    }

    fun isOnSegment(one: Point, two: Point, q: Point): Boolean {
        val a = one.longitude - two.longitude
        val b = two.latitude - one.latitude
        val c = one.latitude * two.longitude - two.latitude * one.longitude
        val x = q.latitude
        val y = q.longitude
        val minX = min(one.latitude, two.latitude)
        val maxX = max(one.latitude, two.latitude)
        val minY = min(one.longitude, two.longitude)
        val maxY = max(one.longitude, two.longitude)
        return a * x + b * y + c <= 1e-6 && x in minX..maxX && y in minY..maxY
    }

    fun isLeft(a: Point, b: Point, q: Point): Boolean =
        (b.latitude - a.latitude) * (q.longitude - a.longitude) > (q.latitude - a.latitude) * (b.longitude - a.longitude)
}

object WDYHM {
    fun contains(points: List<LatLng>, test: LatLng): Boolean {
        var j: Int
        var result = false
        var i = 0
        j = points.size - 1
        while (i < points.size) {
            if (points.get(i).longitude > test.longitude != points.get(j).longitude > test.longitude &&
                test.latitude < (points.get(j).latitude - points.get(i).latitude) * (test.longitude - points.get(i).longitude) / (points.get(
                    j
                ).longitude - points.get(
                    i
                ).longitude) + points.get(i).latitude
            ) {
                result = !result
            }
            j = i++
        }
        return result
    }
}

object GFG {

    private fun onSegment(p: Point, q: Point, r: Point): Boolean {
        return q.latitude <= Math.max(p.latitude, r.latitude) && q.latitude >= Math.min(
            p.latitude,
            r.latitude
        ) && q.longitude <= Math.max(
            p.longitude,
            r.longitude
        ) && q.longitude >= Math.min(p.longitude, r.longitude)
    }

    private fun orientation(p: Point, q: Point, r: Point): Int {
        val `val` = ((q.longitude - p.longitude) * (r.latitude - q.latitude)
                - (q.latitude - p.latitude) * (r.longitude - q.longitude))
        if (`val` <= 1e-6) {
            return 0
        }
        return if (`val` > 0) 1 else 2
    }

    private fun doIntersect(
        p1: Point, q1: Point,
        p2: Point, q2: Point
    ): Boolean {
        val o1 = orientation(p1, q1, p2)
        val o2 = orientation(p1, q1, q2)
        val o3 = orientation(p2, q2, p1)
        val o4 = orientation(p2, q2, q1)

        if (o1 != o2 && o3 != o4) {
            return true
        }

        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true
        }

        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true
        }

        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true
        }

        return o4 == 0 && onSegment(p2, q1, q2)
    }

    fun isInside(polygon: List<Point>, n: Int, p: Point): Boolean {
        if (n < 3) {
            return false
        }
        val extreme = Point(90.0, p.longitude)
        var count = 0
        var i = 0
        do {
            val next = (i + 1) % n
            if (doIntersect(polygon[i], polygon[next], p, extreme)) {
                if (orientation(polygon[i], p, polygon[next]) == 0) {
                    return onSegment(
                        polygon[i], p,
                        polygon[next]
                    )
                }
                count++
            }
            i = next
        } while (i != 0)

        return count % 2 == 1 // Same as (count%2 == 1)
    }

}

typealias Point = LatLng
