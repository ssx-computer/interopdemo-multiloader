# Interop Demo — NeoForge / Fabric 双加载器互通模组（Minecraft 1.21.1）

一份代码，两个加载器：`common` 子项目承载全部游戏内容与逻辑，`fabric` / `neoforge` 只提供各自加载器的入口与注册桥接。
构建产物是两个 jar，注册 ID、配方、语言文件、贴图、mixin 完全一致——同一个存档可以从 Fabric 服务端搬到 NeoForge 服务端（反之亦然），物品/方块不会失配。

底层架构来自 [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template)（Jared，CC0），并按 1.21.1 做了实现填充。

---

## 版本矩阵

| 组件 | 版本 |
|---|---|
| Minecraft | 1.21.1 |
| Java | 21 |
| NeoForge | 21.1.80（FML 4.0.31） |
| Fabric Loader | 0.16.9 |
| Fabric API | 0.116.17+1.21.1 |
| Fabric Loom | 1.8-SNAPSHOT |
| ModDevGradle | 2.0.49-beta（NeoForm 1.21.1-20240808.144430） |
| Parchment | 2024.11.10（1.21 参数名，兼容 1.21.1） |
| Gradle Wrapper | 8.10 |

映射：1.20.5 起 Minecraft 运行时使用 Mojang 官方映射，NeoForge 与 Fabric（`officialMojangMappings()`）都对齐 mojmap，
所以 `common` 里的代码**无需任何重映射**即可同时供两个加载器使用，这是本模板能"一套代码"的根本原因。

---

## 目录结构

```
mod/
├─ common/                       平台无关代码（唯一需要日常改动的模块）
│  ├─ java/com/example/interopdemo/
│  │  ├─ Constants.java          MOD_ID / MOD_NAME / LOG
│  │  ├─ CommonClass.java        双端公用启动入口 + PLATFORM service
│  │  ├─ registry/ModItems.java  示例物品 + 自定义创造标签页
│  │  ├─ registry/ModBlocks.java 示例方块（发光）+ BlockItem
│  │  ├─ platform/Services.java  ServiceLoader 装载
│  │  ├─ platform/services/IPlatformHelper.java   注册抽象接口
│  │  └─ mixin/MixinMinecraft.java 公共 mixin（客户端）
│  └─ resources/
│     ├─ assets/interopdemo/…    模型/方块状态/贴图/中英文语言文件
│     ├─ data/interopdemo/recipes/example_item.json  配方
│     ├─ interopdemo.mixins.json 公共 mixin 配置
│     └─ pack.mcmeta             资源 34 / 数据 48
├─ fabric/                       Fabric 入口（ModInitializer）与平台实现
│  ├─ java/…/fabric/             ExampleMod, FabricPlatformHelper, mixin/MixinTitleScreen
│  └─ resources/                 fabric.mod.json, interopdemo.fabric.mixins.json, META-INF/services/…
├─ neoforge/                     NeoForge 入口（@Mod + DeferredRegister）与平台实现
│  ├─ java/…/neoforge/           ExampleMod, NeoForgePlatformHelper, mixin/MixinTitleScreen
│  └─ resources/                 META-INF/neoforge.mods.toml, interopdemo.neoforge.mixins.json, META-INF/services/…
└─ buildSrc/                     约定插件：源集合并、元数据展开、能力声明
```

## 互通是怎么实现的（三个关键机制）

1. **ServiceLoader 抽象层**
   `common` 只依赖 `IPlatformHelper` 接口；两个加载器项目各自实现它，并在
   `src/main/resources/META-INF/services/com.example.interopdemo.platform.services.IPlatformHelper`
   中登记实现类全名（`FabricPlatformHelper` / `NeoForgePlatformHelper`）。运行期由启动的加载器决定拿到哪一个。

2. **注册时序差异被桥接掉**
   - Fabric：`onInitialize` 里直接 `Registry.register(BuiltInRegistries.X, id, obj)`（即时注册）。
   - NeoForge：注册必须走 mod 总线，`NeoForgePlatformHelper` 内部维护 `DeferredRegister.Items/Blocks` 与 `DeferredRegister<CreativeModeTab>`，
     `@Mod` 构造器先 `CommonClass.init()`（把条目登记到 DeferredRegister），再 `bootstrap(modEventBus)` 把它们 attach 到总线。
   公共代码看到的都是 `Supplier<...>`，因此两侧写法完全一致，注册出来的 `ResourceLocation` 也一致。

3. **源集合并，而不是 jar-in-jar**
   `buildSrc` 的约定插件把 `common` 的 `src/main/java` 与 `src/main/resources` 直接编进两个加载器的 jar，
   因此两边 jar 都是自包含的，不需要 Shadow/JarJar，也不需要额外 runtime 依赖。
   Fabric 侧由 Loom 生成 `interopdemo.refmap.json`；NeoForge 侧 mixin 通过 mods.toml 的 `[[mixins]]` 注册，公共/专属两份配置文件都列了出来。

---

## 构建

```bash
# JDK 21
./gradlew build
```

产物：

```
fabric/build/libs/interopdemo-fabric-1.21.1-1.0.0.jar        # 已 remap，可直接放进 mods/
neoforge/build/libs/interopdemo-neoforge-1.21.1-1.0.0.jar
common/build/libs/interopdemo-common-1.21.1-1.0.0.jar        # 仅用于 IDE/发布，运行不需要
```

**内存要求**：`common` 与 `neoforge` 通过 ModDevGradle/NFRT 走 NeoForm 管线，其中"反编译 Minecraft"这一步需要约 6–8 GB 的堆
（Vineflower 反编译 1.21.1 全量源码；`Blocks.<clinit>` 这类巨型静态初始化在 2.4 GB 以下会失败）。
建议给构建机 16 GB 内存。Fabric 侧只要 Loom 的重映射，1–2 GB 足够。

## 运行与调试

```bash
./gradlew :fabric:runClient      # Fabric 客户端
./gradlew :fabric:runServer
./gradlew :neoforge:runClient    # NeoForge 客户端
./gradlew :neoforge:runServer
./gradlew :neoforge:runData      # NeoForge 数据生成（模型/配方/语言）
```

IntelliJ IDEA 直接 Open 根目录 `build.gradle` 即可，两个模块的运行配置会自动生成。

## 受限网络

所有引用 NeoForge maven 的位置都读取 Gradle 属性 `neoforgeMaven`（见 `settings.gradle`、`buildSrc/src/main/groovy/multiloader-common.gradle`）。
需要走镜像时在 `~/.gradle/gradle.properties` 写：

```properties
neoforgeMaven=https://maven.neoforged.net/releases   # 默认值，可换成任何同结构的镜像
```

默认值即官方仓库，项目文件本身不需要改动。

---

## 已验证内容

- **Fabric 侧完整构建通过**：`./gradlew :fabric:build` → `BUILD SUCCESSFUL`，
  产物经 Loom 重映射到 intermediary（校验：`FabricPlatformHelper` 字节码中出现 `net/minecraft/class_1792` 等），
  `fabric.mod.json` 变量展开正确，`interopdemo.refmap.json` 已生成，`META-INF/services`、双语言 lang、模型/方块状态/配方/图标齐备。
- **NeoForge 侧代码类型校验通过**：用真实的 `neoforge-21.1.80-universal.jar` + FML 4.0.31 + `bus` + mojmap Minecraft 手工 `javac` 编译
  `common` + `neoforge` 全部源文件，无错误（本沙箱内存不足以执行 NeoForm 反编译，故未产出最终 jar；在满足内存与网络的机器上 `./gradlew build` 会一并产出）。

## 用 GitHub Actions 出包（推荐，本机内存/网络受限时尤其适用）

仓库已内置 `.github/workflows/build.yml`：在 GitHub 托管的 `ubuntu-latest`（16 GB 内存）上跑 `./gradlew build`，
一次产出 Fabric 与 NeoForge 两个 jar，并作为构建产物（Artifact）保留 30 天；推送 `v*` tag 时额外自动发布 GitHub Release。

推送方式（首次，PowerShell，需要装 [GitHub CLI](https://cli.github.com/) 并 `gh auth login`）：

```powershell
cd C:\Users\ssx\Desktop\interopdemo-multiloader
git init -b main
git add .
git commit -m "Interop Demo multi-loader mod (MC 1.21.1)"
gh repo create interopdemo-multiloader --public --source=. --push
gh run watch          # 实时看构建；结束后到 Actions 页面下载 interopdemo-jars 产物
```

之后改代码就是常规的 `git add / commit / push`，每次 push 与 PR 都会自动构建。

要点：

- workflow 里有一步 `chmod +x gradlew`，专门兜住从 Windows 推送后丢失可执行位导致的 `Permission denied`。
- 若私有仓库使用 Actions 分钟数受限，可把 `ubuntu-latest` 换成 self-hosted runner；机器要求不变：JDK 21 + 空闲内存 ≥ 8 GB。
- NeoForm 反编译缓存在 `~/.gradle/caches/neoformruntime`，已由 `gradle/actions/setup-gradle` 缓存；首次构建约 10–20 分钟，之后显著变快。

## 改成你自己的模组

1. 改 `gradle.properties`：`mod_id`、`mod_name`、`mod_author`、`group`、`version`、`description`、`license`。
2. 全局替换包名 `com.example.interopdemo` → 你的包（同时改 `META-INF/services/` 下的**文件名与内容**、`fabric.mod.json` 的 entrypoint、mixin json 的 `package`）。
3. 新内容继续写在 `common/registry/` 里，通过 `CommonClass.PLATFORM.registerXxx(...)` 注册；
   需要加载器独有能力时，先往 `IPlatformHelper` 加方法，再在两侧各实现一份。
4. 贴图/模型可直接用 `./gradlew :neoforge:runData` 生成后拷回 `common/src/main/resources`。

## 许可

代码沿用模板的 CC0 公共领域授权（`LICENSE`）；示例贴图为脚本生成的占位图，可随意替换。
