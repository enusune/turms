# 客户端接口

Turms客户端目前支持JavaScript、Kotlin、Swift与Dart这四种语言，对外暴露一致的接口，并且表现为一致的行为。各语言版本之间的部分接口参数可能出现不完全一致的情况，这主要体现在：1. 接口采用更贴近当前语言特性及习惯的参数与语法；2. turms-client-js独有的参数与接口。

由于Turms各语言客户端行为具有高度的一致性，因此如果您基于上述任意一种语言进行业务开发，您可以在代码逻辑不做改变的情况下，轻松将已写好的业务代码翻译为另外两种语言（具体可参考在本文结尾处的示例）。

（之后还会支持C#与C++，其他语言暂不在考虑范围内）

## Quick Start

1. 克隆Turms仓库（目前客户端代码均未发布到第三方依赖仓库中）。参考命令：git clone --depth 1 https://github.com/turms-im/turms.git

2. 在您的项目中，引入对应的客户端文件。具体操作如下：

   * 对于使用turms-client-js的项目：

     首先进入到`turms-client-js`子项目的目录，执行命令`npm run quickbuild`，该命令会安装相关依赖并编译turms-client-js的发布包。然后：

     * 对于使用模块的项目：
       * 安装：在`package.json`的`dependencies`下添加：`"turms-client-js": "file:<YOUR_OWN_PATH>/turms-client-js"`即可
       * 使用：通过`import TurmsClient from 'turms-client-js'`引入Turms客户端实现
     * 对于不使用模块的项目：在HTML上添加：`<script type="text/javascript" src="<YOUR_OWN_PATH>/turms-client-js/dist/turms-client.iife.js"></script>`，并直接使用全局对象`TurmsClient`。

   * 对于使用turms-client-kotlin的项目：

     * 安装：在Turms项目的根目录下（而不是turms-client-kotlin子项目的目录），执行`mvn clean install -pl turms-client-kotlin -am`，该命令会将turms-client-kotlin编译并安装其Jar包到本地Maven仓库。

     * 使用：

       * 对于Maven项目，添加：

         ```xml
         <dependency>
             <groupId>im.turms</groupId>
             <artifactId>turms-client-kotlin</artifactId>
             <version>0.10.0-SNAPSHOT</version>
         </dependency>
         ```

       * 对于Gradle项目，添加：

         ```groovy
         repositories {
             mavenLocal()
         }
         dependencies {
             implementation 'im.turms:turms-client-kotlin:0.10.0-SNAPSHOT'
         }
         ```

   * 对于使用turms-client-swift的项目：在Xcode中，通过`General`标签页下的`Frameworks, Libraries, and Embedded Content`指定本地turms-client-swift文件夹路径并添加即可。

   * 对于使用turms-client-dart的项目：在您项目的`pubspec.yaml`里添加下述依赖即可：

     ```yaml
     dependencies:
       turms_client_dart:
         path: <YOUR_OWN_DIR>/turms_client_dart
     ```

3. 编写业务逻辑

## 版本要求

Turms客户端对版本的最低要求，主要是根据：平台全球市场占有率、平台TLSv1.2最低支持版本与代码实现的优雅程度，三个因素来考量。另外，Turms不提供对TLSv1与TLSv1.1等被时代淘汰协议的官方支持。

| 平台    | 支持的最低版本                                               | 原因                                                         |      |
| ------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ---- |
| Android | 21+                                                          | 考虑到21+的市场占有率与代码实现优雅程度，故支持21+           |      |
| iOS     | 12.0+                                                        | 考虑到[iOS 12.0+在全球的市场占有率](https://developer.apple.com/support/app-store/)以及苹果产品用户的习惯，turms-client-swift采用NWConnection实现TCP协议，因此设备版本的要求等同于支持`NWConnection`设备的版本要求。<br />另外，turms-client-swift不会考虑用古老的`CFStreamCreatePairWithSocketToHost`来实现TCP协议。 |      |
| 浏览器  | [支持WebSocket协议的浏览器](https://caniuse.com/?search=websocket) | 对于IE系列浏览器，turms-client-js仅对IE 11提供官方支持。<br />另外，turms-client-js不会将WebSocket降级为轮询机制 |      |
| 桌面端  | turms-client-kotlin(JDK8+)<br />turms-client-js(Node.js 8+)  | 如果您采用turms-client-kotlin实现，则要求JDK版本为8(+)，因为JDK 8+默认提供对TLSv1.2的支持。<br />如果您采用turms-client-js实现，则Turms提供对Node.js 8+的官方支持 |      |

补充

* 为实现快速迭代，turms-client-swift仍暂时使用WebSocket协议，但未来替换为纯TCP协议实现。此变动不会影响上述的版本要求
* turms-client-kotlin采用的是`Socket`，而非`SocketChannel`。其中最主要的原因是：Android SDK不对`SocketChannel`提供一套标准的TLS协议实现，需要自行实现。考虑到安卓系统的五花八门且系统功能本身就比较受限（尤其相比服务端实现），自行实现TLS协议极易导致各种意料之外的Bugs，故使用`Socket`以采用官方的TLS协议实现。

## 客户端的对外逻辑结构

- TurmsClient：Turms客户端唯一直接对外暴露的类，一个TurmsClient实例代表着一个客户端与服务端之间的会话连接。以下变量是TurmsClient对外的成员变量。
  - driver：TurmsClient的运行驱动。负责连接的开起关闭、底层数据的发送接收与心跳控制等基础性操作。以下介绍到的Service层类都基于driver运作。
  
  - userService：用户相关服务。负责如用户登陆、添加好友、添加关系人分组、发送/处理好友请求、查询附近的用户等操作。
  
  - groupService：群组相关服务。负责如创建群组、变更群主、修改群成员角色、修改群信息等操作。
  
  - messageService：消息相关服务。负责如发送消息、修改已发送消息、查询各类消息与其状态、撤回消息等操作。
  
  - notificationService：通知相关服务。负责接受与响应业务层面上的通知（即：其他用户向该用户发送好友请求、群组成员上下线等通知）。
    提醒：消息（message）不算做业务层面上的“通知”（notification），因此notificationService不会处理用户消息，用户消息仅由messageService进行处理。而driver中TurmsNotification的“通知”概念指的是网络层面上的Turms服务端给Turms客户端的通知，因此notificationService也不会处理底层的TurmsNotification数据。
    
    补充：关于通知功能的开启与关闭，您可以在turms服务端`im.turms.server.common.property.env.service.business.NotificationProperties`处，实时地进行修改。
    
  - storageService：存储相关服务（可选拓展）。负责用户头像、群组头像与消息附件的上传与下载操作。补充：该服务为turms的拓展服务，因此若您希望使用该功能，您需要将turms-plugin-minio或您自行实现的存储插件集成到turms服务端当中。

### Service类的返回值

与Turms服务端交互的所有Turms客户端接口都**基于异步模型编写**。turms-client-js使用Promise模型，turms-client-kotlin使用Coroutines模型，而turms-client-swift使用Promise模型（由PromiseKit提供）。

各种Service类可以对Turms所提供的业务模型进行增删改查操作。您需要了解其返回值种类，以开发您自己的业务代码。

#### 对于状态码为10xx的响应（拓展知识）

* 对于增加业务模型的函数，如果该函数的返回值被声明为一个异步模型（如：Promise\<string>），则返回的泛型（如前文的string类型）的值必定不为空，否则会抛出一个状态码为MISSING_DATA的错误（TurmsBusinessError），表明本应该存在的数据点丢失。若出现该错误，则意味着Turms的服务端或客户端自身存在行为不一致的Bug。
* 对于删除与更新业务模型的函数，它们均返回被异步模型包裹的Void类型（如：Promise\<Void>）。
* 对于查找业务模型的函数，如果该类函数返回被异步模型包裹的List类型，则当服务端返回空数据时，该查找操作函数会返回一个空List，而非null或undefined。如果被包裹的类型不是List类型，则当服务端返回空数据时，该查找操作函数会返回一个undefined（JavaScript）或null（Kotlin）或nil（Swift）。特例：answerGroupQuestions方法可以算做查询方法，但其返回数据永不为空。

#### 对于状态非10xx的响应（拓展知识）

对于Service类而言，这类响应均被认作是“错误”状态响应。通过异步模型抛出TurmsBusinessException，并在该错误模型中包括了具体状态码与错误原因。

### 主要接口差异（拓展知识）

通常情况下，您并不需要关心各客户端接口之间的差异，但如果您的团队需要由一名开发者基于多个Turms客户端进行上层的开发工作，或者您需要对照您项目的上层客户端代码实现的异同，您可以了解一下客户端间主要接口的不同。

在早期Turms客户端实现中，各客户端之间的接口参数与数据模型是尽量保持统一的参数配置与含义，如时间表达相关的参数。但这种强行统一的写法不符合目标语言习惯。同时考虑到在大部分情况下，各客户端的上层业务代码通常有专人负责，而非全由一名开发者负责，统一含义意义不大，并且这些差异也符合目标语言习惯，故不进行强制统一。

客户端主要接口的差异如下表：

|              | JavaScript客户端                  | Kotlin客户端                       | Swift客户端              | Dart客户端  | 实例            |
| ------------ | --------------------------------- | -------------------------------------- | ------------------------ | ------------------------ | ------------------------ |
| 时间表达单位 | 一律为毫秒                        | 一律为毫秒 | 采用TimeInterval（即秒） | 一律为毫秒 | connectTimeout |
| 业务异常模型 | TurmsBusinessError（继承自Error） | TurmsBusinessException（继承自不携带栈信息的RuntimeException） | TurmsBusinessError（继承自Error） | TurmsBusinessException（继承自Exception） |    |
| 异步模型     | Promise模型                       | Coroutines模型 | 由PromiseKit提供的Promise模型 | Future模型 |                          |

补充：对于对外暴露的回调函数实现，Turms的Swift客户端没有采用Swift常见的delegate代理模式，而是和其他语言客户端一样通过函数传递逃逸闭包。

## 会话的生命周期

Turms客户端的会话生命周期比较容易理解，具体而言：先通过`driver.connect()`进行网络层的连接，而后通过`userService.login()`进行业务层面上的登录操作，在登录成功后，对应的会话就建立了。最后再通过`userService.logout()`方法向服务端发送会话关闭通知，同时也会关闭网络层连接。

为了保持逻辑简单，也方便上层开发者自行组合各种逻辑。Turms不提供诸如自动重连、自动路由跳转等操作，一方面开发者可以很容易地实现该类逻辑，另一方面，这类“隐藏”的内部逻辑会使得上层开发者难以把控底层驱动行为，在一些时候反而会成为绊脚石。

拓展：如同WebSocket基于关闭帧的会话关闭机制，Turms服务端在关闭会话时，也会通过一个会话关闭信令来通知客户端该会话已关闭，并在信令被Flushed后，通知底层WebSocket/TCP关闭连接。Turms服务端不需要等待客户端对会话关闭信令的任何响应，客户端也不会向服务端发送有关会话关闭信令的响应。

### 生命周期回调钩子

| 层次       | 名称                             | 调用时机              | 提醒                                                         |
| ---------- | -------------------------------- | --------------------- | ------------------------------------------------------------ |
| 网络层     | driver.addOnConnectedListener    | 当网络层连接建立时    | 通常您并不需要通过`addOnConnectedListener`来添加连接监听事件，<br />而是将您的回调函数赋给`driver.connect()`返回的异步成功回调onSucccess/then |
| 网络层     | driver.addOnDisconnectedListener | 当网络层连接断开时    |                                                              |
| 业务逻辑层 | userService.addOnOnlineListener  | 当会话建立/用户上线时 | 通常您并不需要通过`addOnOnlineListener`来添加上线监听事件，<br />而是将您的回调函数赋给`userService.login()`返回的异步成功回调onSucccess/then |
| 业务逻辑层 | userService.addOnOfflineListener | 当会话断开/用户下线时 |                                                              |

## 业务逻辑的认证与授权（拓展知识）

对于客户端发来的权限信息，Turms服务端的态度是“客户端传来的权限信息均不可信”，因此Turms服务端会根据您在Turms服务端处所设定的业务配置，自行做各种必要的权限判断。

以“修改已发送消息”功能为例，该行为会触发一系列判定逻辑。Turms会先判断目标消息是否确实是由该用户发出的，再根据您在Turms服务端配置的allowEditingMessageBySender（默认为true），来判断是否允许用户修改已发送消息，若您设置其为false，则在客户端处会捕获到一个TurmsBusinessException（Kotlin）或TurmsBusinessError（JavaScript/Swift）对象，而它由业务状态码模型TurmsStatusCode表示（由code与reason描述信息组成）。

再比如对于一个“简单”的“发送消息”请求，Turms服务端就会判断该消息发送用户是否处于激活状态、是否设置了“允许发送消息给陌生人（非关系人）”、消息发送者是否在黑名单中。如果接收方是群组，那么消息发送者是否是群成员，并且是否处于禁言状态等等逻辑判断。而您仅仅只需调用一个sendMessage接口即可。

## 与服务端通信时使用的数据格式（拓展知识）

对于一般请求与响应而言：

* 基于纯TCP协议实现的客户端：varint编码的正文长度 + 正文（Protobuf编码的`TurmsNotification`或`TurmsRequest`）
* 基于WebSocket协议实现的客户端：正文（Protobuf编码的`TurmsNotification`或`TurmsRequest`）。正文的字节长度信息通过底层的WebSocket Frame传输

对于心跳请求而言：

* 基于纯TCP协议实现的客户端：一个长度为一字节的`[0]`字节数组。这里的数值`0`其实是指“该Payload的长度在varint编码下为一字节长度的0”，即Payload为0字节。
* 基于WebSocket协议实现的客户端：一个正文为空（0字节）的Binary类型消息

补充：Turms不通过WebSocket的PING/PONG来实现心跳的原因是：

* 各浏览器WebSocket实现的PING消息发送时间间隔不同
* 上层代码无法控制PING/PONG的行为，甚至无法感知行为的发生
* 网络层面的心跳逻辑不应该和应用层的心跳耦合

## 接口的理解方法（重点）

Turms所有客户端的接口都非常容易理解与使用。开发者甚至不需要看Turms客户端有什么接口，只需要凭借基本的IM业务知识就能反推Turms会有什么接口。

一般开发者只需要记住：

* 通过`new TurmsClient(...)`创建Turms客户端实例
* 在上文`客户端的对外逻辑结构`提到的：Turms客户端分为五个服务：userService（用户相关服务）、groupService（群组相关服务）、messageService（消息相关服务）、notificationService（通知相关服务）、storageService（存储相关服务、可选拓展）。

之后我们就能凭借业务知识反推Turms客户端会有什么接口了，比如：

* 用户首先要能登陆，于是先想到其对应的服务`userService`用户相关服务。既然是`登陆`所以找找有没有`login`接口，于是自然地就找到了`client.userService.login(...)`接口。
* 登陆后，用户需要能够发消息，那就先想到`messageService`消息相关服务，再看看有没有类似`sendMessage`的接口，于是找到了接口`client.messageService.sendMessage(...)`接口。
* 既然能发消息，那有什么接口能监听收到的消息呢？既然跟消息有关，那依旧想到的是`messageService`，于是想到接口可能是`onMessage`、`subscribeMessage`或`addMessageListener`，代码里找一找，找到了`client.messageService.addMessageListener(...)`。
* 既然能监听收到的消息，那怎么监听接收到的通知呢？既然跟通知有关，那想到的就是`notificationService`通知相关类服务，并且既然监听收到的消息的接口叫`addMessageListener`，那监听通知的接口就应该是`addNotificationListener`了，于是找到了`client.notification.addNotificationListener`。

综上，一般开发者只需凭借基本的业务知识就能反推Turms客户端提供的接口，甚至不需要读Turms客户端的源码。

而对于高级开发者，Turms客户端也开放了`driver`对象，让开发者自行实现一些相对底层的操作。另外，如在`会话的生命周期`提到的，Turms客户端是故意设计的清晰易懂，故意不提供诸如自动重连、自动路由跳转等操作，因为一方面开发者可以很容易地自行实现该类逻辑，另一方面，这类“隐藏”的内部逻辑会使得上层开发者难以把控底层驱动行为，在一些时候反而会成为绊脚石。

## 具体示例

以下示例包括turms-client-js/kotlin/swift三个版本，并且其作用等价。具体包括了以下业务操作：初始化客户端、登录、监听会话连接断开（下线）、监听通知、监听新消息、查询附近的用户、发送消息、创建群组操作。

### 体验示例前的服务端准备工作

* 方案一：无需在本地搭建Turms服务端，用户直接在本地通过客户端API连接Playground上的turms-gateway服务端（WebSocket端口：http://playground.turms.im:10510；TCP端口：http://playground.turms.im:11510）。但注意及时将本地客户端升级到最新版本，以避免出现因为服务端侧的接口更新，导致数据不一致的问题。
* 方案二：在application.yaml配置文件中更新以下配置：
   1. 将`turms.gateway.session.enable-authentication`设置为false（取消用户登录认证）
   2. 将`turms.service.message.allow-sending-messages-to-stranger`也设置为true（允许没有用户关系的用户互相发送消息）
* 方案三：使用自带`dev`profile配置。因为Turms提供的`dev`profile已做了上述配置。默认情况下，Turms发布包中的application.yaml的profile字段为空，即默认的profile不是`dev`，需要您手动配置为`dev`。

### turms-client-js版本

```javascript
// Initialize client
const client = new TurmsClient(); // new TurmsClient('ws://any-turms-gateway-server.com');

// Listen to the offline event
client.userService.addOnOfflineListener(info => {
    console.info(`onOffline: ${info.closeStatus}:${info.businessStatus}:${info.reason}`);
});

// Listen to inbound notifications
client.notificationService.addNotificationListener(notification => {
    console.info(`onNotification: Receive a notification from other users or server: ${JSON.stringify(notification)}`);
});

// Listen to inbound messages
client.messageService.addMessageListener(message => {
    console.info(`onMessage: Receive a message from other users or server: ${JSON.stringify(message)}`);
});

client.userService.login('1', '123')
    .then(() => {
        client.userService.queryNearbyUsers(
            35.792657,
            139.667651,
            100,
            10)
            .then(users => {
                console.log(`nearby users: ${JSON.stringify(users)}`);
            });
        client.messageService.sendMessage(
            false,
            '1',
            new Date(),
            'Hello Turms',
            null,
            30)
            .then(id => {
                console.log(`message ${id} has been sent`);
            });
        client.groupService.createGroup(
            'Turms Developers Group',
            'This is a group for the developers who are interested in Turms',
            'nope')
            .then(id => {
                console.log(`group ${id} has been created`);
            });
    })
    .catch(reason => {
        console.error(reason);
    });
```

### turms-client-kotlin版本

```kotlin
// Initialize client
val client = TurmsClient() // TurmsClient("127.0.0.1", 11510)

// Listen to the offline event
client.userService.addOnOfflineListener { info ->
    println("onOffline: ${info.closeStatus}:${info.businessStatus}:${info.reason}")
}

// Listen to inbound notifications
client.notificationService.addNotificationListener { notification ->
    println("onNotification: Receive a notification from other users or server: $notification")
}

// Listen to inbound messages
client.messageService.addMessageListener { message, _ ->
    println("onMessage: Receive a message from other users or server: $message")
}

client.userService.login(1, "123")

val users = client.userService.queryNearbyUsers(
    35.792657f,
    139.667651f,
    100,
    10
)
println("nearby users: [${users.joinToString(", ")}]")

val msgId = client.messageService.sendMessage(
    false,
    1,
    Date(),
    "Hello Turms",
    null,
    30
)
println("message $msgId has been sent")

val groupId = client.groupService.createGroup(
    "Turms Developers Group",
    "This is a group for the developers who are interested in Turms",
    "nope"
)
println("group $groupId has been created")
```

### turms-client-swift版本

```swift

// Initialize client
let client = TurmsClient() // TurmsClient("ws://any-turms-gateway-server.com")

// Listen to the offline event
client.userService.addOfflineListener { (info: SessionCloseInfo) -> () in
    print("onSessionDisconnected: \(info.closeStatus):\(info.businessStatus ?? ""):\(info.reason ?? "")")
}

// Listen to inbound notifications
client.notificationService.addNotificationListener { (notification: TurmsRequest) -> () in
    print("onNotification: Receive a notification from other users or server: \(try! notification.jsonString())")
}

// Listen to inbound messages
client.messageService.addMessageListener{ (message: Message, _: MessageAddition) -> () in
    print("onMessage: Receive a message from other users or server: \(try! message.jsonString())")
}

client.userService.login(userId: 1, password: "123")
    .done {
        client.userService.queryNearbyUsers(
                latitude: 35.792657,
                longitude: 139.667651,
                distance: 100,
                maxNumber: 10)
            .done {
                print("nearby users: \($0)")
            }
        client.messageService.sendMessage(
                isGroupMessage: false,
                toId: 1,
                deliveryDate: Date(),
                text: "Hello Turms",
                records: nil,
                burnAfter: 30)
            .done {
                print("message \($0) has been sent")
            }
        client.groupService.createGroup(
                name: "Turms Developers Group",
                intro: "This is a group for the developers who are interested in Turms",
                announcement: "nope")
            .done {
                print("group \($0) has been created")
            }
    }.catch {
        print($0)
    }
```

### turms-client-dart版本

```dart
// Initialize client
final client = TurmsClient();

// Listen to the offline event
client.userService.addOnOfflineListener((info) => 
    print('onOffline: ${info.closeStatus}:${info.businessStatus}:${info.reason}'));

// Listen to inbound notifications
client.notificationService.addNotificationListener((notification) => 
    print('onNotification: Receive a notification from other users or server: $notification'));

// Listen to inbound messages
client.messageService.addMessageListener((message, _) => 
    print('onMessage: Receive a message from other users or server: $message'));

await client.userService.login(Int64(1), password: '123');

final users = await client.userService
    .queryNearbyUsers(35.792657, 139.667651, distance: 100, maxNumber: 10);
print('nearby users: $users');

final msgId = await client.messageService
    .sendMessage(false, Int64(1), text: 'Hello Turms', burnAfter: 30);
print('message $msgId has been sent');

final groupId = await client.groupService.createGroup(
    'Turms Developers Group',
    announcement: 'This is a group for the developers who are interested in Turms',
    intro: 'nope');
print('group $groupId has been created');
```

## turms-client-js共享上下文

### 背景

由于Turms服务端不支持也没计划支持：一个用户在同一个端同时建立多个会话。因此如果一个用户在浏览器打开多个标签页，并试图以**相同的用户ID与设备类型**进行登陆时，那么有且仅有一个会话可以建立成功。从浏览器角度来看，就是有且仅有一个标签页能够登陆成功。该场景适用于一般的社交应用。

### 适用场景

但部分IM场景需要支持：从用户角度来看，用户只需在一个页面登陆一次，那么其他标签页也就处于登陆状态了，在所有标签页里的Turms客户端都能以相同的用户身份，发送请求、接收消息与通知。适用于客服系统等场景。

为了支持上述场景，需要使用`共享上下文`。具体而言，对于在不同标签页的同域（同协议；同域名；同端口）、同用户ID且同设备类型的Turms客户端，它们可以共享与Turms服务端的WebSocket连接与一些已登陆用户的信息。

提醒：因为只有同域名、同用户ID且同设备类型的Turms客户端才共享上下文，因此您的客户端可以以不同的用户身份登陆不同的标签页，以支持类似“部分标签页共享A用户的会话，部分标签页共享B用户的会话”的特性。

### 开启方法

turms-client-js默认不开启共享上下文功能，而如果您的产品需要使用该功能，可以通过在创建`TurmsClient`实例时，传递一个`useSharedContext: true`开启。具体代码如下：

```javascript
var client = new TurmsClient({
    useSharedContext: true
});
```

如果`useSharedContext`为`true`，但用户的浏览器并不支持`Shared Web Workers`，则`new TurmsClient()`会直接抛异常。

如果您想要提前知道当前浏览器是否支持共享上下文，您可以调用：`TurmsClient.isSharedContextSupported()`，该函数返回`boolean`，`true`即支持，`false`即不支持。

注意事项：

* 如果开启`共享上下文`功能，那么您将不再能自行直接调用`client.driver.connectionService#connect`函数（通常情况下，用户也不需要直接调用该底层函数）。除此之外，您无需为适配共享上下文功能修改其他逻辑代码。

### 支持版本

由于turms-client-js采用`Shared Web Workers`实现共享上下文，因此版本要求同[Shared Web Workers](https://caniuse.com/sharedworkers)。

### 具体代码示例

```javascript
// On the first tab of the same origin
// The client will create a new WebSocket connection
var client = new TurmsClient({
    useSharedContext: true
});
client.userService.login(1, "123", DeviceType.BROWSER);

// On the second tab of the same origin
// The client will share the WebSocket connection with the first tab
var client = new TurmsClient({
    useSharedContext: true
});
client.userService.login(1, "123", DeviceType.BROWSER);

// On the third tab of the same origin
// The client will create a new WebSocket connection because it uses a new device type
var client = new TurmsClient({
    useSharedContext: true
});
client.userService.login(1, "123", DeviceType.ANDROID);
```

## 度量数据

参考文档：[可观测性体系](https://turms-im.github.io/docs/for-developers/observability.html#%E5%BA%A6%E9%87%8F)

### 网络连接度量

Turms各客户端会提供网络连接相关的度量数据（turms-client-swift的TCP度量数据会在近期提供）。开发者可以通过`turmsClient.driver.connectionMetrics`获取该度量数据对象。该对象包含以下数据：

| 数据点名称          | 单位 | 含义                                                         |
| ------------------- | ---- | ------------------------------------------------------------ |
| addressResolverTime | 毫秒 | 域名解析用时。turms-client-js不提供该数据                    |
| connectTime         | 毫秒 | 对于非turms-client-js客户端，该数据指TCP握手用时；<br />对于turms-client-js客户端，该数据指域名解析、TCP握手、TLS握手、建立WebSocket连接的总用时 |
| tlsHandshakeTime    | 毫秒 | TLS握手用时。turms-client-js不提供该数据                     |
| dataReceived        | 字节 | 对于非turms-client-js客户端，该数据指TCP接收到的数据字节数；<br />对于turms-client-js客户端，该数据指WebSocket连接接收到的Binary帧Payload数据字节数 |
| dataSent            | 字节 | 对于非turms-client-js客户端，该数据指TCP已发送的数据字节数；<br />对于turms-client-js客户端，该数据指WebSocket连接已发送的Binary帧Payload数据字节数 |

### 业务请求度量

TODO
