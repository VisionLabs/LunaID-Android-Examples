

#### как добавить в зависимость aar
Пример ниже описанного можно посмотреть в `CameraExample`.

##### добавить репозиторий в `repositories`:
```kotlin
    repositories {
        ...

        ivy {
            url = java.net.URI.create("https://download.visionlabs.ru/")
            patternLayout {
                artifact ("[organisation]/[artifact]-[revision].[ext]")
                setM2compatible(false)
            }
            credentials {
                username = getLocalProperty("vl.login") as String
                password = getLocalProperty("vl.pass") as String
            }
            metadataSources { artifact() }
        }
    }
```

##### настройте логин и пароль для скачивания

Файлы с `https://download.visionlabs.ru/` можно скачивать только с аутентификацией.
Сохраните логин и пароль в файле `local.properties`:

```shell
vl.login=YOUR_LOGIN
vl.pass=YOUR_PASSWORD
```

и добавьте функцию для получения логина и пароля в удобное место.

```kotlin
fun getLocalProperty(key: String, file: String = "local.properties"): Any {
    val properties = java.util.Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        java.io.InputStreamReader(java.io.FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found: '$file'")

    if (!properties.containsKey(key)) {
        error("Key not found '$key' in file '$file'")
    }
    return properties.getProperty(key)
}
```

Примечание: файл `local.properties` принято добавлять в `.gitignore`, чтобы он не попал в систему контроля версий.


##### добавить зависимость в build.gradle
```kotlin
dependencies {
    ...

    implementation("ai.visionlabs.lunaid:core:{VERSION}@aar")
}
```

Пример: `implementation("ai.visionlabs.lunaid:core:1.2.3@aar")`.


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

```kotlin
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

Примечание: для AGP версии 7.1 или ниже `androidResources` замените на `AaptOptions`.


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


#### как включить дескрипторы

Обратите внимание: для генерации дескрипторов в конечное приложение добавятся дополнительные файлы, поэтому размер приложения увеличится.

##### 
Убрать из игнорируемых файлов `cnn` файлы.

```kotlin
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
                ]
        )
    }
    
    ...
}
```

#####
При инициализации `LunaID` выставить `areDescriptorsEnabled = true`.

```kotlin
        LunaID.init(
            app = this@App,
            lunaConfig = LunaConfig.create(),
            areDescriptorsEnabled = true,
        )
```

#####
Чтобы получить или сравнить дескрипторы воспользоваться методами из `LunaUtils` 

* getDescriptor(image)
* getDescriptorFromWrapped(image)
* matchDescriptors(first, second)

#### Как использовать интеракцию
Для кейса, когда от пользователя требуется выполнить действие во время съемки, мы поддерживаем моргание.  
За интеракцию отвечают два параметра в `LunaCongig`.  
`interactionEnabled` - включает или выключает интеракцию во время съемки.  
`interactionTimeout` - как долго (в миллисекундах) камера будет ожидать выполнения действия от пользователя прежде, чем вернуть ошибку.  


Пример:  
```kotlin
        LunaID.init(
            this,
            LunaConfig.create(
                interactionEnabled = true,
                interactionTimeout = 10_000,
            )
        )
```

Если интеракция включена (`interactionEnabled = true`), то спустя `interactionTimeout` в `CameraUIDelegate#onError()` прилетит ошибка `InteractionTimeout`.  


#### Как пользоваться интерфейсом в Platform API
В состав `Luna ID SDK` входит класс для совершения вызовов к ендпоинтам [Platform API](https://docs.visionlabs.ai/luna/v.5.28.0/ReferenceManuals/APIReferenceManual.html).  

##### инициализация

```kotlin
        val baseUrl = "http://luna-platform.com/api/6/"

        val apiConfig = ApiHumanConfig(
            baseUrl = baseUrl,
        )

        val platformApi = ApiHuman(apiConfig)

```

##### добавление статических заголовков

Можно указать заголовки, которые будут использоваться в каждом запросе.  

```kotlin
        // will be added to every request
        val constantHeaders = mapOf(
            "Luna-Account-Id" to "12ed7399-f779-479c-8258-bbc45e6017af"
        )

        val apiConfig = ApiHumanConfig(
            ...
            headers = constantHeaders,
        )

```

##### добавление динамических заголовков

В некоторых случаях заголовки не известны в момент инициализации `ApiHuman`.  
Все запросы `ApiHuman` принимают в качестве параметра дополнительные опциональные заголовки, которые будет добавлены только к этому запросу.

```kotlin
        val api: ApiHuman
        
        val photo: ByteArray

        // one-shot headers
        val dynamicHeaders = mapOf(
            "X-Auth-Token" to "secret_token1"
        )

        val request = VerifyRequest(
            verifierId =  "sdfqw-123asdf-af123",
            payload = BinaryPayload.Photo(photo),
            extraHeaders = dynamicHeaders,
        )

        api.apiVerifiers(
            data = request,
            consumer = verifyResponseConsumer
        )

```

##### использование

Класс `ApiHuman` предоставляет несколько методов для взаимодействия с `Platform API`.  

[#apiEvents](https://docs.visionlabs.ai/luna/v.5.28.0/ReferenceManuals/APIReferenceManual.html#operation/generateEvents)
POST-запрос `handlers/{handler_id}/events`.  

[#apiVerifiers](https://docs.visionlabs.ai/luna/v.5.28.0/ReferenceManuals/APIReferenceManual.html#operation/postVerifier)
POST-запрос на `handlers/{verifier_id}/verifications`.  

[#apiLiveness](https://docs.visionlabs.ai/luna/v.5.28.0/ReferenceManuals/APIReferenceManual.html#tag/liveness)
POST-запрос на `liveness`.  

Каждый из методов принимает в качестве аргумента коблек типа `Consumer<Result<T>>` для получения результата вызова.  
Когда колбек больше не нужен (например, при разрушении вью-модели), его нужно удалить вызовом `ApiHuman#removeCallback()`.  


`ApiHuman` упрощает доступ к ендпоинтам `Platform API`. Под собой он использует интерфейс Retrofit-а.  
Если хочется большего контроля над запросами, интерфейс Retrofit-а доступен через `ApiHuman#retrofit`.  


#### Как пользоваться камерой для получения бестшота
Чтобы получить бестшот, нужно открыть камеру вызовом `LunaID.showCamera(listener: CameraUIDelegate)`.  
У `CameraUIDelegate` есть два основных коблека:  
`bestShot(bestShot: BestShot)` - срабатывает, когда найден бестшот (экран камеры после этого закрывается).  
`canceled()` - срабатывает, если пользователь закрыл камеру (например, нажал Назад; экран камеры после этого закрывается).  

Посколько камера для получения бестшота открывается в новой Activity, необходимо особенно обратить внимание на жизненный цикл ваших компонентов. Например, ваша вызывающая Activity может быть убита, презентер или вью-модель пересозданы в то время, когда осуществляется поиск бестшота.  
Для этого случая предусмотрены два механизма:
* первый это метод `LunaID.unregisterListener(listener: CameraUIDelegate)`, который нужно вызвать, чтобы избежать утечек и срабатывания коблека `CameraUIDelegate` внутри неактуального контекста (разруженная вью-модель, например).  
* если `LunaID.unregisterListener(listener: CameraUIDelegate)` был вызван до того, как пользователь завершил работу с камерой (получил бестшот или закрыл камеру), на помощь прийдет второй механизм. Методы `LunaID.getLastCameraState()` и `LunaID.popLastCameraState()` позволяют получить последнее состояние камеры с бестшотом, если он был сделан. Каждый вызов `getLastCameraState()` возвращает одно и то же значение, у `popLastCameraState()` только первый вызов вернет значение и очистит его.  С помощью этого механизма можно получить сделанный бестшот, если пользователь вернулся на экран, который был пересоздан.

Для примера использования этих механизмов см. `examples/CameraExample`.  


##### как использовать проверку на лайфнесс во время получения бестшота
Во время получения бестшота возможно автоматически осуществлять liveness-проверку полученных кадров средствами `Platform Web API`. Чтобы включить эту возможность, нужно определенным образом инициализировать `LunaID`:

```kotlin
        val apiConfig = ApiHumanConfig("http://luna-platform.com/api/6/")
        LunaID.init(
            ...
            apiHumanConfig = apiConfig,
            onlineLivenessSettings = LivenessSettingsAlwaysEnabled
        )
```

Чтобы работала упомянутая выше проверка online-liveness, при инициализации необходимо указать две вещи:

1) `apiHumanConfig` с адресом, на котором празвернуто `Platform API`. Туда будут отправляться запросы для проверки liveness кадров. Без него невозможно будет делать проверку online-liveness.
2) `onlineLivenessSettings`, который определяет, будет ли экран камеры делать проверку online-liveness.  Аргументом принимается имплементация класса `LivenessSettings`. Можно его реализовать, если нужна динамическое переключение проверки лайфнеса во время работы приложения. Если настройку менять не планируется, то в `LunaID SDK` уже включены две дефолтные имлементации этого интерфейса, которыми можно воспользоваться: `LivenessSettingsAlwaysEnabled` и `LivenessSettingsAlwaysDisabled`.


