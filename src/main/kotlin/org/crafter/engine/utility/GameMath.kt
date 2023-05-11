package org.crafter.engine.utility

import org.joml.Math
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc

object GameMath {
    private val workerVector = Vector3f()

    // For some reason, JOML does not expose this variable
    const val pIHalf_f = (Math.PI * 0.5).toFloat()
    const val pi2 = (Math.PI * 2.0).toFloat()

    /**
     * Utilized for calculation of 2d movement from yaw
     */
    fun getHorizontalDirection(yaw: Float): Vector3fc {
        startWork()
        workerVector.x = Math.sin(-yaw)
        workerVector.z = Math.cos(yaw)
        return work
    }

    fun yawToLeft(yaw: Float): Float {
        return yaw - pIHalf_f
    }

    /**
     * This is simply to neaten up the plain english readability of this.
     * I also don't want to forget to clear out the old data
     */
    private fun startWork() {
        workerVector.zero()
    }

    private val work: Vector3fc
        /**
         * this is because I don't feel like casting the worker vector into 3ic every time
         */
        get() = workerVector

    /**
     * This is for printing out JOML vector3fs because it's simply unusable toString & doing this is annoying
     */
    fun printVector(i: Vector3fc) {
        println(i.x().toString() + ", " + i.y() + ", " + i.z())
    }

    /**
     * Ditto
     */
    fun printVector(info: String, i: Vector3fc) {
        println(info + ": " + i.x() + ", " + i.y() + ", " + i.z())
    }

    /**
     * This is for printing out JOML vector2fs because it's simply unusable toString & doing this is annoying
     */
    fun printVector(i: Vector2fc) {
        println(i.x().toString() + ", " + i.y())
    }

    /**
     * Ditto
     */
    fun printVector(info: String, i: Vector2fc) {
        println(info + ": " + i.x() + ", " + i.y())
    }
}
