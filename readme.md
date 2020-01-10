build apk

   set new version
     app/build.gradle  ->  increase version, for example
             versionCode 35
             versionName "V3.0.5"
             versionCode 36
             versionName "V3.0.6"

  select build -> generate signed Bundle / apk

  key file = /signfile/bigTangleKey
  key store  password =bigtangle
  key password =bigtangle


  select release

  build generate two files release/app-release.apk release/output.json

publish to aliyun for download
    modify the file  ?
    upload  two files to aliyun