## What is Urban Crawl ##
[Urban Crawl](https://play.google.com/store/apps/details?id=com.akamaidev.urbancrawlapp) is a tiny catalogue of a few selected cities that you can carry along, and explore the interesting places around these cities.

### Why was Urban Crawl made ###
Urban Crawl is a reference app from Akamai Developer, which demonstrates the capabilities and usage of Akamai's MAP SDK (https://developer.akamai.com/tools/map/)

The source code of Urban Crawl will help the app developers understand how MAP SDK can be used in different scenarios, and how different functions of MAP SDK work.

### How to use the code ###
- Clone the repository in Android Studio below 3.0 (See known issues)
- Download a copy of MAP SDK bundle from your Luna Portal
- Replace the following keys with your own keys:
  -  MAP SDK License Key, in `res/xml/android_sdk_init.xml`
  -  Replace font certificate keys, in `res/values/font_certs.xml`
  -  If you're using Crashlytics, replace it with your own Crashlytics keys.
-  Build and Run (and let us know if you run into any issues)

Detailed instructions, to setup MAP SDK, can be found in the [Getting Started Guide](https://developer.akamai.com/blog/2017/03/20/getting-started-mobile-app-performance-map-sdk/). 

### How is the code organized ###
The following points will help the developer understand the structure of the code:

- All the activities are located inside com.akamaidev.urbancrawl package.
- The app uses simple MVP design pattern.
- All the network activities are done either inside, or initiated from the Models.
- All the Presenters and Models are located inside the `presenters` and `models` packages.
- The app uses GSON to convert the JSON that the REST API returns, and these GSON classes are inside `jsonobjs` package.
- All the other helper classes, are stored inside `helpers` package. These are classes which have methods for Analytics, Logging, Pickers, Events, and Adapters.
 
##### MAP SDK Specific instructions #####

- MAP SDK provides wrappers for OkHttp, Retrofit and Picasso, and these wrappers are stored under `MAPSDKWrappers` package.
- The `Default Networking Client` uses `AkaUrlConnection` to make Internet calls using MAP SDK
- The `CityDetailsModel` uses Retrofit to interact with our REST API, and uses the Retrofit Wrapper to make these HTTP calls go through MAP SDK
- The ` PlaceModel` uses Volley to interact with the REST API, and uses the OkHttpWrapper to make the HTTP calls go through MAP SDK
- All the models are stored in `models` package
- The `ImageLoader` class contains the centralized Picasso code to download images, and uses the `AkaPicassoDownloader` as Picasso's downloader. the `ImageLoader` class is under `helper` package.


The code tries to be self explanatory, but write to us in case you have any questions.

### Known Issues ###
- Like few of the libraries, we have noticed build errors while compiling on Android Studio 3.0, mainly because of the new Gradle and Gradle Wrapper versions. We'll find a stable fix to it and update the repo as soon as possible.

Write to us at [devrel@akamai.com](mailto:devrel@akamai.com) for any questions.