package com.example.gltf_get_start

import com.google.android.filament.utils.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Choreographer
import android.view.SurfaceView
import com.google.android.filament.Skybox
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    companion object {
        init { Utils.init() }
    }
    private lateinit var surfaceView: SurfaceView
    private  lateinit var choreographer: Choreographer
    private  lateinit var modelViewer: ModelViewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = SurfaceView(this).apply { setContentView(this) }
        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)
        //loadGlb("DamageHelmet")
        loadGltf("BusterDrone")

        /* An another of using the ecs
        * let’s try hiding the floor disk
        * and disabling the emissive tail lights on the back of the drone.
        * */
        val asset = modelViewer.asset!!
        val rm = modelViewer.engine.renderableManager
        for(entity in asset.entities){
            val renderable = rm.getInstance(entity)
            if (renderable == 0){
                continue
            }
            if(asset.getName(entity)== "Scheibe_Boden_0"){
                // call setLayerMask to hide floor disk from the view.
                rm.setLayerMask(renderable,0xff,0x00)
            }
            val metarial = rm.getMaterialInstanceAt(renderable,0)
            metarial.setParameter("emissiveFactor",0f,0f,0f)
        }
        // Becase we use setLayerMask after call loadModelGltf
        // so we loadEnviroment after this snippet
        loadEnvironment("venetian_crossroads_2k")
        modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)

    }
    // allow Filament to render during the frame callback

    private val  frameCallback = object : Choreographer.FrameCallback{
        /*
        override fun doFrame(currentTime: Long) {
            choreographer.postFrameCallback(this)
            modelViewer.render(currentTime)
        }
         */
        // New animation
        private  val startTime = System.nanoTime()
        override  fun doFrame(currentTime: Long){
            val seconds = (currentTime - startTime).toDouble()/1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer.animator?.apply {
                if(animationCount > 0){
                    /*
                    This is the key piece.
                    This takes two arguments:
                    + an index into the model's list of animation definitions
                    + and the elapsed time for that particular animation

                     */
                    applyAnimation(0,seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewer.render(currentTime)

            // Reset the root transform, then rotate it around the Z axis.
            modelViewer.asset?.apply {
                modelViewer.transformToUnitCube()
                val rootTransform = this.root.getTransform()
                val degrees = 20f*seconds.toFloat()
                val zAxis = Float3(0f,0f,1f)
                this.root.setTransform(rootTransform*rotation(zAxis,degrees))
            }
        }
    }
    // two helper methods for animation rotate around the Z axis
    private  fun Int.getTransform():Mat4{
        val tm = modelViewer.engine.transformManager
        return  Mat4.of(*tm.getTransform(tm.getInstance(this),null))
    }

    private fun Int.setTransform(mat:Mat4){
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this),mat.toFloatArray())
    }
    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameCallback)
    }
    //-> At this point, the Filament engine is instantiated and an OpenGL context is created.

    private  fun loadGlb (name: String){
        val buffer = readAsset("models/${name}.glb")
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()
        /* This tells the model viewer to transform the root node of the scene such
        that it fits into a 1x1x1 cube centered at the origin. */
    }

    private  fun readAsset(assetName:String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }
    //  Add an indirect light source and a skybox

    private  fun loadEnvironment(ibl:String){
        //Create the indirect light source and add it to the scene.
        var buffer = readAsset("envs/$ibl/${ibl}_ibl.ktx")
        KtxLoader.createIndirectLight(modelViewer.engine,buffer).apply {
            intensity = 50_000f
            modelViewer.scene.indirectLight = this
        }

        // Create the sky box and add it to the scene.
        buffer = readAsset("envs/$ibl/${ibl}_skybox.ktx")
        KtxLoader.createSkybox(modelViewer.engine,buffer).apply {
            modelViewer.scene.skybox = this
        }
    }

    // JSON-based glTF
    // load model along with its external resources
    private fun loadGltf(name:String){
        val buffer = readAsset("models/${name}.gltf")
        modelViewer.loadModelGltf(buffer) {uri -> readAsset("models/$uri")}
        modelViewer.transformToUnitCube()
    }

    // Applying Animation


}