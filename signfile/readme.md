build apk

   set new version
     app/build.gradle  ->  increase version, for example
             versionCode 35
             versionName "V3.0.5"
             versionCode 36
             versionName "V3.0.6"

  select build -> generate signed Bundle / apk

  key file = /signfile/bigTangleKey
  key store  password =
  key password =


select release

  build generate the files release/app-release.apk release/output.json

publish to aliyun for download
    modify the file  signfile/app.version for new version to enable automatic update
    upload  two files  release/app-release.apk  and signfile/app.version  to aliyun
