// port-lint: source build.rs
// Copyright (c) 2017 Toshiki Teramura
// SPDX-License-Identifier: MIT
package io.github.kotlinmania.intelmklsrc

/** Linkage, integer model, and thread runtime selected for an MKL build. */
enum class MklBuildConfiguration(
    val featureName: String,
    val isStatic: Boolean,
) {
    STATIC_LP64_IOMP("mkl-static-lp64-iomp", true),
    STATIC_LP64_SEQ("mkl-static-lp64-seq", true),
    STATIC_ILP64_IOMP("mkl-static-ilp64-iomp", true),
    STATIC_ILP64_SEQ("mkl-static-ilp64-seq", true),
    DYNAMIC_LP64_IOMP("mkl-dynamic-lp64-iomp", false),
    DYNAMIC_LP64_SEQ("mkl-dynamic-lp64-seq", false),
    DYNAMIC_ILP64_IOMP("mkl-dynamic-ilp64-iomp", false),
    DYNAMIC_ILP64_SEQ("mkl-dynamic-ilp64-seq", false),
    ;

    companion object {
        /** Selects one enabled MKL feature, defaulting to static ILP64 with Intel OpenMP. */
        fun fromFeatures(enabledFeatures: List<String>): MklBuildConfiguration {
            val selected = entries.filter { it.featureName in enabledFeatures }
            require(selected.size <= 1) { "Only one MKL linkage feature may be enabled." }
            return selected.singleOrNull() ?: STATIC_ILP64_IOMP
        }
    }

    /**
     * Selects the fallback package after system library discovery.
     *
     * Dynamic libraries require an explicit loader search path, so only static
     * libraries use the package fallback. A discovered system library takes priority.
     */
    fun fallbackPackage(targetOs: String, systemLibraryFound: Boolean): String? {
        if (systemLibraryFound || !isStatic) return null
        return when (targetOs) {
            "linux" -> "ghcr.io/rust-math/rust-mkl/linux/$featureName:2020.1-3038006115"
            "windows" -> "ghcr.io/rust-math/rust-mkl/windows/$featureName:2022.0-3038006115"
            else -> null
        }
    }
}
