# intel-mkl-src-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fintel-mkl-src-kotlin-blue.svg)](https://github.com/KotlinMania/intel-mkl-src-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/intel-mkl-src-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/intel-mkl-src-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/intel-mkl-src-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/intel-mkl-src-kotlin/actions)

This is a Kotlin Multiplatform port of the build-configuration policy in [`intel-mkl-src`](https://github.com/rust-math/intel-mkl-src).

**Original Project:** This port is based on [`intel-mkl-src`](https://github.com/rust-math/intel-mkl-src). The configuration choices and fallback rules follow the upstream build script.

### Porting status

The Kotlin API implements the portable configuration policy from upstream `build.rs`: selection of all eight MKL linkage features, the default static ILP64 Intel OpenMP configuration, system-library priority, and static-package fallback references for Linux and Windows. Derived source files carry a `// port-lint: source <path>` header.

This artifact provides build-configuration data. It does not contain Intel MKL binaries, discover installed libraries, download OCI packages, emit native linker flags, or expose BLAS/LAPACK bindings. Those operations belong to the consuming native build. The upstream library source has no runtime declarations; its behavior lives in the build script.

---

## Upstream README — `intel-mkl-src`

> The text below is reproduced from [`https://github.com/rust-math/intel-mkl-src`](https://github.com/rust-math/intel-mkl-src). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs where appropriate so they continue to resolve from this repository.

# intel-mkl-src

|crate         | crate.io                                                                                               | docs.rs                                                                               | master                                                                                                                                    | description                                                           |
|:-------------|:-------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------|
|intel-mkl-src | [![crate](https://img.shields.io/crates/v/intel-mkl-src.svg)](https://crates.io/crates/intel-mkl-src)  | [![docs.rs](https://docs.rs/intel-mkl-src/badge.svg)](https://docs.rs/intel-mkl-src)  | [![crate](https://img.shields.io/badge/master-intel--mkl--src-blue)](https://rust-math.github.io/intel-mkl-src/intel_mkl_src/index.html)  | Source crate for Intel-MKL                                            |
|intel-mkl-sys | [![Crate](https://img.shields.io/crates/v/intel-mkl-sys.svg)](https://crates.io/crates/intel-mkl-sys)  | [![docs.rs](https://docs.rs/intel-mkl-sys/badge.svg)](https://docs.rs/intel-mkl-sys)  | [![Crate](https://img.shields.io/badge/master-intel--mkl--sys-blue)](https://rust-math.github.io/intel-mkl-src/intel_mkl_sys/index.html)  | FFI for Intel-MKL [vector math][VM], and [statistical functions][VSL] |
|intel-mkl-tool| [![Crate](https://img.shields.io/crates/v/intel-mkl-tool.svg)](https://crates.io/crates/intel-mkl-tool)| [![docs.rs](https://docs.rs/intel-mkl-tool/badge.svg)](https://docs.rs/intel-mkl-tool)| [![Crate](https://img.shields.io/badge/master-intel--mkl--tool-blue)](https://rust-math.github.io/intel-mkl-src/intel_mkl_tool/index.html)| Seek Intel-MKL libraries from filesystem                              |

[VM]:  https://software.intel.com/en-us/mkl-developer-reference-c-vector-mathematical-functions
[VSL]: https://software.intel.com/en-us/mkl-developer-reference-c-statistical-functions

## Usage

`intel-mkl-src` crate is a `*-src` crate. This links MKL libraries to executable build by cargo, but does not provide Rust bindings.
Please use `blas-sys`, `lapack-sys`, or `fftw-sys` to use BLAS, LAPACK, FFTW interface of MKL, e.g.

```toml
[dependencies]
fftw-sys = { version = "0.4", features = ["intel-mkl"] }
```

Binding to MKL specific features are provided by `intel-mkl-sys` crate. This contains 

- [Vector Mathematical Functions](https://www.intel.com/content/www/us/en/develop/documentation/onemkl-developer-reference-c/top/vector-mathematical-functions.html)
- [Statistical Functions](https://www.intel.com/content/www/us/en/develop/documentation/onemkl-developer-reference-c/top/statistical-functions.html)

## How to find system MKL libraries

`intel-mkl-tool` crate seeks system MKL library installed by various installer as following manner:

- Seek using `pkg-config` command
- Seek `${MKLROOT}` directory
- Seek default installation path
  - `/opt/intel/mkl` for Linux
  - `C:/Program Files (x86)/IntelSWTools/` and `C:/Program Files (x86)/Intel/oneAPI` for Windows

If `intel-mkl-tool` does not find MKL library, `intel-mkl-src` try to download MKL binaries from [GitHub Container Registry (ghcr.io)](https://github.com/orgs/rust-math/packages?repo_name=rust-mkl-container).

## Supported features

There are 8 (=2x2x2) `mkl-*-*-*` features to specify how to link MKL libraries.
If any feature is set, default to `mkl-static-ilp64-iomp`.

### Link type (`static` or `dynamic`)
`dynamic` means MKL is linked dynamically, i.e. the executable does not contains MKL libraries
and will seek them from filesystem while execution.
This is better choice when the MKL libraries are managed by the system package manager e.g. `apt`.

`static` means MKL is linked statically, i.e. the MKL binaries are embedded in the executable file.
This is better choice when creating portable executable.

### Data model (`lp64` or `ilp64`)

This specify the data model:

- `ilp64` means `int` (i), `long` (l), and pointers (p) are 64-bit.
- `lp64` means `long` (l) and pointers (p) are 64-bit, `int` is 32-bit.

### Thread management (`iomp` or `seq`)

- `iomp` means MKL uses Intel OpenMP runtime
- `seq` means sequential (single thread) execution

Using GNU OpenMP runtime (`libgomp`) is not supported yet. Please see https://github.com/rust-math/intel-mkl-src/issues/97

## License
MKL is distributed under the Intel Simplified Software License for Intel(R) Math Kernel Library, See [License.txt](License.txt).
Some wrapper codes are licensed by MIT License (see the header of each file).

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:intel-mkl-src-kotlin:0.1.0")
}
```

### Building

```bash
./gradlew build
./gradlew test
./gradlew setupAndroidSdk centralPortalBundle -PRELEASE_SIGNING_ENABLED=false
```

The final command stages every publication and creates the complete unsigned deployment bundle without uploading it. CI runs this same publication graph.

### Configuration policy

```kotlin
import io.github.kotlinmania.intelmklsrc.MklBuildConfiguration

val configuration = MklBuildConfiguration.fromFeatures(listOf("mkl-static-lp64-seq"))
val fallback = configuration.fallbackPackage("linux", systemLibraryFound = false)
// ghcr.io/rust-math/rust-mkl/linux/mkl-static-lp64-seq:2020.1-3038006115
```

An empty feature list selects `mkl-static-ilp64-iomp`. Conflicting MKL features are rejected. A discovered system library, dynamic linkage, or any target OS other than `linux` or `windows` yields no fallback package.

### Targets

- JVM and Android (API 24+)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Wasm-WASI (Node.js)
- Linux x64 / ARM64 and Windows x64
- Android Native ARM64 / x64
- macOS ARM64, iOS ARM64 / simulator ARM64, tvOS ARM64 / simulator ARM64, watchOS device ARM64 / simulator ARM64

These targets run the portable configuration API. They do not imply that native Intel MKL binaries are available for each target. Apple targets also expose the configuration API through Swift Export and XCFrameworks.

### Porting guidelines

See [AGENTS.md](AGENTS.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

The configuration policy translated from the upstream build script retains its [MIT license](LICENSE-MIT) and Toshiki Teramura attribution. Intel MKL binaries are distributed separately under Intel's terms.

The existing repository [LICENSE](LICENSE) contains the Apache 2.0 terms; [LICENSE-MIT](LICENSE-MIT) contains the terms for the translated configuration code.

Original work copyrighted by the intel-mkl-src authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`intel-mkl-src`](https://github.com/rust-math/intel-mkl-src) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
