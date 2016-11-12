import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class LiveCamSprite_test extends PApplet {





Capture video;
OpenCV opencv;

/* Declare global variables for the GifSprite class here */
  int total = 9;
  ArrayList<GifSprite> spriteList = new ArrayList<GifSprite>();
  int iteration = 0;

public void setup() {
  
  video = new Capture(this, 640/2, 480/2);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE); 
  video.start();
  /* setup for the GifSprite class */
  frameRate(30);
}

public void draw() {
  scale(2);
  opencv.loadImage(video);

  image(video, 0, 0 );
  background(255);
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  println(faces.length);

  for (int i = 0; i < faces.length; i++) {
    println(faces[i].x + "," + faces[i].y);
    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
    ellipse(faces[i].x+(0.5f)*faces[i].width, faces[i].y+(0.5f)*faces[i].height, 100, 100);
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

public void captureEvent(Capture c) {
  c.read();
}

/* Begin functions necessary for the functioning of the GifSprite class */ 
/* Begin declarations of functions necessary for the execution of the program */

public boolean loadSprites(int total, int iteration){
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

public void loadBar(int loaded, int total){
  fill(50,0,0);
  rect(0, height/2, total, 50);
  fill(255, 0, 0);
  rect(0, height/2, loaded, 50);
}

public boolean isInit(int iteration){
    if(iteration > 0){
      return false;
    }
    else{
      println("Initializing...");
      return true; 
    }
}

/* Class for constructing a GIF Sprite which can then be fed position information */
class GifSprite { 
  
  PVector location;
  PVector velocity; 
  PVector attractF;
  PVector repulseF;
  PVector randF;
  
  //Set the 'lifetime size' for this class
  int lifetime = 150;
  
  //Be sure to include the files necessary for the animation class in the data folder
  //Also need an array of strings with 8 elements in it
  //These strings denote the root name for the animation files in order of 
  //up->up-right->...clockwise...->up-left
  String imagePrefixes[] = {"up", "upRight","right","downRight","down","downLeft","left","upLeft"};
  Animation sprite = new Animation(imagePrefixes, 5);
  //Note that this instantiation of the animation sprite requires each animation to have 20 images
  //and the names of the animation files to begin with something listed in the array 'imagePrefixes'
  
  // Constructor for the gifSprite
  GifSprite(){
     //code for initializing the gifSprite 
     location = new PVector(random(0,width), random(0,height), 0);
     velocity = new PVector(random(1,5),random(1,5),random(1,5));
     attractF = new PVector(random(0,1), random(0,1), random(0,1));
     repulseF = new PVector(random(0,1), random(0,1), random(0,1));
     //println("Vx = "+velocity.x+", Vy = "+velocity.y);
     randF = new PVector(random(-10,10), random(-10,10), random(-10,10));
  }
  
  public void run(int locationX, int locationY, int locationZ){
    PVector target = new PVector(locationX, locationY, locationZ);
    PVector dir = PVector.sub(target, location);
    PVector empty = new PVector(0,0,0);
    //println("dir = <"+dir.x+", "+dir.y+"> with mag = "+dir.mag());
    
    //For an attempt at randomizing the movement, we can just use a random number generator
    int randFactor = 3;
    //randF = new PVector(random(-randFactor,randFactor), random(-randFactor,randFactor), random(-randFactor,randFactor));
    
    //A better approach for more organic random movement is to use a Perlin noise generator: 
    randF = new PVector(random(-randFactor, randFactor)*noise(millis()), random(-randFactor, randFactor)*noise(millis()+random(-1,1)), 0);
    
    PVector ADir = PVector.add(empty, dir);
    PVector RDir = PVector.add(empty, dir);
    float AMag;
    float RMag;
    float dirMag = dir.mag();
    
    //println("dir =  <"+target.x+", "+target.y+"> ");
    //copy the values of target into dir, to prepare for vector calculations
    
    //Construct the attractive forces
    AMag = 0.8f;
    ADir.normalize();
    ADir.mult(AMag);
    attractF = ADir;
    
    //Construct the repulsive forces
    RMag = 1/power(dirMag, 0.1f);
    //println(RMag);
    RDir.normalize();
    RDir.mult(-RMag);
    repulseF = RDir;
    //println(repulseF.x+repulseF.y);
    
    //Vector magnitudes have been shown to be conserved up to the 5th decimal place
    //of a float variable on 11/10/16 @ 1213 PST. See "gifSprite_Test_JBW_testVectors"
    //for example code testing vector functionalities.
    //println("velocity is = <"+velocity.x+", "+velocity.y+"> "); 
    velocity.limit(5);
    location.add(velocity);
    velocity.add(attractF);
    velocity.add(repulseF);
    velocity.add(randF);
    
    float error = 0.5f;
    dir.normalize();
    if((1 - error) > dir.mag() || (1 + error) < dir.mag() ){
      println("The dir vector is outside the margin of normalization.");
      println("dirNorm = <" + dir.x + ", " + dir.y + "> ");
      stop();
    }
    //println("dirNorm = <" + dir.x + ", " + dir.y + "> ");
    //Begin adding conditional flow dependent on the direction of dir
    
    /* Note that sqrt(3)/2 = 0.866, 1/2 = 0.5 */
    if(lifetime > 0){
      if(dir.x >= (0.866f - error) && dir.y >= (-1/2) && dir.y <= (1/2)){
         //use right animation 
         sprite.animate(location.x, location.y, "right");
      }
      else if(dir.x < (0.866f + error) && dir.x > (1/2) && dir.y > (1/2) && dir.y < (0.866f + error)){
         //use upRight animation 
         sprite.animate(location.x, location.y, "upRight");
      }
      else if(dir.x <= (1/2) && dir.x >= (-1/2) && dir.y >= (0.866f - error)){
         //use up animation 
         sprite.animate(location.x, location.y, "up");
      }
      else if(dir.x < (-1/2) && dir.x > (-0.866f - error) && dir.y > (1/2) && dir.y < (0.866f + error)){
         //use upLeft animation 
         sprite.animate(location.x, location.y, "upLeft");
      }
      else if(dir.x <= (-0.866f + error) && dir.y <= (1/2) && dir.y >= (-1/2)){
         //use left animation 
         sprite.animate(location.x, location.y, "left");
      }
      else if(dir.x < (-1/2) && dir.x > (-0.866f - error) && dir.y < (-1/2) && dir.y > (-0.866f - error)){
         //use downLeft animation 
         sprite.animate(location.x, location.y, "downLeft");
      }
      else if(dir.x >= (-1/2) && dir.x <= (1/2) && dir.y <= (-0.866f + error)){
         //use down animation 
         sprite.animate(location.x, location.y, "down");
      }
      else if(dir.x > (1/2) && dir.x < (0.866f + error) && dir.y < (-1/2) && dir.y > (-0.866f)){
         //use downRight animation 
         sprite.animate(location.x, location.y, "downRight");
      }
    }
      
      
      //Comment out the below decrementer in order to make the animations immortal
      //lifetime --;
    
    if(lifetime <= 0){
       println("The sprite has died!"); 
    }
    
  }
  
  /* Class for describing the animation for sprite 
     Note that this requires the animations files be in the 
     data folder for the sketch, and to be properly (8.3) named */
  class Animation {
    PImage[] up;
    PImage[] upRight;
    PImage[] right;
    PImage[] downRight;
    PImage[] down;
    PImage[] downLeft;
    PImage[] left;
    PImage[] upLeft;
    
    int imageCount;
    int frame;
  
    //Animation initialization now requires an array of strings which denote the root names 
    //for the animation files in ascending order 
    Animation(String imagePrefix[], int count) {
      imageCount = count;
      up = new PImage[imageCount];
      upRight = new PImage[imageCount];
      right = new PImage[imageCount];
      downRight = new PImage[imageCount];
      down = new PImage[imageCount];
      downLeft = new PImage[imageCount];
      left = new PImage[imageCount];
      upLeft = new PImage[imageCount];
    
    
    //Check that there are enough strings in the imagePrefix array
    if(imagePrefix.length < 8){
       println("There are not enough image prefixes in the image prefix array!!!");
       stop();
    }
    //We'll have to load images for all the different kinds of animation movements
    //Including up/up-right/right/down-right/down/down-left/left/up-left
    
      /* Load animations for the up-right movement */
      //println("Loading images for upRight animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[1] + nf(i, 3) + ".gif";
        upRight[i] = loadImage(filename);
      }
      //println("... completed upRight");
      
      /* Load animations for the right movement */
      //println("Loading images for right animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[2] + nf(i, 3) + ".gif";
        right[i] = loadImage(filename);
      }
      //println("... completed right");
      
      /* Load the animations for the down-right movement */
      //println("Loading images for downRight animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[3] + nf(i, 3) + ".gif";
        downRight[i] = loadImage(filename);
      }
      //println("... completed downRight");
      
      /* Load the animations for the down movement */
      //println("Loading images for down animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[4] + nf(i, 3) + ".gif";
        down[i] = loadImage(filename);
      }
      //println("... completed down");
      
      /* Load the animations for the down-left movement */
      //println("Loading images for downLeft animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[5] + nf(i, 3) + ".gif";
        downLeft[i] = loadImage(filename);
      }
      //println("... completed downLeft");
      
      /* Load the animations for the left movement */
      //println("Loading images for left animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[6] + nf(i, 3) + ".gif";
        left[i] = loadImage(filename);
      }
      //println("... completed left");
      
      /* Load the animations for the up-left movement */
      //println("Loading images for upLeft animation");
      for (int i = 0; i < imageCount; i++) {
        // Use nf() to number format 'i' into four digits
        String filename = imagePrefix[7] + nf(i, 3) + ".gif";
        upLeft[i] = loadImage(filename);
      }
      //println("... completed upLeft");
      
    }

    public void animate(float xpos, float ypos, String imType) {
      frame = (frame+1) % imageCount;
      switch(imType){
        case "up":
          image(up[frame], xpos, ypos);
          break;
        case "upRight":
          image(upRight[frame], xpos, ypos);
          break;
        case "right":
          image(right[frame], xpos, ypos);
          break;
        case "downRight":
          image(downRight[frame], xpos, ypos);
          break;
        case "down":
          image(down[frame], xpos, ypos);
          break;
        case "downLeft":
          image(downLeft[frame], xpos, ypos);
          break;
        case "left":
          image(left[frame], xpos, ypos);
          break;
        case "upLeft":
          image(upLeft[frame], xpos, ypos);
          break;
      }
      
    }
  
    // Legacy code from the original example for animating a sprite
    //int getWidth() {
    //  return images[0].width;
    //}
  }
}
/* End of GifSprite object */
  
/* Class for handling vector mathematics */
static class PVector {
  float x;
  float y;
  float z;
  
  PVector(float x_, float y_, float z_){
    x = x_;
    y = y_;
    z = z_;
  }

  public void replace(PVector v){
    x = v.x;
    y = v.y;
    z = v.z;
  }
  
  public void add(PVector v){
    y = y + v.y;
    x = x + v.x;
    z = z + v.z;
  }
  
   public void sub(PVector v){
     y = y - v.y;
     x = x - v.x;
     z = z - v.z;
   }
   
   public void mult(float n){
     y = y * n;
     x = x * n;
     z = z * n;
   }
   
   public void div(float n){
     y = y/n;
     x = x/n;
     z = z/n;
   }
   
   public float mag(){
     return sqrt(x*x + y*y + z*z);
   }
   
   public void normalize(){
     float m = mag();
     if(m != 0){
     div(m);
     }
   }
     
    public void limit(float max){
      if(abs(x) > max || abs(y) > max || abs(z) > max)
      {
       normalize();
       mult(max); 
      } 
   }
   
     
   
  /* Functions for actual vector mathematics in R3 */ 
  public static float dotProd(PVector v1, PVector v2){
    float result = v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
    return result;
  } 
  public static PVector add(PVector v1, PVector v2){
  PVector v3 = new PVector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
  return v3;
   }
  
  public static PVector sub(PVector v1, PVector v2){
  PVector v3 = new PVector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
  return v3;
  }
  
  public static PVector cross(PVector v1, PVector v2){
  PVector v3 = new PVector((v1.y*v2.z - v2.y*v1.z), -1*(v1.x*v2.z - v2.x*v1.z), (v1.x*v2.y - v2.x*v1.y));
  return v3;
  }
  
}

//Some math functions
public float power(float num, float n){
    float result = 1;
    if(n == 0){
      result = 1;
    }
    if(n > 0){
        for(int i = 0; i < n; i++){
          result = result * num;
        }
    }
    if(n < 0){
        for(int i = 0; i < n; i++){
           result = (1/result) * (1/num); 
        }
    }
    if(result <= 0){
       println("Error: function 'power' did not compute correctly.");
    }
    return result;
}

  public void settings() {  size(640, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#1A0202", "--stop-color=#FF0505", "LiveCamSprite_test" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
