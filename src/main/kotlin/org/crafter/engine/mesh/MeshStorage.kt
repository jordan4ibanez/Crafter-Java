
package org.crafter.engine.mesh

/**
 * This class holds all the meshes in game!
 * To create a mesh, you have to talk to this class.
 */
object MeshStorage {
    private val container = HashMap<String, Mesh>()

    // Create a new mesh
    @JvmStatic
    fun newMesh(
        meshName: String,
        positions: FloatArray?,
        textureCoordinates: FloatArray?,
        indices: IntArray?,
        bones: IntArray?,
        colors: FloatArray?,
        textureFileLocation: String?,
        is2d: Boolean
    ) {
        if (container.containsKey(meshName)) {
            throw RuntimeException("MeshStorage: Tried to create mesh ($meshName) more than once!")
        }
        container[meshName] =
            Mesh(meshName, positions!!, textureCoordinates!!, indices!!, bones, colors, textureFileLocation!!, is2d)
    }

    // Swap a mesh's texture
    fun swapTexture(meshName: String, newTextureLocation: String?) {
        checkExistence(meshName)
        container[meshName]!!.swapTexture(newTextureLocation!!)
    }

    // Render a mesh
    @JvmStatic
    fun render(meshName: String) {
        checkExistence(meshName)
        container[meshName]!!.render()
    }

    // Destroy a SINGLE mesh in the container
    @JvmStatic
    fun destroy(meshName: String) {
        checkExistence(meshName)
        container[meshName]!!.destroy()
        container.remove(meshName)
    }

    // Destroys ALL meshes in the container - Only run this AFTER the main loop has run
    fun destroyAll() {
        for (mesh in container.values) {
            mesh.destroy()
        }
        container.clear()
    }

    // Helper method for preventing undefined behavior
    private fun checkExistence(meshName: String) {
        if (!container.containsKey(meshName)) {
            throw RuntimeException("MeshStorage: Tried to access nonexistent mesh ($meshName!")
        }
    }
}
