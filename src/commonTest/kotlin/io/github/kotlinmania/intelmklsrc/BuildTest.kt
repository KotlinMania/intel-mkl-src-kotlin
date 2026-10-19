package io.github.kotlinmania.intelmklsrc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BuildTest {
    @Test
    fun defaultConfigurationUsesStaticIlp64IntelOpenMp() {
        val configuration = MklBuildConfiguration.fromFeatures(emptyList())
        assertEquals("mkl-static-ilp64-iomp", configuration.featureName)
        assertEquals(
            "ghcr.io/rust-math/rust-mkl/linux/mkl-static-ilp64-iomp:2020.1-3038006115",
            configuration.fallbackPackage("linux", systemLibraryFound = false),
        )
    }

    @Test
    fun selectsEachSupportedFeature() {
        for (linkage in listOf("static", "dynamic")) {
            for (model in listOf("lp64", "ilp64")) {
                for (threading in listOf("iomp", "seq")) {
                    val feature = "mkl-$linkage-$model-$threading"
                    val configuration = MklBuildConfiguration.fromFeatures(listOf(feature))
                    assertEquals(feature, configuration.featureName)
                    assertEquals(linkage == "static", configuration.isStatic)
                }
            }
        }
    }

    @Test
    fun rejectsConflictingFeatures() {
        assertFailsWith<IllegalArgumentException> {
            MklBuildConfiguration.fromFeatures(listOf("mkl-static-lp64-seq", "mkl-dynamic-ilp64-iomp"))
        }
    }

    @Test
    fun unrelatedFeaturesDoNotChangeSelection() {
        assertEquals(
            MklBuildConfiguration.STATIC_ILP64_IOMP,
            MklBuildConfiguration.fromFeatures(listOf("default", "unrelated")),
        )
        assertEquals(
            MklBuildConfiguration.DYNAMIC_LP64_SEQ,
            MklBuildConfiguration.fromFeatures(listOf("default", "mkl-dynamic-lp64-seq", "mkl-dynamic-lp64-seq")),
        )
    }

    @Test
    fun systemLibrariesTakePriorityOverEveryFallback() {
        for (configuration in MklBuildConfiguration.entries) {
            for (os in listOf("linux", "windows")) {
                assertNull(configuration.fallbackPackage(os, systemLibraryFound = true))
            }
        }
    }

    @Test
    fun staticFallbackPreservesPlatformVersionsAndFeatureNames() {
        for (model in listOf("lp64", "ilp64")) {
            for (threading in listOf("iomp", "seq")) {
                val feature = "mkl-static-$model-$threading"
                val configuration = MklBuildConfiguration.fromFeatures(listOf(feature))
                assertEquals(
                    "ghcr.io/rust-math/rust-mkl/linux/$feature:2020.1-3038006115",
                    configuration.fallbackPackage("linux", systemLibraryFound = false),
                )
                assertEquals(
                    "ghcr.io/rust-math/rust-mkl/windows/$feature:2022.0-3038006115",
                    configuration.fallbackPackage("windows", systemLibraryFound = false),
                )
            }
        }
    }

    @Test
    fun dynamicLibrariesNeverUsePackageFallback() {
        for (model in listOf("lp64", "ilp64")) {
            for (threading in listOf("iomp", "seq")) {
                val configuration = MklBuildConfiguration.fromFeatures(listOf("mkl-dynamic-$model-$threading"))
                for (os in listOf("linux", "windows", "macos", "android")) {
                    assertNull(configuration.fallbackPackage(os, systemLibraryFound = false))
                }
            }
        }
    }

    @Test
    fun otherOperatingSystemsHaveNoPackageFallback() {
        for (configuration in MklBuildConfiguration.entries) {
            for (os in listOf("macos", "ios", "tvos", "watchos", "android", "freebsd", "")) {
                assertNull(configuration.fallbackPackage(os, systemLibraryFound = false))
            }
        }
    }
}
