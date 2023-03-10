

#### как добавить в зависимость aar
см. lunaCore/ и settings.gradle.
положить lunaCore.aar файл в соотствующую папку и добавить эту папку как зависимости в settings.gradle.


#### как инициализировать Luna ID SDK
см. App


#### как добавить оверлей в камеру
добавить в свое приложение лейаут с именем vl_lunacamera_overlay.  
в нем может быть что угодно. например, фрагмент.  
см. OverlayFragment


#### как сделать оверлей статический
см. OverlayFragment + ViewportView


#### как получать ошибки распознавания лица
см. CameraOverlayDelegate и в нем подписку на события из камеры.


#### как сделать оверлей с динамической рамкой лица
см. OverlayFragment + FaceDetectionView + CameraOverlayDelegate


#### CameraOverlayDelegate и динамическая типизация через словари
события из камеры приходят через CameraOverlayDelegate в словарях.  
какие структуры могут прийтий в словарях - см. OverlayFragment.  
 

#### как добавить лицензию mobileSDK
в своем приложении положить файл лицензии по пути src/main/assets/data/license.conf.  


### как перезаписать настройки mobileSDK
положить соответствующие файлы настроек по пути src/main/assets/data/.  


### как изменить настройки распознавания

В LunaCore.aar используются настройки детекции по умолчанию.  
Эти настройки хранятся в .conf файлах, которые лежат внутри LunaCore.aar и напрямую их изменить нельзя.  
Однако их можно изменить, если положить одноименные файлы в своем приложении по пути `assets/data`.  
Например, чтобы если нужно поменять настройки FaceEngine, тогда внутри вашего приложения, куда подключается LunaCore.aar как зависимость, нужно создать файл `assests/data/faceengine.conf`, в котором будут прописаны все настройки FaceEngine.  
Для списка доступных настроек обратитесь к документации mobileSDK.  
Важно, чтобы ваш `faceengine.conf` содержал все настройки, а не только те, которые вы хотите изменить, потому что ваш файл полностью перезапишет все настройки, которые содержатся в LunaCore.aar, а не дополнит их.

### как уменьшить размер приложения, исключив лишние .plan-файлы

#### build.gradle.kts

```kotlin
android {
    ...
    
    androidResources {
        ignoreAssetsPatterns.addAll(
            listOf(
                "!glasses_estimation_flwr_arm.plan",
                "!glasses_estimation_flwr_cpu.plan",
                "!mask_clf_v3_arm.plan",
                "!mask_clf_v3_cpu.plan",
                "!oslm_v4_model_1_arm.plan",
                "!oslm_v4_model_1_cpu.plan",
                "!oslm_v4_model_2_arm.plan",
                "!oslm_v4_model_2_cpu.plan",
                "!cnn59m_arm.plan",
                "!cnn59m_cpu.plan",
                "!cnndescriptor_59.conf",
            )
        )
    }
    
    ...
}
```

#### build.gradle

```groovy
android {
    ...

    androidResources {
        ignoreAssetsPatterns.addAll(
                [
                        "!glasses_estimation_flwr_arm.plan",
                        "!glasses_estimation_flwr_cpu.plan",
                        "!mask_clf_v3_arm.plan",
                        "!mask_clf_v3_cpu.plan",
                        "!oslm_v4_model_1_arm.plan",
                        "!oslm_v4_model_1_cpu.plan",
                        "!oslm_v4_model_2_arm.plan",
                        "!oslm_v4_model_2_cpu.plan",
                        "!cnn59m_arm.plan",
                        "!cnn59m_cpu.plan",
                        "!cnndescriptor_59.conf",
                ]
        )
    }
    
    ...
}
```

### как замерить размер, который добавляет Luna ID SDK к вашему приложению

Обновить файлы сборки, чтобы собирались отдельные .apk файлы под разные платформы.  

#### build.gradle.kts:

```kotlin
android {
    ...

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    ...
}
```

#### build.gradle
```groovy
android {
    ...

    splits {
        abi {
            enable true
            reset()
            include "armeabi-v7a", "arm64-v8a", "x86", "x86_64"
            universalApk false
        }
    }
    
    ...
}
```

В Android Studio запустить утилиту `Analyze APK`.  
В ней открыть собранный .apk файл конкретной платформы (например, `armeabi-v7a`).  
И посмотреть, сколько занимают следующие файлы:

  * папка assets/data
  * lib/{platform}/libTrackEngineSDK.so
  * lib/{platform}/libBestShotMobile.so
  * lib/{platform}/libflower.so
  * lib/{platform}/libMatchingKernel.s
  * lib/{platform}/libFaceEngineSDK.so
  * lib/{platform}/libwrapper.so
  * lib/{platform}/libc++_shared.so

Любые другие файлы не относятся к Luna ID SDK и добавляются другими зависимостями вашего приложения.  

Обратите внимание: в утилите `Analyze APK` в папке `lib` должна быть только одна платформа ("armeabi-v7a", "arm64-v8a" или другая). Если в этой папке больше одной платформы, значит вы смотрите универсальный .apk файл, который включает все платформы. Вернитесь на шаг назад и пересоберите приложение с включенным `splits.abi`.  

