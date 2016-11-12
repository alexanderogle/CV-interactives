# openCV-GifSprite
Application for loading custom GIF animations and providing basic vector functions to simulate particle behavior. Utilizes openCV to allow tracking of targets using a webcam and custom made Haar Cascade files. 

Last update: 11/11/16 @ 1605 PST

This code is based off coding examples I found from the following places: 
 - For animating a series of GIF files: https://processing.org/examples/animatedsprite.html 
 - For creating particle systems, etc, in Processing: http://natureofcode.com/

The code needs to be cleaned up eventually, as this application is still under development. 

If you want to utilize your own animations in this application, you need only edit the files in the "data" folder.

You will need animations for each of the following eight (somewhat) cardinal directions:

up
upRight
right
downRight
down
downLeft
left
upLeft

Without editing any of the code, the animation files will need to have filenames that are of the following format: up000.gif --> up019.gif upRight000.gif --> upRight019.gif . . . upLeft000.gif --> upLeft019.gif

The animations can be any arbitrary size, or any arbitrary order, just know that they will be loaded from 000 to 019 and then immediately load again from 000 again.

If you play with the code at https://processing.org/examples/animatedsprite.html, you should be able to figure out how to edit the number of animation frames that get loaded and what their filenames need to be.

Don't know how to make single frame Gif animations? This is where I learned how to do it in Adobe Illustrator: https://design.tutsplus.com/tutorials/how-to-create-animated-vector-icons-in-adobe-illustrator-and-photoshop--cms-24451 Note: You don't have to copy and paste the illustrator vector files to photoshop, you can just hide all the layers you don't want to be a part of the Gif file, then export directly to Gif using the hotkey combo: CMD+ALT+SHIFT+S on Mac

As for the openCV portion, I intend on training my own custom Haar classifier for the detecting the objects I need it to, but if you're curious how to go about doing that yourself, I recommend you read the following on training your own openCV Haar classifier: 
http://coding-robin.de/2013/07/22/train-your-own-opencv-haar-classifier.html