package com.example.arcore;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ArFragment arFragment;
    private ModelRenderable bearRenderable, catRenderable, chair4Renderable, couchRenderable;
    private enum ObjectType {
        BEAR,
        CAT,
        COUCH,
        CHAIR4,
    }
    ObjectType objectType = ObjectType.COUCH;
    ImageView bear, cat, chair4, couch_fbx;
    View arrayView[];
    ViewRenderable name_animal;
    int selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        bear = (ImageView) findViewById(R.id.bear);
        bear.setOnClickListener(view -> objectType = ObjectType.BEAR);
        cat = (ImageView) findViewById(R.id.cat);
        cat.setOnClickListener(view -> objectType = ObjectType.CAT);
//        cow = (ImageView) findViewById(R.id);
        chair4 = (ImageView) findViewById(R.id.cow);
        chair4.setOnClickListener(view -> objectType = ObjectType.CHAIR4);
        couch_fbx = (ImageView) findViewById(R.id.couch_fbx);
        couch_fbx.setOnClickListener(view -> objectType = ObjectType.COUCH);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            switch (objectType) {
                case BEAR:
                    createBear(hitResult.createAnchor());
                    break;
                case CAT:
                    createCat(hitResult.createAnchor());
                    break;
                case CHAIR4:
                    createChair4(hitResult.createAnchor());
                    break;
                case COUCH:
                    createCouch(hitResult.createAnchor());
                    break;
                default:
                    break;
            }
        });
        setArrayView();
//        setClickListener();
//        setupModel();
//        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
//            @Override
//            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
//                if (selected==1){
//                    Anchor anchor = hitResult.createAnchor();
//                    AnchorNode anchorNode = new AnchorNode(anchor);
//                    anchorNode.setParent(arFragment.getArSceneView().getScene());
//                    createModel(anchorNode, selected);
//                }
//            }
//        });
    }

    private void createChair4(Anchor anchor) {
        ModelRenderable.builder().setSource(this, R.raw.chair4)
                .build().thenAccept(renderable -> {
            chair4Renderable = renderable;
            placeModel(chair4Renderable, anchor);

        });
    }

    private void createBear(Anchor anchor) {
        ModelRenderable.builder().setSource(this, R.raw.chair3)
                .build().thenAccept(renderable -> {
                    bearRenderable = renderable;
                    placeModel(bearRenderable, anchor);

        });

    }

    private void createCat(Anchor anchor) {
        ModelRenderable.builder().setSource(this, R.raw.chair1)
                .build().thenAccept(renderable -> {
            catRenderable = renderable;
            placeModel(catRenderable, anchor);

        });
    }
    private void createCouch(Anchor anchor) {
        ModelRenderable.builder().setSource(this, R.raw.couch_fbx)
                .build().thenAccept(renderable -> {
            couchRenderable = renderable;
            placeModel(couchRenderable, anchor);

        });
    }
    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }


//    private void setupModel() {
//
//        ModelRenderable.builder().setSource(this, R.raw.bear)
//                .build().thenAccept(renderable -> bearRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//
//                                Toast.makeText(this, "Unable load", Toast.LENGTH_SHORT).show();
//                                return null;
//                        }
//                );
//        ModelRenderable.builder().setSource(this, R.raw.cat)
//                .build().thenAccept(renderable -> catRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//
//                            Toast.makeText(this, "Unable load", Toast.LENGTH_SHORT).show();
//                            return null;
//                        }
//                );
//        ModelRenderable.builder().setSource(this, R.raw.cow)
//                .build().thenAccept(renderable -> cowRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//
//                            Toast.makeText(this, "Unable load", Toast.LENGTH_SHORT).show();
//                            return null;
//                        }
//                );
//        ModelRenderable.builder().setSource(this, R.raw.couch_fbx)
//                .build().thenAccept(renderable -> cowRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//
//                            Toast.makeText(this, "Unable load", Toast.LENGTH_SHORT).show();
//                            return null;
//                        }
//                );
//    }
//
//    private void createModel(AnchorNode anchorNode, int selected) {
//        if(selected==1){
//            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
//            bear.setParent(anchorNode);
//            bear.setRenderable(bearRenderable);
//            bear.select();
//        }
//        if(selected==2){
//            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
//            bear.setParent(anchorNode);
//            bear.setRenderable(catRenderable);
//            bear.select();
//        }
//        if(selected==1){
//            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
//            bear.setParent(anchorNode);
//            bear.setRenderable(cowRenderable);
//            bear.select();
//        }
//        if(selected==1){
//            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
//            bear.setParent(anchorNode);
//            bear.setRenderable(couchRenderable);
//            bear.select();
//        }
//    }
//
    private void setClickListener(){
        for (int i=0;i<arrayView.length; i++){
            arrayView[i].setOnClickListener(this);
        }
    }
    private void setArrayView(){
        arrayView = new View[]{
                bear,cat,couch_fbx
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bear){
            setBackground(view.getId());
        }
        else  if(view.getId() == R.id.cat){
            setBackground(view.getId());
        }
        else if (view.getId() == R.id.cow) {
            setBackground(view.getId());
        }
        else if (view.getId() == R.id.couch_fbx) {
            setBackground(view.getId());
        }

    }

    private void setBackground(int id) {
        for (int i=0; i<arrayView.length; i++) {
            if (arrayView[i].getId() == id) {
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"));
            }
            else {
                arrayView[i].setBackgroundColor((Color.TRANSPARENT));
            }
        }
    }
}
