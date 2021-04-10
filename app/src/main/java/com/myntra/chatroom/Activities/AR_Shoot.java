package com.myntra.chatroom.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.myntra.chatroom.CustomARFragment;
import com.myntra.chatroom.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AR_Shoot extends AppCompatActivity {
private ModelRenderable modelRenderable;
    private final HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();

    private Texture texture;
private boolean isAdded=false;
//private String MODEL_URL="https://github.com/droidbg/ARCore/blob/main/glasses.glb?raw=true";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_a_r__shoot);

        CustomARFragment customARFragment= (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        ModelRenderable.builder().setSource(this, R.raw.sunglasses)
                .build()
                .thenAccept(renderable->{
                    modelRenderable=renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
//        ModelRenderable.builder().setSource(this,RenderableSource.builder().setSource(
//                this, Uri.parse(MODEL_URL),RenderableSource.SourceType.GLB)
//                .setScale(0.75f)
//                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
//                .build())
//                .setRegistryId(MODEL_URL)
//
//                .build()
//                .thenAccept(renderable->{
//                    modelRenderable=renderable;
//                    modelRenderable.setShadowCaster(false);
//                    modelRenderable.setShadowReceiver(false);
//                })
//        .exceptionally(throwable -> {
//            Toast.makeText(AR_Shoot.this,"CAN't Load",Toast.LENGTH_SHORT).show();
//            return null;
//        });
        Texture.builder().setSource(this,R.drawable.newglass)
                .build()
                .thenAccept(texture-> this.texture=texture);

        customARFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        customARFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (modelRenderable==null || texture==null)
                return;

            Frame frame=customARFragment.getArSceneView().getArFrame();
          assert frame != null;
//            Collection<AugmentedFace> augmentedFaces=frame.getUpdatedTrackables(AugmentedFace.class);
//            for(AugmentedFace augmentedFace:augmentedFaces){
//                if(isAdded)
//                    return;
//                AugmentedFaceNode augmentedFaceNode=new AugmentedFaceNode(augmentedFace);
//                augmentedFaceNode.setParent(customARFragment.getArSceneView().getScene());
//                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
//              augmentedFaceNode.setFaceMeshTexture(texture);
//                isAdded=true;
//            }

            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            // Make new AugmentedFaceNodes for any new faces.
            for (AugmentedFace augmentedFace : augmentedFaces) {
                if (isAdded) return;

                AugmentedFaceNode augmentedFaceMode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceMode.setParent(customARFragment.getArSceneView().getScene());
                augmentedFaceMode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceMode.setFaceMeshTexture(texture);
                faceNodeMap.put(augmentedFace, augmentedFaceMode);
                isAdded = true;

                // Remove any AugmentedFaceNodes associated with
                // an AugmentedFace that stopped tracking.
                Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iterator = faceNodeMap.entrySet().iterator();
                Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iterator.next();
                AugmentedFace face = entry.getKey();
                while (face.getTrackingState() == TrackingState.STOPPED) {
                    AugmentedFaceNode node = entry.getValue();
                    node.setParent(null);
                    iterator.remove();
                }
            }
        });
    }
}