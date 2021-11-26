# 起因

1. 每创建一个Project，就要配置一堆gradle的东西，需要有个模板配置
2. 每个gradle文件可以有单一职责，不希望一个gradle文件中有几百行，不能快速查找或定位需要位置，如资源混淆白名单混在build.gradle中
3. 不同module之间，各依赖的版本号混乱，希望有个统一管理
4. 项目模块化后，可减少每个模块中gradle的模板代码
5. 后期可快速转为kts构建

# 优化思路

1. 使用国内仓库阿里云，加快编译速度。
2. 新建`build-script.gradle`统一配置plugin和project依赖的版本号
3. 利用gradle的apply from 'xxx'的所谓`继承`的特性，封装一些模板代码：通过`app`继承`moudle`，`module`继承`lib`的方式，减少模板代码
4. 配置常用gradleTask，如依赖版本检查等

# 模块化module相关的定义

1. `app`为壳工程
2. `module-xxx`为业务模块，含UI（订单模块、用户登录模块）
3. `lib-xxx`为基础模块，无UI（网络库、垃圾清理、JSBridge）

# 使用方式

1. 将gradle-scrpt文件夹copy到project根目录下

2. 修改project的build.gradle文件

   ```
   // Top-level build file where you can add configuration options common to all sub-projects/modules.
   buildscript {
       // ① 导入配置文件
       apply from: "./gradle-script/build-script.gradle"
       // ② 替换repo
       repositories config.repositories
       // ③ 替换Gradle插件依赖
       dependencies config.pluginDeps
   }
   
   // ④ 代码仓库依赖
   allprojects {
       repositories config.repositories
   }
   
   // ⑤Gradle Task
   // 引入版本check
   apply from: './gradle-script/function/check-deps-version.gradle'
   
   task clean(type: Delete) {
       delete rootProject.buildDir
   }
   
   // 用于jenkins打包读取，PackageTool脚本会读取该信息
   ext {
       APPLICATION_ID = "app.phoenixsky.xxx"
       APP_VERSION_CODE = 1
       APP_VERSION_NAME = '1.0.0'
   }
   ```

3. 移除setting.gradle里的`dependencyResolutionManagement`相关代码

   ```
   // 移除
   dependencyResolutionManagement {
     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
     repositories {
       google()
       mavenCentral()
       jcenter() // Warning: this repository is going to shut down soon
     }
   }
   ```



4. App模块可直接将build修改为以下内容

   ```
   apply from: '../gradle-script/build-app.gradle'
   ```

   仅一句话app的build.gradle的配置就已经完毕，如需要添加定制各种业务逻辑和不同使用场景，可直接在当前的build.gradle文件继续添加。

   如下：

    1. 可继续添加`apply plugin`
    2. 如添加`android`部分，同名覆盖，非同名新增

   ```
   apply from: '../gradle-script/base-app.gradle'
   // if you need but it's deprecated，viewbinding better
   apply plugin: 'kotlin-android-extensions'
   
   android {
   
       buildTypes{
           // debug版本的的混淆版本
           alpha {
               // 基于debug
               initWith debug
               // 用buildType的debug来兜底
               matchingFallbacks = ['debug']
   
               minifyEnabled true
               shrinkResources true
               zipAlignEnabled true
               proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
           }
       }
   }
   
   
   dependencies {
       androidTestImplementation 'xxx'
   		implementation 'xxx'
   }
   ```



5. module(业务含UI)模块

   ```
   apply plugin: 'com.android.library'
   
   // 基础module
   apply from: '../gradle-script/build-module.gradle'
   
   android {
   		xxx
   }
   
   dependencies {
       androidTestImplementation 'xxx'
   		implementation 'xxx'
   }
   ```



6. lib(功能无UI)模块可直接将build修改为如下内容

   ```
   // android library
   apply plugin: 'com.android.library'
   // 基础lib
   apply from: '../gradle-script/build-lib.gradle'
   
   android {
   		xxx
   }
   
   dependencies {
       androidTestImplementation 'xxx'
   		implementation 'xxx'
   }
   ```

   