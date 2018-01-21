## virtual-fitting-room (for Android)

An app that helps with fitting accessaries on any of your personal pictures that has your face in it!
Matches you face in the picture and fits the chosen accessary on the right spot.

Reference:
[![Build Status](https://travis-ci.org/tzutalin/dlib-android-app.png)](https://travis-ci.org/tzutalin/dlib-android-app)
[ ![Download](https://api.bintray.com/packages/tzutalin/maven/dlib-android-app/images/download.svg) ](https://bintray.com/tzutalin/maven/dlib-android-app/_latestVersion)

See [dlib-android](https://github.com/tzutalin/dlib-android) for JNI lib. Refer to dlib-android/jni/jnilib_ex


### Features

* Adding accessaries to a portrait for fitting

* Saving the processed rendering to the local gallery

### Demo
![](demo/demo1.png)


### Instruction

Download and install the apk.

Choose a picture that has your face in it.

Choose an accessary picture that has been cutout.

Submit and enjoy the rendering.


### Sample code

Facial landmark detection
```java
        for (VisionDetRet ret : results) {
            Rect bounds = new Rect();
            bounds.left = (int) (ret.getLeft() * resizeRatio);
            bounds.top = (int) (ret.getTop() * resizeRatio);
            bounds.right = (int) (ret.getRight() * resizeRatio);
            bounds.bottom = (int) (ret.getBottom() * resizeRatio);
            //canvas.drawRect(bounds, paint);
            // Get landmark
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            int counter = 0;
            for (Point point : landmarks) {
                if((counter==0) || (counter==8) || (counter==16)) {
                    cords.add(point);
                    int pointX = (int) (point.x * resizeRatio);
                    int pointY = (int) (point.y * resizeRatio);
                    //canvas.drawCircle(pointX, pointY, 2, paint);
                }
                counter++;
            }
        }
```
