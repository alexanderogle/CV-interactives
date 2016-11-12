import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

PImage bg; 

/* Declare global variables for the GifSprite class here */
  int total = 9;
  ArrayList<GifSprite> spriteList = new ArrayList<GifSprite>();
  int iteration = 0;

void setup() {
  size(640, 480);
  video = new Capture(this, 640/2, 480/2);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE); 
  video.start();
  /* setup for the GifSprite class */
  frameRate(30);
  
  /* load the image you would like to use as the background for the application. 
     This image will have to be the same dimension as the parameters passed through
     the size function. */
  bg = loadImage("background.png");
}

void draw() {
  scale(2);
  opencv.loadImage(video);

  image(video, 0, 0 );
  background(bg);
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  println(faces.length);

  for (int i = 0; i < faces.length; i++) {
    println(faces[i].x + "," + faces[i].y);
    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
    ellipse(faces[i].x+(0.5)*faces[i].width, faces[i].y+(0.5)*faces[i].height, 100, 100);
  }
  
  /* Begin functions for generating the GifSprites */
  loadSprites(total, iteration);
  for(int i = 0; i < total; i++){
   GifSprite gs = spriteList.get(i);
   if(faces.length>0){
     gs.run(faces[0].x, faces[0].y, 0); 
   }
   else{
     gs.run(mouseX, mouseY, 0);
   }
  }
  //println(iteration);
  iteration++;
}

void captureEvent(Capture c) {
  c.read();
}

/* Begin functions necessary for the functioning of the GifSprite class */ 
/* Begin declarations of functions necessary for the execution of the program */

boolean loadSprites(int total, int iteration){
  boolean completed = !isInit(iteration);
  if(!completed){
    int time0 = millis();
    println("Loading sprites... ");
    for(int i = 0; i < total; i++){
      loadBar(i, total);
      spriteList.add(new GifSprite());
      println("Sprite number "+i+" of "+total+" has been loaded");
    }
    int time1 = millis() - time0;
    println("... completed load in "+time1+" milliseconds");
  }
  return completed = true;
}

void loadBar(int loaded, int total){
  fill(50,0,0);
  rect(0, height/2, total, 50);
  fill(255, 0, 0);
  rect(0, height/2, loaded, 50);
}

boolean isInit(int iteration){
    if(iteration > 0){
      return false;
    }
    else{
      println("Initializing...");
      return true; 
    }
}